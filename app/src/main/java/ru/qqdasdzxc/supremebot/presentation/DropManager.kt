package ru.qqdasdzxc.supremebot.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.WorkingMode
import ru.qqdasdzxc.supremebot.data.dto.UserProfile
import ru.qqdasdzxc.supremebot.utils.Constants.BASE_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.HREF_ATTR
import ru.qqdasdzxc.supremebot.utils.zipLiveData

class DropManager {

    companion object {
        var userProfile: UserProfile? = null
        private var itemFound = false
        var messagesLiveData = MutableLiveData<Int>()
        var workingModeLiveData = MutableLiveData<WorkingMode>()
        var foundedClothHrefLiveData = MutableLiveData<String>()
        var startWorkTimeInMillis: Long? = null
        private var pickFirstAvailableSize = MutableLiveData<Boolean>()
        private var sizeValueLiveData = MutableLiveData<String>()
        private var isClothLoadedOnUILiveData = MutableLiveData<Boolean>()
        private var combinedFirstSizeLiveData = MutableLiveData<Pair<Boolean, Boolean>>()
        private var combinedSizeValueLiveData = MutableLiveData<Pair<String, Boolean>>()

        fun getNeededSizeValueLiveData(): LiveData<String?> = Transformations.map(combinedSizeValueLiveData) {
            if (it.second) {
                Log.d("Hello", "DropManager: item page loaded on UI, size was found")
                return@map it.first
            } else {
                Log.d("Hello", "DropManager: found size but item page is not loaded on UI yet")
                return@map null
            }
        }

        fun getFirstSizeLiveData(): LiveData<Boolean?> = Transformations.map(combinedFirstSizeLiveData) {
            if (it.second) {
                Log.d("Hello", "DropManager: item page loaded on UI, pick first size")
                return@map it.first
            } else {
                Log.d("Hello", "DropManager: get first size but item page is not loaded on UI yet")
                return@map null
            }
        }

        fun setItemPageLoaded() {
            Log.d("Hello", "DropManager: item page loaded on UI")
            isClothLoadedOnUILiveData.postValue(true)
        }

        fun refresh() {
            itemFound = false
            messagesLiveData = MutableLiveData()
            workingModeLiveData = MutableLiveData()
            foundedClothHrefLiveData = MutableLiveData()
            pickFirstAvailableSize = MutableLiveData()
            sizeValueLiveData = MutableLiveData()
            isClothLoadedOnUILiveData = MutableLiveData()
            isClothLoadedOnUILiveData.postValue(false)
            combinedSizeValueLiveData = MutableLiveData()
            combinedSizeValueLiveData = zipLiveData(sizeValueLiveData, isClothLoadedOnUILiveData)
            combinedFirstSizeLiveData = zipLiveData(pickFirstAvailableSize, isClothLoadedOnUILiveData)
        }

        init {
            isClothLoadedOnUILiveData.postValue(false)
            combinedSizeValueLiveData = zipLiveData(sizeValueLiveData, isClothLoadedOnUILiveData)
            combinedFirstSizeLiveData = zipLiveData(pickFirstAvailableSize, isClothLoadedOnUILiveData)
        }
    }

    fun startSearchingItem() {
        startWorkTimeInMillis = System.currentTimeMillis()

        if (userProfile == null) {
            messagesLiveData.postValue(R.string.user_profile_empty_msg)
            return
        }

        userProfile?.let {
            Log.d("Hello", "DropManager searching")
            messagesLiveData.postValue(R.string.drop_mode_start_working_msg)
            workingModeLiveData.postValue(WorkingMode.DROP)

            CoroutineScope(Dispatchers.IO).launch {
                val pageDocument =
                    Jsoup.connect("https://www.supremenewyork.com/shop/all/${it.itemTypeValue}").get()
                val scroller = pageDocument.child(0).child(1).child(2).child(1)
                val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
                    it.validateItemName(child.toString())
                }
                firstNotSoldChildren?.let {
                    synchronized(itemFound) {
                        if (!itemFound) {
                            Log.d("Hello", "DropManager: item was found")
                            itemFound = true
                            val clothFullHref = BASE_SUPREME_URL + it.child(0).child(0).attr(HREF_ATTR)
                            foundedClothHrefLiveData.postValue(clothFullHref)

                            messagesLiveData.postValue(R.string.item_was_found_msg)
                            startLoadingItemPage(clothFullHref)
                        }
                    }
                }
            }
        }
    }

    private fun startLoadingItemPage(clothFullHref: String) {
        Log.d("Hello", "DropManager: loading item page")
        CoroutineScope(Dispatchers.IO).launch {
            if (userProfile!!.isOneSize) {
                Log.d("Hello", "DropManager: hit one size")
                pickFirstAvailableSize.postValue(true)
                return@launch
            }

            val neededSizes =
                if (userProfile?.itemTypeValue == "Shoes") userProfile?.itemSneakersNeededSizes else userProfile?.itemClothNeededSizes
            val pageDocument = Jsoup.connect(clothFullHref).get()
            pageDocument.getElementById("size")?.let {
                val availableSizes = it.children()

                if (neededSizes.isNullOrEmpty()) {
                    Log.d("Hello", "DropManager: hit one size")
                    pickFirstAvailableSize.postValue(true)
                    return@launch
                }

                for (neededSize in neededSizes) {
                    val filteredSize = availableSizes.firstOrNull { element ->
                        element.toString().contains(neededSize)
                    }
                    filteredSize?.let { element ->
                        Log.d("Hello", "DropManager: hit $neededSize")
                        sizeValueLiveData.postValue(element.attr("value"))
                        return@launch
                    }
                }
            }

            Log.d("Hello", "DropManager: needed sizes are sold out")
            messagesLiveData.postValue(R.string.needed_sizes_sold_out_msg)
        }
    }

}
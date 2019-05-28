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
        private var itemFounded = false
        var messagesLiveData = MutableLiveData<Int>()
        var workingModeLiveData = MutableLiveData<WorkingMode>()
        var foundedClothHrefLiveData = MutableLiveData<String>()
        var pickFirstAvailableSize = MutableLiveData<Boolean>()
        private var sizeValueLiveData = MutableLiveData<String>()
        private var isClothLoadedOnUILiveData = MutableLiveData<Boolean>()
        private var combinedSizeLiveData = MutableLiveData<Pair<String, Boolean>>()

        fun getSizeValueLiveData(): LiveData<String?> = Transformations.map(combinedSizeLiveData) {
            if (it.second) {
                return@map it.first
            } else {
                return@map null
            }
        }

        fun setItemPageLoaded() {
            isClothLoadedOnUILiveData.postValue(true)
        }

        fun refresh() {
            itemFounded = false
            messagesLiveData = MutableLiveData()
            workingModeLiveData = MutableLiveData()
            foundedClothHrefLiveData = MutableLiveData()
            pickFirstAvailableSize = MutableLiveData()
            sizeValueLiveData = MutableLiveData()
            isClothLoadedOnUILiveData = MutableLiveData()
            isClothLoadedOnUILiveData.postValue(false)
            combinedSizeLiveData = MutableLiveData()
            combinedSizeLiveData = zipLiveData(sizeValueLiveData, isClothLoadedOnUILiveData)
        }

        init {
            isClothLoadedOnUILiveData.postValue(false)
            combinedSizeLiveData = zipLiveData(sizeValueLiveData, isClothLoadedOnUILiveData)
        }
    }

    fun startSearchingItem() {
        if (userProfile == null) {
            messagesLiveData.postValue(R.string.user_profile_empty_msg)
            return
        }

        userProfile?.let {
            Log.d("drop scan", "started")
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
                    if (!itemFounded) {
                        itemFounded = true
                        val clothFullHref = BASE_SUPREME_URL + it.child(0).child(0).attr(HREF_ATTR)
                        foundedClothHrefLiveData.postValue(clothFullHref)

                        messagesLiveData.postValue(R.string.item_was_found_msg)
                        startLoadingItemPage(clothFullHref)
                    }
                }
            }
        }
    }


    private fun startLoadingItemPage(clothFullHref: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val neededSizes =
                if (userProfile?.itemTypeValue == "Accessories") userProfile?.itemSneakersNeededSizes else userProfile?.itemClothNeededSizes
            val pageDocument = Jsoup.connect(clothFullHref).get()
            val availableSizes = pageDocument.getElementById("size").children()

            if (neededSizes.isNullOrEmpty()) {
                pickFirstAvailableSize.postValue(true)
                return@launch
            }

            for (neededSize in neededSizes) {
                val filteredSize = availableSizes.firstOrNull {
                    it.toString().contains(neededSize)
                }
                filteredSize?.let {
                    sizeValueLiveData.postValue(it.attr("value"))
                    return@launch
                }
            }

            messagesLiveData.postValue(R.string.needed_sizes_sold_out_msg)
        }
    }

}
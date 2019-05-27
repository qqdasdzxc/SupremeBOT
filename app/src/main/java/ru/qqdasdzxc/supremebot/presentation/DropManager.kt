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
import ru.qqdasdzxc.supremebot.utils.Constants.SOLD_OUT
import ru.qqdasdzxc.supremebot.utils.zipLiveData

class DropManager {

    companion object {
        lateinit var userProfile: UserProfile
        private var itemFounded = false
        val messagesLiveData = MutableLiveData<Int>()
        val workingModeLiveData = MutableLiveData<WorkingMode>()
        //todo нашлась ссылка на айтем - сразу остановили работу в цикле на ui
        val foundedClothHrefLiveData = MutableLiveData<String>()
        val sizeValueLiveData = MutableLiveData<String>()
        val isClothLoadedOnUILiveData = MutableLiveData<Boolean>()
        private var combinedSizeLiveData = MutableLiveData<Pair<String, Boolean>>()

        fun getSizeValueLiveData(): LiveData<String?> = Transformations.map(combinedSizeLiveData) {
            if (it.second) {
                return@map it.first
            } else {
                return@map null
            }
        }

        init {
            isClothLoadedOnUILiveData.postValue(false)
            combinedSizeLiveData = zipLiveData(sizeValueLiveData, isClothLoadedOnUILiveData)
        }
    }

    fun startSearchingItem() {
        Log.d("drop scan", "started")
        messagesLiveData.postValue(R.string.drop_mode_start_working_msg)
        workingModeLiveData.postValue(WorkingMode.DROP)

        CoroutineScope(Dispatchers.IO).launch {
            val pageDocument =
                Jsoup.connect("https://www.supremenewyork.com/shop/all/${userProfile.itemTypeValue}").get()
            val scroller = pageDocument.child(0).child(1).child(2).child(1)
            val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
                val childString = child.toString()
                //todo здесь же нужно проверить и на название цвета
                userProfile.validateItemName(childString)
                        && !childString.contains(SOLD_OUT)
            }

            firstNotSoldChildren?.let {
                if (!itemFounded) {
                    itemFounded = true
                    val clothFullHref = BASE_SUPREME_URL + it.child(0).child(0).attr(HREF_ATTR)
                    foundedClothHrefLiveData.postValue(clothFullHref)

                    //todo show message item is finded + item title
                    startLoadingItemPage(clothFullHref)
                }

                return@launch
            }

            workingModeLiveData.postValue(WorkingMode.WAITING)
            messagesLiveData.postValue(R.string.did_not_find_an_item)
        }
    }


    private fun startLoadingItemPage(clothFullHref: String) {
        CoroutineScope(Dispatchers.IO).launch {
            //по сути порядок в списке и есть приоритет
            val neededSizes =
                if (userProfile.itemTypeValue == "Accessories") userProfile.itemSneakersNeededSizes else userProfile.itemClothNeededSizes
            val pageDocument = Jsoup.connect(clothFullHref).get()
            val availableSizes = pageDocument.getElementById("size").children()
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


    fun setItemPageLoaded() {
        isClothLoadedOnUILiveData.postValue(true)
    }

}
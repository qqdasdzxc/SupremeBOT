package ru.qqdasdzxc.supremebot.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.WorkingMode
import ru.qqdasdzxc.supremebot.utils.Constants.BASE_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.HREF_ATTR
import ru.qqdasdzxc.supremebot.utils.Constants.SOLD_OUT
import ru.qqdasdzxc.supremebot.utils.zipLiveData

class TestManager {

    private val messagesLiveData = MutableLiveData<Int>()
    private val workingModeLiveData = MutableLiveData<WorkingMode>()
    private val findedClothHrefLiveData = MutableLiveData<String>()
    private val sizeValueLiveData = MutableLiveData<String>()
    //private val isClothLoadedOnUILiveData = MutableLiveData<Boolean>()

    //private var combinedSizeLiveData = MutableLiveData<Pair<String, Boolean>>()

//    init {
//        isClothLoadedOnUILiveData.postValue(false)
//        combinedSizeLiveData = zipLiveData(sizeValueLiveData, isClothLoadedOnUILiveData)
//    }

    fun startSearchingItem() {
        messagesLiveData.postValue(R.string.test_mode_start_working_msg)
        workingModeLiveData.postValue(WorkingMode.TEST)

        CoroutineScope(Dispatchers.IO).launch {
            //todo set all
            val pageDocument = Jsoup.connect("https://www.supremenewyork.com/shop/all/pants").get()
            val scroller = pageDocument.child(0).child(1).child(2).child(1)
            val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
                !child.toString().contains(SOLD_OUT)
                //здесь же можно проверить и на название цвета
            }
            firstNotSoldChildren?.let {
                val clothFullHref = BASE_SUPREME_URL + it.child(0).child(0).attr(HREF_ATTR)
                //startLoadingItemPage(clothFullHref)
                findedClothHrefLiveData.postValue(clothFullHref)
                return@launch
            }

            workingModeLiveData.postValue(WorkingMode.WAITING)
            messagesLiveData.postValue(R.string.everything_is_sold_out)
        }
    }

    //no need to test mode function, in test pick first available size
//    private fun startLoadingItemPage(clothFullHref: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            //по сути порядок в списке и есть приоритет
//            val neededSizes = listOf("Medium", "Small")
//            val pageDocument = Jsoup.connect(clothFullHref).get()
//            val availableSizes = pageDocument.getElementById("size").children()
//            for (neededSize in neededSizes) {
//                val filteredSize = availableSizes.firstOrNull {
//                    it.toString().contains(neededSize)
//                }
//                filteredSize?.let {
//                    sizeValueLiveData.postValue(it.attr("value"))
//                    return@launch
//                }
//            }
//
//            messagesLiveData.postValue(R.string.needed_sizes_sold_out_msg)
//        }
//    }

    fun getMessagesLiveData(): LiveData<Int> = messagesLiveData

    fun getWorkingModelLiveData(): LiveData<WorkingMode> = workingModeLiveData

    fun getClothHrefLiveData(): LiveData<String> = findedClothHrefLiveData

//    fun getSizeValueLiveData(): LiveData<String?> = Transformations.map(combinedSizeLiveData) {
//        if (it.second) {
//            return@map it.first
//        } else {
//            return@map null
//        }
//    }

//    fun setItemPageLoaded() {
//        isClothLoadedOnUILiveData.postValue(true)
//    }
}
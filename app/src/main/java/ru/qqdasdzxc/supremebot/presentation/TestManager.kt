package ru.qqdasdzxc.supremebot.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.WorkingMode
import ru.qqdasdzxc.supremebot.utils.Constants.BASE_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.HREF_ATTR
import ru.qqdasdzxc.supremebot.utils.Constants.SOLD_OUT

class TestManager {

    private val messagesLiveData = MutableLiveData<Int>()
    private val workingModeLiveData = MutableLiveData<WorkingMode>()
    private val foundedClothHrefLiveData = MutableLiveData<String>()

    fun startSearchingItem() {
        messagesLiveData.postValue(R.string.test_mode_start_working_msg)
        workingModeLiveData.postValue(WorkingMode.TEST)

        CoroutineScope(Dispatchers.IO).launch {
            val pageDocument = Jsoup.connect("https://www.supremenewyork.com/shop/all/pants").get()
            val scroller = pageDocument.child(0).child(1).child(2).child(1)
            val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
                !child.toString().contains(SOLD_OUT)
            }
            firstNotSoldChildren?.let {
                val clothFullHref = BASE_SUPREME_URL + it.child(0).child(0).attr(HREF_ATTR)
                foundedClothHrefLiveData.postValue(clothFullHref)
                return@launch
            }

            workingModeLiveData.postValue(WorkingMode.WAITING)
            messagesLiveData.postValue(R.string.everything_is_sold_out)
        }
    }

    fun getMessagesLiveData(): LiveData<Int> = messagesLiveData

    fun getWorkingModelLiveData(): LiveData<WorkingMode> = workingModeLiveData

    fun getClothHrefLiveData(): LiveData<String> = foundedClothHrefLiveData
}
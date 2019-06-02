package ru.qqdasdzxc.supremebot.presentation

import android.webkit.JavascriptInterface
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class CheckoutManager {

    companion object {
        val cartVisible = MutableLiveData<Boolean>()
        var cartLoaded = false
    }

    @JavascriptInterface
    fun processHTML(html: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val doc = Jsoup.parse(html)
            if (doc.getElementById("cart").attr("class").isEmpty()) {
                synchronized(cartLoaded) {
                    if (!cartLoaded) {
                        cartLoaded = true
                        cartVisible.postValue(true)
                    }
                }
            } else {
                cartVisible.postValue(false)
            }
        }
    }
}
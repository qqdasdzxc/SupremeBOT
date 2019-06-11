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
    fun processCheckoutButton(html: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val doc = Jsoup.parse(html)
            val cartElement = doc.getElementById("cart")
            cartElement?.let {
                if (it.attr("class").isEmpty()) {
                    cartVisible.postValue(true)
                } else {
                    cartVisible.postValue(false)
                }
            }
        }
    }

    @JavascriptInterface
    fun showHtml(html: String) {
        val doc = Jsoup.parse(html)
    }
}
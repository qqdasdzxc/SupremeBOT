package ru.qqdasdzxc.supremebot.presentation

import android.util.Log
import android.webkit.JavascriptInterface
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class CheckoutManager {

    companion object {
        var cartVisible = MutableLiveData<Boolean>()
        fun refresh() {
            cartVisible = MutableLiveData()
        }
    }

    @JavascriptInterface
    fun processCheckoutButton(html: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val doc = Jsoup.parse(html)
            val cartElement = doc.getElementById("container")
            Log.d("Hello", "CheckoutManager: parse cartElement")
            cartElement?.let {
                if (it.attr("class").isEmpty()) {
                    Log.d("Hello", "CheckoutManager: post cartElement class is not empty = invisible")
                    cartVisible.postValue(false)
                } else {
                    Log.d("Hello", "CheckoutManager: post cartElement class is empty = visible")
                    cartVisible.postValue(true)
                }
                return@launch
            }
            Log.d("Hello", "CheckoutManager: post cartElement class is null")
            cartVisible.postValue(false)
        }
    }

    @JavascriptInterface
    fun showHtml(html: String) {
        val doc = Jsoup.parse(html)
    }
}
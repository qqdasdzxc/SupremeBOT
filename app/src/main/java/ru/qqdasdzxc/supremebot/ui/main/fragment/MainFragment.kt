package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.OrderState
import ru.qqdasdzxc.supremebot.databinding.FragmentMainViewBinding
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment


class MainFragment : BaseFragment<FragmentMainViewBinding>() {

    var clothHref: String? = null
    var currentState = OrderState.SINGLE_ITEM_STATE

    override fun getLayoutResId(): Int = R.layout.fragment_main_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.mainWebView.settings.javaScriptEnabled = true
        //binding.mainWebView.settings.domStorageEnabled = true
        binding.mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        val javasriptInterface = MyJavaScriptInterface()
        binding.mainWebView.addJavascriptInterface(javasriptInterface, "HTMLOUT")

        binding.mainWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                return false
            }

            @SuppressLint("RestrictedApi")
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                url?.let { urlPage ->
                    if (clothHref != null && urlPage.endsWith(clothHref!!)) {
                        when (currentState) {
                            OrderState.SINGLE_ITEM_STATE -> {
                                val js =
                                    "javascript:(function(){document.getElementsByClassName('button')[2].click();})()"

                                binding.mainWebView.evaluateJavascript(js) {
                                    currentState = OrderState.ITEM_IN_BASKET_STATE

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val doc = Jsoup.connect(urlPage).get()
                                        doc.body()
                                    }

                                    val js1 =
                                        "javascript:(function(){document.getElementsByClassName('button')[1].click();})()"

                                    binding.mainWebView.evaluateJavascript(js1) {
                                        currentState = OrderState.CHECKOUT_STATE
                                    }

                                }
                            }
                            OrderState.ITEM_IN_BASKET_STATE -> {

                            }
                            OrderState.CHECKOUT_STATE -> {

                            }
                            else -> {

                            }
                        }

                        return
                    }

                    if (urlPage.endsWith("checkout")) {


//                        binding.mainWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")

                        //visa american_express master solo paypal

                        val js = "javascript:" +
                                "document.getElementById('order_billing_name').value = 'asd';" +
                                "document.getElementById('credit_card_type').value = 'master';" +
                                "document.getElementById('order_tel').value = '+79374102309';" +
                                "document.getElementById('order_terms').checked = 'true';"


                        binding.mainWebView.evaluateJavascript(js) {

                        }

                    }
                }


                //todo save state depends on finished url
            }
        }
        binding.mainWebView.webChromeClient = object : WebChromeClient() {

        }

        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all/jackets")


        CoroutineScope(Dispatchers.IO).launch {
            val doc = Jsoup.connect("https://www.supremenewyork.com/shop/all/jackets").get()
            doc.body()

            //todo подумать что лучше - цепать сразу все элементы или пройтись по чайлдам вручную
            //doc.child(0).child(1).child(2).child(1)
            //doc.getElementsByAttributeValueContaining("class", "turbolink_scroller")
            //doc.getElementsContainingOwnText("Apple Coaches")
            val scroller = doc.allElements.firstOrNull { it.attributes().get("class") == "turbolink_scroller" }
            val filteredChildren = scroller?.children()?.filter { element ->
                val elementString = element.toString()
                elementString.contains("Apple") && !elementString.contains("sold out")
            }
            filteredChildren?.let {
                //беру первую шмотку рандомного цвета
                clothHref = filteredChildren[0].child(0).child(0).attr("href")
                CoroutineScope(Dispatchers.Main).launch {
                    binding.mainWebView.loadUrl("https://www.supremenewyork.com$clothHref")
                }

                //берем элементы страницы конкретной шмотки
                val doc1 = Jsoup.connect("https://www.supremenewyork.com$clothHref").get()
                doc1.body()
                val addHref = doc1.getElementById("cctrl").child(0).attr("action")
                //Large - 56390
            }

            //сделать несколько модов
            //1)взять конкретный цвет - и выключиться если цвет солдаут
            //2)взять конкретный цвет - и взять первый(или рандом) цвет такой же шмотки если не солдаут
            //3)взять рандомный цвет
        }

    }
}

internal class MyJavaScriptInterface {

    @JavascriptInterface
    fun processHTML(html: String) {
        // process the html as needed by the app
        Log.d("asd", "asd")
        val doc = Jsoup.parse(html)

        //Jsoup.parse(html).getElementById("order_email") находит
        //Jsoup.parse(html).getElementsByClass("input")
    }
}
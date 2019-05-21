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
        loadStartPage()
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
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)

                if (clothHref != null && url.endsWith(clothHref!!)) {
                    when (currentState) {
                        OrderState.SINGLE_ITEM_STATE -> {
                            val js = "javascript:(function(){document.getElementsByClassName('button')[2].click();})()"

                            binding.mainWebView.evaluateJavascript(js) {
                                currentState = OrderState.ITEM_IN_BASKET_STATE

                                CoroutineScope(Dispatchers.IO).launch {
                                    val doc = Jsoup.connect(url).get()
                                    doc.body()
                                }

                                val js1 = "javascript:(function(){document.getElementsByClassName('button')[1].click();})()"

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

                if (url.endsWith("checkout")) {



                    val js = "javascript:(function(){" +
                            "document.getElementById('order_billing_name').value = 'asd';" +
                            "document.getElementById('credit_card_type').value = 'master';" +
                            "document.getElementById('order_tel').value = '+79374102309';" +
                            "document.getElementById('order_terms').checked = 'true';" +
                            "document.getElementsByClassName('button')[0].click();})()"


                    binding.mainWebView.evaluateJavascript(js) {
                        //binding.mainWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
                    }

                }


                //todo save state depends on finished url
            }
        }
    }

    private fun loadStartPage() {
        //TODO show loading somehow
        CoroutineScope(Dispatchers.IO).launch {
            //TODO load start page(1)
            val doc = Jsoup.connect("https://www.supremenewyork.com/shop/all/jackets").get()

            //todo подумать что лучше - цепать сразу все элементы или пройтись по чайлдам вручную
            //doc.child(0).child(1).child(2).child(1)
            //doc.getElementsByAttributeValueContaining("class", "turbolink_scroller")
            //doc.getElementsContainingOwnText("Apple Coaches")
            //doc.allElements.firstOrNull { it.attributes().get("class") == "turbolink_scroller" }
            val scroller = doc.child(0).child(1).child(2).child(1)
            val filteredChildren = scroller?.children()?.filter { element ->
                val elementString = element.toString()
                //todo check name(2) and color(3)
                elementString.contains("Apple") && !elementString.contains("sold out")
            }

            filteredChildren?.let {
                //беру первую шмотку рандомного цвета
                clothHref = filteredChildren[0].child(0).child(0).attr("href")
                CoroutineScope(Dispatchers.Main).launch {
                    binding.mainWebView.loadUrl("https://www.supremenewyork.com$clothHref")
                }

                //берем элементы страницы конкретной шмотки
//                val doc1 = Jsoup.connect("https://www.supremenewyork.com$clothHref").get()
//                doc1.body()
//                val addHref = doc1.getElementById("cctrl").child(0).attr("action")
                //Large - 56390
            }


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
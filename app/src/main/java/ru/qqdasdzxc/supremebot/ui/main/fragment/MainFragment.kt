package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.qqdasdzxc.supremebot.databinding.FragmentMainViewBinding
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import android.webkit.JavascriptInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R


class MainFragment : BaseFragment<FragmentMainViewBinding>() {

    override fun getLayoutResId(): Int = R.layout.fragment_main_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.mainWebView.settings.javaScriptEnabled = true

        binding.mainWebView.addJavascriptInterface(MyJavaScriptInterface(), "HTMLOUT")

        binding.mainWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                binding.mainWebView.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
//                val javasriptInterface = JavascriptInterface(activity)
//                view?.addJavascriptInterface(javasriptInterface, "MyInterface")

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
                val href = filteredChildren[0].child(0).child(0).attr("href")
                CoroutineScope(Dispatchers.Main).launch {
                    binding.mainWebView.loadUrl("https://www.supremenewyork.com$href")
                }

                //берем элементы страницы конкретной шмотки
                val doc1 = Jsoup.connect("https://www.supremenewyork.com$href").get()
                doc1.body()
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

        //Html.fromHtml(html)
    }
}
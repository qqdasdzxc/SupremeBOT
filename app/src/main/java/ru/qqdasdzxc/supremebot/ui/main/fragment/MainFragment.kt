package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.databinding.FragmentMainViewBinding
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import android.webkit.ValueCallback
import android.webkit.JavascriptInterface
import androidx.core.text.HtmlCompat
import java.io.IOException


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

        binding.mainWebView.loadUrl("https://www.supremenewyork.com/mobile/")
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
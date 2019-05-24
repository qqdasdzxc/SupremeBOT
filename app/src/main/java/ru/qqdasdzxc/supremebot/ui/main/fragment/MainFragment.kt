package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Observer
import kotlinx.coroutines.Runnable
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.OrderState
import ru.qqdasdzxc.supremebot.data.WorkingMode
import ru.qqdasdzxc.supremebot.databinding.FragmentMainViewBinding
import ru.qqdasdzxc.supremebot.presentation.TestManager
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.ui.base.HandleBackPressFragment
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_ADD_ITEM_TO_BASKET
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_PROCESS_TEST_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_AND_CLICK_ON_PROCESS_DROP_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE_WITHOUT_CLICK
import ru.qqdasdzxc.supremebot.utils.Constants.MOBILE
import ru.qqdasdzxc.supremebot.utils.hide
import ru.qqdasdzxc.supremebot.utils.show

class MainFragment : BaseFragment<FragmentMainViewBinding>(), HandleBackPressFragment, Runnable {

    private var currentClothHref: String? = null
    private var currentOrderState = OrderState.SINGLE_ITEM_STATE
    private var workingMode = WorkingMode.WAITING
    private var dropHandler = Handler()
    private var testManager: TestManager? = null

    override fun getLayoutResId(): Int = R.layout.fragment_main_view

    override fun onBackPress() {
        if (binding.mainWebView.canGoBack()) {
            binding.mainWebView.goBack()
        } else {
            setWaitingUIState()
        }
    }

    private fun setWaitingUIState() {
        binding.startButton.show()
        binding.testButton.show()
        binding.mainWebView.hide()
        binding.stopButton.hide()
    }

    private fun setWorkingUIState() {
        binding.startButton.hide()
        binding.testButton.hide()
        binding.stopButton.show()
        binding.mainWebView.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWebView()
        initView()
    }

    private fun initView() {
        binding.testButton.setOnClickListener {
            startTestCheckout()
        }
        binding.startButton.setOnClickListener {
            startDropCheckout()
        }
        binding.stopButton.setOnClickListener {
            workingMode = WorkingMode.WAITING
            dropHandler.removeCallbacks(this)
            setWaitingUIState()
        }
    }

    private fun initWebView() {
        binding.mainWebView.settings.javaScriptEnabled = true
        binding.mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        //binding.mainWebView.settings.domStorageEnabled = true
        val javascriptInterface = MyJavaScriptInterface()
        binding.mainWebView.addJavascriptInterface(javascriptInterface, "HTMLOUT")
        binding.mainWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean = false

            @SuppressLint("RestrictedApi")
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                processUrl(url)
            }
        }
    }

    private fun processUrl(pageUrl: String) {
        if (currentClothHref != null && pageUrl.endsWith(currentClothHref!!)) {
            //send test manager currentClothHref page loaded - no need in test mode
            //testManager!!.setItemPageLoaded()
            //todo for test mode evaluate click on add, then click on basket
            getItemAndGoToCheckout()
            return
        }
        if (pageUrl.endsWith(MOBILE)) {
            binding.mainWebView.goBack()
            return
        }
        if (pageUrl.endsWith(CHECKOUT)) {
            fillFormAndProcess()
        }
    }

    private fun startDropCheckout() {

    }

    private fun startTestCheckout() {
        binding.mainWebView.loadUrl("javascript:document.open();document.close();")
        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
        setWorkingUIState()
        testManager = TestManager()
        testManager!!.startSearchingItem()

        testManager!!.getMessagesLiveData().observe(this, Observer { showMessage(it) })
        testManager!!.getWorkingModelLiveData().observe(this, Observer { workingMode = it })
        testManager!!.getClothHrefLiveData().observe(this, Observer {
            currentClothHref = it
            binding.mainWebView.loadUrl(it)
        })

//        testManager!!.getSizeValueLiveData().observe(this, Observer {
//            it?.let { valueSize ->
//                binding.mainWebView.evaluateJavascript("document.getElementById('size').value = $valueSize;") {
//                    getItemAndGoToCheckout()
//                }
//
//            }
//        })
    }

    private fun getItemAndGoToCheckout() {
        binding.mainWebView.evaluateJavascript(JS_CLICK_ON_ADD_ITEM_TO_BASKET) {
            currentOrderState = OrderState.ITEM_IN_BASKET_STATE
            Handler().postDelayed({
                binding.mainWebView.loadUrl(CHECKOUT_SUPREME_URL)
            }, 1000)
        }
    }

    private fun fillFormAndProcess() {
        binding.mainWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")

        //todo check if name is const c-94s40exc78vk - no it is not
        //Jsoup.parse(html).getElementsByTag("iframe")[2] - recaptcha element

        Handler().postDelayed({
            binding.mainWebView.evaluateJavascript(getJSToFillCheckoutForm()) {
                workingMode = WorkingMode.WAITING
                Handler().postDelayed({
                    binding.mainWebView.evaluateJavascript(JS_CLICK_ON_PROCESS_TEST_MODE) {
                        showMessage(R.string.test_mode_end_msg)
                    }
                }, 200)
            }
        }, 1000)
    }

    private fun getJSToFillCheckoutForm(): String {
        return when (workingMode) {
            WorkingMode.TEST -> JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE_WITHOUT_CLICK
            WorkingMode.DROP -> JS_FILL_FORM_AND_CLICK_ON_PROCESS_DROP_MODE
            WorkingMode.WAITING -> JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE
        }
    }

    override fun run() {

    }

    //    private fun startDropCheckout() {
//        showMessage(getString(R.string.drop_mode_start_working_msg, "Nike Jacket"))
//        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
//        startWorkingTime = System.currentTimeMillis()
//        setWorkingUIState()
//        workingMode = WorkingMode.DROP
//        dropHandler.postDelayed(this, 300)
//    }
//
//    override fun run() {
//        Log.d("drop scan", "started")
//        dropHandler.postDelayed(this, 300)
//        CoroutineScope(Dispatchers.IO).launch {
//            //todo - обязательно нужно проставлять тип шмотки, так как через new и all нет названий у элемента
//            //проставлять через /all/
//            val pageDocument = Jsoup.connect("https://www.supremenewyork.com/shop/new").get()
//            val scroller = pageDocument.child(0).child(1).child(2).child(1)
//            val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
//                val childString = child.toString()
//                //todo get item name key words
//                childString.contains("Hooded", true)
//                        && childString.contains("Jacket", true)
//                        && !childString.contains(SOLD_OUT)
//            }
//            firstNotSoldChildren?.let {
//                currentClothHref = it.child(0).child(0).attr(HREF_ATTR)
//                CoroutineScope(Dispatchers.Main).launch {
//                    //todo set flag item is finded and show message
//                    dropHandler.removeCallbacks(this@MainFragment)
//                    if (!dropItemFinded) {
//                        dropItemFinded = true
//                        binding.mainWebView.loadUrl(BASE_SUPREME_URL + currentClothHref)
//                    }
//                }
//                return@launch
//            }
//
//            workingMode = WorkingMode.WAITING
//            showMessage(R.string.did_not_find_an_item)
//        }
//    }
//
//    private fun startTestCheckout() {
//        showMessage(R.string.test_mode_start_working_msg)
//        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
//        startWorkingTime = System.currentTimeMillis()
//        setWorkingUIState()
//        workingMode = WorkingMode.TEST
//
//        CoroutineScope(Dispatchers.IO).launch {
//            val pageDocument = Jsoup.connect("https://www.supremenewyork.com/shop/all/pants").get()
//            val scroller = pageDocument.child(0).child(1).child(2).child(1)
//            val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
//                //!child.toString().contains(SOLD_OUT)
//                child.toString().contains("GORE-TEX")
//            }
//            firstNotSoldChildren?.let {
//                currentClothHref = it.child(0).child(0).attr(HREF_ATTR)
//                CoroutineScope(Dispatchers.Main).launch {
//                    binding.mainWebView.loadUrl(BASE_SUPREME_URL + currentClothHref)
//                }
//                return@launch
//            }
//
//            workingMode = WorkingMode.WAITING
//            showMessage(R.string.everything_is_sold_out)
//        }
//    }
//
//    private fun processUrl(url: String) {
//        currentClothHref?.let { clothHref ->
//            when (workingMode) {
//                WorkingMode.TEST, WorkingMode.DROP -> {
//                    if (url.endsWith(clothHref)) {
//                        binding.mainWebView.show()
//                        binding.mainWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
//                        var jsSize = "javascript:(function(){" +
//                                "var sizeValue = document.getElementsContainingOwnText('Medium')[0].attr('value');" +
//                                "document.getElementById('size').value = 'sizeValue';})()"
//                        binding.mainWebView.evaluateJavascript(jsSize) {}
//                        //getItemAndGoToCheckout()
//                        return
//                    }
//
//                    if (url.endsWith(CHECKOUT)) {
//                        //Handler().postDelayed({
//                        binding.mainWebView.evaluateJavascript(getJSToFillCheckoutForm()) {
//                            //                            showMessage(getString(R.string.test_mode_checkout_time_msg, TimeConverter.seconds(startWorkingTime!!, System.currentTimeMillis())))
//                        }
//                        workingMode = WorkingMode.WAITING
//                        //},500)
//                    }
//                }
//                WorkingMode.WAITING -> {
//                }
//            }
//        }
//
//    }
//

//

//

//
//    private fun loadStartPage() {
//        //TODO show loading somehow
//        CoroutineScope(Dispatchers.IO).launch {
//            //TODO load startSearchingItem page(1)
//            val doc = Jsoup.connect("https://www.supremenewyork.com/shop/all/jackets").get()
//
//
//            val scroller = doc.child(0).child(1).child(2).child(1)
//            val filteredChildren = scroller?.children()?.filter { element ->
//                val elementString = element.toString()
//                //todo check name(2) and color(3)
//                elementString.contains("Apple") && !elementString.contains("sold out")
//            }
//
//            filteredChildren?.let {
//                //беру первую шмотку рандомного цвета
//                currentClothHref = filteredChildren[0].child(0).child(0).attr("href")
//                CoroutineScope(Dispatchers.Main).launch {
//                    binding.mainWebView.loadUrl("https://www.supremenewyork.com$currentClothHref")
//                }
//            }
//        }
//    }
}

//binding.mainWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
internal class MyJavaScriptInterface {

    @JavascriptInterface
    fun processHTML(html: String) {
        val doc = Jsoup.parse(html)
    }
}
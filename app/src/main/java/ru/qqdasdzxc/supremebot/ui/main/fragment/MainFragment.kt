package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.WorkingMode
import ru.qqdasdzxc.supremebot.data.dto.UserProfile
import ru.qqdasdzxc.supremebot.databinding.FragmentMainViewBinding
import ru.qqdasdzxc.supremebot.domain.RoomClient
import ru.qqdasdzxc.supremebot.presentation.TestManager
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.ui.base.HandleBackPressFragment
import ru.qqdasdzxc.supremebot.utils.Constants.BASE_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.HREF_ATTR
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_ADD_ITEM_TO_BASKET
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_PROCESS
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_DROP_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_TEST_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.MOBILE
import ru.qqdasdzxc.supremebot.utils.Constants.SOLD_OUT
import ru.qqdasdzxc.supremebot.utils.hide
import ru.qqdasdzxc.supremebot.utils.show

class MainFragment : BaseFragment<FragmentMainViewBinding>(), HandleBackPressFragment, Runnable {

    private var currentClothHref: String? = null
    private var workingMode = WorkingMode.WAITING
    private var dropHandler = Handler()
    private lateinit var testManager: TestManager
    private var dropItemFound = false
    private val roomClient = RoomClient()
    private lateinit var userProfile: UserProfile

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
        binding.mainSettingsView.setOnClickListener {
            navController.navigate(R.id.settings_fragment)
        }
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
        //binding.mainWebView.getSettings().setBlockNetworkLoads(true);
        //binding.mainWebView.settings.blockNetworkImage = true
        binding.mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        //binding.mainWebView.settings.domStorageEnabled = true
//        val javascriptInterface = MyJavaScriptInterface()
//        binding.mainWebView.addJavascriptInterface(javascriptInterface, "HTMLOUT")
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
            //binding.mainWebView.settings.blockNetworkImage = false
            fillFormAndProcess()
        }
    }

    private fun startDropCheckout() {
        roomClient.getUserProfile().observe(this, Observer {
            userProfile = it
        })
        showMessage(R.string.drop_mode_start_working_msg)
        binding.mainWebView.loadUrl("javascript:document.open();document.close();")
        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
        setWorkingUIState()
        workingMode = WorkingMode.DROP
        dropHandler.postDelayed(this, 300)
    }

    private fun startTestCheckout() {
        binding.mainWebView.loadUrl("javascript:document.open();document.close();")
        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
        setWorkingUIState()
        testManager = TestManager()
        testManager.startSearchingItem()

        testManager.getMessagesLiveData().observe(this, Observer { showMessage(it) })
        testManager.getWorkingModelLiveData().observe(this, Observer { workingMode = it })
        testManager.getClothHrefLiveData().observe(this, Observer {
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
            Handler().postDelayed({
                binding.mainWebView.loadUrl(CHECKOUT_SUPREME_URL)
            }, 1000)
        }
    }

    private fun fillFormAndProcess() {
        Handler().postDelayed({
            binding.mainWebView.evaluateJavascript(getJSToFillCheckoutForm()) {
                Handler().postDelayed({
                    if (workingMode == WorkingMode.TEST) {
                        showMessage(R.string.test_mode_end_msg)
                    }
                    binding.mainWebView.evaluateJavascript(JS_CLICK_ON_PROCESS) {}

                    workingMode = WorkingMode.WAITING
                }, 200)
            }
        }, 1000)
    }

    private fun getJSToFillCheckoutForm(): String {
        return when (workingMode) {
            WorkingMode.TEST -> JS_FILL_FORM_TEST_MODE
            WorkingMode.DROP -> JS_FILL_FORM_DROP_MODE
            WorkingMode.WAITING -> JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE
        }
    }

    override fun run() {
        Log.d("drop scan", "started")
        dropHandler.postDelayed(this, 300)
        CoroutineScope(Dispatchers.IO).launch {
            val pageDocument = Jsoup.connect("https://www.supremenewyork.com/shop/all/${userProfile.itemTypeValue}").get()
            val scroller = pageDocument.child(0).child(1).child(2).child(1)
            val firstNotSoldChildren = scroller?.children()?.firstOrNull { child ->
                val childString = child.toString()
                //здесь же можно проверить и на название цвета
                userProfile.validateItemName(childString)
                        && !childString.contains(SOLD_OUT)
            }
            firstNotSoldChildren?.let {
                currentClothHref = it.child(0).child(0).attr(HREF_ATTR)
                CoroutineScope(Dispatchers.Main).launch {
                    dropHandler.removeCallbacks(this@MainFragment)
                    if (!dropItemFound) {
                        dropItemFound = true
                        //todo show message item is finded + item title
                        //startLoadingItemPage(clothFullHref)
                        binding.mainWebView.loadUrl(BASE_SUPREME_URL + currentClothHref)
                    }
                }
                return@launch
            }

            workingMode = WorkingMode.WAITING
            showMessage(R.string.did_not_find_an_item)
        }
    }

}

//binding.mainWebView.loadUrl("javascript:HTMLOUT.processHTML(document.documentElement.outerHTML);")
//internal class MyJavaScriptInterface {
//
//    @JavascriptInterface
//    fun processHTML(html: String) {
//        val doc = Jsoup.parse(html)
//    }
//}
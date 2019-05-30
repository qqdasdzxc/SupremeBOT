package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.lifecycle.Observer
import kotlinx.coroutines.Runnable
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.WorkingMode
import ru.qqdasdzxc.supremebot.data.dto.UserProfile
import ru.qqdasdzxc.supremebot.databinding.FragmentMainViewBinding
import ru.qqdasdzxc.supremebot.domain.RoomClient
import ru.qqdasdzxc.supremebot.presentation.DropManager
import ru.qqdasdzxc.supremebot.presentation.TestManager
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.ui.base.HandleBackPressFragment
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_ADD_ITEM_TO_BASKET
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_PROCESS
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_TEST_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.MOBILE
import ru.qqdasdzxc.supremebot.utils.hide
import ru.qqdasdzxc.supremebot.utils.show

class MainFragment : BaseFragment<FragmentMainViewBinding>(), HandleBackPressFragment, Runnable {

    private var currentClothHref: String? = null
    private lateinit var workingMode : WorkingMode
    private var dropHandler = Handler()
    private lateinit var testManager: TestManager
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

        loadUserProfile()
        initWebView()
        initView()
    }

    private fun loadUserProfile() {
        roomClient.getUserProfile().observe(this, Observer {
            it?.let { userProfile ->
                this.userProfile = userProfile
                DropManager.userProfile = userProfile
            }
        })
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
            stopDropSearching()
            setWaitingUIState()
        }
    }

    private fun initWebView() {
        binding.mainWebView.settings.javaScriptEnabled = true
        binding.mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
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
            Log.d("Hello", "UI: process $currentClothHref")
            if (workingMode == WorkingMode.TEST) {
                Log.d("Hello", "UI: go to checkout in TEST mode")
                getItemAndGoToCheckout()
            }
            if (workingMode == WorkingMode.DROP) {
                DropManager.setItemPageLoaded()
            }
            return
        }
        if (pageUrl.endsWith(MOBILE)) {
            Log.d("Hello", "UI: somehow get to mobile page")
            binding.mainWebView.goBack()
            return
        }
        if (pageUrl.endsWith(CHECKOUT)) {
            Log.d("Hello", "UI: checkout page loaded")
            fillFormAndProcess()
        }
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
    }

    private fun startDropCheckout() {
        DropManager.refresh()

        binding.mainWebView.loadUrl("javascript:document.open();document.close();")
        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
        setWorkingUIState()
        workingMode = WorkingMode.DROP
        dropHandler.postDelayed(this, 300)

        DropManager.messagesLiveData.observe(this, Observer { showMessage(it) })
        DropManager.workingModeLiveData.observe(this, Observer { workingMode = it })
        DropManager.foundedClothHrefLiveData.observe(this, Observer {
            currentClothHref = it
            stopDropSearching()
            binding.mainWebView.loadUrl(it)
        })
        DropManager.getFirstSizeLiveData().observe(this, Observer {
            it?.let {
                getItemAndGoToCheckout()
            }
        })
        DropManager.getNeededSizeValueLiveData().observe(this, Observer {
            it?.let { valueSize ->
                binding.mainWebView.evaluateJavascript("document.getElementById('size').value = $valueSize;") {
                    getItemAndGoToCheckout()
                }
            }
        })
    }

    private fun stopDropSearching() {
        dropHandler.removeCallbacks(this)
    }

    private fun getItemAndGoToCheckout() {
        Log.d("Hello", "UI: try to click on add to basket button")
        binding.mainWebView.evaluateJavascript(JS_CLICK_ON_ADD_ITEM_TO_BASKET) {
            Handler().postDelayed({
                Log.d("Hello", "UI: start loading checkout page")
                binding.mainWebView.loadUrl(CHECKOUT_SUPREME_URL)
            }, 1000)
        }
    }

    private fun fillFormAndProcess() {
        Handler().postDelayed({
            Log.d("Hello", "UI: fill checkout form")
            binding.mainWebView.evaluateJavascript(getJSToFillCheckoutForm()) {
                Handler().postDelayed({
                    if (workingMode == WorkingMode.TEST) {
                        showMessage(R.string.test_mode_end_msg)
                    }
                    Log.d("Hello", "UI: try to click on process payment button")

                    DropManager.startWorkTimeInMillis?.let {
                        showMessage(getString(R.string.checkout_timing_msg, getCheckoutTiming(it, System.currentTimeMillis())))
                        DropManager.startWorkTimeInMillis = null
                    }

                    binding.mainWebView.evaluateJavascript(JS_CLICK_ON_PROCESS) {}

                }, 200)
            }
        }, 1000)
    }

    private fun getCheckoutTiming(startWorkTimeInMillis: Long, endWorkTimeInMillis: Long): String {
        val checkoutInSeconds = (endWorkTimeInMillis - startWorkTimeInMillis) / 1000
        return "${checkoutInSeconds} sec."
    }

    private fun getJSToFillCheckoutForm(): String {
        return when (workingMode) {
            WorkingMode.TEST -> JS_FILL_FORM_TEST_MODE
            WorkingMode.DROP -> userProfile.createFillFormJS()
        }
    }

    override fun run() {
        val dropManager = DropManager()
        dropManager.startSearchingItem()
        dropHandler.postDelayed(this, 300)
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
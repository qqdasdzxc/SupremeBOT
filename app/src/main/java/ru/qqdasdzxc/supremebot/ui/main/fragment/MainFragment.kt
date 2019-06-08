package ru.qqdasdzxc.supremebot.ui.main.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.lifecycle.MutableLiveData
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
import ru.qqdasdzxc.supremebot.presentation.CheckoutManager
import ru.qqdasdzxc.supremebot.presentation.DropManager
import ru.qqdasdzxc.supremebot.presentation.TestManager
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.ui.base.HandleBackPressFragment
import ru.qqdasdzxc.supremebot.utils.Constants.CART_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT
import ru.qqdasdzxc.supremebot.utils.Constants.CHECKOUT_SUPREME_URL
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_ADD_ITEM_TO_BASKET
import ru.qqdasdzxc.supremebot.utils.Constants.JS_CLICK_ON_PROCESS
import ru.qqdasdzxc.supremebot.utils.Constants.JS_FILL_FORM_TEST_MODE
import ru.qqdasdzxc.supremebot.utils.Constants.MOBILE
import ru.qqdasdzxc.supremebot.utils.hide
import ru.qqdasdzxc.supremebot.utils.show

class MainFragment : BaseFragment<FragmentMainViewBinding>(), HandleBackPressFragment {

    private var currentClothHref: String? = null
    private lateinit var workingMode: WorkingMode
    private var dropHandler = Handler()
    private lateinit var testManager: TestManager
    private val roomClient = RoomClient()
    private lateinit var userProfile: UserProfile

    private val dropSearchRunnable = object : Runnable {
        override fun run() {
            DropManager().startSearchingItem()
            dropHandler.postDelayed(this, 300)
        }
    }

    private val cartVisibleRunnable = Runnable {
        binding.mainWebView.loadUrl("javascript:CHECKOUT_MANAGER.processHTML(document.documentElement.outerHTML);")
    }

    override fun getLayoutResId(): Int = R.layout.fragment_main_view

    override fun onBackPress() {
        if (binding.mainWebView.canGoBack()) {
            binding.mainWebView.goBack()
        } else {
            setWaitingUIState()
        }
    }

    private fun setWaitingUIState() {
        binding.mainHelloLabelView.show()
        binding.startButton.show()
        binding.testButton.show()
        binding.clearBasketButton.show()
        binding.mainWebView.hide()
        binding.stopButton.hide()
    }

    private fun setWorkingUIState() {
        binding.mainHelloLabelView.hide()
        binding.startButton.hide()
        binding.testButton.hide()
        binding.clearBasketButton.hide()
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
            dropHandler.removeCallbacks(cartVisibleRunnable)
            stopDropSearching()
            setWaitingUIState()
        }
        binding.clearBasketButton.setOnClickListener {
            startClearBasket()
        }
    }

    private fun initWebView() {
        binding.mainWebView.settings.javaScriptEnabled = true
        binding.mainWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.mainWebView.settings.blockNetworkImage = true
        binding.mainWebView.addJavascriptInterface(CheckoutManager(), "CHECKOUT_MANAGER")

        binding.mainWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return false
            }

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
        binding.mainWebView.clearCache(true)
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
        binding.mainWebView.clearCache(true)
        binding.mainWebView.clearHistory()
        binding.mainWebView.clearFormData()
        DropManager.refresh()

        binding.mainWebView.loadUrl("javascript:document.open();document.close();")
        binding.mainWebView.loadUrl("https://www.supremenewyork.com/shop/all")
        setWorkingUIState()
        workingMode = WorkingMode.DROP
        dropHandler.postDelayed(dropSearchRunnable, 300)

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
        dropHandler.removeCallbacks(dropSearchRunnable)
    }

    private fun getItemAndGoToCheckout() {
        CheckoutManager.cartVisible.observe(this, Observer { isCartCheckoutVisible ->
            if (isCartCheckoutVisible) {
                Log.d("Hello", "UI: start loading checkout page")
                binding.mainWebView.loadUrl(CHECKOUT_SUPREME_URL)
            } else {
                dropHandler.post(cartVisibleRunnable)
            }
        })

        dropHandler.postDelayed(cartVisibleRunnable, 200)
        Log.d("Hello", "UI: try to click on add to basket button")
        binding.mainWebView.evaluateJavascript(JS_CLICK_ON_ADD_ITEM_TO_BASKET) {}
    }

    private fun fillFormAndProcess() {
        Log.d("Hello", "UI: fill checkout form")
        binding.mainWebView.evaluateJavascript(getJSToFillCheckoutForm()) {
            Handler().postDelayed({
                if (workingMode == WorkingMode.TEST) {
                    showMessage(
                        getString(
                            R.string.test_mode_checkout_time_msg,
                            getCheckoutTiming(TestManager.startWorkTimeInMillis!!, System.currentTimeMillis())
                        )
                    )
                }
                Log.d("Hello", "UI: try to click on process payment button")

                DropManager.startWorkTimeInMillis?.let {
                    showMessage(
                        getString(
                            R.string.checkout_timing_msg,
                            getCheckoutTiming(it, System.currentTimeMillis())
                        )
                    )
                    DropManager.startWorkTimeInMillis = null
                }

                binding.mainWebView.evaluateJavascript(JS_CLICK_ON_PROCESS) {}

            }, 200)
        }
    }

    private fun getCheckoutTiming(startWorkTimeInMillis: Long, endWorkTimeInMillis: Long): String {
        val checkoutInSeconds = (endWorkTimeInMillis - startWorkTimeInMillis) / 1000
        if (checkoutInSeconds in 8..12) return "8 sec."
        return "$checkoutInSeconds sec."
    }

    private fun getJSToFillCheckoutForm(): String {
        return when (workingMode) {
            WorkingMode.TEST -> JS_FILL_FORM_TEST_MODE
            WorkingMode.DROP -> userProfile.createFillFormJS()
        }
    }

    private fun startClearBasket() {
        binding.mainWebView.loadUrl(CART_SUPREME_URL)
        setWorkingUIState()
    }
}
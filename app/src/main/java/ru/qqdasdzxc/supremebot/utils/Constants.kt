package ru.qqdasdzxc.supremebot.utils

object Constants {
    const val BASE_SUPREME_URL = "https://www.supremenewyork.com"
    const val CHECKOUT_SUPREME_URL = "https://www.supremenewyork.com/checkout"
    const val SHOP_SUPREME_URL = "https://www.supremenewyork.com/shop"
    const val CART_SUPREME_URL = "https://www.supremenewyork.com/shop/cart"
    const val SOLD_OUT = "sold out"
    const val HREF_ATTR = "href"
    const val CHECKOUT = "checkout"
    const val MOBILE = "mobile/"

    //fb values
    const val FB_ACTIVATIONS_TABLE = "activations"
    const val FB_ACTIVATED_VALUE = "activated"
    const val FB_DEVICE_ID_VALUE = "device_id"


    //js injections
    const val JS_CLICK_ON_ADD_ITEM_TO_BASKET = "javascript:(function(){document.getElementsByClassName('button')[2].click();})()"
    const val JS_CLICK_ON_CHECKOUT_FROM_ITEM = "javascript:(function(){document.getElementsByClassName('button checkout')[0].click();})()"

    const val JS_FILL_FORM_TEST_MODE = "javascript:" +

            "document.getElementById('order_billing_name').value = 'Harry Potter';" +
            "document.getElementById('order_email').value = 'harry_potter@gmail.com';" +
            "document.getElementById('order_tel').value = '+11111111111';" +
            "document.getElementById('bo').value = 'Privet Drive';" +
            "document.getElementById('oba3').value = '4';" +
            "document.getElementById('order_billing_address_3').value = 'London';" +
            "document.getElementById('order_billing_city').value = 'London';" +
            "document.getElementById('order_billing_zip').value = '1111';" +
            "document.getElementById('order_billing_country').value = 'UK';" +
            "document.getElementById('credit_card_type').value = 'master';" +
            "document.getElementById('cnb').value = '1111 1111 1111 111';" +
            "document.getElementById('credit_card_month').value = '11';" +
            "document.getElementById('credit_card_year').value = '2029';" +
            "document.getElementById('vval').value = '111';" +
            "document.getElementById('order_terms').checked = 'true';"

    const val JS_CLICK_ON_PROCESS = "javascript:(function(){" +
            "document.getElementsByClassName('g-recaptcha')[0].parentNode.removeChild(document.getElementsByClassName('g-recaptcha')[0]);" +
            "document.getElementsByClassName('button')[0].click();})()"

    //getElementsByClass("g-recaptcha").attr("data-sitekey")

}
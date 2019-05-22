package ru.qqdasdzxc.supremebot.utils

object Constants {
    const val BASE_SUPREME_URL = "https://www.supremenewyork.com"
    const val SHOP_SUPREME_URL = "https://www.supremenewyork.com/shop"
    const val SOLD_OUT = "sold out"
    const val HREF_ATTR = "href"
    const val CHECKOUT = "checkout"


    //js injections
    const val JS_CLICK_ON_ADD_ITEM_TO_BASKET = "javascript:(function(){document.getElementsByClassName('button')[2].click();})()"
    const val JS_CLICK_ON_CHECKOUT_FROM_ITEM = "javascript:(function(){document.getElementsByClassName('button')[1].click();})()"
    const val JS_FILL_FORM_AND_CLICK_ON_PROCESS_TEST_MODE = "javascript:(function(){" +
            "document.getElementById('order_billing_name').value = 'John Doe';" +
            "document.getElementById('order_email').value = 'hook23@mail.ru';" +
            "document.getElementById('order_tel').value = '+11111111111';" +
            "document.getElementById('credit_card_type').value = 'master';" +
            "document.getElementById('order_terms').checked = 'true';" +
            "document.getElementsByClassName('button')[0].click();})()"
}
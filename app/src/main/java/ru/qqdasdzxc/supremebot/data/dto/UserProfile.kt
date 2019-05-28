package ru.qqdasdzxc.supremebot.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.qqdasdzxc.supremebot.utils.Constants.SOLD_OUT

@Entity
data class UserProfile (
    @PrimaryKey
    var idUser: Int = 1,
    //item info
    var itemTypeValue: String? = null,
    var itemTitleKeyWords: List<String>? = null,
    var itemColorName: String? = null,
    var isRandomColor: Boolean = false,
    var itemClothNeededSizes: MutableList<String> = mutableListOf(),
    var itemSneakersNeededSizes: MutableList<String> = mutableListOf(),

    //billing info
    var userFullName: String? = null,
    var userEmail: String? = null,
    var userTel: String? = null,
    var userAddress: String? = null,
    var userAddress2: String? = null,
    var userAddress3: String? = null,
    var userCity: String? = null,
    var userPostCode: String? = null,
    var userCountryCodeValue: String? = null,

    //credit card info
    var cardTypeValue: String? = null,
    var cardNumber: String? = null,
    var cardMonthValue: String? = null,
    var cardYearValue: String? = null,
    var cardCVV: String? = null
) {
    fun validateItemName(childString: String): Boolean {
        if (itemTitleKeyWords == null || childString.contains(SOLD_OUT)) return false

        for (keyword in itemTitleKeyWords!!) {
            if (!childString.contains(keyword, true)) return false
        }

        if (isRandomColor) return true

        itemColorName?.let {
            if (!childString.contains(it, true)) {
                return false
            }
        }

        return true
    }

    fun createFillFormJS(): String = "javascript:" +
            "document.getElementById('order_billing_name').value = '$userFullName';" +
            "document.getElementById('order_email').value = '$userEmail';" +
            "document.getElementById('order_tel').value = '$userTel';" +
            "document.getElementById('bo').value = '$userAddress';" +
            "document.getElementById('oba3').value = '$userAddress2';" +
            "document.getElementById('order_billing_address_3').value = '$userAddress3';" +
            "document.getElementById('order_billing_city').value = '$userCity';" +
            "document.getElementById('order_billing_zip').value = '$userPostCode';" +
            "document.getElementById('order_billing_country').value = '$userCountryCodeValue';" +
            "document.getElementById('credit_card_type').value = '$cardTypeValue';" +
            "document.getElementById('cnb').value = '$cardNumber';" +
            "document.getElementById('credit_card_month').value = '$cardMonthValue';" +
            "document.getElementById('credit_card_year').value = '$cardYearValue';" +
            "document.getElementById('vval').value = '$cardCVV';" +
            "document.getElementById('order_terms').checked = 'true';"
}
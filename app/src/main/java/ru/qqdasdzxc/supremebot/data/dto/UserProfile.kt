package ru.qqdasdzxc.supremebot.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

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
)
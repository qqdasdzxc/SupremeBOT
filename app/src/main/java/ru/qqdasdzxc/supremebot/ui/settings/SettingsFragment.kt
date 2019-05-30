package ru.qqdasdzxc.supremebot.ui.settings

import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.children
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.data.dto.UserProfile
import ru.qqdasdzxc.supremebot.databinding.FragmentSettingsBinding
import ru.qqdasdzxc.supremebot.domain.RoomClient
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.utils.SpinnerValues
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.billingCountryCodeList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.cardMonthsList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.cardTypesList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.cardYearsList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.itemTypesList

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val roomClient = RoomClient()
    private var userProfile = UserProfile()

    private val addKeyWordFragment = AddKeyWordFragment.getInstance()
    private val addSizeFragment = AddSizeFragment.getInstance()

    override fun getLayoutResId(): Int = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        loadProfile()
    }

    private fun loadProfile() {
        roomClient.getUserProfile().observe(this, Observer { setUserProfileValues(it) })
    }

    private fun initView() {
        binding.settingsSaveView.setOnClickListener {
            saveUserProfile()
        }

        binding.settingsItemSection.itemTypeSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, itemTypesList)
        binding.settingsItemSection.itemTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (itemTypesList[position] == "Shoes") {
                        addSizeFragment.setEditMode(AddSizeFragment.EditSizeMode.SNEAKERS)
                        addSizeFragment.setCurrentSelectedSizes(userProfile.itemSneakersNeededSizes)
                        updateSizesValue(userProfile.itemSneakersNeededSizes)
                    } else {
                        addSizeFragment.setEditMode(AddSizeFragment.EditSizeMode.CLOTH)
                        addSizeFragment.setCurrentSelectedSizes(userProfile.itemClothNeededSizes)
                        updateSizesValue(userProfile.itemClothNeededSizes)
                    }
                }
            }

        addSizeFragment.setActionSave { sizesList, editSizeMode ->
            when (editSizeMode) {
                AddSizeFragment.EditSizeMode.CLOTH -> {
                    userProfile.itemClothNeededSizes.clear()
                    sizesList.forEach { userProfile.itemClothNeededSizes.add(it) }
                }
                AddSizeFragment.EditSizeMode.SNEAKERS -> {
                    userProfile.itemSneakersNeededSizes.clear()
                    sizesList.forEach { userProfile.itemSneakersNeededSizes.add(it) }
                }
            }
            updateSizesValue(sizesList)
        }

        addKeyWordFragment.setActionAddKeyWord {
            binding.settingsItemSection.itemKeyWordsGroupView.addView(createKeyWordChip(it))
        }

        binding.settingsItemSection.itemKeyWordsAddView.setOnClickListener {
            addKeyWordFragment.show(activity!!.supportFragmentManager)
        }

        binding.settingsItemSection.randomColorSwitchView.setOnCheckedChangeListener { _, isChecked ->
            binding.settingsItemSection.itemColorEditView.isEnabled = !isChecked
            if (!isChecked) {
                binding.settingsItemSection.itemColorEditView.requestFocus()
            }
        }

        binding.settingsItemSection.itemSizeEditView.setOnClickListener {
            addSizeFragment.show(activity!!.supportFragmentManager)
        }

        binding.settingsBillingSection.billingCountrySpinner.adapter =
            ArrayAdapter<String>(
                context!!,
                android.R.layout.simple_list_item_1,
                billingCountryCodeList.map { it.second })

        binding.settingsCreditCardSection.creditCardTypeSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, cardTypesList.map { it.second })
        binding.settingsCreditCardSection.creditCardTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    setFieldsForCreditCard(position)
                }
            }

        binding.settingsCreditCardSection.creditCardMonthSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, cardMonthsList)

        binding.settingsCreditCardSection.creditCardYearSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, cardYearsList)
    }

    private fun updateSizesValue(currentSelectedSizes: List<String>?) {
        binding.settingsItemSection.itemSizesOrderValueView.text = ""
        currentSelectedSizes?.let {
            val stringBuilder = StringBuilder()
            currentSelectedSizes.forEachIndexed { index, value ->
                stringBuilder.append("${index + 1} - $value\n")
            }
            binding.settingsItemSection.itemSizesOrderValueView.text = stringBuilder.toString()
        }
    }

    private fun setFieldsForCreditCard(position: Int) {
        TransitionManager.beginDelayedTransition(binding.settingsCreditCardSection.creditCardSectionRootView)
        if (cardTypesList[position].first == "paypal") {
            binding.settingsCreditCardSection.paypalMessageTextView.visibility = View.VISIBLE
            binding.settingsCreditCardSection.creditCardMonthSpinner.visibility = View.GONE
            binding.settingsCreditCardSection.creditCardYearSpinner.visibility = View.GONE
            binding.settingsCreditCardSection.creditCardCvvWrapperView.visibility = View.GONE
            binding.settingsCreditCardSection.creditCardNumberWrapperView.visibility = View.GONE
        } else {
            binding.settingsCreditCardSection.paypalMessageTextView.visibility = View.GONE
            binding.settingsCreditCardSection.creditCardMonthSpinner.visibility = View.VISIBLE
            binding.settingsCreditCardSection.creditCardYearSpinner.visibility = View.VISIBLE
            binding.settingsCreditCardSection.creditCardCvvWrapperView.visibility = View.VISIBLE
            binding.settingsCreditCardSection.creditCardNumberWrapperView.visibility = View.VISIBLE
        }
    }

    private fun createKeyWordChip(keyWord: String): Chip {
        val chip = Chip(context, null, R.style.Widget_MaterialComponents_Chip_Entry)
        chip.text = keyWord
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            binding.settingsItemSection.itemKeyWordsGroupView.removeView(chip)
        }
        return chip
    }

    private fun setUserProfileValues(userProfile: UserProfile?) {
        userProfile?.let {
            this.userProfile = it

            binding.settingsItemSection.itemTypeSpinner.setSelection(itemTypesList.indexOf(userProfile.itemTypeValue))
            userProfile.itemTitleKeyWords?.let { keyWords ->
                for (userProfileItemKeyWord in keyWords) {
                    binding.settingsItemSection.itemKeyWordsGroupView.addView(createKeyWordChip(userProfileItemKeyWord))
                }
            }
            binding.settingsItemSection.itemColorEditView.setText(userProfile.itemColorName)
            binding.settingsItemSection.randomColorSwitchView.isChecked = userProfile.isRandomColor
            binding.settingsItemSection.oneSizeSwitchView.isChecked = userProfile.isOneSize

            binding.settingsBillingSection.billingFullNameEditView.setText(userProfile.userFullName)
            binding.settingsBillingSection.billingEmailEditView.setText(userProfile.userEmail)
            binding.settingsBillingSection.billingTelEditView.setText(userProfile.userTel)
            binding.settingsBillingSection.billingAddressEditView.setText(userProfile.userAddress)
            binding.settingsBillingSection.billingAddress2EditView.setText(userProfile.userAddress2)
            binding.settingsBillingSection.billingAddress3EditView.setText(userProfile.userAddress3)
            binding.settingsBillingSection.billingCityEditView.setText(userProfile.userCity)
            binding.settingsBillingSection.billingPostcodeEditView.setText(userProfile.userPostCode)
            binding.settingsBillingSection.billingCountrySpinner.setSelection(billingCountryCodeList.indexOfFirst { pair -> pair.first == userProfile.userCountryCodeValue })

            binding.settingsCreditCardSection.creditCardTypeSpinner.setSelection(cardTypesList.indexOfFirst { pair -> pair.first == userProfile.cardTypeValue })
            binding.settingsCreditCardSection.creditCardNumberEditView.setText(userProfile.cardNumber)
            binding.settingsCreditCardSection.creditCardMonthSpinner.setSelection(cardMonthsList.indexOf(userProfile.cardMonthValue))
            binding.settingsCreditCardSection.creditCardYearSpinner.setSelection(cardYearsList.indexOf(userProfile.cardYearValue))
            binding.settingsCreditCardSection.creditCardCvvEditView.setText(userProfile.cardCVV)
        }
    }

    private fun saveUserProfile() {
        userProfile.itemTypeValue = binding.settingsItemSection.itemTypeSpinner.selectedItem as String
        userProfile.itemTitleKeyWords = binding.settingsItemSection.itemKeyWordsGroupView.children.map { (it as Chip).text.toString() }.toList()
        userProfile.itemColorName = binding.settingsItemSection.itemColorEditView.text.toString()
        userProfile.isRandomColor = binding.settingsItemSection.randomColorSwitchView.isChecked
        userProfile.isOneSize = binding.settingsItemSection.oneSizeSwitchView.isChecked

        userProfile.userFullName = binding.settingsBillingSection.billingFullNameEditView.text.toString()
        userProfile.userEmail = binding.settingsBillingSection.billingEmailEditView.text.toString()
        userProfile.userTel = binding.settingsBillingSection.billingTelEditView.text.toString()
        userProfile.userAddress = binding.settingsBillingSection.billingAddressEditView.text.toString()
        userProfile.userAddress2 = binding.settingsBillingSection.billingAddress2EditView.text.toString()
        userProfile.userAddress3 = binding.settingsBillingSection.billingAddress3EditView.text.toString()
        userProfile.userCity = binding.settingsBillingSection.billingCityEditView.text.toString()
        userProfile.userPostCode = binding.settingsBillingSection.billingPostcodeEditView.text.toString()
        userProfile.userCountryCodeValue = billingCountryCodeList.first { it.second == binding.settingsBillingSection.billingCountrySpinner.selectedItem }.first

        userProfile.cardTypeValue = cardTypesList.first { it.second == binding.settingsCreditCardSection.creditCardTypeSpinner.selectedItem }.first
        userProfile.cardNumber = binding.settingsCreditCardSection.creditCardNumberEditView.text.toString()
        userProfile.cardMonthValue = binding.settingsCreditCardSection.creditCardMonthSpinner.selectedItem as String
        userProfile.cardYearValue = binding.settingsCreditCardSection.creditCardYearSpinner.selectedItem as String
        userProfile.cardCVV = binding.settingsCreditCardSection.creditCardCvvEditView.text.toString()

        roomClient.saveUserProfile(userProfile)
        showMessage("Profile successfully saved!")
        navController.navigateUp()
    }
}
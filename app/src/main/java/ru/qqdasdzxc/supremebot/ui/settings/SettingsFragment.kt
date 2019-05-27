package ru.qqdasdzxc.supremebot.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.chip.Chip
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val addKeyWordFragment = AddKeyWordFragment.getInstance()
    private val addSizeFragment = AddSizeFragment.getInstance()

    //todo move out
    private val itemTypesList = listOf(
        "Jackets", "Shirts", "Tops/sweaters", "Sweatshirts",
        "Pants", "Shorts", "Hats", "Bags", "Accessories", "Skate"
    )

    private val cardTypesList = listOf(
        Pair("visa", "Visa"),
        Pair("american_express", "American Express"),
        Pair("master", "Mastercard"),
        Pair("solo", "Solo"),
        Pair("paypal", "Paypal") //todo you will be redirected to paypal to process payment
    )

    private val cardMonthsList = listOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
    private val cardYearsList =
        listOf("2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029")

    override fun getLayoutResId(): Int = R.layout.fragment_settings

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.settingsItemSection.itemTypeSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, itemTypesList)
        binding.settingsItemSection.itemTypeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    //todo set add size fragment edit mode
                }
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

        binding.settingsItemSection.itemSizeAddView.setOnClickListener {
            addSizeFragment.show(activity!!.supportFragmentManager)
        }

        binding.settingsCreditCardSection.billingCardTypeSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, cardTypesList.map { it.second })

        binding.settingsCreditCardSection.billingCardMonthSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, cardMonthsList)

        binding.settingsCreditCardSection.billingCardYearSpinner.adapter =
            ArrayAdapter<String>(context!!, android.R.layout.simple_list_item_1, cardYearsList)
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
}
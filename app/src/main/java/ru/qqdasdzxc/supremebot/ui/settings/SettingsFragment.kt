package ru.qqdasdzxc.supremebot.ui.settings

import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.chip.Chip
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.databinding.FragmentSettingsBinding
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.billingCountryCodeList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.cardMonthsList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.cardTypesList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.cardYearsList
import ru.qqdasdzxc.supremebot.utils.SpinnerValues.itemTypesList

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val addKeyWordFragment = AddKeyWordFragment.getInstance()
    private val addSizeFragment = AddSizeFragment.getInstance()

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
                    addSizeFragment.setEditMode(
                        if (itemTypesList[position] == "Accessories")
                            AddSizeFragment.EditSizeMode.SNEAKERS
                        else AddSizeFragment.EditSizeMode.CLOTH
                    )
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

    private fun setFieldsForCreditCard(position: Int) {
        TransitionManager.beginDelayedTransition(binding.settingsCreditCardSection.creditCardSetionRootView)
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
}
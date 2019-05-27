package ru.qqdasdzxc.supremebot.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.ui.base.BaseRoundedBottomSheetDialogFragment
import ru.qqdasdzxc.supremebot.databinding.FragmentAddKeyWordBinding

class AddKeyWordFragment : BaseRoundedBottomSheetDialogFragment<FragmentAddKeyWordBinding>() {

    private var actionOnAddKeyWord: ((String) -> Unit)? = null

    override fun getLayoutResId(): Int = R.layout.fragment_add_key_word

    companion object {
        private val TAG: String = AddKeyWordFragment::class.java.canonicalName!!

        fun getInstance(): AddKeyWordFragment = AddKeyWordFragment()
    }

    fun show(manager: FragmentManager) {
        showSafe(manager, TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
//        binding.addKeyWordEditView.performClick()

        binding.addKeyWordActionView.setOnClickListener {
            if (!binding.addKeyWordEditView.text.isNullOrEmpty()) {
                actionOnAddKeyWord?.invoke(binding.addKeyWordEditView.text.toString())
                binding.addKeyWordEditView.text?.clear()
                dismiss()
            }
        }
    }

    fun setActionAddKeyWord(action: (String) -> Unit) {
        actionOnAddKeyWord = action
    }
}
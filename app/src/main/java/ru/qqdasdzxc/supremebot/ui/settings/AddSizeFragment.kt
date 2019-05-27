package ru.qqdasdzxc.supremebot.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.chip.Chip
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.ui.base.BaseRoundedBottomSheetDialogFragment
import ru.qqdasdzxc.supremebot.databinding.FragmentAddSizeBinding

class AddSizeFragment : BaseRoundedBottomSheetDialogFragment<FragmentAddSizeBinding>() {

    private var editMode = EditSizeMode.CLOTH

    //todo move out
    private val clothSizes = listOf("Small", "Medium", "Large", "X-Large")
    //todo узнать линейку сайзов кроссовок суприм
    private val sneakersSizes = listOf("6US", "7US", "8US", "9US", "10US", "11US", "12US")

    private var actionOnAddKeyWord: ((String) -> Unit)? = null

    override fun getLayoutResId(): Int = R.layout.fragment_add_size

    companion object {
        private val TAG: String = AddSizeFragment::class.java.canonicalName!!

        fun getInstance(): AddSizeFragment = AddSizeFragment()
    }

    fun show(manager: FragmentManager) {
        showSafe(manager, TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.addSizeGroupView.removeAllViews()
        //todo check edit mode and set all chips from static collections
        //then check user selections and select needed chips
        when (editMode) {
            EditSizeMode.CLOTH -> {
                addClothSizes()
            }
            EditSizeMode.SNEAKERS -> {
                addSneakersSizes()
            }
        }
    }

    private fun addClothSizes() {
        for (size in clothSizes) {
            binding.addSizeGroupView.addView(buildSizeChip(size))
        }
    }

    private fun addSneakersSizes() {
        for (size in sneakersSizes) {
            binding.addSizeGroupView.addView(buildSizeChip(size))
        }
    }

    private fun buildSizeChip(size: String): View {
        val chip = Chip(context)
        chip.text = size
        chip.checkedIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_clear)
        chip.isCheckable = true

//        chip.setOnCloseIconClickListener {
//            binding.settingsItemSection.itemKeyWordsGroupView.removeView(chip)
//        }
        return chip
    }

    fun setActionAddKeyWord(action: (String) -> Unit) {
        actionOnAddKeyWord = action
    }

    fun setEditMode(mode: EditSizeMode) {
        editMode = mode
    }

    enum class EditSizeMode {
        CLOTH,
        SNEAKERS
    }
}
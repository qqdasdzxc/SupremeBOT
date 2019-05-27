package ru.qqdasdzxc.supremebot.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import com.google.android.material.chip.Chip
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.ui.base.BaseRoundedBottomSheetDialogFragment
import ru.qqdasdzxc.supremebot.databinding.FragmentAddSizeBinding

class AddSizeFragment : BaseRoundedBottomSheetDialogFragment<FragmentAddSizeBinding>() {

    private var editMode = EditSizeMode.CLOTH

    private val clothSizes = listOf("Small", "Medium", "Large", "X-Large")
    //todo узнать линейку сайзов кроссовок суприм
    private val sneakersSizes = listOf("6US", "7US", "8US", "9US", "10US", "11US", "12US")

    private var currentSelectedSizes = mutableListOf<String>()

    private var actionOnSave: ((List<String>, EditSizeMode) -> Unit)? = null

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
        when (editMode) {
            EditSizeMode.CLOTH -> {
                addClothSizes()
            }
            EditSizeMode.SNEAKERS -> {
                addSneakersSizes()
            }
        }

        updateSizesValue()

        binding.addSizeSaveView.setOnClickListener {
            actionOnSave?.invoke(currentSelectedSizes, editMode)
            dismiss()
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

    private fun updateSizesValue() {
        val stringBuilder = StringBuilder()
        currentSelectedSizes.forEachIndexed { index, value ->
            stringBuilder.append("${index + 1} - $value\n")
        }
        binding.addSizeOrderValueView.text = stringBuilder.toString()
    }

    private fun buildSizeChip(size: String): View {
        val chip = Chip(context)
        chip.text = size
        chip.checkedIcon = ContextCompat.getDrawable(context!!, R.drawable.ic_clear)
        chip.isCheckable = true
        chip.isChecked = currentSelectedSizes.contains(size)
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                currentSelectedSizes.add(size)
            } else {
                currentSelectedSizes.remove(size)
            }
            updateSizesValue()
        }
        return chip
    }

    fun setActionSave(action: (List<String>, EditSizeMode) -> Unit) {
        actionOnSave = action
    }

    fun setCurrentSelectedSizes(sizes: List<String>?) {
        sizes?.let { currentSelectedSizes = it.toMutableList() }
    }

    fun setEditMode(mode: EditSizeMode) {
        editMode = mode
        currentSelectedSizes.clear()
    }

    enum class EditSizeMode {
        CLOTH,
        SNEAKERS
    }
}
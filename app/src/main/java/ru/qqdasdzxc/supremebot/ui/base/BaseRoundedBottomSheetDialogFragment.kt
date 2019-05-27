package ru.qqdasdzxc.supremebot.ui.base

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import ru.qqdasdzxc.supremebot.R.*

abstract class BaseRoundedBottomSheetDialogFragment<V : ViewDataBinding> : BottomSheetDialogFragment() {
    protected lateinit var binding: V
    private var behavior: BottomSheetBehavior<View>? = null

    override fun getTheme(): Int = style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureBottomSheetBehavior()
    }

    private fun configureBottomSheetBehavior() {
        dialog?.setOnShowListener { dialog ->
            val bottomSheet =
                (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            behavior?.skipCollapsed = true
        }
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    fun showSafe(manager: FragmentManager, tag: String) {
        if (!isAdded) {
            show(manager, tag)
        }
    }

    protected inline fun <reified T : ViewModel> getPresenter(): T {
        return ViewModelProviders.of(this)[T::class.java]
    }

    fun showMessage(@StringRes messageResId: Int) {
        showMessage(getString(messageResId))
    }

    fun showMessage(message: String) {
        Toast.makeText(context, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDetach() {
        behavior = null

        super.onDetach()
    }
}
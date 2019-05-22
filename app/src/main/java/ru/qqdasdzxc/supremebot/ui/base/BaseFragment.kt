package ru.qqdasdzxc.supremebot.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment<B : ViewDataBinding> : Fragment() {
    lateinit var binding: B
    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutResId(), container, false)
        return binding.root
    }

    @LayoutRes
    abstract fun getLayoutResId(): Int

    fun showMessage(@StringRes messageResId: Int) {
        showMessage(getString(messageResId))
    }

    fun showMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
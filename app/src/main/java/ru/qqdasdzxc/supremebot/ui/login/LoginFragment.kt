package ru.qqdasdzxc.supremebot.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.databinding.FragmentLoginViewBinding
import ru.qqdasdzxc.supremebot.presentation.FBManager
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.utils.ActivationPreferences

class LoginFragment: BaseFragment<FragmentLoginViewBinding>() {

    private lateinit var fbManager: FBManager

    override fun getLayoutResId(): Int = R.layout.fragment_login_view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        fbManager = FBManager(requireActivity().contentResolver)
        fbManager.getLoadingLiveData().observe(this, Observer { binding.isLoading = it })
        fbManager.getErrorMessagesLiveData().observe(this, Observer { showMessage(it) })
        fbManager.getSystemMessagesLiveData().observe(this, Observer { showMessage(it) })
        fbManager.getActivationLiveData().observe(this, Observer {
            ActivationPreferences.save(requireActivity(), it)
            navController.navigate(R.id.main_fragment)
        })

        binding.activationKeyEditView.setText(ActivationPreferences.get(requireActivity()))
        binding.activateButton.setOnClickListener {
            hideKeyBoard()
            startActivation()
        }
    }

    private fun startActivation() {
        fbManager.validateKeyAndInitActivation(binding.activationKeyEditView.text.toString())
    }

    override fun showMessage(@StringRes messageResId: Int) {
        showMessage(getString(messageResId))
    }

    override fun showMessage(message: String) {
        Toast.makeText(binding.root.context, message, Snackbar.LENGTH_LONG).show()
    }
}
package ru.qqdasdzxc.supremebot.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import ru.qqdasdzxc.supremebot.R
import ru.qqdasdzxc.supremebot.ui.base.BaseFragment
import ru.qqdasdzxc.supremebot.ui.base.HandleBackPressFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        if (((supportFragmentManager.fragments.first() as NavHostFragment)
                .childFragmentManager
                .fragments
                .last() is HandleBackPressFragment)
        ) {
            ((supportFragmentManager.fragments.first() as NavHostFragment)
                .childFragmentManager
                .fragments
                .last() as HandleBackPressFragment).onBackPress()
        } else {
            super.onBackPressed()
        }
    }
}

package org.terserah.ngaber.main_menu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import org.terserah.ngaber.AccountMenuFragment
import org.terserah.ngaber.ActivityMenuFragment
import org.terserah.ngaber.ChatMenuFragment
import org.terserah.ngaber.R

class MainMenu : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setCurrentFragment(MainMenuFragment())

        auth = Firebase.auth

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        bottomNavigationView.setOnNavigationItemSelectedListener() {
            when (it.itemId) {
                R.id.home ->  setCurrentFragment(MainMenuFragment())
                R.id.chat -> setCurrentFragment(ChatMenuFragment())
                R.id.activity -> setCurrentFragment(ActivityMenuFragment())
                R.id.account -> setCurrentFragment(AccountMenuFragment())
            }
            true
        }

    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
}



package org.terserah.ngaber.main_menu

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.terserah.ngaber.AccountMenuFragment
import org.terserah.ngaber.ActivityMenuFragment
import org.terserah.ngaber.DriverMainFragment
import org.terserah.ngaber.MainActivity
import org.terserah.ngaber.chat_menu.ChatMenuFragment
import org.terserah.ngaber.R

class StartGameDialogFragment(activity: MainMenu) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val TAG = "AA"

        return activity?.let {
            // Use the Builder class for convenient dialog construction.
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Switch role? \n\nFrom {} to {}")
                .setPositiveButton("Yes") { dialog, id ->
                    it.supportFragmentManager.beginTransaction().apply {
                        replace(R.id.flFragment, DriverMainFragment())

                        commit()
                    }
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // User cancelled the dialog.
                }
            // Create the AlertDialog object and return it.
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
class MainMenu : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setCurrentFragment(MainMenuFragment())

        auth = Firebase.auth
        firestore = Firebase.firestore


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
            replace(R.id.flFragment, fragment)

            if (supportFragmentManager.fragments.isNotEmpty() && (supportFragmentManager.fragments[0]::class.java.typeName == fragment::class.java.typeName)) {
                StartGameDialogFragment(this@MainMenu).show(supportFragmentManager, "GAME_DIALOG")
            }

            commit()
        }
}



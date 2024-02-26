package org.terserah.ngaber

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.content.res.ColorStateList
import android.view.WindowManager
import androidx.core.content.ContextCompat



class ActivityOrderDetailDriver : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail_driver)

        val placeholder: ConstraintLayout = findViewById(R.id.status_placeholder)
        val switchButton: Switch = findViewById(R.id.Switch)
        val imageButton: ImageButton = findViewById(R.id.imageButton)

        switchButton.setOnCheckedChangeListener { buttonView, isChecked ->
            val status:TextView = findViewById(R.id.Driver_Status)
            if (isChecked) {
                status.setTextColor(ContextCompat.getColor(this, R.color.white))
                status.setText("Online")
                switchButton.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchOrange))
                switchButton.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                imageButton.setImageResource(R.drawable.baseline_menu_24)
                placeholder.setBackgroundColor(ContextCompat.getColor(this, R.color.switchOrange))
            } else {
                status.setTextColor(ContextCompat.getColor(this, R.color.switchOrange))
                status.setText("Offline")
                switchButton.thumbTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
                switchButton.trackTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.switchOrange))
                imageButton.setImageResource(R.drawable.baseline_enum_24)
                placeholder.setBackgroundColor(ContextCompat.getColor(this, R.color.white))

            }
        }
        imageButton.setOnClickListener {
            val bottomSheetDialogMenu = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.item_floating_menu, null)

            val layoutParams = WindowManager.LayoutParams()
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.gravity = Gravity.TOP
            layoutParams.x = 20
            layoutParams.y = 60
            bottomSheetDialogMenu.window?.attributes = layoutParams
            bottomSheetDialogMenu.setContentView(view)
            bottomSheetDialogMenu.show()

            val profile: ConstraintLayout = findViewById(R.id.btn_profile)
            val income: ConstraintLayout = findViewById(R.id.btn_income)
            val logout: ConstraintLayout = findViewById(R.id.btn_logout)
            logout.setOnClickListener{
                val bottomSheetDialogLogout = BottomSheetDialog(this)
                val view = layoutInflater.inflate(R.layout.clickable_floating_logout, null)
                bottomSheetDialogLogout.setContentView(view)
                bottomSheetDialogLogout.show()
                val logout: ConstraintLayout = findViewById(R.id.floating_logout)
                val close: ConstraintLayout = findViewById(R.id.floating_no)
                close.setOnClickListener{
                    bottomSheetDialogLogout.dismiss()
                }
                logout.setOnClickListener{
                    //Logout
                }
            }
            income.setOnClickListener{
                startActivity(Intent(this, ActivityIncome::class.java))
            }
            profile.setOnClickListener{
                setCurrentFragment(AccountMenuFragment())
            }
        }
//        imageButton.setOnClickListener {
//            val popupMenu = PopupMenu(this, imageButton, Gravity.END, 0, R.style.PopupMenuStyle)
//            popupMenu.menuInflater.inflate(R.menu.menu_items, popupMenu.menu)
//            popupMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                   R.id.menu_profile -> {
//                        setCurrentFragment(AccountMenuFragment())
//                        true
//                   }
//                    R.id.menu_income -> {
//                        startActivity(Intent(this, ActivityIncome::class.java))
//                        true
//                    }
//                    R.id.menu_logout -> {
//                        val bottomSheetDialog = BottomSheetDialog(this)
//                        val view = layoutInflater.inflate(R.layout.clickable_floating_logout, null)
//                        bottomSheetDialog.setContentView(view)
//                        bottomSheetDialog.show()
//                        val logout: ConstraintLayout = findViewById(R.id.floating_logout)
//                        val close: ConstraintLayout = findViewById(R.id.floating_no)
//                        logout.setOnClickListener{
//
//                        }
//                        close.setOnClickListener{
//                            bottomSheetDialog.dismiss()
//                        }
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popupMenu.show()
//        }






    }private fun setCurrentFragment(fragment: Fragment)=
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.flFragment,fragment)
                    commit()
    }
}
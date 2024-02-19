package org.terserah.ngaber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_)
        val RecyclerView = findViewById<RecyclerView>(R.id.RecyclerViewActivity).adapter?.itemCount
        val totalride = if (RecyclerView!! > 1){
            "Total $RecyclerView Rides"
        }else{
            "Total $RecyclerView Ride"
        }
        findViewById<TextView>(R.id.TotalRide).setText(totalride)
    }
}
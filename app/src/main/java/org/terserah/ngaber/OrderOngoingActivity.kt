package org.terserah.ngaber

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class OrderOngoingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_ongoing)

        val originLat: Double = intent.getDoubleExtra("origin_loc_lat", 0.0)
        val originLong: Double = intent.getDoubleExtra("origin_loc_long", 0.0)


    }
}
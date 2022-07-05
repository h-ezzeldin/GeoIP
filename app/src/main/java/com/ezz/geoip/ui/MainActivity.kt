package com.ezz.geoip.ui

import android.content.Intent
import android.net.InetAddresses
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.ezz.geoip.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.locateButton.isEnabled = false
        b.locateButton.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            intent.putExtra("ip", b.ipInput.editText!!.text.toString())
            val p1: Pair<View, String> = Pair(b.locateButton, "button_trans")
            val p2: Pair<View, String> = Pair(b.ipInput, "input_trans")
            val options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this@MainActivity, p1, p2)
            startActivity(intent, options.toBundle())
        }

        b.ipInput.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val checker: Boolean
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
                    checker = InetAddresses.isNumericAddress(p0.toString())
                } else {
                    checker = Patterns.IP_ADDRESS.matcher(p0.toString()).matches()
                }
                if (checker) {
                    b.ipInput.error = null
                    b.locateButton.isEnabled = true
                } else {
                    b.ipInput.error = "Please enter a valid ip"
                    b.locateButton.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // 255.255.255.255
            }
        })
    }
}

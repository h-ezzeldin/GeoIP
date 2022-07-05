package com.ezz.geoip.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources.getSystem
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.ezz.geoip.R
import com.ezz.geoip.databinding.ActivityInfoBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class InfoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mapInitialized: Boolean = false
    private lateinit var b: ActivityInfoBinding
    private val mainVM by viewModels<MainViewModel>()

    private lateinit var ip: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityInfoBinding.inflate(layoutInflater)
        setContentView(b.root)
        mapInitialized = false
        ip = intent.getStringExtra("ip").toString()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mainVM.getIPInfo(ip)

        b.searchInput.editText!!.setText(ip)

        b.searchButton.setOnClickListener {
            mainVM.getIPInfo(b.searchInput.editText!!.text.toString())
        }

        b.searchInput.editText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length < 15 &&
                    p0.count { it == '.' } == 3 &&
                    !p0.endsWith('.') &&
                    !p0.startsWith('.') &&
                    !p0.contains("..")
                ) {
                    b.searchInput.error = null
                    b.searchButton.isEnabled = true
                } else {
                    b.searchInput.error = "Please enter a valid ip"
                    b.searchButton.isEnabled = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        initializeObservers()

    }

    private fun initializeObservers() {
        mainVM.infoLD.observe(this) {
            b.info = it
            moveCamera(LatLng(it.latitude, it.longitude))

            BottomSheetBehavior.from(b.standardBottomSheet).apply {

                // get heights of arrow View, ip TextView and location TextView + container padding
                val height = b.arrow.height + b.ip.height + b.location.height + 16.px
                Log.d("TAG", "onCreate:${b.arrow.height} + ${b.ip.height} + ${16.px}")
                peekHeight = height
                this.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapInitialized = true
        mMap.uiSettings.isCompassEnabled = false
        mMap.uiSettings.isMyLocationButtonEnabled = false
        mMap.uiSettings.isMapToolbarEnabled = false
        mMap.uiSettings.isZoomControlsEnabled = false
    }

    private fun moveCamera(location: LatLng) {
        if (mapInitialized) {
            val cameraPosition = CameraPosition.Builder()
                .target(location) // Sets the center of the map to Mountain View
                .zoom(15f)            // Sets the zoom
                .tilt(45f)            // Sets the tilt of the camera to 30 degrees
                .build()              // Creates a CameraPosition from the builder
            mMap.addMarker(MarkerOptions().position(location).title("Location Of IP"))
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    fun copyToClipboard(view: View) {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text", (view as TextView).text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_SHORT).show()
    }

    private val Int.px: Int get() = (this * getSystem().displayMetrics.density).toInt()
}
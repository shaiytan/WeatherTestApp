package slip.dev.weathertestapp.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import slip.dev.weathertestapp.R
import slip.dev.weathertestapp.model.Geopoint
import slip.dev.weathertestapp.viewmodel.LocationViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var viewModel: LocationViewModel
    private lateinit var selectedLocation: LiveData<Geopoint>
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        setSupportActionBar(toolbar)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        viewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        selectedLocation = viewModel.location
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        selectedLocation.observe(this, Observer { point ->
            if (point != null) {
                val current = LatLng(point.latitude, point.longitude)
                if (marker == null) {
                    marker = map.addMarker(MarkerOptions().title(point.city).position(current))
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 6.0F))
                } else marker?.apply {
                    position = current
                    title = point.city
                }
                title = point.city
            }
        })
        map_btn.setOnClickListener(this::onSubmit)
        map.setOnMapClickListener(this::onMapClick)
    }

    override fun onSupportNavigateUp(): Boolean {
        cancelSelection()
        return true
    }

    override fun onBackPressed() {
        cancelSelection()
    }

    private fun cancelSelection() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun onSubmit(view: View) {
        selectedLocation.value?.let { (latitude, longitude, city) ->
            val intent = Intent()
                    .putExtra("city", city)
                    .putExtra("lat", latitude)
                    .putExtra("lng", longitude)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun onMapClick(point: LatLng) {
        viewModel.updateLocation(point.latitude, point.longitude)
    }
}

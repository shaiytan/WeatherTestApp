package slip.dev.weathertestapp.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceSelectionListener
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment
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
    private lateinit var searchFragment: SupportPlaceAutocompleteFragment
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        selectedLocation = viewModel.location

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchFragment = supportFragmentManager
                .findFragmentById(R.id.place_pick) as SupportPlaceAutocompleteFragment
        searchFragment.setFilter(AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build())
        search_container.visibility = View.GONE
        searchFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place?) {
                if (place != null) {
                    val lat = place.latLng.latitude
                    val lng = place.latLng.longitude
                    viewModel.updateLocation(lat, lng)
                }
            }

            override fun onError(status: Status) {
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        selectedLocation.observe(this, Observer { point ->
            if (point != null) {
                val current = LatLng(point.latitude, point.longitude)
                if (marker == null) {
                    marker = map.addMarker(MarkerOptions().title(point.city).position(current))
                } else marker?.apply {
                    position = current
                    title = point.city
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 6.0F))
                searchFragment.setText(point.city)
                title = point.city
                search_container.visibility = View.GONE
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return if (item?.itemId == R.id.search_item) {
            if (search_container.visibility == View.VISIBLE)
                search_container.visibility = View.GONE
            else search_container.visibility = View.VISIBLE
            true
        } else super.onOptionsItemSelected(item)
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

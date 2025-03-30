package com.andriipedosenko.localstations.ui.screen.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.andriipedosenko.localstations.R
import com.andriipedosenko.localstations.databinding.ActivityMainBinding
import com.andriipedosenko.localstations.domain.model.Station
import com.andriipedosenko.localstations.ui.dialog.createMarker.CreateMarkerDialog
import com.andriipedosenko.localstations.ui.dialog.infoMarker.InfoMarkerDialog
import com.andriipedosenko.localstations.utils.visibleCoordinates
import com.andriipedosenko.localstations.utils.zoomLevel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraIdleListener,
    GoogleMap.OnInfoWindowCloseListener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    private var googleMap: GoogleMap? = null

    private var currentDynamicMarker: Marker? = null

    private var clusterManager: ClusterManager<Station>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        setupMapFragment()
        setupClickListeners()
    }

    override fun onDestroy() {
        super.onDestroy()

        googleMap = null
        clusterManager = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setupGoogleMap(googleMap)
        setupClusterManager(googleMap)
        setupViewModelCallback()
    }

    override fun onCameraIdle() {
        val visibleCoordinates = googleMap?.visibleCoordinates() ?: return
        val zoomLevel = googleMap?.zoomLevel() ?: return

        viewModel.getVisibleStations(
            visibleCoordinates = visibleCoordinates,
            zoomLevel = zoomLevel
        )
    }

    override fun onInfoWindowClose(marker: Marker) {
        disableClusterMarkerButtons()
    }

    private fun setupViews() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    private fun setupGoogleMap(googleMap: GoogleMap) = with(googleMap) {
        this@MainActivity.googleMap = googleMap

        setOnMapClickListener {
            placeMarker(it)
        }

        setOnCameraIdleListener(this@MainActivity)
        setOnInfoWindowCloseListener(this@MainActivity)

        setMinZoomPreference(4f)

        googleMap.moveCamera(CameraUpdateFactory.zoomTo(4f))

        uiSettings.isMapToolbarEnabled = false
    }

    private fun setupClickListeners() = with(binding) {
        addMarkerButton.setOnClickListener {
            val latLng = currentDynamicMarker?.position ?: return@setOnClickListener

            CreateMarkerDialog.show(
                latLng = latLng,
                fragmentManager = supportFragmentManager,
                onMarkerCreated = {
                    removeMarker()
                    onCameraIdle()
                }
            )
        }

        editMarkerButton.setOnClickListener {
            val station = viewModel.currentClusterStation.value ?: return@setOnClickListener

            InfoMarkerDialog.show(
                station = station,
                fragmentManager = supportFragmentManager,
                onMarkerUpdated = {
                    onCameraIdle()
                }
            )
        }

        deleteMarkerButton.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this@MainActivity)
                .setTitle(getString(R.string.delete_station_confirmation))
                .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                    viewModel.deleteStation()
                    dialog.dismiss()
                }
                .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                    dialog.dismiss()
                }


            val dialog = builder.create()
            dialog.show()
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun setupClusterManager(googleMap: GoogleMap) {
        clusterManager = ClusterManager(this, googleMap)

        this@MainActivity.googleMap?.setOnMarkerClickListener(clusterManager)

        clusterManager?.setOnClusterItemClickListener { station ->
            removeMarker()

            viewModel.setCurrentClusterStation(station)

            false
        }
    }

    private fun setupViewModelCallback() = with(viewModel) {
        currentClusterStation.observe(this@MainActivity) {
            if (it == null) {
                onCameraIdle()
                return@observe
            }

            enableClusterMarkerButtons()
        }

        stations.observe(this@MainActivity) {
            clusterManager?.clearItems()
            clusterManager?.addItems(it)
            clusterManager?.cluster()
        }
    }

    private fun placeMarker(coordinates: LatLng) {
        currentDynamicMarker?.remove()

        currentDynamicMarker = googleMap?.addMarker(
            MarkerOptions().position(coordinates)
        )

        binding.addMarkerButton.isEnabled = true
    }

    private fun removeMarker() {
        currentDynamicMarker?.remove()
        currentDynamicMarker = null

        binding.addMarkerButton.isEnabled = false
    }

    private fun enableClusterMarkerButtons() {
        binding.editMarkerButton.isEnabled = true
        binding.deleteMarkerButton.isEnabled = true
    }

    private fun disableClusterMarkerButtons() {
        binding.editMarkerButton.isEnabled = false
        binding.deleteMarkerButton.isEnabled = false
    }
}
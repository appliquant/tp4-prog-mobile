package com.example.tp3.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.tp3.MainActivity
import com.example.tp3.R
import com.example.tp3.databinding.FragmentMapBinding
import com.example.tp3.db.Message
import com.example.tp3.db.MessageApplication
import com.example.tp3.googlemap.MarkerInfoWindowAdapter
import com.example.tp3.viewmodel.MessageViewModel
import com.example.tp3.viewmodel.MessageViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.coroutines.launch


class MapFragment : Fragment(), EasyPermissions.PermissionCallbacks,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener,
    OnMapReadyCallback {

    // ============================================================================
    // Variables
    // ============================================================================
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var googleMap: GoogleMap
    private val messageViewModel: MessageViewModel by activityViewModels {
        MessageViewModelFactory(
            (activity?.application as MessageApplication).database.messageDao()
        )
    }

    // ============================================================================
    // Fonctions de cycle de vie
    // ============================================================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val fragmentBinding = FragmentMapBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup google map
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragment_google_map) as? SupportMapFragment
        // Attendre que la carte soit chargée
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            googleMap = mapFragment?.awaitMap()!!
            googleMap.awaitMapLoad()

            // Setup window
            googleMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))

            // Setup gestures
            googleMap.uiSettings.let {
                it.isZoomControlsEnabled = true
                it.isMyLocationButtonEnabled = true
            }

            // Setup map type
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

            // Demander la permission de localisation
            requireLocationPermissions()
        }

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Click listener top app bar
        binding.topAppBar.setNavigationOnClickListener {
            openDrawer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // ============================================================================
    // EasyPermissions
    // ============================================================================
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissons handler
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        // Vérifier si l'utilisateur a refusé la permission et a coché "NE JAMAIS DEMANDER À NOUVEAU"
        // Afficher un message pour l'inviter à activer la permission dans les paramètres de l'application
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext())
                .title(getString(R.string.permission_location))
                .build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // Si la permission est acceptée, charger les markers sur la carte
        loadMarkersToMap()
    }

    /**
     * Demander la permission de localisation
     * Si la permission est refusée, les markers ne seront pas chargés
     */
    @AfterPermissionGranted(REQUEST_CODE_LOCATION)
    private fun requireLocationPermissions() {
        if (EasyPermissions.hasPermissions(requireContext(), ACCESS_FINE_LOCATION)) {
            // Permissions déja acceptée
            loadMarkersToMap()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_location),
                REQUEST_CODE_LOCATION,
                ACCESS_FINE_LOCATION
            )
        }
    }

    // ============================================================================
    // Google Map
    // ============================================================================

    override fun onMyLocationClick(location: Location) {
//        Toast.makeText(requireContext(), "Current location:\n$location", Toast.LENGTH_LONG)
//            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
//        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT)
//            .show()

        return false
    }

    /**
     * Fonction appelée lorsque la carte est prête à être utilisée.
     */
    override fun onMapReady(mMap: GoogleMap?) {
        mMap?.setOnMyLocationClickListener(this)
        mMap?.setOnMyLocationButtonClickListener(this)

        // Click listener sur les markers
        mMap?.setOnMarkerClickListener { marker ->
            calculateDistance(marker.tag as Message)
            false
        }

        // Click listener long sur la carte
        mMap?.setOnMapLongClickListener {
            Toast.makeText(requireContext(), "Long click", Toast.LENGTH_SHORT).show()
            addMarker()
        }
    }

    /**
     * Loader les markers sur la carte google map.
     * Location doit être activée pour que cette fonction fonctionne.
     */
    @SuppressLint("MissingPermission")
    private fun loadMarkersToMap() {
        // Remplir la map des points longitude/latitude des messages dans la base de données
        viewLifecycleOwner.lifecycleScope.launch {

            messageViewModel.getAllMessages().collect { messages ->
                messages.forEach { message ->
                    val marker = googleMap.addMarker {
                        title(message.message)
                        position(LatLng(message.latitude, message.longitude))
                    }

                    marker.tag = message
                }

                // Activer bouton de localisation (location est active à ce point)
                googleMap.isMyLocationEnabled = true

                // Initialiser fusedLocation (pour localiser l'utilisateur)
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireContext())

                // Centrer la carte sur le premier message
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    null
                )
                    .addOnSuccessListener { userLocation ->
                        val latLng = LatLng(userLocation.latitude, userLocation.longitude)
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                    }

                // Callback
                onMapReady(googleMap)
            }
        }
    }


    /**
     * Calculer distance entre le marker et la position de l'utilisateur.
     * Si les markers sont sur la carte, la permission de localisation est déjà acceptée.
     */
    @SuppressLint("MissingPermission")
    private fun calculateDistance(message: Message) {
        fusedLocationClient.lastLocation.addOnSuccessListener { userLocation ->
            val messageLocation = Location("Marker")
            messageLocation.latitude = message.latitude
            messageLocation.longitude = message.longitude

            val distance = "${userLocation.distanceTo(messageLocation) / 1000}"

            // Message de confirmation
            activity?.let {
                Snackbar.make(
                    it.findViewById(android.R.id.content),
                    getString(R.string.distance_text, distance), Snackbar.LENGTH_LONG
                ).show()
            }

        }
    }

    /**
     * Ajouter un marker sur la carte.
     */
    private fun addMarker() {
        // Navigation vers le fragment d'ajout de message
        view?.findNavController()?.navigate(R.id.action_mapFragment_to_addMarkerFragment)
    }

    // ============================================================================
    // Autre
    // ============================================================================

    /**
     * Ouvre le drawer
     * */
    private fun openDrawer() {
        (activity as MainActivity?)?.openDrawer()
    }

    companion object {
        // Code de la permission de localisation de EasyPermissions
        private const val REQUEST_CODE_LOCATION = 50012
    }
}
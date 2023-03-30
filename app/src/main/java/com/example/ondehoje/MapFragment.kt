import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ondehoje.R
import com.example.ondehoje.dao.Event
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var mapState: CameraPosition? = null
    private lateinit var eventsReference: DatabaseReference
    private var events = arrayListOf<Event>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var currentLocationMarker: Marker? = null
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)
        eventsReference = FirebaseDatabase.getInstance().reference.child("events")

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        bottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        return rootView
    }
    override fun onResume() {
        super.onResume()

        bottomNavigationView.selectedItemId = R.id.navigation_compass
    }
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        addMyLocation()
        val event = arguments?.getParcelable<Event>("event")
        if(event == null){
            getEvents()

            // Restaurar estado do mapa, se existir
            if (mapState != null) {
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mapState!!))
            } else {
                // Mover a câmera para a localização
                val latLng = LatLng(-3.0926155783574143, -60.017682273201736)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                googleMap.moveCamera(cameraUpdate)
            }
        }else{
            // Mover a câmera para a localização
            val latLng = LatLng( event.latitude.toDouble(), event.longitude.toDouble())
            val markerOptions = MarkerOptions().position(latLng).title(event.name)
            googleMap.addMarker(markerOptions)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            googleMap.moveCamera(cameraUpdate)
        }



        // Adicionar listener para salvar estado do mapa
        googleMap.setOnCameraIdleListener {
            mapState = googleMap.cameraPosition
        }
    }

    fun getEvents(){
        eventsReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (d in dataSnapshot.children) {
                    val u = d.getValue(Event::class.java)
                    u?.let {
                        val latLng = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                        val markerOptions = MarkerOptions().position(latLng).title(it.name)
                        googleMap.addMarker(markerOptions)
                        events.add(it)
                    }

                }
            }

        })

    }
    fun addMyLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permissão concedida, atualiza a posição atual
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(location.latitude, location.longitude)
                    addCurrentLocationMarker(latLng)
                }
            }
        } else {
            // Solicita permissão do usuário para acessar a localização
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

    }

    private fun addCurrentLocationMarker(latLng: LatLng) {
        currentLocationMarker?.remove()
        currentLocationMarker = googleMap.addMarker(
            MarkerOptions().position(latLng).title("Você está aqui")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Salvar estado do mapa
        outState.putParcelable("mapState", mapState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        // Restaurar estado do mapa, se existir
        mapState = savedInstanceState?.getParcelable("mapState")
    }

}

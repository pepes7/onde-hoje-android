import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ondehoje.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var mapState: CameraPosition? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Adicionar marcador em uma localização
        val latLng = LatLng(-3.0926155783574143, -60.017682273201736)
        val markerOptions = MarkerOptions().position(latLng).title("Sydney")
        googleMap.addMarker(markerOptions)

        // Restaurar estado do mapa, se existir
        if (mapState != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(mapState!!))
        } else {
            // Mover a câmera para a localização
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15f)
            googleMap.moveCamera(cameraUpdate)
        }

        // Adicionar listener para salvar estado do mapa
        googleMap.setOnCameraIdleListener {
            mapState = googleMap.cameraPosition
        }
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

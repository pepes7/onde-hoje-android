package com.example.ondehoje

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.example.ondehoje.dao.Event
import com.example.ondehoje.helper.Permissao
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class CreateEventActivity : AppCompatActivity() {
    private lateinit var eventsReference: DatabaseReference
    private lateinit var storageReference: StorageReference

    private lateinit var editName: EditText
    private lateinit var editDate: EditText
    private lateinit var editDescription: EditText
    private lateinit var btnInsertPhoto: Button

    private lateinit var locationEditText: EditText
    private lateinit var resultsListView: ListView
    private lateinit var geocoder: Geocoder

    private var latitude = ""
    private var longitude = ""
    var category = ""
    private val SELECAO_GALERIA = 200
    private var imagem: Bitmap? = null
    private lateinit var imageEvent: ImageView
    private val permisssaoGaleria = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (resultCode == RESULT_OK) {
            try {
                when (requestCode) {
                    SELECAO_GALERIA -> {
                        val localImagemSelecionada = data?.data
                        imagem = MediaStore.Images.Media.getBitmap(
                            contentResolver,
                            localImagemSelecionada
                        )

                    }
                }
                if (imagem != null) {
                    //recuperar dados da imagem para o firebase
                    imageEvent.setImageBitmap(imagem)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        initial()
        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val btnCreate = findViewById<Button>(R.id.btn_create_event)
        eventsReference = FirebaseDatabase.getInstance().reference.child("events")
        btnBack.setOnClickListener {
            finish()
        }

        btnCreate.setOnClickListener {
            create()
        }

        resultsListView = findViewById(R.id.resultsListView)
        geocoder = Geocoder(this)

        // cria um objeto Handler na thread principal
        val handler = Handler(Looper.getMainLooper())

        // define um tempo de espera em milissegundos
        val DELAY_TIME_MS = 300L

        locationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Não faz nada aqui
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    // Realiza a pesquisa toda vez que o texto é alterado
                    val locationName = s.toString()

                    if (locationName.isNotBlank()) {
                        val stateBoundingBox =
                            LatLngBounds(LatLng(-9.822, -73.985), LatLng(4.481, -56.023))
                        val addresses = geocoder.getFromLocationName(
                            "$locationName", 10, stateBoundingBox.southwest.latitude,
                            stateBoundingBox.southwest.longitude,
                            stateBoundingBox.northeast.latitude,
                            stateBoundingBox.northeast.longitude
                        )

                        if (addresses.isNotEmpty()) {
                            val adapter = ArrayAdapter(baseContext,
                                android.R.layout.simple_list_item_1,
                                addresses.map { it.getAddressLine(0) })

                            resultsListView.adapter = adapter
                        }
                    }
                }, DELAY_TIME_MS)

            }

            override fun afterTextChanged(s: Editable?) {
                // Não faz nada aqui
            }
        })

        resultsListView.setOnItemClickListener { parent, view, position, id ->
            val selectedAddress = resultsListView.adapter.getItem(position) as String
            locationEditText.setText(selectedAddress)
            val addresses = geocoder.getFromLocationName(selectedAddress, 1)

            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                latitude = address.latitude.toString()
                longitude = address.longitude.toString()

            }
        }
    }

    fun initial() {
        editName = findViewById(R.id.edit_name_event)
        editDate = findViewById(R.id.edit_date_event)
        locationEditText = findViewById(R.id.edit_location_event)
        editDescription = findViewById(R.id.edit_description_event)
        storageReference = FirebaseStorage.getInstance().reference
        btnInsertPhoto = findViewById(R.id.btn_change_foto)
        imageEvent = findViewById(R.id.image_event)

        val itens = arrayOf("", "Festa", "Comida")
        val spinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, itens)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                category = selectedItem
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinner.adapter = adapter

        btnInsertPhoto.setOnClickListener {
            if (Permissao.validarPermissao(permisssaoGaleria, this, SELECAO_GALERIA)) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, SELECAO_GALERIA)
            }
        }
    }

    fun create() {
        val name = editName.text.toString()
        val date = editDate.text.toString()
        val location = locationEditText.text.toString()
        val description = editDescription.text.toString()

        var permit = true

        if (name.isEmpty()) {
            editName.error = "Campo obrigatório!"
            permit = false
        }

        if (date.isEmpty()) {
            editDate.error = "Campo obrigatório!"
            permit = false
        }

        if (location.isEmpty()) {
            locationEditText.error = "Campo obrigatório!"
            permit = false
        }

        if (description.isEmpty()) {
            editDescription.error = "Campo obrigatório!"
            permit = false
        }

        if (category.isEmpty()) {
            Toast.makeText(applicationContext, "Escolha o tipo do evento", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(this, "Procure seu endereco na barra de pesquisa", Toast.LENGTH_LONG)
                .show()
            permit = false
        }

        if (permit) {
            val pd = ProgressDialog(this)
            pd.setMessage("Criando evento")
            pd.show()

            val event = Event()
            event.name = name
            event.date = date
            event.location = location
            event.description = description
            event.category = category
            event.latitude = latitude
            event.longitude = longitude

            val reference =  eventsReference.push()
            reference.setValue(event).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (imagem != null) {
                        salvarFoto(reference)
                    } else {
                        finish()
                    }

                }
            }
        }
    }

    fun salvarFoto(ref: DatabaseReference) {
        //recuperar dados da imagem para o firebase
        val baos = ByteArrayOutputStream()

        imagem?.compress(Bitmap.CompressFormat.JPEG, 70, baos)

        val dadosImagem = baos.toByteArray()

        //Salvar no Firebase
        val imagemRef = storageReference
            .child("imagens")
            .child("events")
            .child(ref.key.toString())
            .child("event.jpeg")

        val uploadTask = imagemRef.putBytes(dadosImagem)
        uploadTask.addOnFailureListener {
            //Se houve erro no upload da imageFile
            Toast.makeText(this, "Erro ao salvar  a foto", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnSuccessListener {
            imagemRef.downloadUrl.addOnSuccessListener {
                ref.child("foto").setValue(it.toString())
            }
            //Se o upload da imageFile foi realizado com sucesso
            Toast.makeText(this, "Evento criado com sucesso!", Toast.LENGTH_SHORT)
                .show()
            finish()
        }
    }
}
package com.example.ondehoje

import MapFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ondehoje.dao.Event
import com.squareup.picasso.Picasso

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var photo: ImageView

    private lateinit var viewTitle: TextView
    private lateinit var viewLocation: TextView
    private lateinit var viewCategory: TextView
    private lateinit var viewDescription: TextView
    private lateinit var route: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val event = arguments?.getParcelable<Event>("event")
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        initial(view)
        route.setOnClickListener {
            showFragment(event)
        }
        showData(event)
        // Inflate the layout for this fragment
        return view
    }

    fun initial (view:View){
        photo = view.findViewById(R.id.imageView4)
        viewTitle = view.findViewById(R.id.txt_details_title)
        viewLocation = view.findViewById(R.id.txt_details_location)
        viewCategory = view.findViewById(R.id.txt_details_category)
        viewDescription = view.findViewById(R.id.txt_details_description)
        route = view.findViewById(R.id.btn_route)


    }

    fun showData(event: Event?){
        event?.let {
            if(it.foto.isNotEmpty()){
                Picasso.get()
                    .load(it.foto)
                    .into(photo)
            }
            viewTitle.text = it.name
            viewLocation.text = it.location
            viewCategory.text = it.category
            viewDescription.text = it.description
        }
    }

    fun showFragment(event: Event?) {
        val fragment = MapFragment()
        val bundle = Bundle()
        bundle.putParcelable("event", event)
        fragment.arguments = bundle

        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.viewPage, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}
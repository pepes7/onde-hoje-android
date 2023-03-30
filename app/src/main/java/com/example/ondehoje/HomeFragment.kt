package com.example.ondehoje

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ondehoje.adapter.EventAdapter
import com.example.ondehoje.dao.Event
import com.example.ondehoje.dao.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {
    private lateinit var eventAdapterTrending: EventAdapter
    private lateinit var eventAdapterNew: EventAdapter
    private lateinit var eventAdapterForYou: EventAdapter

    private lateinit var recyclerViewEventTrending: RecyclerView
    private lateinit var recyclerViewEventNew: RecyclerView
    private lateinit var recyclerViewEventForYou: RecyclerView

    private var eventsTrending = arrayListOf<Event>()
    private var eventsNew = arrayListOf<Event>()
    private var eventsForYou = arrayListOf<Event>()

    private lateinit var eventsReferenceTrending: DatabaseReference
    private lateinit var userReference: DatabaseReference

    private lateinit var eventsReferenceNew: Query
    private lateinit var eventsReferenceForYou: Query

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        initial(view)
        view.findViewById<ImageView>(R.id.logo_home).setOnClickListener {
            screenLogin()
        }
        setAdapters(view)

        getEventsTrending()
        getEventsNew()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userReference =
                FirebaseDatabase.getInstance().reference.child("usuarios").child(user.uid)
            getEventsForYou()
        }
        return view
    }


    fun initial(view: View) {
        recyclerViewEventTrending = view.findViewById(R.id.recycler_view_events_trending)
        recyclerViewEventNew = view.findViewById(R.id.recycler_view_events_new)
        recyclerViewEventForYou = view.findViewById(R.id.recycler_view_events_for_you)


        eventsReferenceTrending = FirebaseDatabase.getInstance().reference.child("events")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayDate = dateFormat.format(Date())
        eventsReferenceNew =
            FirebaseDatabase.getInstance().reference.child("events").orderByChild("date")
                .equalTo(todayDate.toString())
        eventsReferenceForYou = FirebaseDatabase.getInstance().reference.child("events")

        eventAdapterTrending = EventAdapter(view.context, eventsTrending)
        eventAdapterNew = EventAdapter(view.context, eventsNew)
        eventAdapterForYou = EventAdapter(view.context, eventsForYou)

    }

    fun setAdapters(view: View) {
        recyclerViewEventTrending.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewEventTrending.hasFixedSize()

        recyclerViewEventNew.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewEventNew.hasFixedSize()

        recyclerViewEventForYou.layoutManager =
            LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewEventForYou.hasFixedSize()

        recyclerViewEventTrending.adapter = eventAdapterTrending
        recyclerViewEventNew.adapter = eventAdapterNew
        recyclerViewEventForYou.adapter = eventAdapterForYou

    }


    fun screenLogin() {
        startActivity(Intent(context, LoginActivity::class.java))
    }

    fun getEventsTrending() {
        eventsReferenceTrending.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventsTrending.clear()
                for (d in dataSnapshot.children) {
                    val u = d.getValue(Event::class.java)
                    u?.let {
                        eventsTrending.add(it)
                    }
                }
                eventAdapterTrending.notifyDataSetChanged()
            }

        })
    }

    fun getEventsNew() {
        eventsReferenceNew.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                eventsNew.clear()
                for (d in dataSnapshot.children) {
                    val u = d.getValue(Event::class.java)
                    u?.let {
                        eventsNew.add(it)
                    }
                }
                eventAdapterNew.notifyDataSetChanged()
            }

        })
    }

    fun getEventsForYou() {
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                user?.let {
                    eventsReferenceForYou.orderByChild("category").equalTo(it.category)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                            }

                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                eventsForYou.clear()
                                for (d in dataSnapshot.children) {
                                    val u = d.getValue(Event::class.java)
                                    u?.let {
                                        if(u.turno == user.turno){
                                            eventsForYou.add(it)
                                        }
                                    }
                                }
                                eventAdapterForYou.notifyDataSetChanged()
                            }

                        })

                }

            }
        })

    }
}
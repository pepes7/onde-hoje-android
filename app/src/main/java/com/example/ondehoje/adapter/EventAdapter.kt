package com.example.ondehoje.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ondehoje.DetailsFragment
import com.example.ondehoje.R
import com.example.ondehoje.dao.Event
import com.squareup.picasso.Picasso


class EventAdapter(private val context: Context, private val listEvent: ArrayList<Event>) : RecyclerView.Adapter<EventAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_events,viewGroup,false)
        return MyViewHolder(view)

    }
    ;
    override fun getItemCount(): Int {
        return listEvent.size
    }

    override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
        val event = listEvent.get(i)
        //myViewHolder.nome.text = event.nome

        //metodo de click
        myViewHolder.itemView.setOnClickListener {
            showFragment(event)
        }

        myViewHolder.textView.text = event.name
        if(event.foto.isNotEmpty()){
            Picasso.get()
                .load(event?.foto)
                .into(myViewHolder.photo)
        }
    }

    fun showFragment(event: Event) {
        val fragment = DetailsFragment()
        val bundle = Bundle()
        bundle.putParcelable("event", event)
        fragment.arguments = bundle

        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        transaction.replace(R.id.viewPage, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    inner  class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var photo : ImageView
        var textView: TextView
        init {
            photo = itemView.findViewById(R.id.list_image_event)
            textView = itemView.findViewById(R.id.textView)
        }
    }
}


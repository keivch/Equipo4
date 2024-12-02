package com.example.project1.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.project1.R
import com.example.project1.model.Challenge

class ChallengeAdapter(
    var listaRetos: List<Challenge>,
    val onBorrarClick: (String) -> Unit,
    val onActualizarClick: (Challenge) -> Unit
): RecyclerView.Adapter<ChallengeAdapter.viewHolder>(){
    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cvReto: CardView = itemView.findViewById(R.id.recyclerViewRetos)
        val tvReto: TextView = itemView.findViewById(R.id.)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeAdapter.viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChallengeAdapter.viewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return listaRetos.size
    }

}
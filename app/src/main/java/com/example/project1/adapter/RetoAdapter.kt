package com.example.project1.adapter


import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
import com.example.project1.database.RetoDatabase
import com.example.project1.model.Reto

class RetoAdapter(
    private val retos: MutableList<Reto>,
    private val database: RetoDatabase,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<RetoAdapter.RetoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reto, parent, false)
        return RetoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RetoViewHolder, position: Int) {
        val reto = retos[position]
        holder.bind(reto)

        // Animación al hacer clic en editar/eliminar
        holder.editButton.setOnClickListener {
            ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).setDuration(100).start()
            // Lógica para editar el reto
        }

        holder.deleteButton.setOnClickListener {
            ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).setDuration(100).start()
            database.deleteReto(reto.id)
            onUpdate()
        }
    }

    override fun getItemCount(): Int = retos.size

    class RetoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val descriptionTextView: TextView = view.findViewById(R.id.reto_description)
        private val nombreTextView: TextView = view.findViewById(R.id.reto_nombre) // Asegúrate de tener este TextView en tu layout

        val editButton: ImageView = view.findViewById(R.id.icon_edit)
        val deleteButton: ImageView = view.findViewById(R.id.icon_delete)

        fun bind(reto: Reto) {
            descriptionTextView.text = reto.description
            nombreTextView.text = reto.nombre // Asegúrate de que este TextView esté definido en el layout
        }
    }

}

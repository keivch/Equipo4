package com.example.project1.adapter

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
import com.example.project1.Editar_reto
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

        // Animación al hacer clic en editar
        holder.editButton.setOnClickListener {
            ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).setDuration(100).start()
            // Lógica para editar el reto
            val context = holder.itemView.context
            val intent = Intent(context, Editar_reto::class.java).apply {
                putExtra("CHALLENGE_ID", reto.id)  // Pasar el ID del reto
                putExtra("CURRENT_DESCRIPTION", reto.description)  // Pasar la descripción actual
            }
            context.startActivity(intent)
        }

        // Animación al hacer clic en eliminar
        holder.deleteButton.setOnClickListener {
            ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).setDuration(100).start()
            showCustomDialog(position, holder.itemView.context)
        }
    }

    override fun getItemCount(): Int = retos.size

    class RetoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val descriptionTextView: TextView = view.findViewById(R.id.reto_description)
        private val nombreTextView: TextView = view.findViewById(R.id.reto_nombre)

        val editButton: ImageView = view.findViewById(R.id.icon_edit)
        val deleteButton: ImageView = view.findViewById(R.id.icon_delete)

        fun bind(reto: Reto) {
            descriptionTextView.text = reto.description
            nombreTextView.text = reto.nombre
        }
    }

    private fun showCustomDialog(position: Int, context: Context) {
        val reto = retos[position]
        val mensaje = reto.nombre

        // Inflar el diseño personalizado para el diálogo
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_eliminar_reto, null)

        // Crear el AlertDialog con el diseño personalizado
        val builder = AlertDialog.Builder(context)
            .setTitle("Desea eliminar el siguiente reto")
            .setView(dialogView)
            .setMessage(mensaje)
            .setPositiveButton("SI") { _, _ ->
                // Acción a realizar cuando se presiona "Aceptar"
                database.deleteReto(reto.id)
                actualizarLista()
                onUpdate()
                Toast.makeText(context, "Reto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss() // Cerrar el diálogo al presionar "Cancelar"
            }

        // Mostrar el diálogo
        builder.create().show()
    }

    fun actualizarLista() {
        retos.clear()
        retos.addAll(database.getAllRetos().reversed())
    }
}


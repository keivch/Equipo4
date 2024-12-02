package com.example.project1.adapter

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
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
            showEditDialog(position, holder.itemView.context)
        }

        // Animación al hacer clic en eliminar
        holder.deleteButton.setOnClickListener {
            ObjectAnimator.ofFloat(it, "alpha", 0f, 1f).setDuration(100).start()
            showCustomDialog(position, holder.itemView.context)
        }
    }


    override fun getItemCount(): Int = retos.size

    class RetoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nombreTextView: TextView = view.findViewById(R.id.reto_nombre)
        private val descriptionTextView: TextView = view.findViewById(R.id.reto_description)


        val editButton: ImageView = view.findViewById(R.id.icon_edit)
        val deleteButton: ImageView = view.findViewById(R.id.icon_delete)

        fun bind(reto: Reto) {
            nombreTextView.text = reto.nombre
            descriptionTextView.text = reto.description
        }
    }

    private fun showCustomDialog(position: Int, context: Context) {
        val reto = retos[position]
        val mensaje = reto.nombre

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_eliminar_reto, null)

        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        // Configurar el título y mensaje
        dialogView.findViewById<TextView>(R.id.dialog_title).text = "¿Desea eliminar el siguiente reto?"
        dialogView.findViewById<TextView>(R.id.dialog_message).text = mensaje

        // Configurar el botón NO
        dialogView.findViewById<TextView>(R.id.button_no).setOnClickListener {
            dialog.dismiss()
        }

        // Configurar el botón SI
        dialogView.findViewById<TextView>(R.id.button_yes).setOnClickListener {
            database.deleteReto(reto.id)
            actualizarLista()
            onUpdate()
            Toast.makeText(context, "Reto eliminado", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.show()
    }

    fun showEditDialog(position: Int, context: Context) {
        val reto = retos[position]

        // Inflar el diseño personalizado para el diálogo de edición
        val dialogView = LayoutInflater.from(context).inflate(R.layout.editar_reto, null)
        val editNombre = dialogView.findViewById<EditText>(R.id.edit_nombre)
        val editDescripcion = dialogView.findViewById<EditText>(R.id.edit_descripcion)

        // Setear los valores actuales en los campos de edición
        editNombre.setText(reto.nombre)
        editDescripcion.setText(reto.description)

        // Crear el AlertDialog
        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        // Configurar el botón CANCELAR
        dialogView.findViewById<TextView>(R.id.button_cancel).setOnClickListener {
            dialog.dismiss()
        }

        // Configurar el botón GUARDAR
        dialogView.findViewById<TextView>(R.id.button_save).setOnClickListener {
            val nuevoNombre = editNombre.text.toString()
            val nuevaDescripcion = editDescripcion.text.toString()

            if (nuevoNombre.isNotEmpty() && nuevaDescripcion.isNotEmpty()) {
                // Actualizar en la base de datos
                database.editReto(reto.id, nuevoNombre, nuevaDescripcion)
                actualizarLista()
                onUpdate()
                Toast.makeText(context, "Reto actualizado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Todos los campos deben estar completos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }





    fun actualizarLista() {
        retos.clear()
        retos.addAll(database.getAllRetos().reversed())
    }
}


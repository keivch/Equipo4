package com.example.project1.adapter


import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.R
import com.example.project1.RetosActivity
import com.example.project1.database.RetoDatabase
import com.example.project1.model.Reto

class RetoAdapter(
    private val retos: MutableList<Reto>,
    private val database: RetoDatabase,
    private val onUpdate: () -> Unit,
    //private val context: Context
) : RecyclerView.Adapter<RetoAdapter.RetoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val context = parent.context
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

            val context = holder.itemView.context
            showCustomDialog(position,context)


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


    private fun showCustomDialog(position: Int,context: Context) {
        val reto = retos[position]
        var mensaje=reto.nombre
        // Inflar el diseño personalizado para el diálogo
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_eliminar_reto, null)

        // Crear el AlertDialog con el diseño personalizado
        val builder = AlertDialog.Builder(context)
            .setTitle("Desea eliminar el siguiente reto")

            .setView(dialogView)

            // Configurar los botones
            .setPositiveButton("SI") { _, _ ->
                // Acción a realizar cuando se presiona "Aceptar"
                database.deleteReto(reto.id)
                actualizarLista()
                onUpdate()

                 Toast.makeText(context, "Reto eliminado", Toast.LENGTH_SHORT).show() }
            .setCancelable(false)
            .setNegativeButton("NO") { builder, _ ->
                // Cerrar el diálogo al presionar "Cancelar"
                builder.dismiss() }

        builder.setMessage(mensaje).toString()

        // Mostrar el diálogo
        builder.create()
        builder.show()  // Hacer visible la ventana emergente

    }

    fun actualizarLista() {
        retos.clear()
        retos.addAll(database.getAllRetos().reversed())
    }
}

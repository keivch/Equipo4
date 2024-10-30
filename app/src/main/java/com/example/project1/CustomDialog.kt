package com.example.project1

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class CustomDialog(context: Context) : Dialog(context) {


    // Configuracion el título
    override fun setTitle(title: CharSequence?) {
        //Eliminacion del Fondo por defecto del dialog
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        val titleTextView = findViewById<TextView>(R.id.textViewTitle)
        titleTextView.text = title
    }

    // Configuracion del mensaje
    fun setMessage(message: CharSequence?) {
        val messageTextView = findViewById<TextView>(R.id.textViewChallenge)
        messageTextView.text = message
    }

    fun setPokemonImage(imageUrl: String) {
        val imageView = findViewById<ImageView>(R.id.imageViewPokemon)
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)
    }

    init {
        setContentView(R.layout.dialog_reto)

        // Configuracion del boton
        val closeButton = findViewById<Button>(R.id.buttonClose)
        closeButton.setOnClickListener {
            // Acción del botón cerrar
            this.dismiss()
        }
    }
}
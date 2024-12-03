package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.adapter.ChallengeAdapter
import com.example.project1.databinding.ActivityRetosBinding
import com.example.project1.model.Challenge
import com.example.project1.model.Reto
import com.example.project1.viewModel.ChallengeViewModel
import com.google.firebase.auth.FirebaseAuth

class ChallengeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRetosBinding
    private lateinit var adapter: ChallengeAdapter
    private lateinit var viewModel: ChallengeViewModel

    var retoEdit = Challenge()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChallengeViewModel::class.java]

        viewModel.listaRetos.observe(this){ retos ->
            setupRecyclerView(retos)
        }

        binding.btnAddReto.setOnClickListener{
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_reto, null)

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Añadir Reto")
                .setPositiveButton("Guardar",null)
                .setNegativeButton("cancelar") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()

            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                val nombre = dialogView.findViewById<EditText>(R.id.editTextNombre).text.toString().trim()
                val description = dialogView.findViewById<EditText>(R.id.editTextDescripcion).text.toString().trim()

                if (nombre.isEmpty() || description.isEmpty()) {
                    // Muestra un mensaje de error si los campos están vacíos
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    // Crea un nuevo reto
                    val reto = Challenge(name = nombre, description = description)

                    viewModel.agregarReto(reto)



                    // Cierra el diálogo
                    dialog.dismiss()
                }
            }
        }

    }

    fun setupRecyclerView(listaRetos: List<Challenge>){
        adapter = ChallengeAdapter(listaRetos, ::borrarReto, ::actualizarReto)
        binding.recyclerViewRetos.adapter = adapter

    }

    fun borrarReto(id: String){
        // Infla el diseño del diálogo personalizado
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_eliminar_reto, null)

        // Configura el contenido del diálogo

        val buttonNo = dialogView.findViewById<Button>(R.id.button_no)
        val buttonYes = dialogView.findViewById<Button>(R.id.button_yes)

        // Crea el AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Muestra el diálogo
        dialog.show()

        // Configura el botón "NO"
        buttonNo.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
        }

        // Configura el botón "SI"
        buttonYes.setOnClickListener {

            viewModel.borrarReto(id)
            dialog.dismiss() // Cierra el diálogo
        }
    }

    fun actualizarReto(reto: Challenge) {
        // Infla el diseño del diálogo personalizado
        val dialogView = LayoutInflater.from(this).inflate(R.layout.editar_reto, null)

        // Configura las vistas del diálogo
        val editNombre = dialogView.findViewById<EditText>(R.id.edit_nombre)
        val editDescripcion = dialogView.findViewById<EditText>(R.id.edit_descripcion)
        val buttonCancel = dialogView.findViewById<Button>(R.id.button_cancel)
        val buttonSave = dialogView.findViewById<Button>(R.id.button_save)

        // Precarga los datos actuales del reto en los campos de texto
        editNombre.setText(reto.name)
        editDescripcion.setText(reto.description)

        // Crea el AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Impide que se cierre el diálogo tocando fuera de él
            .create()

        // Mostrar el diálogo
        dialog.show()

        // Manejo del botón "CANCELAR"
        buttonCancel.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo sin realizar cambios
        }

        // Manejo del botón "GUARDAR"
        buttonSave.setOnClickListener {
            val nuevoNombre = editNombre.text.toString().trim()
            val nuevaDescripcion = editDescripcion.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (nuevoNombre.isEmpty() || nuevaDescripcion.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Actualiza el reto con los nuevos valores
                reto.name = nuevoNombre
                reto.description = nuevaDescripcion

                // Notifica al adaptador que el ítem ha cambiado
                adapter.notifyDataSetChanged()

                dialog.dismiss() // Cierra el diálogo después de guardar
            }
        }
    }


}

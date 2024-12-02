package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.model.Reto
import com.example.project1.viewModel.ChallengeViewModel
import com.google.firebase.auth.FirebaseAuth

class ChallengeActivity : AppCompatActivity() {

    private lateinit var viewModel: ChallengeViewModel
    private var userId: String? = null // Reemplaza con el ID del usuario autenticado

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retos)

        // Inicializa el ViewModel
        viewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)

        // Obtén el usuario autenticado
        val currentUser = FirebaseAuth.getInstance().currentUser
        userId = currentUser?.uid // El UID es el ID único del usuario autenticado

        if (userId == null) {
            // Si no hay usuario autenticado, puedes redirigir a la pantalla de login
            // o mostrar un mensaje de error
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            // Opcionalmente redirigir al usuario a la pantalla de login
            return
        }

        // Cargar los retos del usuario
        viewModel.loadChallenges(userId!!)

        // Observa los retos para actualizar la UI
        viewModel.challenges.observe(this) { challenges ->
            challenges?.let { updateUI(it) }
        }

        // Botón para agregar un reto
        findViewById<Button>(R.id.btnAddReto).setOnClickListener {
            val challenge = Reto(
                name = "Nuevo reto",
                description = "Descripción del reto"
            )
            viewModel.saveChallenge(userId!!, challenge) { success ->
                if (success) Toast.makeText(this, "Reto guardado", Toast.LENGTH_SHORT).show()
                else Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(challenges: List<Reto>) {
        // Actualiza tu RecyclerView con la lista de retos
    }

    private fun showEditDialog(challenge: Reto) {
        // Crea y muestra el diálogo para editar el reto
        val dialogView = layoutInflater.inflate(R.layout.editar_reto, null)
        val editNombre = dialogView.findViewById<EditText>(R.id.edit_nombre)
        val editDescripcion = dialogView.findViewById<EditText>(R.id.edit_descripcion)

        editNombre.setText(challenge.name)
        editDescripcion.setText(challenge.description)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Editar Reto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { dialog, _ ->
                // Actualizar reto en la base de datos
                val updatedChallenge = challenge.copy(
                    name = editNombre.text.toString(),
                    description = editDescripcion.text.toString()
                )
                viewModel.saveChallenge(userId!!, updatedChallenge) { success ->
                    if (success) {
                        Toast.makeText(this, "Reto actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}

package com.example.project1

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.project1.database.RetoDatabase
import com.example.project1.model.Reto

class Editar_reto : AppCompatActivity() {

    private lateinit var database: RetoDatabase  // Inicializar la base de datos SQLite
    private var challengeId: Int = 0  // Almacenar el ID del reto a editar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Asume que existe una actividad principal

        // Inicializar la base de datos SQLite
        database = RetoDatabase(this)

        // Aquí puedes obtener el ID del reto a editar, por ejemplo, de un Intent
        // challengeId = intent.getIntExtra("CHALLENGE_ID", 0)

        // Obtener la descripción actual del reto (puedes cargarla de la base de datos usando challengeId)
        val currentDescription = "Descripción del reto actual"  // Cambia esto por la descripción real
        showEditChallengeDialog(currentDescription)
    }

    @SuppressLint("MissingInflatedId")
    private fun showEditChallengeDialog(currentDescription: String) {
        // Crear el diálogo personalizado
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(this).inflate(R.layout.editar_reto, null)
        dialog.setContentView(view)

        // Configurar el título
        val title = view.findViewById<TextView>(R.id.dialogTitle)
        title.text = "Editar reto"

        // Configurar el campo de texto con la descripción del reto actual
        val descriptionEditText = view.findViewById<EditText>(R.id.editChallengeDescription)
        descriptionEditText.setText(currentDescription)

        // Configurar el botón "Cancelar"
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()  // Cierra el diálogo y regresa a la pantalla anterior
        }

        // Configurar el botón "Guardar"
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val newDescription = descriptionEditText.text.toString()
            saveChallengeToDatabase(newDescription)  // Guardar el reto en la base de datos SQLite
            dialog.dismiss()  // Cierra el diálogo
            // Aquí puedes actualizar la vista de la lista de retos si es necesario
        }

        // Evitar que el cuadro de diálogo se cierre al hacer clic fuera de él
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun saveChallengeToDatabase(description: String) {
        // Guardar el reto en SQLite
        val contentValues = ContentValues().apply {
            put("description", description)
        }

        // Actualizar la descripción en la base de datos
        database.writableDatabase.update("retos", contentValues, "id=?", arrayOf(challengeId.toString()))
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()  // Cerrar la base de datos para liberar recursos
    }
}


package com.example.project1

import android.app.Dialog
import android.content.ContentValues
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

    private lateinit var database: RetoDatabase
    private var challengeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = RetoDatabase(this)

        // Obtener el ID del reto a editar y su descripción de la base de datos
        challengeId = intent.getIntExtra("CHALLENGE_ID", 0)
        val currentDescription = database.getRetoById(challengeId)  // Función para obtener la descripción

        showEditChallengeDialog(currentDescription)
    }

    private fun showEditChallengeDialog(currentDescription: String) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = LayoutInflater.from(this).inflate(R.layout.editar_reto, null)
        dialog.setContentView(view)

        val title = view.findViewById<TextView>(R.id.dialogTitle)
        title.text = "Editar reto"

        val descriptionEditText = view.findViewById<EditText>(R.id.editChallengeDescription)
        descriptionEditText.setText(currentDescription)

        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        val btnSave = view.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val newDescription = descriptionEditText.text.toString()
            saveChallengeToDatabase(newDescription)
            dialog.dismiss()
            updateChallengeList()  // Llama a la función para actualizar la lista en la pantalla anterior
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun saveChallengeToDatabase(description: String) {
        val contentValues = ContentValues().apply {
            put("description", description)
        }
        database.writableDatabase.update("retos", contentValues, "id=?", arrayOf(challengeId.toString()))
    }

    private fun updateChallengeList() {
        // Envía una señal a la actividad anterior para recargar la lista
        setResult(RESULT_OK)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close()
    }
}


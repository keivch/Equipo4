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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.database.RetoDatabase
import com.example.project1.model.Reto
import com.example.project1.adapter.RetoAdapter

class RetosActivity : AppCompatActivity() {
    private lateinit var retoAdapter: RetoAdapter
    private lateinit var retoDatabase: RetoDatabase
    private var retosList = mutableListOf<Reto>()
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerViewRetos: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retos)

        toolbar = findViewById(R.id.toolbar)
        recyclerViewRetos = findViewById(R.id.recyclerViewRetos)

        val buttonAgregar: Button = findViewById(R.id.btnAddReto) // Cambia esto por el ID correcto de tu botón
        buttonAgregar.setOnClickListener {
            mostrarDialogAgregarReto()
        }


        // Configura la toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Retos"


        // Configuración de la Toolbar para navegación
        toolbar.setNavigationOnClickListener {
            onBackPressed() // Esto es opcional si deseas manejar el retroceso con el dispatcher
        }

        // Configuración de la base de datos y del RecyclerView
        try {
            retoDatabase = RetoDatabase(this)
            retosList = retoDatabase.getAllRetos().toMutableList()
            retosList.reverse() // Orden inverso para mostrar el más reciente en la parte superior

            retoAdapter = RetoAdapter(retosList, retoDatabase) { actualizarLista() }
            recyclerViewRetos.layoutManager = LinearLayoutManager(this)
            recyclerViewRetos.adapter = retoAdapter
        } catch (e: Exception) {
            Log.e("RetosActivity", "Error al inicializar la base de datos o adaptador", e)
        }

        // Registrar el callback para el botón de retroceso
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Maneja el evento de retroceso aquí
                finish() // Cierra la actividad
            }
        })
    }

    fun actualizarLista() {
        retosList.clear()
        retosList.addAll(retoDatabase.getAllRetos().reversed())
        Log.d("RetosActivity", "Retos obtenidos: ${retosList.size}")
        retoAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
    }


    private fun agregarReto(nombre: String, descripcion: String) {
        if (nombre.isNotEmpty() && descripcion.isNotEmpty()) {
            retoDatabase.addReto(nombre, descripcion) // Agrega el reto a la base de datos
            Log.d("RetosActivity", "Reto agregado: $nombre - $descripcion") // Mensaje en el log
            Toast.makeText(this, "Reto agregado", Toast.LENGTH_SHORT).show()
            actualizarLista() // Actualiza la lista de retos
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }
    private fun mostrarDialogAgregarReto() {
        // Inflar el layout del dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_agregar_reto, null)

        // Inicializar los EditText
        val editTextNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
        val editTextDescripcion = dialogView.findViewById<EditText>(R.id.editTextDescripcion)

        // Crear el dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar Reto")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = editTextNombre.text.toString()
                val descripcion = editTextDescripcion.text.toString()
                // Llamar a la función para agregar el reto
                agregarReto(nombre, descripcion)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }
}
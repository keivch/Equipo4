package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project1.adapter.ChallengeAdapter
import com.example.project1.databinding.ActivityRetosBinding
import com.example.project1.model.Challenge
import com.example.project1.view.LoginView
import com.example.project1.viewModel.ChallengeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.logging.HttpLoggingInterceptor

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

        val toolbar = findViewById<Toolbar>(R.id.toolbarRetos)

        setSupportActionBar(toolbar)

        toolbar.setNavigationOnClickListener {
            // Regresar al home y restaurar audio
            viewModel.onBackPressed()

            // Cierra la sesión del usuario
            FirebaseAuth.getInstance().signOut()

            // Inicia la actividad principal y limpia la pila de retroceso
            val intent = Intent(this, LoginView::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }



        /*binding.btnAddReto.setOnClickListener{
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

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
                setBackgroundColor(ContextCompat.getColor(this@ChallengeActivity, R.color.orange))
                setTextColor(ContextCompat.getColor(this@ChallengeActivity, R.color.white))

            }

            // Personalizar el botón "Cancelar"
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
                setBackgroundColor(ContextCompat.getColor(this@ChallengeActivity, R.color.orange))
                setTextColor(ContextCompat.getColor(this@ChallengeActivity, R.color.white))
            }

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                val nombre = dialogView.findViewById<EditText>(R.id.editTextNombre).text.toString().trim()
                val description = dialogView.findViewById<EditText>(R.id.editTextDescripcion).text.toString().trim()

                if (nombre.isEmpty() || description.isEmpty()) {
                    // Muestra un mensaje de error si los campos están vacíos
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    // Crea un nuevo reto
                    val userId = FirebaseAuth.getInstance().currentUser?.uid

                    val reto = Challenge(name = nombre, description = description, userId = userId)
                    viewModel.agregarReto(reto)
                    dialog.dismiss()
                }
            }
        }*/



        binding.btnAddReto.setOnClickListener {
            // Crear el contenedor principal del diálogo
            val dialogLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT) }




            val editTextDescripcion = EditText(this).apply {
                hint = "Descripción del reto"
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 16 // Espaciado entre los EditTexts
                }
            }

            // Agregar los EditTexts al contenedor principal

            dialogLayout.addView(editTextDescripcion)

            // Crear el contenedor para los botones
            val buttonContainer = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 32 // Espaciado entre los EditTexts y los botones
                }
            }

            // Crear el botón "Cancelar"
            val btnCancelar = Button(this).apply {
                text = "Cancelar"
                setTextColor(ContextCompat.getColor(this@ChallengeActivity, R.color.white))
                backgroundTintList = ContextCompat.getColorStateList(this@ChallengeActivity, R.color.orange)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16 // Espaciado entre los botones
                }
            }

            // Crear el botón "Guardar"
            val btnGuardar = Button(this).apply {
                text = "Guardar"
                setTextColor(ContextCompat.getColor(this@ChallengeActivity, R.color.white))
                backgroundTintList = ContextCompat.getColorStateList(this@ChallengeActivity, R.color.dark_gray) // Fondo gris inicialmente
                isEnabled = false // Inicialmente deshabilitado
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Agregar los botones al contenedor de botones
            buttonContainer.addView(btnCancelar)
            buttonContainer.addView(btnGuardar)

            // Agregar el contenedor de botones al diálogo
            dialogLayout.addView(buttonContainer)

            // Crear el diálogo
            val dialog = AlertDialog.Builder(this)
                .setView(dialogLayout)
                .create()

            dialog.show()

            // Configurar acciones para los botones
            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            editTextDescripcion.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Habilitar el botón "Guardar" solo si el campo de descripción no está vacío
                    if (!s.isNullOrEmpty()) {
                        btnGuardar.isEnabled = true
                        btnGuardar.backgroundTintList = ContextCompat.getColorStateList(this@ChallengeActivity, R.color.orange) // Fondo naranja
                    } else {
                        btnGuardar.isEnabled = false
                        btnGuardar.backgroundTintList = ContextCompat.getColorStateList(this@ChallengeActivity, R.color.dark_gray) // Fondo gris
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })


            btnGuardar.setOnClickListener {

                val descripcion = editTextDescripcion.text.toString().trim()

                if ( descripcion.isEmpty()) {
                    Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
                } else {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val reto = Challenge( description = descripcion, userId = userId)
                    viewModel.agregarReto(reto)
                    dialog.dismiss()
                }
            }
        }





    }

    fun setupRecyclerView(listaRetos: List<Challenge>) {
        // Configurar un LayoutManager (por ejemplo, LinearLayoutManager)
        if (binding.recyclerViewRetos.layoutManager == null) {
            binding.recyclerViewRetos.layoutManager = LinearLayoutManager(this)
        }

        adapter = ChallengeAdapter(listaRetos, ::borrarReto, ::actualizarReto)
        binding.recyclerViewRetos.adapter = adapter
    }

    fun borrarReto(id: String){
        // Infla el diseño del diálogo personalizado
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_eliminar_reto, null)

        // Configura el contenido del diálogo

        val buttonNo = dialogView.findViewById<TextView>(R.id.button_no)
        val buttonYes = dialogView.findViewById<TextView>(R.id.button_yes)


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

        val editDescripcion = dialogView.findViewById<EditText>(R.id.edit_descripcion)
        val buttonCancel = dialogView.findViewById<TextView>(R.id.button_cancel)
        val buttonSave = dialogView.findViewById<TextView>(R.id.button_save)

        // Precarga los datos actuales del reto en los campos de texto

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

            val nuevaDescripcion = editDescripcion.text.toString().trim()

            // Validar que los campos no estén vacíos
            if (nuevaDescripcion.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                // Actualiza el reto localmente

                reto.description = nuevaDescripcion

                viewModel.editarReto(reto)
                dialog.dismiss()
            }
        }
    }



}

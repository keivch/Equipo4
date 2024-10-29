package com.example.project1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.app.AlertDialog
import com.example.project1.R
import com.example.project1.database.RetoDatabase
import kotlin.random.Random

class Ventana_home_principal : AppCompatActivity() {

    private lateinit var bottleImage: ImageView
    private lateinit var countdownText: TextView
    private lateinit var btnParpa: View
    private var mediaPlayer: MediaPlayer? = null
    private var bottleSpinSound: MediaPlayer? = null
    private var lastRotation = 0f  // Guardar el ángulo de la última posición

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_home_principal)

        // Incluir el fragmento de la toolbar
        supportFragmentManager.beginTransaction()
            .replace(R.id.toolbar_container, ToolbarFragment())
            .commit()

        // Inicializar vistas
        bottleImage = findViewById(R.id.botellaImg)
        countdownText = findViewById(R.id.countdown_text)
        btnParpa = findViewById<AppCompatButton>(R.id.btnParpadeante)

        // Configurar animación de parpadeo en el botón
        val blinkAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.animacion_circulo_parpadeante)
        btnParpa.startAnimation(blinkAnimation)

        // Configuración del sonido de fondo
        mediaPlayer = MediaPlayer.create(this, R.raw.musica_espera)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        // Configuración del sonido de la botella girando
        bottleSpinSound = MediaPlayer.create(this, R.raw.spinig_bottle)

        // Configuración del botón para iniciar el giro
        btnParpa.setOnClickListener {
            startBottleSpin()
        }

        // Configuración inicial del temporizador de cuenta regresiva
        startCountdownLoop()
    }

    private fun startBottleSpin() {
        // Desactivar el botón mientras la botella gira y ocultarlo temporalmente
        btnParpa.visibility = View.INVISIBLE

        // Pausar el sonido de fondo y comenzar el sonido de la botella girando
        mediaPlayer?.pause()
        bottleSpinSound?.start()

        // Generar una rotación hacia la derecha y aplicar al ImageView de la botella
        val rotationAmount = Random.nextInt(360, 1080) // Valor positivo para girar a la derecha
        val newRotation = lastRotation + rotationAmount
        bottleImage.animate()
            .rotation(newRotation.toFloat())
            .setDuration(2000)  // Duración del giro a 2 segundos
            .withEndAction {
                lastRotation = newRotation.toFloat() % 360  // Guardar la última rotación en grados
                bottleSpinSound?.pause()  // Pausar el sonido cuando la botella deja de girar

                // Iniciar la cuenta regresiva cuando la botella se detiene
                startCountdown()
            }
            .start()
    }

    private fun startCountdown() {
        countdownText.visibility = View.VISIBLE
        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                countdownText.text = "0"
                showRandomChallenge()  // Mostrar el reto cuando la cuenta llegue a 0
            }
        }.start()
    }

    private fun startCountdownLoop() {
        // Iniciar un temporizador en bucle
        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Actualizar el texto con los segundos restantes
                countdownText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                countdownText.text = "0"
                this.start()  // Reiniciar el temporizador
            }
        }.start()
    }

    private fun showRandomChallenge() {
        countdownText.visibility = View.GONE

        // Obtener todos los retos de la base de datos
        val db = RetoDatabase(this)
        val retos = db.getAllRetos()

        // Verificar si hay retos disponibles
        if (retos.isNotEmpty()) {
            // Elegir un reto aleatorio
            val randomReto = retos[Random.nextInt(retos.size)]

            // Crear un cuadro de diálogo emergente con el reto
            AlertDialog.Builder(this)
                .setTitle("¡Reto!")
                .setMessage(randomReto.description + " \nDescripcion: " + randomReto.nombre) // Usar la descripción del reto aleatorio
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    btnParpa.visibility = View.VISIBLE // Mostrar el botón nuevamente
                    mediaPlayer?.start() // Reanudar el sonido de fondo
                }
                .setCancelable(false)
                .show()
        } else {
            // Si no hay retos, mostrar un mensaje
            AlertDialog.Builder(this)
                .setTitle("¡Sin retos!")
                .setMessage("No hay retos disponibles en la base de datos.")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    btnParpa.visibility = View.VISIBLE // Mostrar el botón nuevamente
                    mediaPlayer?.start() // Reanudar el sonido de fondo
                }
                .setCancelable(false)
                .show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Liberar los recursos del MediaPlayer al destruir la actividad
        mediaPlayer?.release()
        bottleSpinSound?.release()
    }
}


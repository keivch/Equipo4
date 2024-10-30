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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.project1.R
import com.example.project1.database.RetoDatabase
import kotlin.random.Random

class Ventana_home_principal : AppCompatActivity() {

    private lateinit var bottleImage: ImageView
    private lateinit var countdownText: TextView
    private lateinit var btnParpa: View
    private var mediaPlayer: MediaPlayer? = null
    private var bottleSpinSound: MediaPlayer? = null
    private var lastRotation = 0f
    private var countdownTimer: CountDownTimer? = null  // Variable para controlar el temporizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ventana_home_principal)

        supportFragmentManager.beginTransaction()
            .replace(R.id.toolbar_container, ToolbarFragment())
            .commit()

        bottleImage = findViewById(R.id.botellaImg)
        countdownText = findViewById(R.id.countdown_text)
        btnParpa = findViewById<AppCompatButton>(R.id.btnParpadeante)

        val blinkAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.animacion_circulo_parpadeante)
        btnParpa.startAnimation(blinkAnimation)



        bottleSpinSound = MediaPlayer.create(this, R.raw.spinig_bottle)

        btnParpa.setOnClickListener {
            startBottleSpin()
        }

        // Configurar el temporizador inicial para la cuenta regresiva solo una vez al iniciar la actividad

    }

    private fun startBottleSpin() {
        btnParpa.visibility = View.INVISIBLE

        mediaPlayer?.pause()
        bottleSpinSound?.start()

        val baseSpin = 3600f // Ángulo base para asegurar que siempre haya múltiples giros completos

        val rotationAmount = baseSpin + (0..360).random()
        val newRotation = lastRotation + rotationAmount
        bottleImage.animate()
            .rotation(newRotation.toFloat())
            .setDuration(1000)
        bottleImage.rotation = lastRotation
        bottleImage.animate().rotation(newRotation).setDuration(3000).withEndAction {
        // Guardar el ángulo actual al terminar el giro en un rango de 0 a 360°
        }
            .withEndAction {
                lastRotation = newRotation.toFloat() % 360
                bottleSpinSound?.pause()

                // Iniciar la cuenta regresiva después de que la botella se detiene
                startCountdown()
            }
            .start()
    }

    private fun startCountdown() {
        countdownText.visibility = View.VISIBLE

        // Cancelar cualquier temporizador existente antes de crear uno nuevo
        countdownTimer?.cancel()

        countdownTimer = object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdownText.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                countdownText.text = "0"
                showRandomChallenge()
                btnParpa.visibility = View.VISIBLE
                mediaPlayer?.start()  // Reanudar la música de fondo
            }
        }
        countdownTimer?.start()
    }

    private fun showRandomChallenge() {
        countdownText.visibility = View.GONE

        val db = RetoDatabase(this)
        val retos = db.getAllRetos()

        if (retos.isNotEmpty()) {
            val randomReto = retos[Random.nextInt(retos.size)]

            AlertDialog.Builder(this)
                .setTitle("¡Reto!")
                .setMessage("${randomReto.nombre} \nDescripción: ${randomReto.description}")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    btnParpa.visibility = View.VISIBLE
                    mediaPlayer?.start()
                }
                .setCancelable(false)
                .show()
        } else {
            AlertDialog.Builder(this)
                .setTitle("¡Sin retos!")
                .setMessage("No hay retos disponibles en la base de datos.")
                .setPositiveButton("Aceptar") { dialog, _ ->
                    dialog.dismiss()
                    btnParpa.visibility = View.VISIBLE
                    mediaPlayer?.start()
                }
                .setCancelable(false)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        bottleSpinSound?.release()
        countdownTimer?.cancel()  // Cancelar el temporizador al destruir la actividad
    }
}

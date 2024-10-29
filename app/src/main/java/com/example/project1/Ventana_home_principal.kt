package com.example.project1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.project1.R
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
        bottleSpinSound = MediaPlayer.create(this, R.raw.Spining_Bottle)

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

        // Generar una rotación aleatoria y aplicar al ImageView de la botella
        val newRotation = lastRotation + Random.nextInt(360, 1080)
        bottleImage.animate()
            .rotation(newRotation.toFloat())
            .setDuration(3000)  // Duración del giro
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
                countdownText.text = ((millisUntilFinished / 1000)).toString()
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

        // Aquí podrías abrir un cuadro de diálogo o realizar la acción deseada
        // Por ahora, simplemente muestra el botón nuevamente para otro giro
        btnParpa.visibility = View.VISIBLE

        // Reanudar el sonido de fondo
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberar los recursos del MediaPlayer al destruir la actividad
        mediaPlayer?.release()
        bottleSpinSound?.release()
    }
}

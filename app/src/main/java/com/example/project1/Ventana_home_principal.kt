package com.example.project1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.example.project1.R


class Ventana_home_principal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ventana_home_principal)

        val btnParpa = findViewById<AppCompatButton>(R.id.btnParpadeante)
        val animar: Animation = AnimationUtils.loadAnimation(this,R.anim.animacion_circulo_parpadeante)
        btnParpa.startAnimation(animar)
        val countDown_text=findViewById<TextView>(R.id.countdown_text)


        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Actualiza el texto del TextView con los segundos restantes
                countDown_text.text = (millisUntilFinished / 1000).toString() }

            override fun onFinish() {
                // Muestra "0" cuando termina la cuenta regresiva
                countDown_text.text = "0"

                // Reinicia el temporizador
                this.start()  // Reinicia el temporizador desde el comienzo
            }
        }.start()




        val mediaPlayer = MediaPlayer.create(this, R.raw.musica_espera)

        // Reproducir el audio de fondo en bucle
        mediaPlayer.isLooping = true
        mediaPlayer.start()

    }

    /**
    override fun onDestroy() {
    super.onDestroy()
    // Liberar los recursos del MediaPlayer cuando la actividad se destruya
    if (::mediaPlayer.isInitialized) {
    mediaPlayer.stop()
    mediaPlayer.release()
    }

    }**/









}
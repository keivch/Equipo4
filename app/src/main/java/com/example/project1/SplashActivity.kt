package com.example.project1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.view.animation.AnimationUtils

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Manejo del botón de atrás con OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity() // Cierra la actividad de splash y todas las actividades asociadas
            }
        })

        // Iniciar la animación de la botella si es una AnimationDrawable
        val bottleImageView = findViewById<ImageView>(R.id.icon_bottle)
        val bottleAnimation = AnimationUtils.loadAnimation(this, R.anim.animate_splash_bottle)
        bottleImageView.startAnimation(bottleAnimation)

        // Mostrar la pantalla de splash durante 5 segundos
        Handler(Looper.getMainLooper()).postDelayed({
            // Después de 5 segundos, ir a la MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Finalizar SplashActivity para que no regrese al presionar atrás
        }, 5000) // 5000 milisegundos = 5 segundos
    }
}


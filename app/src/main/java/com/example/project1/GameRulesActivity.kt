package com.example.project1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class GameRulesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_rules)

        // Manejo de la flecha para retroceder
        val backArrow: ImageView = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener {
            // Regresar a la actividad anterior
            onBackPressed()
        }
    }
}

package com.example.project1.view

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.project1.R
import com.example.project1.viewModel.GameRulesViewModel

// GameRulesActivity.kt (Vista)
class GameRulesActivity : AppCompatActivity() {

    private lateinit var viewModel: GameRulesViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_game_rules)

        viewModel = ViewModelProvider(this).get(GameRulesViewModel::class.java)

        // Iniciar la lógica para pausar el audio al entrar
        viewModel.onEnterScreen()

        // Configurar la toolbar
        val backArrowButton = findViewById<ImageButton>(R.id.iv_back_arrow)
        backArrowButton.setOnClickListener {
            // Regresar al home y restaurar audio
            viewModel.onBackPressed()
            finish()  // Finaliza la actividad actual
        }
    }
}


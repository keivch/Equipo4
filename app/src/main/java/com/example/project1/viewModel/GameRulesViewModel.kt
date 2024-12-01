package com.example.project1.viewModel

import androidx.lifecycle.ViewModel
import com.example.project1.model.AudioManager

// GameRulesViewModel.kt (ViewModel)
class GameRulesViewModel : ViewModel() {

    fun onEnterScreen() {
        // Pausar el audio si está en ON
        AudioManager.pauseAudio()
    }

    fun onBackPressed() {
        // Restaurar el audio si estaba en ON al regresar al home
        AudioManager.playAudio()
    }
}

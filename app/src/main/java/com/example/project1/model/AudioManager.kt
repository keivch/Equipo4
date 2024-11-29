package com.example.project1.model

import android.content.Context
import android.media.MediaPlayer
import com.example.project1.R

object AudioManager {

    private var mediaPlayer: MediaPlayer? = null
    private var isAudioOn: Boolean = true  // Para controlar el estado del audio

    /**
     * Inicializa el MediaPlayer con el archivo de audio.
     */
    fun initializeAudio(context: Context) {
        if (mediaPlayer == null) {
            // Reemplaza "audio_background" con el nombre de tu archivo en res/raw
            mediaPlayer = MediaPlayer.create(context, R.raw.musica_espera)
            mediaPlayer?.isLooping = true  // Configura el audio para repetirse
        }
    }

    /**
     * Reproduce el audio si está activo.
     */
    fun playAudio() {
        if (isAudioOn && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    /**
     * Pausa el audio si se está reproduciendo.
     */
    fun pauseAudio() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    /**
     * Activa o desactiva el audio de fondo.
     * @param enable `true` para activar, `false` para desactivar
     */
    fun toggleAudio(enable: Boolean) {
        isAudioOn = enable
        if (isAudioOn) {
            playAudio()
        } else {
            pauseAudio()
        }
    }

    /**
     * Detiene y libera los recursos del MediaPlayer.
     */
    fun releaseAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    /**
     * Verifica si el audio está activo.
     */
    fun isAudioEnabled(): Boolean {
        return isAudioOn
    }
}

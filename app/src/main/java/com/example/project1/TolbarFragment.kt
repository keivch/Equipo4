package com.example.project1

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.project1.view.GameRulesActivity
import com.example.project1.view.LoginView

class ToolbarFragment : Fragment() {

    private var isAudioOn = true
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el layout del fragment
        return inflater.inflate(R.layout.toolbar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val iconStar: ImageView = view.findViewById(R.id.icon_star)
        val iconAudio: ImageView = view.findViewById(R.id.icon_audio)
        val iconInstructions: ImageView = view.findViewById(R.id.icon_instructions)
        val iconChallenges: ImageView = view.findViewById(R.id.icon_challenges)
        val iconShare: ImageView = view.findViewById(R.id.icon_share)
        val iconClose: ImageView = view.findViewById(R.id.icon_close)

        // Inicializar audio
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.musica_espera)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        iconStar.setOnClickListener {
            val intent = Intent(requireContext(), WebViewActivity::class.java).apply {
                putExtra("URL", "https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es")
            }
            startActivity(intent)
        }



        iconAudio.setOnClickListener {
            toggleAudio(iconAudio)
        }

        iconInstructions.setOnClickListener {
            // Crea un Intent para iniciar GameRulesActivity
            val intent = Intent(requireContext(), GameRulesActivity::class.java)
            startActivity(intent)
        }

        iconChallenges.setOnClickListener {
            mediaPlayer.pause()  // Pausa la música al entrar a RetosActivity
            val intent = Intent(requireContext(), ChallengeActivity::class.java)
            startActivity(intent)
        }

        iconShare.setOnClickListener {
            // Compartir la app
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "App pico botella \n¡¡Solo los valientes lo juegan!! \nhttps://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es")
            }
            startActivity(Intent.createChooser(shareIntent, "Compartir vía"))
        }

        iconClose.setOnClickListener{
//            val intent = Intent(requireContext(), LoginView::class.java)
//            startActivity(intent)
            (activity as? Ventana_home_principal)?.logoutAndCloseApp()
        }
    }

    private fun toggleAudio(iconAudio: ImageView) {
        if (isAudioOn) {
            mediaPlayer.pause()
            iconAudio.setImageResource(R.drawable.ic_audio_off)
        } else {
            mediaPlayer.start()
            iconAudio.setImageResource(R.drawable.ic_audio_on)
        }
        isAudioOn = !isAudioOn
    }

    override fun onResume() {
        super.onResume()
        // Reanudar la música si está activada
        if (isAudioOn) {
            mediaPlayer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        // Pausar la música cuando el fragmento está en pausa
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}


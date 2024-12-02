package com.example.project1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.project1.model.Pokemon
import com.example.project1.model.PokemonApi
import com.example.project1.view.CustomDialog
import com.google.firebase.auth.FirebaseAuth
import kotlin.random.Random
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.lifecycle.ViewModelProvider

import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



enum class ProviderType{
    BASIC
}

class Ventana_home_principal : AppCompatActivity() {

    private lateinit var bottleImage: ImageView
    private lateinit var countdownText: TextView
    private lateinit var btnParpa: View
    private var mediaPlayer: MediaPlayer? = null
    private var bottleSpinSound: MediaPlayer? = null
    private var lastRotation = 0f
    private var countdownTimer: CountDownTimer? = null  // Variable para controlar el temporizador

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient) // Usa el OkHttpClient con logging
        .build()

    private val pokemonApi = retrofit.create(PokemonApi::class.java)

    private suspend fun fetchPokemonList(): List<Pokemon> {
        return try {
            val response = pokemonApi.getPokemon()
            response.pokemon
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al obtener la lista de Pokémon: ${e.message}", e)
            emptyList() // Devuelve una lista vacía en caso de error
        }
    }

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

        // Si tienes el userId y el viewModel configurados, obtenemos los retos desde Firestore
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            lifecycleScope.launch {
                try {
                    // Obtén los retos desde Firestore
                    val retos = viewModel.laodChallenge(userId)

                    if (retos.isNotEmpty()) {
                        // Obtén un reto aleatorio
                        val randomReto = retos.random()

                        // Si estás mostrando un Pokémon también, obtén su imagen de alguna fuente
                        val pokemonList = fetchPokemonList()
                        val randomPokemon = pokemonList.random()

                        // Muestra el diálogo con el reto y el Pokémon
                        val dialog = CustomDialog(this@Ventana_home_principal)
                        dialog.setPokemonImage(randomPokemon.img)
                        dialog.setTitle(randomReto.name)
                        dialog.setMessage(randomReto.description)
                        dialog.setCancelable(false)
                        dialog.show()
                    } else {
                        // Si no hay retos, muestra un mensaje de advertencia
                        val dialog = CustomDialog(this@Ventana_home_principal)
                        dialog.setTitle("Sin reto :(")
                        dialog.setMessage("Al parecer no hay retos disponibles, ¡vamos a agregar uno!")
                        dialog.setCancelable(false)
                        dialog.show()
                    }
                } catch (e: Exception) {
                    // Maneja cualquier error que pueda ocurrir
                    Toast.makeText(this@Ventana_home_principal, "Error al cargar los retos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Si el usuario no está autenticado
            Toast.makeText(this@Ventana_home_principal, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        bottleSpinSound?.release()
        countdownTimer?.cancel()  // Cancelar el temporizador al destruir la actividad
    }
}


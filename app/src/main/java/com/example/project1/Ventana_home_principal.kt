package com.example.project1

import android.content.Intent
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
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
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
import com.example.project1.model.Challenge
import com.example.project1.view.LoginView
import com.example.project1.viewModel.ChallengeViewModel

import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor



enum class ProviderType{
    BASIC
}

class Ventana_home_principal : AppCompatActivity() {

    private lateinit var bottleImage: ImageView
    private lateinit var countdownText: TextView
    private lateinit var viewModel: ChallengeViewModel

    private lateinit var btnParpa: View
    private var mediaPlayer: MediaPlayer? = null
    private var bottleSpinSound: MediaPlayer? = null
    private var lastRotation = 0f
    private var countdownTimer: CountDownTimer? = null  // Variable para controlar el temporizador
    private var shouldCloseApp = false

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
        viewModel = ViewModelProvider(this).get(ChallengeViewModel::class.java)


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

        onBackPressedDispatcher.addCallback(this, object:OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(shouldCloseApp){
                    finishAffinity()
                }
            }
        })
        supportFragmentManager.beginTransaction()
            .replace(R.id.toolbar_container, ToolbarFragment())
            .commit()
    }

    fun logoutAndCloseApp() {
        FirebaseAuth.getInstance().signOut()
        shouldCloseApp = true
        val intent = Intent(this, LoginView::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
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

        // Obtén el ID del usuario autenticado
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            // Carga los retos desde el repositorio
            viewModel.obtenerRetos()

            // Observa los cambios en los retos después de haber llamado a obtenerRetos
            val RetosObserver = Observer<List<Challenge>> { retos ->
                if (!retos.isNullOrEmpty()) {
                    // Obtén un reto aleatorio
                    val randomReto = retos.random()

                    // Si estás mostrando un Pokémon también, obtén su imagen
                    lifecycleScope.launch {
                        val pokemonList = fetchPokemonList()
                        val randomPokemon = pokemonList.random()

                        // Muestra el diálogo con el reto y el Pokémon
                        val dialog = CustomDialog(this@Ventana_home_principal)
                        dialog.setPokemonImage(randomPokemon.img)
                        dialog.setTitle(randomReto.name)
                        dialog.setMessage(randomReto.description)
                        dialog.setCancelable(false)
                        dialog.show()
                    }
                } else {
                    // Si no hay retos, muestra un mensaje de advertencia
                    val dialog = CustomDialog(this@Ventana_home_principal)
                    dialog.setTitle("Sin reto :(")
                    dialog.setMessage("Al parecer no hay retos disponibles, ¡vamos a agregar uno!")
                    dialog.setCancelable(false)
                    dialog.show()
                }
            }

            viewModel.listaRetos.observe(this, RetosObserver)

            viewModel.listaRetos.removeObserver(RetosObserver)
        } else {
            // Manejo de caso en que el usuario no está autenticado
            val dialog = CustomDialog(this@Ventana_home_principal)
            dialog.setTitle("Error de autenticación")
            dialog.setMessage("No estás autenticado. Por favor, inicia sesión.")
            dialog.setCancelable(false)
            dialog.show()
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        bottleSpinSound?.release()
        countdownTimer?.cancel()  // Cancelar el temporizador al destruir la actividad
    }
}


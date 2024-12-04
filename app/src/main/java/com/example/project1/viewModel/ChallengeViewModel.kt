package com.example.project1.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.model.AudioManager
import com.example.project1.model.Challenge
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChallengeViewModel: ViewModel() {
    private val db = Firebase.firestore

    private var _listaRetos = MutableLiveData<List<Challenge>>(emptyList())
    val listaRetos: LiveData<List<Challenge>> = _listaRetos

    init{
        obtenerRetos()
    }

    fun obtenerRetos() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    // Filtra los retos según el userId
                    val resultado = db.collection("challenges")
                        .whereEqualTo("userId", userId) // Filtra por el userId del usuario logueado
                        .get()
                        .await()

                    val retos = resultado.documents.mapNotNull { it.toObject(Challenge::class.java) }
                    Log.d("Firestore", "Retos obtenidos: $retos")
                    _listaRetos.postValue(retos)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            Log.e("FirebaseAuth", "No hay usuario logueado.")
        }
    }


    fun agregarReto(reto: Challenge){
        reto.id = UUID.randomUUID().toString()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("challenges").document(reto.id).set(reto).await()
                _listaRetos.postValue(_listaRetos.value?.plus(reto))
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun editarReto(reto: Challenge){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("challenges").document(reto.id).update(reto.toMap()).await()
                _listaRetos.postValue(_listaRetos.value?.map { if (it.id == reto.id) reto else it })
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    fun onBackPressed() {

    }

    fun borrarReto(id: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                db.collection("challenges").document(id).delete().await()
                _listaRetos.postValue(_listaRetos.value?.filter { it.id != id })

            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}
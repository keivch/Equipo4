package com.example.project1.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.model.Challenge
import com.google.firebase.Firebase
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

    fun obtenerRetos(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val resultado = db.collection("challenges").get().await()
                val retos = resultado.documents.mapNotNull { it.toObject(Challenge::class.java) }
                _listaRetos.postValue(retos)
            }catch (e: Exception){
                e.printStackTrace()
            }
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
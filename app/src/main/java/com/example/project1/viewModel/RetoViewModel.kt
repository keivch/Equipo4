package com.example.project1.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project1.model.Reto
import java.util.UUID

class ChallengeViewModel : ViewModel() {

    private val repository = RetoRepositorio()

    // LiveData para observar los retos
    private val _challenges = MutableLiveData<List<Reto>?>()
    val challenges: MutableLiveData<List<Reto>?> get() = _challenges

    // Método para cargar retos
    fun loadChallenges(userId: String) {
        repository.getChallenges(userId) { result ->
            _challenges.postValue(result)
        }
    }

    // Método para guardar un reto
    fun saveChallenge(userId: String, challenge: Reto, onComplete: (Boolean) -> Unit) {
        // Si no tienes un id en el reto, puedes agregarlo automáticamente
        val challengeWithId = challenge.copy(id = UUID.randomUUID().toString())

        repository.saveChallenge(userId, challengeWithId) { success ->
            onComplete(success as Boolean)
        }
    }

    fun deleteChallenge(userId: String, challenge: Reto, onComplete: (Boolean) -> Unit) {
        repository.deleteChallenge(userId, challenge) { success ->
            if (success) loadChallenges(userId) // Actualiza la lista después de eliminar
            onComplete(success)
        }
    }

    fun updateChallenge(userId: String, challenge: Reto, onComplete: (Boolean) -> Unit) {
        repository.updateChallenge(userId, challenge) { success ->
            if (success) loadChallenges(userId) // Actualiza la lista después de actualizar
            onComplete(success)
        }
    }

}


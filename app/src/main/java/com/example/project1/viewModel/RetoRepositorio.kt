package com.example.project1.viewModel

import com.example.project1.model.Reto
import com.google.firebase.firestore.FirebaseFirestore

class RetoRepositorio{

    private val db = FirebaseFirestore.getInstance()

    // Método para guardar un reto
    fun saveChallenge(userId: String, Reto: Reto, challenge: (Any) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("challenges")
            .add(challenge)
    }

    // Método para obtener retos
    fun getChallenges(userId: String, onResult: (List<Reto>?) -> Unit) {
        db.collection("users")
            .document(userId)
            .collection("challenges")
            .get()
            .addOnSuccessListener { snapshot ->
                val challenges = snapshot.toObjects(Reto::class.java)
                onResult(challenges)
            }
            .addOnFailureListener { onResult(null) }
    }
    fun deleteChallenge(userId: String, challenge: Reto, onComplete: (Boolean) -> Unit) {
        // Referencia al documento del reto específico en Firestore
        db.collection("users")
            .document(userId)
            .collection("challenges")
            .document(challenge.id) // Asegúrate de tener un campo 'id' único para cada reto
            .delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }
    fun updateChallenge(userId: String, challenge: Reto, onComplete: (Boolean) -> Unit) {
        // Referencia al documento del reto específico
        db.collection("users")
            .document(userId)
            .collection("challenges")
            .document(challenge.id) // Usamos 'id' para localizar el reto
            .set(challenge) // Actualiza el reto con los nuevos datos
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

}

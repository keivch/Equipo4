package com.example.project1.viewModel

import com.example.project1.model.Reto
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class RetoRepositorio{

    private val db = FirebaseFirestore.getInstance()

    // Método para guardar un reto
    fun saveChallenge(userId: String, challenge: Reto, onComplete: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(userId).collection("retos")
            .document(challenge.id ?: UUID.randomUUID().toString())
            .set(challenge)
            .addOnSuccessListener { onComplete(true) }
            .addOnFailureListener { onComplete(false) }
    }


    // Método para obtener retos
    fun getChallenges(userId: String, onComplete: (List<Reto>) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Accede a la colección "usuarios" y a la subcolección "retos"
        db.collection("usuarios").document(userId).collection("retos")
            .get() // Obtén todos los documentos de la subcolección "retos"
            .addOnSuccessListener { documents ->
                val retos = documents.mapNotNull { it.toObject(Reto::class.java) } // Convierte los documentos a objetos de tipo Reto
                onComplete(retos) // Devuelve la lista de retos al callback
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                onComplete(emptyList()) // En caso de error, devuelve una lista vacía
            }
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

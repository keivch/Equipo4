package com.example.project1.model

data class Challenge(
    var id: String = "",
    var name: String = "",
    var description: String = "",
){
    fun toMap(): Map<String, String>{
        return mapOf(
            "name" to name,
            "description" to description
        )
    }
}

package com.example.project1.model

data class Challenge(
    var id: String = "",
    var description: String = "",
    var userId: String? = ""
){
    fun toMap(): Map<String, String?>{
        return mapOf(
            "description" to description,
            "userId" to userId
        )
    }
}

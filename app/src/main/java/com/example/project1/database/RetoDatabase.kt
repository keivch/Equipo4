package com.example.project1.database


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.project1.model.Reto

class RetoDatabase(context: Context) : SQLiteOpenHelper(context, "retoDB", null, 2) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE retos (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, nombre TEXT)")
    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) { // Cambia 2 a la versión adecuada
            db.execSQL("ALTER TABLE retos ADD COLUMN nombre TEXT")
        }
    }


    fun getAllRetos(): MutableList<Reto> {
        val retos = mutableListOf<Reto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM retos", null)
        while (cursor.moveToNext()) {
            // Cambia el índice para obtener el nombre y la descripción
            val id = cursor.getInt(0)
            val nombre = cursor.getString(1)
            val description = cursor.getString(2) // Asegúrate de que esto sea correcto
            retos.add(Reto(id, nombre, description))
            Log.d("RetoDatabase", "Reto encontrado: $nombre - $description")
        }
        cursor.close()

        return retos
    }


    fun deleteReto(id: Int) {
        val db = writableDatabase
        db.delete("retos", "id=?", arrayOf(id.toString()))
    }


    fun addReto(nombre: String, descripcion: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("description", descripcion)
        }
        val newRowId = db.insert("retos", null, values)
        if (newRowId == -1L) {
            Log.e("RetoDatabase", "Error al agregar reto")
        }
        db.close()
    }

    fun editReto(id: Int, nuevoNombre: String, nuevaDescripcion: String) {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put("nombre", nuevoNombre)
            put("description", nuevaDescripcion)
        }

        // Ejecuta la actualización en la base de datos usando el id
        db.update("retos", contentValues, "id = ?", arrayOf(id.toString()))
        db.close()
    }



}

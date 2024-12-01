package com.example.project1.view

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.project1.ProviderType
import com.example.project1.R
import com.example.project1.Ventana_home_principal
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginView : AppCompatActivity() {
    private val db=FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val textInputLayoutEmail: TextInputLayout = findViewById(R.id.textInput_LayoutEmail)
        val editTextEmail: TextInputEditText = findViewById(R.id.editTextEmail)

        val textInputLayoutPassword: TextInputLayout =findViewById(R.id.textInput_LayoutPassword)
        val editTextPassword: TextInputEditText= findViewById(R.id.editTextPassword)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val buttonRegistrarse: Button = findViewById(R.id.buttonRegistrarse)
        
        val colorHintFocused = ContextCompat.getColor(this, R.color.white)

        editTextEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutEmail.boxStrokeColor = Color.WHITE
                textInputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(colorHintFocused)
            } else {
                textInputLayoutEmail.boxStrokeColor = Color.GRAY
                textInputLayoutEmail.defaultHintTextColor = ColorStateList.valueOf(colorHintFocused) } }

        
        editTextPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                textInputLayoutPassword.boxStrokeColor = Color.WHITE
                textInputLayoutPassword.defaultHintTextColor = ColorStateList.valueOf(colorHintFocused)
            } else {
                textInputLayoutPassword.boxStrokeColor = Color.GRAY
                textInputLayoutPassword.defaultHintTextColor = ColorStateList.valueOf(colorHintFocused) } }

        editTextPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (password.length < 6) {
                    textInputLayoutPassword.error = "Debe tener al menos 6 caracteres"
                } else if (password.length > 10) {
                    textInputLayoutPassword.error = "Debe tener como máximo 10 caracteres"
                } else {
                    textInputLayoutPassword.error = null } }
            override fun afterTextChanged(s: Editable?) {} })

        
        fun updateButtonState() {
            val isEmailFilled = !editTextEmail.text.isNullOrEmpty()
            val isPasswordFilled = !editTextPassword.text.isNullOrEmpty()
            
            if (isEmailFilled && isPasswordFilled) {
                buttonLogin.isEnabled = true
                buttonLogin.setTextColor(resources.getColor(R.color.white, theme))
                buttonLogin.typeface = Typeface.create(buttonLogin.typeface, Typeface.BOLD)

                buttonRegistrarse.isEnabled = true
                buttonRegistrarse.setTextColor(resources.getColor(R.color.white, theme))
                buttonRegistrarse.typeface = Typeface.create(buttonLogin.typeface, Typeface.BOLD)
            } else {
                buttonLogin.isEnabled = false
                buttonLogin.setTextColor(resources.getColor(R.color.black, theme))
                buttonLogin.typeface = Typeface.create(buttonLogin.typeface, Typeface.NORMAL)

                buttonRegistrarse.isEnabled = false
                buttonRegistrarse.setTextColor(resources.getColor(R.color.black, theme))
                buttonRegistrarse
                    .typeface = Typeface.create(buttonLogin.typeface, Typeface.NORMAL) } }

        
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        editTextEmail.addTextChangedListener(textWatcher)
        editTextPassword.addTextChangedListener(textWatcher)




        fun showHome(email:String,provider:ProviderType){
            val homeIntent:Intent= Intent(this, Ventana_home_principal::class.java).apply {
                putExtra("email",email)
                putExtra("provider",provider.name)
            }
            startActivity(homeIntent)

        }

        fun setup(){

            buttonRegistrarse.setOnClickListener{



                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(editTextEmail.text.toString(),editTextPassword.text.toString()).addOnCompleteListener{
                        if(it.isSuccessful){

                            db.collection("users").document(editTextEmail.text.toString())

                            showHome(it.result?.user?.email ?:"", ProviderType.BASIC)

                        }else{
                            Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                        } } }


            buttonLogin.setOnClickListener{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(editTextEmail.text.toString(),editTextPassword.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        showHome(it.result?.user?.email ?:"", ProviderType.BASIC)

                    }else{
                        Toast.makeText(this, "Error en el registro", Toast.LENGTH_SHORT).show()
                    } } }

        }


        setup()
    }
    
}
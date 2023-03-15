package com.example.ondehoje

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var linear_login: LinearLayout
    private lateinit var linear_register: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        linear_login = findViewById(R.id.linear_login)
        linear_register = findViewById(R.id.linear_register)

        tabInit()

        auth = FirebaseAuth.getInstance()
        val buttonRegister = findViewById<Button>(R.id.btn_register)
        val buttonLogin = findViewById<Button>(R.id.btn_login)

        buttonRegister.setOnClickListener {
            register()
        }
        buttonLogin.setOnClickListener {
            logar()
        }
    }

    fun logar() {
        var editTextEmail = findViewById<EditText>(R.id.edit_email_login)
        var editTextSenha = findViewById<EditText>(R.id.edit_password_login)

        var email = editTextEmail.text.toString()
        var senha = editTextSenha.text.toString()

        var valido = true

        if (email.isEmpty()) {
            editTextEmail.error = "Campo vazio!"
            valido = false
        }

        if (senha.isEmpty()) {
            editTextSenha.error = "Campo vazio!"
            valido = false
        }
        val pd = ProgressDialog(this)
        pd.setMessage("Entrando...")
        pd.show()
        if (valido) {
            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                    } else {
                        try {
                            throw task.exception!!
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(
                                applicationContext,
                                "Credenciais inválidas!", Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: Exception) {
                            Toast.makeText(
                                applicationContext,
                                "Erro ao realizar login: " + e.message, Toast.LENGTH_SHORT
                            ).show()
                        }

                    }
                }
        }
    }

    fun register() {
        val editName = findViewById<EditText>(R.id.edit_name_register)
        val editEmail = findViewById<EditText>(R.id.edit_email_register)
        val editPassword = findViewById<EditText>(R.id.edit_password_register)

        val name = editName.text.toString()
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()
        var permit = true

        if (name.isEmpty()) {
            editName.error = "Campo obrigatório!"
            permit = false
        }

        if (email.isEmpty()) {
            editEmail.error = "Campo obrigatório!"
            permit = false
        }

        if (password.isEmpty()) {
            editPassword.error = "Campo obrigatório!"
            permit = false
        }

        if (permit) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Usuaŕio cadastrado!",
                        Toast.LENGTH_SHORT
                    ).show()
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                } else {
                    try {
                        throw it.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        Toast.makeText(
                            this,
                            "Senha fraca!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            this,
                            "Email inválido!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: FirebaseAuthUserCollisionException) {
                        Toast.makeText(
                            this,
                            "Usuário já cadastrado!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this,
                            "Erro ao realizar cadastro ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    fun tabInit() {
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout_login)


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        linear_login.visibility = View.VISIBLE
                        linear_register.visibility = View.GONE
                    }
                    1 -> {
                        linear_login.visibility = View.GONE
                        linear_register.visibility = View.VISIBLE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }
}
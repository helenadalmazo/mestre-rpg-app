package com.dalmazo.helena.mestrerpg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.*

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private var login = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()

        val emailTextView = findViewById<TextView>(R.id.email)
        val passwordTextView = findViewById<TextView>(R.id.password)
        val loginButton = findViewById<Button>(R.id.login)
        val toggleLoginRegisterLink = findViewById<TextView>(R.id.toggle_login_register)

        loginButton.setOnClickListener {
            val email = emailTextView.text.toString()
            val password = passwordTextView.text.toString()

            if (email.isEmpty() || password.isEmpty()) return@setOnClickListener

            if (login) {
                login(email, password)
            } else {
                register(email, password)
            }
        }

        toggleLoginRegisterLink.setOnClickListener {
            if (login) {
                loginButton.setText("Criar nova conta")
                toggleLoginRegisterLink.setText("Login")
            } else {
                loginButton.setText("Login")
                toggleLoginRegisterLink.setText("Criar nova conta")
            }

            login = !login
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            // mantém no authActivity
        }
    }

    private fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    var message = getAuthenticationErrorMessage(task.exception as FirebaseAuthException)
                    Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    var message = getAuthenticationErrorMessage(task.exception as FirebaseAuthException)
                    Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

    private fun getAuthenticationErrorMessage(exception: FirebaseAuthException): String {
        if (exception is FirebaseAuthWeakPasswordException) {
            return "A senha deve ter mais de 6 letras."
        }

        if (exception is FirebaseAuthUserCollisionException) {
            return "O e-mail está em uso por outro usuário."
        }

        if (exception is FirebaseAuthInvalidCredentialsException) {
            if (exception.errorCode == "ERROR_INVALID_EMAIL") {
                return "E-mail inválido."
            }

            if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                return "Usuário não encontrado."
            }

            if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                return "Senha incorreta."
            }

            return "Não foi possível identificar e/ ou autenticar este E-mail/Senha."
        }


        if (exception is FirebaseAuthInvalidUserException) {
            if (exception.errorCode == "ERROR_USER_DISABLED") {
                return "Usuário desabilitado."
            }

            if (exception.errorCode == "ERROR_USER_NOT_FOUND") {
                return "Usuário não encontrado."
            }

            if (exception.errorCode == "ERROR_USER_TOKEN_EXPIRED") {
                return "Token expirado."
            }

            if (exception.errorCode == "ERROR_INVALID_USER_TOKEN") {
                return "Token inválido."
            }

            return "O usuário é/está inválido."
        }

        return "Erro desconhecido, tente novamente mais tarde."
    }
}

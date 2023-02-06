package com.cursoandroid.encontrarpetscampinas.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.cursoandroid.encontrarpetscampinas.R
import com.cursoandroid.encontrarpetscampinas.config.ConfiguracaoFirebase
import com.cursoandroid.encontrarpetscampinas.databinding.ActivityEsqueceuAsenhaBinding
import com.google.android.gms.tasks.OnCompleteListener


class EsqueceuASenhaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEsqueceuAsenhaBinding
    private val firebaseAuth = ConfiguracaoFirebase.getFirebaseAutenticacao()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEsqueceuAsenhaBinding.inflate(layoutInflater)

        setContentView( binding.root )

        binding.botaoContato.setOnClickListener(View.OnClickListener {
            enviarEmail() })

        val toolbar = findViewById<Toolbar>(R.id.toolbarEsqueceuASenha)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

    }

    fun enviarEmail(){

        val emailAddress = binding.campoEmailLogin.text.toString()

        if ( !emailAddress.isBlank() ){

            firebaseAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(OnCompleteListener<Void?> { task ->
                    if (task.isSuccessful) {
                        finish()
                        Toast.makeText(applicationContext, "E-mail enviado com sucesso!!!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "Erro ao enviar e-mail!!!", Toast.LENGTH_SHORT).show()

                    }
                })

        }else{
            Toast.makeText(applicationContext, "Campo e-mail não pode ser vazio!!!", Toast.LENGTH_SHORT).show()

        }



    }

}
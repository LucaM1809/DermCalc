package com.morosini.dermcalc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_bsa_result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bsa_result)

        val bsa = intent.getDoubleExtra("bsa", 0.0)
        val nomePaziente = App.nomePaziente
        val dataVisita = App.dataVisita

        findViewById<TextView>(R.id.tvNomePaziente).text = nomePaziente
        findViewById<TextView>(R.id.tvDataVisita).text = dataVisita

        // risultato colorato
        findViewById<TextView>(R.id.tvValoreBsa).apply {
            text = "BMI: ${String.format("%.2f", bsa)}"
        }


        // link fonte
        findViewById<TextView>(R.id.tvFonte).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://leaderfarmablog.it/bmi-indice-di-massa-corporea/"))
            startActivity(intent)
        }

        // torna alla home
        findViewById<Button>(R.id.btnTorna).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

    }
}
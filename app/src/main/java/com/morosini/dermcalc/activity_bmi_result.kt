package com.morosini.dermcalc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.widget.Button
import android.widget.TextView

class activity_bmi_result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bmi_result)

        val bmi = intent.getDoubleExtra("bmi", 0.0)
        val classe = intent.getStringExtra("bmi_classe") ?: ""
        val nomePaziente = intent.getStringExtra("nome_paziente") ?: ""
        val dataVisita = intent.getStringExtra("data_visita") ?: ""

        findViewById<TextView>(R.id.tvNomePaziente).text = nomePaziente
        findViewById<TextView>(R.id.tvDataVisita).text = dataVisita

        // colore in base al range
        val colore = when {
            bmi < 16.0 -> Color.parseColor("#0D2B6B")
            bmi < 17.0 -> Color.parseColor("#1A52B0")
            bmi < 18.5 -> Color.parseColor("#A8C4E0")
            bmi < 25.0 -> Color.parseColor("#639922")
            bmi < 30.0 -> Color.parseColor("#F5C842")
            bmi < 35.0 -> Color.parseColor("#EF9F27")
            bmi < 40.0 -> Color.parseColor("#E05A2B")
            else ->       Color.parseColor("#A32D2D")
        }

        // risultato colorato
        findViewById<TextView>(R.id.tvValoreBmi).apply {
            text = "BMI: ${String.format("%.2f", bmi)}"
            setTextColor(colore)
        }
        findViewById<TextView>(R.id.tvClasseBmi).apply {
            text = classe
            setTextColor(colore)
        }

        // gradi severità — colora tutti e mette in bold quello attivo
        val gradi = listOf(
            Triple(R.id.tvGrado1, Color.parseColor("#0D2B6B"), bmi < 16.0),
            Triple(R.id.tvGrado2, Color.parseColor("#1A52B0"), bmi in 16.0..16.99),
            Triple(R.id.tvGrado3, Color.parseColor("#A8C4E0"), bmi in 17.0..18.49),
            Triple(R.id.tvGrado4, Color.parseColor("#639922"), bmi in 18.5..24.99),
            Triple(R.id.tvGrado5, Color.parseColor("#F5C842"), bmi in 25.0..29.99),
            Triple(R.id.tvGrado6, Color.parseColor("#EF9F27"), bmi in 30.0..34.99),
            Triple(R.id.tvGrado7, Color.parseColor("#E05A2B"), bmi in 35.0..39.99),
            Triple(R.id.tvGrado8, Color.parseColor("#A32D2D"), bmi >= 40.0)
        )

        gradi.forEach { (id, coloreGrado, attivo) ->
            findViewById<TextView>(id).apply {
                setTextColor(coloreGrado)
                if (attivo) {
                    setTypeface(null, Typeface.BOLD)
                    textSize = 14f
                } else {
                    setTypeface(null, Typeface.NORMAL)
                    textSize = 13f
                }
            }
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
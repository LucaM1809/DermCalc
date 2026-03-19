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

class activity_easi_result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easi_result)

        val easi = intent.getDoubleExtra("easi", 0.0)
        val classe = intent.getStringExtra("easi_classe") ?: ""

        findViewById<TextView>(R.id.tvNomePaziente).text = App.nomePaziente
        findViewById<TextView>(R.id.tvDataVisita).text = App.dataVisita

        // colore in base al range
        val colore = when {
            easi == 0.0 -> Color.parseColor("#1A52B0")
            easi <= 1.0 -> Color.parseColor("#4A90D9")
            easi <= 7.0 -> Color.parseColor("#639922")
            easi <= 21.0 -> Color.parseColor("#F5C842")
            easi <= 50.0 -> Color.parseColor("#EF9F27")
            else -> Color.parseColor("#A32D2D")
        }

        // risultato colorato
        findViewById<TextView>(R.id.tvValoreEasi).apply {
            text = "EASI: ${String.format("%.2f", easi)}"
            setTextColor(colore)
        }
        findViewById<TextView>(R.id.tvClasseEasi).apply {
            text = classe
            setTextColor(colore)
        }

        // gradi severità colore
        val gradi = listOf(
            Triple(R.id.tvGrado1, Color.parseColor("#1A52B0"), easi == 0.0),
            Triple(R.id.tvGrado2, Color.parseColor("#4A90D9"), easi in 0.1..1.0),
            Triple(R.id.tvGrado3, Color.parseColor("#639922"), easi in 1.1 .. 7.0),
            Triple(R.id.tvGrado4, Color.parseColor("#F5C842"), easi in 7.1..21.0),
            Triple(R.id.tvGrado5, Color.parseColor("#EF9F27"), easi in 21.1..50.0),
            Triple(R.id.tvGrado6, Color.parseColor("#A32D2D"), easi > 50.0)
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
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.synvisc.it/dam/jcr:09a0e776-b2e5-4e0a-a731-91f6668049fc/Dupixent-Eczema-Area-and-Severity-Index-EN-Accessible.pdf")
            )
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
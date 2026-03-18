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

class activity_pasi_result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasi_result)

        val pasi = intent.getDoubleExtra("pasi", 0.0)
        val classe = intent.getStringExtra("pasi_classe") ?: ""

        findViewById<TextView>(R.id.tvNomePaziente).text = App.nomePaziente
        findViewById<TextView>(R.id.tvDataVisita).text = App.dataVisita

        // colore in base al range
        val colore = when {
            pasi <= 10.0 -> Color.parseColor("#639922")
            pasi < 20.0 -> Color.parseColor("#F5C842")
            else -> Color.parseColor("#A32D2D")
        }

        // risultato colorato
        findViewById<TextView>(R.id.tvValorePasi).apply {
            text = "PASI: ${String.format("%.2f", pasi)}"
            setTextColor(colore)
        }
        findViewById<TextView>(R.id.tvClassePasi).apply {
            text = classe
            setTextColor(colore)
        }

        // gradi severità colore
        val gradi = listOf(
            Triple(R.id.tvGrado1, Color.parseColor("#639922"), pasi <= 10.0),
            Triple(R.id.tvGrado2, Color.parseColor("#F5C842"), pasi in 10.0..19.99),
            Triple(R.id.tvGrado3, Color.parseColor("#A32D2D"), pasi >= 20.0)
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
                Uri.parse("https://www.lapelleconta.it/psoriasi/esami-e-diagnosi/pasi-psoriasi//")
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
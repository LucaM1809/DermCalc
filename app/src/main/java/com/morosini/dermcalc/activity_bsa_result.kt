package com.morosini.dermcalc

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_bsa_result : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bsa_result)

        val bsa = intent.getDoubleExtra("bsa", 0.0)
        val supBsa = intent.getDoubleExtra("supBsa", 0.0)
        val classe = intent.getStringExtra("bsa_class") ?: ""

        findViewById<TextView>(R.id.tvNomePaziente).text = App.nomePaziente
        findViewById<TextView>(R.id.tvDataVisita).text = App.dataVisita

        val colore = when {
            bsa <= 10.0 -> Color.parseColor("#639922")
            bsa <= 20.0 -> Color.parseColor("#F5C842")
            else -> Color.parseColor("#A32D2D")
        }

        findViewById<TextView>(R.id.tvValoreBsa).apply {
            text = "BSA: ${String.format("%.2f", bsa)}%"
            setTextColor(colore)
        }
        findViewById<TextView>(R.id.tvSuperficieBsa).apply {
            text = "Superficie in cm²: ${String.format("%.2f", supBsa)}"
            setTextColor(colore)
        }
        findViewById<TextView>(R.id.tvClasseBsa).apply {
            text = classe
            setTextColor(colore)
        }

        val gradi = listOf(
            Triple(R.id.tvGrado1, Color.parseColor("#639922"), bsa <= 10.0),
            Triple(R.id.tvGrado2, Color.parseColor("#F5C842"), bsa > 10.0 && bsa <= 20.0),
            Triple(R.id.tvGrado3, Color.parseColor("#A32D2D"), bsa > 20.0)
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
        findViewById<TextView>(R.id.tvFonte1).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://calcolatrice.now/calcolatore-dell-area-superficiale-corporea/"))
            startActivity(intent)
        }
        findViewById<TextView>(R.id.tvFonte2).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.ildermatologorisponde.it/superficie-cutanea-bsa.php"))
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
package com.morosini.dermcalc

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PatientActivity : AppCompatActivity() {

    private lateinit var db: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)
        db = DataBaseHelper(this)
    }

    override fun onResume() {
        super.onResume()
        caricaLista()
    }

    private fun caricaLista() {
        val lista = db.getPazienti()
        val container = findViewById<LinearLayout>(R.id.listaPazienti)
        val tvNumero = findViewById<TextView>(R.id.tvNumeroPazienti)

        container.removeAllViews()
        tvNumero.text = "${lista.size} pazienti registrati"

        if (lista.isEmpty()) {
            val tvVuoto = TextView(this).apply {
                text = "Nessun paziente registrato"
                textSize = 14f
                setTextColor(Color.parseColor("#888888"))
                setPadding(0, 24, 0, 0)
            }
            container.addView(tvVuoto)
            return
        }

        lista.forEach { (id, nome, dataNascita) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(32, 24, 32, 24)
                setBackgroundResource(R.drawable.bg_card)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(0, 0, 0, 24)
                layoutParams = params
            }

            val tvNome = TextView(this).apply {
                text = nome
                textSize = 15f
                setTextColor(Color.parseColor("#222222"))
                setTypeface(null, Typeface.BOLD)
            }

            val tvData = TextView(this).apply {
                text = "Nato il $dataNascita"
                textSize = 12f
                setTextColor(Color.parseColor("#888888"))
            }

            row.addView(tvNome)
            row.addView(tvData)

            row.setOnClickListener {
                val intent = Intent(this, PatientDetailActivity::class.java)
                intent.putExtra("paziente_id", id)
                intent.putExtra("nome_paziente", nome)
                startActivity(intent)
            }

            container.addView(row)
        }
    }
}
package com.morosini.dermcalc

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PatientDetailActivity : AppCompatActivity() {

    private lateinit var db: DataBaseHelper
    private var pazienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_detail)

        db = DataBaseHelper(this)
        pazienteId = intent.getIntExtra("paziente_id", -1)
        val nomePaziente = intent.getStringExtra("nome_paziente") ?: ""

        val paziente = db.getPaziente(pazienteId)
        findViewById<TextView>(R.id.tvNomePazienteDetail).text = nomePaziente
        findViewById<TextView>(R.id.tvCFDetail).text = paziente?.third ?: ""

        caricaVisite()

        findViewById<Button>(R.id.btnEliminaPaziente).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Elimina paziente")
                .setMessage("Sei sicuro? Verranno eliminate anche tutte le visite associate.")
                .setPositiveButton("Elimina") { _, _ ->
                    db.eliminaPaziente(pazienteId)
                    Toast.makeText(this, "Paziente eliminato", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    private fun caricaVisite() {
        val visite = db.getVisitePaziente(pazienteId)
        val container = findViewById<LinearLayout>(R.id.listaVisite)
        container.removeAllViews()

        if (visite.isEmpty()) {
            val tvVuoto = TextView(this).apply {
                text = "Nessuna visita registrata"
                textSize = 14f
                setTextColor(Color.parseColor("#888888"))
                setPadding(0, 24, 0, 0)
            }
            container.addView(tvVuoto)
            return
        }

        visite.forEach { visita ->
            val visitaId = visita["id"] as Int
            val dataVisita = visita["data_visita"] as String
            val pasi = visita["pasi"] as? Double
            val easi = visita["easi"] as? Double
            val bmi = visita["bmi"] as? Double
            val bsa = visita["bsa"] as? Double

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

            val tvData = TextView(this).apply {
                text = "Visita del $dataVisita"
                textSize = 14f
                setTextColor(Color.parseColor("#222222"))
                setTypeface(null, Typeface.BOLD)
            }

            val sb = StringBuilder()
            if (pasi != null) sb.append("PASI: ${String.format("%.2f", pasi)}  ")
            if (easi != null) sb.append("EASI: ${String.format("%.2f", easi)}  ")
            if (bmi != null) sb.append("BMI: ${String.format("%.2f", bmi)}  ")
            if (bsa != null) sb.append("BSA: ${String.format("%.2f", bsa)}")
            if (sb.isEmpty()) sb.append("Nessun indice calcolato")

            val tvIndici = TextView(this).apply {
                text = sb.toString().trim()
                textSize = 12f
                setTextColor(Color.parseColor("#888888"))
            }

            row.addView(tvData)
            row.addView(tvIndici)

            row.setOnClickListener {
                val intent = Intent(this, ReportActivity::class.java)
                intent.putExtra("visita_id", visitaId)
                intent.putExtra("from_db", true)
                startActivity(intent)
            }

            container.addView(row)
        }
    }
}
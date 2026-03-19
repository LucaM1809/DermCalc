package com.morosini.dermcalc

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReportActivity : AppCompatActivity() {

    private lateinit var db: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        db = DataBaseHelper(this)

        val fromDb = intent.getBooleanExtra("from_db", false)

        val visita: Map<String, Any?>?
        val nomePaziente: String
        val codiceFiscale: String
        val dataVisita: String

        if (fromDb) {
            val visitaId = intent.getIntExtra("visita_id", -1)
            visita = db.getVisita(visitaId)
            val pazienteId = visita["paziente_id"] as Int
            val paziente = db.getPaziente(pazienteId)
            nomePaziente = paziente?.second ?: ""
            codiceFiscale = paziente?.third ?: ""
            dataVisita = visita["data_visita"] as? String ?: ""
            findViewById<Button>(R.id.btnSalvaReport).visibility = android.view.View.GONE
        } else {
            val visite = db.getVisitePaziente(App.pazienteId)
            visita = visite.firstOrNull { (it["id"] as Int) == App.visitaId }
            nomePaziente = App.nomePaziente
            codiceFiscale = App.codiceFiscale
            dataVisita = App.dataVisita
        }

        findViewById<TextView>(R.id.tvDataVisitaReport).text = dataVisita
        findViewById<TextView>(R.id.tvNomeReport).text = nomePaziente
        findViewById<TextView>(R.id.tvCFReport).text = codiceFiscale

        val pasi = visita?.get("pasi") as? Double
        val easi = visita?.get("easi") as? Double
        val bmi = visita?.get("bmi") as? Double
        val bsa = visita?.get("bsa") as? Double

        // PASI
        val tvPasi = findViewById<TextView>(R.id.tvPasiReport)
        if (pasi != null) {
            val (classePasi, colorePasi) = when {
                pasi <= 10.0 -> Pair("Lieve", Color.parseColor("#639922"))
                pasi < 20.0 -> Pair("Moderata", Color.parseColor("#F5C842"))
                else -> Pair("Grave", Color.parseColor("#A32D2D"))
            }
            tvPasi.text = "${String.format("%.2f", pasi)} — $classePasi"
            tvPasi.setTextColor(colorePasi)
        } else {
            tvPasi.text = "Non calcolato"
            tvPasi.setTextColor(Color.parseColor("#888888"))
        }

        // EASI
        val tvEasi = findViewById<TextView>(R.id.tvEasiReport)
        if (easi != null) {
            val (classeEasi, coloreEasi) = when {
                easi == 0.0 -> Pair("Assente", Color.parseColor("#1A52B0"))
                easi <= 1.0 -> Pair("Quasi assente", Color.parseColor("#4A90D9"))
                easi <= 7.0 -> Pair("Lieve", Color.parseColor("#639922"))
                easi <= 21.0 -> Pair("Moderata", Color.parseColor("#F5C842"))
                easi <= 50.0 -> Pair("Grave", Color.parseColor("#EF9F27"))
                else -> Pair("Molto grave", Color.parseColor("#A32D2D"))
            }
            tvEasi.text = "${String.format("%.2f", easi)} — $classeEasi"
            tvEasi.setTextColor(coloreEasi)
        } else {
            tvEasi.text = "Non calcolato"
            tvEasi.setTextColor(Color.parseColor("#888888"))
        }

        // BMI
        val tvBmi = findViewById<TextView>(R.id.tvBmiReport)
        if (bmi != null) {
            val (classeBmi, coloreBmi) = when {
                bmi < 16.0 -> Pair("Sottopeso severo", Color.parseColor("#0D2B6B"))
                bmi < 17.0 -> Pair("Sottopeso moderato", Color.parseColor("#1A52B0"))
                bmi < 18.5 -> Pair("Sottopeso lieve", Color.parseColor("#A8C4E0"))
                bmi < 25.0 -> Pair("Normopeso", Color.parseColor("#639922"))
                bmi < 30.0 -> Pair("Sovrappeso", Color.parseColor("#F5C842"))
                bmi < 35.0 -> Pair("Obesità I", Color.parseColor("#EF9F27"))
                bmi < 40.0 -> Pair("Obesità II", Color.parseColor("#E05A2B"))
                else -> Pair("Obesità III", Color.parseColor("#A32D2D"))
            }
            tvBmi.text = "${String.format("%.2f", bmi)} — $classeBmi"
            tvBmi.setTextColor(coloreBmi)
        } else {
            tvBmi.text = "Non calcolato"
            tvBmi.setTextColor(Color.parseColor("#888888"))
        }

        // BSA
        val tvBsa = findViewById<TextView>(R.id.tvBsaReport)
        if (bsa != null) {
            val colorBsa = Color.parseColor("#D4537E")
            tvBsa.text = "${String.format("%.2f", bsa)}"
            tvBsa.setTextColor(colorBsa)
        } else {
            tvBsa.text = "Non calcolato"
            tvBsa.setTextColor(Color.parseColor("#888888"))
        }

        // torna alla visita
        findViewById<Button>(R.id.btnTornaVisita).setOnClickListener {
            finish()
        }

        // salva report
        findViewById<Button>(R.id.btnSalvaReport).setOnClickListener {
            val tuttiNull = pasi == null && easi == null && bmi == null && bsa == null
            if (tuttiNull) {
                Toast.makeText(this, "Non puoi salvare un report vuoto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Salva report")
                .setMessage("Sei sicuro di voler salvare il report?")
                .setPositiveButton("Salva") { _, _ ->
                    // il report è già salvato nel DB aggiornando la visita
                    // qui resettiamo la sessione e torniamo alla home
                    App.pazienteId = -1
                    App.visitaId = -1
                    App.nomePaziente = ""
                    App.dataNascita = ""
                    App.codiceFiscale = ""
                    App.dataVisita = ""
                    App.sessioneAttiva = false
                    App.salva(this)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }
}
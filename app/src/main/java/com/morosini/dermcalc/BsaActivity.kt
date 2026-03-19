package com.morosini.dermcalc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BsaActivity : AppCompatActivity() {
    private lateinit var Peso: EditText
    private lateinit var Altezza: EditText
    private lateinit var PalmiTesta: EditText
    private lateinit var PalmiTronco: EditText
    private lateinit var PalmiArtiSup: EditText
    private lateinit var PalmiArtiInf: EditText
    private lateinit var btnCalcolaBsa: Button
    private lateinit var tvNomePaziente: TextView
    private lateinit var tvDataVisita: TextView

    private lateinit var db: DataBaseHelper

    private var pazienteId: Int = -1
    private var visitaId: Int = -1
    private var nomePaziente: String = ""
    private var dataVisita: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bsa)

        db = DataBaseHelper(this)

        pazienteId = App.pazienteId
        visitaId = App.visitaId
        nomePaziente = App.nomePaziente
        dataVisita = App.dataVisita

        Peso = findViewById(R.id.Peso)
        Altezza = findViewById(R.id.Altezza)
        PalmiTesta = findViewById(R.id.PalmiTesta)
        PalmiTronco = findViewById(R.id.PalmiTronco)
        PalmiArtiSup = findViewById(R.id.PalmiArtiSup)
        PalmiArtiInf = findViewById(R.id.PalmiArtiInf)
        tvNomePaziente = findViewById(R.id.tvNomePaziente)
        tvDataVisita = findViewById(R.id.tvDataVisita)
        btnCalcolaBsa = findViewById(R.id.btnCalcolaBsa)

        tvNomePaziente.text = nomePaziente
        tvDataVisita.text = dataVisita

        btnCalcolaBsa.setOnClickListener {
            val pesoStr = Peso.text.toString().trim()
            val altezzaStr = Altezza.text.toString().trim()

            if (pesoStr.isEmpty() || altezzaStr.isEmpty()) {
                Toast.makeText(this, "Inserisci peso e altezza", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val peso = pesoStr.toDoubleOrNull()
            val altezza = altezzaStr.toDoubleOrNull()

            if (peso == null || altezza == null || peso <= 0 || altezza <= 0) {
                Toast.makeText(this, "Inserisci valori validi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // validazione palmi
            val testa = PalmiTesta.text.toString().trim().toDoubleOrNull() ?: 0.0
            val tronco = PalmiTronco.text.toString().trim().toDoubleOrNull() ?: 0.0
            val artiSup = PalmiArtiSup.text.toString().trim().toDoubleOrNull() ?: 0.0
            val artiInf = PalmiArtiInf.text.toString().trim().toDoubleOrNull() ?: 0.0

            if (testa > 10) {
                Toast.makeText(this, "Testa: massimo 10 palmi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (tronco > 30) {
                Toast.makeText(this, "Tronco: massimo 30 palmi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (artiSup > 20) {
                Toast.makeText(this, "Arti superiori: massimo 20 palmi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (artiInf > 40) {
                Toast.makeText(this, "Arti inferiori: massimo 40 palmi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // calcolo BSA totale con formula di Mosteller
            val bsaTotaleM2 = Math.sqrt((peso * altezza) / 3600.0)
            val bsaTotaleCm2 = bsaTotaleM2 * 10000
            val palmoSingolo = bsaTotaleCm2 / 100

            // palmi totali interessati
            val palmiTotali = testa + tronco + artiSup + artiInf

            // BSA interessata
            val bsa = if (bsaTotaleCm2 > 0) (palmiTotali*palmoSingolo / bsaTotaleCm2) * 100 else 0.0
            val bsaRounded = String.format("%.2f", bsa).toDouble()

            //superificie di bsa
            val supBsa = palmoSingolo * palmiTotali

            // classe
            val classe = when {
                bsa <= 10.0 -> "Lieve"
                bsa <= 20.0 -> "Moderata"
                else -> "Grave"
            }
            db.aggiornaBsa(visitaId, bsaRounded)

            //apri pagina result
            val intent = Intent(this, activity_bsa_result::class.java)
            intent.putExtra("bsa", bsaRounded)
            intent.putExtra("supBsa", supBsa)
            intent.putExtra("bsa_class", classe)
            startActivity(intent)

        }
    }
}
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
        tvNomePaziente = findViewById(R.id.tvNomePaziente)
        tvDataVisita = findViewById(R.id.tvDataVisita)
        btnCalcolaBsa = findViewById(R.id.btnCalcolaBsa)

        tvNomePaziente.text = nomePaziente
        tvDataVisita.text = dataVisita

        btnCalcolaBsa.setOnClickListener {

            //string per controllo compilazione
            val pesoStr = Peso.text.toString().trim()
            val altezzaStr = Altezza.text.toString().trim()
            if(pesoStr.isEmpty() || altezzaStr.isEmpty()){
                Toast.makeText(
                    this, "Compila tutti i campi", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //controllo inserimento corretto
            val peso = pesoStr.toDoubleOrNull()
            val altezza = altezzaStr.toDoubleOrNull()
            if(peso == null || altezza == null || peso <= 0 || altezza <= 0){
                Toast.makeText(
                    this, "Compila tutti i campi", Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            //calcolo
            val atimesw = altezza * peso
            val bsa = Math.sqrt(atimesw/3600)

            //salvataggio del bmi arrotondato ai cents
            val bsaRounded = String.format("%.2f", bsa).toDouble()
            db.aggiornaBsa(visitaId, bsaRounded)

            //apri pagina result
            val intent = Intent(this, activity_bsa_result::class.java)
            intent.putExtra("bsa", bsaRounded)
            startActivity(intent)

        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
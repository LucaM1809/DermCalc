package com.morosini.dermcalc

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var Nome: EditText
    private lateinit var DDN: EditText
    private lateinit var CodFis: EditText
    private lateinit var btnSalvaPaziente: Button
    private lateinit var btnPasi: androidx.appcompat.widget.AppCompatButton
    private lateinit var btnEasi: androidx.appcompat.widget.AppCompatButton
    private lateinit var btnBmi: androidx.appcompat.widget.AppCompatButton
    private lateinit var btnBsa: androidx.appcompat.widget.AppCompatButton
    private lateinit var btnReport: Button
    private lateinit var btnNavVisita: Button
    private lateinit var btnNavPazienti: Button
    private lateinit var CodFisEsistente: EditText
    private lateinit var btnCercaPaziente: Button
    private lateinit var btnNuovaVisita: Button

    private lateinit var db: DataBaseHelper
    private var pazienteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DataBaseHelper(this)

        // collegamento viste
        Nome = findViewById(R.id.Nome)
        DDN = findViewById(R.id.DDN)
        CodFis = findViewById(R.id.CodFis)
        btnSalvaPaziente = findViewById(R.id.SalvaPaziente)
        btnPasi = findViewById(R.id.btnPasi)
        btnEasi = findViewById(R.id.btnEasi)
        btnBmi = findViewById(R.id.btnBmi)
        btnBsa = findViewById(R.id.btnBsa)
        btnReport = findViewById(R.id.btnReport)
        btnNavVisita = findViewById(R.id.btnNavVisita)
        btnNavPazienti = findViewById(R.id.btnNavPazienti)
        CodFisEsistente = findViewById(R.id.CodFisEsistente)
        btnCercaPaziente = findViewById(R.id.btnCercaPaziente)
        btnNuovaVisita = findViewById(R.id.btnNuovaVisita)


        btnNuovaVisita.setOnClickListener {
            App.pazienteId = -1
            App.visitaId = -1
            App.nomePaziente = ""
            App.dataNascita = ""
            App.codiceFiscale = ""
            App.dataVisita = ""
            App.sessioneAttiva = false
            App.salva(this)
            Nome.setText("")
            DDN.setText("")
            CodFis.setText("")
            CodFisEsistente.setText("")
            abilitaBottoni(false)
            Toast.makeText(this, "Nuova visita avviata", Toast.LENGTH_SHORT).show()
        }

        btnCercaPaziente.setOnClickListener {
            val cf = CodFisEsistente.text.toString().trim().uppercase()

            if (cf.length != 16) {
                Toast.makeText(this, "Codice fiscale non valido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paziente = db.getPazienteByCF(cf)
            if (paziente != null) {
                App.pazienteId = paziente.first
                App.nomePaziente = paziente.second
                App.dataNascita = paziente.third
                App.codiceFiscale = cf
                App.dataVisita = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                App.visitaId = db.creaVisita(App.pazienteId, App.dataVisita).toInt()
                App.sessioneAttiva = true
                App.salva(this)
                Nome.setText(App.nomePaziente)
                DDN.setText(App.dataNascita)
                CodFis.setText(App.codiceFiscale)
                abilitaBottoni(true)
                Toast.makeText(this, "Paziente trovato: ${App.nomePaziente}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Paziente non trovato", Toast.LENGTH_SHORT).show()
            }
        }

        // click sulla data — apre DatePickerDialog
        DDN.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, anno, mese, giorno ->
                DDN.setText("$giorno/${mese + 1}/$anno")
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // salva paziente
        btnSalvaPaziente.setOnClickListener {
            val nome = Nome.text.toString().trim()
            val dataNascita = DDN.text.toString().trim()
            val codiceFiscale = CodFis.text.toString().trim().uppercase()

            if (nome.isEmpty() || dataNascita.isEmpty() || codiceFiscale.isEmpty()) {
                Toast.makeText(this, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (codiceFiscale.length != 16) {
                Toast.makeText(this, "Codice fiscale non valido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = db.inserisciPaziente(nome, dataNascita, codiceFiscale)
            if (id != -1L) {
                App.pazienteId = id.toInt()
                App.nomePaziente = nome
                App.dataNascita = dataNascita
                App.codiceFiscale = codiceFiscale
                App.dataVisita = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                App.visitaId = db.creaVisita(App.pazienteId, App.dataVisita).toInt()
                App.sessioneAttiva = true
                App.salva(this)
                abilitaBottoni(true)
                Toast.makeText(this, "Paziente salvato!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }
        }

        // bottoni indici
        btnPasi.setOnClickListener {
            val intent = Intent(this, PasiActivity::class.java)
            startActivity(intent)
        }

        btnEasi.setOnClickListener {
            val intent = Intent(this, EasiActivity::class.java)
            startActivity(intent)
        }

        btnBmi.setOnClickListener {
            val intent = Intent(this, BmiActivity::class.java)
            startActivity(intent)
        }

        btnBsa.setOnClickListener {
            val intent = Intent(this, BsaActivity::class.java)
            startActivity(intent)
        }

        btnReport.setOnClickListener {
            val intent = Intent(this, ReportActivity::class.java)
            startActivity(intent)
        }

        btnNavPazienti.setOnClickListener {
            val intent = Intent(this, PatientActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        App.carica(this)
        if (App.pazienteId != -1 && App.sessioneAttiva) {
            Nome.setText(App.nomePaziente)
            DDN.setText(App.dataNascita)
            CodFis.setText(App.codiceFiscale)
            abilitaBottoni(true)
            btnSalvaPaziente.isEnabled = false
        } else {
            Nome.setText("")
            DDN.setText("")
            CodFis.setText("")
            abilitaBottoni(false)
            btnSalvaPaziente.isEnabled = true
        }
    }

    private fun abilitaBottoni(abilita: Boolean) {
        btnPasi.isEnabled = abilita
        btnEasi.isEnabled = abilita
        btnBmi.isEnabled = abilita
        btnBsa.isEnabled = abilita
        btnReport.isEnabled = abilita
    }
}
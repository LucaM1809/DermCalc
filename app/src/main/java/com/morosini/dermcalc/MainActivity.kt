package com.morosini.dermcalc

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var Nome: EditText
    private lateinit var DDN: EditText
    private lateinit var CodFis: EditText
    private lateinit var btnSalvaPaziente: Button
    private lateinit var btnPasi: Button
    private lateinit var btnEasi: Button
    private lateinit var btnBmi: Button
    private lateinit var btnBsa: Button
    private lateinit var btnReport: Button
    private lateinit var btnNavVisita: Button
    private lateinit var btnNavPazienti: Button

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

        // i bottoni indici e report sono disabilitati finché non si salva il paziente
        abilitaBottoni(false)

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
            val codiceFiscale = CodFis.text.toString().trim()

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
                pazienteId = id.toInt()
                abilitaBottoni(true)
                Toast.makeText(this, "Paziente salvato!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Errore nel salvataggio", Toast.LENGTH_SHORT).show()
            }
        }

        // bottoni indici
        btnPasi.setOnClickListener {
            if (pazienteId == -1) return@setOnClickListener
            val intent = Intent(this, PasiActivity::class.java)
            intent.putExtra("paziente_id", pazienteId)
            intent.putExtra("nome:paziente", Nome.text.toString())
            intent.putExtra("data_visita", java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()))
            startActivity(intent)
        }

        btnEasi.setOnClickListener {
            if (pazienteId == -1) return@setOnClickListener
            val intent = Intent(this, EasiActivity::class.java)
            intent.putExtra("paziente_id", pazienteId)
            intent.putExtra("nome:paziente", Nome.text.toString())
            intent.putExtra("data_visita", java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()))
            startActivity(intent)
        }

        btnBmi.setOnClickListener {
            if (pazienteId == -1) return@setOnClickListener
            val intent = Intent(this, BmiActivity::class.java)
            intent.putExtra("paziente_id", pazienteId)
            intent.putExtra("nome:paziente", Nome.text.toString())
            intent.putExtra("data_visita", java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()))
            startActivity(intent)
        }

        btnBsa.setOnClickListener {
            if (pazienteId == -1) return@setOnClickListener
            val intent = Intent(this, BsaActivity::class.java)
            intent.putExtra("paziente_id", pazienteId)
            intent.putExtra("nome:paziente", Nome.text.toString())
            intent.putExtra("data_visita", java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()))
            startActivity(intent)
        }

        // report
        btnReport.setOnClickListener {
            if (pazienteId == -1) {
                Toast.makeText(this, "Salva prima un paziente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this, ReportActivity::class.java)
            intent.putExtra("paziente_id", pazienteId)
            startActivity(intent)
        }

        // navbar
        btnNavPazienti.setOnClickListener {
            val intent = Intent(this, PatientActivity::class.java)
            startActivity(intent)
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
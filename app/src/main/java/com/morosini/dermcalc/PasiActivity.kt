package com.morosini.dermcalc

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import android.content.Intent

class PasiActivity : AppCompatActivity() {

    private lateinit var rgDistretto: RadioGroup
    private lateinit var rgEritema: RadioGroup
    private lateinit var rgIndurimento: RadioGroup
    private lateinit var rgDesquamazione: RadioGroup
    private lateinit var Area: EditText
    private lateinit var btnSalvaDistretto: Button
    private lateinit var btnCalcolaPasi: Button
    private lateinit var tvNomePaziente: TextView
    private lateinit var tvDataVisita: TextView


    //salvataggio dati per distretto
    private val distretti = mutableMapOf<String, IntArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasi)


        rgDistretto = findViewById(R.id.rgDistretto)
        rgEritema = findViewById(R.id.rgEritema)
        rgIndurimento = findViewById(R.id.rgIndurimento)
        rgDesquamazione = findViewById(R.id.rgDesquamazione)
        Area = findViewById(R.id.Area)
        btnSalvaDistretto = findViewById(R.id.btnSalvaDistretto)
        btnCalcolaPasi = findViewById(R.id.btnCalcolaPasi)
        tvNomePaziente = findViewById(R.id.tvNomePaziente)
        tvDataVisita = findViewById(R.id.tvDataVisita)

        tvNomePaziente.text  = App.nomePaziente
        tvDataVisita.text = App.dataVisita

        btnSalvaDistretto.setOnClickListener {
            if(!validArea()) return@setOnClickListener
            salvaDistrettoCorrente()
            azzeraInput()
            Toast.makeText(this, "Distretto salvato!", Toast.LENGTH_SHORT).show()
        }

        btnCalcolaPasi.setOnClickListener {
            if(!validArea()) return@setOnClickListener
            salvaDistrettoCorrente()

            val pasi = calcolaPasi()
            val classe = when{
                pasi <= 10.0 -> "lieve"
                pasi < 20.0 -> "moderata"
                else -> "grave"
            }

            val pasiRounded = String.format("%.2f", pasi).toDouble()
            DataBaseHelper(this).aggiornaPasi(App.visitaId, pasiRounded)

            val intent = Intent(this, activity_pasi_result::class.java)
            intent.putExtra("pasi", pasiRounded)
            intent.putExtra("pasi_class", classe)
            startActivity(intent)
        }
    }

    private fun validArea(): Boolean{
        val areaStr = Area.text.toString().trim()
        if(areaStr.isEmpty()){ //area non inserita = 0
            return true
        }
        val area = areaStr.toIntOrNull()
        if (area == null || area < 0 || area > 100) {
            Toast.makeText(this, "L'area deve essere un valore tra 0 e 100", Toast.LENGTH_SHORT).show()
            Area.requestFocus()
            return false
        }
        return true
    }

    private fun getDistrettoCorrente(): String{
        return when (rgDistretto.checkedRadioButtonId){
            R.id.rbTesta -> "H"
            R.id.rbArtiSup -> "U"
            R.id.rbTronco -> "T"
            R.id.rbArtiInf -> "L"
            else -> "H"
        }
    }

    private fun getValoreRadioGroup(rg: RadioGroup): Int {
        return when (rg.checkedRadioButtonId) {
            R.id.rbE0, R.id.rbI0, R.id.rbD0 -> 0
            R.id.rbE1, R.id.rbI1, R.id.rbD1 -> 1
            R.id.rbE2, R.id.rbI2, R.id.rbD2 -> 2
            R.id.rbE3, R.id.rbI3, R.id.rbD3 -> 3
            R.id.rbE4, R.id.rbI4, R.id.rbD4 -> 4
            else -> 0
        }
    }

    private fun getAreaValore(): Int {
        val area = Area.text.toString().trim().toIntOrNull() ?: 0
        return when {
            area == 0 -> 0
            area < 10 -> 1
            area < 30 -> 2
            area < 50 -> 3
            area < 70 -> 4
            area < 90 -> 5
            else -> 6
        }
    }

    private fun salvaDistrettoCorrente() {
        val distretto = getDistrettoCorrente()
        val E = getValoreRadioGroup(rgEritema)
        val I = getValoreRadioGroup(rgIndurimento)
        val D = getValoreRadioGroup(rgDesquamazione)
        val A = getAreaValore()
        distretti[distretto] = intArrayOf(E, I, D, A)
    }

    private fun azzeraInput() {
        rgEritema.check(R.id.rbE0)
        rgIndurimento.check(R.id.rbI0)
        rgDesquamazione.check(R.id.rbD0)
        Area.setText("")
    }

    private fun calcolaPasi(): Double {
        val H = distretti["H"] ?: intArrayOf(0, 0, 0, 0)
        val U = distretti["U"] ?: intArrayOf(0, 0, 0, 0)
        val T = distretti["T"] ?: intArrayOf(0, 0, 0, 0)
        val L = distretti["L"] ?: intArrayOf(0, 0, 0, 0)

        return 0.1 * (H[0] + H[1] + H[2]) * H[3] +
                0.2 * (U[0] + U[1] + U[2]) * U[3] +
                0.3 * (T[0] + T[1] + T[2]) * T[3] +
                0.4 * (L[0] + L[1] + L[2]) * L[3]
    }
}
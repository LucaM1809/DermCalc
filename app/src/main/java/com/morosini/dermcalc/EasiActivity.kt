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

class EasiActivity : AppCompatActivity() {

    private lateinit var rgDistretto: RadioGroup
    private lateinit var rgEritema: RadioGroup
    private lateinit var rgEdema: RadioGroup
    private lateinit var rgEscoriazione: RadioGroup
    private lateinit var rgLichen: RadioGroup
    private lateinit var Area: EditText
    private lateinit var btnSalvaDistretto: Button
    private lateinit var btnCalcolaEasi: Button
    private lateinit var tvNomePaziente: TextView
    private lateinit var tvDataVisita: TextView


    //salvataggio dati per distretto
    private val distretti = mutableMapOf<String, IntArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easi)


        rgDistretto = findViewById(R.id.rgDistretto)
        rgEritema = findViewById(R.id.rgEritema)
        rgEdema = findViewById(R.id.rgEdema)
        rgEscoriazione = findViewById(R.id.rgEscoriazione)
        rgLichen = findViewById(R.id.rgLichen)
        Area = findViewById(R.id.Area)
        btnSalvaDistretto = findViewById(R.id.btnSalvaDistretto)
        btnCalcolaEasi = findViewById(R.id.btnCalcolaEasi)
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

        btnCalcolaEasi.setOnClickListener {
            if(!validArea()) return@setOnClickListener
            salvaDistrettoCorrente()

            val easi = calcolaEasi()
            val classe = when{
                easi == 0.0 -> "Non rilevata"
                easi <= 1.0 -> "quasi irrilevante"
                easi <= 7.0 -> "lieve"
                easi <= 21.0 -> "moderata"
                easi <= 50.0 -> "Grave"
                else -> "Molto grave"
            }

            val easiRounded = String.format("%.2f", easi).toDouble()
            DataBaseHelper(this).aggiornaEasi(App.visitaId, easiRounded)

            val intent = Intent(this, activity_easi_result::class.java)
            intent.putExtra("easi", easiRounded)
            intent.putExtra("easi_class", classe)
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
            R.id.rbEr0, R.id.rbEd0, R.id.rbEx0, R.id.rbL0 -> 0
            R.id.rbEr1, R.id.rbEd1, R.id.rbEx1, R.id.rbL1 -> 1
            R.id.rbEr2, R.id.rbEd2, R.id.rbEx2, R.id.rbL2 -> 2
            R.id.rbEr3, R.id.rbEd3, R.id.rbEx3, R.id.rbL3 -> 3
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
        val Er = getValoreRadioGroup(rgEritema)
        val Ed = getValoreRadioGroup(rgEdema)
        val Ex = getValoreRadioGroup(rgEscoriazione)
        val L = getValoreRadioGroup(rgLichen)
        val A = getAreaValore()
        distretti[distretto] = intArrayOf(Er, Ed, Ex, L, A)
    }

    private fun azzeraInput() {
        rgEritema.check(R.id.rbEr0)
        rgEdema.check(R.id.rbEd0)
        rgEscoriazione.check(R.id.rbEx0)
        rgLichen.check(R.id.rbL0)
        Area.setText("")
    }

    private fun calcolaEasi(): Double {
        val H = distretti["H"] ?: intArrayOf(0, 0, 0, 0, 0)
        val U = distretti["U"] ?: intArrayOf(0, 0, 0, 0, 0)
        val T = distretti["T"] ?: intArrayOf(0, 0, 0, 0, 0)
        val L = distretti["L"] ?: intArrayOf(0, 0, 0, 0, 0)

        return 0.1 * (H[0] + H[1] + H[2] + H[3]) * H[4] +
                0.2 * (U[0] + U[1] + U[2] + U[3]) * U[4] +
                0.3 * (T[0] + T[1] + T[2] + T[3]) * T[4] +
                0.4 * (L[0] + L[1] + L[2] + L[3]) * L[4]
    }
}
package com.morosini.dermcalc

import android.app.Application
import android.content.Context

class App: Application() {

    companion object {
        var pazienteId: Int = -1
        var visitaId: Int = -1
        var nomePaziente: String = ""
        var dataNascita: String = ""
        var codiceFiscale: String = ""
        var dataVisita: String = ""
        var sessioneAttiva: Boolean = false

        fun salva(context: Context) {
            val prefs = context.getSharedPreferences("dermcalc", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putInt("pazienteId", pazienteId)
                putInt("visitaId", visitaId)
                putString("nomePaziente", nomePaziente)
                putString("dataNascita", dataNascita)
                putString("codiceFiscale", codiceFiscale)
                putString("dataVisita", dataVisita)
                putBoolean("sessioneAttiva", sessioneAttiva)
                apply()
            }
        }

        fun carica(context: Context) {
            val prefs = context.getSharedPreferences("dermcalc", Context.MODE_PRIVATE)
            pazienteId = prefs.getInt("pazienteId", -1)
            visitaId = prefs.getInt("visitaId", -1)
            nomePaziente = prefs.getString("nomePaziente", "") ?: ""
            dataNascita = prefs.getString("dataNascita", "") ?: ""
            codiceFiscale = prefs.getString("codiceFiscale", "") ?: ""
            dataVisita = prefs.getString("dataVisita", "") ?: ""
            sessioneAttiva = prefs.getBoolean("sessioneAttiva", false)
        }
    }

}
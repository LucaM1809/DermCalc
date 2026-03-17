package com.morosini.dermcalc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DataBaseHelper(context: Context): SQLiteOpenHelper(context, "dermcalc.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase){
        db.execSQL("""
            CREATE TABLE Pazienti(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            data_nascita TEXT NOT NULL,
            codice_fiscale TEXT NOT NULL
            )
        """.trimIndent())
        db.execSQL("""
            CREATE TABLE Visite(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            paziente_id INTEGER NOT NULL,
            data_visita TEXT NOT NULL,
            pasi REAL,
            easi REAL,
            bmi REAL,
            bsa REAL,
            FOREIGN KEY(paziente_id) REFERENCES pazienti(id)
            )
        """.trimIndent())
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pazienti")
        db.execSQL("DROP TABLE IF EXISTS visite")
        onCreate(db)
    }

    // nuovo paziente
    fun inserisciPaziente(nome: String, dataNascita: String, codiceFiscale: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", nome)
            put("data_nascita", dataNascita)
            put("codice_fiscale", codiceFiscale)
        }
        return db.insert("pazienti", null, values)
    }

    // get pazienti
    fun getPazienti(): List<Triple<Int, String, String>> {
        val lista = mutableListOf<Triple<Int, String, String>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT id, nome, data_nascita FROM pazienti", null)
        while (cursor.moveToNext()) {
            lista.add(Triple(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2)
            ))
        }
        cursor.close()
        return lista
    }

    // Salva
    fun inserisciVisita(
        pazienteId: Int,
        dataVisita: String,
        pasi: Double?,
        easi: Double?,
        bmi: Double?,
        bsa: Double?
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("paziente_id", pazienteId)
            put("data_visita", dataVisita)
            pasi?.let { put("pasi", it) }
            easi?.let { put("easi", it) }
            bmi?.let { put("bmi", it) }
            bsa?.let { put("bsa", it) }
        }
        return db.insert("visite", null, values)
    }
}
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
    // crea visita vuota e restituisce l'id
    fun creaVisita(pazienteId: Int, dataVisita: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("paziente_id", pazienteId)
            put("data_visita", dataVisita)
        }
        return db.insert("Visite", null, values)
    }

    // aggiorna singoli indici
    fun aggiornaBmi(visitaId: Int, bmi: Double) {
        val db = writableDatabase
        val values = ContentValues().apply { put("bmi", bmi) }
        db.update("Visite", values, "id = ?", arrayOf(visitaId.toString()))
    }

    fun aggiornaPasi(visitaId: Int, pasi: Double) {
        val db = writableDatabase
        val values = ContentValues().apply { put("pasi", pasi) }
        db.update("Visite", values, "id = ?", arrayOf(visitaId.toString()))
    }

    fun aggiornaEasi(visitaId: Int, easi: Double) {
        val db = writableDatabase
        val values = ContentValues().apply { put("easi", easi) }
        db.update("Visite", values, "id = ?", arrayOf(visitaId.toString()))
    }

    fun aggiornaBsa(visitaId: Int, bsa: Double) {
        val db = writableDatabase
        val values = ContentValues().apply { put("bsa", bsa) }
        db.update("Visite", values, "id = ?", arrayOf(visitaId.toString()))
    }

    // get visite di un paziente ordinate per data
    fun getVisitePaziente(pazienteId: Int): List<Map<String, Any?>> {
        val lista = mutableListOf<Map<String, Any?>>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Visite WHERE paziente_id = ? ORDER BY data_visita ASC",
            arrayOf(pazienteId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(mapOf(
                "id" to cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                "data_visita" to cursor.getString(cursor.getColumnIndexOrThrow("data_visita")),
                "pasi" to if (cursor.isNull(cursor.getColumnIndexOrThrow("pasi"))) null else cursor.getDouble(cursor.getColumnIndexOrThrow("pasi")),
                "easi" to if (cursor.isNull(cursor.getColumnIndexOrThrow("easi"))) null else cursor.getDouble(cursor.getColumnIndexOrThrow("easi")),
                "bmi" to if (cursor.isNull(cursor.getColumnIndexOrThrow("bmi"))) null else cursor.getDouble(cursor.getColumnIndexOrThrow("bmi")),
                "bsa" to if (cursor.isNull(cursor.getColumnIndexOrThrow("bsa"))) null else cursor.getDouble(cursor.getColumnIndexOrThrow("bsa"))
            ))
        }
        cursor.close()
        return lista
    }
}
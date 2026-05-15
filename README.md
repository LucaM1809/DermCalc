# DermCalc
Applicazione Android per il calcolo di indici dermatologici

## Descrizione
Il progetto simula lo sviluppo di un'applicazione medica

## Indici calcolati
**PASI** - Psoriasis Area and Severity Index

**EASI** - Eczema Area and Severity Index

**BMI** - Body Mass Index

**BSA** - Body Surface Area

## Architettura

- **Linguaggio**: Kotlin
- **Database**: SQLite nativo tramite `SQLiteOpenHelper`
- **Persistenza sessione**: `SharedPreferences`
- **UI**: `ConstraintLayout`, `AppCompatButton`, `ScrollView`, drawable XML personalizzati
- **Navigazione**: Intent espliciti tra Activity

### Struttura database

```sql
Pazienti (id, nome, data_nascita, codice_fiscale)
Visite (id, paziente_id, data_visita, pasi, easi, bmi, bsa)
```

## Funzionalità

- Inserimento nuovo paziente con nome, data di nascita e codice fiscale
- Ricerca paziente esistente tramite codice fiscale
- Calcolo dei 4 indici con salvataggio automatico nel database
- Generazione report completo per visita con colori per grado di severità
- Storico visite per paziente
- Eliminazione paziente con cancellata a cascata delle visite associate

## Palette colori

| Indice | Colore |
|--------|--------|
| PASI | `#1C3A5E` |
| EASI | `#639922` |
| BMI | `#EF9F27` |
| BSA | `#D4537E` |

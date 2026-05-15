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

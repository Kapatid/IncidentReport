package com.example.incidentreport.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.incidentreport.models.IncidentReport
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashSet

class IncidentReportDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        /** 1 */
        private const val DATABASE_VERSION = 1
        /** IncidentReportDatabase */
        private const val DATABASE_NAME = "IncidentReportDatabase"
        /** IncidentReportTable */
        private const val TABLE_INCIDENT_REPORT = "IncidentReportTable"

        private const val KEY_ID = "_id"
        private const val KEY_REPORTER = "reporter"
        private const val KEY_INCIDENT_TYPE = "incidentType"
        private const val KEY_DATE_TIME = "dateTime"
        private const val KEY_LOCATION = "location"
        private const val KEY_INJURED = "injured"
        private const val KEY_MISSING = "missing"
        private const val KEY_FATALITIES = "fatalities"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_VICTIMS = "victims"
        private const val KEY_CREATED_AT = "createdAt"
        private const val KEY_UPDATED_AT = "updatedAt"
    }

    private val sdf = SimpleDateFormat(
        "EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())

    override fun onCreate(db: SQLiteDatabase?) {
        val createIncidentReportTable = (
                "CREATE TABLE " + TABLE_INCIDENT_REPORT + "("
                        + KEY_ID + " INTEGER PRIMARY KEY," + KEY_REPORTER + " TEXT,"
                        + KEY_INJURED + " TEXT," + KEY_MISSING + " TEXT,"
                        + KEY_FATALITIES + " TEXT," + KEY_INCIDENT_TYPE + " TEXT,"
                        + KEY_DATE_TIME + " TEXT," + KEY_DESCRIPTION + " TEXT,"
                        + KEY_VICTIMS + " TEXT," + KEY_LOCATION + " TEXT,"
                        + KEY_CREATED_AT + " TEXT,"+ KEY_UPDATED_AT + " TEXT" +")"
                )

        db?.execSQL(createIncidentReportTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_INCIDENT_REPORT")
        onCreate(db)
    }

    /**
     * Add [IncidentReport] to [IncidentReportDatabase].
     * @param incidentReport [IncidentReport]
     * @return success [Long]
     */
    fun addIncidentReport(incidentReport: IncidentReport): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_REPORTER, incidentReport.reporter)
        contentValues.put(KEY_INCIDENT_TYPE, incidentReport.incidentType)
        contentValues.put(KEY_DATE_TIME, incidentReport.dateTime.toString())
        contentValues.put(KEY_LOCATION, incidentReport.location)
        contentValues.put(KEY_INJURED, incidentReport.injured.toString())
        contentValues.put(KEY_MISSING, incidentReport.missing.toString())
        contentValues.put(KEY_FATALITIES, incidentReport.fatalities.toString())
        contentValues.put(KEY_DESCRIPTION, incidentReport.description)
        contentValues.put(KEY_VICTIMS, incidentReport.victims.toString())
        contentValues.put(KEY_UPDATED_AT, incidentReport.updatedAt.toString())
        contentValues.put(KEY_CREATED_AT, incidentReport.createdAt.toString())

        // Inserting IncidentReport using insert query.
        val success = db.insert(TABLE_INCIDENT_REPORT, null, contentValues)

        db.close()
        return success
    }

    /**
     * Read incidentReports from database in form of [HashSet].
     * @return incidentReports [List<IncidentReport>]
     */
    fun getIncidentReports(): List<IncidentReport> {

        val incidentReports: HashSet<IncidentReport> = HashSet()

        val selectQuery = "SELECT  * FROM $TABLE_INCIDENT_REPORT"

        val db = this.readableDatabase
        // Cursor is used to read the IncidentReports one by one. Add them to data model class.
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    incidentReports.add(
                        IncidentReport(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                            reporter = cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPORTER)),
                            incidentType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INCIDENT_TYPE)),
                            dateTime = sdf.parse(
                                cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_TIME))) as Date,
                            location = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                            description = cursor.getString(cursor.getColumnIndexOrThrow(
                                KEY_DESCRIPTION)),
                            missing = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MISSING)),
                            injured = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INJURED)),
                            fatalities = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FATALITIES)),
                            victims = cursor.getString(cursor.getColumnIndexOrThrow(KEY_VICTIMS))
                                .replace("[", "").replace("]", "")
                                .split(",").map { it.trim() },
                            updatedAt = sdf.parse(
                                cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT))),
                            createdAt = sdf.parse(
                                cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)))
                        )
                    )

                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return emptyList()
        }

        cursor.close()

        return incidentReports.sortedBy { e -> e.id }
    }

    fun getReport(id: String): IncidentReport? {

        val selectQuery = "SELECT  * FROM $TABLE_INCIDENT_REPORT WHERE _id = $id"

        val db = this.readableDatabase

        val cursor: Cursor?

        var incidentReport: IncidentReport? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)) == id) {
                        incidentReport = IncidentReport(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_ID)),
                            reporter = cursor.getString(cursor.getColumnIndexOrThrow(KEY_REPORTER)),
                            incidentType = cursor.getString(cursor.getColumnIndexOrThrow(KEY_INCIDENT_TYPE)),
                            dateTime = sdf.parse(
                                cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE_TIME))) as Date,
                            location = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                            description = cursor.getString(cursor.getColumnIndexOrThrow(
                                KEY_DESCRIPTION)),
                            missing = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_MISSING)),
                            injured = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_INJURED)),
                            fatalities = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_FATALITIES)),
                            victims = cursor.getString(cursor.getColumnIndexOrThrow(KEY_VICTIMS))
                                .replace("[", "").replace("]", "")
                                .split(",").map { it.trim() },
                            updatedAt = sdf.parse(
                                cursor.getString(cursor.getColumnIndexOrThrow(KEY_UPDATED_AT))),
                            createdAt = sdf.parse(
                                cursor.getString(cursor.getColumnIndexOrThrow(KEY_CREATED_AT)))
                        )

                        cursor.close()
                    }
                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return incidentReport
        }

        cursor.close()

        return incidentReport
    }

    /**
     * Update IncidentReport.
     * @param incidentReport IncidentReport to update.
     * @return success [Int]
     */
    fun updateIncidentReport(incidentReport: IncidentReport): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_REPORTER, incidentReport.reporter)
        contentValues.put(KEY_INCIDENT_TYPE, incidentReport.incidentType)
        contentValues.put(KEY_DATE_TIME, incidentReport.dateTime.toString())
        contentValues.put(KEY_LOCATION, incidentReport.location)
        contentValues.put(KEY_DESCRIPTION, incidentReport.description)
        contentValues.put(KEY_MISSING, incidentReport.missing.toString())
        contentValues.put(KEY_INJURED, incidentReport.injured.toString())
        contentValues.put(KEY_FATALITIES, incidentReport.fatalities.toString())
        contentValues.put(KEY_VICTIMS, incidentReport.victims.toString())
        contentValues.put(KEY_UPDATED_AT,
            SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.getDefault())
            .format(Calendar.getInstance().time))
        contentValues.put(KEY_CREATED_AT, incidentReport.createdAt.toString())

        // Update
        val success = db.update(
            TABLE_INCIDENT_REPORT,
            contentValues,
            KEY_ID + "=" + incidentReport.id,
            null
        )

        db.close()
        return success
    }

    /**
     * Delete IncidentReport.
     * @param id [Long] to delete.
     * @return success [Int]
     */
    fun deleteIncidentReport(id: Long): Int {
        val db = this.writableDatabase

        // Delete
        val success = db.delete(
            TABLE_INCIDENT_REPORT,
            "$KEY_ID=$id",
            null
        )

        db.close()
        return success
    }

    fun injuredSum(): Int {
        var sum = 0
        val selectQuery = "SELECT SUM($KEY_INJURED) FROM $TABLE_INCIDENT_REPORT"

        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    sum = cursor.getInt(0)
                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return sum
        }

        cursor.close()

        return sum
    }

    fun missingSum(): Int {
        var sum = 0
        val selectQuery = "SELECT SUM($KEY_MISSING) FROM $TABLE_INCIDENT_REPORT"

        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    sum = cursor.getInt(0)
                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return sum
        }

        cursor.close()

        return sum
    }

    fun fatalitiesSum(): Int {
        var sum = 0
        val selectQuery = "SELECT SUM($KEY_FATALITIES) FROM $TABLE_INCIDENT_REPORT"

        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    sum = cursor.getInt(0)
                } while (cursor.moveToNext())
            }

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return sum
        }

        cursor.close()

        return sum
    }

    fun todayReportSum(): Int {

        val reports = getIncidentReports()
        var todayReports = 0
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        for (report in reports) {
            if (sdf.format(report.createdAt as Date).equals(sdf.format(Calendar.getInstance().time))) {
                todayReports++
            }
        }

        return todayReports
    }

    fun latestReports(max: Int): List<IncidentReport> {

        val reports = getIncidentReports().toMutableList()
        val latestReports = emptyList<IncidentReport>().toMutableList()

        reports.sortedByDescending { it.createdAt }
        reports.reverse()

        if (max > 0) {
            for ((curr, report) in reports.withIndex()) {
                if (curr < max ) { latestReports.add(report) }
                else break
            }

            return latestReports
        } else {
            return reports
        }
    }
}
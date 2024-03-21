package com.example.travelease.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.travelease.model.ChecklistItem
import com.example.travelease.model.ItineraryDay
import com.example.travelease.model.Note
import com.example.travelease.model.Place
import com.example.travelease.model.Trip
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TravelDatabase"

        // Table Names
        private const val TABLE_NOTE = "note"
        private const val TABLE_CHECKLIST_ITEM = "checklistItem"
        private const val TABLE_PLACE = "place"
        private const val TABLE_ITINERARY_DAY = "itineraryDay"
        private const val TABLE_TRIP = "trip"

        // Common column names
        private const val KEY_ID = "id"
        private const val KEY_TRIP_ID = "tripId"

        // NOTE Table - column names
        private const val KEY_DESCRIPTION = "description"

        // CHECKLISTITEM Table - column names
        private const val KEY_ORDER = "orderNo"
        private const val KEY_TITLE = "title"
        private const val KEY_IS_COMPLETED = "isCompleted"

        // PLACE Table - column names
        private const val KEY_ITINERARY_DAY_ID = "itineraryDayId"
        private const val KEY_ADDRESS = "address"
        private const val KEY_PHOTO_METADATA = "photoMetadata"

        // ITINERARYDAY and TRIP Table - additional column names
        private const val KEY_DATE = "date"
        private const val KEY_USER_ID = "userId"
        private const val KEY_START_DATE = "startDate"
        private const val KEY_END_DATE = "endDate"
        private const val KEY_PLACE_ID = "placeId"


        // Table Create Statements
        private const val CREATE_TABLE_NOTE = """
            CREATE TABLE $TABLE_NOTE (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_TRIP_ID INTEGER,
                $KEY_DESCRIPTION TEXT
            )
        """

        private const val CREATE_TABLE_CHECKLIST_ITEM = """
            CREATE TABLE $TABLE_CHECKLIST_ITEM (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_TRIP_ID INTEGER,
                $KEY_TITLE TEXT,
                $KEY_IS_COMPLETED INTEGER
            )
        """

        private const val CREATE_TABLE_PLACE = """
    CREATE TABLE $TABLE_PLACE (
        $KEY_ID TEXT PRIMARY KEY,
        $KEY_ITINERARY_DAY_ID INTEGER,
        $KEY_TITLE TEXT,
        $KEY_ADDRESS TEXT
    )
"""
        // Assuming JSON string for PhotoMetadata list

        private const val CREATE_TABLE_ITINERARY_DAY = """
    CREATE TABLE $TABLE_ITINERARY_DAY (
        $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $KEY_TRIP_ID INTEGER,
        $KEY_DATE TEXT
    )
"""

        private const val CREATE_TABLE_TRIP = """
    CREATE TABLE $TABLE_TRIP (
        $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $KEY_USER_ID INTEGER,
        $KEY_PLACE_ID TEXT,
        $KEY_TITLE TEXT,
        $KEY_START_DATE TEXT,
        $KEY_END_DATE TEXT
    )
""" }


    override fun onCreate(db: SQLiteDatabase) {
        // creating required tables
        db.execSQL(CREATE_TABLE_NOTE)
        db.execSQL(CREATE_TABLE_CHECKLIST_ITEM)
        db.execSQL(CREATE_TABLE_PLACE)
        db.execSQL(CREATE_TABLE_ITINERARY_DAY)
        db.execSQL(CREATE_TABLE_TRIP)
        // Execute CREATE TABLE statements for other tables
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHECKLIST_ITEM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLACE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ITINERARY_DAY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRIP")

        onCreate(db)
    }



    fun addNote(note: Note) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_TRIP_ID, note.tripId)
        values.put(KEY_DESCRIPTION, note.description)

        // insert row
        db.insert(TABLE_NOTE, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getNote(id: Int): Note? {
        val db = this.readableDatabase

        val selectQuery = "SELECT * FROM $TABLE_NOTE WHERE $KEY_ID = $id"
        val cursor = db.rawQuery(selectQuery, null)

        var note: Note? = null
        if (cursor.moveToFirst()) {
            note = Note(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
            )
        }
        cursor.close()
        return note
    }

    fun updateNote(note: Note): Int {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(KEY_TRIP_ID, note.tripId)
        values.put(KEY_DESCRIPTION, note.description)

        // updating row
        return db.update(TABLE_NOTE, values, "$KEY_ID = ?", arrayOf(note.id.toString()))
    }

    fun deleteNote(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTE, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteNoteWithTripIdAndTextAs(tripId: Int, text: String) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTE, "$KEY_TRIP_ID = ? AND $KEY_DESCRIPTION = ?", arrayOf(tripId.toString(), text))
        db.close()
    }

    @SuppressLint("Range")
    fun getNotesForTrip(tripId: Int): List<Note> {
        val notes = mutableListOf<Note>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_NOTE WHERE $KEY_TRIP_ID = $tripId"

        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val note = Note(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION))
                )
                notes.add(note)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return notes
    }




    fun addChecklistItem(checklistItem: ChecklistItem) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TRIP_ID, checklistItem.tripId)
        values.put(KEY_TITLE, checklistItem.title)
        values.put(KEY_IS_COMPLETED, if (checklistItem.isCompleted) 1 else 0)

        db.insert(TABLE_CHECKLIST_ITEM, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getChecklistItem(id: Int): ChecklistItem? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CHECKLIST_ITEM WHERE $KEY_ID = $id"
        val cursor = db.rawQuery(selectQuery, null)

        var checklistItem: ChecklistItem? = null
        if (cursor.moveToFirst()) {
            checklistItem = ChecklistItem(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getInt(cursor.getColumnIndex(KEY_IS_COMPLETED)) != 0
            )
        }
        cursor.close()
        return checklistItem
    }

    fun updateChecklistItem(checklistItem: ChecklistItem): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TRIP_ID, checklistItem.tripId)
        values.put(KEY_TITLE, checklistItem.title)
        values.put(KEY_IS_COMPLETED, if (checklistItem.isCompleted) 1 else 0)

        return db.update(TABLE_CHECKLIST_ITEM, values, "$KEY_ID = ?", arrayOf(checklistItem.id.toString()))
    }

    fun deleteChecklistItem(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_CHECKLIST_ITEM, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }
    fun deleteChecklistItemWithTripIdAndTitleAs(tripId: Int, title: String) {
        val db = this.writableDatabase
        db.delete(TABLE_CHECKLIST_ITEM, "$KEY_TRIP_ID = ? AND $KEY_TITLE = ?", arrayOf(tripId.toString(), title))
        db.close()
    }

    @SuppressLint("Range")
    fun getAllChecklistItemsForTrip(tripId: Int): List<ChecklistItem> {
        val checklistItems = mutableListOf<ChecklistItem>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_CHECKLIST_ITEM WHERE $KEY_TRIP_ID = $tripId"

        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val checklistItem = ChecklistItem(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getInt(cursor.getColumnIndex(KEY_IS_COMPLETED)) != 0
                )
                checklistItems.add(checklistItem)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return checklistItems
    }







    fun addPlace(place: Place) : Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ID, place.id)
        values.put(KEY_ITINERARY_DAY_ID, place.itineraryDayId)
        values.put(KEY_TITLE, place.title)
        values.put(KEY_ADDRESS, place.address)

        val id = db.insert(TABLE_PLACE, null, values)
        db.close()
        return id
    }

    @SuppressLint("Range")
    fun getPlace(id: String): Place? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PLACE WHERE $KEY_ID = '$id'"
        val cursor = db.rawQuery(selectQuery, null)

        var place: Place? = null
        if (cursor.moveToFirst()) {
            place = Place(
                cursor.getString(cursor.getColumnIndex(KEY_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_ITINERARY_DAY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)),
            )
        }
        cursor.close()
        return place
    }

    fun updatePlace(place: Place): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_ITINERARY_DAY_ID, place.itineraryDayId)
        values.put(KEY_TITLE, place.title)
        values.put(KEY_ADDRESS, place.address)

        return db.update(TABLE_PLACE, values, "$KEY_ID = ?", arrayOf(place.id))
    }

    fun deletePlace(id: String) {
        val db = this.writableDatabase
        db.delete(TABLE_PLACE, "$KEY_ID = ?", arrayOf(id))
        db.close()
    }

    @SuppressLint("Range")
    fun getPlacesForItineraryDay(itineraryDayId: Int): List<Place> {
        val places = mutableListOf<Place>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_PLACE WHERE $KEY_ITINERARY_DAY_ID = $itineraryDayId"

        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val place = Place(
                    cursor.getString(cursor.getColumnIndex(KEY_ID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_ITINERARY_DAY_ID)),
                    cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                    cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)),
                )
                places.add(place)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return places
    }

    fun getPlacesForTrip(tripId: Int): List<Place> {
        val places = mutableListOf<Place>()
        val itineraryDays = getItineraryDaysForTrip(tripId)

        for (itineraryDay in itineraryDays) {
            itineraryDay.id?.let { getPlacesForItineraryDay(it) }?.let { places.addAll(it) }
        }

        return places
    }





    fun addItineraryDay(itineraryDay: ItineraryDay) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TRIP_ID, itineraryDay.tripId)
        values.put(KEY_DATE, itineraryDay.date.toString())

        db.insert(TABLE_ITINERARY_DAY, null, values)
        db.close()
    }

    @SuppressLint("Range")
    fun getItineraryDay(id: Int): ItineraryDay? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ITINERARY_DAY WHERE $KEY_ID = $id"
        val cursor = db.rawQuery(selectQuery, null)

        var itineraryDay: ItineraryDay? = null
        if (cursor.moveToFirst()) {
            itineraryDay = ItineraryDay(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)),
                Date(cursor.getString(cursor.getColumnIndex(KEY_DATE))),
                null
            )
        }
        cursor.close()
        return itineraryDay
    }

    @SuppressLint("Range")
    fun getItineraryDaysForTrip(tripId: Int): List<ItineraryDay> {
        val itineraryDays = mutableListOf<ItineraryDay>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ITINERARY_DAY WHERE $KEY_TRIP_ID = $tripId"

        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val itineraryDay = ItineraryDay(
                    cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                    cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID)),
                    Date(cursor.getString(cursor.getColumnIndex(KEY_DATE))) // Convert string to Date
                )
                itineraryDays.add(itineraryDay)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return itineraryDays
    }

    @SuppressLint("Range")
    fun getItineraryDayWithPlaces(id: Int): ItineraryDay? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ITINERARY_DAY WHERE $KEY_ID = $id"
        val cursor = db.rawQuery(selectQuery, null)

        var itineraryDay: ItineraryDay? = null
        if (cursor.moveToFirst()) {
            val itineraryDayId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
            val tripId = cursor.getInt(cursor.getColumnIndex(KEY_TRIP_ID))
            val date = Date(cursor.getString(cursor.getColumnIndex(KEY_DATE))) // Convert string to Date

            val places = getPlacesForItineraryDay(itineraryDayId)

            itineraryDay = ItineraryDay(
                id = itineraryDayId,
                tripId = tripId,
                date = date,
                places = places
            )
        }
        cursor.close()
        return itineraryDay
    }

    fun updateItineraryDay(itineraryDay: ItineraryDay): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_TRIP_ID, itineraryDay.tripId)
        values.put(KEY_DATE, itineraryDay.date.toString())

        return db.update(TABLE_ITINERARY_DAY, values, "$KEY_ID = ?", arrayOf(itineraryDay.id.toString()))
    }

    fun deleteItineraryDay(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_ITINERARY_DAY, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getItineraryDaysWithPlacesForTrip(tripId: Int): List<ItineraryDay> {
        val itineraryDays = mutableListOf<ItineraryDay>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_ITINERARY_DAY WHERE $KEY_TRIP_ID = $tripId"

        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val itineraryDayId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val date = Date(cursor.getString(cursor.getColumnIndex(KEY_DATE))) // Convert string to Date

                // Fetch Places for each Itinerary Day
                val places = getPlacesForItineraryDay(itineraryDayId)

                val itineraryDay = ItineraryDay(
                    id = itineraryDayId,
                    tripId = tripId,
                    date = date,
                    places = places
                )
                itineraryDays.add(itineraryDay)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return itineraryDays
    }






    @SuppressLint("SimpleDateFormat")
    fun addTrip(trip: Trip) : Long {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_USER_ID, trip.userId)
        values.put(KEY_TITLE, trip.title)
        values.put(KEY_PLACE_ID, trip.placeId)
        values.put(KEY_START_DATE, trip.startDate)
        values.put(KEY_END_DATE, trip.endDate)

        val id = db.insert(TABLE_TRIP, null, values)

        // Parse the start and end dates
        val startDate = SimpleDateFormat("yyyy-MM-dd").parse(trip.startDate)
        val endDate = SimpleDateFormat("yyyy-MM-dd").parse(trip.endDate)
        val calendar = Calendar.getInstance()

        // Iterate from the start date to the end date
        if (startDate != null) {
            calendar.time = startDate
            while (!calendar.time.after(endDate)) {

                val itineraryDay = ItineraryDay(null, id.toInt(), calendar.time)
                addItineraryDay(itineraryDay)
                calendar.add(Calendar.DATE, 1)
            }
        }

        db.close()

        return id
    }

    @SuppressLint("Range")
    fun getTrip(id: Int): Trip? {
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_TRIP WHERE $KEY_ID = $id"
        val cursor = db.rawQuery(selectQuery, null)

        var trip: Trip? = null
        if (cursor.moveToFirst()) {
            trip = Trip(
                cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_USER_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_PLACE_ID)),
                cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                cursor.getString(cursor.getColumnIndex(KEY_START_DATE)),
                cursor.getString(cursor.getColumnIndex(KEY_END_DATE))
            )
        }
        cursor.close()
        return trip
    }

    @SuppressLint("Range")
    fun getTripWithDetails(tripId: Int): Trip? {
        val db = this.readableDatabase

        // Fetch Trip
        val tripSelectQuery = "SELECT * FROM $TABLE_TRIP WHERE $KEY_ID = $tripId"
        val tripCursor = db.rawQuery(tripSelectQuery, null)

        var trip: Trip? = null
        if (tripCursor.moveToFirst()) {
            val id = tripCursor.getInt(tripCursor.getColumnIndex(KEY_ID))
            val userId = tripCursor.getString(tripCursor.getColumnIndex(KEY_USER_ID))
            val placeId = tripCursor.getString(tripCursor.getColumnIndex(KEY_PLACE_ID))
            val title = tripCursor.getString(tripCursor.getColumnIndex(KEY_TITLE))
            val startDate = tripCursor.getString(tripCursor.getColumnIndex(KEY_START_DATE)) // Convert string to Date
            val endDate = tripCursor.getString(tripCursor.getColumnIndex(KEY_END_DATE))
            // Fetch Notes
            val notes = getNotesForTrip(tripId)

            // Fetch Checklist Items
            val checklists = getAllChecklistItemsForTrip(tripId)

            // Fetch Itinerary Days with Places (and their PhotoMetadata)
            val itineraryDays = getItineraryDaysWithPlacesForTrip(tripId)

            trip = Trip(
                id = id,
                userId = userId,
                placeId = placeId,
                title = title,
                startDate = startDate,
                endDate = endDate,
                checkLists = checklists,
                notes = notes,
                itineraryDays = itineraryDays
            )
        }
        tripCursor.close()
        return trip
    }

    fun updateTrip(trip: Trip): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(KEY_PLACE_ID, trip.placeId)
        values.put(KEY_USER_ID, trip.userId)
        values.put(KEY_TITLE, trip.title)
        values.put(KEY_START_DATE, trip.startDate.toString())
        values.put(KEY_END_DATE, trip.endDate.toString())

        return db.update(TABLE_TRIP, values, "$KEY_ID = ?", arrayOf(trip.id.toString()))
    }

    fun deleteTrip(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TRIP, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    @SuppressLint("Range")
    fun getTripsForUser(userId: String): List<Trip> {
        val trips = mutableListOf<Trip>()
        val db = this.readableDatabase
        val selectQuery = "SELECT * FROM $TABLE_TRIP WHERE $KEY_USER_ID = '$userId'"

        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val tripId = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                val trip = getTripWithDetails(tripId)
                trip?.let { trips.add(it) }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return trips
    }


}


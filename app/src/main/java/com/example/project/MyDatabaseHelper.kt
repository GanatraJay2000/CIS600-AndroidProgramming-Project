import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.project.models.*
import java.util.Date

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "my_database.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create your database tables and define the schema here
        // Example:

        // Create notes table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS notes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT
            )
            """
        )

        // Create checklist_items table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS checklist_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                isCompleted INTEGER
            )
            """
        )

        // Create checklists table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS checklists (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT
            )
            """
        )

        // Create places table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS places (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                imageUrl TEXT,
                description TEXT,
                latitude REAL,
                longitude REAL
            )
            """
        )

        // Create sections table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS sections (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                type TEXT
            )
            """
        )

        // Create trips table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS trips (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER,
                title TEXT,
                imageUrl TEXT,
                startDate INTEGER,
                endDate INTEGER,
                sectionId INTEGER
            )
            """
        )

        // Create itinerary_days table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS itinerary_days (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date INTEGER,
                tripId INTEGER
            )
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades here
        // Example:
        db.execSQL("DROP TABLE IF EXISTS notes")
        db.execSQL("DROP TABLE IF EXISTS checklist_items")
        db.execSQL("DROP TABLE IF EXISTS checklists")
        db.execSQL("DROP TABLE IF EXISTS places")
        db.execSQL("DROP TABLE IF EXISTS sections")
        db.execSQL("DROP TABLE IF EXISTS trips")
        db.execSQL("DROP TABLE IF EXISTS itinerary_days")
        // Drop tables for other entities here
        onCreate(db)
    }

    // Insert a new note into the "notes" table
    fun insertNote(note: Note): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", note.title)
        contentValues.put("description", note.description)
        return db.insert("notes", null, contentValues)
    }

    // Retrieve all notes from the "notes" table
    @SuppressLint("Range")
    fun getAllNotes(): List<Note> {
        val db = readableDatabase
        val notes = mutableListOf<Note>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM notes", null)
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    notes.add(Note(id, description, title))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return notes
    }

    // Insert a new checklist item into the "checklist_items" table
    fun insertChecklistItem(checklistItem: ChecklistItem): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", checklistItem.title)
        contentValues.put("isCompleted", if (checklistItem.isCompleted) 1 else 0)
        return db.insert("checklist_items", null, contentValues)
    }

    // Retrieve all checklist items from the "checklist_items" table
    @SuppressLint("Range")
    fun getAllChecklistItems(): List<ChecklistItem> {
        val db = readableDatabase
        val checklistItems = mutableListOf<ChecklistItem>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM checklist_items", null)
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE IF NOT EXISTS checklist_items (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, isCompleted INTEGER)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val isCompleted = cursor.getInt(cursor.getColumnIndex("isCompleted")) == 1
                    checklistItems.add(ChecklistItem(id, title, isCompleted))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return checklistItems
    }

    // Insert a new place into the "places" table
    fun insertPlace(place: Place): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", place.title)
        contentValues.put("imageUrl", place.imageUrl)
        contentValues.put("description", place.description)
        contentValues.put("latitude", place.latitude)
        contentValues.put("longitude", place.longitude)
        return db.insert("places", null, contentValues)
    }

    // Retrieve all places from the "places" table
    @SuppressLint("Range")
    fun getAllPlaces(): List<Place> {
        val db = readableDatabase
        val places = mutableListOf<Place>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM places", null)
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE IF NOT EXISTS places (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, imageUrl TEXT, description TEXT, latitude REAL, longitude REAL)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    val latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                    val longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                    places.add(Place(id, title, imageUrl, description, latitude, longitude))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return places
    }

    // Insert a new section into the "sections" table
    fun insertSection(section: Section): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", section.title)
        contentValues.put("type", section.type.name)
        return db.insert("sections", null, contentValues)
    }

    // Retrieve all sections from the "sections" table
    @SuppressLint("Range")
    fun getAllSections(): List<Section> {
        val db = readableDatabase
        val sections = mutableListOf<Section>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM sections", null)
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE IF NOT EXISTS sections (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, type TEXT)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val type = SectionType.valueOf(cursor.getString(cursor.getColumnIndex("type")))
                    sections.add(Section(id, title, type, emptyList()))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return sections
    }

    // Insert a new trip into the "trips" table
    fun insertTrip(trip: Trip): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("userId", trip.userId)
        contentValues.put("title", trip.title)
        contentValues.put("imageUrl", trip.imageUrl)
        contentValues.put("startDate", trip.startDate.time)
        contentValues.put("endDate", trip.endDate.time)
        contentValues.put("sectionId", trip.sections.first().id)
        return db.insert("trips", null, contentValues)
    }

    // Retrieve all trips from the "trips" table
    @SuppressLint("Range")
    fun getAllTrips(): List<Trip> {
        val db = readableDatabase
        val trips = mutableListOf<Trip>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM trips", null)
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE IF NOT EXISTS trips (id INTEGER PRIMARY KEY AUTOINCREMENT, userId INTEGER, title TEXT, imageUrl TEXT, startDate INTEGER, endDate INTEGER, sectionId INTEGER)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val userId = cursor.getInt(cursor.getColumnIndex("userId"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"))
                    val startDate = cursor.getLong(cursor.getColumnIndex("startDate"))
                    val endDate = cursor.getLong(cursor.getColumnIndex("endDate"))
                    val sectionId = cursor.getInt(cursor.getColumnIndex("sectionId"))

                    // Retrieve the sections associated with this trip
                    val sections = getAllSections().filter { it.id == sectionId }

                    // Retrieve the itinerary days associated with this trip
                    val itineraryDays = getAllItineraryDays().filter { it.tripId == id }

                    trips.add(
                        Trip(
                            userId,
                            id,
                            title,
                            imageUrl,
                            Date(startDate),
                            Date(endDate),
                            sections,
                            itineraryDays
                        )
                    )
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trips
    }

    // Insert a new itinerary day into the "itinerary_days" table
    fun insertItineraryDay(itineraryDay: ItineraryDay): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("date", itineraryDay.date.time)
        contentValues.put("tripId", itineraryDay.tripId)
        return db.insert("itinerary_days", null, contentValues)
    }

    // Retrieve all itinerary days from the "itinerary_days" table
    @SuppressLint("Range")
    fun getAllItineraryDays(): List<ItineraryDay> {
        val db = readableDatabase
        val itineraryDays = mutableListOf<ItineraryDay>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT * FROM itinerary_days", null)
        } catch (e: SQLException) {
            db.execSQL("CREATE TABLE IF NOT EXISTS itinerary_days (id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, tripId INTEGER)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val date = cursor.getLong(cursor.getColumnIndex("date"))
                    val tripId = cursor.getInt(cursor.getColumnIndex("tripId"))

                    itineraryDays.add(ItineraryDay(Date(date), tripId))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return itineraryDays
    }

    // Update an existing note in the "notes" table
    fun updateNote(note: Note): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", note.title)
        contentValues.put("description", note.description)

        return db.update("notes", contentValues, "id=?", arrayOf(note.id.toString()))
    }

    // Delete a note from the "notes" table by its ID
    fun deleteNoteById(noteId: Int): Int {
        val db = writableDatabase

        return db.delete("notes", "id=?", arrayOf(noteId.toString()))
    }
    // Update an existing checklist item in the "checklist_items" table
    fun updateChecklistItem(checklistItem: ChecklistItem): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", checklistItem.title)
        contentValues.put("isCompleted", if (checklistItem.isCompleted) 1 else 0)

        return db.update("checklist_items", contentValues, "id=?", arrayOf(checklistItem.id.toString()))
    }

    // Delete a checklist item from the "checklist_items" table by its ID
    fun deleteChecklistItemById(checklistItemId: Int): Int {
        val db = writableDatabase

        return db.delete("checklist_items", "id=?", arrayOf(checklistItemId.toString()))
    }

    // Update an existing place in the "places" table
    fun updatePlace(place: Place): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", place.title)
        contentValues.put("imageUrl", place.imageUrl)
        contentValues.put("description", place.description)
        contentValues.put("latitude", place.latitude)
        contentValues.put("longitude", place.longitude)

        return db.update("places", contentValues, "id=?", arrayOf(place.id.toString()))
    }

    // Delete a place from the "places" table by its ID
    fun deletePlaceById(placeId: Int): Int {
        val db = writableDatabase

        return db.delete("places", "id=?", arrayOf(placeId.toString()))
    }

    // Update an existing section in the "sections" table
    fun updateSection(section: Section): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("title", section.title)
        contentValues.put("type", section.type.name)

        return db.update("sections", contentValues, "id=?", arrayOf(section.id.toString()))
    }

    // Delete a section from the "sections" table by its ID
    fun deleteSectionById(sectionId: Int): Int {
        val db = writableDatabase

        return db.delete("sections", "id=?", arrayOf(sectionId.toString()))
    }
    // Update an existing trip in the "trips" table
    fun updateTrip(trip: Trip): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("userId", trip.userId)
        contentValues.put("title", trip.title)
        contentValues.put("imageUrl", trip.imageUrl)
        contentValues.put("startDate", trip.startDate.time)
        contentValues.put("endDate", trip.endDate.time)
        contentValues.put("sectionId", trip.sections.first().id)

        return db.update("trips", contentValues, "id=?", arrayOf(trip.id.toString()))
    }

    // Delete a trip from the "trips" table by its ID
    fun deleteTripById(tripId: Int): Int {
        val db = writableDatabase

        return db.delete("trips", "id=?", arrayOf(tripId.toString()))
    }
    // Delete all itinerary days associated with a trip from the "itinerary_days" table
    fun deleteItineraryDaysByTripId(tripId: Int): Int {
        val db = writableDatabase

        return db.delete("itinerary_days", "tripId=?", arrayOf(tripId.toString()))
    }

}

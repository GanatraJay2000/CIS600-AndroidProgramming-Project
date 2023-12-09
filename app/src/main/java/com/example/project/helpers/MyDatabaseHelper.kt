import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.project.models.*
import com.google.android.libraries.places.api.model.PhotoMetadata
import com.google.gson.Gson
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

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS checklist (
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
                address TEXT,
                photoMetadata TEXT
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
                title TEXT,
                imageUrl TEXT,
                startDate INTEGER,
                endDate INTEGER
            )
            """
        )



        // Create itinerary_days table
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS itinerary_days (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                date INTEGER
            )
            """
        )

        // Reference table for ItineraryDays - Trips relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS itinerary_day_trip (
        itinerary_day_id INTEGER,
        trip_id INTEGER,
        UNIQUE(itinerary_day_id, trip_id),
        FOREIGN KEY(itinerary_day_id) REFERENCES itinerary_days(id),
        FOREIGN KEY(trip_id) REFERENCES trips(id)
    )
    """
        )

        // Reference table for Sections - ItineraryDays relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS section_itinerary_day (
        section_id INTEGER,
        itinerary_day_id INTEGER,
        UNIQUE(section_id, itinerary_day_id),
        FOREIGN KEY(section_id) REFERENCES sections(id),
        FOREIGN KEY(itinerary_day_id) REFERENCES itinerary_days(id)
    )
    """
        )

        // Reference table for Sections - Trips relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS section_trip (
        section_id INTEGER,
        trip_id INTEGER,
        UNIQUE(section_id, trip_id),
        FOREIGN KEY(section_id) REFERENCES sections(id),
        FOREIGN KEY(trip_id) REFERENCES trips(id)
    )
    """
        )

        // Reference table for Notes - Sections relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS note_section (
        note_id INTEGER,
        section_id INTEGER,
        UNIQUE(note_id, section_id),
        FOREIGN KEY(note_id) REFERENCES notes(id),
        FOREIGN KEY(section_id) REFERENCES sections(id)
    )
    """
        )

        // Reference table for Checklist - Sections relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS checklist_section (
        checklist_id INTEGER,
        section_id INTEGER,
        UNIQUE(checklist_id, section_id),
        FOREIGN KEY(checklist_id) REFERENCES checklist(id),
        FOREIGN KEY(section_id) REFERENCES sections(id)
    )
    """
        )

        // Reference table for Places - Sections relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS place_section (
        place_id INTEGER,
        section_id INTEGER,
        UNIQUE(place_id, section_id),
        FOREIGN KEY(place_id) REFERENCES places(id),
        FOREIGN KEY(section_id) REFERENCES sections(id)
    )
    """
        )
        // Reference table for ChecklistItem - Checklist relationship
        db.execSQL(
            """
    CREATE TABLE IF NOT EXISTS checklist_item_checklist (
        checklist_item_id INTEGER,
        checklist_id INTEGER,
        UNIQUE(checklist_item_id, checklist_id),
        FOREIGN KEY(checklist_item_id) REFERENCES checklist_items(id),
        FOREIGN KEY(checklist_id) REFERENCES checklist(id)
    )
    """
        )

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema upgrades here
        // Example:
        db.execSQL("DROP TABLE IF EXISTS notes")
        db.execSQL("DROP TABLE IF EXISTS checklist_items")
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
            db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val description = cursor.getString(cursor.getColumnIndex("description"))
                    notes.add(Note(id, description))
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
        contentValues.put("address", place.address)
        val gson = Gson()
        contentValues.put("photoMetadata", gson.toJson(place.photoMetadata))
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
            db.execSQL("CREATE TABLE IF NOT EXISTS places (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, address TEXT, photoMetadata TEXT)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val address = cursor.getString(cursor.getColumnIndex("address"))
                    val photoMetadataJson = cursor.getString(cursor.getColumnIndex("photoMetadata"))
                    val gson = Gson()
                    val photoMetadata = gson.fromJson(photoMetadataJson, MutableList::class.java)
                    places.add(Place(id.toString(), title, address, photoMetadata as MutableList<PhotoMetadata>))
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
        contentValues.put("title", trip.title)
        contentValues.put("imageUrl", trip.imageUrl)
        contentValues.put("startDate", trip.startDate.time)
        contentValues.put("endDate", trip.endDate.time)
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
            db.execSQL("CREATE TABLE IF NOT EXISTS trips (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, imageUrl TEXT, startDate INTEGER, endDate INTEGER)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val title = cursor.getString(cursor.getColumnIndex("title"))
                    val imageUrl = cursor.getString(cursor.getColumnIndex("imageUrl"))
                    val startDate = cursor.getLong(cursor.getColumnIndex("startDate"))
                    val endDate = cursor.getLong(cursor.getColumnIndex("endDate"))

                    trips.add(
                        Trip(
                            id,
                            title,
                            imageUrl,
                            Date(startDate),
                            Date(endDate),
                            emptyList(),
                            emptyList()
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
            db.execSQL("CREATE TABLE IF NOT EXISTS itinerary_days (id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER)")
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndex("id"))
                    val date = cursor.getLong(cursor.getColumnIndex("date"))
                    itineraryDays.add(ItineraryDay(Date(date), emptyList()))
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
        contentValues.put("address", place.address)
        val gson = Gson()
        contentValues.put("photoMetadata", gson.toJson(place.photoMetadata))

        return db.update("places", contentValues, "id=?", arrayOf(place.id))
    }

    // Delete a place from the "places" table by its ID
    fun deletePlaceById(placeId: String): Int {
        val db = writableDatabase

        return db.delete("places", "id=?", arrayOf(placeId))
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
        contentValues.put("title", trip.title)
        contentValues.put("imageUrl", trip.imageUrl)
        contentValues.put("startDate", trip.startDate.time)
        contentValues.put("endDate", trip.endDate.time)

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











    // Insert a new record into the "itinerary_day_trip" table
    fun insertItineraryDayTrip(itineraryDayId: Int, tripId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("itinerary_day_id", itineraryDayId)
        contentValues.put("trip_id", tripId)
        return db.insert("itinerary_day_trip", null, contentValues)
    }

    // Update an existing record in the "itinerary_day_trip" table
    fun updateItineraryDayTrip(oldItineraryDayId: Int, oldTripId: Int, newItineraryDayId: Int, newTripId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("itinerary_day_id", newItineraryDayId)
        contentValues.put("trip_id", newTripId)
        return db.update("itinerary_day_trip", contentValues, "itinerary_day_id=? AND trip_id=?", arrayOf(oldItineraryDayId.toString(), oldTripId.toString()))
    }

    // Delete a record from the "itinerary_day_trip" table
    fun deleteItineraryDayTrip(itineraryDayId: Int, tripId: Int): Int {
        val db = writableDatabase
        return db.delete("itinerary_day_trip", "itinerary_day_id=? AND trip_id=?", arrayOf(itineraryDayId.toString(), tripId.toString()))
    }

    // Retrieve all itinerary days associated with a specific trip from the "itinerary_day_trip" table
    @SuppressLint("Range")
    fun getAllItineraryDaysForTrip(tripId: Int): List<Int> {
        val db = readableDatabase
        val itineraryDays = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT itinerary_day_id FROM itinerary_day_trip WHERE trip_id=?", arrayOf(tripId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val itineraryDayId = cursor.getInt(cursor.getColumnIndex("itinerary_day_id"))
                    itineraryDays.add(itineraryDayId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return itineraryDays
    }



    // Insert a new record into the "section_itinerary_day" table
    fun insertSectionItineraryDay(sectionId: Int, itineraryDayId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("section_id", sectionId)
        contentValues.put("itinerary_day_id", itineraryDayId)
        return db.insert("section_itinerary_day", null, contentValues)
    }

    // Update an existing record in the "section_itinerary_day" table
    fun updateSectionItineraryDay(oldSectionId: Int, oldItineraryDayId: Int, newSectionId: Int, newItineraryDayId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("section_id", newSectionId)
        contentValues.put("itinerary_day_id", newItineraryDayId)
        return db.update("section_itinerary_day", contentValues, "section_id=? AND itinerary_day_id=?", arrayOf(oldSectionId.toString(), oldItineraryDayId.toString()))
    }

    // Delete a record from the "section_itinerary_day" table
    fun deleteSectionItineraryDay(sectionId: Int, itineraryDayId: Int): Int {
        val db = writableDatabase
        return db.delete("section_itinerary_day", "section_id=? AND itinerary_day_id=?", arrayOf(sectionId.toString(), itineraryDayId.toString()))
    }

    // Retrieve all sections associated with a specific itinerary day from the "section_itinerary_day" table
    @SuppressLint("Range")
    fun getAllSectionsForItineraryDay(itineraryDayId: Int): List<Int> {
        val db = readableDatabase
        val sections = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT section_id FROM section_itinerary_day WHERE itinerary_day_id=?", arrayOf(itineraryDayId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val sectionId = cursor.getInt(cursor.getColumnIndex("section_id"))
                    sections.add(sectionId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return sections
    }

    // Retrieve all notes associated with a specific section from the "note_section" table
    @SuppressLint("Range")
    fun getAllNotesForSection(sectionId: Int): List<Int> {
        val db = readableDatabase
        val notes = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT note_id FROM note_section WHERE section_id=?", arrayOf(sectionId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val noteId = cursor.getInt(cursor.getColumnIndex("note_id"))
                    notes.add(noteId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return notes
    }

    // Retrieve all checklists associated with a specific section from the "checklist_section" table
    @SuppressLint("Range")
    fun getAllChecklistsForSection(sectionId: Int): List<Int> {
        val db = readableDatabase
        val checklists = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT checklist_id FROM checklist_section WHERE section_id=?", arrayOf(sectionId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val checklistId = cursor.getInt(cursor.getColumnIndex("checklist_id"))
                    checklists.add(checklistId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return checklists
    }

    // Retrieve all places associated with a specific section from the "place_section" table
    @SuppressLint("Range")
    fun getAllPlacesForSection(sectionId: Int): List<Int> {
        val db = readableDatabase
        val places = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT place_id FROM place_section WHERE section_id=?", arrayOf(sectionId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val placeId = cursor.getInt(cursor.getColumnIndex("place_id"))
                    places.add(placeId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return places
    }

    // Retrieve all checklist items associated with a specific checklist from the "checklist_item_checklist" table
    @SuppressLint("Range")
    fun getAllChecklistItemsForChecklist(checklistId: Int): List<Int> {
        val db = readableDatabase
        val checklistItems = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT checklist_item_id FROM checklist_item_checklist WHERE checklist_id=?", arrayOf(checklistId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val checklistItemId = cursor.getInt(cursor.getColumnIndex("checklist_item_id"))
                    checklistItems.add(checklistItemId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return checklistItems
    }

    // section_trip table operations
    fun insertSectionTrip(sectionId: Int, tripId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("section_id", sectionId)
        contentValues.put("trip_id", tripId)
        return db.insert("section_trip", null, contentValues)
    }

    fun updateSectionTrip(oldSectionId: Int, oldTripId: Int, newSectionId: Int, newTripId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("section_id", newSectionId)
        contentValues.put("trip_id", newTripId)
        return db.update("section_trip", contentValues, "section_id=? AND trip_id=?", arrayOf(oldSectionId.toString(), oldTripId.toString()))
    }

    fun deleteSectionTrip(sectionId: Int, tripId: Int): Int {
        val db = writableDatabase
        return db.delete("section_trip", "section_id=? AND trip_id=?", arrayOf(sectionId.toString(), tripId.toString()))
    }

    @SuppressLint("Range")
    fun getAllTripsForSection(sectionId: Int): List<Int> {
        val db = readableDatabase
        val trips = mutableListOf<Int>()
        val cursor: Cursor?

        try {
            cursor = db.rawQuery("SELECT trip_id FROM section_trip WHERE section_id=?", arrayOf(sectionId.toString()))
        } catch (e: SQLException) {
            return emptyList()
        }

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val tripId = cursor.getInt(cursor.getColumnIndex("trip_id"))
                    trips.add(tripId)
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return trips
    }

    // note_section table operations
    fun insertNoteSection(noteId: Int, sectionId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("note_id", noteId)
        contentValues.put("section_id", sectionId)
        return db.insert("note_section", null, contentValues)
    }

    fun updateNoteSection(oldNoteId: Int, oldSectionId: Int, newNoteId: Int, newSectionId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("note_id", newNoteId)
        contentValues.put("section_id", newSectionId)
        return db.update("note_section", contentValues, "note_id=? AND section_id=?", arrayOf(oldNoteId.toString(), oldSectionId.toString()))
    }

    fun deleteNoteSection(noteId: Int, sectionId: Int): Int {
        val db = writableDatabase
        return db.delete("note_section", "note_id=? AND section_id=?", arrayOf(noteId.toString(), sectionId.toString()))
    }

    // checklist_section table operations
    fun insertChecklistSection(checklistId: Int, sectionId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("checklist_id", checklistId)
        contentValues.put("section_id", sectionId)
        return db.insert("checklist_section", null, contentValues)
    }

    fun updateChecklistSection(oldChecklistId: Int, oldSectionId: Int, newChecklistId: Int, newSectionId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("checklist_id", newChecklistId)
        contentValues.put("section_id", newSectionId)
        return db.update("checklist_section", contentValues, "checklist_id=? AND section_id=?", arrayOf(oldChecklistId.toString(), oldSectionId.toString()))
    }

    fun deleteChecklistSection(checklistId: Int, sectionId: Int): Int {
        val db = writableDatabase
        return db.delete("checklist_section", "checklist_id=? AND section_id=?", arrayOf(checklistId.toString(), sectionId.toString()))
    }

    // place_section table operations
    fun insertPlaceSection(placeId: Int, sectionId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("place_id", placeId)
        contentValues.put("section_id", sectionId)
        return db.insert("place_section", null, contentValues)
    }

    fun updatePlaceSection(oldPlaceId: Int, oldSectionId: Int, newPlaceId: Int, newSectionId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("place_id", newPlaceId)
        contentValues.put("section_id", newSectionId)
        return db.update("place_section", contentValues, "place_id=? AND section_id=?", arrayOf(oldPlaceId.toString(), oldSectionId.toString()))
    }

    fun deletePlaceSection(placeId: Int, sectionId: Int): Int {
        val db = writableDatabase
        return db.delete("place_section", "place_id=? AND section_id=?", arrayOf(placeId.toString(), sectionId.toString()))
    }

    // checklist_item_checklist table operations
    fun insertChecklistItemChecklist(checklistItemId: Int, checklistId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("checklist_item_id", checklistItemId)
        contentValues.put("checklist_id", checklistId)
        return db.insert("checklist_item_checklist", null, contentValues)
    }

    fun updateChecklistItemChecklist(oldChecklistItemId: Int, oldChecklistId: Int, newChecklistItemId: Int, newChecklistId: Int): Int {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("checklist_item_id", newChecklistItemId)
        contentValues.put("checklist_id", newChecklistId)
        return db.update("checklist_item_checklist", contentValues, "checklist_item_id=? AND checklist_id=?", arrayOf(oldChecklistItemId.toString(), oldChecklistId.toString()))
    }

    fun deleteChecklistItemChecklist(checklistItemId: Int, checklistId: Int): Int {
        val db = writableDatabase
        return db.delete("checklist_item_checklist", "checklist_item_id=? AND checklist_id=?", arrayOf(checklistItemId.toString(), checklistId.toString()))
    }
}

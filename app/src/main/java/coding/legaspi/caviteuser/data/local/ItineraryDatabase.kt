package coding.legaspi.caviteuser.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ItineraryEntity::class], version = 4, exportSchema = false)
abstract class ItineraryDatabase : RoomDatabase() {
    abstract fun itineraryDao() : ItineraryDao

    companion object {
        @Volatile private var instance: ItineraryDatabase? = null
        fun getDatabase(context: Context): ItineraryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ItineraryDatabase::class.java,
                    "itinerary_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build().also { instance = it }
            }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE ItineraryEntity ADD COLUMN newColumn TEXT")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table with the correct schema
        database.execSQL("CREATE TABLE IF NOT EXISTS `itineraries_new` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`eventID` TEXT NOT NULL, " +
                "`scheduleDateTimestamp` INTEGER NOT NULL, " +
                "`timestamp` TEXT NOT NULL, " +
                "`itineraryName` TEXT NOT NULL, " +
                "`itineraryPlace` TEXT NOT NULL)")

        // Migrate data from the old table to the new one, without carrying over the id
        database.execSQL("INSERT INTO `itineraries_new` (eventID, scheduleDateTimestamp, timestamp, itineraryName, itineraryPlace) " +
                "SELECT eventID, scheduleDateTimestamp, timestamp, itineraryName, itineraryPlace FROM `itineraries`")

        // Drop the old table
        database.execSQL("DROP TABLE IF EXISTS `itineraries`")

        // Rename the new table to match the expected table name
        database.execSQL("ALTER TABLE `itineraries_new` RENAME TO `itineraries`")
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add the new column `itineraryImg` to the existing table `itineraries`
        database.execSQL("ALTER TABLE `itineraries` ADD COLUMN `itineraryImg` TEXT NOT NULL DEFAULT ''")
    }
}



package ru.kkaysheva.flowexample

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import java.util.*

@Database(entities = [User::class], version = 1)
abstract class DemoDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: DemoDatabase? = null

        fun getInstance(context: Context): DemoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DemoDatabase::class.java, "Demo.db"
            ).build()
    }
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE userid = :id")
    fun getUserById(id: String): Flow<User>

    @Query("SELECT * FROM users ORDER BY username DESC LIMIT 1")
    suspend fun getLastUser(): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    @ColumnInfo(name = "userid")
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "username")
    val userName: String
)

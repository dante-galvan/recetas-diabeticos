package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "favorites")
data class FavoriteEntity(
  @PrimaryKey val recipeId: String,
  val savedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "collections")
data class CollectionEntity(
  @PrimaryKey val id: String,
  val nameEs: String,
  val nameEn: String,
  val icon: String,
  val recipeIdsCsv: String
)

@Entity(tableName = "meal_plan")
data class MealPlanEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val dayOfWeek: Int, // 1..7
  val slotKey: String, // breakfast, lunch, dinner, snack
  val recipeId: String
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
  @PrimaryKey val id: Int = 1,
  val dietaryPrefsCsv: String,
  val allergiesCsv: String,
  val langCode: String,
  val isDarkTheme: Int?, // null = system, 0 = light, 1 = dark
  val mealReminders: Boolean,
  val onboardingDone: Boolean,
  val profilePhotoPath: String? = null
)

@Dao
interface DiaRecipesDao {
  // Favorites
  @Query("SELECT * FROM favorites ORDER BY savedAt DESC")
  fun getAllFavorites(): Flow<List<FavoriteEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFavorite(fav: FavoriteEntity)

  @Query("DELETE FROM favorites WHERE recipeId = :recipeId")
  suspend fun deleteFavorite(recipeId: String)

  // Collections
  @Query("SELECT * FROM collections")
  fun getAllCollections(): Flow<List<CollectionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCollection(col: CollectionEntity)

  @Query("DELETE FROM collections WHERE id = :id")
  suspend fun deleteCollection(id: String)

  // Meal Plan
  @Query("SELECT * FROM meal_plan")
  fun getAllMealPlanItems(): Flow<List<MealPlanEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMealPlanItem(item: MealPlanEntity): Long

  @Query("DELETE FROM meal_plan WHERE dayOfWeek = :dayOfWeek AND slotKey = :slotKey")
  suspend fun deleteMealPlanSlot(dayOfWeek: Int, slotKey: String)

  @Query("DELETE FROM meal_plan WHERE id = :id")
  suspend fun deleteMealPlanItemById(id: Long)

  @Query("DELETE FROM meal_plan")
  suspend fun clearMealPlan()

  // User Profile
  @Query("SELECT * FROM user_profile WHERE id = 1")
  fun getUserProfile(): Flow<UserProfileEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun saveUserProfile(profile: UserProfileEntity)
}

@Database(
  entities = [
    FavoriteEntity::class,
    CollectionEntity::class,
    MealPlanEntity::class,
    UserProfileEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class DiaRecipesDatabase : RoomDatabase() {
  abstract fun dao(): DiaRecipesDao
}

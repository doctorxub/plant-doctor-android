package com.doctorx.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.doctorx.db.data.Disease

@Dao
interface DiseasesDao {
  @Query("SELECT * FROM Disease")
  fun getAll(): LiveData<List<Disease>>

  @Query("SELECT * FROM Disease WHERE id LIKE :id LIMIT 1")
  fun findById(id: Int): LiveData<Disease>

  @Insert
  fun insertAll(vararg deseases: Disease)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(desease: Disease)

  @Delete
  fun delete(user: Disease)

  @Query("DELETE FROM Disease")
  fun nukeTable()
}
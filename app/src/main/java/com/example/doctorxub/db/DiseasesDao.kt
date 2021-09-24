package com.example.doctorxub.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.doctorxub.db.data.Disease

@Dao
interface DiseasesDao {
  @Query("SELECT * FROM Disease")
  fun getAll(): LiveData<List<Disease>>

  @Query("SELECT * FROM Disease WHERE id LIKE :id LIMIT 1")
  fun findById(id: Int): LiveData<Disease>

  @Insert
  fun insertAll(vararg deseases: Disease)

  @Delete
  fun delete(user: Disease)

  @Query("DELETE FROM Disease")
  fun nukeTable()
}
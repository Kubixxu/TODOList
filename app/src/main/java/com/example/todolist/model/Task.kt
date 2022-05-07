package com.example.todolist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate

@Parcelize
@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Topic::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("topic"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var topic: Int,
    var name: String,
    var date: LocalDate,
    var flag: Boolean,
    var completed: Boolean,
    var dateCreation: LocalDate
) : Parcelable
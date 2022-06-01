package com.example.todolist

import com.example.todolist.task.Converters
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.lang.ArithmeticException
import java.time.LocalDate
import java.util.*

@RunWith(JUnit4::class)
class DateConvertersTest {
    private lateinit var conv: Converters

    @Before
    fun setUp() {
        conv = Converters()
    }

    @Test
    fun correctDataDateToTimestampTest() {
        assertEquals(conv.dateToTimestamp(LocalDate.of(2012, 4, 17)), 1334613600000)
        assertEquals(conv.dateToTimestamp(LocalDate.of(2020, 11, 9)), 1604876400000)
        assertEquals(conv.dateToTimestamp(LocalDate.of(2017, 8, 29)), 1503957600000)
    }

    @Test
    fun correctDataTimestampToDateTest() {
        assertEquals(conv.fromTimestamp(948495600000), LocalDate.of(2000, 1, 22))
        assertEquals(conv.fromTimestamp(-852253200000), LocalDate.of(1942, 12, 30))
        assertEquals(conv.fromTimestamp(-3600000), LocalDate.of(1970, 1, 1))
    }

    @Test
    fun nullDateToTimestampTest() {
        assertEquals(conv.dateToTimestamp(null), null)
    }

    @Test
    fun nullTimestampToDateTest() {
        assertEquals(conv.fromTimestamp(null),null)
    }

    @Test
    fun overflowDateToTimestampTest() {
        assertThrows(ArithmeticException::class.java) {
            conv.dateToTimestamp(LocalDate.of(999999999, 6, 12))
        }
    }
}
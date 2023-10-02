package pt.isel.gomoku.domain.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RowTests {

    private val dummyNum = 3

    @Test
    fun numberToRow() {
        val row = dummyNum.toRow()
        assertEquals(dummyNum, row.number)
    }

    @Test
    fun allValidNumbersToRows() {
        assertEquals(
            (1 .. 10).toList(),
            (1..10).map { it.toRow().number }
        )
    }

    @Test
    fun equalsAndIdentityOfRows() {
        val row1 = dummyNum.toRow()
        val row2 = Row(dummyNum)
        assertEquals(row1, row2)
        Assertions.assertSame(row1, row2)
    }
}
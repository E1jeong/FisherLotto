package com.queentech.domain.model.common

import org.junit.Assert.assertEquals
import org.junit.Test

class CommonResponseTest {

    @Test
    fun `statusInt parses status string to Int`() {
        val response = CommonResponse(status = "200")
        assertEquals(200, response.statusInt)
    }

    @Test
    fun `statusInt parses zero correctly`() {
        val response = CommonResponse(status = "0")
        assertEquals(0, response.statusInt)
    }
}

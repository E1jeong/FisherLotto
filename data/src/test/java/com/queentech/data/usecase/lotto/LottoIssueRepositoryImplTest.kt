package com.queentech.data.usecase.lotto

import com.queentech.data.database.room.dao.LottoIssueDao
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class LottoIssueRepositoryImplTest {

    private val dao: LottoIssueDao = mockk(relaxed = true)
    private val repository = LottoIssueRepositoryImpl(dao)

    @Test
    fun `deleteWeek deletes only the given week`() = runTest {
        val weekStart = 1_700_000_000_000L

        repository.deleteWeek(weekStart)

        coVerify(exactly = 1) { dao.deleteByWeek(weekStart) }
        coVerify(exactly = 0) { dao.deleteAll() }
        coVerify(exactly = 0) { dao.deleteOlderThan(any()) }
    }
}

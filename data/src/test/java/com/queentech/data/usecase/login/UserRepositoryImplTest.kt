package com.queentech.data.usecase.login

import com.queentech.data.database.datastore.UserLocalDataSource
import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.dao.ScanHistoryDao
import com.queentech.data.model.common.CommonResponseDto
import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.service.LottoService
import com.queentech.data.model.service.UserService
import com.queentech.domain.model.login.User
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryImplTest {

    private val userService: UserService = mockk()
    private val lottoService: LottoService = mockk()
    private val localDataSource: UserLocalDataSource = mockk()
    private val lottoIssueDao: LottoIssueDao = mockk()
    private val scanHistoryDao: ScanHistoryDao = mockk()
    private val user = User(
        name = "홍길동",
        email = "hong@test.com",
        birth = "1990-01-01",
        phone = "01011112222",
    )
    private val repository = UserRepositoryImpl(
        userService = userService,
        lottoService = lottoService,
        localDataSource = localDataSource,
        lottoIssueDao = lottoIssueDao,
        scanHistoryDao = scanHistoryDao,
    )

    @Test
    fun `deleteAccount clears local data only when server withdrawal succeeds`() = runTest {
        loadCachedUser()
        coEvery { userService.withdraw(any()) } returns CommonResponseDto(status = "8200")
        coJustRun { localDataSource.clear() }
        coJustRun { lottoIssueDao.deleteAll() }
        coJustRun { scanHistoryDao.deleteAll() }

        val result = repository.deleteAccount()

        assertTrue(result.isSuccess)
        coVerify { userService.withdraw(GetUserRequestBody(email = user.email, phone = user.phone)) }
        coVerify { localDataSource.clear() }
        coVerify { lottoIssueDao.deleteAll() }
        coVerify { scanHistoryDao.deleteAll() }
    }

    @Test
    fun `deleteAccount preserves local data when server returns failure status`() = runTest {
        loadCachedUser()
        coEvery { userService.withdraw(any()) } returns CommonResponseDto(status = "8655")

        val result = repository.deleteAccount()

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { localDataSource.clear() }
        coVerify(exactly = 0) { lottoIssueDao.deleteAll() }
        coVerify(exactly = 0) { scanHistoryDao.deleteAll() }
    }

    @Test
    fun `deleteAccount preserves local data when server request throws`() = runTest {
        loadCachedUser()
        coEvery { userService.withdraw(any()) } throws RuntimeException("Timeout")

        val result = repository.deleteAccount()

        assertTrue(result.isFailure)
        coVerify(exactly = 0) { localDataSource.clear() }
        coVerify(exactly = 0) { lottoIssueDao.deleteAll() }
        coVerify(exactly = 0) { scanHistoryDao.deleteAll() }
    }

    private suspend fun loadCachedUser() {
        every { localDataSource.userFlow } returns flowOf(user)
        repository.loadCachedUser()
    }
}

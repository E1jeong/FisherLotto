package com.queentech.presentation.main.camera

import android.net.Uri
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.lotto.ScanHistoryRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class CameraViewModelTest {

    private val getLottoNumberUseCase: GetLottoNumberUseCase = mockk(relaxed = true)
    private val scanHistoryRepository: ScanHistoryRepository = mockk(relaxed = true)

    @Test
    fun `무효 QR은 결과 다이얼로그를 열지 않고 안내 토스트를 표시한다`() = runTest {
        val viewModel = createViewModel()

        viewModel.test(this) {
            expectInitialState()
            viewModel.onQrCodeScanned("invalid-qr")

            var toastSeen = false
            while (!toastSeen) {
                when (val item = awaitItem()) {
                    is Item.StateItem -> assertFalse(item.value.showQrResultDialog)
                    is Item.SideEffectItem -> {
                        assertTrue(item.value is CameraSideEffect.Toast)
                        toastSeen = true
                    }
                }
            }
            cancelAndIgnoreRemainingItems()
        }

        assertFalse(viewModel.container.stateFlow.value.showQrResultDialog)
        assertNull(viewModel.container.stateFlow.value.result)
    }

    @Test
    fun `유효 QR은 결과 다이얼로그를 열고 닫을 때 결과 상태를 비운다`() = runTest {
        val rawValue = "https://m.dhlottery.co.kr/?v=1210021825303444"
        val uri: Uri = mockk()
        mockkStatic(Uri::class)
        every { Uri.parse(rawValue) } returns uri
        every { uri.getQueryParameter("v") } returns "1210021825303444"
        coEvery { getLottoNumberUseCase(1210) } returns Result.success(mockk())
        coEvery { scanHistoryRepository.exists(any(), any()) } returns true
        val viewModel = createViewModel()

        try {
            viewModel.test(this) {
                expectInitialState()
                viewModel.onQrCodeScanned(rawValue)

                var dialogOpened = false
                while (!dialogOpened) {
                    val item = awaitItem()
                    if (item is Item.StateItem && item.value.showQrResultDialog) dialogOpened = true
                }

                viewModel.dismissQrResultDialog()
                var dialogDismissed = false
                while (!dialogDismissed) {
                    val item = awaitItem()
                    if (item is Item.StateItem && !item.value.showQrResultDialog) dialogDismissed = true
                }
                cancelAndIgnoreRemainingItems()
            }
        } finally {
            unmockkStatic(Uri::class)
        }

        assertFalse(viewModel.container.stateFlow.value.showQrResultDialog)
        assertNull(viewModel.container.stateFlow.value.result)
        assertNull(viewModel.container.stateFlow.value.winningNumbers)
    }

    @Test
    fun `연속된 같은 QR 프레임은 열린 결과 다이얼로그를 닫지 않는다`() = runTest {
        val rawValue = "https://m.dhlottery.co.kr/?v=1210021825303444"
        val uri: Uri = mockk()
        mockkStatic(Uri::class)
        every { Uri.parse(rawValue) } returns uri
        every { uri.getQueryParameter("v") } returns "1210021825303444"
        coEvery { getLottoNumberUseCase(1210) } returns Result.success(mockk())
        coEvery { scanHistoryRepository.exists(any(), any()) } returns true
        val viewModel = createViewModel()

        try {
            viewModel.test(this) {
                expectInitialState()
                viewModel.onQrCodeScanned(rawValue)
                while (true) {
                    val item = awaitItem()
                    if (item is Item.StateItem && item.value.showQrResultDialog) break
                }

                viewModel.onQrCodeScanned(rawValue)
                advanceUntilIdle()
                assertTrue(viewModel.container.stateFlow.value.showQrResultDialog)
                cancelAndIgnoreRemainingItems()
            }
        } finally {
            unmockkStatic(Uri::class)
        }
    }

    @Test
    fun `당첨번호 조회 실패는 다이얼로그 대신 안내 토스트를 표시한다`() = runTest {
        val rawValue = "https://m.dhlottery.co.kr/?v=1210021825303444"
        val uri: Uri = mockk()
        mockkStatic(Uri::class)
        every { Uri.parse(rawValue) } returns uri
        every { uri.getQueryParameter("v") } returns "1210021825303444"
        coEvery { getLottoNumberUseCase(1210) } returns Result.failure(IllegalStateException())
        val viewModel = createViewModel()

        try {
            viewModel.test(this) {
                expectInitialState()
                viewModel.onQrCodeScanned(rawValue)
                while (true) {
                    when (val item = awaitItem()) {
                        is Item.StateItem -> assertFalse(item.value.showQrResultDialog)
                        is Item.SideEffectItem -> {
                            assertTrue(item.value is CameraSideEffect.Toast)
                            break
                        }
                    }
                }
                cancelAndIgnoreRemainingItems()
            }
        } finally {
            unmockkStatic(Uri::class)
        }

        assertFalse(viewModel.container.stateFlow.value.showQrResultDialog)
        assertNull(viewModel.container.stateFlow.value.result)
    }

    @Test
    fun `동일한 무효 QR의 연속 스캔은 쿨다운 기간 동안 무시된다`() = runTest {
        val viewModel = createViewModel()

        viewModel.test(this) {
            expectInitialState()
            viewModel.onQrCodeScanned("invalid-qr")

            var toastCount = 0
            while (toastCount < 1) {
                when (val item = awaitItem()) {
                    is Item.SideEffectItem -> {
                        if (item.value is CameraSideEffect.Toast) toastCount++
                    }
                    is Item.StateItem -> {}
                }
            }

            // 쿨다운 기간 내 동일 QR 재스캔 -> 무시됨
            viewModel.onQrCodeScanned("invalid-qr")
            advanceUntilIdle()
            cancelAndIgnoreRemainingItems()
        }

        assertFalse(viewModel.container.stateFlow.value.showQrResultDialog)
    }

    private fun createViewModel() = CameraViewModel(
        getLottoNumberUseCase = getLottoNumberUseCase,
        scanHistoryRepository = scanHistoryRepository,
    )
}

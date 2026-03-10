package com.queentech.presentation.main.expect_number

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.FisherLottoTheme
import com.queentech.presentation.theme.LastWeekAccent
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import com.queentech.presentation.util.ColorHelper
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ExpectNumberScreen(viewModel: ExpectNumberViewModel = hiltViewModel()) {
    val state by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // 보상형 광고 객체를 담을 상태
    val rewardedAdState = remember { mutableStateOf<RewardedAd?>(null) }
    val isAdLoading = remember { mutableStateOf(false) }

    // 화면 진입 시 광고 최초 1회 로드
    LaunchedEffect(Unit) {
        isAdLoading.value = true
        loadRewardedAd(context) { ad ->
            rewardedAdState.value = ad
            isAdLoading.value = false
        }
    }

    InitExpectNumberScreen(
        context = context,
        activity = activity,
        viewModel = viewModel,
        rewardedAd = rewardedAdState.value,
        onAdConsumed = {
            rewardedAdState.value = null
            if (!isAdLoading.value) {
                isAdLoading.value = true
                loadRewardedAd(context) { ad ->
                    rewardedAdState.value = ad
                    isAdLoading.value = false
                }
            }
        }
    )

    ExpectNumberContent(
        lastWeekNumbers = state.lastWeekNumbers,
        thisWeekNumbers = state.thisWeekNumbers,
        isThisWeekIssued = state.isThisWeekIssued,
        isDeadlineClosed = state.isDeadlineClosed,
        showDeadlineDialog = state.showDeadlineDialog,
        lastWeekRange = state.lastWeekRange,
        thisWeekRange = state.thisWeekRange,
        onNumberIssueClick = viewModel::onExpectNumberClick,
        onDismissDeadlineDialog = viewModel::dismissDeadlineDialog
    )
}

@Composable
private fun InitExpectNumberScreen(
    context: Context,
    activity: Activity?,
    viewModel: ExpectNumberViewModel,
    rewardedAd: RewardedAd?,
    onAdConsumed: () -> Unit
) {
    // collectSideEffect 람다 내에서 최신 상태를 읽기 위한 장치입니다.
    val currentRewardedAd by rememberUpdatedState(rewardedAd)
    val currentActivity by rememberUpdatedState(activity)
    val currentOnAdConsumed by rememberUpdatedState(onAdConsumed)

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ExpectNumberSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is ExpectNumberSideEffect.ShowRewardAd -> {
                // 광고가 준비되었는지 확인 후 띄우기
                if (currentRewardedAd != null && currentActivity != null) {
                    currentRewardedAd?.show(currentActivity!!) { _ ->
                        // 유저가 광고 시청을 완료하여 보상(Reward)을 획득했을 때 호출됨
                        viewModel.onAdWatchedSuccessfully()
                    }
                    currentOnAdConsumed()
                } else {
                    // 인터넷 문제 등으로 아직 광고가 로드되지 않은 경우
                    Toast.makeText(context, "광고를 불러오는 중입니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    currentOnAdConsumed() // 재시도를 위해 상태 초기화 및 로드 함수 호출
                }
            }
        }
    }
}

private fun loadRewardedAd(
    context: Context,
    onAdLoaded: (RewardedAd?) -> Unit
) {
    val adRequest = AdRequest.Builder().build()
    // 보상형 광고 테스트 ID입니다.
    RewardedAd.load(
        context,
        "ca-app-pub-3940256099942544/5224354917",
        adRequest,
        object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                onAdLoaded(null)
            }
            override fun onAdLoaded(ad: RewardedAd) {
                onAdLoaded(ad)
            }
        }
    )
}

@Composable
private fun ExpectNumberContent(
    lastWeekNumbers: List<String>,
    thisWeekNumbers: List<String>,
    isThisWeekIssued: Boolean,
    isDeadlineClosed: Boolean = false,
    showDeadlineDialog: Boolean = false,
    lastWeekRange: String,
    thisWeekRange: String,
    onNumberIssueClick: () -> Unit,
    onDismissDeadlineDialog: () -> Unit = {},
) {
    if (showDeadlineDialog) {
        DeadlineClosedDialog(onDismiss = onDismissDeadlineDialog)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── 저번주 섹션 (상단 절반) ──
        WeekSection(
            modifier = Modifier.weight(1f),
            label = "LAST WEEK",
            labelKor = "저번주 예상 번호",
            dateRange = lastWeekRange,
            accentColor = LastWeekAccent,
            numbers = lastWeekNumbers,
            isIssued = lastWeekNumbers.isNotEmpty(),
            emptyMessage = "저번주 번호가 없어요",
            emptyEmoji = "📭",
        )

        // ── 구분선 ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(BgDark, DividerColor, BgDark)
                    )
                )
        )

        // ── 이번주 섹션 (하단 절반) ──
        WeekSection(
            modifier = Modifier.weight(1f),
            label = "THIS WEEK",
            labelKor = "이번주 예상 번호",
            dateRange = thisWeekRange,
            accentColor = AccentBlue,
            numbers = thisWeekNumbers,
            isIssued = isThisWeekIssued,
            emptyMessage = "발급하기를 눌러보세요",
            emptyEmoji = "🎣",
            showIssueButton = true,
            isButtonDisabled = isDeadlineClosed,
            onIssueClick = onNumberIssueClick
        )
    }
}

@Composable
private fun WeekSection(
    modifier: Modifier = Modifier,
    label: String,
    labelKor: String,
    dateRange: String,
    accentColor: Color,
    numbers: List<String>,
    isIssued: Boolean,
    emptyMessage: String,
    emptyEmoji: String,
    showIssueButton: Boolean = false,
    isButtonDisabled: Boolean = false,
    onIssueClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SectionBg)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // 헤더 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 라벨 + 억센트 바
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(36.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = label,
                            color = accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        if (dateRange.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "($dateRange)",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Text(
                        text = labelKor,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 발급하기 버튼 (이번주만)
            if (showIssueButton) {
                Button(
                    onClick = onIssueClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isButtonDisabled) Color.Gray else AccentBlue
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
                    elevation = ButtonDefaults.buttonElevation(if (isButtonDisabled) 0.dp else 4.dp)
                ) {
                    Text(
                        text = if (isButtonDisabled) "마감" else "발급하기",
                        color = if (isButtonDisabled) Color.LightGray else Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 번호 리스트 or 빈 상태
        if (!isIssued) {
            EmptyState(emoji = emptyEmoji, message = emptyMessage)
        } else {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    itemsIndexed(numbers) { index, numberSet ->
                        LottoNumberRow(index = index + 1, numberSet = numberSet)
                    }
                }
            }
        }
    }
}

@Composable
private fun LottoNumberRow(index: Int, numberSet: String) {
    val numbers = numberSet.split(",").mapNotNull { it.trim().toIntOrNull() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 순번 뱃지
        Text(
            text = "$index",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(20.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 로또 번호 공들
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            numbers.forEach { number ->
                LottoBall(number = number)
            }
        }
    }
}

@Composable
private fun LottoBall(number: Int) {
    val ballColor = ColorHelper.selectBallColor(number)

    Box(
        modifier = Modifier
            .size(34.dp)
            .shadow(3.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        ballColor.copy(alpha = 1f),
                        ballColor.copy(alpha = 0.75f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = number.toString(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyState(emoji: String, message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 38.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = message,
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
@Preview
fun ExpectNumberScreenPreview() {
    FisherLottoTheme {
        Surface {
            ExpectNumberContent(
                lastWeekNumbers = listOf(
                    "1,7,15,23,38,42",
                    "3,12,18,27,33,41",
                    "5,9,21,28,35,44"
                ),
                thisWeekNumbers = listOf(
                    "2,11,19,25,36,43",
                    "4,8,22,30,37,40"
                ),
                isThisWeekIssued = true,
                lastWeekRange = "02.15 ~ 02.21",
                thisWeekRange = "02.22 ~ 02.28",
                onNumberIssueClick = {}
            )
        }
    }
}

@Composable
private fun DeadlineClosedDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        DeadlineClosedDialogContent(onDismiss = onDismiss)
    }
}

@Composable
private fun DeadlineClosedDialogContent(onDismiss: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BgDark)
            .padding(20.dp)
    ) {
        // ── 헤더 ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AccentGold)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "NOTICE",
                    color = AccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "발급 마감 안내",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── 본문 ──
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SectionBg)
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "이번회차는 마감됐습니다.",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "토요일 20:30 이후에는 발급이 불가합니다.",
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── 닫기 버튼 ──
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "확인",
                color = AccentBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview
fun DeadlineDialogPreview() {
    FisherLottoTheme {
        DeadlineClosedDialogContent()
    }
}
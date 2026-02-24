package com.queentech.presentation.main.mypage

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.queentech.domain.model.login.User
import com.queentech.domain.model.openbanking.Account
import com.queentech.domain.model.openbanking.AccountBalance
import com.queentech.presentation.navigation.RouteName.LOGIN
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.AccentGreen
import com.queentech.presentation.theme.AccentRed
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.CardBg
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import com.queentech.presentation.util.NavigationHelper
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    navController: NavHostController,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by myPageViewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    InitMyPageScreen(context, navController, myPageViewModel)

    MyPageContent(
        state = state,
        onLogoutClick = myPageViewModel::onLogoutClick,
        onConnectBankClick = myPageViewModel::onConnectBankClick,
        onCheckBalanceClick = myPageViewModel::onCheckBalanceClick,
    )
}

@Composable
private fun InitMyPageScreen(
    context: Context,
    navController: NavHostController,
    myPageViewModel: MyPageViewModel,
) {
    myPageViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is MyPageSideEffect.NavigateToLogin -> {
                NavigationHelper.navigateToLoginAfterLogout(navController, LOGIN)
            }

            is MyPageSideEffect.OpenBankAuth -> {
                // TODO: WebView나 Custom Tab으로 인증 URL 오픈
                Toast.makeText(context, "은행 인증 페이지로 이동합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun MyPageContent(
    state: MyPageState,
    onLogoutClick: () -> Unit = {},
    onConnectBankClick: () -> Unit = {},
    onCheckBalanceClick: (String) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──
        MyPageHeader()

        // ── User Profile Section ──
        UserProfileSection(user = state.user)

        Spacer(Modifier.height(16.dp))

        // ── Banking Section ──
        BankingSection(
            isLoading = state.isLoading,
            isBankConnected = state.isBankConnected,
            accounts = state.accounts,
            selectedBalance = state.selectedBalance,
            onConnectBankClick = onConnectBankClick,
            onCheckBalanceClick = onCheckBalanceClick,
        )

        Spacer(Modifier.height(16.dp))

        // ── Logout Button ──
        LogoutButton(onLogoutClick = onLogoutClick)

        Spacer(Modifier.height(32.dp))
    }
}

// ── Header ──

@Composable
private fun MyPageHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AccentBlue.copy(alpha = 0.3f), BgDark)
                )
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Text(
            text = "마이페이지",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

// ── User Profile ──

@Composable
private fun UserProfileSection(user: User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SectionBg),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 프로필 아이콘
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AccentBlue.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "프로필",
                        tint = AccentBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(
                        text = user?.name ?: "로그인이 필요합니다",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (user != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Fisher Lotto 회원",
                            color = AccentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            if (user != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = DividerColor,
                    thickness = 1.dp,
                )

                UserInfoRow(icon = Icons.Default.Email, label = "이메일", value = user.email)
                Spacer(Modifier.height(10.dp))
                UserInfoRow(
                    icon = Icons.Default.Phone,
                    label = "연락처",
                    value = user.phone.ifEmpty { "-" })
            }
        }
    }
}

@Composable
private fun UserInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = TextSecondary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.width(52.dp),
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 14.sp,
        )
    }
}

// ── Banking Section ──

@Composable
private fun BankingSection(
    isLoading: Boolean,
    isBankConnected: Boolean,
    accounts: List<Account>,
    selectedBalance: AccountBalance?,
    onConnectBankClick: () -> Unit,
    onCheckBalanceClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SectionBg),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalance,
                    contentDescription = "계좌",
                    tint = AccentGreen,
                    modifier = Modifier.size(22.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "오픈뱅킹",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentBlue, strokeWidth = 2.dp)
                }
            } else if (!isBankConnected) {
                // 아직 은행 연결 안 됨
                BankNotConnectedContent(onConnectBankClick = onConnectBankClick)
            } else {
                // 연결된 계좌 목록
                if (accounts.isEmpty()) {
                    Text(
                        text = "등록된 계좌가 없습니다.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                    )
                } else {
                    accounts.forEach { account ->
                        AccountItem(
                            account = account,
                            onCheckBalanceClick = onCheckBalanceClick,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }

                // 잔액 조회 결과
                if (selectedBalance != null) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = DividerColor,
                        thickness = 1.dp,
                    )
                    BalanceInfoCard(balance = selectedBalance)
                }
            }
        }
    }
}

@Composable
private fun BankNotConnectedContent(onConnectBankClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "계좌를 연결하고\n잔액 조회 · 송금 기능을 이용하세요",
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onConnectBankClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = AccentBlue,
                contentColor = TextPrimary,
            ),
            shape = RoundedCornerShape(10.dp),
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(text = "계좌 연결하기", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun AccountItem(
    account: Account,
    onCheckBalanceClick: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = account.bankName,
                    color = AccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = account.accountNumMasked,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = account.accountHolderName,
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
            }

            IconButton(
                onClick = { onCheckBalanceClick(account.fintechUseNum) }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "잔액 조회",
                    tint = AccentBlue,
                )
            }
        }
    }
}

@Composable
private fun BalanceInfoCard(balance: AccountBalance) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${balance.bankName} ${balance.accountNumMasked}",
                color = TextSecondary,
                fontSize = 12.sp,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "%,d".format(balance.availableAmt),
                    color = AccentGreen,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "원",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = "출금 가능 잔액",
                color = TextSecondary,
                fontSize = 12.sp,
            )
        }
    }
}

// ── Logout ──

@Composable
private fun LogoutButton(onLogoutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onLogoutClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SectionBg),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "로그아웃",
                tint = AccentRed,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "로그아웃",
                color = AccentRed,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

// ── Preview ──

@Composable
@Preview
private fun MyPageScreenPreview() {
    MyPageContent(
        state = MyPageState(
            user = User(
                name = "홍길동",
                email = "hong@example.com",
                birth = "1990-01-01",
                phone = "010-1234-5678",
            ),
            isBankConnected = true,
            accounts = listOf(
                Account(
                    fintechUseNum = "1234567890",
                    accountAlias = "주거래통장",
                    bankName = "국민은행",
                    accountNumMasked = "***-****-1234",
                    accountHolderName = "홍길동",
                    transferAgreeYn = "Y",
                ),
            ),
            selectedBalance = AccountBalance(
                bankName = "국민은행",
                accountNumMasked = "***-****-1234",
                balanceAmt = 1500000,
                availableAmt = 1450000,
                productName = "KB스타통장",
            ),
        ),
    )
}

@Composable
@Preview
private fun MyPageNotConnectedPreview() {
    MyPageContent(
        state = MyPageState(
            user = User(
                name = "홍길동",
                email = "hong@example.com",
                birth = "1990-01-01",
                phone = "010-1234-5678",
            ),
        ),
    )
}

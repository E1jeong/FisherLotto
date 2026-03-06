package com.queentech.presentation.main.mypage

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.queentech.domain.model.login.User
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.AccentRed
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    onLogoutClick: () -> Unit,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by myPageViewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    myPageViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is MyPageSideEffect.NavigateToLogin -> onLogoutClick()
        }
    }

    MyPageContent(
        state = state,
        onLogoutClick = myPageViewModel::onLogoutClick,
    )
}

@Composable
private fun MyPageContent(
    state: MyPageState,
    onLogoutClick: () -> Unit = {},
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
        ),
    )
}

@Composable
@Preview
private fun MyPageNotLoggedInPreview() {
    MyPageContent(
        state = MyPageState(),
    )
}

package com.queentech.presentation.main.mypage

import android.app.Activity
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.login.User
import com.queentech.presentation.theme.AccentBlue
import com.queentech.presentation.theme.AccentGold
import com.queentech.presentation.theme.AccentGreen
import com.queentech.presentation.theme.AccentRed
import com.queentech.presentation.theme.BgDark
import com.queentech.presentation.theme.DividerColor
import com.queentech.presentation.theme.SectionBg
import com.queentech.presentation.theme.TextPrimary
import com.queentech.presentation.theme.TextSecondary
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MyPageScreen(
    onNavigateToLogin: () -> Unit,
    myPageViewModel: MyPageViewModel = hiltViewModel(),
) {
    val state by myPageViewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    myPageViewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is MyPageSideEffect.Toast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is MyPageSideEffect.NavigateToLogin -> onNavigateToLogin()

            is MyPageSideEffect.LaunchBillingFlow -> {
                val activity = context as? Activity
                if (activity != null) {
                    myPageViewModel.launchBillingFlow(activity, sideEffect.productId)
                }
            }
        }
    }

    MyPageContent(
        state = state,
        onDeleteAccountClick = myPageViewModel::onDeleteAccountClick,
        onSubscribeClick = myPageViewModel::onSubscribeClick,
        onRestorePurchasesClick = myPageViewModel::onRestorePurchasesClick,
    )
}

@Composable
private fun MyPageContent(
    state: MyPageState,
    onDeleteAccountClick: () -> Unit = {},
    onSubscribeClick: (String) -> Unit = {},
    onRestorePurchasesClick: () -> Unit = {},
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

        // ── Subscription Section ──
        SubscriptionSection(
            subscriptionStatus = state.subscriptionStatus,
            products = state.subscriptionProducts,
            isBillingLoading = state.isBillingLoading,
            onSubscribeClick = onSubscribeClick,
            onRestorePurchasesClick = onRestorePurchasesClick,
        )

        Spacer(Modifier.height(16.dp))

        // ── Delete Account Button ──
        DeleteAccountButton(onDeleteAccountClick = onDeleteAccountClick)

        Spacer(Modifier.height(24.dp))

        // ── App Version ──
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val context = LocalContext.current
            val versionName = remember {
                try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (_: Exception) {
                    "-"
                }
            }
            Text(
                text = "v$versionName",
                color = TextSecondary,
                fontSize = 12.sp,
            )
        }

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

// ── Subscription ──

@Composable
private fun SubscriptionSection(
    subscriptionStatus: SubscriptionStatus,
    products: List<SubscriptionProduct>,
    isBillingLoading: Boolean,
    onSubscribeClick: (String) -> Unit,
    onRestorePurchasesClick: () -> Unit,
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
                    imageVector = Icons.Default.Star,
                    contentDescription = "구독",
                    tint = AccentGold,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "프리미엄 구독",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(16.dp))

            if (isBillingLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = AccentBlue,
                        strokeWidth = 2.dp,
                    )
                }
            } else if (subscriptionStatus.isActive) {
                ActiveSubscriptionContent(subscriptionStatus = subscriptionStatus)
            } else {
                InactiveSubscriptionContent(
                    products = products,
                    onSubscribeClick = onSubscribeClick,
                )
            }

            Spacer(Modifier.height(12.dp))

            if (!subscriptionStatus.isActive) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    TextButton(onClick = onRestorePurchasesClick) {
                        Text(
                            text = "구독 복원",
                            color = TextSecondary,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveSubscriptionContent(subscriptionStatus: SubscriptionStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AccentGreen.copy(alpha = 0.15f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "구독 중",
            tint = AccentGreen,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = "구독 중",
                color = AccentGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            val productName = when (subscriptionStatus.productId) {
                "fisherlotto_monthly" -> "월간 구독"
                "fisherlotto_yearly" -> "연간 구독"
                else -> subscriptionStatus.productId ?: ""
            }
            if (productName.isNotEmpty()) {
                Text(
                    text = productName,
                    color = TextSecondary,
                    fontSize = 12.sp,
                )
            }
            if (subscriptionStatus.autoRenewing) {
                Text(
                    text = "자동 갱신 활성화",
                    color = TextSecondary,
                    fontSize = 11.sp,
                )
            }
        }
    }
}

@Composable
private fun InactiveSubscriptionContent(
    products: List<SubscriptionProduct>,
    onSubscribeClick: (String) -> Unit,
) {
    if (products.isEmpty()) {
        Text(
            text = "구독 상품을 불러오는 중입니다...",
            color = TextSecondary,
            fontSize = 13.sp,
        )
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            products.forEach { product ->
                SubscriptionProductCard(
                    product = product,
                    onSubscribeClick = { onSubscribeClick(product.productId) },
                )
            }
        }
    }
}

@Composable
private fun SubscriptionProductCard(
    product: SubscriptionProduct,
    onSubscribeClick: () -> Unit,
) {
    val periodLabel = when (product.billingPeriod) {
        "P1M" -> "월간"
        "P1Y" -> "연간"
        else -> product.billingPeriod
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BgDark),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$periodLabel 구독",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = product.formattedPrice,
                    color = AccentGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Button(
                onClick = onSubscribeClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
            ) {
                Text(
                    text = "구독하기",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

// ── Delete Account ──

@Composable
private fun DeleteAccountButton(onDeleteAccountClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onDeleteAccountClick() },
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
                contentDescription = "회원탈퇴",
                tint = AccentRed,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "회원탈퇴",
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
            subscriptionProducts = listOf(
                SubscriptionProduct("fisherlotto_monthly", "월간 구독", "매월 자동 결제", "₩4,900", "P1M"),
                SubscriptionProduct("fisherlotto_yearly", "연간 구독", "매년 자동 결제", "₩49,000", "P1Y"),
            ),
        ),
    )
}

@Composable
@Preview
private fun MyPageSubscribedPreview() {
    MyPageContent(
        state = MyPageState(
            user = User(
                name = "홍길동",
                email = "hong@example.com",
                birth = "1990-01-01",
                phone = "010-1234-5678",
            ),
            subscriptionStatus = SubscriptionStatus(
                isActive = true,
                productId = "fisherlotto_monthly",
                expiryTimeMillis = null,
                autoRenewing = true,
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

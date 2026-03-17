package com.queentech.domain.model.login

data class User(
    val name: String,
    val email: String,
    val birth: String,
    val phone: String,
    val tier: String = TIER_FREE,
) {
    val isPremium: Boolean get() = tier == TIER_PREMIUM

    companion object {
        const val TIER_FREE = "FREE"
        const val TIER_PREMIUM = "PREMIUM"
    }
}

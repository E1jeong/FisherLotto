package com.queentech.presentation.util

object ValidCheckHelper {

    fun checkEmail(email: String): Boolean {
        // 너무 빡빡하게 하면 실제 이메일인데도 걸릴 수 있어서 최소 체크만
        val at = email.indexOf('@')
        val dot = email.lastIndexOf('.')
        return at > 0 && dot > at + 1 && dot < email.length - 1
    }
}
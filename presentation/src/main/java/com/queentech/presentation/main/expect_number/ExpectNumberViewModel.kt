package com.queentech.presentation.main.expect_number

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ExpectNumberViewModel @Inject constructor(

) : ViewModel(), ContainerHost<ExpectNumberState, ExpectNumberSideEffect> {
    override val container: Container<ExpectNumberState, ExpectNumberSideEffect> = container(
        initialState = ExpectNumberState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(ExpectNumberSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {

        },
    )

    companion object {
        const val TAG = "ExpectNumberViewModel"
    }
}

@Immutable
data class ExpectNumberState(
    val id: Int = 0
)

sealed interface ExpectNumberSideEffect {
    data class Toast(val message: String) : ExpectNumberSideEffect
}

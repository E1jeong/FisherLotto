package com.queentech.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.queentech.presentation.navigation.NavigationHost
import com.queentech.presentation.theme.FisherLottoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FisherLottoTheme {
                NavigationHost()
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//
//    Firebase.auth.createUserWithEmailAndPassword("abc@def@zxc", "123456")
//        .addOnCompleteListener {
//            if (it.isSuccessful) {
//                // 회원가입 성공
//                Log.e("!!@@", "회원가입 성공")
//            } else {
//                // 회원가입 실패
//                Log.e("!!@@", "회원가입 실패: " + it.exception)
//            }
//        }
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}

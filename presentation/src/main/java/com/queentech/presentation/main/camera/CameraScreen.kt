package com.queentech.presentation.main.camera

import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun CameraScreen() {
//    val state = viewModel.collectAsState().value
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var qrCodeValueDialogVisible by remember { mutableStateOf(false) }

//    viewModel.collectSideEffect { sideEffect ->
//        when (sideEffect) {
//            is MainSideEffect.Toast -> {
//                Toast.makeText(
//                    context,
//                    sideEffect.message,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        }
//    }

    CameraScreen(
        lifecycleOwner = lifecycleOwner,
        qrCodeValue = "",
        onQrCodeValueChange = {},
        onQrCodeValueDetect = { qrCodeValueDialogVisible = true }
    )

    QrResultDialog(
        visible = qrCodeValueDialogVisible,
        qrCodeValue = "",
        onDismissRequest = { qrCodeValueDialogVisible = false }
    )
}


@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@Composable
private fun CameraScreen(
    lifecycleOwner: LifecycleOwner,
    qrCodeValue: String,
    onQrCodeValueChange: (String) -> Unit,
    onQrCodeValueDetect: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val previewView = PreviewView(context)
                val preview = androidx.camera.core.Preview.Builder()
                    .build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }
                val selector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(previewView.width, previewView.height))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            processImageProxy(imageProxy, onQrCodeValueChange, onQrCodeValueDetect)
                        }
                    }

                try {
                    ProcessCameraProvider.getInstance(context).get().bindToLifecycle(
                        lifecycleOwner,
                        selector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                previewView
            },
        )
        QrCodeValueText(qrCodeValue = qrCodeValue)
    }
}

@ExperimentalGetImage
private fun processImageProxy(
    imageProxy: ImageProxy,
    onQrCodeValueChange: (String) -> Unit,
    onQrCodeValueDetect: () -> Unit
) {
    imageProxy.image?.let { image ->
        val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    barcode.rawValue?.let { qrValue ->
                        onQrCodeValueChange(qrValue)
                        onQrCodeValueDetect()
                    }
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

@Composable
private fun BoxScope.QrCodeValueText(qrCodeValue: String) {
    Text(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        text = qrCodeValue,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer
    )
}
package com.example.ui.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ui.components.ProceduralCatCharacter
import com.example.ui.theme.PeachSecondary
import com.example.ui.theme.SagePrimary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

@Composable
fun CameraCaptureScreen(
  onPhotoCaptured: (String) -> Unit,
  onNavigateBack: () -> Unit
) {
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current

  var hasCameraPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
    onResult = { isGranted ->
      hasCameraPermission = isGranted
    }
  )

  var capturedPhotoUri by remember { mutableStateOf<String?>(null) }
  var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
  var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

  // Gallery Picker as fallback or option
  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    if (uri != null) {
      onPhotoCaptured(uri.toString())
    }
  }

  LaunchedEffect(Unit) {
    if (!hasCameraPermission) {
      permissionLauncher.launch(Manifest.permission.CAMERA)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
  ) {
    if (capturedPhotoUri != null) {
      // Photo Review State
      PhotoReviewOverlay(
        photoUri = capturedPhotoUri!!,
        onRetake = { capturedPhotoUri = null },
        onConfirm = { onPhotoCaptured(capturedPhotoUri!!) }
      )
    } else if (hasCameraPermission) {
      // Live Camera Preview
      AndroidView(
        factory = { ctx ->
          val previewView = PreviewView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )
          }

          try {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
              try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                  it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val capture = ImageCapture.Builder()
                  .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                  .build()
                imageCapture = capture

                val cameraSelector = CameraSelector.Builder()
                  .requireLensFacing(lensFacing)
                  .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                  lifecycleOwner,
                  cameraSelector,
                  preview,
                  capture
                )
              } catch (e: Exception) {
                e.printStackTrace()
              }
            }, ContextCompat.getMainExecutor(ctx))
          } catch (e: Exception) {
            e.printStackTrace()
          }

          previewView
        },
        modifier = Modifier.fillMaxSize()
      )

      // Top Bar Overlay
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onNavigateBack,
          modifier = Modifier
            .clip(CircleShape)
            .background(Color(0x66000000))
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Kembali",
            tint = Color.White
          )
        }

        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color(0x66000000)
        ) {
          Text(
            text = "Temukan Cuking",
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }

        IconButton(
          onClick = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
              CameraSelector.LENS_FACING_FRONT
            } else {
              CameraSelector.LENS_FACING_BACK
            }
          },
          modifier = Modifier
            .clip(CircleShape)
            .background(Color(0x66000000))
        ) {
          Icon(
            imageVector = Icons.Default.Cameraswitch,
            contentDescription = "Ganti Kamera",
            tint = Color.White
          )
        }
      }

      // Center Guide Box
      Box(
        modifier = Modifier
          .size(260.dp)
          .align(Alignment.Center)
          .border(2.dp, Color(0x88FFFFFF), RoundedCornerShape(24.dp))
      )

      // Bottom Camera Controls
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .navigationBarsPadding()
          .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "Pastikan cuking terlihat jelas & aman",
          color = Color.White,
          style = MaterialTheme.typography.bodySmall,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x66000000))
            .padding(horizontal = 12.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Gallery Fallback button
          IconButton(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(Color(0x66000000))
          ) {
            Icon(
              imageVector = Icons.Default.Collections,
              contentDescription = "Pilih dari Galeri",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }

          // Shutter Button
          Surface(
            shape = CircleShape,
            color = Color.White,
            modifier = Modifier
              .size(80.dp)
              .border(4.dp, SagePrimary, CircleShape)
              .clickable {
                val outputDir = context.cacheDir
                val photoFile = File(
                  outputDir,
                  "cuking_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())}.jpg"
                )

                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture?.takePicture(
                  outputOptions,
                  ContextCompat.getMainExecutor(context),
                  object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                      onPhotoCaptured(Uri.fromFile(photoFile).toString())
                    }

                    override fun onError(exception: ImageCaptureException) {
                      // Fallback simulated photo
                      onPhotoCaptured("asset://cat_milo.png")
                    }
                  }
                ) ?: run {
                  onPhotoCaptured("asset://cat_milo.png")
                }
              }
          ) {
            Box(contentAlignment = Alignment.Center) {
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .clip(CircleShape)
                  .background(SagePrimary)
              ) {
                Icon(
                  imageVector = Icons.Default.CameraAlt,
                  contentDescription = "Ambil Foto",
                  tint = Color.White,
                  modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.Center)
                )
              }
            }
          }

          // Quick Simulation / Preset button
          IconButton(
            onClick = {
              onPhotoCaptured("asset://cat_milo.png")
            },
            modifier = Modifier
              .size(52.dp)
              .clip(CircleShape)
              .background(Color(0x66000000))
          ) {
            Icon(
              imageVector = Icons.Default.Pets,
              contentDescription = "Preset Cat",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
        }
      }
    } else {
      // Permission Denied / Request rationale State
      CameraPermissionDeniedView(
        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
        onPickFromGallery = { galleryLauncher.launch("image/*") },
        onUseSimulation = { capturedPhotoUri = "asset://cat_milo.png" },
        onNavigateBack = onNavigateBack
      )
    }
  }
}

@Composable
private fun PhotoReviewOverlay(
  photoUri: String,
  onRetake: () -> Unit,
  onConfirm: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF1E2320))
      .statusBarsPadding()
      .navigationBarsPadding()
      .padding(20.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = "Review Foto Cuking",
      color = Color.White,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold
    )

    // Preview Container
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = Color.DarkGray,
      modifier = Modifier
        .fillMaxWidth()
        .height(380.dp)
    ) {
      ProceduralCatCharacter(
        nickname = "milo",
        modifier = Modifier.fillMaxSize()
      )
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Button(
        onClick = onRetake,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF37474F)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
      ) {
        Icon(Icons.Default.Refresh, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Ulangi")
      }

      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(containerColor = SagePrimary),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .weight(1f)
          .height(52.dp)
      ) {
        Icon(Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Gunakan")
      }
    }
  }
}

@Composable
private fun CameraPermissionDeniedView(
  onRequestPermission: () -> Unit,
  onPickFromGallery: () -> Unit,
  onUseSimulation: () -> Unit,
  onNavigateBack: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.primaryContainer,
      modifier = Modifier.size(80.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.CameraAlt,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(40.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    Text(
      text = "Kami Membutuhkan Kamera",
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "Kamera digunakan untuk memotret cuking yang kamu temukan di jalan agar dapat didokumentasikan.",
      style = MaterialTheme.typography.bodyMedium,
      textAlign = TextAlign.Center,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(28.dp))

    Button(
      onClick = onRequestPermission,
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
    ) {
      Text("Izinkan Akses Kamera")
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = onUseSimulation,
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
    ) {
      Text("Gunakan Simulasi Cuking")
    }

    Spacer(modifier = Modifier.height(10.dp))

    Button(
      onClick = onNavigateBack,
      shape = RoundedCornerShape(16.dp),
      colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
    ) {
      Text(
        text = "Kembali ke Home",
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

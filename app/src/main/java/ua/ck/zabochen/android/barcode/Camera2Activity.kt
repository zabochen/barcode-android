package ua.ck.zabochen.android.barcode

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import kotlinx.android.synthetic.main.activity_camera2.*

// https://medium.com/@tasneem.alshiekh/building-android-barcode-detector-using-google-mobile-vision-library-a5ef0f557597

class Camera2Activity : AppCompatActivity() {

    private var cameraSource: CameraSource? = null
    private var barcodeDetector: BarcodeDetector? = null

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            when (result) {
                true -> {
                    baseInit()
                    startCamera()
                }
                false -> finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera2)
        if (cameraPermissionGranted()) {
            baseInit()
            startCamera()
        } else launchCameraPermission()
    }

    private fun launchCameraPermission() {
        requestCameraPermission.launch(cameraPermission)
    }

    private fun cameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, cameraPermission) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {

    }

    private fun baseInit() {
        this.barcodeDetector = BarcodeDetector.Builder(this).build()
        this.cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setAutoFocusEnabled(true)
            .build()
        svCameraPreview.holder.addCallback(surfaceHolderCallback)
        this.barcodeDetector?.setProcessor(processor)
    }

    private val surfaceHolderCallback = object : SurfaceHolder.Callback {
        @SuppressLint("MissingPermission")
        override fun surfaceCreated(surfaceHolder: SurfaceHolder?) {
            Log.i("Camera2Activity", "surfaceCreated: ")
            try {
                Log.i("Camera2Activity", "surfaceCreated: ")
                surfaceHolder?.let { cameraSource?.start(it) }
            } catch (e: Exception) {

            }
        }

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            Log.i("Camera2Activity", "surfaceChanged: ")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            Log.i("Camera2Activity", "surfaceDestroyed: ")
            cameraSource?.stop()
        }
    }

    private val processor = object : Detector.Processor<Barcode> {
        override fun release() {

        }

        override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
            if (detections != null && detections.detectedItems.isNotEmpty()) {
                val qrCodes: SparseArray<Barcode> = detections.detectedItems
                Log.i("Camera2Activity", "receiveDetections: ${qrCodes.valueAt(0).displayValue}")
            }
        }
    }

    companion object {
        const val cameraPermission = Manifest.permission.CAMERA
    }
}
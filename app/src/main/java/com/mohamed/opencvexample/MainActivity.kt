package com.mohamed.opencvexample

import android.os.Bundle
import android.util.Log
import android.view.SurfaceView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.OpenCVLoader
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc


class MainActivity : AppCompatActivity(), CameraBridgeViewBase.CvCameraViewListener2 {
    var baseLoaderCallback: BaseLoaderCallback? = null
    var counter = 0

    private val TAG = "TEST"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CameraView.setVisibility(SurfaceView.VISIBLE)
        CameraView.setCvCameraViewListener(this)
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME)
        CameraView.enableView()
        baseLoaderCallback = object : BaseLoaderCallback(this) {
            override fun onManagerConnected(status: Int) {
                super.onManagerConnected(status)
                when (status) {
                    SUCCESS -> CameraView.enableView()
                    else -> super.onManagerConnected(status)
                }
            }
        }
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame): Mat {
        val input = inputFrame.gray()
        val circles = Mat()
        Imgproc.blur(input, input, Size(7.0, 7.0), Point(2.0, 2.0))
        Imgproc.HoughCircles(
            input,
            circles,
            Imgproc.CV_HOUGH_GRADIENT,
            2.0,
            100.0,
            100.0,
            90.0,
            0,
            1000
        )

        Log.i(
            TAG,
            "size: " + circles.cols() + ", " + circles.rows().toString()
        )

        if (circles.cols() > 0) {
            for (x in 0 until Math.min(circles.cols(), 5)) {
                val circleVec = circles[0, x] ?: break
                val center = Point(circleVec[0], circleVec[1])
                val radius = circleVec[2].toInt()
                Imgproc.circle(input, center, 3, Scalar(255.0, 255.0, 255.0), 5)
                Imgproc.circle(input, center, radius, Scalar(255.0, 255.0, 255.0), 2)
            }
        }

        circles.release()
        input.release()
        return inputFrame.rgba()
    }

    override fun onCameraViewStarted(width: Int, height: Int) {}
    override fun onCameraViewStopped() {}
    override fun onResume() {
        super.onResume()
        if (!OpenCVLoader.initDebug()) {
            Toast.makeText(applicationContext, "There's a problem, yo!", Toast.LENGTH_SHORT)
                .show()
        } else {
            //baseLoaderCallback?.onManagerConnected(baseLoaderCallback?.SUCCESS)
        }
    }
    /*override fun onResume() {
        super.onResume()
        OpenCVLoader.initAsync(
            OpenCVLoader.OPENCV_VERSION_3_0_0,
            this,
            baseLoaderCallback
        )
    }*/


    override fun onPause() {
        super.onPause()
        if (CameraView != null) {
            CameraView.disableView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (CameraView != null) {
            CameraView.disableView()
        }
    }
}
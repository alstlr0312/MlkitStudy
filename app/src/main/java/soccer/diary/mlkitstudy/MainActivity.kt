package soccer.diary.mlkitstudy

import android.content.Context
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import soccer.diary.mlkitstudy.databinding.ActivityMainBinding
import java.io.IOException

private lateinit var binding: ActivityMainBinding
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val fileName = "face-test.jpg"

        // 에셋 폴더에서 비트맵 이미지 가져오기
        val bitmap: Bitmap? = assetsToBitmap(fileName)
        bitmap?.apply {
            binding.imageFace.setImageBitmap(this)
        }
        //버튼 누르면 플로팅박스 띄우기
        binding.btnTest.setOnClickListener {
            val highAccuracyOpts = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .build()

            val detector = FaceDetection.getClient(highAccuracyOpts)
            val image = InputImage.fromBitmap(bitmap!!, 0)
            //fromBitmap은 nullable을 인자로 받을 수 없어서 !!처리 해줘야함
            val result = detector.process(image)
                .addOnSuccessListener { faces ->
                    // 테스크가 성공
                    bitmap?.apply{
                        binding.imageFace.setImageBitmap(drawWithRectangle(faces))
                    }
                }
                .addOnFailureListener { e ->
                    // 실패
               }
        }
    }
}
//에셋 폴더에서 비트맵 이미지를 가져옴
fun Context.assetsToBitmap(fileName: String): Bitmap?{
    return try {
        with(assets.open(fileName)){
            BitmapFactory.decodeStream(this)
        }
    } catch (e: IOException) { null }
}
//얼굴에 사각형 그리기
fun Bitmap.drawWithRectangle(faces: List<Face>):Bitmap?{
    val bitmap = copy(config, true)
    val canvas = Canvas(bitmap)
    for (face in faces){
        val bounds = face.boundingBox
        Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4.0f
            isAntiAlias = true
            //얼굴에 사각형
            canvas.drawRect(
                bounds,
                this
            )
        }
    }
    return bitmap
}
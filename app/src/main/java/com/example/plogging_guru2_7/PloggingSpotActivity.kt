package com.example.plogging_guru2_7

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.plogging_guru2_7.NaverMapActivity.Companion
import com.example.plogging_guru2_7.databinding.ActivityNaverMapBinding
import com.example.plogging_guru2_7.databinding.ActivityPloggingSpotBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread

class PloggingSpotActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityPloggingSpotBinding
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationSource: FusedLocationSource
    private var selectedAddress: String? = null
    private var selectedLatLng: LatLng? = null
    private var marker: Marker? = null
    private lateinit var firebaseManager: FirebaseManager
    private lateinit var currentUsername: String
    private lateinit var currentNickname: String
    private var isInitialLocationSet = false // 플래그 추가

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_plogging_spot)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 바인딩 및 초기화
        binding = ActivityPloggingSpotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)

        fusedLocationSource = FusedLocationSource(this,
            PloggingSpotActivity.LOCATION_PERMISSION_REQUEST_CODE
        )

        // FirebaseManager 초기화
        firebaseManager = FirebaseManager()

        // 현재 로그인 중인 사용자 정보 가져오기
        val sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE)
        currentUsername = sharedPreferences.getString("username", "") ?: ""
        firebaseManager.getUserByUsername(currentUsername) { user ->
            currentNickname = user?.nickname ?: currentUsername
        }

        // btnSelectSpot 버튼 클릭 리스터
        binding.btnSelectSpot.setOnClickListener {
            if (selectedLatLng != null && selectedAddress != null) {
                val newMarker = FirebaseManager.Marker(
                    userId = currentUsername,
                    nickname = currentNickname,
                    latitude = selectedLatLng!!.latitude,
                    longitude = selectedLatLng!!.longitude,
                    address = selectedAddress!!
                )
                firebaseManager.addMarker(newMarker) { success ->
                    if (success) {
                        Toast.makeText(this, "마커가 저장되었습니다", Toast.LENGTH_SHORT).show()
                        loadMarkers()
                    } else {
                        Toast.makeText(this, "마커 저장에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "마커를 선택해 주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = fusedLocationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow // 현재 위치 표시

        // 맵 클릭 리스너 추가
        naverMap.setOnMapClickListener { _, latLng ->
            reverseGeocode(latLng)
            selectedLatLng = latLng
            placeMarker(latLng) // 마커 표시
        }

        // 현재 위치로 카메라 이동 (한 번만)
        naverMap.addOnLocationChangeListener { location ->
            if (!isInitialLocationSet) {
                val latLng = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdate.scrollTo(latLng)
                naverMap.moveCamera(cameraUpdate)
                isInitialLocationSet = true // 플래그 설정
            }
        }

        // 저장된 마커 로드
        loadMarkers()
    }

    // 역지오코딩 (위치정보 받아오기 위해)
    private fun reverseGeocode(latLng: LatLng) {
        thread { // 별도의 스레드 생성ㅕ
            try {
                // 역지오코딩 엔드포인트 URL을 생성
                val url = URL("https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=${latLng.longitude},${latLng.latitude}&orders=roadaddr&output=json")
                val connection = url.openConnection() as HttpsURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "0krxoij4ub")
                connection.setRequestProperty("X-NCP-APIGW-API-KEY", "IUhets80UVIFMnmcOp2hSV74tRSArEheoY7koUCA")

                // 응답 처리
                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val response = connection.inputStream.bufferedReader().readText()
                    val jsonObject = JSONObject(response)
                    val results = jsonObject.getJSONArray("results")
                    if (results.length() > 0) {
                        val region = results.getJSONObject(0).getJSONObject("region")
                        val land = results.getJSONObject(0).getJSONObject("land")

                        // 상세 주소 정보 가져오기
                        val area1 = region.getJSONObject("area1").getString("name")
                        val area2 = region.getJSONObject("area2").getString("name")
                        val area3 = region.getJSONObject("area3").getString("name")
                        val landName = land.getString("name")
                        val number1 = land.getString("number1")
                        val number2 = land.optString("number2")

                        val address = if (number2.isNotEmpty()) {
                            "$area1 $area2 $area3 $landName $number1-$number2"
                        } else {
                            "$area1 $area2 $area3 $landName $number1"
                        }

                        // UI 처리
                        runOnUiThread {
                            selectedAddress = address
                            Toast.makeText(this@PloggingSpotActivity, "선택한 주소: $address", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@PloggingSpotActivity, "주소를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@PloggingSpotActivity, "주소를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@PloggingSpotActivity, "주소를 가져오는 도중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun placeMarker(latLng: LatLng) {
        marker?.map = null
        marker = Marker().apply {
            position = latLng
            map = naverMap
        }
    }

    private fun loadMarkers() {
        firebaseManager.getAllMarkers { markers ->
            for (markerData in markers) {
                val marker = Marker().apply {
                    position = LatLng(markerData.latitude, markerData.longitude)
                    map = naverMap
                    captionText = markerData.nickname
                    iconTintColor = if (markerData.userId == currentUsername) android.graphics.Color.GREEN else android.graphics.Color.BLUE
                }
                marker.setOnClickListener {
                    Toast.makeText(this, "${markerData.nickname}님의 마커: ${markerData.address}", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.map.onLowMemory()
    }
}
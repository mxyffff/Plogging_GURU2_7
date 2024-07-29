package com.example.plogging_guru2_7

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.example.plogging_guru2_7.databinding.ActivityMakingGroupBinding
import com.example.plogging_guru2_7.databinding.FragmentNaverMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.concurrent.thread


class NaverMapFragment : Fragment(), OnMapReadyCallback, Overlay.OnClickListener {

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationSource: FusedLocationSource
    private var selectedAddress: String? = null
    private var selectedLatLng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    // Fragment의 뷰를 생성할 때 호출
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 데이터 바인딩 및 mapView 초기화
        val binding = FragmentNaverMapBinding.inflate(inflater, container, false)
        mapView = binding.map
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // 버튼 클릭 리스너 설정
        binding.btnSelectLocation.setOnClickListener {
            if (selectedAddress != null && selectedLatLng != null) {
                val activity = requireActivity() as NaverMapActivity
                activity.finishWithResult(selectedAddress!!, selectedLatLng!!.latitude, selectedLatLng!!.longitude)
            } else {
                Toast.makeText(requireContext(), "위치를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    // NaverMap이 준비되었을 때 호출
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        fusedLocationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = fusedLocationSource

        // 맵 클릭 리스너 추가
        naverMap.setOnMapClickListener { _, latLng ->
            reverseGeocode(latLng)
            selectedLatLng = latLng
        }
    }

    // 역지오코딩 (위치정보 받아오기 위해)
    private fun reverseGeocode(latLng: LatLng) {
        thread { // 별도의 스레드 생성
            //역지오코딩 엔드포인트 URL을 생성
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
                val address = jsonObject.getJSONArray("results")
                    .getJSONObject(0)
                    .getJSONObject("region")
                    .getJSONObject("area1")
                    .getString("name") + " " +
                        jsonObject.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("region")
                            .getJSONObject("area2")
                            .getString("name") + " " +
                        jsonObject.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("region")
                            .getJSONObject("area3")
                            .getString("name")

                // UI 처리
                activity?.runOnUiThread {
                    selectedAddress = address
                    Toast.makeText(requireContext(), "선택한 주소: $address", Toast.LENGTH_SHORT).show()
                }
            } else {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "주소를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Fragment의 생명주기 메소드
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onClick(p0: Overlay): Boolean {
        return false
    }

}
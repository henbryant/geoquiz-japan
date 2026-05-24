package com.example.geoquiz

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.geoquiz.api.RetrofitInstance
import androidx.core.view.isVisible
import com.example.geoquiz.databinding.ActivityMainBinding
import com.example.geoquiz.quiz.QuizCreator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var mapView: MapView
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBinding()
        setupMapView()
        setUpSnackBar()
    }

    private fun setupBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupMapView() {
        mapView = binding.mapView
        mapView.getMapboxMap().loadStyleUri("mapbox://styles/pathfinders2023/clkakqrw0002501q9353029ho")
        mapView.getMapboxMap().addOnMapClickListener (createMapClickListener())
    }

    private fun createMapClickListener(): OnMapClickListener {
        return OnMapClickListener { point ->
            Log.d("MapClick", "Clicked at lat: ${point.latitude()}, lon: ${point.longitude()}")
            handleMapClick(point)
            false
        }
    }

    private fun setUpSnackBar(){
        val tabLayout = binding.levelTab

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Snackbar.make(binding.coordinatorLayout, "${tab.text.toString()}が選択されました", Snackbar.LENGTH_SHORT).show()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun handleMapClick(point: com.mapbox.geojson.Point) {
        lifecycleScope.launch {
            binding.progressBar.isVisible = true
            val address = getAddress(point)
            val tab = binding.levelTab.getTabAt(binding.levelTab.selectedTabPosition)
            handleQuizCreation(address, point.latitude().toString(), point.longitude().toString(), tab?.text.toString())
            binding.progressBar.isVisible = false
        }
    }

    private suspend fun getAddress(point: com.mapbox.geojson.Point): String? {
        val response = RetrofitInstance.reverseGeoCoderData.get(point.latitude().toString(), point.longitude().toString(), BuildConfig.APPID)
        return response.body()?.getAddress()
    }

    private suspend fun handleQuizCreation(address: String?, lat: String, lon: String, difficulty: String) {
        val quiz = QuizCreator.createQuiz(address, lat, lon, difficulty)

        lifecycleScope.launch {
            if (quiz != null) {
                QuizDialog.newInstance(this@MainActivity, quiz).show()
            } else {
                // Show failure dialog
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Failed to create quiz")
                    .setPositiveButton("Close", null)
                    .create()
            }
        }
    }
}

package com.example.bemyplant

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.bemyplant.data.StatusData
import com.example.bemyplant.fragment.FlowerIdFragment
import com.example.bemyplant.model.PlantModel
import com.example.bemyplant.module.PlantModule
import com.example.bemyplant.network.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

data class PlantImage(val resourceId: Int?, val description: String)


class MainActivity : AppCompatActivity() {
    private lateinit var currentPlantImage: PlantImage // TODO: DB 연동 후 삭제
    private lateinit var regenerateButton: ImageButton

    private lateinit var mainFlowerImgBtn: ImageButton
    private lateinit var plantNameTextView: TextView
    private lateinit var statusText: TextView
    private val retrofitService = RetrofitService().apiService2
    private lateinit var realmPlant: Realm
    private lateinit var realmUser: Realm
    private lateinit var statusImages: Array<ImageView>
    private lateinit var plantNameVar : String
    private lateinit var plantBirthVar : String
    private lateinit var plantRaceVar : String
    private lateinit var plantImageVar : ByteArray
    private lateinit var plantRegistrationVar : String
    private lateinit var strangeConText: TextView
    private lateinit var strangeCondition: LinearLayout

    //@RequiresApi(Build.VERSION_CODES.O)
    //override fun onResume() {
//        super.onResume()
//        Log.d("rendering", "onResume .. updateStatus before ..")
//        updateStatus()
//        Log.d("rendering", "onResume .. updateStatus after ..")
//        checkIfSensorDataIsLatest(this) { isLatest ->
//            if (!isLatest) {
//                updateSensorError()
//            } else {
//                updateStatus()
//            }
//        }
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("rendering", "onCreate Start")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        regenerateButton = findViewById<ImageButton>(R.id.regenerateButton)
        mainFlowerImgBtn = findViewById<ImageButton>(R.id.mainFlower)
        statusText = findViewById<TextView>(R.id.textView_main_healthValue)
        strangeConText = findViewById<TextView>(R.id.strangeConText)
        strangeCondition = findViewById<LinearLayout>(R.id.strangeCondition)
        mainFlowerImgBtn = findViewById<ImageButton>(R.id.mainFlower)
        plantNameTextView = findViewById<TextView>(R.id.textView_main_flowerName)

        statusImages = arrayOf(
            findViewById<ImageView>(R.id.statusImage1),
            findViewById<ImageView>(R.id.statusImage2),
            findViewById<ImageView>(R.id.statusImage3),
        )

        val configPlant : RealmConfiguration = RealmConfiguration.Builder()
            .name("plant.realm") // 생성할 realm 파일 이름 지정
            .deleteRealmIfMigrationNeeded()
            .modules(PlantModule())
            .allowWritesOnUiThread(true) // sdhan : UI thread에서 realm에 접근할수 있게 허용
            .build()
        realmPlant = Realm.getInstance(configPlant)

//        val configUser : RealmConfiguration = RealmConfiguration.Builder()
//            .name("user.realm") // 생성할 realm 파일 이름 지정
//            .deleteRealmIfMigrationNeeded()
//            .modules(UserModel())
//            .allowWritesOnUiThread(true) // sdhan : UI thread에서 realm에 접근할수 있게 허용
//            .build()
//        realmUser = Realm.getInstance(configUser)

        mainFlowerImgBtn = findViewById<ImageButton>(R.id.mainFlower)
        plantNameTextView = findViewById<TextView>(R.id.textView_main_flowerName)

        statusText = findViewById<TextView>(R.id.textView_main_healthValue)
        statusText.text =  "???"
        currentPlantImage = PlantImage(R.drawable.delete_plant, "Default Image")

        plantNameVar = ""
        plantBirthVar = ""
        plantRaceVar = ""
        plantImageVar = byteArrayOf()
        plantRegistrationVar = ""
        regenerateButton = findViewById<ImageButton>(R.id.regenerateButton)
        mainFlowerImgBtn = findViewById<ImageButton>(R.id.mainFlower)
        //plantName = findViewById<TextView>(R.id.textView_main_flowerName)
        statusText = findViewById<TextView>(R.id.textView_main_healthValue)
        strangeConText = findViewById<TextView>(R.id.strangeConText)
        strangeCondition = findViewById<LinearLayout>(R.id.strangeCondition)

        // main image 설정
        //mainFlower.setImageResource(R.drawable.flower)
        //mainFlower.setImageResource(R.drawable.delete_plant)
        //mainFlower.setImageResource(R.drawable.sea_otter)
        //mainFlower.setImageResource(R.drawable.test_img)
        //R.drawable.flower
        // TODO: (정현) 식물 DB 조회 후 렌더링 (D+Day, 식물 이미지, 식물 이름)
        //  R.id.textView_main_dDayValue, R.id.mainFlower, R.id.textView_main_flowerName
        //  렌더링하지 않아도, 일단 DB에서 받아온 값은 모두 변수에 저장해주세요(단, 주민등록번호의 경우 반드시 plantRegistration에 저장하고, 품종은 plantRace에 저장해주세요,...) (다른 화면으로 이동 시 데이터 넘길때 사용)

        //-----------이전 화면에서 넘어오는 이미지 값이 있다면 해당 값으로 이미지 수정
        //currentPlantImage = PlantImage(R.drawable.delete_plant, "Default Image") // TODO: DB 연동 후 삭제
//        currentPlantImage = PlantImage(R.drawable.flower, "Default Image")


        Log.d("rendering", "onResume .. updateStatus before ..")
        updateStatus()
        Log.d("rendering", "onResume .. updateStatus after ..")
//        checkIfSensorDataIsLatest(this) { isLatest ->
//            if (!isLatest) {
//                updateSensorError()
//            } else {
//                updateStatus()
//            }
//        }


        // 새로고침 버튼
        regenerateButton.setOnClickListener {
            updateStatus()
//            checkIfSensorDataIsLatest(this) { isLatest ->
//                if (!isLatest) {
//                    updateSensorError()
//                } else {
//                    updateStatus()
//                }
//            }
        }

        val screenFrame = findViewById<FrameLayout>(R.id.screenFrame)
        //val newPlantImageResId = intent.getIntExtra("newPlantImageResId", 0) // 다른 화면에서 전달되는 이미지
        val deletePlant = R.drawable.delete_plant

        var dbPlant = realmPlant.where(PlantModel::class.java).findFirst()
        Log.d("rendering", "onCreate .. dbPlant realm search ..")

        if (dbPlant != null) {

            plantNameVar = dbPlant.plantName
            plantBirthVar = dbPlant.plantBirth
            plantRaceVar = dbPlant.plantRace
            plantImageVar = dbPlant.plantImage
            plantRegistrationVar = dbPlant.plantRegNum
//
            plantNameTextView.text = plantNameVar // 이름
            var transImageToBitmap = byteArrayToBitmap(plantImageVar)
            mainFlowerImgBtn.setImageBitmap(transImageToBitmap)
            currentPlantImage = PlantImage(null, "Image from DB")

        } else {
            plantNameTextView.text = ""
            plantBirthVar = "???"
            plantRaceVar = ""
            mainFlowerImgBtn.setImageResource(deletePlant)
            plantRegistrationVar = ""
            currentPlantImage = PlantImage(R.drawable.delete_plant, "Delete Image")
        }
//
        val textView_dDayValue = findViewById<TextView>(R.id.textView_main_dDayValue)

        // sdhan : D-Day 계산
        var sampleDate = dbPlant?.plantBirth
//        var sampleDate = "1900-01-02"
        if (sampleDate != null) {
            if (sampleDate == ""){
                sampleDate = "1900-01-01"
            }
        } else {
            sampleDate = "1900-01-01"
        }

        if (sampleDate != "1900-01-01") {
            var date = SimpleDateFormat("yyyy-MM-dd").parse(sampleDate)
            var today = Calendar.getInstance()
            var calculateDate = (today.time.time - date.time) / (1000 * 60 * 60 * 24)
            textView_dDayValue.text = calculateDate.toString()
        } else {
            textView_dDayValue.text = "???"
        }

        //----------메인화면에서 식물 이미지 값에 따라 특정 화면 전환
        mainFlowerImgBtn.setOnClickListener {
            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()

            when (currentPlantImage.resourceId){//resources.getIdentifier("mainFlower", "id", this.packageName)) { //currentPlantImage.resourceId
                R.drawable.delete_plant -> {

                    val plantRegisterIntent = Intent(this@MainActivity, PlantImageTempActivity::class.java)
                    // 액티비티 이동
                    startActivity(plantRegisterIntent)

                }
                else -> {
                    val bundle = Bundle()
                    bundle.putParcelable("plantImage", mainFlowerImgBtn.drawable.toBitmap())
                    bundle.putString("plantName", plantNameTextView.text.toString())
                    bundle.putString("plantBirth", plantBirthVar)
                    bundle.putString("plantRace", plantRaceVar)
                    bundle.putString("plantRegistration", plantRegistrationVar)

                    val fragment = FlowerIdFragment()
                    fragment.arguments = bundle
                    fragmentTransaction.add(R.id.screenFrame, fragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                    screenFrame.bringToFront()

                }

            }

        }

        //------------------------상태에 관한 텍스트 클릭시 , 센서 화면으로 이동
        //TODO: 새로고침 버튼 구현
        val healthText = findViewById<TextView>(R.id.textView_main_healthValue)
        val healthTitle = findViewById<TextView>(R.id.textView_main_healthFrame)

        healthText.setOnClickListener {
            // "LinearLayout" 클릭 시 SensorActivity로 이동
            val sensorIntent = Intent(this@MainActivity, SensorActivity::class.java)
            startActivity(sensorIntent)
        }
        healthTitle.setOnClickListener{
            val sensorIntent = Intent(this@MainActivity, SensorActivity::class.java)
            startActivity(sensorIntent)

        }
        //------------------------하단바

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation_main_menu)
        bottomNavigationView.selectedItemId = R.id.menu_home

        val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
//        val selectedItemIndex = 1
//
//        menuView.getChildAt(selectedItemIndex).performClick()

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.menu_home -> {
                    // "홈" 메뉴 클릭 시 MainActivity로 이동
                    val homeIntent = Intent(this@MainActivity, MainActivity::class.java)
                    startActivity(homeIntent)
                    true
                }

                R.id.menu_setting -> {
                    // "setting" 메뉴 클릭 시 SettingActivity로 이동
                    val boardIntent = Intent(this@MainActivity, SettingActivity::class.java)
                    boardIntent.putExtra("plantName", plantNameVar)
                    startActivity(boardIntent)
                    true
                }

                R.id.menu_chat -> {
                    // "채팅" 메뉴 클릭 시 ChatActivity로 이동
                    val chatIntent = Intent(this@MainActivity, ChatActivity::class.java)
                    startActivity(chatIntent)
                    true
                }

                R.id.menu_diary -> {
                    // "일기" 메뉴 클릭 시 DiaryActivity로 이동
                    val diaryIntent = Intent(this@MainActivity, DiaryActivity::class.java)
                    startActivity(diaryIntent)
                    true
                }

                else -> false
            }
        }
    }

    // ----------- 상태에 따른 이미지 및 텍스트 변경
    private fun updateStatus() {
        val statusData = StatusData("Seoul")
        val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        var statusText = findViewById<TextView>(R.id.textView_main_healthValue)
        val statusImages = arrayOf(
            findViewById<ImageView>(R.id.statusImage1),
            findViewById<ImageView>(R.id.statusImage2),
            findViewById<ImageView>(R.id.statusImage3),
        )
        val strangeCondition = findViewById<LinearLayout>(R.id.strangeCondition)
        val strangeConText = findViewById<TextView>(R.id.strangeConText)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("rendering", "retrofit response start..")
                val response = retrofitService.getWeatherAndStatus(statusData, "Bearer $token")
                Log.d("rendering", "retrofit response end..")
                if (response.isSuccessful) {
                    launch(Dispatchers.Main) {
                        // 상태에 따른 이미지 변경
                        Log.d("rendering", "updateStatus .. image rendering start..")
                        val statusResponse = response.body()
                        val statusTemp = statusResponse?.status
                        val strangeTemp = statusResponse?.most_important_feature
                        if (statusTemp != 0) {
                            val statusImageResource = arrayOf(R.drawable.good_status1, R.drawable.good_status2, R.drawable.good_status3)
                            val randomIndex = (0 until statusImageResource.size).random()
                            for (image in statusImages) {
                                image.setImageResource(statusImageResource[randomIndex])
                            }
                        } else {
                            val statusImageResource = arrayOf(R.drawable.bad_status)
                            for (image in statusImages) {
                                image.setImageResource(statusImageResource[0])
                            }
                        }
                        Log.d("rendering", "updateStatus .. set image complete..")
                        var vo = realmPlant.where(PlantModel::class.java).findFirst()
                        Log.d("rendering", "realm search")

                        if (vo != null) {
                            println("############" + vo.plantName)
                            if (vo.plantName == null || vo.plantName == "") {
                                statusText.text = "???"
                            } else {
                                //상태에 따른 텍스트 변경
                                if (statusTemp != 0) {
                                    statusText.text = "Good"
                                    strangeConText.visibility = View.INVISIBLE
                                    strangeCondition.visibility = View.INVISIBLE

                                } else {
                                    statusText.text = "Bad"
                                    strangeCondition.visibility = View.VISIBLE
                                    strangeConText.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            statusText.text = "???"
                        }
                        Log.d("rendering", "realm search complete..")


                        when (strangeTemp) {
                            "airHumid" -> strangeConText.text = "공기습도이상"
                            "airTemp" -> strangeConText.text = "온도이상"
                            "lightIntensity" -> strangeConText.text = "조도이상"
                            "soilHumid" -> strangeConText.text = "토양습도이상"
                        }
                    }

                } else {
                    launch(Dispatchers.Main) {
                        statusText.text = "???"
                        val errorBody = response.errorBody()?.string()
                        Log.e("Error_Response", errorBody ?: "error body X")
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    statusText.text = "???"
                    Log.e("API_Connection", "API 연결 실패")
                    e.printStackTrace()
                }
            }
        }
        Log.d("rendering", "updateStatus COMPLETE")

    }

    private fun updateSensorError() {
        statusText.text = "???"
        for (image in statusImages){
            image.visibility = View.INVISIBLE
        }
        strangeCondition.visibility = View.VISIBLE
        strangeConText.visibility = View.VISIBLE
        strangeConText.text = "센서 연결을 다시 진행해주세요!"

    }

    fun updateMainFlower(newPlantImageResId: Int) {
        mainFlowerImgBtn.setImageResource(newPlantImageResId) // TODO: DB 연동 후 삭제

    }
    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
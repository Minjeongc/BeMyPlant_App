package com.example.bemyplant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bemyplant.adapter.MessageAdapter
import com.example.bemyplant.data.ChatMsg
import com.example.bemyplant.data.ChatRequest
import com.example.bemyplant.network.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messageAdapterMe: MessageAdapter
    private lateinit var messageAdapterOther: MessageAdapter
    private val itemListMe = ArrayList<ChatMsg>()
    private val itemListOther = ArrayList<ChatMsg>()
    private val currentUser = "jo" //임시값
    private val retrofitService = RetrofitService().apiService2


    private fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun getTime(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val dateFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun renderingMessage(message: String, user:String, recyclerViewMe: RecyclerView, recyclerViewOther: RecyclerView) {
        Log.d("chatbot", "[function] message: $message")
        Log.d("chatbot", "[function] user: $user")

        if (message.isNotEmpty()) {
            if (user == "jo") {
                itemListMe.add(ChatMsg(message, currentUser, getTime()))
                messageAdapterMe.notifyDataSetChanged()
                recyclerViewMe.scrollToPosition(itemListMe.size - 1)

                // 빈 문자열 추가
                itemListOther.add(ChatMsg(" ".repeat(message.length), currentUser, ""))
                messageAdapterOther.notifyDataSetChanged()
                recyclerViewOther.scrollToPosition(itemListMe.size - 1)
            } else {
                Log.d("chatbot", "[function] other user enter")
                itemListOther.add(ChatMsg(message, user, getTime()))
                Log.d("chatbot", "[function] (1) itemListOther.add")
                messageAdapterOther.notifyDataSetChanged()
                Log.d("chatbot", "[function] (2) messageAdapterOther")
                recyclerViewOther.scrollToPosition(itemListOther.size - 1)
                Log.d("chatbot", "[function] (3) recyclerViewOther")

                // 빈 문자열 추가
                itemListMe.add(ChatMsg(" ".repeat(message.length), currentUser, ""))
                Log.d("chatbot", "[function] (4) itemListOther.add")
                messageAdapterMe.notifyDataSetChanged()
                Log.d("chatbot", "[function] (5) messageAdapterOtherjmj0801")
                recyclerViewMe.scrollToPosition(itemListMe.size - 1)
                Log.d("chatbot", "[function] (6) recyclerViewOther")
            }
            messageEditText.text.clear()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.bottomNavigation_main_menu)


        bottomNavigationView.selectedItemId = R.id.menu_chat
        val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    // "홈" 메뉴 클릭 시 MainActivity로 이동
                    val homeIntent = Intent(this@ChatActivity, MainActivity::class.java)
                    startActivity(homeIntent)
                    true
                }

                R.id.menu_setting -> {
                    // "setting" 메뉴 클릭 시 SettingActivity로 이동
                    val boardIntent = Intent(this@ChatActivity, SettingActivity::class.java)
                    startActivity(boardIntent)
                    true
                }

                R.id.menu_chat -> {
                    // "채팅" 메뉴 클릭 시 ChatActivity로 이동
                    val chatIntent = Intent(this@ChatActivity, ChatActivity::class.java)
                    startActivity(chatIntent)
                    true
                }

                R.id.menu_diary -> {
                    // "일기" 메뉴 클릭 시 DiaryActivity로 이동
                    val diaryIntent = Intent(this@ChatActivity, DiaryActivity::class.java)
                    startActivity(diaryIntent)
                    true
                }

                else -> false
            }
        }

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)

        val recyclerViewMe = findViewById<RecyclerView>(R.id.recycler_view_me)
        val recyclerViewOther = findViewById<RecyclerView>(R.id.recycler_view_other)

        messageAdapterMe = MessageAdapter(itemListMe, currentUser)
        messageAdapterOther = MessageAdapter(itemListOther, "other")

        recyclerViewMe.adapter = messageAdapterMe
        recyclerViewOther.adapter = messageAdapterOther

        recyclerViewMe.layoutManager = LinearLayoutManager(this)
        recyclerViewOther.layoutManager = LinearLayoutManager(this)

        ///*
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            CoroutineScope(Dispatchers.Main).launch { // 메인 스레드에서 코루틴 실행
                renderingMessage(message, "jo", recyclerViewMe, recyclerViewOther)

                val chatData = ChatRequest(message)
                val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
                val token: String? = sharedPreferences.getString("token", null)

                if (token != null) {
                    Log.d("full-token", "Bearer " + token)
                }

                // 토큰 부재 시 초기화면(MJ_main)으로 전환
                if (token.isNullOrEmpty()) {
                    val homeIntent = Intent(this@ChatActivity, MJ_MainActivity::class.java)
                    startActivity(homeIntent)
                }

                val response = retrofitService.chat(chatData, "Bearer " + token)

                if (response.code() == 401) {
                    // 401 Unauthorized 오류 처리
                    // 첫 화면으로 (로그인 시도 화면) 이동
                    val homeIntent = Intent(this@ChatActivity, MJ_MainActivity::class.java)
                    startActivity(homeIntent)
                } else {
                    // TODO: 채팅 데이터 api call - 다른 HTTP 오류 코드에 대한 처리
                }

                val Tag: String = "chatbot"
                Log.d(Tag, "chatbot raw-response: $response")
                Log.d(Tag, "chatbot raw-response-body: ${response.body()}")
                Log.d(Tag, "chatbot raw-response-body-response: ${response.body()?.response}")


                if (response.isSuccessful) {
                    val responseString = response.body()?.response
                    if (responseString != null) {
                        renderingMessage(responseString, "otherUser", recyclerViewMe, recyclerViewOther)
                    }
                }

            }
        }
    }

}

            /*lifecycleScope.launch {
                val result = sendMessage(message)

                result.onSuccess { response ->
                    // 성공한 경우 response를 사용하여 처리
                    val Tag : String = "chatbot"
                    Log.d(Tag, "chatbot processing seccuess")
                    renderingMessage(response)
                }

                result.onFailure { exception ->
                    // 실패한 경우 exception을 사용하여 처리
                    val Tag : String = "chatbot"
                    Log.d(Tag, "chatbot processing failed: ${exception.message}")

                }
            }

        }*/

    /*private suspend fun sendMessage(message: String): Result<String> {
        val chatData = ChatRequest(message)
        val sharedPreferences = getSharedPreferences("Prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        // 토큰 부재 시 초기화면(MJ_main)으로 전환
        if (token.isNullOrEmpty()) {
            val homeIntent = Intent(this@ChatActivity, MJ_MainActivity::class.java)
            startActivity(homeIntent)
            return Result.failure(Exception("Token is missing"))
        }


        try {
            // API 요청 보내기
            val response = retrofitService.chat(chatData, token)

            return if (response.isSuccessful) {
                // API 요청 성공
                Result.success(response.body()?.response ?: "")
            } else {
                // 챗봇 처리 실패
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Chatbot processing failed: $errorBody"))

            }
        } catch (e: Exception) {
            // API 요청 실패
            return Result.failure(e)
        }
    }*/


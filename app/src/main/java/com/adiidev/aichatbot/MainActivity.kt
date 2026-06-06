package com.adiidev.aichatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import org.w3c.dom.Text
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.adiidev.aichatbot.BuildConfig

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var recycleView : RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList : MutableList<Message>

    private lateinit var editText: EditText

    private lateinit var welcomeImage: ImageView

    private lateinit var helloText: TextView

    private lateinit var startingText: TextView

    private lateinit var drawerLayout: DrawerLayout

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val menuBtn = findViewById<ImageButton>(R.id.menuButton)
        val newChatBtn = findViewById<ImageButton>(R.id.newChatButton)
        val newChatBtn2 = findViewById<ImageButton>(R.id.newChatMenu)

        val sendBtn = findViewById<ImageButton>(R.id.sendBtn)

        drawerLayout = findViewById(R.id.drawerLayout)
        val historyOption = findViewById<ImageButton>(R.id.historyOption)
        val settingOption = findViewById<ImageButton>(R.id.settingsOption)


        welcomeImage = findViewById(R.id.ImageView)
        helloText = findViewById(R.id.hello)
        startingText = findViewById(R.id.StartText)


        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            supportFragmentManager.beginTransaction().replace(
                R.id.drawerFragmentContainer,
                HistoryFragment()
            ).commit()
        }
        newChatBtn.setOnClickListener {
            startNewChat()
        }

        editText = findViewById(R.id.message_edittext)

        sendBtn.setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {

                welcomeImage.visibility = View.GONE
                helloText.visibility = View.GONE
                startingText.visibility = View.GONE

                recycleView.visibility = View.VISIBLE

                messageList.add(Message(text, true))

                adapter.notifyItemInserted(messageList.size - 1)

                recycleView.scrollToPosition(messageList.size - 1)

                CoroutineScope(Dispatchers.IO).launch {

                    try {

                        val request = GeminiRequest(
                            contents = listOf(
                                Content(
                                    parts = listOf(
                                        Part(text)
                                    )
                                )
                            )
                        )

                        val response = RetrofitClient.api.generateContent(
                            BuildConfig.GEMINI_API_KEY,
                            request
                        )

                        val aiReply = response.body()
                            ?.candidates
                            ?.firstOrNull()
                            ?.content
                            ?.parts
                            ?.firstOrNull()
                            ?.text ?: "No response"

                        withContext(Dispatchers.Main) {

                            messageList.add(Message(aiReply, false))

                            adapter.notifyItemInserted(messageList.size - 1)

                            recycleView.scrollToPosition(messageList.size - 1)
                        }

                    } catch (e: Exception) {

                        withContext(Dispatchers.Main) {

                            messageList.add(
                                Message("Error: ${e.message}", false)
                            )

                            adapter.notifyItemInserted(messageList.size - 1)
                        }
                    }
                }

                editText.text.clear()
            }
        }


        historyOption.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                R.id.drawerFragmentContainer,
                HistoryFragment()
            ).commit()
        }

        settingOption.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(
                R.id.drawerFragmentContainer,
                SettingsFragment()
            ).commit()
        }

        recycleView = findViewById(R.id.chatRecyclerView)
        messageList = mutableListOf()
        adapter = MessageAdapter(messageList)

        recycleView.adapter = adapter
        recycleView.layoutManager = LinearLayoutManager(this)


        newChatBtn.setOnClickListener {
            startNewChat()
        }

        newChatBtn2.setOnClickListener {
            startNewChat()
            drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    fun clearChatFromSettings() {
        // 1. Firestore mein saari sessions ko "hidden" mark karein
        db.collection("sessions")
            .get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    // Delete nahi kar rahe, sirf ek flag laga rahe hain
                    db.collection("sessions").document(doc.id)
                        .update("isHidden", true)
                }
            }

        // 2. Current screen clear karein
        messageList.clear()
        adapter.notifyDataSetChanged()

        welcomeImage.visibility = View.VISIBLE
        helloText.visibility = View.VISIBLE
        startingText.visibility = View.VISIBLE
        recycleView.visibility = View.GONE
        editText.text.clear()
        
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun startNewChat() {
        if (messageList.isNotEmpty()) {
            saveChatSessionToFirestore(messageList.toList())
        }
        
        messageList.clear()
        adapter.notifyDataSetChanged()

        welcomeImage.visibility = View.VISIBLE
        helloText.visibility = View.VISIBLE
        startingText.visibility = View.VISIBLE
        recycleView.visibility = View.GONE
        
        editText.text.clear()
    }

    private fun saveChatSessionToFirestore(messages: List<Message>) {
        val firstUserMessage = messages.firstOrNull { it.isUser }?.text ?: "New Chat"
        val sessionData = hashMapOf(
            "title" to if (firstUserMessage.length > 30) firstUserMessage.take(30) + "..." else firstUserMessage,
            "messages" to messages.map { mapOf("text" to it.text, "isUser" to it.isUser) },
            "timestamp" to FieldValue.serverTimestamp(),
            "isHidden" to false
        )

        db.collection("sessions")
            .add(sessionData)
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save history", Toast.LENGTH_SHORT).show()
            }
    }
}
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
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var recycleView : RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList : MutableList<Message>

    private lateinit var editText: EditText

    private lateinit var welcomeImage: ImageView

    private lateinit var helloText: TextView

    private lateinit var startingText: TextView

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
        val sendBtn = findViewById<ImageButton>(R.id.sendBtn)
        val message_Ai = findViewById<EditText>(R.id.message_edittext)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
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
            Toast.makeText(this, "newChat Button clicked!", Toast.LENGTH_SHORT).show()
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

                Handler(Looper.getMainLooper()).postDelayed({
                    messageList.add(
                        Message("Hello, I am AI.\n Not really....\n" +
                                "I'm Fake AI response", false)
                    )

                    adapter.notifyItemInserted(messageList.size - 1)

                    recycleView.scrollToPosition(messageList.size - 1)

                }, 1000)

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
    }
}
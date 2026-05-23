package com.adiidev.aichatbot

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
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

        menuBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
            supportFragmentManager.beginTransaction().replace(R.id.drawerFragmentContainer ,
                HistoryFragment()).commit()
        }
        newChatBtn.setOnClickListener {
            Toast.makeText(this, "newChat Button clicked!", Toast.LENGTH_SHORT).show()
        }
        sendBtn.setOnClickListener {
            val message = message_Ai.text.toString()
            if (message.isNotEmpty()){
                Toast.makeText(this, "Menu Button clicked!", Toast.LENGTH_SHORT).show()
            }
            message_Ai.text.clear()
        }
        historyOption.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.drawerFragmentContainer ,
                HistoryFragment()
            ).commit()
        }
        settingOption.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.drawerFragmentContainer ,
                SettingsFragment()
            ).commit()
        }
    }
}
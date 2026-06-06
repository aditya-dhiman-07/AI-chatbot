package com.adiidev.aichatbot

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val clearBtn = view.findViewById<ImageButton>(R.id.clear_history)
        clearBtn.setOnClickListener {
            // MainActivity ke clearChatFromSettings function ko call kar rahe hain
            (activity as? MainActivity)?.clearChatFromSettings()
            Toast.makeText(requireContext(), "Chat Cleared!", Toast.LENGTH_SHORT).show()
        }
    }
}
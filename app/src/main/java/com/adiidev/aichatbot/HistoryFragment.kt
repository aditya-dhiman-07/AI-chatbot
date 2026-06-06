package com.adiidev.aichatbot

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private val sessionList = mutableListOf<ChatSession>()
    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView setup (Aapko fragment_history.xml mein id historyRecyclerView deni hogi)
        recyclerView = view.findViewById(R.id.historyRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(sessionList)
        recyclerView.adapter = adapter

        loadHistory()
    }

    private fun loadHistory() {
        db.collection("sessions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                sessionList.clear()
                for (doc in documents) {
                    // Sirf wahi dikhayenge jo hidden nahi hain
                    val isHidden = doc.getBoolean("isHidden") ?: false
                    if (!isHidden) {
                        val title = doc.getString("title") ?: "No Title"
                        val timestamp = doc.getTimestamp("timestamp")?.toDate()?.time ?: 0L
                        sessionList.add(ChatSession(doc.id, title, timestamp))
                    }
                }
                adapter.notifyDataSetChanged()
            }
    }
}
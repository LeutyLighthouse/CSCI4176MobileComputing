package com.example.mobilecomputingproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.R

class ChatGradesAdapter(context: Context, data: MutableList<String>, listener: ChatGradesAdapter.ItemClickListener) : RecyclerView.Adapter<ChatGradesAdapter.ViewHolder>() {

    private var grading_scheme_names = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: ChatGradesAdapter.ItemClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.chat_grade_selection_entry, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: ChatGradesAdapter.ViewHolder, position: Int) {
        var name: String = grading_scheme_names[position]
        holder.entryTextView!!.text = name

        holder.entryTextView.setOnClickListener {
            clickListener.onItemClick(position)
        }


    }

    override fun getItemCount(): Int {
        return grading_scheme_names.size
    }

    inner class ViewHolder(itemView: View, listener: ItemClickListener?) :
        RecyclerView.ViewHolder(itemView) {
        var entryTextView = itemView.findViewById<TextView>(R.id.gradeTitle)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}
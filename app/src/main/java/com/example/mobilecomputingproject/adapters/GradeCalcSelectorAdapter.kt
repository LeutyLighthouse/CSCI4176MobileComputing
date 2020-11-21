package com.example.mobilecomputingproject.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.chat.ChatMessage
import com.example.mobilecomputingproject.grades.GradeCalculatorSelector


class GradeCalcSelectorAdapter(context: Context, data: MutableList<String>, listener: GradeCalcSelectorAdapter.ItemClickListener) : RecyclerView.Adapter<GradeCalcSelectorAdapter.ViewHolder>() {

    private var grading_scheme_names = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: GradeCalcSelectorAdapter.ItemClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.grade_selection_entry, parent, false)
        return ViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: GradeCalcSelectorAdapter.ViewHolder, position: Int) {
        var name: String = grading_scheme_names[position]
        holder.entryTextView!!.text = name


        holder.entryTextView.setOnClickListener {
            clickListener.onItemClick(position)
        }
        holder.entryButton.setOnClickListener {
            clickListener.onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int { return grading_scheme_names.size }

    inner class ViewHolder(itemView: View, listener: ItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        var entryTextView = itemView.findViewById<TextView>(R.id.gradeTitle)
        var entryButton = itemView.findViewById<Button>(R.id.grade_list_row_button)
    }

    interface ItemClickListener
    {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int)
    }
}
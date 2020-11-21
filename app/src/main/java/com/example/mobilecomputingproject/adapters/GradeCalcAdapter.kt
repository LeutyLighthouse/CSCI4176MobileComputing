package com.example.mobilecomputingproject.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.grades.GradeCalcActivity
import com.example.mobilecomputingproject.grades.GradingScheme

class GradeCalcAdapter(
    context: Context,
    data: GradingScheme,
    listener: GradeCalcAdapter.ItemClickListener
) : RecyclerView.Adapter<GradeCalcAdapter.ViewHolder>() {

    var grading_scheme = data
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var clickListener: GradeCalcAdapter.ItemClickListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.grading_scheme_list_row, parent, false)
        val holder = ViewHolder(view, clickListener)


        return holder
    }

    override fun onBindViewHolder(holder: GradeCalcAdapter.ViewHolder, position: Int) {
        var name = grading_scheme.names.get(position)
        var worth = grading_scheme.worths.get(position)
        var have = grading_scheme.haves.get(position)

        holder.criteria_title.setText(name)
        holder.grade_worth.setText(worth)
        holder.grade_have.setText(have)

        println("BINDING $position")
        println("ADAP POSITION: ${holder.adapterPosition}")
//        println("immediately after adding:\n" +
//                "names: ${GradeCalcActivity.curr_grading_scheme.names}\n" +
//                "worths: ${GradeCalcActivity.curr_grading_scheme.worths}\n" +
//                "haves: ${GradeCalcActivity.curr_grading_scheme.haves}")

        holder.criteria_title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(holder.adapterPosition >= grading_scheme.names.size)
                    return
                grading_scheme.names[holder.adapterPosition] = s.toString()
                clickListener.onNameChange(holder.adapterPosition, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        holder.grade_worth.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(holder.adapterPosition >= grading_scheme.names.size)
                    return
                grading_scheme.worths[holder.adapterPosition] = s.toString()
                clickListener.onWorthChange(holder.adapterPosition, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        holder.grade_have.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(holder.adapterPosition >= grading_scheme.names.size)
                    return
                grading_scheme.haves[holder.adapterPosition] = s.toString()
                clickListener.onHaveChange(holder.adapterPosition, s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        holder.delete_bttn.setOnClickListener {
            clickListener.onDeleteClick(holder.adapterPosition)
        }

        GradeCalcActivity.update_haves()
    }


    override fun getItemCount(): Int { return grading_scheme.names.size }

    inner class ViewHolder(itemView: View, listener: ItemClickListener?) : RecyclerView.ViewHolder(
        itemView
    ) {

        var have_subtitle = itemView.findViewById<TextView>(R.id.g_have_subtitle)
        var worth_subtitle = itemView.findViewById<TextView>(R.id.g_worth_subtitle)

        var criteria_title = itemView.findViewById<EditText>(R.id.g_entry_title)
        var grade_worth = itemView.findViewById<EditText>(R.id.g_entry_worth)
        var grade_have = itemView.findViewById<EditText>(R.id.g_entry_have)

        var delete_bttn = itemView.findViewById<Button>(R.id.g_delete_button)
    }

    fun set_new_row(data: GradingScheme, size: Int)
    {
        this.grading_scheme = data
        notifyItemInserted(size - 1)
        notifyItemRangeChanged(size - 1, size)
    }





    interface ItemClickListener
    {
        fun onNameChange(position: Int, str: String)
        fun onWorthChange(position: Int, str: String)
        fun onHaveChange(position: Int, str: String)
        fun onDeleteClick(position: Int)
    }
}
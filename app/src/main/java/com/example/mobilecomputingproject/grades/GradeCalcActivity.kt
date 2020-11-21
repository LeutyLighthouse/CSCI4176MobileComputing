package com.example.mobilecomputingproject.grades

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.adapters.GradeCalcAdapter
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import org.json.JSONObject
import kotlin.math.round


class GradeCalcActivity : AppCompatActivity(), GradeCalcAdapter.ItemClickListener {

    companion object
    {
        lateinit var curr_grading_scheme_json: JSONObject
        var curr_grading_scheme: GradingScheme = GradingScheme()
        lateinit var recyclerView: RecyclerView
        var needed_grade: Double = 0.0

        fun update_haves()
        {

            var count = recyclerView.childCount
            for (i in 0 until count)
            {
                var holder: GradeCalcAdapter.ViewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)
                ) as GradeCalcAdapter.ViewHolder

                if (i == count-1)
                {
                    println("last entry:\n" +
                            "worth: ${holder.grade_worth.text.toString()}")
                }

                if (!(curr_grading_scheme.target != "" &&
                            holder.grade_have.text.toString() == "" &&
                            curr_grading_scheme.curr_total != "N/A" &&
                            holder.grade_worth.text.toString() != ""))
                {
                    holder.grade_have.hint = ""
                    holder.have_subtitle.text = "Have"
                }
                else
                {
                    holder.grade_have.hint = needed_grade.toString()
                    holder.have_subtitle.text = "Need"
                }
            }
        }
    }

    lateinit var adapter: GradeCalcAdapter

    lateinit var current_grade: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade_calc)

        curr_grading_scheme.unpack_json_into_scheme(curr_grading_scheme_json)

        recyclerView = findViewById<RecyclerView>(R.id.grade_entries_recyclerView)
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var itemDeco =  DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(itemDeco)

        adapter = GradeCalcAdapter(this, curr_grading_scheme, this)
        recyclerView.adapter = adapter


        var title = findViewById<EditText>(R.id.grades_title_editText)
        title.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                curr_grading_scheme.title = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        var target = findViewById<EditText>(R.id.grades_target_editText)
        target.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                curr_grading_scheme.target = s.toString()
                update_dynamic_fields()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        current_grade = findViewById<TextView>(R.id.grades_current_textView)
        current_grade.text = curr_grading_scheme.curr_total

        title.setText(curr_grading_scheme.title)
        target.setText(curr_grading_scheme.target)

        val new_entry = findViewById<TextView>(R.id.g_new_entry)
        new_entry.setOnClickListener {
            curr_grading_scheme.names.add("")
            curr_grading_scheme.worths.add("")
            curr_grading_scheme.haves.add("")



            recyclerView.post(Runnable {
                adapter.set_new_row(curr_grading_scheme, curr_grading_scheme.names.size) })
        }

        wait_and_update_dynamic_fields()
    }

    fun wait_and_update_dynamic_fields()
    {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                update_dynamic_fields()
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun curr_total_to_str(): String
    {
        var result: Double? = calculate_current_grade()
        return if(result == null) {
            "N/A"
        } else {
            result.toString()
        }
    }

    fun calculate_current_grade(): Double?
    {
        var n: Int = 0
        var worth_sum: Double = 0.0
        var preadjusted_sum: Double = 0.0
        var total_worth: Double = 0.0
        for (i in 0 until curr_grading_scheme.names.size)
        {
            if (curr_grading_scheme.haves[i] != "" &&
                    curr_grading_scheme.worths[i] != "")
            {
                var curr_worth: Double = curr_grading_scheme.worths[i].toDouble()
                var curr_have: Double = curr_grading_scheme.haves[i].toDouble()
                worth_sum += curr_worth
                preadjusted_sum += curr_worth * (curr_have * 0.01)
                n++
            }

            if (curr_grading_scheme.worths[i] != "")
            {
                var curr_worth: Double = curr_grading_scheme.worths[i].toDouble()
                total_worth += curr_worth
            }
        }

        // if no grade criteria have been filled out there's nothing to compute
        if (n == 0)
            return null



        var result: Double = preadjusted_sum / worth_sum
        result *= 100

        // If we don't have a target just return early with the value only
        if (curr_grading_scheme.target == "")
        {
            return round(result * 100.0) / 100.0
        }

        var curr_total: Double = result
        var rem_sum: Double = total_worth - worth_sum
        var target: Double = curr_grading_scheme.target.toDouble()

        needed_grade = (((target*0.01*total_worth) - (worth_sum*0.01*curr_total)) / rem_sum)
        needed_grade = round(needed_grade * 100.0)
        if (needed_grade < 0)
            needed_grade = 0.0

        return round(result * 100.0) / 100.0
    }

    fun update_haves()
    {
        var count = recyclerView.childCount
        for (i in 0 until count)
        {
            var holder: GradeCalcAdapter.ViewHolder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)
            ) as GradeCalcAdapter.ViewHolder

            if (i == count-1)
            {
                println("last entry:\n" +
                        "worth: ${holder.grade_worth.text.toString()}")
            }

            if (!(curr_grading_scheme.target != "" &&
                holder.grade_have.text.toString() == "" &&
                curr_grading_scheme.curr_total != "N/A" &&
                holder.grade_worth.text.toString() != ""))
            {
                holder.grade_have.hint = ""
                holder.have_subtitle.text = "Have"
            }
            else
            {
                holder.grade_have.hint = needed_grade.toString()
                holder.have_subtitle.text = "Need"
            }
        }
    }

    fun update_dynamic_fields()
    {
        curr_grading_scheme.curr_total = curr_total_to_str()
        current_grade.text = curr_grading_scheme.curr_total
        update_haves()
    }

    override fun onNameChange(position: Int, str: String){
        if(position < 0 || position >= curr_grading_scheme.names.size)
            return
        curr_grading_scheme.names[position] = str
    }
    override fun onWorthChange(position: Int, str: String){
        if(position < 0 || position >= curr_grading_scheme.names.size)
            return
        curr_grading_scheme.worths[position] = str

        update_dynamic_fields()
    }
    override fun onHaveChange(position: Int, str: String){
        if(position < 0 || position >= curr_grading_scheme.names.size)
            return
        curr_grading_scheme.haves[position] = str

        update_dynamic_fields()
    }
    override fun onDeleteClick(position: Int){
        if(position < 0 || position >= curr_grading_scheme.names.size)
            return
        curr_grading_scheme.names.removeAt(position)
        curr_grading_scheme.worths.removeAt(position)
        curr_grading_scheme.haves.removeAt(position)

        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, curr_grading_scheme.names.size)

        update_dynamic_fields()
    }

    override fun onStop()
    {
        var tmp = GradingScheme()
        tmp.unpack_json_into_scheme(GradingScheme.create_blank_grading_scheme())
        if(curr_grading_scheme == tmp)
        {
            super.onStop()
            return
        }

        // If this is a brand new scheme let's generate a UID for it
        if (curr_grading_scheme.uid == "") {
            var new_idx = GradesPersistence.getNewSchemeKey()
            if (new_idx != null) {
                curr_grading_scheme.uid = new_idx
            } else {
                return
            }
        }

        curr_grading_scheme_json = GradingScheme.convert_to_JSONObject(curr_grading_scheme)

        GradesPersistence.set_scheme_json(
            curr_grading_scheme.uid,
            curr_grading_scheme_json,
            object : ICallback {
                override fun callback(data: DataSnapshot?) {
                }
            })
        super.onStop()
    }

}
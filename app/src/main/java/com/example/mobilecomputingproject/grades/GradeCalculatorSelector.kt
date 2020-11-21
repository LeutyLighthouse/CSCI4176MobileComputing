package com.example.mobilecomputingproject.grades

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobilecomputingproject.MainActivity
import com.example.mobilecomputingproject.R
import com.example.mobilecomputingproject.adapters.ChatSelectorAdapter
import com.example.mobilecomputingproject.adapters.GradeCalcSelectorAdapter
import com.example.mobilecomputingproject.chat.ChatActivity
import com.example.mobilecomputingproject.interfaces.ICallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.getValue
import org.json.JSONObject

class GradeCalculatorSelector : AppCompatActivity(), GradeCalcSelectorAdapter.ItemClickListener  {
    companion object
    {
        var grading_scheme_names = mutableListOf<String>()
        var grading_schemes = mutableListOf<JSONObject>()
    }

    lateinit var adapter: GradeCalcSelectorAdapter

    var callback = object : ICallback {
        override fun callback(data: DataSnapshot?) {

            if (data != null) {
                grading_schemes.clear()
                grading_scheme_names.clear()

                for (snapshot in data.children) {
                    var answer: String? = snapshot.getValue<String>()
                    var curr_json: JSONObject = JSONObject(answer)
                    grading_schemes.add(curr_json)
                }

                for (json in grading_schemes)
                {
                    grading_scheme_names.add(json.get(GradingScheme.TITLE).toString())
                }
            }
            adapter.notifyDataSetChanged()
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grade_calculator_selector)

        var recyclerView = findViewById<RecyclerView>(R.id.grade_selector_list)
        var layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        var itemDeco =  DividerItemDecoration(recyclerView.context, layoutManager.orientation)
        recyclerView.addItemDecoration(itemDeco)

        adapter = GradeCalcSelectorAdapter(this, grading_scheme_names, this)
        recyclerView.adapter = adapter

        val new_scheme = findViewById<TextView>(R.id.grade_selector_new_entry)
        new_scheme.setOnClickListener {
            val scheme = GradingScheme.create_blank_grading_scheme()
            GradeCalcActivity.curr_grading_scheme_json = scheme

            val intent = Intent(applicationContext, GradeCalcActivity::class.java)
            startActivity(intent)
        }

        GradesPersistence.turn_on_schemes_listener(callback)
    }


    override fun onItemClick(position: Int) {

        // set up scheme info
        GradeCalcActivity.curr_grading_scheme_json = grading_schemes[position]

        val intent = Intent(applicationContext, GradeCalcActivity::class.java)
        startActivity(intent)
    }

    override fun onDeleteClick(position: Int) {
        if (position >= grading_scheme_names.size)
            return

        var selected_uid = grading_schemes[position].get(GradingScheme.UID).toString()


        grading_scheme_names.removeAt(position)
        grading_schemes.removeAt(position)

        adapter.notifyItemRemoved(position)
        adapter.notifyItemRangeChanged(position, grading_scheme_names.size)

        GradesPersistence.delete_scheme(selected_uid)
    }

    override fun onBackPressed() {
        GradesPersistence.turn_off_schemes_listener(object: ICallback
        {
            override fun callback(data: DataSnapshot?) {

            }
        })

        super.onBackPressed()
    }


}
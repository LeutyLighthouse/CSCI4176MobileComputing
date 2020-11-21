package com.example.mobilecomputingproject.grades

import org.json.JSONArray
import org.json.JSONObject

class GradingScheme {
    companion object
    {
        const val TITLE = "TITLE"
        const val TARGET_GRADE = "TARGET"
        const val CURR_GRADE = "CURR_GRADE"
        const val GRADES = "GRADES"

        // For each item in grades
        const val G_NAME = "NAME"
        const val G_WORTH = "WORTH"
        const val G_CURR = "G_CURR"

        const val UID = "UID"

        const val special_entry_marker = "SPECIAL"

        fun create_blank_grading_scheme(): JSONObject
        {
            var obj: JSONObject = JSONObject()
            obj.put(TITLE, "")
            obj.put(TARGET_GRADE, "")
            obj.put(CURR_GRADE, "N/A")
            obj.put(UID, "")

            var list = JSONArray()
            obj.put(GRADES, list)

            return obj
        }

        fun convert_to_JSONObject(scheme: GradingScheme): JSONObject
        {
            var obj: JSONObject = JSONObject()

            obj.put(TITLE, scheme.title)
            obj.put(TARGET_GRADE, scheme.target)
            obj.put(CURR_GRADE, scheme.curr_total)
            obj.put(UID, scheme.uid)

            var list = JSONArray()
            for (i in 0 until scheme.names.size)
            {
                var list_obj = JSONObject()
                list_obj.put(G_NAME, scheme.names[i])
                list_obj.put(G_WORTH, scheme.worths[i])
                list_obj.put(G_CURR, scheme.haves[i])
                list.put(list_obj)
            }
            obj.put(GRADES, list)

            return obj
        }
    }

    var title = ""
    var target: String = ""
    var curr_total: String = ""
    var uid = ""
    var names = mutableListOf<String>()
    var worths = mutableListOf<String>()
    var haves = mutableListOf<String>()


    fun unpack_json_into_scheme(json: JSONObject)
    {
        title = json.get(TITLE).toString()
        target = json.get(TARGET_GRADE).toString()
        curr_total = json.get(CURR_GRADE).toString()
        uid = json.get(UID).toString()

        names.clear()
        worths.clear()
        haves.clear()

        var list = json.getJSONArray(GRADES)
        for (i in 0 until list.length())
        {
            var curr_obj: JSONObject = list.getJSONObject(i)
            names.add(curr_obj.get(G_NAME).toString())
            worths.add(curr_obj.get(G_WORTH).toString())
            haves.add(curr_obj.get(G_CURR).toString())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is GradingScheme)
            return false
        if (names.size != other.names.size ||
            worths.size != other.worths.size ||
            haves.size != other.haves.size)
            return false
        if(uid != other.uid ||
            title != other.title ||
            target != other.target ||
            curr_total != other.curr_total)
            return false

        for (i in 0 until names.size)
        {
            if (names[i] != other.names[i])
                return false
        }

        for (i in 0 until worths.size)
        {
            if (worths[i] != other.worths[i])
                return false
        }

        for (i in 0 until haves.size)
        {
            if (haves[i] != other.haves[i])
                return false
        }

        return true
    }



}
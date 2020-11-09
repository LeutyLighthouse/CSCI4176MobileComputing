package com.example.mobilecomputingproject.interfaces

import com.google.firebase.database.DataSnapshot

interface ICallback {
    fun callback(data: DataSnapshot?){}
}
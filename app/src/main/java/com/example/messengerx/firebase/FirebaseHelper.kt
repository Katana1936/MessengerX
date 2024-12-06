package com.example.messengerx.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseHelper {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getDatabaseReference(path: String): DatabaseReference {
        return database.getReference(path)
    }
}

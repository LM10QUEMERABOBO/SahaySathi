package com.example.sahaysathi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 🔐 LOGIN
    fun loginUser(
        email: String,
        password: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback(true, "Login Successful")
                } else {
                    callback(false, it.exception?.message)
                }
            }
    }

    // 📝 REGISTER USER (FIXED)
    fun registerUser(
        name: String,
        email: String,
        password: String,
        role: String,
        callback: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val userId = auth.currentUser?.uid ?: ""

                    val userMap = hashMapOf(
                        "userId" to userId, // ✅ ADD THIS
                        "name" to name,
                        "email" to email,
                        "role" to role,
                        "createdAt" to FieldValue.serverTimestamp() // ✅ ADD THIS
                    )

                    db.collection("users")
                        .document(userId) // ✅ FIX: use userId as docId
                        .set(userMap)
                        .addOnSuccessListener {
                            callback(true, "Signup Successful")
                        }
                        .addOnFailureListener {
                            callback(false, it.message)
                        }

                } else {
                    callback(false, task.exception?.message)
                }
            }
    }

    // 👤 GET USER ID
    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    // 🔍 GET USER DATA
    fun getUserData(
        userId: String,
        callback: (HashMap<String, String>?) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->

                if (doc.exists()) {

                    val data = HashMap<String, String>()
                    data["name"] = doc.getString("name") ?: ""
                    data["email"] = doc.getString("email") ?: ""
                    data["role"] = doc.getString("role") ?: ""

                    callback(data)

                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}
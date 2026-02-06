package com.example.sahaysathi

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // 🔹 SIGN UP
    fun registerUser(
        name: String,
        email: String,
        password: String,
        role: String, // "volunteer" or "ngo"
        onResult: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""

                    val userMap = hashMapOf(
                        "id" to userId,
                        "name" to name,
                        "email" to email,
                        "role" to role,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("users").document(userId)
                        .set(userMap)
                        .addOnSuccessListener {
                            onResult(true, "User Registered")
                        }
                        .addOnFailureListener {
                            onResult(false, it.message ?: "Error saving user")
                        }
                } else {
                    onResult(false, task.exception?.message ?: "Signup Failed")
                }
            }
    }

    // 🔹 LOGIN
    fun loginUser(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, "Login Successful")
                } else {
                    onResult(false, task.exception?.message ?: "Login Failed")
                }
            }
    }

    // 🔹 LOGOUT
    fun logout() {
        auth.signOut()
    }

    // 🔹 CURRENT USER
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
}
package com.manish.tripgenie

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun login(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val result = com.google.android.gms.tasks.Tasks.await(auth.signInWithEmailAndPassword(email, pass))
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val result = com.google.android.gms.tasks.Tasks.await(auth.createUserWithEmailAndPassword(email, pass))
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }
}

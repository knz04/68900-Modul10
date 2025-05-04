package id.ac.umn.myapplication

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signupWithEmailPassword(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun loginWithEmailAndPassword(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun signOut() {
        auth.signOut()
    }

    fun signInWithGoogle(idToken: String, onResult: (Boolean, String?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun uploadProfilePicture(imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")
        val storageRef = Firebase.storage.reference
        val profilePicRef = storageRef.child("profile_pictures/$uid.jpg")

        profilePicRef.putFile(imageUri)
            .addOnSuccessListener {
                profilePicRef.downloadUrl.addOnSuccessListener { uri ->
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(uri)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, updateTask.exception?.message)
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                onResult(false, exception.message)
            }
    }

    fun deleteProfilePicture(onResult: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return onResult(false, "User not logged in")
        val storageRef = Firebase.storage.reference
        val profilePicRef = storageRef.child("profile_pictures/$uid.jpg")

        profilePicRef.delete()
            .addOnSuccessListener {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(null)
                        .build()
                    auth.currentUser?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                onResult(true, null)
                            } else {
                                onResult(false, updateTask.exception?.message)
                            }
                        }
            }
            .addOnFailureListener { e -> if (e.message?.contains("object does not exist") == true) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(null)
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onResult(true, null)
                        } else {
                            onResult(false, updateTask.exception?.message)
                        }
                    }
                } else {
                    onResult(false, e.message)
            }
            }
    }
}
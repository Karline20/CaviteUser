package coding.legaspi.caviteuser.utils

import android.app.Notification.Action
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import coding.legaspi.caviteuser.data.model.profile.Profile
import coding.legaspi.caviteuser.data.model.profile.ProfileImage
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage


class FirebaseManager {

    val firebaseStorage = FirebaseStorage.getInstance().reference
    val firebaseRealtime = FirebaseDatabase.getInstance().getReference("Profile")
    val firebaseAuth = FirebaseAuth.getInstance()

    val TAG = "FirebaseManager"

    fun checkUser(context: Context, callback: (Boolean) -> Unit){
        val user: FirebaseUser? = firebaseAuth.currentUser
        if (user != null) {
            user.reload() // Reload the user to ensure the latest information
                .addOnCompleteListener { reloadTask ->
                    if (reloadTask.isSuccessful) {
                        if (user.isEmailVerified) {
                            callback(true)
                            Log.d(TAG, "Email is verified: true")
                        } else {
                            callback(true)
                            Toast.makeText(context, "Please check your verification email!", Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "Email is verified: false")
                        }
                    } else {
                        callback(false)
                        Log.e(TAG, "Failed to reload user: ${reloadTask.exception}")
                    }
                }
        } else {
            callback(false)
            Log.d(TAG, "No user is signed in")
        }
    }

    fun registerUser(email: String, password: String, callback: (Boolean) -> Unit){
        Log.d(TAG, "Email: "+email)
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    firebaseAuth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener {
                            if (it.isSuccessful){
                                callback(true)
                                Log.d(TAG, ""+true)
                                Log.d(TAG, it.toString())
                            }else{
                                callback(false)
                                Log.e(TAG, ""+false)
                            }
                        }?.addOnFailureListener {
                            Log.e(TAG, ""+it)
                            callback(false)
                        }
                }else{
                    Log.e(TAG, ""+false)
                }
            }.addOnFailureListener {
                Log.e(TAG, ""+it)
                callback(false)
            }
    }
    fun logout(callback: (Boolean) -> Unit){
        if (firebaseAuth.currentUser!=null){
            firebaseAuth.signOut()
            if (firebaseAuth.currentUser==null){
                callback(true)
            }
        }
    }

    fun saveImageToFirebase(context: Context, userID: String, imageUri: Uri, callback: (Boolean) -> Unit){
        if (imageUri != null && userID != null) {
            Log.d("Check Result", "saveImageToFirebase $userID")
            val timestamp = System.currentTimeMillis().toString()
            val imageRef = firebaseStorage.child("images").child(timestamp) // Store in 'images' subdirectory

            imageRef.putFile(imageUri)
                .addOnSuccessListener { uploadTask ->
                    uploadTask.storage.downloadUrl
                        .addOnSuccessListener { uri ->
                            val hashMap: HashMap<String, String> = HashMap()
                            hashMap["timestamp"] = timestamp
                            hashMap["userID"] = userID
                            hashMap["id"] = timestamp
                            hashMap["imageUri"] = uri.toString()

                            firebaseRealtime.child(userID).setValue(hashMap)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        callback(true)
                                        Toast.makeText(context, "Saved...", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Can't save Image", Toast.LENGTH_SHORT).show()
                                    callback(false)
                                }
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Can't save Image", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
        }

    }

    fun saveUpdateToFirebase(context: Context, eventID: String, imageUri: Uri, id: String, callback: (Boolean) -> Unit) {
        if (imageUri!=null || eventID!=null){
            val timestamp = System.currentTimeMillis().toString()
            val uploadTask = firebaseStorage.child("images").putFile(imageUri)
            uploadTask.addOnSuccessListener {
                if (uploadTask.isSuccessful){
                    var hashMap : HashMap<String, Any> = HashMap<String, Any> ()
                    hashMap.put("timestamp", timestamp)
                    hashMap.put("eventID", eventID)
                    hashMap.put("id", id)
                    hashMap.put("imageUri", it.uploadSessionUri.toString())
                    firebaseRealtime.child(eventID).child(id).updateChildren(hashMap)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                callback(true)
                                Toast.makeText(context, "Saved...", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener {
                            Toast.makeText(context, "Can't save Image", Toast.LENGTH_SHORT).show()
                            callback(false)
                        }
                }else{
                    Toast.makeText(context, "Can't save Image", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }.addOnFailureListener{
                Toast.makeText(context, "Can't save Image", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }

    }

    fun deleteToFirebase(context: Context, eventID: String, callback: (Boolean) -> Unit) {
        firebaseRealtime.child(eventID).removeValue()
            .addOnCompleteListener {
                callback(true)
                Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
            }.addOnCanceledListener {
                Toast.makeText(context, "Can't delete", Toast.LENGTH_SHORT).show()
                callback(false)
            }
        }

    fun fetchDataToFirebase(eventID: String, callback: (List<DataSnapshot>?) -> Unit) {
        Log.d(TAG, eventID)
        val eventRef = firebaseRealtime.child(eventID)
        eventRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val dataList: List<DataSnapshot> = dataSnapshot.children.toList()
                    callback(dataList)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    fun fetchProfileFromFirebase(userid: String, callback: (ProfileImage) -> Unit){
        Log.d("HomeActivity", "fetchProfileFromFirebase")
        val profileRef = firebaseRealtime.child(userid)
        profileRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeActivity", "onDataChange")
                if (snapshot.exists()){
                    val id = snapshot.child("id").value
                    val imageUri = snapshot.child("imageUri").value
                    val timestamp = snapshot.child("timestamp").value
                    val userID = snapshot.child("userID").value
                    val profile = ProfileImage(id.toString(), imageUri.toString(), timestamp.toString(), userID.toString())
                    callback(profile)
                }else{
                    val profile = ProfileImage("", "", "", "")
                    callback(profile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", "onCancelled $error")
                val profile = ProfileImage("", "", "", "")
                callback(profile)
            }
        })
    }

    fun fetchResearchFromFirebase(userid: String, callback: (ProfileImage) -> Unit){
        val researchRef = FirebaseDatabase.getInstance().getReference("Researcher")
        researchRef.child(userid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val id = snapshot.child("id").value
                    val imageUri = snapshot.child("imageUri").value
                    val timestamp = snapshot.child("timestamp").value
                    val userID = snapshot.child("userID").value
                    val rID = snapshot.child("rID").value
                    val profile = ProfileImage(id.toString(), imageUri.toString(), timestamp.toString(), userID.toString())
                    callback(profile)
                    Log.d("LOL", profile.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    fun fetchIdTermsFromFirebase(callback: (String) -> Unit){
        val researchRef = FirebaseDatabase.getInstance().getReference("Terms")
        researchRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val id = snapshot.value
                    if (id != null) {
                        callback(id.toString())
                    } else {
                        callback("")
                    }
                } else {
                    callback("")
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun fetchIdAboutFromFirebase(callback: (String) -> Unit){
        val researchRef = FirebaseDatabase.getInstance().getReference("About")
        researchRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val id = snapshot.value
                    if (id != null) {
                        callback(id.toString())
                    } else {
                        callback("")
                    }
                } else {
                    callback("")
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


}

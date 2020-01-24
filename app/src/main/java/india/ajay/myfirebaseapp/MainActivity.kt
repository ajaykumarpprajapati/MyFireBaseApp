package india.ajay.myfirebaseapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 123
    }

    private var saveButton: Button? = null
    private var fetchButton: Button? = null
    private var signInButton: Button? = null
    private var db: FirebaseFirestore? = null
    private val TAG: String = "MyFireBaseApp"
    private var quoteEditText: EditText? = null
    private var authortEditText: EditText? = null
    private var displayTextView: TextView? = null
    private var documentReference: DocumentReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        setupFireBaseDataBase()
        addAdaLovelace()
        getAllUsers()
    }

    override fun onStart() {
        super.onStart()
        documentReference?.addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
       }
    }


    private fun saveQuote(){
        val quote = quoteEditText?.text.toString()
        val author = authortEditText?.text.toString()
        val map: MutableMap<String, Any> = HashMap<String, Any>()
        map["quote"] = quote
        map["author"] = author
        documentReference?.set(map)?.addOnSuccessListener {
            Log.d("quote","Saved")
        }?.addOnFailureListener {
            Log.d("onFailure","Failure")
        }
    }

    private fun setupView(){
        authortEditText = findViewById(R.id.author_edit_text)
        displayTextView = findViewById(R.id.quote_text_view)
        fetchButton = findViewById(R.id.fetch_button)
        quoteEditText = findViewById(R.id.quote_edit_text)
        saveButton = findViewById(R.id.save_button)
        signInButton = findViewById(R.id.sign_in_button)

        saveButton?.setOnClickListener {
            saveQuote()
        }

        fetchButton?.setOnClickListener {
            fetchQuote()
        }

        signInButton?.setOnClickListener {
            openPreBuildUI()
        }

        documentReference = FirebaseFirestore.getInstance()
            .document("sampleData/insipration")
    }

    private fun setupFireBaseDataBase(){
        db =  FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db?.firestoreSettings = settings
    }

    private fun addAdaLovelace(){
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )
        // Add a new document with a generated ID
        db?.collection("users")
            ?.add(user)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    private fun addAlanTuring(){
        // Create a new user with a first, middle, and last name
        val user = hashMapOf(
            "first" to "Alan",
            "middle" to "Mathison",
            "last" to "Turing",
            "born" to 1912
        )

        // Add a new document with a generated ID
        db?.collection("users")
            ?.add(user)
            ?.addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            ?.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    private fun getAllUsers(){
        db?.collection("users")
            ?.get()
            ?.addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
            }
            ?.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun fetchQuote(){
        documentReference?.get()?.addOnSuccessListener {
            if(it.exists()){
                val quote = it.getString("quote")
                val author = it.getString("author")
                displayTextView?.text = "$quote : $author"
            }
        }?.addOnFailureListener {

        }
    }

    private fun openPreBuildUI(){
        // Choose authentication providers
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        // Create and launch sign-in intent
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}

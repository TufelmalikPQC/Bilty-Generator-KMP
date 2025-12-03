import com.bilty.generator.model.constants.FirebaseConstants
import com.google.firebase.FirebaseOptions
import com.google.firebase.FirebasePlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.initialize
import kotlinx.coroutines.tasks.await

object FirebaseInitializer {

    private var auth: FirebaseAuth? = null

    fun initialize() {
        // STEP 1: Initialize Firebase platform
        FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
            private val storage = mutableMapOf<String, String>()

            override fun store(key: String, value: String) {
                storage[key] = value
            }

            override fun retrieve(key: String): String? = storage[key]

            override fun clear(key: String) {
                storage.remove(key)
            }

            override fun log(msg: String) {
                println("FirebaseLog: $msg")
            }
        })

        // STEP 2: Setup Firebase options
        val options = FirebaseOptions.Builder()
            .setProjectId(FirebaseConstants.PROJECT_ID)
            .setApplicationId(FirebaseConstants.APPLICATION_ID)
            .setApiKey(FirebaseConstants.API_KEY)
            .setDatabaseUrl(FirebaseConstants.REALTIME_DATABASE_URL)
            .build()

        // STEP 3: Initialize Firebase App
        com.google.firebase.Firebase.initialize(android.app.Application(), options)

        // STEP 4: Initialize Auth
        auth = FirebaseAuth.getInstance()

        println("✅ Firebase initialized successfully with Authentication")
    }

    fun getAuth(): FirebaseAuth {
        return auth ?: throw IllegalStateException("Firebase not initialized. Call initialize() first.")
    }
}
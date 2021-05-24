package com.maxdreher.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Extension of [IContextBase] which provides [signin] and [signout] functions and
 * overrideable listeners ([onSigninSuccess], [onSigninFail], etc.)
 */
interface IGoogleBaseBase : IContextBase {
    val GOOGLE_REQUEST_CODE: Int

    companion object {
        private const val accountNamePrefKey = "accountName"

        @SuppressLint("StaticFieldLeak")
        private var signInClient: GoogleSignInClient? = null

        var account: GoogleSignInAccount? = null
            private set
    }

    val activity: Activity?

    val sharedPreferences: SharedPreferences?
        get() = activity?.getSharedPreferences("googleSharedPref", Context.MODE_PRIVATE)


    fun client(): GoogleSignInClient {
        if (signInClient == null) {
            signInClient = init()
        }
        return signInClient!!
    }

    private fun init(): GoogleSignInClient? {
        call(object {})
        return getContext()?.let {
            GoogleSignIn.getClient(
                it, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
            )
        }
    }


    fun onSigninSuccess(account: GoogleSignInAccount) {
        call(object {})
        toast("Signed into ${account.displayName}")
        try {
            val json = Gson().toJson(account)
            sharedPreferences?.edit()
                ?.putString(accountNamePrefKey, json)
                ?.apply()
        } catch (e: java.lang.Exception) {
            loge("Could not save account: ${e.message}")
            e.printStackTrace()
        }
    }

    fun onSigninFail(exception: Exception) {
        call(object {})
        error("Could not sign in\n${exception.message}")
    }

    fun onSignoutSuccess() {
        call(object {})
        account = null
        toast("Signed out")
    }

    fun onSignoutFail(exception: Exception) {
        call(object {})
        error("Could not sign out\n${exception.message}")
    }

    fun signin() {
        call(object {})
        if (account == null) {
            log("No account found")
            if (sharedPreferences?.contains(accountNamePrefKey) == true) {
                log("Account in shared prefs")
                sharedPreferences?.getString(accountNamePrefKey, null)?.let { json ->
                    log("Has json")
                    try {
                        val acc = Gson().fromJson(json, GoogleSignInAccount::class.java)
                        GlobalScope.launch {
                            withContext(Dispatchers.Main) {
                                onSigninSuccess(acc)
                            }
                        }
                        return
                    } catch (e: java.lang.Exception) {
                        loge("Bad account parse\n")
                        e.printStackTrace()
                    }
                }
            }
            client().let {
                log("Starting signin")
                activity?.startActivityForResult(it.signInIntent, GOOGLE_REQUEST_CODE)
            }
        } else {
            log("Account found")
        }
    }

    fun signout(): Task<Void>? {
        call(object {})
        return client().signOut()
            ?.addOnSuccessListener { onSignoutSuccess() }
            ?.addOnFailureListener { onSignoutFail(it) }
    }


    private fun handleSignin(data: Intent) {
        call(object {})
        val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            account = completedTask.getResult(ApiException::class.java)
                ?.also(this::onSigninSuccess)
        } catch (e: ApiException) {
            onSigninFail(e)
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        call(object {})
        when (requestCode) {
            GOOGLE_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        handleSignin(data!!)
                    }
                    Activity.RESULT_CANCELED -> {
                        toast("Signin cancelled")
                    }
                    else -> onSigninFail(Exception("Bad signin code: $resultCode"))
                }
            }
        }
    }
}
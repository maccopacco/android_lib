package com.maxdreher.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

/**
 * Extension of [IContextBase] which provides [signin] and [signout] functions and
 * overrideable listeners ([onSigninSuccess], [onSigninFail], etc.)
 */
interface IGoogleBaseBase : IContextBase {
    val GOOGLE_REQUEST_CODE: Int

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var signInClient: GoogleSignInClient? = null
        var account: GoogleSignInAccount? = null
            private set
    }

    val fragment: Fragment

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
            client().let {
                fragment.startActivityForResult(it.signInIntent, GOOGLE_REQUEST_CODE)
            }
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
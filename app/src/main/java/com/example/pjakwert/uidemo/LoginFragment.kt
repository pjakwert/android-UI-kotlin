package com.example.pjakwert.uidemo

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView


class LoginFragment : Fragment() {

    lateinit var mLoginButton: Button
    lateinit var mPasswordEditText: EditText
    lateinit var mLoginEditText: EditText
    lateinit var mLoginProgressBar: ProgressBar

    /*
    override fun onDetach() {
        super.onDetach()
        val imManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imManager.hideSoftInputFromWindow( activity.currentFocus.windowToken, 0)
    }
    */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setContentView(R.layout.activity_login);

        mLoginButton = getView()!!.findViewById(R.id.loginButton)
        mPasswordEditText = getView()!!.findViewById(R.id.passwordEditText)
        mLoginEditText = getView()!!.findViewById(R.id.loginEditText)
        mLoginProgressBar = getView()!!.findViewById(R.id.loginProgress)

        mPasswordEditText.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_ACTION_GO) {
                doLogin()
                return@OnEditorActionListener true
            }
            false
        })

        mLoginButton.setOnClickListener { doLogin() }
    }


    private fun validate(): Boolean {
        mLoginEditText.error = null
        mPasswordEditText.error = null

        val loginText = mLoginEditText.text.toString()
        if (loginText.isEmpty()) {
            mLoginEditText.error = "Login cannot be empty"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(loginText).matches()) {
            mLoginEditText.setError("Login must be valid email address");
            return false;
        }

        val passwordText = mPasswordEditText.text.toString()
        if (passwordText.isEmpty()) {
            mPasswordEditText.error = "Password cannot be empty"
            return false
        }

        return true
    }

    private fun enableUi(yes: Boolean) {

        mLoginButton.isEnabled = yes
        //mPasswordEditText.setEnabled(yes);
        //mLoginEditText.setEnabled(yes);

        /*
        if (yes) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }*/
    }

    private fun doLogin() {

        if (!validate()) return

        val imManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imManager.hideSoftInputFromWindow( activity.currentFocus.windowToken, 0)

        mLoginProgressBar.visibility = View.VISIBLE
        enableUi(false)

        mLoginProgressBar.postDelayed({
            mLoginProgressBar.visibility = View.GONE
            enableUi(true)
        }, 3000)
    }

    /*
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    } */
}

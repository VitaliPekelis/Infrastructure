package com.madanes.myapp

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.*
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler
import com.madanes.myapp.aws.CognitoIdentityHelper
import com.madanes.myapp.utils.Logger
import com.madanes.myapp.utils.addFragment
import com.madanes.myapp.utils.replaceFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


class IdentityActivity : AppCompatActivity(), SignInFragment.OnFragmentInteractionListener,
        SignUpFragment.OnInteractionListener, SignUpConfirmFragment.OnFragmentInteractionListener,
        ForgotPasswordFragment.OnFragmentInteractionListener,
        UserDetailsFragment.OnFragmentInteractionListener
{
    private var forgotPasswordContinuation: ForgotPasswordContinuation? = null

    private var mIdentityHelper: CognitoIdentityHelper? = null

    private val mAuthenticationHandler = object :AuthenticationHandler
    {
        override fun onSuccess(userSession: CognitoUserSession,
                               newDevice: CognitoDevice?)
        {
            /*if (BuildConfig.DEBUG) Logger.logDebug("Authentication", """Success newDevice: ${newDevice.toString()}
                                        accessToken.expiration = ${userSession?.accessToken?.expiration}
                                        accessToken.jwtToken = ${userSession?.accessToken?.jwtToken}
                                        accessToken.username = ${userSession?.accessToken?.username}
                                        refreshToken.token = ${userSession?.refreshToken?.token}
                                        isValidForThreshold = ${userSession?.isValidForThreshold}
                                        isValid = ${userSession?.isValid}
                                        username = ${userSession?.username}
                                        """)*/

            UserDetailsFragment.newInstance(userSession.username).also {
                addFragment(fragment_container.id, it)
            }
        }

        override fun onFailure(exception: Exception?)
        {
            exception?.printStackTrace()
        }

        override fun getAuthenticationDetails(
                authenticationContinuation: AuthenticationContinuation,
                userName: String)
        {
            onGetAuthenticationDetailsArrived(authenticationContinuation, userName)
        }


        override fun authenticationChallenge(continuation: ChallengeContinuation?)
        {
            /**
             * For Custom authentication challenge, implement your logic to present challenge to the
             * user and pass the user's responses to the continuation.
             */


            //TODO: VITALI contineu implement this method
            /*if ("NEW_PASSWORD_REQUIRED" == continuation.getChallengeName())
            {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = continuation as NewPasswordContinuation
                mIdentityHelper.setUserAttributeForDisplayFirstLogIn(
                        newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes())
                closeWaitDialog()
                firstTimeSignIn()
            } else if ("SELECT_MFA_TYPE" == continuation.getChallengeName())
            {
                closeWaitDialog()
                mfaOptionsContinuation = continuation as ChooseMfaContinuation
                val mfaOptions = mfaOptionsContinuation.getMfaOptions()
                selectMfaToSignIn(mfaOptions, continuation.getParameters())
            }*/
        }

        override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?)
        {
            //TODO: VITALI contineu implement this method
            /*mfaAuth(multiFactorAuthenticationContinuation)*/
        }
    }

    /*Views*/
    private val mDialog: AlertDialog? by lazy {
        createDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mIdentityHelper = CognitoIdentityHelper.getInstance(this.applicationContext)

        if (savedInstanceState == null)
        {
            SignInFragment.newInstance().also {
                replaceFragment(fragment_container.id, it)
            }

            findCurrentUser()
        }

    }

    private fun findCurrentUser()
    {
        val identityHelper = CognitoIdentityHelper.getInstance(this.applicationContext)
        val user = identityHelper.userPool.currentUser

        identityHelper.username = user.userId

        identityHelper.username?.let {

            user.getSessionInBackground(mAuthenticationHandler)

        }
    }

    private fun mfaAuth(continuation: MultiFactorAuthenticationContinuation)
    {
        //TODO: VITALI contineu implement this method

        /*multiFactorAuthenticationContinuation = continuation
        val mfaActivity = Intent(this, MFAActivity::class.java)
        mfaActivity.putExtra("mode",
                multiFactorAuthenticationContinuation.getParameters().getDeliveryMedium())
        startActivityForResult(mfaActivity, 5)*/
    }

    override fun onDestroy()
    {
        mIdentityHelper = null
        mDialog?.let {
            if (it.isShowing)
            {
                it.dismiss()
            }
        }
        super.onDestroy()
    }

    private fun createDialog(): AlertDialog
    {
        return AlertDialog.Builder(this).create()
    }

    /**
     * Show default Alert Dialog with Button Ok onClick closed the mDialog.
     *
     * @param title Dialog Title.
     * @param body Dialog Body Message.
     * @param fragmentTagToClose Fragment Tag To close.
     *                           If NULL only closed Dialog,
     *                           if not NULL removed fragment from back stack. Default value NULL
     */
    private fun showDialogFromActivity(title: String, body: String, fragmentTagToClose: String? = null)
    {
        mDialog?.apply {
            setTitle(title)
            setMessage(body)
            setButton(AlertDialog.BUTTON_NEGATIVE, "OK") { dialog, _ ->
                if (this.isShowing)
                {
                    dialog.dismiss()

                    fragmentTagToClose?.let { tag ->
                        if (supportFragmentManager.backStackEntryCount > 0)
                        {
                            if (!supportFragmentManager.popBackStackImmediate(tag,
                                            FragmentManager.POP_BACK_STACK_INCLUSIVE))
                            {
                                //error to log if fragment not closed
                                if (BuildConfig.DEBUG) Logger.logError("IdentityActivity",
                                        "popBackStack fragment $tag Failed")
                            }
                        }
                    }
                }
            }
        }

        mDialog?.show()
    }

    private fun lunchForgotPasswordFragment(forgotPasswordContinuation: ForgotPasswordContinuation)
    {
        this.forgotPasswordContinuation = forgotPasswordContinuation

        ForgotPasswordFragment.newInstance(
                forgotPasswordContinuation.parameters.destination,
                forgotPasswordContinuation.parameters.deliveryMedium).also { fragment ->
            addFragment(fragment_container.id, fragment)
        }
    }

    private fun onGetAuthenticationDetailsArrived(
            authenticationContinuation: AuthenticationContinuation,
            userName: String)
    {
        mIdentityHelper?.username = userName

        val authenticationDetails = AuthenticationDetails(mIdentityHelper?.username, mIdentityHelper?.password, null)
        authenticationContinuation.setAuthenticationDetails(authenticationDetails)
        authenticationContinuation.continueTask()
    }

    //----------------------------------------------------------------------------------------------
    // SignUpConfirmFragment.OnInteractionListener - implementation
    //----------------------------------------------------------------------------------------------
    override fun confirmationSuccess(userName: String)
    {
        showDialogFromActivity("Success!", "$userName has been confirmed!",
                SignUpConfirmFragment::class.java.simpleName)
    }

    override fun showDialogMessage(title: String, body: String, fragmentTagToClose: String?)
    {
        showDialogFromActivity(title, body, fragmentTagToClose)
    }

    //----------------------------------------------------------------------------------------------
    // SignUpFragment.OnInteractionListener - implementation
    //----------------------------------------------------------------------------------------------
    override fun openConformSignUpFragment(usernameInput: String,
                                           passwordInput:String,
                                           cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails?)
    {
        SignUpConfirmFragment.newInstance(name = usernameInput,
                password = passwordInput,
                destination = cognitoUserCodeDeliveryDetails?.destination,
                deliveryMed = cognitoUserCodeDeliveryDetails?.deliveryMedium,
                attribute = cognitoUserCodeDeliveryDetails?.attributeName).also {
            addFragment(fragment_container.id, it)
        }
    }


    //----------------------------------------------------------------------------------------------
    // SignInFragment.OnInteractionListener - implementation
    //----------------------------------------------------------------------------------------------
    override fun signUpNewUser()
    {
        SignUpFragment.newInstance().also {
            addFragment(fragment_container.id, it)
        }
    }

    override fun forgotPasswordUser(userName: String)
    {
        mIdentityHelper?.userPool?.getUser(userName)?.forgotPasswordInBackground(
                object : ForgotPasswordHandler
                {
                    override fun onSuccess()
                    {
                        showDialogFromActivity("Password","Successfully changed!",
                                ForgotPasswordFragment::class.java.simpleName)

                        /*val view = window.decorView.findViewById(android.R.id.content) as View
                        Snackbar.make(view, "Will be soon, in developer process ",
                                Snackbar.LENGTH_INDEFINITE).also { snackBar ->
                            snackBar.setAction("Ok") {
                                snackBar.dismiss()
                            }
                            snackBar.show()
                        }

                        Logger.logDebug()*/
                    }

                    override fun onFailure(exception: Exception?)
                    {
                        exception?.printStackTrace()
                        showDialogFromActivity("Password", "Forgot password failed")

                        /*val view = window.decorView.findViewById(android.R.id.content) as View
                        Snackbar.make(view, "Will be soon, in developer process ",
                                Snackbar.LENGTH_INDEFINITE).also { snackBar ->
                            snackBar.setAction("Ok") {
                                snackBar.dismiss()
                            }
                            snackBar.show()
                        }*/
                    }

                    override fun getResetCode(continuation: ForgotPasswordContinuation?)
                    {
                        /*val view = window.decorView.findViewById(android.R.id.content) as View
                        Snackbar.make(view, "Will be soon, in developer process ",
                                Snackbar.LENGTH_INDEFINITE).also {  snackBar -> snackBar.setAction("Ok") {
                            snackBar.dismiss()
                        }}.show()*/

                        continuation?.let { lunchForgotPasswordFragment(it) }
                    }
                })
    }

    override fun signInClick(username: String, password: String)
    {
        mIdentityHelper?.username = username
        mIdentityHelper?.password = password

        mIdentityHelper?.userPool?.getUser(username)?.getSessionInBackground(mAuthenticationHandler)
    }

    //----------------------------------------------------------------------------------------------
    // ForgotPasswordFragment.OnFragmentInteractionListener - implementation
    //----------------------------------------------------------------------------------------------
    override fun onSendForgotPasswordClick(newPassword: String, verificationCode: String)
    {
        forgotPasswordContinuation?.setPassword(newPassword)
        forgotPasswordContinuation?.setVerificationCode(verificationCode)
        forgotPasswordContinuation?.continueTask()

        /*val view = window.decorView.findViewById(android.R.id.content) as View
        Snackbar.make(view, "Will be soon, in developer process ",
                Snackbar.LENGTH_INDEFINITE).also { snackBar ->
            snackBar.setAction("Ok") {
                snackBar.dismiss()
            }
            snackBar.show()
        }*/
    }

    //----------------------------------------------------------------------------------------------
    // UserDetailsFragment.OnFragmentInteractionListener - implementation
    //----------------------------------------------------------------------------------------------
    override fun onUserSignOutClick()
    {
        mIdentityHelper?.userPool?.currentUser?.signOut()
        showDialogFromActivity("להתנתק", "בטוח?", UserDetailsFragment::class.java.simpleName)
    }
}

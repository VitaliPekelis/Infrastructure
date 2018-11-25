package com.madanes.myapp


import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler
import com.google.android.material.snackbar.Snackbar
import com.madanes.myapp.aws.CognitoIdentityHelper
import com.madanes.myapp.aws.CognitoIdentityHelper.Companion.EMAIL
import com.madanes.myapp.aws.CognitoIdentityHelper.Companion.PHONE_NUMBER
import kotlinx.android.synthetic.main.fragment_sign_up.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpFragment.OnInteractionListener] interface
 * to handle interaction events.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpFragment : Fragment()
{
    /*private var param1: String? = null
    private var param2: String? = null*/


    private var mUsernameInput: String? = null
    private var mPasswordInput: String? = null

    private var listener: OnInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
           /* param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        setupListeners()
    }

    private fun setupListeners()
    {
        user_name_edt.addTextChangedListener(object: TextWatcher  by TextWatcherDelegate()
        {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                //clear error
                if(s.isNotEmpty())
                {
                    user_name_til.error = null
                }
            }
        })

        sign_up_password_edt.addTextChangedListener(object: TextWatcher  by TextWatcherDelegate()
        {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int)
            {
                //clear error
                if(s.isNotEmpty())
                {
                    sign_up_password_til.helperText = getString(R.string.sign_up_password_helper_text)

                    //sign_up_password_til.error = null
                }
            }
        })

        sign_up_bttn.setOnClickListener{ signUp()}
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        if (context is OnInteractionListener)
        {
            listener = context
        } else
        {
            throw RuntimeException(context.toString() + """must implement ${OnInteractionListener::class.java.simpleName}""")
        }
    }

    override fun onDetach()
    {
        super.onDetach()
        listener = null
    }


    private fun signUp()
    {

        val helper = CognitoIdentityHelper.getInstance(context!!.applicationContext)
        // Read user data and register
        val userAttributes = CognitoUserAttributes()

        mUsernameInput = user_name_edt.text.toString()
        if (mUsernameInput == null || mUsernameInput!!.isEmpty())
        {
            user_name_til.error = ("${user_name_til.hint} cannot be empty")

            //TODO: 'username'  Member must satisfy regular expression pattern: [\p{L}\p{M}\p{S}\p{N}\p{P}]+ implement later

            return
        }

        mPasswordInput = sign_up_password_edt.text.toString()
        if (mPasswordInput == null || mPasswordInput!!.isEmpty())
        {
            sign_up_password_til.error = ("${sign_up_password_til.hint} cannot be empty")
            return
        }

        var userInput: String?

        userInput = sign_up_email_edt.text.toString()
        if (userInput.isNotEmpty())
        {
            userAttributes.addAttribute(
                    helper.signUpFieldsC2O[EMAIL],
                    userInput)
        }


        userInput = sign_up_phone_edt.text.toString()
        if (userInput.isNotEmpty())
        {
            userAttributes.addAttribute(
                    helper.signUpFieldsC2O[PHONE_NUMBER],
                    userInput)
        }


        //showWaitDialog("Signing up...")

        CognitoIdentityHelper.getInstance(context!!.applicationContext)
                .userPool
                .signUpInBackground(mUsernameInput,
                        mPasswordInput,
                        userAttributes,
                        null,
                        object : SignUpHandler
                        {
                            override fun onFailure(exception: Exception?)
                            {
                                exception?.printStackTrace()

                                if(isAdded)
                                {
                                    Snackbar.make(view!!, "onFailure Sign Up", Snackbar.LENGTH_SHORT).show()
                                }

                            }

                            override fun onSuccess(user: CognitoUser?, signUpConfirmationState: Boolean,
                                           cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails?)
                            {
                                if(isAdded)
                                {
                                    onSuccessSignUpArrived(user, signUpConfirmationState, cognitoUserCodeDeliveryDetails)
                                }
                            }
                        })

    }

    private fun onSuccessSignUpArrived(user: CognitoUser?, signUpConfirmationState: Boolean,
                                       cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails?)
    {
        if(signUpConfirmationState) // User is already confirmed
        {
            Snackbar.make(view!!, "Success $mUsernameInput SignUp !!!", Snackbar.LENGTH_SHORT).show()
        }
        else // User is not confirmed
        {
            listener?.openConformSignUpFragment(mUsernameInput!!, mPasswordInput!!, cognitoUserCodeDeliveryDetails)
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnInteractionListener
    {
        fun openConformSignUpFragment(usernameInput: String,
                                      passwordInput: String,
                                      cognitoUserCodeDeliveryDetails: CognitoUserCodeDeliveryDetails?)
    }

    companion object
    {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =

                SignUpFragment().apply {
                    arguments = Bundle().apply {
                        /*putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)*/
                    }
                }
    }
}

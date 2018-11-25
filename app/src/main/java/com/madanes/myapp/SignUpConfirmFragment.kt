package com.madanes.myapp

import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler
import com.google.android.material.snackbar.Snackbar
import com.madanes.myapp.aws.CognitoIdentityHelper
import kotlinx.android.synthetic.main.fragment_sign_up_confirm.*
import java.lang.Exception


private const val ARG_USER_NAME = "user_name"
private const val ARG_USER_PASSWORD = "user_password"
private const val ARG_DESTINATION = "destination"
private const val ARG_DELIVERY_MED = "delivery_med"
private const val ARG_ATTRIBUTE = "attribute"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SignUpConfirmFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SignUpConfirmFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SignUpConfirmFragment : Fragment()
{
    private lateinit var mUserName: String
    private lateinit var mPassword: String

    private var destination: String? = null
    private var deliveryMed: String? = null
    private var attribute: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUserName =      it.getString(ARG_USER_NAME)!!
            mPassword =      it.getString(ARG_USER_PASSWORD)!!
            destination =   it.getString(ARG_DESTINATION)
            deliveryMed =   it.getString(ARG_DELIVERY_MED)
            attribute =     it.getString(ARG_ATTRIBUTE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up_confirm, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        initUI()
        setListeners()
    }

    private fun setListeners()
    {
        sign_up_conform_resend_btn.setOnClickListener {
            requestConfirmationCode()
        }

        sign_up_conform_send_btn.setOnClickListener{
            sendConformCode()
        }

        sign_up_conform_username_edt.addTextChangedListener(object:TextWatcher  by TextWatcherDelegate()
        {
            override fun onTextChanged(s: CharSequence, start: Int, befor: Int, count: Int)
            {
                if(s.isNotEmpty())
                {
                    sign_up_conform_username_til.helperText = ""
                }
            }
        })
    }

    private fun sendConformCode()
    {
        mUserName = sign_up_conform_username_edt.text.toString()
        val conformCode = sign_up_conform_code_edt.text.toString()

        if (mUserName.isEmpty())
        {
            sign_up_conform_username_til.error = """${sign_up_conform_username_til.hint} cannot be empty"""
            return
        }

        if (conformCode.isEmpty())
        {
            sign_up_conform_code_til.error = """${sign_up_conform_code_til.hint} cannot be empty"""
            return
        }

        val identityHelper = CognitoIdentityHelper.getInstance(context!!)

        identityHelper.userPool.getUser(mUserName)
                .confirmSignUpInBackground(conformCode, false,object : GenericHandler
        {
            override fun onSuccess()
            {
                if(isAdded)
                {
                    identityHelper.password = mPassword
                    identityHelper.username = mUserName

                    listener?.confirmationSuccess(mUserName)
                }
            }

            override fun onFailure(exception: Exception?)
            {
                exception?.printStackTrace()
                if (isAdded)
                {
                    Toast.makeText(context!!, "Confirm was Failure",Toast.LENGTH_SHORT).show()
                    //TODO handle of error
                }

            }
        })
    }

    private fun requestConfirmationCode()
    {
        mUserName = sign_up_conform_username_edt.text.toString()

        if (mUserName.isEmpty())
        {
            sign_up_conform_username_til.error = """${sign_up_conform_username_til.hint} cannot be empty"""
            return
        }

        CognitoIdentityHelper.getInstance(context!!.applicationContext).userPool.getUser(mUserName)
                .resendConfirmationCodeInBackground(object: VerificationHandler{
                    override fun onSuccess(
                            verificationCodeDeliveryMedium: CognitoUserCodeDeliveryDetails)
                    {
                        if (isAdded)
                        {
                            sign_up_conform_code_edt.requestFocus()
                            listener?.showDialogMessage("Confirmation code sent.",
                                    "Code sent to " + verificationCodeDeliveryMedium.destination + " via " + verificationCodeDeliveryMedium.deliveryMedium + ".")
                        }
                    }

                    override fun onFailure(exception: Exception)
                    {
                        exception.printStackTrace()
                        if (isAdded)
                        {
                            Snackbar.make(view!!, "Confirmation code resend failed", Snackbar.LENGTH_INDEFINITE ).also { snack ->
                                snack.setAction("Ok") { snack.dismiss() }
                                        .show()
                            }
                        }
                    }
                })
    }

    private fun initUI()
    {
        sign_up_conform_username_edt.setText(mUserName)

        sign_up_conform_code_edt.requestFocus()

        var text = "A confirmation code was sent"

        destination?.let {
            text = """$text to $it"""
            deliveryMed?.let {
                text = """$text via $it"""
            }
        }

        sign_up_conform_sanded_to_tv.text = text
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener)
        {
            listener = context
        } else
        {
            throw RuntimeException(
                     """$context  must implement ${SignInFragment.OnFragmentInteractionListener::class.java.simpleName}""")


        }
    }

    override fun onDetach()
    {
        super.onDetach()
        listener = null
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
    interface OnFragmentInteractionListener
    {
        fun confirmationSuccess(userName:String)
        fun showDialogMessage(title: String, body: String, fragmentTagToClose: String? = null)
    }

    companion object
    {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param name User Name.
         * @param destination Destination.
         * @param deliveryMed DeliveryMed.
         * @param attribute Attribute.
         * @return A new instance of fragment SignUpConfirmFragment.
         */

        @JvmStatic
        fun newInstance(name: String,
                        password: String,
                        destination: String?,
                        deliveryMed: String?,
                        attribute: String?) = SignUpConfirmFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_NAME, name)
                putString(ARG_USER_PASSWORD, password)
                destination?.let{putString(ARG_DESTINATION, it)}
                deliveryMed?.let{putString(ARG_DELIVERY_MED, it)}
                attribute?.let{putString(ARG_ATTRIBUTE, it) }
            }
        }
    }
}

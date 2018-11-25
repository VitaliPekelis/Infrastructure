package com.madanes.myapp


import android.content.Context
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_forgot_password.*


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_DESTINATION = "arg_destination"
private const val ARG_DELIVERY_MED = "arg_deliveryMed"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ForgotPasswordFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ForgotPasswordFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class ForgotPasswordFragment : Fragment()
{
    private var destination: String? = null
    private var deliveryMed: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            destination = it.getString(ARG_DESTINATION)
            deliveryMed = it.getString(ARG_DELIVERY_MED)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        setListeners()
        forgot_password_title_tv.text = """Code to set a new password was sent to $destination via $deliveryMed"""
    }

    private fun setListeners()
    {
        forgot_password_send_btn.setOnClickListener{ sendNewPassword()}

        forgot_password_pass_edt.addTextChangedListener(object : TextWatcher by TextWatcherDelegate()
        {
            override fun onTextChanged(s: CharSequence, start: Int, befor: Int, count: Int)
            {
                if(s.isNotEmpty())
                {
                    forgot_password_pass_til.helperText = ""
                }
            }
        })

        forgot_password_code_edt.addTextChangedListener(object : TextWatcher by TextWatcherDelegate()
        {
            override fun onTextChanged(s: CharSequence, start: Int, befor: Int, count: Int)
            {
                if(s.isNotEmpty())
                {
                    forgot_password_code_til.helperText = ""
                }
            }
        })

    }

    private fun sendNewPassword()
    {
        val newPassword = forgot_password_pass_edt.text.toString()
        if(newPassword.isEmpty())
        {
            forgot_password_pass_til.error  = """${forgot_password_pass_til.hint} cannot be empty} """
            return
        }

        val code = forgot_password_code_edt.text.toString()
        if(code.isEmpty())
        {
            forgot_password_code_til.error  = """${forgot_password_code_til.hint} cannot be empty} """
            return
        }


        listener?.onSendForgotPasswordClick(newPassword, code)
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
                    """$context must implement ${ForgotPasswordFragment.OnFragmentInteractionListener::class.java.simpleName}""")
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
        fun onSendForgotPasswordClick(newPassword:String, verificationCode:String)
    }

    companion object
    {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ForgotPasswordFragment.
         */
        @JvmStatic
        fun newInstance(destination: String, deliveryMed: String) = ForgotPasswordFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_DESTINATION, destination)
                putString(ARG_DELIVERY_MED, deliveryMed)
            }
        }
    }
}

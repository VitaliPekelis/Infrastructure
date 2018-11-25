package com.madanes.myapp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_sign_in.*

/**
 * Activities that contain this fragment must implement the
 * [SignInFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignInFragment : Fragment()
{
    /*private var mParam1: String? = null
    private var mParam2: String? = null*/

    private var mListener: OnFragmentInteractionListener? = null

    companion object
    {
        // the fragment initialization parameters

        /*private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"*/

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignInFragment.
         */
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =

                SignInFragment().apply {
                    arguments = Bundle().apply {
                        /*putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)*/

                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        arguments?.let{
            /*mParam1 = it.getString(ARG_PARAM1)
            mParam2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_in, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        forgot_pass_button.setOnClickListener{ forgotPasswordUserClick() }
        sign_in_sign_up_btn.setOnClickListener{ signUpNewUserClick() }

        sign_in_send_btn.setOnClickListener { signInClick() }


        sign_in_username_edt.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?)
            {
                s?.let{
                  if(it.isNotEmpty()){
                      sign_in_username_til.error = null
                  }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int)
            {}

        })
    }

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener)
        {
            mListener = context
        } else
        {
            throw RuntimeException(context!!.toString() + """ must implement  + ${SignUpConfirmFragment.OnFragmentInteractionListener::class.java.simpleName}""")
        }
    }

    override fun onDetach()
    {
        super.onDetach()
        mListener = null
    }

    private fun signInClick()
    {
        val username = sign_in_username_edt.text.toString()
        if (username.isEmpty())
        {
            sign_in_username_til.error = ("""${sign_in_username_til.hint.toString()}  ${getString(R.string.can_not_be_empty)}""")
            return
        }

        val password = sign_in_password_edt.text.toString()
        if (password.isEmpty())
        {
            sign_in_password_til.error = ("""${sign_in_password_til.hint.toString()}  ${getString(R.string.can_not_be_empty)}""")
            return
        }

        mListener?.signInClick(username, password)
    }
    private fun forgotPasswordUserClick()
    {
        val username = sign_in_username_edt.text.toString()
        if (username.isEmpty())
        {
            sign_in_username_til.error = ("""${sign_in_username_til.hint.toString()}  ${getString(R.string.can_not_be_empty)}""")
            return
        }

        //showWaitDialog("")

        mListener?.forgotPasswordUser(username)
    }

    private fun signUpNewUserClick()
    {
        mListener?.signUpNewUser()
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener
    {
        fun signUpNewUser()
        fun forgotPasswordUser(userName:String)
        fun signInClick(username: String, password: String)
    }

} // Required empty public constructor

package com.madanes.myapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler
import com.madanes.myapp.aws.CognitoIdentityHelper
import kotlinx.android.synthetic.main.fragment_user_details.*
import java.lang.Exception


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_USER_NAME = "arg_user_name"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [UserDetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [UserDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserDetailsFragment : Fragment()
{
    private var userName: String? = null
    private val mAdapter = UserDetailsAdapter()
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userName = it.getString(ARG_USER_NAME, null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        user_details_rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
        getUserDetails()

        user_details_sign_out_btn.isEnabled = false
        user_details_sign_out_btn.setOnClickListener{
            listener?.onUserSignOutClick()
        }
    }

    private fun getUserDetails()
    {
        CognitoIdentityHelper.getInstance(context!!.applicationContext).userPool
                .getUser(userName).getDetailsInBackground(
                        object: GetDetailsHandler
                        {
                            override fun onSuccess(cognitoUserDetails: CognitoUserDetails?)
                            {
                                if(isAdded)
                                {
                                    user_details_sign_out_btn.isEnabled = true

                                    val attributesMap = cognitoUserDetails?.attributes?.attributes!!
                                    mAdapter.data =
                                            convertDataForRecyclerView(attributesMap)
                                }
                            }

                            override fun onFailure(exception: Exception?)
                            {
                                exception?.printStackTrace()
                            }

                        })
    }

    private fun convertDataForRecyclerView(attributes: Map<String, String>): List<UserDetailItemData>
    {
        val result = attributes.map { UserDetailItemData(it.key, it.value) }
        return result
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
                    """$context must implement ${OnFragmentInteractionListener::class.java.simpleName}""")
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
        fun onUserSignOutClick()
    }

    companion object
    {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param userName UserId parameter
         * @return A new instance of fragment UserDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(userName: String?) = UserDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_USER_NAME, userName)
            }
        }
    }
}

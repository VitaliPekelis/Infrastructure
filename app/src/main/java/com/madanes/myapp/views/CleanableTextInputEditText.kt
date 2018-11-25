package com.madanes.myapp.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.textfield.TextInputEditText
import com.madanes.myapp.R


/**
 *  Attributes:
 *
 *  app:cleanable_icon = "@drawable/some_drawable"
 *
 * */
class CleanableTextInputEditText : TextInputEditText, View.OnTouchListener, View.OnFocusChangeListener,  TextWatcher
{

    private var mCleanableDrawable:Drawable? = null
    private var mOnTouchListener: View.OnTouchListener? = null
    private var mOnFocusChangeListener: View.OnFocusChangeListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet): super(context, attrs)
    {
        setupAttrs(attrs)
        initListeners()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    {
        setupAttrs(attrs)
        initListeners()
    }


    private fun initListeners()
    {
        super.setOnTouchListener(this)
        super.setOnFocusChangeListener(this)
        addTextChangedListener(this)
    }

    private fun setupAttrs(attrs: AttributeSet)
    {
        val a = context.obtainStyledAttributes(
                attrs,
                R.styleable.cleanable_text_input_edit_text,
                0, 0)
        try
        {
            val drawableResId = a.getResourceId(R.styleable.cleanable_text_input_edit_text_cleanable_icon, -1)

            if(drawableResId > 0)
            {
                mCleanableDrawable = AppCompatResources.getDrawable(context, drawableResId)
                setClearIconVisible(false)
            }
        }
        finally
        {
            a.recycle()
        }
    }

    override fun setOnTouchListener(onTouchListener: View.OnTouchListener?)
    {
        mOnTouchListener = onTouchListener
    }

    override fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener?)
    {
        mOnFocusChangeListener = onFocusChangeListener
    }

    override fun onDetachedFromWindow()
    {
        mOnFocusChangeListener = null
        mOnTouchListener = null
        super.onDetachedFromWindow()
    }

    private fun setClearIconVisible(visible: Boolean)
    {
        mCleanableDrawable?.let{
            it.setVisible(visible, false)
            val compoundDrawables = compoundDrawables
            setCompoundDrawablesWithIntrinsicBounds(
                    compoundDrawables[0],
                    compoundDrawables[1],
                    if (visible) mCleanableDrawable else null,
                    compoundDrawables[3])
        }
    }


    //---------------------------------------------------------
    // View.OnTouchListener - implementation
    //---------------------------------------------------------
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean
    {
        val x = motionEvent.x.toInt()

        mCleanableDrawable?.let {
            if (it.isVisible && x > width - paddingRight - it.intrinsicWidth)
            {
                if (motionEvent.action == MotionEvent.ACTION_UP)
                {
                    error = null
                    setText("")
                }
                return true
            }
        }

        return mOnTouchListener != null && mOnTouchListener!!.onTouch(view, motionEvent)
    }

    //---------------------------------------------------------
    // TextWatcher - implementation
    //---------------------------------------------------------
    override fun afterTextChanged(p0: Editable?)
    {}

    override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int)
    {}

    override fun onTextChanged(text: CharSequence, start: Int, lengthBefore: Int, lengthAfter: Int)
    {
        if (isFocused) {
            setClearIconVisible(text.isNotEmpty())
        }
    }


    //-------------------------------------------------------------------
    //View.OnFocusChangeListener
    //-------------------------------------------------------------------
    override fun onFocusChange(view: View, hasFocus: Boolean)
    {
        if (hasFocus)
        {
            setClearIconVisible(text!!.isNotEmpty())
        }
        else
        {
            setClearIconVisible(false)
        }

        mOnFocusChangeListener?.onFocusChange(view, hasFocus)
    }
}
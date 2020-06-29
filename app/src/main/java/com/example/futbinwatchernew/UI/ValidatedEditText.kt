package com.example.futbinwatchernew.UI

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class ValidatedEditText @JvmOverloads constructor(context: Context,attrs:AttributeSet? = null,
                                                  defStyleAttr:Int = android.R.attr.editTextStyle ):
    AppCompatEditText(context,attrs,defStyleAttr),TextWatcher {
    var isValid = false
    private var validator: Validator? = null
    private var priceListener: TrackedPriceListener? = null
    fun setValidator(newValidator: Validator){
        validator = newValidator
    }
    fun setPriceListener(listener: TrackedPriceListener){
        priceListener = listener
    }

    override fun afterTextChanged(p0: Editable?) {
        print("In AfterTextChanged")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        validator?.let{
            if(!it.validate(text.toString())){
                isValid = false
                setError(it.errorMessage)
            }
            else{
                priceListener?.let {
                    it.onPriceChanged(text.toString())
                }
                isValid = true
            }
        }

        super.onTextChanged(text, start, lengthBefore, lengthAfter)
    }

}
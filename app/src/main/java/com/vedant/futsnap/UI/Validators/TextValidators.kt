package com.vedant.futsnap.UI.Validators

abstract class TextValidator :Validator<String> {
    var errorMessage:String = ""

}

data class TextLengthValidator(val minLength:Int?,val maxLength:Int?):TextValidator(){

    init {
        if(minLength != null && maxLength!= null){
            super.errorMessage = "Field must have at least $minLength characters " +
                    "and at max $maxLength characters"
        }
        else if(minLength!= null){
            super.errorMessage = "Field must have at least $minLength characters"
        }
        else if(maxLength!= null){
            super.errorMessage = "Field must have at max $maxLength characters"
        }
    }



    override fun validate(data: String): Boolean {
        var isValid = true
        minLength?.let {
            if(data.length < it){
                isValid = false
            }
        }
        maxLength?.let {
            if(data.length > it){
                isValid = false
            }
        }

        return isValid
    }

}



data class TextContentValidator(val disallowedCharacters:String):TextValidator(){

    override fun validate(data: String): Boolean {
        var isValid = true
        disallowedCharacters.forEach {character ->
            if(character in data){
                isValid = false
                super.errorMessage = "$character is disallowed"
            }
        }
        return isValid
    }
}
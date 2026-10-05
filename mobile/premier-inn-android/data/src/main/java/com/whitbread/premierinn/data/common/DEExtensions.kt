package com.whitbread.premierinn.domain.common

private const val WHITESPACE = " "

fun String.toGermanIfApplicable(language: String): String {
    return if (language == LANGUAGE_DEUTSCH_DOMAIN) {
        val roomNameMap = mapOf(
            "Double" to "Doppel", "Family" to "Familien",
            "Twin" to "Zerbett", "Accessible" to "Barrierefreies",
            "Single" to "Einzel",
            "double" to "doppel", "family" to "familien",
            "twin" to "zerbett", "accessible" to "barrierefreies",
            "single" to "einzel"
        )
        try {
            roomNameMap.getValue(this)
        } catch (exception : NoSuchElementException) {
            return this
        }
    } else {
        this
    }
}

fun String.translateTitleToEnglishIfApplicable(language: String) : String{
    return if (language == LANGUAGE_DEUTSCH_DOMAIN) {
        this.translateTitleToEnglish()
    } else {
        this
    }
}

fun String.translateFullNameToEnglishIfApplicable(language: String) : String{
    return if (language == LANGUAGE_DEUTSCH_DOMAIN) {
        val split = this.split(WHITESPACE, limit = 2)
        val translateTitleToGerman = split[0].translateTitleToGerman() + WHITESPACE
        translateTitleToGerman + split[1]
    } else {
        this
    }
}

fun String.translateFullNameToEnglishOrGerman(language: String) = this.translateTitleToLanguage(language)


private fun String.translateTitleToLanguage(language: String): String{
    val split = this.split(WHITESPACE, limit = 2)

    val translation:String = if (language == LANGUAGE_DEUTSCH_DOMAIN){
        split[0].translateTitleToGerman()
    }else{
        split[0].translateTitleToEnglish()
    }

    return split.getOrNull(1)?.let {
        translation + WHITESPACE + it
    } ?: translation
}

fun String.translateTitleToGermanIfApplicable(language: String) : String{
    return if (language == LANGUAGE_DEUTSCH_DOMAIN) {
        this.translateTitleToGerman()
    } else {
        this
    }
}

fun String.translateTitleToEnglish() : String{
    val titleMap = mapOf(
        "Herr" to "Mr",
        "Frau" to "Mrs",
        "Fräulein" to "Ms",
        "Mag" to "Master"
    )
    return try {
        titleMap.getValue(this)
    } catch (exception : NoSuchElementException) {
        this
    }
}

fun String.translateTitleToGerman() : String{
    val titleMap = mapOf(
        "Mr" to "Herr",
        "Mrs" to "Frau",
        "Ms" to "Fräulein",
        "Miss" to "Fräulein",
        "Master" to "Mag"
    )
    return try {
        titleMap.getValue(this)
    } catch (exception : NoSuchElementException) {
        this
    }
}

fun List<Guest>.toRoomLeadGuestWithMappedTitle(language: String): List<Guest> {
    val listOfGuest = mutableListOf<Guest>()
    return if (language == LANGUAGE_DEUTSCH_DOMAIN) {
        this.map {
            val mappedTitleGuest = it.copy(title = it.title.translateTitleToGerman())
            listOfGuest.add(mappedTitleGuest)
        }
        listOfGuest
    } else {
        this
    }
}

fun Guest.mapTitleToGerman(): Guest {
    return this.copy(title = this.title.translateTitleToGerman())
}

fun LeadGuest.toLeadGuestMappedTitle(language: String): LeadGuest {
    return if (language == LANGUAGE_DEUTSCH_DOMAIN) {
        this.copy(guest = guest.mapTitleToGerman())
    } else {
        this
    }
}
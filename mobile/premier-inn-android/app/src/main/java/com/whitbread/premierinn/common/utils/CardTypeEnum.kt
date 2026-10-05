package com.whitbread.premierinn.common.utils

enum class CardTypeEnum(val cardTypeCode: String) {
    AT("Business Account Card"),
    BD("InnBusiness Pay Card"),
    VI("Visa Credit"),
    MD("Mastercard Debit"),
    MA("Maestro"),
    DL("Visa Debit"),
    AC("Mastercard Credit"),
    AM("American Express"),
    DI("Diners Club"),
    EL("Electron")
}

enum class CardTypeEnumOpera(val cardTypeCode: String) {
    PI("Account Card"),
    BD("InnBusiness Pay"),
    AX("American Express"),
    VS("Visa"),
    MA("Maestro"),
    MC("Mastercard"),
    DN("Diners Club"),
    PP("Paypal"),
    GP("Google")
}
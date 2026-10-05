package com.whitbread.premierinn.common.mapper

import com.whitbread.premierinn.common.mapper.Mapper.AMEX
import com.whitbread.premierinn.common.mapper.Mapper.BUSINESS_ACCOUNT
import com.whitbread.premierinn.common.mapper.Mapper.BUSINESS_ACCOUNT_EURO
import com.whitbread.premierinn.common.mapper.Mapper.DINERS_CLUB
import com.whitbread.premierinn.common.mapper.Mapper.ELECTRON
import com.whitbread.premierinn.common.mapper.Mapper.GOOGLE_PAY
import com.whitbread.premierinn.common.mapper.Mapper.MAESTRO
import com.whitbread.premierinn.common.mapper.Mapper.MASTERCARD_CREDIT
import com.whitbread.premierinn.common.mapper.Mapper.MASTERCARD_DEBIT
import com.whitbread.premierinn.common.mapper.Mapper.PAYPAL
import com.whitbread.premierinn.common.mapper.Mapper.VISA_CREDIT
import com.whitbread.premierinn.common.mapper.Mapper.VISA_DEBIT
import com.whitbread.premierinn.common.utils.CardTypeEnum
import com.whitbread.premierinn.common.utils.CardTypeEnumOpera
import com.whitbread.premierinn.data.common.EMPTY_STRING


fun String?.toCardIconPath(): String {
    return when (this) {
        CardTypeEnum.AC.name -> MASTERCARD_CREDIT
        CardTypeEnum.AM.name -> AMEX
        CardTypeEnum.BD.name,
        CardTypeEnum.AT.name -> BUSINESS_ACCOUNT
        CardTypeEnum.DI.name -> DINERS_CLUB
        CardTypeEnum.DL.name -> VISA_DEBIT
        CardTypeEnum.EL.name -> ELECTRON
        CardTypeEnum.MA.name -> MAESTRO
        CardTypeEnum.MD.name -> MASTERCARD_DEBIT
        CardTypeEnum.VI.name -> VISA_CREDIT
        else -> EMPTY_STRING
    }
}

fun String?.toCardName(): String {
    return when (this) {
        CardTypeEnum.AC.name -> CardTypeEnum.AC.cardTypeCode
        CardTypeEnum.AM.name -> CardTypeEnum.AM.cardTypeCode
        CardTypeEnum.AT.name -> CardTypeEnum.AT.cardTypeCode
        CardTypeEnum.BD.name -> CardTypeEnum.BD.cardTypeCode
        CardTypeEnum.DI.name -> CardTypeEnum.DI.cardTypeCode
        CardTypeEnum.DL.name -> CardTypeEnum.DL.cardTypeCode
        CardTypeEnum.EL.name -> CardTypeEnum.EL.cardTypeCode
        CardTypeEnum.MA.name -> CardTypeEnum.MA.cardTypeCode
        CardTypeEnum.MD.name -> CardTypeEnum.MD.cardTypeCode
        CardTypeEnum.VI.name -> CardTypeEnum.VI.cardTypeCode
        CardTypeEnumOpera.PI.name -> CardTypeEnumOpera.PI.cardTypeCode
        CardTypeEnumOpera.BD.name -> CardTypeEnumOpera.BD.cardTypeCode
        CardTypeEnumOpera.AX.name -> CardTypeEnumOpera.AX.cardTypeCode
        CardTypeEnumOpera.VS.name -> CardTypeEnumOpera.VS.cardTypeCode
        CardTypeEnumOpera.MA.name -> CardTypeEnumOpera.MA.cardTypeCode
        CardTypeEnumOpera.MC.name -> CardTypeEnumOpera.MC.cardTypeCode
        CardTypeEnumOpera.DN.name -> CardTypeEnumOpera.DN.cardTypeCode
        else -> EMPTY_STRING
    }
}

fun String?.toCardIconPathOpera(): String {
    return when (this) {
        CardTypeEnumOpera.PI.name -> BUSINESS_ACCOUNT
        CardTypeEnumOpera.BD.name -> BUSINESS_ACCOUNT_EURO
        CardTypeEnumOpera.PP.name -> PAYPAL
        CardTypeEnumOpera.GP.name -> GOOGLE_PAY
        else -> EMPTY_STRING
    }
}

private object Mapper {
    const val VISA_DEBIT = "Visa_Debit"
    const val VISA_CREDIT = "VC"
    const val MASTERCARD_CREDIT = "Mastercard"
    const val MASTERCARD_DEBIT = "MD"
    const val AMEX = "AX"
    const val BUSINESS_ACCOUNT = "Business_Account"
    const val BUSINESS_ACCOUNT_EURO = "Business_Account_Euro"
    const val DINERS_CLUB = "dinersclub"
    const val ELECTRON = "Electron_white_v"
    const val MAESTRO = "maestro"
    const val PAYPAL = "Paypal"
    const val GOOGLE_PAY = "GooglePay"
}

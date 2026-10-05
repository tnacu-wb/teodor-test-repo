package com.whitbread.premierinn.common.format;

import static com.whitbread.premierinn.domain.common.Constants.EUR;
import static com.whitbread.premierinn.domain.common.Constants.GBP;

import androidx.annotation.NonNull;

import com.whitbread.premierinn.data.common.Constants;
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider;
import com.whitbread.premierinn.domain.common.PriceDomain;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class PriceFormat {

    private static final int MAXIMUM_FRACTION_DIGITS = 2;

    public static String format(@NonNull PriceDomain price, DeviceLocaleProvider localeProvider) {
        return format(price.getAmount(), price.getCurrency(), MAXIMUM_FRACTION_DIGITS, localeProvider.getDeviceLocale());
    }

    public static String format(float amount, String currencyCode, DeviceLocaleProvider localeProvider) {
        return format(amount, currencyCode, MAXIMUM_FRACTION_DIGITS, localeProvider.getDeviceLocale());
    }

    public static String format(float amount, String currencyCode, int maximumFractionDigits, DeviceLocaleProvider deviceLocaleProvider) {
        return format(amount, currencyCode, maximumFractionDigits, deviceLocaleProvider.getDeviceLocale());
    }

    public static String format(float amount, String currencyCode, int maximumFractionDigits, Locale locale) {
        if (locale.getLanguage().equals(Constants.LANGUAGE_ENGLISH)) {
            return englishLanguagePriceFormat(amount, currencyCode, maximumFractionDigits);
        } else if (locale.getLanguage().equals(Constants.LANGUAGE_DEUTSCH)) {
            if (currencyCode.equals(GBP)) {
                return englishLanguagePriceFormat(amount, currencyCode, maximumFractionDigits);
            }

            if (currencyCode.equals(EUR)) {
                return germanLanguagePriceFormat(amount, maximumFractionDigits);
            }
        }

        return englishLanguagePriceFormat(amount, currencyCode, maximumFractionDigits);
    }

    private static String englishLanguagePriceFormat(float amount, String currencyCode, int maximumFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.UK);
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
        Currency currency = Currency.getInstance(currencyCode);
        numberFormat.setCurrency(currency);
        return numberFormat.format(amount);
    }

    private static String germanLanguagePriceFormat(float amount, int maximumFractionDigits) {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
        Currency currency = Currency.getInstance(EUR);
        numberFormat.setCurrency(currency);
        return numberFormat.format(amount);
    }

    public static String format(PriceDomain price, int maximumFractionDigits,
                                DeviceLocaleProvider deviceLocaleProvider) {
        return format(price.getAmount(), price.getCurrency(), maximumFractionDigits, deviceLocaleProvider);
    }
}
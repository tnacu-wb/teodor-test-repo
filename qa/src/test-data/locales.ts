import { Constants } from './constants';
import { Strings } from './strings';

/** Locale used for language/country-specific test expectations. */
export interface Locale {
  country: string;
  language: string;
  name: string;
}

/** The locales supported by the migrated Premier Inn tests. */
export class Locales {
  private constructor() {}

  static readonly GB_EN = Locales.createLocale('gb', 'en');
  static readonly DE_DE = Locales.createLocale('de', 'de');

  /** Localized route prefixes for Premier Inn and Inn Business flows. */
  static readonly GB_EN_URL = '/gb/en';
  static readonly DE_DE_URL = '/de/de';
  static readonly EN_GB_IB_URL = '/en-gb';
  static readonly DE_DE_IB_URL = '/de-de';

  /** Create a locale object from country and language segments. */
  private static createLocale(country: string, language: string): Locale {
    return { country, language, name: `${country}-${language}` };
  }

  /**
   * Get locale object based on the locale string.
   * @param localeString Locale in country-language format.
   * @returns Matching locale details.
   */
  static getLocaleByString(localeString: string): Locale {
    const tokens = localeString.split('-');
    const country = (tokens[0] ?? 'gb').toLowerCase();
    const language = (tokens[1] ?? 'en').toLowerCase();
    return Locales.createLocale(country, language);
  }

  /** Locale GB_EN is set. */
  static isEnglishWebsite(localeString = getCurrentLocale().name): boolean {
    return Locales.getLocaleByString(localeString).name === Locales.GB_EN.name;
  }

  /** Get currency sign based on country. */
  static async getCurrencyByCountry(): Promise<string> {
    const locale = getCurrentLocale();
    return locale.country === Locales.GB_EN.country ? Strings.POUND_CURRENCY_SIGN.name : Strings.EURO_CURRENCY_SIGN.name;
  }

  /** Get currency sign based on hotel country. */
  static async getCurrencyByHotelCountry(hotelCountry: string): Promise<string> {
    return hotelCountry === Constants.UK_COUNTRY_CODE ? Strings.POUND_CURRENCY_SIGN.name : Strings.EURO_CURRENCY_SIGN.name;
  }

  /** Format the price label with currency based on country and language. */
  static async formatCurrencyAmount({ amount, currency, isUkCompany = true }: { amount: string | number; currency?: string | null; isUkCompany?: boolean }): Promise<string> {
    let resolvedCurrency = await Locales.resolveCurrency(currency ?? undefined, isUkCompany);
    let newPriceLabel = Number.parseFloat(String(amount)).toFixed(2);

    if (getCurrentLocale().name === Locales.DE_DE.name) {
      newPriceLabel = Locales.withGermanDecimalAndGrouping(newPriceLabel, amount);
      return resolvedCurrency === await Strings.POUND_CURRENCY_SIGN.name ? `${resolvedCurrency}${newPriceLabel}` : `${newPriceLabel} ${resolvedCurrency}`;
    }

    if (resolvedCurrency === await Strings.EURO_CURRENCY_SIGN.name) {
      return `${Locales.withEnglishThousands(newPriceLabel, amount)} ${resolvedCurrency}`;
    }
    return `${resolvedCurrency}${Locales.withEnglishThousands(newPriceLabel, amount)}`;
  }

  /** Format the price label with currency based on country and language. */
  static async formatPriceCurrency(priceLabel: string | number, currency?: string | null): Promise<string> {
    const resolvedCurrency = await Locales.resolveCurrency(currency ?? undefined, true);
    if (getCurrentLocale().name === Locales.DE_DE.name && resolvedCurrency === await Strings.EURO_CURRENCY_SIGN.name) {
      return `${Number.parseFloat(String(priceLabel)).toFixed(2)}${resolvedCurrency}`.replace('.', ',');
    }
    return `${resolvedCurrency}${Number.parseFloat(String(priceLabel)).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`;
  }

  /** Retrieve the payment specific format of the price with currency based on country and language. */
  static async formatPriceCurrencyForPayment(priceLabel: string | number, currency?: string | null): Promise<string> {
    const resolvedCurrency = await Locales.resolveCurrency(currency ?? undefined, true);
    let newPriceLabel = Number.parseFloat(String(priceLabel)).toFixed(2);
    if (resolvedCurrency === await Strings.EURO_CURRENCY_SIGN.name && getCurrentLocale().name === Locales.DE_DE.name) {
      newPriceLabel = Locales.withGermanDecimalAndGrouping(newPriceLabel, priceLabel, false);
      return `${newPriceLabel} ${resolvedCurrency}`;
    }
    return `${resolvedCurrency}${Locales.withEnglishThousands(newPriceLabel, priceLabel, false)}`;
  }

  /** Retrieve the spend over time chart bar cost label specific format of spending amount with currency based on country and language. */
  static async formatCurrencyAmountForSpendOverTime({ amount, currency, scheme }: { amount: string | number; currency?: string | null; scheme?: string | null }): Promise<string> {
    let resolvedCurrency = currency;
    if ((resolvedCurrency === null || resolvedCurrency === undefined) && scheme === Constants.UK_SCHEME_CODE) {
      resolvedCurrency = Constants.UK_CURRENCY_CODE;
    } else if ((resolvedCurrency === null || resolvedCurrency === undefined) && scheme === Constants.DE_SCHEME_CODE) {
      resolvedCurrency = Constants.EURO_CURRENCY_CODE;
    }
    const symbol = await Locales.resolveCurrency(resolvedCurrency ?? undefined, true);
    let newPriceLabel = Number.parseFloat(String(amount)).toFixed(2);
    if (getCurrentLocale().name === Locales.DE_DE.name) {
      newPriceLabel = Locales.withGermanDecimalAndGrouping(newPriceLabel, amount);
      return symbol === await Strings.POUND_CURRENCY_SIGN.name ? `${symbol} ${newPriceLabel}` : `${newPriceLabel} ${symbol}`;
    }
    newPriceLabel = Locales.withEnglishThousands(newPriceLabel, amount);
    return symbol === await Strings.POUND_CURRENCY_SIGN.name ? `${symbol} ${newPriceLabel}` : `${newPriceLabel} ${symbol}`;
  }

  /** Format the price label with currency based on currency code and language. */
  static async formatPriceBasedOnCurrencyCode(price: string | number, currency: string, spaceAfterPrice = false): Promise<string> {
    let formattedPrice = Number.parseFloat(String(price)).toFixed(2);
    switch (currency) {
      case Constants.UK_CURRENCY_CODE:
        return `${await Strings.POUND_CURRENCY_SIGN.name}${formattedPrice}`;
      case Constants.EURO_CURRENCY_CODE:
        if (getCurrentLocale().language === Locales.DE_DE.language) {
          formattedPrice = formattedPrice.replace('.', ',');
          return spaceAfterPrice ? `${formattedPrice} ${await Strings.EURO_CURRENCY_SIGN.name}` : `${formattedPrice}${await Strings.EURO_CURRENCY_SIGN.name}`;
        }
        return `${await Strings.EURO_CURRENCY_SIGN.name}${formattedPrice}`;
      default:
        throw new Error(`Unknown ${currency} currency code`);
    }
  }

  /** Get default country label. */
  static async getDefaultCountryLabel(): Promise<string> {
    return getCurrentLocale().name === Locales.DE_DE.name ? Strings.GERMANY.name : Strings.UNITED_KINGDOM_THE.name;
  }

  /** Get default country from country drop-down in billing address section. */
  static async getDefaultCountryAddressByLocale(): Promise<string> {
    return getCurrentLocale().name === Locales.GB_EN.name ? Strings.UNITED_KINGDOM_THE.name : String(Strings.GERMANY.data.default);
  }

  /** Get url name. */
  static getUrlName(): string {
    return Locales.isEnglishWebsite() ? Locales.GB_EN_URL : Locales.DE_DE_URL;
  }

  /** Get IB url name. */
  static getIbUrlName(): string {
    return Locales.isEnglishWebsite() ? Locales.EN_GB_IB_URL : Locales.DE_DE_IB_URL;
  }

  /** Returns currency symbol next to amount given the currency code number and company origin. */
  static async getCurrencyAndAmountByCurrencyNumber(currencyCodeNumber: string, amount: string | number, isUkCompany: boolean): Promise<string> {
    let currencySymbol: string;
    if (currencyCodeNumber === Constants.UK_CURRENCY_CODE_NUMBER) {
      currencySymbol = await Strings.POUND_CURRENCY_SIGN.name;
    } else if (currencyCodeNumber === Constants.EURO_CURRENCY_CODE_NUMBER) {
      currencySymbol = await Strings.EURO_CURRENCY_SIGN.name;
    } else {
      throw new Error('Currency not recognized!');
    }

    const formattedAmount = Locales.isEnglishWebsite() ? Number.parseFloat(String(amount)).toFixed(2) : Number.parseFloat(String(amount)).toFixed(2).replace('.', ',');
    return isUkCompany ? `${currencySymbol}${formattedAmount}` : `${formattedAmount} ${currencySymbol}`;
  }

  /** Get localized date format. */
  static getLocalizedDateFormat(): string {
    const locale = getCurrentLocale();
    return `${locale.language}-${locale.country.toUpperCase()}`;
  }

  private static async resolveCurrency(currency: string | undefined, isUkCompany: boolean): Promise<string> {
    if (currency === Constants.UK_CURRENCY_CODE) {
      return Strings.POUND_CURRENCY_SIGN.name;
    }
    if (currency === Constants.EURO_CURRENCY_CODE) {
      return Strings.EURO_CURRENCY_SIGN.name;
    }
    return isUkCompany ? Strings.POUND_CURRENCY_SIGN.name : Strings.EURO_CURRENCY_SIGN.name;
  }

  private static withGermanDecimalAndGrouping(price: string, originalAmount: string | number, addThousandsForNegative = true): string {
    let formattedPrice = price.replace('.', ',');
    if (Number.parseFloat(String(originalAmount)) >= 1000 || (addThousandsForNegative && Number.parseFloat(String(originalAmount)) <= -1000)) {
      formattedPrice = `${formattedPrice.slice(0, -6)}.${formattedPrice.slice(-6)}`;
    }
    return formattedPrice;
  }

  private static withEnglishThousands(price: string, originalAmount: string | number, addThousandsForNegative = true): string {
    if (Number.parseFloat(String(originalAmount)) >= 1000 || (addThousandsForNegative && Number.parseFloat(String(originalAmount)) <= -1000)) {
      return `${price.slice(0, -6)},${price.slice(-6)}`;
    }
    return price;
  }
}

/**
 * Get the current locale from browser options.
 * @returns Locale object with country and language.
 */
export function getCurrentLocale(): Locale {
  const locale = global.browser?.options?.locale ?? 'gb-en';
  return Locales.getLocaleByString(locale);
}

/**
 * Apply locale defaults to an object, filling missing country/language fields.
 * @param params Object with optional country and language properties.
 * @returns Object with country and language fields guaranteed to be set.
 */
export function withLocaleDefaults<T extends { country?: string; language?: string }>(
  params: T,
): T & { country: string; language: string } {
  const locale = getCurrentLocale();
  return {
    ...params,
    country: params.country || locale.country,
    language: params.language || locale.language,
  };
}

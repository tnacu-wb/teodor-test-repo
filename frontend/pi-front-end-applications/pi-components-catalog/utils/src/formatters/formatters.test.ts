import {
  Area,
  LanguageEnum,
  Currency,
  BookingDataReservationDetailsProps,
  ShortCountry,
} from '@whitbread-eos/api';
import {
  getLocalStorageMock,
  bknReservationsMock,
  renderSanitizedHtml,
} from '@whitbread-eos/utils';
import parseHtml from 'html-react-parser';
import { parsePhoneNumberFromString } from 'libphonenumber-js';

import {
  formatCurrency,
  formatPrice,
  formatPriceWithDecimal,
  getPriceValueWithDecimal,
  formatDataTestId,
  formatAssetsUrl,
  formatInnerHTMLAssetUrls,
  formatDate,
  checkDateFormatter,
  formatNextLocaleLink,
  formatFindBookingToken,
  formatUrlTermsConditions,
  formatGuestTitleOptions,
  formatTextWithSpace,
  replaceWithEmptyString,
  uppercaseAndReplace,
  upperFirst,
  upperOnlyFirst,
  swapKeysAndValues,
  formatForMobile,
  removeHtmlTags,
  filterAncillaryCloseOutData,
  createReservationDetails,
  matchSilentSubstitutions,
  formatBillingAddress,
  formatGuests,
  getObjectFormatNumber,
  parsePhoneNumber,
  formatRequiredString,
  transformLabels,
} from './formatters';

type StaticContent = {
  labels?: {
    main?: string;
    piBookings?: string;
    booking?: string;
    piPreCheckIn?: string;
    piGroupBooking?: string;
    extras?: string;
    promotions?: string;
  };
};

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: 'https://secure2.premierinn.com',
  },
}));
jest.mock('libphonenumber-js', () => ({
  parsePhoneNumberFromString: jest.fn(),
}));

describe('formatters', () => {
  describe('getObjectFormatNumber Method', () => {
    it('should return UK object', function () {
      expect(getObjectFormatNumber('123', 'en')).toEqual({
        prefix: '+44',
        phoneNumber: '123',
        countryCode: ShortCountry.GB,
      });
    });

    it('should return DE object', function () {
      expect(getObjectFormatNumber('123', 'de')).toEqual({
        prefix: '+49',
        phoneNumber: '123',
        countryCode: ShortCountry.DE,
      });
    });
  });

  describe('parsePhoneNumber Method', () => {
    beforeEach(() => {
      jest.resetAllMocks();
    });

    it('should call parsePhoneNumber with wrong number', function () {
      expect(parsePhoneNumber('+44', '' as any)).toEqual(undefined);
    });

    it('should call parsePhoneNumber with correct params', function () {
      (parsePhoneNumberFromString as jest.Mock).mockReturnValue({
        countryCallingCode: '44',
        nationalNumber: '7654321',
        country: undefined,
      });
      expect(parsePhoneNumber('+447654321', 'en')).toEqual({
        prefix: '+44',
        phoneNumber: '7654321',
        countryCode: undefined,
      });
    });

    it('should call parsePhoneNumber without prefix and DE', function () {
      (parsePhoneNumberFromString as jest.Mock).mockReturnValue({
        countryCallingCode: '49',
        nationalNumber: '7654321',
        country: ShortCountry.DE,
      });
      expect(parsePhoneNumber('7654321', 'de')).toEqual({
        prefix: '+49',
        phoneNumber: '7654321',
        countryCode: ShortCountry.DE,
      });
    });
  });

  describe('formatCurrency Method', () => {
    it('should display the € symbol when EUR is sent', function () {
      const testCurrency = 'EUR';
      const expectedCurrency = Currency.EUR;
      expect(formatCurrency(testCurrency)).toEqual(expectedCurrency);
    });

    it('should display the £ symbol when GBP is sent', function () {
      const testCurrency = 'GBP';
      const expectedCurrency = Currency.GBP;
      expect(formatCurrency(testCurrency)).toEqual(expectedCurrency);
    });

    it('should display empty string when other currency codes are sent', function () {
      const testCurrency = 'USD';
      const expectedCurrency = '';
      expect(formatCurrency(testCurrency)).toEqual(expectedCurrency);
    });
  });

  describe('formatPrice Method', () => {
    it('should display the price before currency if language is german and currency is euro', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.EUR;
      const testPrice = 10;
      expect(formatPrice(testCurrency, testPrice, testLanguage)).toEqual('10€');
    });

    it('should display the price before currency if language is german and currency is euro and with comma', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.EUR;
      const testPrice = 10.53;
      expect(formatPrice(testCurrency, testPrice, testLanguage)).toEqual('10,53€');
    });

    it('should display the price after currency if language is german and currency is not euro and with comma', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.GBP;
      const testPrice = 10.01;
      expect(formatPrice(testCurrency, testPrice, testLanguage)).toEqual('£10.01');
    });

    it('should display the price after currency if language is not german and currency is not euro and not with comma', function () {
      const testLanguage = LanguageEnum.ENGLISH;
      const testCurrency = Currency.GBP;
      const testPrice = 20;
      expect(formatPrice(testCurrency, testPrice, testLanguage)).toEqual('£20');
    });

    it('should display the price after currency if language default and currency is not euro and not with comma', function () {
      const testCurrency = Currency.GBP;
      const testPrice = 20;
      expect(formatPrice(testCurrency, testPrice)).toEqual('£20');
    });
  });

  describe('formatPriceWithDecimal Method', () => {
    it('should display the price without decimals before currency if language is german or currency is euro', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.EUR;
      const testPrice = 10;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, false)).toEqual('10 €');
    });

    it('should display the price with decimals before currency if language is german or currency is euro', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.EUR;
      const testPrice = 10;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, true)).toEqual(
        '10,00 €'
      );
    });

    it('should display the price with decimals before currency if language is german and currency is euro and with comma and decimals props is false', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.EUR;
      const testPrice = 10.53;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, false)).toEqual(
        '10,53 €'
      );
    });

    it('should display the price with decimals before currency if language is german and currency is euro and with comma and decimals props is true', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.EUR;
      const testPrice = 10.53;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, true)).toEqual(
        '10,53 €'
      );
    });

    it('should display the price with decimals after currency if language is german and currency is not euro and with comma and decimals props is false', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.GBP;
      const testPrice = 10.01;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, false)).toEqual(
        '£10.01'
      );
    });

    it('should display the price with decimals after currency if language is german and currency is not euro and with comma and decimals props is true', function () {
      const testLanguage = LanguageEnum.GERMAN;
      const testCurrency = Currency.GBP;
      const testPrice = 10.01;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, true)).toEqual('£10.01');
    });

    it('should display the price without decimals after currency if language is not german and currency is not euro and not with comma', function () {
      const testLanguage = LanguageEnum.ENGLISH;
      const testCurrency = Currency.GBP;
      const testPrice = 20;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, false)).toEqual('£20');
    });

    it('should display the price with decimals after currency if language is not german and currency is not euro and not with comma and decimals props is true', function () {
      const testLanguage = LanguageEnum.ENGLISH;
      const testCurrency = Currency.GBP;
      const testPrice = 20;
      expect(formatPriceWithDecimal(testLanguage, testCurrency, testPrice, true)).toEqual('£20.00');
    });
  });

  describe('getPriceValueWithDecimal Method', () => {
    it('should return a decimal formatted price, from £15.6 -> 15.6', () => {
      const givenValue = '£15.6';
      const expectedValue = 15.6;
      expect(getPriceValueWithDecimal(givenValue)).toEqual(expectedValue);
    });

    it('should return a decimal formatted price for large numbers from £15,000 -> 15000', () => {
      const givenValue = '£15,000';
      const expectedValue = 15000;
      expect(getPriceValueWithDecimal(givenValue)).toEqual(expectedValue);
    });

    it('should return a decimal formatted price for Euro also, from 24€ -> 24', () => {
      const givenValue = '24€';
      const expectedValue = 24;
      expect(getPriceValueWithDecimal(givenValue)).toEqual(expectedValue);
    });

    it('should return 0.0 if no price is provided', () => {
      const givenValue = '';
      const expectedValue = 0;
      expect(getPriceValueWithDecimal(givenValue)).toEqual(expectedValue);
    });
  });

  describe('formatDataTestId Method', () => {
    it('should display empty string when both params are null', function () {
      const prefix = null;
      const dataTestId = null;
      const expectedOutput = '';
      expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
    });

    it('should display only the dataTestId when prefix is null', function () {
      const prefix = null;
      const dataTestId = 'DataTestId';
      const expectedOutput = 'DataTestId';
      expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
    });

    it('should display only the prefix when dataTestId is null', function () {
      const prefix = 'Prefix';
      const dataTestId = null;
      const expectedOutput = 'Prefix-';
      expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
    });

    it('should display both prefix and dataTestId when both are sent', function () {
      const prefix = 'Prefix';
      const dataTestId = 'DataTestId';
      const expectedOutput = 'Prefix-DataTestId';
      expect(formatDataTestId(prefix, dataTestId)).toEqual(expectedOutput);
    });

    it('should display empty string when both are null', function () {
      const expectedOutput = '';
      expect(formatDataTestId()).toEqual(expectedOutput);
    });
  });

  describe('formatAssetsUrl Method', () => {
    it('should add the path at the end of assets url correctly', function () {
      const testPath = '/example/image.png';
      expect(formatAssetsUrl(testPath)).toEqual('https://secure2.premierinn.com/example/image.png');
    });

    it('should remove the assets url duplicate part of the path when appending the path at the end of the assets url', function () {
      const testPath = 'https://secure2.premierinn.com/example/image.png';
      expect(formatAssetsUrl(testPath)).toEqual('https://secure2.premierinn.com/example/image.png');
    });

    it('should use the next public assets url if useBasicAuth flag is true', function () {
      const testPath = '/example/image.png';
      expect(formatAssetsUrl(testPath)).toEqual('https://secure2.premierinn.com/example/image.png');
    });
  });

  describe('formatInnerHTMLAssetUrls Method', () => {
    it('should format the href attribute of an <a> tag', function () {
      expect(
        formatInnerHTMLAssetUrls('Text <a href="/example/doc.pdf" target="_blank">Example</a>')
      ).toEqual(
        'Text <a href="https://secure2.premierinn.com/example/doc.pdf" target="_blank">Example</a>'
      );

      expect(
        formatInnerHTMLAssetUrls('<p>Text <a href="/example/doc.pdf">Example</a> text.</p>')
      ).toEqual(
        '<p>Text <a href="https://secure2.premierinn.com/example/doc.pdf">Example</a> text.</p>'
      );

      expect(
        formatInnerHTMLAssetUrls(
          '<div><p>Text <a href="/example/doc.pdf">Example</a> text.</p></div>'
        )
      ).toEqual(
        '<div><p>Text <a href="https://secure2.premierinn.com/example/doc.pdf">Example</a> text.</p></div>'
      );
    });

    it('should format the href attribute of multiple <a> tags', function () {
      expect(
        formatInnerHTMLAssetUrls(
          `Text
        <p>
          Text <a href="/example/doc.pdf">Example</a> 
          text 
          <a href="/example/doc.pdf">Example</a> 
          text 
          <a href="/example/doc.pdf">Example</a> 
          text.
        </p>`
        )
      ).toEqual(
        `Text
        <p>
          Text <a href="https://secure2.premierinn.com/example/doc.pdf">Example</a> 
          text 
          <a href="https://secure2.premierinn.com/example/doc.pdf">Example</a> 
          text 
          <a href="https://secure2.premierinn.com/example/doc.pdf">Example</a> 
          text.
        </p>`
      );
    });

    it('should return the same string when href url is not a relative path', function () {
      expect(
        formatInnerHTMLAssetUrls(
          'Text <p><a href="https://secure2.premierinn.com/example/doc.pdf">Example</a></p>'
        )
      ).toEqual('Text <p><a href="https://secure2.premierinn.com/example/doc.pdf">Example</a></p>');

      expect(formatInnerHTMLAssetUrls('Text <p><a href="doc.pdf">Example</a></p>')).toEqual(
        'Text <p><a href="doc.pdf">Example</a></p>'
      );
    });

    it('should not format URLs that do not have a dot character followed by multiple word characters (a file extension)', function () {
      expect(
        formatInnerHTMLAssetUrls(
          'Text <p><a href="/example/path">Example 1</a><a href="/example/doc.pdf">Example 2</a></p>'
        )
      ).toEqual(
        'Text <p><a href="/example/path">Example 1</a><a href="https://secure2.premierinn.com/example/doc.pdf">Example 2</a></p>'
      );
    });

    it('should not format .html files as they are site pages, not assets', function () {
      expect(
        formatInnerHTMLAssetUrls('Text <a href="/en-gb/info/terms/terms-of-use.html">Terms</a>')
      ).toEqual('Text <a href="/en-gb/info/terms/terms-of-use.html">Terms</a>');

      expect(
        formatInnerHTMLAssetUrls(
          '<p><a href="/en-gb/info/terms.html" target="_blank">Terms</a> and <a href="/example/doc.pdf">PDF</a></p>'
        )
      ).toEqual(
        '<p><a href="/en-gb/info/terms.html" target="_blank">Terms</a> and <a href="https://secure2.premierinn.com/example/doc.pdf">PDF</a></p>'
      );
    });

    it('should return the same string when no <a> tag is given', function () {
      expect(formatInnerHTMLAssetUrls('Text <p>Text.</p>')).toEqual('Text <p>Text.</p>');
    });

    it('should return an empty string if no innerHTML prop is provided', function () {
      expect(formatInnerHTMLAssetUrls('')).toEqual('');
    });
  });

  describe('formatDate Method', () => {
    it('should format the date with the given format and EN language', function () {
      const testDate = '2023-05-15';
      const testFormatDate = 'EEE do MMM';
      const testLanguage = LanguageEnum.ENGLISH;
      const expectedDate = 'Mon 15th May';
      expect(formatDate(testDate, testFormatDate, testLanguage)).toEqual(expectedDate);
    });

    it('should format the date with the given format and DE language', function () {
      const testDate = '2023-05-15';
      const testFormatDate = 'EEE do MMM';
      const testLanguage = LanguageEnum.GERMAN;
      const expectedDate = 'Mo. 15. Mai';
      expect(formatDate(testDate, testFormatDate, testLanguage)).toEqual(expectedDate);
    });

    it('should format the date with the given format and default EN language', function () {
      const testDate = '2023-05-15';
      const testFormatDate = 'EEE do MMM';
      const expectedDate = 'Mon 15th May';
      expect(formatDate(testDate, testFormatDate)).toEqual(expectedDate);
    });

    it('should format ISO date with the given format and default EN language', function () {
      const testDate = '2023-05-15T14:48:00.000Z';
      const testFormatDate = 'EEE do MMM';
      const expectedDate = 'Mon 15th May';
      expect(formatDate(testDate, testFormatDate)).toEqual(expectedDate);
    });
  });

  describe('checkDateFormatter Method', () => {
    it('should format the label correctly in english', function () {
      const testTime = '15:00';
      const testDate = '2022-11-28';
      const testLanguage = LanguageEnum.ENGLISH;
      expect(checkDateFormatter(testTime, testDate, testLanguage)).toEqual('3pm - Mon 28 Nov 2022');
    });

    it('should format the label correctly in german', function () {
      const testTime = '15:00';
      const testDate = '2022-11-28';
      const testLanguage = LanguageEnum.GERMAN;
      expect(checkDateFormatter(testTime, testDate, testLanguage)).toEqual(
        '15:00 - Mo. 28 Nov 2022'
      );
    });
  });

  describe('formatNextLocaleLink Method', () => {
    it('should replace /de to /gb/en/ when toggling to English', function () {
      const pathName = '/de/home.html';
      const nextLocale = LanguageEnum.ENGLISH;
      const currentLocale = LanguageEnum.GERMAN;
      const area = Area.PI;
      const expectedOutput = '/gb/en/home.html';
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should not place /home.html for non PI sites when toggling to English', function () {
      const pathName = '/en/';
      const nextLocale = LanguageEnum.ENGLISH;
      const currentLocale = LanguageEnum.ENGLISH;
      const area = Area.CCUI;
      const expectedOutput = '/gb/en/';
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should replace HDP page in English to German version and redirect to home', function () {
      const pathName =
        '/en/hotels/england/greater-manchester/manchester/manchester-old-trafford.html?ARRdd=15&ARRmm=11&ARRyyyy=2022&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BOOKINGCHANNEL=WEB&SORT=1&VIEW=2&BRAND=PI';
      const nextLocale = LanguageEnum.GERMAN;
      const currentLocale = LanguageEnum.ENGLISH;
      const area = Area.CCUI;
      const expectedOutput = '/de/de/';
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should redirect FAQ page in English to German Homepage', function () {
      const pathName = '/en/faq.html';
      const nextLocale = LanguageEnum.GERMAN;
      const currentLocale = LanguageEnum.ENGLISH;
      const expectedOutput = '/de/de/home.html';
      const area = Area.PI;

      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should redirect Contact page in German to English Homepage', function () {
      const pathName = '/de/kontakt.html';
      const nextLocale = LanguageEnum.ENGLISH;
      const currentLocale = LanguageEnum.GERMAN;
      const expectedOutput = '/gb/en/home.html';
      const area = Area.PI;
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should replace /en to /de/de/ when toggling to English', function () {
      const pathName = '/en/home.html';
      const nextLocale = LanguageEnum.GERMAN;
      const currentLocale = LanguageEnum.ENGLISH;
      const expectedOutput = '/de/de/home.html';
      const area = Area.CCUI;
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should keep the business-booker path /en to /de/de/ when toggling to English', function () {
      const pathName = '/en/business-booker/home.html';
      const nextLocale = LanguageEnum.GERMAN;
      const currentLocale = LanguageEnum.ENGLISH;
      const expectedOutput = '/de/de/business-booker/home.html';
      const area = Area.BB;
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should return homepage link if no pathName is provided', function () {
      const pathName = '';
      const nextLocale = LanguageEnum.ENGLISH;
      const currentLocale = LanguageEnum.ENGLISH;
      const expectedOutput = '/gb/en';
      const area = Area.PI;
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should return de home page if given pathName is in english, but locale is german', function () {
      const pathName =
        '/en/hotels/germany/bavaria/munich/munich-messe.html?ARRdd=16&ARRmm=05&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PID';
      const nextLocale = LanguageEnum.GERMAN;
      const currentLocale = LanguageEnum.ENGLISH;
      const expectedOutput = '/de/de/home.html';
      const area = Area.PI;
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });

    it('should return gb home page if given pathName is in german, but locale is english', function () {
      const pathName =
        '/de/hotels/deutschland/bavaria/munich/munich-messe.html?ARRdd=16&ARRmm=05&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB&BRAND=PID';
      const nextLocale = LanguageEnum.ENGLISH;
      const currentLocale = LanguageEnum.GERMAN;
      const expectedOutput = '/gb/en/home.html';
      const area = Area.PI;
      expect(formatNextLocaleLink(pathName, nextLocale, currentLocale, area)).toEqual(
        expectedOutput
      );
    });
  });

  describe('formatFindBookingToken Method', () => {
    it('should return encodedURI token if one is provided', function () {
      const testToken = 'test?#$@';
      const expectedOutput = 'test%3F%23%24%40';
      expect(formatFindBookingToken(testToken)).toEqual(expectedOutput);
    });

    it('should return empty string if no token is provided', function () {
      const testToken = '';
      const expectedOutput = '';
      expect(formatFindBookingToken(testToken)).toEqual(expectedOutput);
    });
  });

  describe('formatUrlTermsConditions Method', () => {
    it('should return empty string if no text is provided', function () {
      const testText = '';
      const expectedOutput = '';
      expect(formatUrlTermsConditions(testText)).toEqual(expectedOutput);
    });

    it('should format the terms and conditions url', function () {
      const testText = '/termsAndConditions.html';
      const expectedOutput = 'https://secure2.premierinn.com/termsAndConditions.html';
      expect(formatUrlTermsConditions(testText)).toEqual(expectedOutput);
    });

    it('should format a multi-segment terms and conditions url', function () {
      const testText =
        '/content/pi/websites/desktop/gb/en/unsecured/terms/booking-terms-and-conditions.html';
      const expectedOutput =
        'https://secure2.premierinn.com/content/pi/websites/desktop/gb/en/unsecured/terms/booking-terms-and-conditions.html';
      expect(formatUrlTermsConditions(testText)).toEqual(expectedOutput);
    });

    it('should return empty string if provided text does not contain .html ending', function () {
      const testText = '/termsAndConditions.pdf';
      const expectedOutput = '';
      expect(formatUrlTermsConditions(testText)).toEqual(expectedOutput);
    });
  });

  describe('formatGuestTitleOptions Method', () => {
    it('should create a list with the titles in english correctly', function () {
      const titles = "'Mr','Mrs','Ms'";
      expect(formatGuestTitleOptions(titles)).toEqual([
        { id: 'Mr', label: 'Mr' },
        { id: 'Mrs', label: 'Mrs' },
        { id: 'Ms', label: 'Ms' },
      ]);
    });

    it('should create a list with the titles in german correctly', function () {
      const titles = "Herr','Fräulein','Frau'";
      expect(formatGuestTitleOptions(titles)).toEqual([
        { id: 'Herr', label: 'Herr' },
        { id: 'Fräulein', label: 'Fräulein' },
        { id: 'Frau', label: 'Frau' },
      ]);
    });

    it('should return a correctly formatted object if given parameter is invalid', function () {
      expect(formatGuestTitleOptions('null')).toEqual([
        {
          id: 'null',
          label: 'null',
        },
      ]);
    });

    it('should return an object of empty strings if no title string is passed', function () {
      expect(formatGuestTitleOptions()).toEqual([
        {
          id: '',
          label: '',
        },
      ]);
    });
  });

  describe('formatTextWithSpace Method', () => {
    it('should replace all spaces with - for a given text', function () {
      const testText = 'This is a text to be formatted';
      const expectedOutput = 'This-is-a-text-to-be-formatted';
      expect(formatTextWithSpace(testText)).toEqual(expectedOutput);
    });

    it('should not format the text if an empty text string is passed', function () {
      const testText = '';
      const expectedOutput = '';
      expect(formatTextWithSpace(testText)).toEqual(expectedOutput);
    });
  });

  describe('replaceWithEmptyString Method', () => {
    it('should remove all whitespaces for a given text', function () {
      const testText = 'Manchester Old Trafford';
      const expectedOutput = 'ManchesterOldTrafford';
      expect(replaceWithEmptyString(testText)).toEqual(expectedOutput);
    });

    it('should return empty string if no text is provided', function () {
      const testText = '';
      const expectedOutput = '';
      expect(replaceWithEmptyString(testText)).toEqual(expectedOutput);
    });

    it('should return empty string if null is provided', function () {
      const testText = null;
      const expectedOutput = '';
      expect(replaceWithEmptyString(testText)).toEqual(expectedOutput);
    });
  });

  describe('uppercaseAndReplace Method', () => {
    it('should uppercase given text', function () {
      const testVal = 'e13 a91';
      const testRegex = /\s+/g;
      const testReplaceStr = '';
      const expectedOutcome = 'E13A91';
      expect(uppercaseAndReplace(testVal, testRegex, testReplaceStr)).toEqual(expectedOutcome);
    });

    it('should replace whitespaces based on provided regex with provided replacing string', function () {
      const testVal = 'This is a test';
      const testRegex = /\s+/g;
      const testReplaceStr = '$';
      const expectedOutcome = 'THIS$IS$A$TEST';
      expect(uppercaseAndReplace(testVal, testRegex, testReplaceStr)).toEqual(expectedOutcome);
    });
  });

  describe('upperFirst Method', () => {
    it('should uppercase first letter of given word', function () {
      const testValue = 'booking';
      const expectedValue = 'Booking';
      expect(upperFirst(testValue)).toEqual(expectedValue);
    });

    it('should uppercase first letter of given word containing uppercase letters', function () {
      const testValue = 'boOKinG';
      const expectedValue = 'BoOKinG';
      expect(upperFirst(testValue)).toEqual(expectedValue);

      const secondTestValue = 'BoOKinG';
      const secondExpectedValue = 'BoOKinG';
      expect(upperFirst(secondTestValue)).toEqual(secondExpectedValue);
    });

    it('should uppercase first letter even if provided word is all uppercased', function () {
      const testValue = 'BOOKING';
      const expectedValue = 'BOOKING';
      expect(upperFirst(testValue)).toEqual(expectedValue);
    });
  });

  describe('upperOnlyFirst Method', () => {
    it('should uppercase first letter of given word', function () {
      const testValue = 'booking';
      const expectedValue = 'Booking';
      expect(upperOnlyFirst(testValue)).toEqual(expectedValue);
    });

    it('should uppercase first letter and lowercase rest of letters for a given word containing uppercase letters', function () {
      const testValue = 'boOKinG';
      const expectedValue = 'Booking';
      expect(upperOnlyFirst(testValue)).toEqual(expectedValue);

      const secondTestValue = 'BoOKinG';
      const secondExpectedValue = 'Booking';
      expect(upperOnlyFirst(secondTestValue)).toEqual(secondExpectedValue);
    });

    it('should uppercase first letter and lowercase rest of letters for a given uppercased work', function () {
      const testValue = 'BOOKING';
      const expectedValue = 'Booking';
      expect(upperOnlyFirst(testValue)).toEqual(expectedValue);
    });
  });

  describe('swapKeysAndValues Method', () => {
    it('should return an object with swapped keys and values', function () {
      const object = {
        key: 'value',
      };
      expect(swapKeysAndValues(object)).toEqual({ value: 'key' });
    });
  });

  describe('formatForMobile Method', () => {
    it('should format a list with company ', () => {
      const input = [
        {
          id: 'COMPANY',
          navTitle: 'Company',
          subNav: [
            {
              title: 'Submenu 1',
              navOptions: [{ title: 'Link 1', url: 'Link 1' }],
            },
          ],
        },
        {
          id: 'PRODUCTS',
          navTitle: 'Products',
        },
      ];

      const expectedOutput = [
        {
          id: 'COMPANY',
          navTitle: 'Company',
          subNav: [
            {
              title: 'Submenu 1',
              navOptions: [{ title: 'Link 1', url: 'Link 1' }],
            },
          ],
        },
        {
          id: 'PRODUCTS',
          navTitle: 'Products',
        },
      ];

      expect(formatForMobile(input)).toEqual(expectedOutput);
    });
    it('should handle a list with no company', () => {
      const input = [
        {
          id: 'PRODUCTS',
          navTitle: 'Products',
        },
        {
          id: 'SERVICES',
          navTitle: 'Services',
        },
      ];

      const expectedOutput = [
        {
          id: 'PRODUCTS',
          navTitle: 'Products',
        },
        {
          id: 'SERVICES',
          navTitle: 'Services',
        },
      ];

      expect(formatForMobile(input)).toEqual(expectedOutput);
    });
  });
  describe('removeHtmlTags function', () => {
    it('removes HTML tags from input', () => {
      const inputString = '<p>Amend or cancel up to 1pm on arrival day</p>';
      const expectedOutput = 'Amend or cancel up to 1pm on arrival day';

      const result = removeHtmlTags(inputString);

      expect(result).toEqual(expectedOutput);
    });

    it('empty String', () => {
      const inputString = '';
      const expectedOutput = '';

      const result = removeHtmlTags(inputString);

      expect(result).toEqual(expectedOutput);
    });
  });

  describe('filterAncillaryCloseOutData Method', () => {
    it('should return object with valid endDate greater than startDate', () => {
      const input = {
        items: [
          {
            text: 'Kitchen close',
            startDate: '22/12/2023',
            endDate: '22/11/2023',
            serviceCode: 'MDP',
            upsellCodes: 'MDP',
          },
          {
            text: 'Restaurant Close',
            startDate: '01/11/2023',
            endDate: '30/11/2023',
            serviceCode: 'MDP,BFADBF,BBIB,BFADCT',
            upsellCodes: null,
          },
          {
            text: 'Restaurant Close',
            startDate: '01/12/2023',
            endDate: '05/12/2023',
            serviceCode: 'BFADCT',
            upsellCodes: 'BFADCT',
          },
        ],
      };

      const expectedOutput = [
        {
          endDate: '30/11/2023',
          serviceCode: 'MDP,BFADBF,BBIB,BFADCT',
          startDate: '01/11/2023',
          text: 'Restaurant Close',
          upsellCodes: null,
        },
        {
          endDate: '05/12/2023',
          serviceCode: 'BFADCT',
          startDate: '01/12/2023',
          text: 'Restaurant Close',
          upsellCodes: 'BFADCT',
        },
      ];
      const result = filterAncillaryCloseOutData(input);
      expect(result).toEqual(expectedOutput);
    });

    it('should NOT return object without a startDate or endDate value', () => {
      const input = {
        items: [
          {
            text: 'Kitchen close',
            startDate: '',
            endDate: '22/12/2023',
            serviceCode: 'MDP',
            upsellCodes: 'MDP',
          },
          {
            text: 'Restaurant Close',
            startDate: '01/11/2023',
            endDate: '',
            serviceCode: 'MDP,BFADBF,BBIB,BFADCT',
            upsellCodes: null,
          },
          {
            text: 'Restaurant Close',
            startDate: '01/12/2023',
            endDate: '05/12/2023',
            serviceCode: 'BFADCT',
            upsellCodes: 'BFADCT',
          },
        ],
      };

      const expectedOutput = [
        {
          endDate: '05/12/2023',
          serviceCode: 'BFADCT',
          startDate: '01/12/2023',
          text: 'Restaurant Close',
          upsellCodes: 'BFADCT',
        },
      ];

      const result = filterAncillaryCloseOutData(input);

      expect(result).toEqual(expectedOutput);
    });
  });
  describe('renderSanitizedHtml Method', () => {
    it('should sanitize and parse the sanitizedHtml to prevent XSS injection', () => {
      const testHtml = '<img src=x onerror=alert(1)//>';
      const expectedHtml = '<img src="x" />';
      expect(renderSanitizedHtml(testHtml)).toEqual(parseHtml(expectedHtml));
    });

    it('should sanitize and parse the sanitizedHtml a link with target and style attributes', () => {
      const testHtml = '<a href="x" style=y target="_blank" />';
      const expectedHtml = '<a href="x" style="y" target="_blank" />';
      expect(renderSanitizedHtml(testHtml)).toEqual(parseHtml(expectedHtml));
    });
  });
  describe('formatBillingAddress Method', () => {
    it('should return the right billingAddress by the provided cardToken', () => {
      const address = {
        line1: '120 Holborn',
        postCode: 'EC1N 2TD',
        countryCode: 'GB',
        addressType: 'BUSINESS',
      };
      const companyName = 'Whitbread';
      const formattedAddress = {
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: '',
        country: 'GB',
        postalCode: 'EC1N 2TD',
        companyName: 'Whitbread',
        addressType: 'BUSINESS',
      };
      expect(formatBillingAddress(address, companyName)).toEqual(formattedAddress);
    });
    it('should return the right billingAddress by the provided cardToken even if there are different fields specified', function () {
      const address = {
        addressLine1: '120 Holborn',
        postCode: 'EC1N 2TD',
        country: 'GB',
        addressType: 'BUSINESS',
      };
      const companyName = 'Whitbread';
      const formattedAddress = {
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        cityName: '',
        country: 'GB',
        postalCode: 'EC1N 2TD',
        companyName: 'Whitbread',
        addressType: 'BUSINESS',
      };
      expect(formatBillingAddress(address, companyName)).toEqual(formattedAddress);
    });
  });
  describe('formatGuests Method', () => {
    it('should return the guest formated when single guest, without accompanying guest', () => {
      const data = {
        leadGuest: [
          {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
          },
        ],
        title: 'Mr.',
        firstName: 'John',
        lastName: 'Doe',
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        countryCode: 'GB',
        postcodeAddress: 'EC1N 2TD',
      };
      const formattedGuest = {
        stayingGuestDetails: {
          title: 'Mr.',
          firstName: 'John',
          lastName: 'Doe',
          address: {
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            cityName: '',
            countryCode: 'GB',
            postalCode: 'EC1N 2TD',
          },
        },
        sameAsBooker: true,
      };
      expect(formatGuests(data)).toEqual([formattedGuest]);
    });

    it('should return the guest formated when single guest, with accompanying guest', () => {
      const data = {
        leadGuest: [
          {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Jane',
            accompanyinglastName: 'Doe',
          },
        ],
        title: 'Mr.',
        firstName: 'Bill',
        lastName: 'Doe',
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        countryCode: 'GB',
        postcodeAddress: 'EC1N 2TD',
      };
      const formattedGuest = {
        stayingGuestDetails: {
          title: 'Mr.',
          firstName: 'Bill',
          lastName: 'Doe',
          address: {
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            cityName: '',
            countryCode: 'GB',
            postalCode: 'EC1N 2TD',
          },
        },
        accompanyingGuestDetails: {
          title: 'Miss',
          firstName: 'Jane',
          lastName: 'Doe',
        },
        sameAsBooker: true,
      };
      expect(formatGuests(data, false, true)).toEqual([formattedGuest]);
    });

    it('should return the lead guest formated when single guest, with accompanying guest', () => {
      const data = {
        leadGuest: [
          {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Jane',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
        ],
        title: 'Mr.',
        firstName: 'Bill',
        lastName: 'Doe',
      };
      const formattedGuest = {
        stayingGuestDetails: {
          title: 'Mr.',
          firstName: 'John',
          lastName: 'Doe',
          address: {
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            cityName: '',
            countryCode: 'GB',
            postalCode: 'EC1N 2TD',
          },
        },
        accompanyingGuestDetails: {
          title: 'Miss',
          firstName: 'Jane',
          lastName: 'Doe',
        },
        sameAsBooker: false,
      };
      expect(formatGuests(data, true, true)).toEqual([formattedGuest]);
    });

    it('should return the guest formated when multi guests, with accompanying guest', () => {
      const data = {
        leadGuest: [
          {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Jane',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
          {
            title: 'Mr.',
            firstName: 'Mark',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Gabrielle',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
        ],
        title: 'Mr.',
        firstName: 'John',
        lastName: 'Doe',
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        countryCode: 'GB',
        postcodeAddress: 'EC1N 2TD',
      };
      const formattedGuests = [
        {
          stayingGuestDetails: {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            address: {
              addressLine1: '120 Holborn',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: '',
              countryCode: 'GB',
              postalCode: 'EC1N 2TD',
            },
          },
          accompanyingGuestDetails: {
            title: 'Miss',
            firstName: 'Jane',
            lastName: 'Doe',
          },
          sameAsBooker: false,
        },
        {
          stayingGuestDetails: {
            title: 'Mr.',
            firstName: 'Mark',
            lastName: 'Doe',
            address: {
              addressLine1: '120 Holborn',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: '',
              countryCode: 'GB',
              postalCode: 'EC1N 2TD',
            },
          },
          accompanyingGuestDetails: {
            title: 'Miss',
            firstName: 'Gabrielle',
            lastName: 'Doe',
          },
          sameAsBooker: false,
        },
      ];
      expect(formatGuests(data, false, true)).toEqual(formattedGuests);
    });

    it('should return the lead guest formated when multi guests, without accompanying guest', () => {
      const data = {
        leadGuest: [
          {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Jane',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
          {
            title: 'Mr.',
            firstName: 'Mark',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Gabrielle',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
        ],
        title: 'Mr.',
        firstName: 'John',
        lastName: 'Doe',
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        countryCode: 'GB',
        postcodeAddress: 'EC1N 2TD',
      };
      const formattedGuests = [
        {
          stayingGuestDetails: {
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            address: {
              addressLine1: '120 Holborn',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: '',
              countryCode: 'GB',
              postalCode: 'EC1N 2TD',
            },
          },
          sameAsBooker: false,
        },
        {
          stayingGuestDetails: {
            title: 'Mr.',
            firstName: 'Mark',
            lastName: 'Doe',
            address: {
              addressLine1: '120 Holborn',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: '',
              countryCode: 'GB',
              postalCode: 'EC1N 2TD',
            },
          },
          sameAsBooker: false,
        },
      ];
      expect(formatGuests(data, false, false)).toEqual(formattedGuests);
    });

    it('should return the guest formated when multi guests, without accompanying guest', () => {
      const data = {
        leadGuest: [
          {
            stayInThisRoom: true,
            title: 'Mr.',
            firstName: 'John',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Jane',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
          {
            title: 'Mr.',
            firstName: 'Mark',
            lastName: 'Doe',
            accompanyingtitle: 'Miss',
            accompanyingfirstName: 'Gabrielle',
            accompanyinglastName: 'Doe',
            addressLine1: '120 Holborn',
            addressLine2: '',
            addressLine3: '',
            addressLine4: '',
            countryCode: 'GB',
            postcodeAddress: 'EC1N 2TD',
          },
        ],
        title: 'Mr.',
        firstName: 'Bill',
        lastName: 'Doe',
        addressLine1: '120 Holborn',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        countryCode: 'GB',
        postcodeAddress: 'EC1N 2TD',
      };
      const formattedGuests = [
        {
          stayingGuestDetails: {
            title: 'Mr.',
            firstName: 'Bill',
            lastName: 'Doe',
            address: {
              addressLine1: '120 Holborn',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: '',
              countryCode: 'GB',
              postalCode: 'EC1N 2TD',
            },
          },
          sameAsBooker: true,
        },
        {
          stayingGuestDetails: {
            title: 'Mr.',
            firstName: 'Mark',
            lastName: 'Doe',
            address: {
              addressLine1: '120 Holborn',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              cityName: '',
              countryCode: 'GB',
              postalCode: 'EC1N 2TD',
            },
          },
          sameAsBooker: false,
        },
      ];
      expect(formatGuests(data, true, false)).toEqual(formattedGuests);
    });
  });
});

describe('createReservationDetails function', () => {
  const mockCreateReservationDetails = {
    arrivalDate: '2023-01-01',
    departureDate: '2023-01-05',
    currency: 'EUR',
    noNights: 4,
    noRooms: 1,
  };

  it('should create reservation details with valid input', () => {
    const result: BookingDataReservationDetailsProps = createReservationDetails(
      '2023-01-01',
      '2023-01-05',
      'EUR',
      [
        {
          roomStay: {
            arrivalDate: '2023-01-01',
            departureDate: '2023-01-05',
            adultsNumber: 1,
            childrenNumber: 0,
            ratePlanCode: '',
            rateName: '',
            roomType: '',
            infoMessages: [],
            rateExtraInfo: {
              rateName: '',
              rateClassification: '',
              rateOrder: '',
              rateDescription: '',
              rateLongDescription: '',
              rateNotes: '',
            },
            roomExtraInfo: { roomType: '', roomName: '', roomDescription: '' },
            accessibleRoom: { isAccessible: false, phoneNumber: '' },
          },
          reservationGuestList: [{ givenName: '', surName: '', nameTitle: '' }],
          depositPolicies: [
            {
              policyCode: '',
              amountPaid: { amount: 0, currencyCode: '' },
              amountDue: { amount: 0, currencyCode: '' },
            },
          ],
          billing: {
            address: {
              addressLine1: '',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              country: '',
              postalCode: '',
              companyName: '',
            },
            email: '',
            firstName: '',
            lastName: '',
            telephone: '',
            title: '',
            landline: '',
          },
          reservationId: '',
        },
      ],
      4
    );

    expect(result).toEqual(mockCreateReservationDetails);
  });

  it('should handle null values for arrival and departure dates', () => {
    const result: BookingDataReservationDetailsProps = createReservationDetails(
      null,
      null,
      'EUR',
      [
        {
          roomStay: {
            arrivalDate: '2023-01-01',
            departureDate: '2023-01-05',
            adultsNumber: 1,
            childrenNumber: 0,
            ratePlanCode: '',
            rateName: '',
            roomType: '',
            infoMessages: [],
            rateExtraInfo: {
              rateName: '',
              rateClassification: '',
              rateOrder: '',
              rateDescription: '',
              rateLongDescription: '',
              rateNotes: '',
            },
            roomExtraInfo: { roomType: '', roomName: '', roomDescription: '' },
            accessibleRoom: { isAccessible: false, phoneNumber: '' },
          },
          reservationGuestList: [{ givenName: '', surName: '', nameTitle: '' }],
          depositPolicies: [
            {
              policyCode: '',
              amountPaid: { amount: 0, currencyCode: '' },
              amountDue: { amount: 0, currencyCode: '' },
            },
          ],
          billing: {
            address: {
              addressLine1: '',
              addressLine2: '',
              addressLine3: '',
              addressLine4: '',
              country: '',
              postalCode: '',
              companyName: '',
            },
            email: '',
            firstName: '',
            lastName: '',
            telephone: '',
            title: '',
            landline: '',
          },
          reservationId: '',
        },
      ],
      1
    );

    expect(result).toEqual({
      arrivalDate: null,
      departureDate: null,
      currency: 'EUR',
      noRooms: 1,
      noNights: 1,
    });
  });
});

describe('matchSilentSubstitutions method', () => {
  const localStorageMock = getLocalStorageMock();
  Object.defineProperty(window, 'localStorage', {
    value: localStorageMock,
  });

  const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';

  it('should return the correct value from matchedSubstitutions method', () => {
    localStorageMock.clear();
    const initialLocalStorageMock = [
      {
        roomLabelCode: 'Double room',
        silentSubstitution: true,
        adults: 1,
        children: 0,
        pmsRoomType: 'DOUBLE',
      },
      {
        roomLabelCode: 'Family room',
        silentSubstitution: true,
        adults: 2,
        children: 1,
        pmsRoomType: 'FMTRPL',
      },
      {
        roomLabelCode: 'Twin room',
        silentSubstitution: true,
        adults: 2,
        children: 0,
        pmsRoomType: 'FMTRPL',
      },
      {
        roomLabelCode: 'Double room',
        silentSubstitution: true,
        adults: 2,
        children: 0,
        pmsRoomType: 'DOUBLE',
      },
    ];

    const matchedSubstitutions = [
      {
        roomLabelCode: 'Double room',
        silentSubstitution: true,
        adults: 2,
        children: 0,
        pmsRoomType: 'DOUBLE',
        filtered: true,
      },
      {
        roomLabelCode: 'Twin room',
        silentSubstitution: true,
        adults: 2,
        children: 0,
        pmsRoomType: 'FMTRPL',
        filtered: true,
      },
      {
        roomLabelCode: 'Family room',
        silentSubstitution: true,
        adults: 2,
        children: 1,
        pmsRoomType: 'FMTRPL',
        filtered: true,
      },
      {
        roomLabelCode: 'Double room',
        silentSubstitution: true,
        adults: 1,
        children: 0,
        pmsRoomType: 'DOUBLE',
        filtered: true,
      },
    ];

    localStorageMock.setItem(
      SILENT_SUBSTITUTION_STORAGE_KEY,
      JSON.stringify(initialLocalStorageMock)
    );

    const result = matchSilentSubstitutions(bknReservationsMock, initialLocalStorageMock);

    expect(result).toEqual(matchedSubstitutions);
  });
});

describe('formatRequiredString', () => {
  it('should remove asterisk characters from the string', () => {
    const input = 'Name*';
    const expected = 'Name';
    expect(formatRequiredString(input)).toEqual(expected);
  });

  it('should remove multiple asterisks from the string', () => {
    const input = '*Required*Field*';
    const expected = 'RequiredField';
    expect(formatRequiredString(input)).toEqual(expected);
  });

  it('should return the same string if there are no asterisks', () => {
    const input = 'NoAsterisk';
    const expected = 'NoAsterisk';
    expect(formatRequiredString(input)).toEqual(expected);
  });

  it('should return empty string if input is undefined', () => {
    expect(formatRequiredString(undefined as any)).toEqual('');
  });

  it('should return empty string if input is null', () => {
    expect(formatRequiredString(null as any)).toEqual('');
  });
});

describe('transformLabels', () => {
  it('merges all label JSON strings into a single object', () => {
    const staticData: StaticContent = {
      labels: {
        main: JSON.stringify({ title: 'Main Title' }),
        piBookings: JSON.stringify({ bookingLabel: 'PI Booking' }),
        booking: JSON.stringify({ bookingTitle: 'Booking' }),
        piPreCheckIn: JSON.stringify({ checkIn: 'Pre Check In' }),
        piGroupBooking: JSON.stringify({ group: 'Group Booking' }),
        extras: JSON.stringify({ extra: 'Extras' }),
        promotions: JSON.stringify({ promo: 'Promotion' }),
      },
    };

    const result = transformLabels(staticData as any);

    expect(result).toEqual({
      title: 'Main Title',
      bookingLabel: 'PI Booking',
      bookingTitle: 'Booking',
      checkIn: 'Pre Check In',
      group: 'Group Booking',
      extra: 'Extras',
      promo: 'Promotion',
    });
  });

  it('returns an empty object when labels are missing', () => {
    const staticData: StaticContent = {};

    const result = transformLabels(staticData as any);

    expect(result).toEqual({});
  });

  it('merges only available label sections', () => {
    const staticData: StaticContent = {
      labels: {
        main: JSON.stringify({ title: 'Main' }),
        extras: JSON.stringify({ extra: 'Extras' }),
      },
    };

    const result = transformLabels(staticData as any);

    expect(result).toEqual({
      title: 'Main',
      extra: 'Extras',
    });
  });

  it('overrides values when duplicate keys exist (last one wins)', () => {
    const staticData: StaticContent = {
      labels: {
        main: JSON.stringify({ label: 'Main Label' }),
        booking: JSON.stringify({ label: 'Booking Label' }),
      },
    };

    const result = transformLabels(staticData as any);

    expect(result).toEqual({
      label: 'Booking Label',
    });
  });

  it('does not throw when label values are undefined', () => {
    const staticData: StaticContent = {
      labels: {
        main: undefined,
        booking: undefined,
      },
    };

    expect(() => transformLabels(staticData as any)).not.toThrow();
    expect(transformLabels(staticData as any)).toEqual({});
  });
});

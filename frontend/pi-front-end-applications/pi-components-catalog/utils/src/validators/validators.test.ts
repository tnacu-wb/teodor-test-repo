import { HotelBrand, UserRoles, CCUI_ROLES } from '@whitbread-eos/api';

import {
  isStringValid,
  isDateValid,
  isSameDate,
  isNumber,
  isAlphabetic,
  hasValidCharacters,
  isJsonValid,
  ternaryCondition,
  logicalAndOperator,
  logicalOrOperator,
  isEmailValid,
  isUrl,
  isNonEmptyString,
  validateArrivalDate,
  isPromoAdmin,
} from './validators';

describe('validators', () => {
  describe('isStringValid Method', () => {
    it('should return false if the given string is empty', function () {
      const testString = '';
      expect(isStringValid(testString)).toEqual(false);
    });

    it('should return false if the given string is null', function () {
      const testString = null;
      expect(isStringValid(testString)).toEqual(false);
    });

    it('should return false if the given string is undefined', function () {
      const testString = undefined;
      expect(isStringValid(testString)).toEqual(false);
    });

    it('should return true if the given string is not null, undefined or empty', function () {
      const testString = 'abc';
      expect(isStringValid(testString)).toEqual(true);
    });
  });

  describe('isDateValid Method', () => {
    it('should return true if given date is valid', function () {
      const day = 1;
      const month = 10;
      const year = 2023;
      expect(isDateValid(day, month, year)).toEqual(true);
    });

    it('should return false if given date is invalid', function () {
      const day = 0;
      const month = 13;
      const year = 2023;
      expect(isDateValid(day, month, year)).toEqual(false);
    });
  });

  describe('isSameDate Method', () => {
    it('should return true if both given dates are the same', function () {
      const testDateOne = new Date('2023-05-15');
      const testDateTwo = new Date('2023-05-15');
      expect(isSameDate(testDateOne, testDateTwo)).toEqual(true);
    });

    it('should return false if given dates are different', function () {
      const testDateOne = new Date('2023-05-15');
      const testDateTwo = new Date('2023-05-17');
      expect(isSameDate(testDateOne, testDateTwo)).toEqual(false);

      const testDateThree = new Date('2023-03-15');
      const testDateFour = new Date('2023-05-15');
      expect(isSameDate(testDateThree, testDateFour)).toEqual(false);

      const testDateFive = new Date('2022-05-15');
      const testDateSix = new Date('2023-05-15');
      expect(isSameDate(testDateFive, testDateSix)).toEqual(false);
    });

    it('should return false if first given date is null', function () {
      const testDateOne = null;
      const testDateTwo = new Date('2023-05-15');
      expect(isSameDate(testDateOne, testDateTwo)).toEqual(false);
    });

    it('should return false if second given date is null', function () {
      const testDateOne = new Date('2023-05-15');
      const testDateTwo = null;
      expect(isSameDate(testDateOne, testDateTwo)).toEqual(false);
    });
  });

  describe('isNumber Method', () => {
    it('should return true if the given value is a string containing only numbers', () => {
      expect(isNumber('1234')).toEqual(true);
    });

    it('should return false if the given value is a string containing non-numeric characters', () => {
      expect(isNumber('12B34A')).toEqual(false);
    });
  });

  describe('isAlphabetic Method', () => {
    it('should return true if the given string contains only letters', () => {
      const testString = 'Jackson';
      expect(isAlphabetic(testString)).toEqual(true);
    });

    it('should return true if the given string contains only letters with localize special characters as ü, hyphen or space', () => {
      const testStringWithü = 'Vlüdislove';
      const testStringWithHyphen = 'Harper-Jackson';
      const testStringWithSpace = 'Jackson Harper';
      expect(isAlphabetic(testStringWithü)).toEqual(true);
      expect(isAlphabetic(testStringWithHyphen)).toEqual(true);
      expect(isAlphabetic(testStringWithSpace)).toEqual(true);
    });

    it('should return false if the given string contains letters and digits', () => {
      const testString = 'ABX43';
      expect(isAlphabetic(testString)).toEqual(false);
    });

    it('should return false if the given string contains only digits', () => {
      const testString = '21337';
      expect(isAlphabetic(testString)).toEqual(false);
    });

    it('should return false if the given string contains contains both letters and digits and special characters such as @', () => {
      const testString = 'ABX21@';
      expect(isAlphabetic(testString)).toEqual(false);
    });
  });

  describe('hasValidCharacters Method', () => {
    it('should return true if the given string contains only letters', () => {
      const testString = 'Jackson';
      expect(hasValidCharacters(testString)).toEqual(true);
    });

    it('should return true if the given string contains only letters with localize special characters as ü, hyphen or space', () => {
      const testStringWithü = 'Vlüdislove';
      const testStringWithHyphen = 'Harper-Jackson';
      const testStringWithSpace = 'Jackson Harper';
      expect(hasValidCharacters(testStringWithü)).toEqual(true);
      expect(hasValidCharacters(testStringWithHyphen)).toEqual(true);
      expect(hasValidCharacters(testStringWithSpace)).toEqual(true);
    });

    it('should return true if the given string contains letters and digits', () => {
      const testString = 'ABX43';
      expect(hasValidCharacters(testString)).toEqual(true);
    });

    it('should return false if the given string contains contains letters and special characters such as @ or *', () => {
      const testString1 = 'ABX21@';
      const testString2 = 'ABX*';
      expect(hasValidCharacters(testString1)).toEqual(false);
      expect(hasValidCharacters(testString2)).toEqual(false);
    });
  });

  describe('isJsonValid Method', () => {
    it('should return true if the given string is a valid stringified JSON', () => {
      const testObj = { hello: 'world' };
      expect(isJsonValid(JSON.stringify(testObj))).toEqual(true);
      expect(isJsonValid('{"key":"value"}')).toEqual(true);
    });

    it('should return false if the given string is not a valid JSON string', () => {
      expect(isJsonValid('')).toEqual(false);
      expect(isJsonValid('test')).toEqual(false);
      expect(isJsonValid('AA12937&@#$!')).toEqual(false);
    });

    it('should return false if an error is thrown', () => {
      jest.spyOn(JSON, 'parse').mockImplementation(() => {
        throw new Error('error');
      });
      expect(isJsonValid('{"key":"value"}')).toEqual(false);
    });
  });

  describe('ternaryCondition Method', () => {
    it('should return first expression (true) if given condition is true', () => {
      const testTrueExpression = 'red';
      const testFalseExpression = 'blue';
      const testCondition = true;
      expect(ternaryCondition(testCondition, testTrueExpression, testFalseExpression)).toEqual(
        testTrueExpression
      );
    });

    it('should return second expression (false) if given condition is false', () => {
      const testTrueExpression = 'red';
      const testFalseExpression = 'blue';
      const testCondition = false;
      expect(ternaryCondition(testCondition, testTrueExpression, testFalseExpression)).toEqual(
        testFalseExpression
      );
    });

    it('should return second expression (false) if given condition is undefined', () => {
      const testTrueExpression = 'red';
      const testFalseExpression = 'blue';
      const testCondition = undefined;
      expect(ternaryCondition(testCondition, testTrueExpression, testFalseExpression)).toEqual(
        testFalseExpression
      );
    });
  });

  describe('logicalAndOperator Method', () => {
    it('should return true if all passed arguments are true', () => {
      const testValue = 'abc' as string;
      expect(
        logicalAndOperator(
          testValue !== '',
          testValue !== undefined,
          testValue !== null,
          testValue !== '6'
        )
      ).toEqual(true);
    });

    it('should return false if at least one argument is false', () => {
      const testValue = '6' as string;
      expect(
        logicalAndOperator(
          testValue !== '',
          testValue !== undefined,
          testValue !== null,
          testValue !== '6'
        )
      ).toEqual(false);
    });
  });

  describe('logicalOrOperator Method', () => {
    it('should return true if any of the passed arguments is true', () => {
      const testValue = 'abc' as string;
      expect(
        logicalOrOperator(
          testValue === '',
          testValue === undefined,
          testValue === null,
          testValue === 'abc'
        )
      ).toEqual(true);
    });

    it('should return false if no passed argument is true', () => {
      const testValue = '6' as string;
      expect(
        logicalOrOperator(
          testValue === '',
          testValue === undefined,
          testValue === null,
          testValue !== '6'
        )
      ).toEqual(false);
    });
  });
});

describe('isEmailValid Method', () => {
  it('should return true if the given string is a valid email Address', () => {
    const testEmail = 'test@test.com';
    expect(isEmailValid(testEmail)).toEqual(true);
  });
  it('should return false if the given string is not a valid email Address', () => {
    const testEmail = 'test.test.com';
    expect(isEmailValid(testEmail)).toEqual(false);
  });
  it('should return false if the given string is empty', () => {
    expect(isEmailValid('')).toEqual(false);
  });
});

describe('isUrl Method', () => {
  it('should return true if the given string is a valid url', () => {
    const testURL = 'http://test.com';
    expect(isUrl(testURL)).toEqual(true);
  });
  it('should return false if the given string is not a valid url', () => {
    const testURL = 'test';
    expect(isUrl(testURL)).toEqual(false);
  });
  it('should return false if the given string is empty', () => {
    expect(isUrl('')).toEqual(false);
  });
});

describe('isNonEmptyString', () => {
  test('returns true for a non-empty string', () => {
    expect(isNonEmptyString('hello')).toBe(true);
  });

  test('returns true for a non-empty string with white spaces', () => {
    expect(isNonEmptyString('hello  ')).toBe(true);
  });

  test('returns false for an empty string', () => {
    expect(isNonEmptyString('')).toBe(false);
  });

  test('returns false for null', () => {
    expect(isNonEmptyString(null)).toBe(false);
  });

  test('returns false for undefined', () => {
    expect(isNonEmptyString(undefined)).toBe(false);
  });

  test('returns false for a string with only whitespace', () => {
    expect(isNonEmptyString('   ')).toBe(false);
  });
});

describe('validateArrivalDate', () => {
  it('returns true if current date is before arrival date', () => {
    expect(
      validateArrivalDate('2025-06-02', HotelBrand.PID, new Date('2025-06-01T12:00:00Z'))
    ).toBe(true);
  });

  it('returns false if current date is after arrival date', () => {
    expect(
      validateArrivalDate('2025-06-02', HotelBrand.PID, new Date('2025-06-03T00:00:00Z'))
    ).toBe(false);
  });

  describe('on arrival date', () => {
    it('returns true before cutoff for German hotel (PID)', () => {
      // Berlin cutoff = 23:59 Berlin = 21:59 UTC (summer time)
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PID, new Date('2025-06-02T21:59:59Z'))
      ).toBe(true);
    });

    it('returns false exactly at cutoff for German hotel (PID)', () => {
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PID, new Date('2025-06-02T22:00:00Z'))
      ).toBe(false);
    });

    it('returns false after cutoff for German hotel (PID)', () => {
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PID, new Date('2025-06-02T22:30:00Z'))
      ).toBe(false);
    });

    it('returns true before cutoff for non-German hotel (PI)', () => {
      // London cutoff = 23:59 London = 22:59 UTC (summer time)
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PI, new Date('2025-06-02T22:59:59Z'))
      ).toBe(true);
    });

    it('returns false exactly at cutoff for non-German hotel (PI)', () => {
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PI, new Date('2025-06-02T23:00:00Z'))
      ).toBe(false);
    });

    it('returns false after cutoff for non-German hotel (PI)', () => {
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PI, new Date('2025-06-02T23:30:00Z'))
      ).toBe(false);
    });
  });

  describe('time zone translation checks', () => {
    it('German cutoff (23:59 Berlin summer) corresponds to 03:29 IST next day', () => {
      // 2025-08-29 21:59 UTC = 2025-08-30 03:29 IST
      expect(
        validateArrivalDate('2025-08-29', HotelBrand.PID, new Date('2025-08-29T21:59:59Z'))
      ).toBe(true);

      // At cutoff (22:00 UTC = 03:30 IST)
      expect(
        validateArrivalDate('2025-08-29', HotelBrand.PID, new Date('2025-08-29T22:00:00Z'))
      ).toBe(false);
    });

    it('German cutoff (23:59 Berlin winter) corresponds to 04:29 IST next day', () => {
      // 2025-12-15 Berlin = CET (UTC+1), cutoff 22:59 UTC = 04:29 IST
      expect(
        validateArrivalDate('2025-12-15', HotelBrand.PID, new Date('2025-12-15T22:59:59Z'))
      ).toBe(true);

      // Exactly at cutoff
      expect(
        validateArrivalDate('2025-12-15', HotelBrand.PID, new Date('2025-12-15T23:00:00Z'))
      ).toBe(false);
    });
  });

  describe('edge cases', () => {
    it('returns true if arrival date is same as current date but before any cutoff', () => {
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PID, new Date('2025-06-02T00:00:00Z'))
      ).toBe(true);
    });

    it('returns false if arrival date is today but current time is the next day midnight', () => {
      expect(
        validateArrivalDate('2025-06-02', HotelBrand.PI, new Date('2025-06-03T00:00:00Z'))
      ).toBe(false);
    });

    it('returns false if arrival date is invalid string', () => {
      expect(validateArrivalDate('invalid-date', HotelBrand.PID, new Date())).toBe(false);
    });

    it('handles leap year arrival date correctly', () => {
      expect(
        validateArrivalDate('2028-02-29', HotelBrand.PID, new Date('2028-02-28T23:59:59Z'))
      ).toBe(true);
    });
  });
});

describe('isPromoAdmin', () => {
  it('returns true when user has PROMO_ADMIN (UAT) role', () => {
    const user = {
      [CCUI_ROLES]: [UserRoles.MANAGER, UserRoles.PROMO_ADMIN],
    };
    expect(isPromoAdmin(user)).toBe(true);
  });

  it('returns true when user has PROMO_ADMIN_PROD role', () => {
    const user = {
      [CCUI_ROLES]: [UserRoles.AGENT, UserRoles.PROMO_ADMIN_PROD],
    };
    expect(isPromoAdmin(user)).toBe(true);
  });

  it('returns false when user does not have any promo admin roles', () => {
    const user = {
      [CCUI_ROLES]: [UserRoles.MANAGER, UserRoles.AGENT],
    };
    expect(isPromoAdmin(user)).toBe(false);
  });

  it('returns false when roles array is empty', () => {
    const user = {
      [CCUI_ROLES]: [],
    };
    expect(isPromoAdmin(user)).toBe(false);
  });

  it('returns false when user object is undefined', () => {
    expect(isPromoAdmin(undefined)).toBe(false);
  });

  it('returns false when user object is null', () => {
    expect(isPromoAdmin(null)).toBe(false);
  });

  it('returns false when CCUI_ROLES key is missing', () => {
    const user = {};
    expect(isPromoAdmin(user)).toBe(false);
  });

  it('returns false when roles is not an array', () => {
    const user = {
      [CCUI_ROLES]: UserRoles.PROMO_ADMIN,
    };
    expect(isPromoAdmin(user)).toBe(false);
  });
});

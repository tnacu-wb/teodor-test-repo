import { deleteCookie, getCookie, setCookie, deleteCookieWithDefaultDomain } from './cookies';

describe('cookies methods', () => {
  describe('getCookie Method', () => {
    beforeEach(() => {
      Object.defineProperty(document, 'cookie', {
        writable: true,
        value: 'a=1; b=2; c=3',
      });
    });

    it('should retrieve the cookie value based on a given cookie name', () => {
      const cookieValue = getCookie('b');
      const expectedOutput = '2';
      expect(cookieValue).toEqual(expectedOutput);
    });

    it('should return undefined if cookie value is not found for the given cookie name', () => {
      const cookieValue = getCookie('d');
      expect(cookieValue).toEqual(undefined);
    });

    it('should return undefined if there are no cookies', () => {
      Object.defineProperty(document, 'cookie', {
        writable: true,
        value: undefined,
      });
      const cookieValue = getCookie('a');
      expect(cookieValue).toEqual(undefined);
    });
  });

  describe('setCookie Method', () => {
    beforeEach(() => {
      Object.defineProperty(document, 'cookie', {
        writable: true,
      });
    });

    it('should set a new cookie with provided parameters, that does not expire', () => {
      const testName = 'booking_cookie';
      const testValue = 'AWU123123123';
      const testTime = 0;
      setCookie(testName, testValue, testTime);
      const expectedOutput = 'booking_cookie=AWU123123123;;path=/;domain=localhost';
      expect(document.cookie).toEqual(expectedOutput);
    });

    it('should set a new cookie with provided parameters, that expires in 1 minute', () => {
      const testName = 'booking_cookie';
      const testValue = 'AWU123123123';
      const testTime = 1;
      const expiryDate = new Date();
      expiryDate.setTime(expiryDate.getTime() + testTime * 60 * 1000);

      setCookie(testName, testValue, testTime);
      const expectedOutput = `booking_cookie=AWU123123123;expires=${expiryDate.toUTCString()};path=/;domain=localhost`;
      expect(document.cookie).toEqual(expectedOutput);
    });

    it('should set cookie with provided domain', () => {
      setCookie('testCookie', 'testValue', 10, '/', '', 'custom.domain.com');
      expect(document.cookie).toContain('domain=custom.domain.com');
      expect(document.cookie).toContain('testCookie=testValue');
    });

    it('should set cookie with correct path', () => {
      setCookie('testCookie4', 'testValue4', 10, '/somepath');
      expect(document.cookie).toContain('path=/somepath');
    });

    it('should set cookie with correct expires attribute', () => {
      setCookie('testCookie5', 'testValue5', 1);
      expect(document.cookie).toMatch(/expires=/);
    });
  });

  describe('deleteCookie Method', () => {
    beforeEach(() => {
      Object.defineProperty(document, 'cookie', {
        writable: true,
        value: 'a=1; b=2; c=3',
      });
    });

    it('should set expiry date to 1970 for a given cookie name', () => {
      deleteCookie('a');
      expect(document.cookie).toEqual(
        'a=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;domain=localhost'
      );
    });
  });

  describe('deleteCookieWithDefaultDomain', () => {
    beforeEach(() => {
      // Reset document.cookie before each test
      Object.defineProperty(document, 'cookie', {
        writable: true,
        value: '',
      });
    });

    it('should set the cookie with an expired date and path=/', () => {
      // Set a cookie first
      document.cookie = 'testCookie=someValue; path=/;';
      expect(document.cookie).toContain('testCookie=someValue');

      // Delete the cookie
      deleteCookieWithDefaultDomain('testCookie');

      // The cookie string should now contain the expired cookie
      expect(document.cookie).toContain(
        'testCookie=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;'
      );
    });

    it('should not throw if the cookie does not exist', () => {
      expect(() => deleteCookieWithDefaultDomain('nonExistentCookie')).not.toThrow();
      expect(document.cookie).toContain(
        'nonExistentCookie=;expires=Thu, 01 Jan 1970 00:00:01 GMT;path=/;'
      );
    });
  });
});

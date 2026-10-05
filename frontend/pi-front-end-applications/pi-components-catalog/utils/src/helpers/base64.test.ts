import { decodeFromBase64, encodeToBase64 } from './base64';

let windowSpy;

describe('base64', () => {
  describe('decodeFromBase64 Method', () => {
    beforeEach(() => {
      windowSpy = jest.spyOn(window, 'window', 'get');
    });

    afterEach(() => {
      windowSpy.mockRestore();
    });

    it('should return null if provided value is an empty string', () => {
      const result = decodeFromBase64('');
      expect(result).toEqual(null);
    });

    it('should return null if provided value is null', () => {
      const result = decodeFromBase64(null);
      expect(result).toEqual(null);
    });

    it('should return null if provided value is undefined', () => {
      const result = decodeFromBase64(undefined);
      expect(result).toEqual(null);
    });

    it('should return decoded string for a valid provided value', () => {
      const result = decodeFromBase64('aGVsbG9fd29ybGQ=');
      expect(result).toEqual('hello_world');
    });

    it('should return decoded string for a valid provided value even if window is not defined', () => {
      windowSpy.mockImplementation(() => undefined);
      const result = decodeFromBase64('aGVsbG9fd29ybGQ=');
      expect(result).toEqual('hello_world');
    });

    it('should return null if an error is thrown', () => {
      windowSpy.mockImplementation(() => undefined);
      jest.spyOn(Buffer, 'from').mockImplementationOnce(() => {
        throw new Error('error');
      });
      const result = decodeFromBase64('aGVsbG9fd29ybGQ=');
      expect(result).toEqual(null);
    });
  });

  describe('encodeToBase64 Method', () => {
    beforeEach(() => {
      windowSpy = jest.spyOn(window, 'window', 'get');
    });

    afterEach(() => {
      windowSpy.mockRestore();
    });

    it('should return null if provided value is an empty string', () => {
      const result = encodeToBase64('');
      expect(result).toEqual(null);
    });

    it('should return null if provided value is null', () => {
      const result = encodeToBase64(null);
      expect(result).toEqual(null);
    });

    it('should return null if provided value is undefined', () => {
      const result = encodeToBase64(undefined);
      expect(result).toEqual(null);
    });

    it('should return encoded string for a valid provided value', () => {
      const result = encodeToBase64('hello_world');
      expect(result).toEqual('aGVsbG9fd29ybGQ=');
    });

    it('should return encoded string for a valid provided value even if window is not defined', () => {
      windowSpy.mockImplementation(() => undefined);
      const result = encodeToBase64('hello_world');
      expect(result).toEqual('aGVsbG9fd29ybGQ=');
    });

    it('should return null if an error is thrown', () => {
      windowSpy.mockImplementation(() => undefined);
      jest.spyOn(Buffer, 'from').mockImplementationOnce(() => {
        throw new Error('error');
      });
      const result = encodeToBase64('hello_world');
      expect(result).toEqual(null);
    });
  });
});

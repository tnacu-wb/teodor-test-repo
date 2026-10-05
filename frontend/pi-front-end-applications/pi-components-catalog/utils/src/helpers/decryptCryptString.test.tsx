import crypto from 'crypto';

import { normalizeKey, pkcs7Unpad, decryptCryptString } from './decryptCryptString';

describe('decryptCryptString', () => {
  describe('normalizeKey', () => {
    it('should return buffer from valid base64 key with 16 bytes', () => {
      const key = Buffer.from('0123456789abcdef').toString('base64'); // 16 bytes base64
      const result = normalizeKey(key);
      expect(result).toEqual(Buffer.from('0123456789abcdef'));
    });

    it('should return buffer from valid base64 key with 24 bytes', () => {
      const key = Buffer.from('0123456789abcdef01234567').toString('base64'); // 24 bytes
      const result = normalizeKey(key);
      expect(result).toEqual(Buffer.from('0123456789abcdef01234567'));
    });

    it('should return utf8 buffer for valid utf8 key', () => {
      const key = '0123456789abcdef'; // 16 bytes utf8
      const result = normalizeKey(key);
      expect(result).toEqual(Buffer.from(key, 'utf8'));
    });

    it('should throw error for utf8 key with invalid length', () => {
      const key = 'short';
      expect(() => normalizeKey(key)).toThrow(
        'Key must be 16/24/32 bytes (after base64-decoding if encoded).'
      );
    });
  });

  describe('pkcs7Unpad', () => {
    it('should unpad valid PKCS7 padding', () => {
      const buf = Buffer.from([1, 2, 3, 1]); // last byte 1, so remove 1 byte
      const result = pkcs7Unpad(buf);
      expect(result).toEqual(Buffer.from([1, 2, 3]));
    });

    it('should unpad with padding of 16', () => {
      const buf = Buffer.concat([Buffer.from([1, 2, 3]), Buffer.alloc(13, 13)]); // 16 bytes, last 13 are 13
      const result = pkcs7Unpad(buf);
      expect(result).toEqual(Buffer.from([1, 2, 3]));
    });

    it('should throw error for invalid pad value 0', () => {
      const buf = Buffer.from([1, 2, 3, 0]);
      expect(() => pkcs7Unpad(buf)).toThrow('Invalid PKCS7 padding');
    });

    it('should throw error for invalid pad value 17', () => {
      const buf = Buffer.from([1, 2, 3, 17]);
      expect(() => pkcs7Unpad(buf)).toThrow('Invalid PKCS7 padding');
    });
  });

  describe('decryptCryptString', () => {
    it('should decrypt a valid cipher string', () => {
      const key = '0123456789abcdef'; // 16 bytes
      const plaintext = 'a';
      // Manually encrypt to get cipher
      const iv = Buffer.alloc(16, 0);
      const cipher = crypto.createCipheriv('aes-128-cbc', Buffer.from(key), iv);
      cipher.setAutoPadding(false);
      // Add PKCS7 padding to plaintext + check byte
      const ptWithCheck = Buffer.from(plaintext + 'b'); // add dummy check byte
      const blockSize = 16;
      const padding = blockSize - (ptWithCheck.length % blockSize);
      const padded = Buffer.concat([ptWithCheck, Buffer.alloc(padding, padding)]);
      const encrypted = Buffer.concat([cipher.update(padded), cipher.final()]);
      const cipherB64 = encrypted.toString('base64');

      const result = decryptCryptString(cipherB64, key);
      expect(result).toBe(plaintext);
    });

    it('should throw error for invalid base64 cipher', () => {
      const key = '0123456789abcdef';
      expect(() => decryptCryptString('invalid-base64!!!', key)).toThrow();
    });

    it('should throw error for invalid key', () => {
      const cipherB64 = Buffer.from('some data').toString('base64');
      expect(() => decryptCryptString(cipherB64, 'short')).toThrow('Key must be 16/24/32 bytes');
    });
  });
});

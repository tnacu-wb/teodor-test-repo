/**
 * Encryption Utility class for encoding/decoding base64 tokens
 */
export class EncryptionUtils {
  /**
   * Encode a string to base64
   * @param token - string that will be encoded
   * @returns encoded buffer
   */
  static encode(token: string): string {
    return Buffer.from(token).toString('base64');
  }

  /**
   * Decode a base64 token to string
   * @param token - string that will be decoded
   * @returns decoded buffer
   */
  static decode(token: string): string {
    return Buffer.from(String(token), 'base64').toString('utf8');
  }
}

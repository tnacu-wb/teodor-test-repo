import { HashType } from '@whitbread-eos/api';
import * as crypto from 'crypto';

/**
 * Hashes a given string using the specified hash type.
 * @param {string} input - The string to be hashed.
 * @param {HashType} hashType - The type of hash to use. Must be one of:
 * - HashType.SHA256
 * - HashType.SHA384
 * - HashType.SHA512
 * @returns {Promise<string>} A promise that resolves to a string containing the hash in hexadecimal format.
 * @throws {Error} Will throw an error if the hashing process fails.
 */
export async function hashString(input: string, hashType: HashType): Promise<string | undefined> {
  try {
    if (typeof window !== 'undefined' && window.crypto?.subtle) {
      // Running in the browser with Web Crypto API support
      const encoder = new TextEncoder();
      const data = encoder.encode(input);

      // Using the Web Crypto API to hash the data
      const hashBuffer = await window.crypto.subtle.digest(hashType, data);

      // Convert ArrayBuffer to a Hexadecimal string
      const hashArray: number[] = Array.from(new Uint8Array(hashBuffer));
      return hashArray.map((byte) => byte.toString(16).padStart(2, '0')).join('');
    } else {
      // Fallback for server-side (Node.js) using the Node.js 'crypto' module
      const nodeHash = crypto.createHash(convertHashTypeToNodeAlgorithm(hashType));
      nodeHash.update(input);
      return nodeHash.digest('hex');
    }
  } catch (error: unknown) {
    if (error instanceof Error) {
      console.error(`Failed to hash the provided string: ${error.message}`);
    } else {
      console.error(`Failed to hash the provided string due to an 'Unknown Error'`);
    }
  }

  return undefined;
}

/**
 * Converts the HashType enum to a Node.js crypto-compatible algorithm name.
 * @param {HashType} hashType - The hash type enum.
 * @returns {string} The corresponding Node.js crypto algorithm name.
 */
function convertHashTypeToNodeAlgorithm(hashType: HashType): string {
  switch (hashType) {
    case HashType.SHA256:
      return 'sha256';
    case HashType.SHA384:
      return 'sha384';
    case HashType.SHA512:
      return 'sha512';
    default:
      throw new Error(`Unsupported hash type: ${hashType}`);
  }
}

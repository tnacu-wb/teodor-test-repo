import { isStringValid } from '../validators';

export function decodeFromBase64(value: string | null | undefined) {
  try {
    if (!value || !isStringValid(value)) {
      return null;
    }

    const valueToString = value.toString();

    if (typeof window !== 'undefined') {
      return atob(valueToString);
    }

    return Buffer.from(valueToString, 'base64').toString('ascii');
  } catch (e) {
    return null;
  }
}

export function encodeToBase64(value: string | null | undefined) {
  try {
    if (!value || !isStringValid(value)) {
      return null;
    }

    const valueToString = value.toString();

    if (typeof window !== 'undefined') {
      return btoa(valueToString);
    }

    return Buffer.from(valueToString, 'ascii').toString('base64');
  } catch (e) {
    return null;
  }
}

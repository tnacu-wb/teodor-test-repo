import { describe, it, expect } from 'vitest';
import { isAxiosError } from '@/lib/types';

describe('isAxiosError', () => {
  it('returns true for a genuine Axios-shaped error', () => {
    const err = {
      isAxiosError: true,
      message: 'Request failed with status code 400',
      response: { status: 400, data: { detail: 'Bad Request' } },
    };
    expect(isAxiosError(err)).toBe(true);
  });

  it('returns true for an Axios error without a response (e.g. network error)', () => {
    const err = { isAxiosError: true, message: 'Network Error' };
    expect(isAxiosError(err)).toBe(true);
  });

  it('returns false for a plain Error even though it has a message', () => {
    expect(isAxiosError(new Error('boom'))).toBe(false);
  });

  it('returns false for a non-Axios object that happens to have message and response', () => {
    // Regression: previously any object with a string `message` passed, so an
    // unrelated object carrying a `response` field was misclassified.
    const notAxios = { message: 'looks similar', response: { status: 500, data: 'nope' } };
    expect(isAxiosError(notAxios)).toBe(false);
  });

  it('returns false when isAxiosError is not strictly true', () => {
    expect(isAxiosError({ isAxiosError: 'true', message: 'x' })).toBe(false);
    expect(isAxiosError({ isAxiosError: 1, message: 'x' })).toBe(false);
  });

  it('returns false when the marker is present but message is not a string', () => {
    expect(isAxiosError({ isAxiosError: true, message: 123 })).toBe(false);
    expect(isAxiosError({ isAxiosError: true })).toBe(false);
  });

  it('returns false for null, undefined and primitives', () => {
    expect(isAxiosError(null)).toBe(false);
    expect(isAxiosError(undefined)).toBe(false);
    expect(isAxiosError('error')).toBe(false);
    expect(isAxiosError(42)).toBe(false);
  });
});

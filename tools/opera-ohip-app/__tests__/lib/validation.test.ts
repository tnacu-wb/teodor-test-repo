import { describe, it, expect } from 'vitest';
import { validateHotelId, validateRatePlanCode } from '@/lib/ohip/validation';

describe('validateHotelId', () => {
  it('accepts a valid alphanumeric hotel ID', () => {
    expect(validateHotelId('WHBPI')).toBe('WHBPI');
  });

  it('trims whitespace', () => {
    expect(validateHotelId('  WHBPI  ')).toBe('WHBPI');
  });

  it('throws on empty string', () => {
    expect(() => validateHotelId('')).toThrow('Hotel ID is required');
  });

  it('throws on whitespace-only string', () => {
    expect(() => validateHotelId('   ')).toThrow('Hotel ID is required');
  });

  it('throws on special characters', () => {
    expect(() => validateHotelId('WHB-PI')).toThrow('Invalid Hotel ID');
    expect(() => validateHotelId('WHB/PI')).toThrow('Invalid Hotel ID');
    expect(() => validateHotelId('WHB PI')).toThrow('Invalid Hotel ID');
  });

  it('throws on IDs longer than 20 characters', () => {
    const longId = 'A'.repeat(21);
    expect(() => validateHotelId(longId)).toThrow('Must be 20 characters or fewer');
  });

  it('accepts ID at exactly 20 characters', () => {
    const maxId = 'A'.repeat(20);
    expect(validateHotelId(maxId)).toBe(maxId);
  });
});

describe('validateRatePlanCode', () => {
  it('accepts a valid alphanumeric code', () => {
    expect(validateRatePlanCode('FLEXRATE')).toBe('FLEXRATE');
  });

  it('accepts hyphens and underscores', () => {
    expect(validateRatePlanCode('FLEX-RATE_1')).toBe('FLEX-RATE_1');
  });

  it('trims whitespace', () => {
    expect(validateRatePlanCode('  FLEX  ')).toBe('FLEX');
  });

  it('throws on empty string', () => {
    expect(() => validateRatePlanCode('')).toThrow('Rate Plan Code is required');
  });

  it('throws on invalid characters', () => {
    expect(() => validateRatePlanCode('FLEX/RATE')).toThrow('Invalid Rate Plan Code');
    expect(() => validateRatePlanCode('FLEX RATE')).toThrow('Invalid Rate Plan Code');
    expect(() => validateRatePlanCode('FLEX@RATE')).toThrow('Invalid Rate Plan Code');
  });

  it('throws on codes longer than 50 characters', () => {
    const longCode = 'A'.repeat(51);
    expect(() => validateRatePlanCode(longCode)).toThrow('Must be 50 characters or fewer');
  });

  it('accepts code at exactly 50 characters', () => {
    const maxCode = 'A'.repeat(50);
    expect(validateRatePlanCode(maxCode)).toBe(maxCode);
  });
});

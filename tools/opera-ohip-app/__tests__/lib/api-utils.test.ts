import { describe, it, expect } from 'vitest';
import { validateRequiredFields } from '@/lib/api-utils';

describe('validateRequiredFields', () => {
  it('returns null when all required fields are present', () => {
    const body = { hotelId: 'WHBPI', ratePlanCode: 'FLEX' };
    const result = validateRequiredFields(body, ['hotelId', 'ratePlanCode']);
    expect(result).toBeNull();
  });

  it('returns error response when a field is missing', () => {
    const body = { hotelId: 'WHBPI' };
    const result = validateRequiredFields(body, ['hotelId', 'ratePlanCode']);
    expect(result).not.toBeNull();
    expect(result!.status).toBe(400);
  });

  it('returns error response when a field is empty string', () => {
    const body = { hotelId: '', ratePlanCode: 'FLEX' };
    const result = validateRequiredFields(body, ['hotelId', 'ratePlanCode']);
    expect(result).not.toBeNull();
    expect(result!.status).toBe(400);
  });

  it('returns error response when a field is null', () => {
    const body = { hotelId: null, ratePlanCode: 'FLEX' };
    const result = validateRequiredFields(body, ['hotelId', 'ratePlanCode']);
    expect(result).not.toBeNull();
    expect(result!.status).toBe(400);
  });

  it('returns error response when a field is undefined', () => {
    const body = { ratePlanCode: 'FLEX' };
    const result = validateRequiredFields(body, ['hotelId', 'ratePlanCode']);
    expect(result).not.toBeNull();
    expect(result!.status).toBe(400);
  });

  it('allows zero and false as valid values', () => {
    const body = { count: 0, enabled: false };
    const result = validateRequiredFields(body, ['count', 'enabled']);
    expect(result).toBeNull();
  });
});

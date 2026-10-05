import { getValidOrTodayDate } from './getValidOrTodayDate';

describe('getValidOrTodayDate method', () => {
  const fixedNow = new Date('2025-10-20T12:00:00Z');

  beforeAll(() => {
    jest.useFakeTimers();
    jest.setSystemTime(fixedNow);
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  it('should return today when inputs are undefined', () => {
    const today = fixedNow.toLocaleDateString('en-CA'); // 'YYYY-MM-DD'
    const result = getValidOrTodayDate(undefined, undefined, undefined, today);
    expect(result).toBe(today);
  });

  it('should accept string inputs and returns a future valid date', () => {
    const ARRdd = '25';
    const ARRmm = '12';
    const ARRyyyy = '2025';
    const today = fixedNow.toLocaleDateString('en-CA');

    const result = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
    expect(result).toBe('2025-12-25');
  });

  it('should accept array inputs (router.query style) and returns future valid date', () => {
    const ARRdd = ['15'];
    const ARRmm = ['11'];
    const ARRyyyy = ['2025'];
    const today = fixedNow.toLocaleDateString('en-CA');

    const result = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
    expect(result).toBe('2025-11-15');
  });

  it('should return today when the provided date is in the past', () => {
    const ARRdd = '10';
    const ARRmm = '09';
    const ARRyyyy = '2025';
    const today = fixedNow.toLocaleDateString('en-CA');

    const result = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
    expect(result).toBe(today);
  });

  it('returns today when given invalid numeric inputs', () => {
    const ARRdd = 'not-a-number';
    const ARRmm = '13';
    const ARRyyyy = 'abcd';
    const today = fixedNow.toLocaleDateString('en-CA');

    const result = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
    expect(result).toBe(today);
  });

  it('should handle edge case: selected date is exactly today', () => {
    const ARRdd = '20';
    const ARRmm = '10';
    const ARRyyyy = '2025';
    const today = fixedNow.toLocaleDateString('en-CA');

    const result = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
    expect(result).toBe(today);
  });

  it('should reject invalid calendar dates (e.g., 31 Sep) and returns today', () => {
    const ARRdd = '31';
    const ARRmm = '09';
    const ARRyyyy = '2025';
    const today = fixedNow.toLocaleDateString('en-CA');

    const result = getValidOrTodayDate(ARRdd, ARRmm, ARRyyyy, today);
    expect(result).toBe(today);
  });

  it('should handle leap year date correctly (29 Feb 2024 valid, 29 Feb 2025 invalid)', () => {
    const today = fixedNow.toLocaleDateString('en-CA');

    const r1 = getValidOrTodayDate('29', '02', '2024', today);
    expect(r1).toBe(today);

    const r2 = getValidOrTodayDate('29', '02', '2025', today);
    expect(r2).toBe(today);
  });
});

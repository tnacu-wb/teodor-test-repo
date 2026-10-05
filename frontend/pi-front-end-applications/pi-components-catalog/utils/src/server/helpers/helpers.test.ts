import { cachePromise } from './helpers';
import { extractProfileContactErrorCodes, findErrorCode } from './profile-errors';

const mockPromise = jest.fn((input: string) => {
  return new Promise((resolve) => {
    setTimeout(() => resolve(input));
  });
});

describe('cachePromise', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('calls the cached promise only once for the same input', async () => {
    const cachedPromise = cachePromise(mockPromise);

    const [result1, result2, result3] = await Promise.all([
      cachedPromise('abc'),
      cachedPromise('abc'),
      cachedPromise('abc'),
    ]);

    expect(result1).toBe('abc');
    expect(result2).toBe('abc');
    expect(result3).toBe('abc');

    expect(mockPromise).toBeCalledTimes(1);
  });

  it('calls the cached promise multiple times if the input changes', async () => {
    const cachedPromise = cachePromise(mockPromise);

    const [result1, result2, result3] = await Promise.all([
      cachedPromise('abc'),
      cachedPromise('test'),
      cachedPromise('abc'),
    ]);

    expect(result1).toBe('abc');
    expect(result2).toBe('test');
    expect(result3).toBe('abc');

    expect(mockPromise).toBeCalledTimes(2);
  });
});

jest.mock('../getters');

describe('profile error helpers', () => {
  it('finds a matching error code in GraphQL errors', () => {
    const errors = [{ message: '{"code":"037"}' }];
    expect(findErrorCode('037', errors)).toBe(true);
    expect(findErrorCode('999', errors)).toBe(false);
  });

  it('extracts profile contact error codes from 7102 payload', () => {
    const errors = [
      {
        message: JSON.stringify({
          code: '7102',
          details: ['WorldLine errors when updating contact details: 2, 4, 10', 'Other info: 11'],
        }),
      },
    ];
    expect(extractProfileContactErrorCodes(errors)).toEqual([2, 4, 10, 11]);
  });

  it('returns empty array when no 7102 errors are present', () => {
    const errors = [{ message: '{"code":"1234"}' }];
    expect(extractProfileContactErrorCodes(errors)).toEqual([]);
  });
});

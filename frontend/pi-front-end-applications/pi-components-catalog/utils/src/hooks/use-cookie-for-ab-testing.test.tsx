import { act } from '@testing-library/react';
import { renderHook } from '@testing-library/react';

import { getCookie } from '../helpers';
import useCookieForABTesting from './use-cookie-for-ab-testing';

// Mock the getCookie function
jest.mock('../helpers', () => ({
  getCookie: jest.fn(),
}));

describe('useCookieForABTesting', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should return false when the cookie does not exist', () => {
    (getCookie as jest.Mock).mockReturnValue('');

    const { result } = renderHook(() => useCookieForABTesting('test_cookie', 'expected_value'));

    expect(result.current).toBeFalsy();
    expect(getCookie).toHaveBeenCalledWith('test_cookie');
  });

  it('should return false when the cookie value does not match the expected mode', () => {
    (getCookie as jest.Mock).mockReturnValue('different_value');

    const { result } = renderHook(() => useCookieForABTesting('test_cookie', 'expected_value'));

    expect(result.current).toBe(false);
    expect(getCookie).toHaveBeenCalledWith('test_cookie');
  });

  it('should return true when the cookie value matches the expected mode', () => {
    (getCookie as jest.Mock).mockReturnValue('expected_value');

    const { result } = renderHook(() => useCookieForABTesting('test_cookie', 'expected_value'));

    expect(result.current).toBe(true);
    expect(getCookie).toHaveBeenCalledWith('test_cookie');
  });

  it('should update the state if the cookie changes', () => {
    let cookieValue = 'initial_value';
    (getCookie as jest.Mock).mockImplementation(() => cookieValue);

    const { result, rerender } = renderHook(() =>
      useCookieForABTesting('test_cookie', 'expected_value')
    );

    expect(result.current).toBe(false); // Initial value doesn't match

    // Simulate cookie update
    act(() => {
      cookieValue = 'expected_value';
      rerender();
    });
    expect(result.current).toBeFalsy();
  });

  it('should handle changes to the cookie name or expected mode', () => {
    (getCookie as jest.Mock).mockReturnValue('expected_value');

    const { result, rerender } = renderHook(
      ({ cookieName, expectedMode }) => useCookieForABTesting(cookieName, expectedMode),
      {
        initialProps: { cookieName: 'test_cookie', expectedMode: 'expected_value' },
      }
    );

    expect(result.current).toBe(true);

    // Change expectedMode to one that doesn't match the cookie value
    rerender({ cookieName: 'test_cookie', expectedMode: 'other_value' });
    expect(result.current).toBe(false);

    // Change cookieName to a non-existent cookie
    (getCookie as jest.Mock).mockReturnValue(null);
    rerender({ cookieName: 'new_cookie', expectedMode: 'expected_value' });
    expect(result.current).toBe(null);
  });
});

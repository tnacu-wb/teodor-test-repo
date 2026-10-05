import { renderHook } from '@testing-library/react';
import { useRouter } from 'next/router';

import * as findBookingTokenModule from '../getters/findBookingToken';
import useAmendCookieValidation from './use-amend-cookie-validation';

jest.mock('next/router', () => ({
  useRouter: jest.fn(),
}));

jest.mock('../getters/findBookingToken', () => ({
  getFindBookingToken: jest.fn(),
}));

describe('useAmendCookieValidation', () => {
  const mockUseRouter = useRouter as jest.MockedFunction<typeof useRouter>;
  const mockGetFindBookingToken = findBookingTokenModule.getFindBookingToken as jest.MockedFunction<
    typeof findBookingTokenModule.getFindBookingToken
  >;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return invalid state when basketReference is missing', () => {
    mockUseRouter.mockReturnValue({
      query: { bookingReference: 'BK123' },
    } as any);

    mockGetFindBookingToken.mockReturnValue({
      token: 'test-token',
      basketReference: undefined,
      bookingReference: 'BK123',
    } as any);

    const { result } = renderHook(() => useAmendCookieValidation());

    expect(result.current.isValid).toBe(false);
    expect(result.current.error).toBe('missing-basket-reference');
  });

  it('should return invalid state when token is missing', () => {
    mockUseRouter.mockReturnValue({
      query: { bookingReference: 'BK123' },
    } as any);

    mockGetFindBookingToken.mockReturnValue({
      token: undefined,
      basketReference: 'basket-ref',
      bookingReference: 'BK123',
    } as any);

    const { result } = renderHook(() => useAmendCookieValidation());

    expect(result.current.isValid).toBe(false);
    expect(result.current.error).toBe('missing-token');
  });

  it('should return invalid state when booking reference does not match', () => {
    mockUseRouter.mockReturnValue({
      query: { bookingReference: 'BK123' },
    } as any);

    mockGetFindBookingToken.mockReturnValue({
      token: 'test-token',
      basketReference: 'basket-ref',
      bookingReference: 'BK456',
    } as any);

    const { result } = renderHook(() => useAmendCookieValidation());

    expect(result.current.isValid).toBe(false);
    expect(result.current.error).toBe('booking-reference-mismatch');
  });

  it('should return valid state when all values are present and match', () => {
    mockUseRouter.mockReturnValue({
      query: { bookingReference: 'BK123' },
    } as any);

    mockGetFindBookingToken.mockReturnValue({
      token: 'test-token',
      basketReference: 'basket-ref',
      bookingReference: 'BK123',
    } as any);

    const { result } = renderHook(() => useAmendCookieValidation());

    expect(result.current.isValid).toBe(true);
    expect(result.current.token).toBe('test-token');
    expect(result.current.basketReference).toBe('basket-ref');
    expect(result.current.bookingReference).toBe('BK123');
    expect(result.current.error).toBe(null);
  });

  it('should handle no booking reference in query', () => {
    mockUseRouter.mockReturnValue({
      query: {},
    } as any);

    const { result } = renderHook(() => useAmendCookieValidation());

    expect(result.current.isValid).toBe(false);
    expect(result.current.error).toBe('missing-token');
  });
});

import { renderHook, act } from '@testing-library/react';
import { getAuthCookie } from '@whitbread-eos/utils';
import { getDetailsFromToken, getPayApplicationDetails } from '@whitbread-eos/utils/server';

import { usePayAppAccessValidation } from './usePayAppAccessValidation';

const mockPush = jest.fn();
const mockReplace = jest.fn();
let mockPathname = '/en/business-pay/apply/test';

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: mockPush,
    replace: mockReplace,
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => mockPathname,
}));

jest.mock('@whitbread-eos/utils', () => ({
  getAuthCookie: jest.fn(),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  getDetailsFromToken: jest.fn(),
  getPayApplicationDetails: jest.fn(),
}));

const mockGetAuthCookie = getAuthCookie as jest.MockedFunction<typeof getAuthCookie>;
const mockGetDetailsFromToken = getDetailsFromToken as jest.MockedFunction<
  typeof getDetailsFromToken
>;
const mockGetPayApplicationDetails = getPayApplicationDetails as jest.MockedFunction<
  typeof getPayApplicationDetails
>;

describe('usePayAppAccessValidation', () => {
  const mockWizardState = {
    applicationGuid: 'test-guid',
    applicationId: 'test-id',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.spyOn(console, 'error').mockImplementation(() => undefined);
    Object.defineProperty(window, 'location', {
      value: { pathname: mockPathname },
      writable: true,
    });
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('validatePayAppAccess', () => {
    it('should return true when pathname does not include business-pay/apply', async () => {
      mockPathname = '/en/some-other-path';

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      const hasAccess = await result.current.validatePayAppAccess();
      expect(hasAccess).toBe(true);
    });

    it('should return false when no auth token is present', async () => {
      mockPathname = '/en/business-pay/apply/test';
      mockGetAuthCookie.mockReturnValue(null as any);

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      const hasAccess = await result.current.validatePayAppAccess();
      expect(hasAccess).toBe(false);
    });

    it('should redirect to access restricted page when user has no access', async () => {
      mockPathname = '/en-gb/business-pay/apply/test';
      const mockToken = 'mock-token';
      const mockEmail = 'test@example.com';

      mockGetAuthCookie.mockReturnValue(mockToken);
      mockGetDetailsFromToken.mockReturnValue({
        email: mockEmail,
        companyId: 'test-company',
        employeeId: 'test-employee',
        accessLevel: 'user',
        isTravelManager: false,
        isBooker: true,
        isSelfBooker: true,
        isGuest: false,
        customerId: 'test-customer',
        profile: {},
        isBusinessPayManager: false,
        isBusinessPayUser: false,
      });
      mockGetPayApplicationDetails.mockResolvedValue({
        participants: [
          {
            email: 'other@example.com',
            companyId: 'test-company',
            employeeId: 'test-employee',
            accessLevel: 'user',
            isTravelManager: false,
            isBooker: true,
            isSelfBooker: true,
            isGuest: false,
          },
        ],
      });

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      const hasAccess = await result.current.validatePayAppAccess();

      expect(hasAccess).toBe(false);
      expect(mockReplace).toHaveBeenCalledWith('/en-gb/pay-application-access-restricted');
      expect(mockGetPayApplicationDetails).toHaveBeenCalledWith(
        mockToken,
        'test-guid',
        'test-id',
        'GB'
      );
    });

    it('should return true when user has access to the application', async () => {
      const mockToken = 'mock-token';
      const mockEmail = 'test@example.com';

      mockGetAuthCookie.mockReturnValue(mockToken);
      mockGetDetailsFromToken.mockReturnValue({
        email: mockEmail,
        companyId: 'test-company',
        employeeId: 'test-employee',
        accessLevel: 'user',
        isTravelManager: false,
        isBooker: true,
        isSelfBooker: true,
        isGuest: false,
        customerId: 'test-customer',
        profile: {},
        isBusinessPayManager: false,
        isBusinessPayUser: false,
      });
      mockGetPayApplicationDetails.mockResolvedValue({
        participants: [
          {
            email: 'test@example.com',
            companyId: 'test-company',
            employeeId: 'test-employee',
            accessLevel: 'user',
            isTravelManager: false,
            isBooker: true,
            isSelfBooker: true,
            isGuest: false,
          },
          {
            email: 'other@example.com',
            companyId: 'test-company-2',
            employeeId: 'test-employee-2',
            accessLevel: 'user',
            isTravelManager: false,
            isBooker: true,
            isSelfBooker: true,
            isGuest: false,
          },
        ],
      });

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      const hasAccess = await result.current.validatePayAppAccess();

      expect(hasAccess).toBe(true);
      expect(mockReplace).not.toHaveBeenCalled();
    });

    it('should return true when an error occurs during validation', async () => {
      const mockToken = 'mock-token';

      mockGetAuthCookie.mockReturnValue(mockToken);
      mockGetDetailsFromToken.mockImplementation(() => {
        throw new Error('Token parsing failed');
      });

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      const hasAccess = await result.current.validatePayAppAccess();

      expect(hasAccess).toBe(true);
      expect(console.error).toHaveBeenCalledWith(
        'Error validating participant access:',
        expect.any(Error)
      );
    });
  });

  describe('withValidation', () => {
    it('should execute function when user has access', async () => {
      const mockToken = 'mock-token';
      const mockEmail = 'test@example.com';
      const mockFn = jest.fn();

      mockGetAuthCookie.mockReturnValue(mockToken);
      mockGetDetailsFromToken.mockReturnValue({
        email: mockEmail,
        companyId: 'test-company',
        employeeId: 'test-employee',
        accessLevel: 'user',
        isTravelManager: false,
        isBooker: true,
        isSelfBooker: true,
        isGuest: false,
        customerId: 'test-customer',
        profile: {},
        isBusinessPayManager: false,
        isBusinessPayUser: false,
      });
      mockGetPayApplicationDetails.mockResolvedValue({
        participants: [
          {
            email: 'test@example.com',
            companyId: 'test-company',
            employeeId: 'test-employee',
            accessLevel: 'user',
            isTravelManager: false,
            isBooker: true,
            isSelfBooker: true,
            isGuest: false,
          },
        ],
      });

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      await act(async () => {
        await result.current.withValidation(mockFn);
      });

      expect(mockFn).toHaveBeenCalled();
    });

    it('should not execute function when user has no access', async () => {
      const mockToken = 'mock-token';
      const mockEmail = 'test@example.com';
      const mockFn = jest.fn();

      mockGetAuthCookie.mockReturnValue(mockToken);
      mockGetDetailsFromToken.mockReturnValue({
        email: mockEmail,
        companyId: 'test-company',
        employeeId: 'test-employee',
        accessLevel: 'user',
        isTravelManager: false,
        isBooker: true,
        isSelfBooker: true,
        isGuest: false,
        customerId: 'test-customer',
        profile: {},
        isBusinessPayManager: false,
        isBusinessPayUser: false,
      });
      mockGetPayApplicationDetails.mockResolvedValue({
        participants: [
          {
            email: 'other@example.com',
            companyId: 'test-company',
            employeeId: 'test-employee',
            accessLevel: 'user',
            isTravelManager: false,
            isBooker: true,
            isSelfBooker: true,
            isGuest: false,
          },
        ],
      });

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      await act(async () => {
        await result.current.withValidation(mockFn);
      });

      expect(mockFn).not.toHaveBeenCalled();
    });

    it('should handle async functions in withValidation', async () => {
      const mockToken = 'mock-token';
      const mockEmail = 'test@example.com';
      const mockAsyncFn = jest.fn().mockResolvedValue('success');

      mockGetAuthCookie.mockReturnValue(mockToken);
      mockGetDetailsFromToken.mockReturnValue({
        email: mockEmail,
        companyId: 'test-company',
        employeeId: 'test-employee',
        accessLevel: 'user',
        isTravelManager: false,
        isBooker: true,
        isSelfBooker: true,
        isGuest: false,
        customerId: 'test-customer',
        profile: {},
        isBusinessPayManager: false,
        isBusinessPayUser: false,
      });
      mockGetPayApplicationDetails.mockResolvedValue({
        participants: [
          {
            email: 'test@example.com',
            companyId: 'test-company',
            employeeId: 'test-employee',
            accessLevel: 'user',
            isTravelManager: false,
            isBooker: true,
            isSelfBooker: true,
            isGuest: false,
          },
        ],
      });

      const { result } = renderHook(() => usePayAppAccessValidation(mockWizardState));

      await act(async () => {
        await result.current.withValidation(mockAsyncFn);
      });

      expect(mockAsyncFn).toHaveBeenCalled();
    });
  });
});

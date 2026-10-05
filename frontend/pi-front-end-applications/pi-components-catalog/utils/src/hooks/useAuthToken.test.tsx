import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';

import * as auth from '../getters/auth';
import * as useFeatureToggleModule from './use-feature-toggle';
import * as useAuth0AccessTokenModule from './useAuth0AccessToken';
import { useAuthToken } from './useAuthToken';

// Mock dependencies
jest.mock('../getters/auth');
jest.mock('./useAuth0AccessToken');
jest.mock('./use-feature-toggle');

describe('useAuthToken', () => {
  const mockGetAuthCookie = auth.getAuthCookie as jest.MockedFunction<typeof auth.getAuthCookie>;
  const mockUseAuth0AccessToken =
    useAuth0AccessTokenModule.useAuth0AccessToken as jest.MockedFunction<
      typeof useAuth0AccessTokenModule.useAuth0AccessToken
    >;
  const mockUseFeatureToggle = useFeatureToggleModule.default as jest.MockedFunction<
    typeof useFeatureToggleModule.default
  >;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('when FT_PI_AUTH0_LOGIN is false (legacy mode)', () => {
    beforeEach(() => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: false });
      mockUseAuth0AccessToken.mockReturnValue({
        accessToken: null,
        isLoading: false,
        error: null,
      });
    });

    it('should return legacy id_token_cookie', () => {
      const mockLegacyCookie = 'legacyvalue';
      mockGetAuthCookie.mockReturnValue(mockLegacyCookie);

      const result = useAuthToken();

      expect(result.token).toBe(mockLegacyCookie);
      expect(result.isAuth0Enabled).toBe(false);
      expect(result.isLoading).toBe(false);
      expect(mockGetAuthCookie).toHaveBeenCalled();
    });

    it('should not enable Auth0 token hook when flag is off', () => {
      mockGetAuthCookie.mockReturnValue('some-token');

      useAuthToken();

      expect(mockUseAuth0AccessToken).toHaveBeenCalledWith(false);
    });
  });

  describe('when FT_PI_AUTH0_LOGIN is true (Auth0 mode)', () => {
    beforeEach(() => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });
    });

    it('should return Auth0 access token when available', () => {
      const mockAuth0Value = 'samplevalue';
      mockUseAuth0AccessToken.mockReturnValue({
        accessToken: mockAuth0Value,
        isLoading: false,
        error: null,
      });

      const result = useAuthToken();

      expect(result.token).toBe(mockAuth0Value);
      expect(result.isAuth0Enabled).toBe(true);
      expect(result.isLoading).toBe(false);
      expect(mockUseAuth0AccessToken).toHaveBeenCalledWith(true);
    });

    it('should fall back to legacy token when Auth0 token is null', () => {
      const mockLegacyCookie = 'placeholder';
      mockUseAuth0AccessToken.mockReturnValue({
        accessToken: null,
        isLoading: false,
        error: null,
      });
      mockGetAuthCookie.mockReturnValue(mockLegacyCookie);

      const result = useAuthToken();

      expect(result.token).toBe(mockLegacyCookie);
      expect(result.isAuth0Enabled).toBe(true);
      expect(mockGetAuthCookie).toHaveBeenCalled();
    });

    it('should return isLoading true when Auth0 token is loading', () => {
      mockUseAuth0AccessToken.mockReturnValue({
        accessToken: null,
        isLoading: true,
        error: null,
      });

      const result = useAuthToken();

      expect(result.isLoading).toBe(true);
      expect(result.isAuth0Enabled).toBe(true);
    });
  });

  describe('when feature toggle is undefined', () => {
    it('should default to legacy mode', () => {
      mockUseFeatureToggle.mockReturnValue({});
      const mockLegacyCookie = 'samplevalue';
      mockGetAuthCookie.mockReturnValue(mockLegacyCookie);
      mockUseAuth0AccessToken.mockReturnValue({
        accessToken: null,
        isLoading: false,
        error: null,
      });

      const result = useAuthToken();

      expect(result.token).toBe(mockLegacyCookie);
      expect(result.isAuth0Enabled).toBe(false);
    });
  });

  describe('when feature toggle hook returns null', () => {
    it('should default to legacy mode', () => {
      mockUseFeatureToggle.mockReturnValue(null as any);
      const mockLegacyCookie = 'samplevalue';
      mockGetAuthCookie.mockReturnValue(mockLegacyCookie);
      mockUseAuth0AccessToken.mockReturnValue({
        accessToken: null,
        isLoading: false,
        error: null,
      });

      const result = useAuthToken();

      expect(result.token).toBe(mockLegacyCookie);
      expect(result.isAuth0Enabled).toBe(false);
    });
  });
});

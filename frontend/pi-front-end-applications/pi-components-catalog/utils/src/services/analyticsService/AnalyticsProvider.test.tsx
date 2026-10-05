import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { render, waitFor } from '../../utils/test-utils';
import { AnalyticsProviderComponent } from './AnalyticsProvider.component';
import analytics from './analytics';

const mockUseFeatureToggle = jest.fn<Record<string, boolean>, []>(() => ({}));
const mockConfigureAnalytics = jest.fn<void, [{ isAnalyticsDebounceEnabled?: boolean }]>();

let mockIsLoggedIn = true;
let mockIsSuccess = true;
let mockIsError = false;

jest.mock('../../store/UserContext', () => ({
  useUserData: () => ({ isLoggedIn: mockIsLoggedIn }),
}));

const defaultMockContactDetails = {
  mobile: '123',
  telephone: '456',
  firstName: 'John',
  lastName: 'Doe',
  address: { countryCode: 'GB' },
} as any;

let mockContactDetails = { ...defaultMockContactDetails };

jest.mock('../../hooks', () => ({
  useUserDetails: () => ({
    contactDetail: mockContactDetails,
    customerAccountId: 'acc1',
  }),
  useRestQueryRequest: () => ({ isSuccess: mockIsSuccess, isError: mockIsError }),
  useFeatureToggle: () => mockUseFeatureToggle(),
}));

jest.mock('./analytics', () => {
  const actual = jest.requireActual('./analytics');

  return {
    __esModule: true,
    ...actual,
    configureAnalytics: (options: { isAnalyticsDebounceEnabled?: boolean }) =>
      mockConfigureAnalytics(options),
  };
});

jest.mock('../../getters/auth', () => ({
  getAuthCookie: () => 'cookie',
  decodeIdToken: () => ({ email: 'test@email.com' }),
  getLoggedInUserInfo: () => ({
    cdhEmployeeId: 'emp1',
    cdhCustomerId: 'cust1',
  }),
}));
jest.mock('../../helpers/hashing', () => ({
  hashString: async (val: string) => `hashed-${val}`,
}));

const mockUseAuthToken = jest.fn();
const mockUseAuth0User = jest.fn();

jest.mock('../../hooks/useAuthToken', () => ({
  useAuthToken: (...args: unknown[]) => mockUseAuthToken(...args),
}));

jest.mock('../../hooks/useAuth0User', () => ({
  useAuth0User: (...args: unknown[]) => mockUseAuth0User(...args),
}));

const props = {
  children: <div data-testid="test">Test</div>,
};

describe('<AnalyticsProviderComponent />', () => {
  const queryClient = new QueryClient();

  beforeEach(() => {
    mockIsLoggedIn = true;
    mockIsSuccess = true;
    mockIsError = false;
    mockContactDetails = {
      ...defaultMockContactDetails,
      address: { ...defaultMockContactDetails.address },
    };
    mockUseFeatureToggle.mockReturnValue({});
    mockConfigureAnalytics.mockClear();
    // Use legacy path by default so existing tests are unaffected
    mockUseAuthToken.mockReturnValue({ token: 'cookie', isAuth0Enabled: false, isLoading: false });
    mockUseAuth0User.mockReturnValue({ user: null, loading: false, error: null });
    window.analyticsData = {};
  });

  it('should update analytics with user details', async () => {
    render(
      <QueryClientProvider client={queryClient}>
        <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(window.analyticsData.userMobile).toBe('hashed-123');
    });
  });

  it('should update analytics with user details but no contact details', async () => {
    mockContactDetails.mobile = undefined;
    mockContactDetails.telephone = undefined;
    mockContactDetails.firstName = undefined;
    mockContactDetails.lastName = undefined;
    mockContactDetails.address = { countryCode: undefined };

    render(
      <QueryClientProvider client={queryClient}>
        <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
      </QueryClientProvider>
    );
    await waitFor(() => {
      expect(window.analyticsData.userMobile).toBe(undefined);
      expect(window.analyticsData.userTelephone).toBe(undefined);
      expect(window.analyticsData.userFN).toBe(undefined);
      expect(window.analyticsData.userLN).toBe(undefined);
      expect(window.analyticsData.userCountry).toBe(undefined);
    });
  });

  it('should configure analytics debounce when the feature flag is enabled', async () => {
    mockUseFeatureToggle.mockReturnValue({
      release_pi_analytics_debounce: true,
    });

    render(
      <QueryClientProvider client={queryClient}>
        <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(mockConfigureAnalytics).toHaveBeenCalledWith({
        isAnalyticsDebounceEnabled: true,
      });
    });
  });

  describe('Auth0 user analytics', () => {
    it('sets userLoggedIn to Logged In when Auth0 user is resolved and request succeeds', async () => {
      mockUseAuthToken.mockReturnValue({
        token: 'Test',
        isAuth0Enabled: true,
        isLoading: false,
      });
      mockUseAuth0User.mockReturnValue({
        user: { email: 'auth0user@example.com' },
        loading: false,
        error: null,
      });

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      await waitFor(() => {
        expect(window.analyticsData.userLoggedIn).toBe('Logged In');
      });
    });

    it('uses Auth0 email for userHashedEA when Auth0 is enabled', async () => {
      mockUseAuthToken.mockReturnValue({
        token: 'Test',
        isAuth0Enabled: true,
        isLoading: false,
      });
      mockUseAuth0User.mockReturnValue({
        user: { email: 'auth0user@example.com' },
        loading: false,
        error: null,
      });

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      await waitFor(() => {
        expect(window.analyticsData.userHashedEA).toBe('hashed-auth0user@example.com');
      });
    });

    it('does not set userLoggedIn to Logged In when Auth0 user email is not yet resolved', async () => {
      mockUseAuthToken.mockReturnValue({
        token: 'Test',
        isAuth0Enabled: true,
        isLoading: true,
      });
      mockUseAuth0User.mockReturnValue({ user: null, loading: true, error: null });
      mockIsSuccess = false;

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      // Wait a tick to confirm nothing was set
      await new Promise((resolve) => setTimeout(resolve, 0));
      expect(window.analyticsData.userLoggedIn).not.toBe('Logged In');
    });

    it('sets userLoggedIn to Not Logged In when Auth0 is enabled but user is not logged in', async () => {
      mockIsLoggedIn = false;
      mockIsSuccess = false;
      mockUseAuthToken.mockReturnValue({
        token: '',
        isAuth0Enabled: true,
        isLoading: false,
      });
      mockUseAuth0User.mockReturnValue({ user: null, loading: false, error: null });

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      await waitFor(() => {
        expect(window.analyticsData.userLoggedIn).toBe('Not Logged In');
      });
    });

    it('clears user analytics fields when Auth0 user logs out', async () => {
      mockIsLoggedIn = false;
      mockIsSuccess = false;
      mockUseAuthToken.mockReturnValue({
        token: '',
        isAuth0Enabled: true,
        isLoading: false,
      });
      mockUseAuth0User.mockReturnValue({ user: null, loading: false, error: null });

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      await waitFor(() => {
        expect(window.analyticsData.userID).toBeUndefined();
        expect(window.analyticsData.userHashedEA).toBeUndefined();
        expect(window.analyticsData.userFN).toBeUndefined();
        expect(window.analyticsData.userLN).toBeUndefined();
      });
    });
  });

  describe('auth_sign_in_success tracking', () => {
    let trackSpy: jest.SpyInstance;

    beforeEach(() => {
      trackSpy = jest.spyOn(analytics, 'track').mockClear();
    });

    afterEach(() => {
      window.history.replaceState({}, '', '/');
      trackSpy.mockRestore();
    });

    it('strips the marker params immediately but only tracks auth_sign_in_success once the user-details fetch succeeds', async () => {
      window.history.pushState(
        {},
        '',
        '/gb/en/guest-details.html?authSignInSuccess=1&authRedirectPageType=guest_details_page'
      );

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      // Marker is stripped from the URL right away, regardless of fetch status
      await waitFor(() => {
        expect(window.location.search).toBe('');
      });

      // mockIsSuccess defaults to true, so the user-details fetch "succeeds"
      // in the same tick - the event fires only once that happens
      await waitFor(() => {
        expect(trackSpy).toHaveBeenCalledWith('auth_sign_in_success', {
          authStep: 'success',
          authRedirectPageType: 'guest_details_page',
        });
      });
    });

    it('does not track auth_sign_in_success when the user-details fetch never succeeds, even with the marker present', async () => {
      mockIsSuccess = false;
      mockIsError = true;
      window.history.pushState(
        {},
        '',
        '/gb/en/guest-details.html?authSignInSuccess=1&authRedirectPageType=guest_details_page'
      );

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      await waitFor(() => {
        expect(window.analyticsData.userLoggedIn).toBe('Not Logged In');
      });
      expect(trackSpy).not.toHaveBeenCalledWith('auth_sign_in_success', expect.anything());
    });

    it('does not track auth_sign_in_success on a plain page load', async () => {
      window.history.pushState({}, '', '/gb/en/guest-details.html');

      render(
        <QueryClientProvider client={queryClient}>
          <AnalyticsProviderComponent {...props} isBusinessBooker={false} />
        </QueryClientProvider>
      );

      await new Promise((resolve) => setTimeout(resolve, 0));
      expect(trackSpy).not.toHaveBeenCalledWith('auth_sign_in_success', expect.anything());
    });
  });
});

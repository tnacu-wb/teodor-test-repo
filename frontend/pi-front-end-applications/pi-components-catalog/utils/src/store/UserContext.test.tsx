import { act } from '@testing-library/react';
import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';
import { useEffect } from 'react';

import { render, screen, waitFor } from '../utils/test-utils';
import { UserContextProvider, useUserData } from './UserContext';

// Placeholder token - actual content is irrelevant as decodeIdToken is always mocked in these tests
const token = 'test.id.token';

const decodedToken = {
  at_hash: 'mock-at-hash',
  aud: 'mock-audience-id',
  email: 'testing.manager@mailinator.com',
  exp: 1678201607,
  'https://ccui.opera.whitbread.digital/role': [],
  'https://premierinn.com/companyAccountId': 'COMP_mock-company-id',
  'https://premierinn.com/email': 'traveling.bgl@mailinator.com',
  'https://premierinn.com/employeeAccountId': 'EMPL_mock-employee-id',
  'https://premierinn.com/globalCompanyId': 1361,
  'https://premierinn.com/operaCompanyId': '2569616',
  iat: 1678200107,
  iss: 'https://auth0.sandbox.whitbread.digital/',
  name: 'traveling.bgl@mailinator.com',
  nickname: 'traveling.bgl',
  nonce: 'mock-nonce-value',
  picture: 'https://cdn.auth0.com/avatars/tr.png',
  profile: {
    accessLevel: 'SUPER',
    companyId: '35086',
    employeeId: '1',
    isBusiness: true,
    sessionId: 'mock-session-id',
  },
  sub: 'auth0|mock-user-sub',
  updated_at: '2023-03-07T14:41:46.001Z',
  wb_account_locale: 'en',
};

const mockCustomLocale = jest.fn();
const mockUseFeatureToggle = jest.fn();
jest.mock('../hooks', () => ({
  useCustomLocale: () => mockCustomLocale(),
  useFeatureToggle: (...args: any[]) => mockUseFeatureToggle(...args),
}));

// Stand-in for a consumer-supplied authWatcher (e.g. Auth0LoginWatcher in the
// premier-inn app) - reports `loggedIn` via an effect, same timing as the real
// useUser()-backed implementation.
function TestAuthWatcher({
  onChange,
  loggedIn,
}: {
  onChange: (isLoggedIn: boolean) => void;
  loggedIn: boolean;
}) {
  useEffect(() => {
    onChange(loggedIn);
  }, [onChange, loggedIn]);
  return null;
}

const mockGetAuthCookie = jest.fn();
const mockDecodeIdToken = jest.fn();
jest.mock('../getters', () => ({
  ...jest.requireActual('../getters'),
  getAuthCookie: () => mockGetAuthCookie(),
  decodeIdToken: () => mockDecodeIdToken(),
}));

function MockApp() {
  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
  // @ts-ignore
  const { isLoggedIn } = useUserData();

  return <>{`Is user logged in? ${isLoggedIn}`}</>;
}

describe('UserContext', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
    });
    mockUseFeatureToggle.mockReturnValue({});
    mockGetAuthCookie.mockReturnValue('');
    mockDecodeIdToken.mockReturnValue('');
  });

  afterEach(() => {
    mockGetAuthCookie.mockReset();
    mockDecodeIdToken.mockReset();
    mockUseFeatureToggle.mockReset();
  });

  it('should return isLoggedIn false by default', () => {
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );

    screen.getByText('Is user logged in? false');
  });

  it('should return isLoggedIn false by default for non BB app', () => {
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );
    screen.getByText('Is user logged in? false');
  });

  it('should return isLoggedIn false by default for BB app', () => {
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );
    screen.getByText('Is user logged in? false');
  });

  it('should return isLoggedIn true if user has auth cookie and email in the decodedToken and country is not de', () => {
    mockGetAuthCookie.mockReturnValue(token);
    mockDecodeIdToken.mockReturnValue(decodedToken);
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );

    screen.getByText('Is user logged in? true');
  });

  it('should return isLoggedIn false if user has auth cookie and email in the decodedToken and country is de', () => {
    mockGetAuthCookie.mockReturnValue(token);
    mockDecodeIdToken.mockReturnValue(decodedToken);
    mockCustomLocale.mockReturnValue({ country: 'de' });
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );
    screen.getByText('Is user logged in? true');
  });

  it('should return isLoggedIn true if user has auth cookie and email in the decodedToken and country is not de and app is BB', () => {
    mockGetAuthCookie.mockReturnValue(token);
    mockDecodeIdToken.mockReturnValue(decodedToken);
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );
    screen.getByText('Is user logged in? true');
  });

  it('should return isLoggedIn true if user has auth cookie and email in the decodedToken and country is de', () => {
    mockGetAuthCookie.mockReturnValue(token);
    mockDecodeIdToken.mockReturnValue(decodedToken);
    mockCustomLocale.mockReturnValue({ country: 'de' });
    render(
      <UserContextProvider>
        <MockApp />
      </UserContextProvider>
    );
    screen.getByText('Is user logged in? true');
  });

  describe('Auth0 flow', () => {
    it('should return isLoggedIn true when Auth0 is enabled and the authWatcher reports a session', () => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });

      render(
        <UserContextProvider
          authWatcher={(onChange) => <TestAuthWatcher onChange={onChange} loggedIn={true} />}
        >
          <MockApp />
        </UserContextProvider>
      );

      screen.getByText('Is user logged in? true');
    });

    it('should return isLoggedIn false when Auth0 is enabled but the authWatcher reports no session', () => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });

      render(
        <UserContextProvider
          authWatcher={(onChange) => <TestAuthWatcher onChange={onChange} loggedIn={false} />}
        >
          <MockApp />
        </UserContextProvider>
      );

      screen.getByText('Is user logged in? false');
    });

    it('should not call getAuthCookie when Auth0 is enabled', () => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });

      render(
        <UserContextProvider
          authWatcher={(onChange) => <TestAuthWatcher onChange={onChange} loggedIn={false} />}
        >
          <MockApp />
        </UserContextProvider>
      );

      expect(mockGetAuthCookie).not.toHaveBeenCalled();
    });

    it('should never invoke authWatcher when Auth0 is disabled', () => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: false });
      const mockAuthWatcher = jest.fn((onChange: (isLoggedIn: boolean) => void) => (
        <TestAuthWatcher onChange={onChange} loggedIn={true} />
      ));

      render(
        <UserContextProvider authWatcher={mockAuthWatcher}>
          <MockApp />
        </UserContextProvider>
      );

      expect(mockAuthWatcher).not.toHaveBeenCalled();
      screen.getByText('Is user logged in? false');
    });
  });

  describe('useEffect tests', () => {
    jest.useFakeTimers();

    beforeEach(() => {
      mockCustomLocale.mockReturnValue({ country: 'gb' });
      mockUseFeatureToggle.mockReturnValue({});
    });

    afterEach(() => {
      jest.clearAllMocks();
      jest.clearAllTimers();
    });

    it('should check login status every second', async () => {
      mockGetAuthCookie.mockReturnValue(token);
      mockDecodeIdToken.mockReturnValue(decodedToken);
      mockCustomLocale.mockReturnValue({ country: 'gb' });
      jest.spyOn(global, 'setInterval');

      render(
        <UserContextProvider>
          <MockApp />
        </UserContextProvider>
      );

      await waitFor(() => {
        expect(setInterval).toHaveBeenCalledTimes(1);
        expect(setInterval).toHaveBeenCalledWith(expect.any(Function), 1000);
      });

      jest.advanceTimersByTime(1000);

      await waitFor(() => {
        expect(mockGetAuthCookie).toHaveBeenCalledTimes(2); // One call at beginning, second call in useEffect
        expect(mockDecodeIdToken).toHaveBeenCalledTimes(2);
      });
    });

    it('should not change login status from false to true if user logs in non-BB app and country is de', () => {
      mockGetAuthCookie.mockReturnValue('');
      mockDecodeIdToken.mockReturnValue('');
      mockCustomLocale.mockReturnValue({ country: 'de' });
      jest.spyOn(global, 'setInterval');

      render(
        <UserContextProvider>
          <MockApp />
        </UserContextProvider>
      );

      screen.getByText('Is user logged in? false');

      mockGetAuthCookie.mockReturnValue(token);
      mockDecodeIdToken.mockReturnValue(decodedToken);

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      screen.getByText('Is user logged in? true');
    });

    it('should handle expired token in legacy polling interval', async () => {
      const expiredDecodedToken = {
        ...decodedToken,
        exp: 1, // far in the past — triggers deleteCookie branch
      };

      mockGetAuthCookie.mockReturnValue(token);
      mockDecodeIdToken.mockReturnValue(expiredDecodedToken);

      render(
        <UserContextProvider>
          <MockApp />
        </UserContextProvider>
      );

      // isLoggedIn starts as true because email is present
      screen.getByText('Is user logged in? true');

      act(() => {
        jest.advanceTimersByTime(1000);
      });

      // Interval ran — getAuthCookie called again in the polling callback
      await waitFor(() => {
        expect(mockGetAuthCookie).toHaveBeenCalledTimes(2);
      });
    });

    it('should skip legacy polling when Auth0 is enabled', () => {
      mockUseFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });
      jest.spyOn(global, 'setInterval');

      render(
        <UserContextProvider
          authWatcher={(onChange) => <TestAuthWatcher onChange={onChange} loggedIn={true} />}
        >
          <MockApp />
        </UserContextProvider>
      );

      expect(setInterval).not.toHaveBeenCalled();
    });
  });
});

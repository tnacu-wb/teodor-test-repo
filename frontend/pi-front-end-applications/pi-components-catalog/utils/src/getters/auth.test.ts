import jwtDecode from 'jwt-decode';

import {
  decodeIdToken,
  getAccessTokenCookie,
  getAuthCookie,
  getLoggedInUserInfo,
  logout,
} from './auth';
import { getSecureTwoURL } from './getters';

const mockGetCookie = jest.fn();

jest.mock('../helpers/cookies', () => ({
  ...jest.requireActual('../helpers/cookies'),
  getCookie: () => mockGetCookie(),
}));

jest.mock('jwt-decode', () => jest.fn());

jest.mock('./getters', () => ({
  getSecureTwoURL: jest.fn(),
}));

const token =
  'eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InAzblU5b3M0RTBubGRMVF9ROHBnbSJ9.eyJodHRwczovL2NjdWkub3BlcmEud2hpdGJyZWFkLmRpZ2l0YWwvcm9sZSI6W10sIndiX2FjY291bnRfbG9jYWxlIjoiZW4iLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2NvbXBhbnlBY2NvdW50SWQiOiJDT01QXzc4NDYwNjNlLWFjZDEtNDkzMS04YzA3LTFjMzNkZGMyYTQyZiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZW1wbG95ZWVBY2NvdW50SWQiOiJFTVBMX2I0NWZkNzU1LWFhY2UtNGU5OS05MmIxLWRmN2NkZDUwOWU1MiIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vZ2xvYmFsQ29tcGFueUlkIjoxMzYxLCJodHRwczovL3ByZW1pZXJpbm4uY29tL2VtYWlsIjoidHJhdmVsaW5nLmJnbEBtYWlsaW5hdG9yLmNvbSIsImh0dHBzOi8vcHJlbWllcmlubi5jb20vb3BlcmFDb21wYW55SWQiOiIyNTY5NjE2Iiwibmlja25hbWUiOiJ0cmF2ZWxpbmcuYmdsIiwicHJvZmlsZSI6eyJhY2Nlc3NMZXZlbCI6IlNVUEVSIiwiY29tcGFueUlkIjoiMzUwODYiLCJlbXBsb3llZUlkIjoiMSIsImlzQnVzaW5lc3MiOnRydWUsInNlc3Npb25JZCI6IjhSdHIxUktlaEFPb21rU2IifSwibmFtZSI6InRyYXZlbGluZy5iZ2xAbWFpbGluYXRvci5jb20iLCJwaWN0dXJlIjoiaHR0cHM6Ly9zLmdyYXZhdGFyLmNvbS9hdmF0YXIvYzYyNjU3YzE4NmFiNWIzMjNjOWFhZWJkYzJiZjExY2M_cz00ODAmcj1wZyZkPWh0dHBzJTNBJTJGJTJGY2RuLmF1dGgwLmNvbSUyRmF2YXRhcnMlMkZ0ci5wbmciLCJ1cGRhdGVkX2F0IjoiMjAyMy0wMy0wN1QxNDo0MTo0Ni4wMDFaIiwiaXNzIjoiaHR0cHM6Ly9hdXRoMC5zYW5kYm94LndoaXRicmVhZC5kaWdpdGFsLyIsImF1ZCI6IjhLT0NKa3o3MXBXRFlhamFNQUxhZWJKdVczQ0Nxb3ZzIiwiaWF0IjoxNjc4MjAwMTA3LCJleHAiOjE2NzgyMDE2MDcsInN1YiI6ImF1dGgwfDYzZWY0MzlkZDc0ZTZmOTZkYjAxZmEzYyIsImF0X2hhc2giOiJJalRkVEJhd2lHRmItRXVIUFNZVk93Iiwibm9uY2UiOiJaSGd3Sms2NVhkZ0g4QUZNV2pFeEpLZFNoZlRYMU5UeSJ9.E11UiJpwGnx5aj4WQvfdsjj5h2_h9MS-kiXMffYaPqk5x9QCegySS6kK0S_Z7rOKJG2QHtKH-wYyIyz-5moDA2u1iT4PLHtdwD4XoXtB-H8aKfvlpuMlCgeH9yoFrlk9NOJFcYiN8oNjL8hUymX4JOvZHOxwMbyJ-HNOcPtuWFd-HZqTrWlNXBu5XH459uDOhZGs1Ll425gbS8tP-Wa7F6liJG7046y4FVELwXZyoksKWFxmCxHrW8gEw3BCh4Ht4F_j2ORPbs0idt2VuNhp6NVuysCh0eks7CeyLLW951KCp40OW3P5P3KoK89T9ty0zpHXHlvCXKDDCmil962d7g';

const decodedToken = {
  at_hash: 'IjTdTBawiGFb-EuHPSYVOw',
  aud: '8KOCJkz71pWDYajaMALaebJuW3CCqovs',
  exp: 1678201607,
  'https://ccui.opera.whitbread.digital/role': [],
  'https://premierinn.com/companyAccountId': 'COMP_7846063e-acd1-4931-8c07-1c33ddc2a42f',
  'https://premierinn.com/email': 'traveling.bgl@mailinator.com',
  'https://premierinn.com/employeeAccountId': 'EMPL_b45fd755-aace-4e99-92b1-df7cdd509e52',
  'https://premierinn.com/globalCompanyId': 1361,
  'https://premierinn.com/operaCompanyId': '2569616',
  iat: 1678200107,
  iss: 'https://auth0.sandbox.whitbread.digital/',
  name: 'traveling.bgl@mailinator.com',
  nickname: 'traveling.bgl',
  nonce: 'ZHgwJk65XdgH8AFMWjExJKdShfTX1NTy',
  picture:
    'https://s.gravatar.com/avatar/c62657c186ab5b323c9aaebdc2bf11cc?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Ftr.png',
  profile: {
    accessLevel: 'SUPER',
    companyId: '35086',
    employeeId: '1',
    isBusiness: true,
    sessionId: '8Rtr1RKehAOomkSb',
  },
  sub: 'auth0|63ef439dd74e6f96db01fa3c',
  updated_at: '2023-03-07T14:41:46.001Z',
  wb_account_locale: 'en',
};

describe('auth methods', () => {
  beforeEach(() => {
    (jwtDecode as jest.Mock).mockImplementation(() => decodedToken);
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('getAuthCookie Method', () => {
    beforeEach(() => {
      mockGetCookie.mockImplementation(() => token);
    });

    it('should return the id token cookie', () => {
      const tokenFromAuthCookie = getAuthCookie();
      expect(tokenFromAuthCookie).toEqual(token);
    });

    it('should return empty string if no id token cookie is set', () => {
      mockGetCookie.mockImplementation(() => '');
      const tokenFromAuthCookie = getAuthCookie();
      expect(tokenFromAuthCookie).toEqual('');
    });
  });

  describe('getAccessTokenCookie Method', () => {
    beforeEach(() => {
      mockGetCookie.mockImplementation(() => token);
    });

    it('should return the access token cookie', () => {
      const tokenFromAuthCookie = getAccessTokenCookie();
      expect(tokenFromAuthCookie).toEqual(token);
    });

    it('should return empty string if no access token cookie is set', () => {
      mockGetCookie.mockImplementation(() => '');
      const tokenFromAuthCookie = getAccessTokenCookie();
      expect(tokenFromAuthCookie).toEqual('');
    });
  });

  describe('getLoggedInUserInfo Method', () => {
    it('should return the correct values after token is decoded', () => {
      const { accessLevel, companyId, employeeId, isBusiness, sessionId } =
        getLoggedInUserInfo(token);
      expect(accessLevel).toEqual('SUPER');
      expect(companyId).toEqual('35086');
      expect(employeeId).toEqual('1');
      expect(isBusiness).toEqual(true);
      expect(sessionId).toEqual('8Rtr1RKehAOomkSb');
    });

    it('should return the default values if the token is an empty string', () => {
      const { accessLevel, companyId, employeeId, isBusiness, sessionId } = getLoggedInUserInfo('');
      expect(accessLevel).toEqual('');
      expect(companyId).toEqual('');
      expect(employeeId).toEqual('');
      expect(isBusiness).toEqual('');
      expect(sessionId).toEqual('');
    });

    it('should log any error received while decoding the user info from the token', () => {
      (jwtDecode as jest.Mock).mockImplementationOnce(() => {
        throw new Error('decode error');
      });
      const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation((e) => e);

      getLoggedInUserInfo(token);
      expect(consoleLogSpy).toHaveBeenCalledWith(new Error('decode error'));
    });
  });

  describe('decodeIdToken Method', () => {
    it('should return decoded token data for a given token', () => {
      const decodedIdToken = decodeIdToken(token);
      expect(decodedIdToken).toEqual(decodedToken);
    });

    it('should return default decoded token data if given token is an empty string', () => {
      const decodedToken = decodeIdToken('');
      const expectedDecodedToken = { email: '', name: '', exp: null, profile: { employeeId: '' } };
      expect(decodedToken).toEqual(expectedDecodedToken);
    });

    it('should log any error received while decoding the token', () => {
      (jwtDecode as jest.Mock).mockImplementationOnce(() => {
        throw new Error('decode error');
      });
      const consoleLogSpy = jest.spyOn(console, 'log').mockImplementation((e) => e);

      decodeIdToken(token);
      expect(consoleLogSpy).toHaveBeenCalledWith(new Error('decode error'));
    });
  });

  describe('logout Method', () => {
    let getSecureTwoURLMock;

    beforeEach(() => {
      getSecureTwoURLMock = getSecureTwoURL as jest.Mock;
      getSecureTwoURLMock.mockReturnValue('https://secure2.com');
    });

    afterEach(() => {
      jest.clearAllMocks();
      document.body.innerHTML = '';
    });

    it('should call getSecureTwoURL and send message if authIframe and contentWindow are defined', () => {
      const iframe = document.createElement('iframe');
      iframe.id = 'authIframe';
      document.body.appendChild(iframe);

      const postMessageMock = jest.fn();
      Object.defineProperty(iframe, 'contentWindow', {
        value: {
          postMessage: postMessageMock,
        },
      });

      logout();

      expect(getSecureTwoURLMock).toHaveBeenCalled();
      expect(postMessageMock).toHaveBeenCalledWith(
        JSON.stringify({ action: 'logout' }),
        'https://secure2.com'
      );
    });

    it('should not call getSecureTwoURL if authIframe is not defined', () => {
      logout();

      expect(getSecureTwoURLMock).not.toHaveBeenCalled();
    });
  });
});

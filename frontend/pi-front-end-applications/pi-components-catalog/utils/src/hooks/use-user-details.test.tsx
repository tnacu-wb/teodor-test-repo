import { BUSINESS_BOOKER_USER_ROLES, AccessLevel } from '@whitbread-eos/api';

import useUserDetails from './use-user-details';

jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useRestQueryRequest: () => mockUseRestQueryRequest,
}));

jest.mock('../getters/auth', () => ({
  ...jest.requireActual('../getters/auth'),
  getAuthCookie: () => 'the auth cookie',
  decodeIdToken: () => {
    return {
      email: 'traveling.bgl@mailinator.com',
    };
  },
}));

jest.mock('./useAuthToken', () => ({
  useAuthToken: () => ({ token: 'the auth cookie', isAuth0Enabled: false, isLoading: false }),
}));

jest.mock('./useAuth0User', () => ({
  useAuth0User: () => ({ user: null, loading: false, error: null, refetch: jest.fn() }),
}));

const mockUseRestQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    sessionId: '5ib3z8UxGPQRH0Ox',
    contactDetail: {
      title: 'Miss',
      firstName: 'Boatyness',
      lastName: 'McBoatss',
      email: 'traveling.bgl@mailinator.com',
      telephone: '5555555555',
      mobile: '',
      address: {
        line1: '3 Anthony Road',
        line2: 'Newquay',
        line3: '',
        line4: 'Cornwall',
        line5: '',
        postCode: 'TR4 5AS',
        countryCode: 'GB',
      },
    },
    paymentPreference: { electronicInvoiceRequired: false, paymentCard: {} },
    bookingPreference: { preselectWifi: false },
    companyId: '35086',
    guestHistoryNumber: 'G55969520',
    business: {
      accessLevel: 'SUPER',
      purchaseOrderAnswer: '',
      customerReferenceAnswer: '',
      centralCard: '',
      myPILink: null,
      dismissMPILink: false,
      miSetupRequired: false,
      awaitingApproval: 0,
      employeeId: '1',
      tethered: false,
    },
    guestHistoryCreation: '2021-02-11',
    totalStays: 0,
  },
};

describe('useUserDetails', () => {
  it('should not be enabled if user is not logged in', () => {
    const expectedUserRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
    const userDetails = useUserDetails(true, false);
    expect(userDetails.business.accessLevel).toBe(expectedUserRole);
  });

  it('should return business booker user details with role', () => {
    const expectedUserRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
    const userDetails = useUserDetails(true, true);
    expect(userDetails.business.accessLevel).toBe(expectedUserRole);
  });

  it('should return innbusinees user details', () => {
    const expectedUserRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
    const userDetails = useUserDetails(true, true, {
      business: { accessLevel: AccessLevel.Super },
    });
    expect(userDetails.business.accessLevel).toBe(expectedUserRole);
  });
});

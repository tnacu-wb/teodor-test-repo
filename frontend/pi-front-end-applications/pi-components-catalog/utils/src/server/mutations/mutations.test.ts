import {
  BUSINESS_BOOKER_USER_ROLES,
  LOCALES,
  requestErrors,
  requestStatus,
  bookingAllowancesMocks,
  UpdateAppContactDetailsCriteria,
  UpdateAppCompanyDetailsCriteria,
  Scheme,
  ResetPasswordRequest,
  ForgottenPasswordRequest,
  ValidateResetKeyRequest,
  ValidateResetKeyResponse,
  CustomerAccountDetails,
} from '@whitbread-eos/api';

import * as edge from '../edge';
import {
  bulkUploadEmployees,
  businessTetherLogin,
  businessTether,
  sendActivationEmail,
  updateEmployeeDetails,
  addNewEmployee,
  updateContactPreferences,
  updateProfileDetails,
  updateCompanyDetails,
  updateBusinessQuestions,
  resetMemorableWord,
  updateCompanyUserQuestion,
  saveCard,
  createCompanyUserQuestion,
  deleteCompanyUserQuestion,
  addCardPIBAMutation,
  initializePayApplication,
  updateBookingAllowances,
  activatePibaCard,
  deleteApplication,
  updateAppContactDetails,
  resendActivationEmail,
  InnBRegistrationStepOne,
  updateMarketingPreferences,
  registrationStepTwo,
  authenticateRegistration,
  updateAppCompanyDetails,
  updateBookingAlerts,
  updateCDHCard,
  deleteCDHCard,
  shareApplication,
  removeParticipant,
  approveRejectEmployee,
  submitRegistration,
  forgotPassword,
  addPayAppCard,
  deletePayAppCard,
  resetPassword,
  directDebit,
  validateResetKey,
  updateResumeUrl,
  submitApplication,
  viewCustomerInvoices,
  updatePIBACardMutation,
  cancelAndReplacePIBACardMutation,
  replaceCardMutation,
  resendCodeMutation,
  downloadBookingInvoice,
} from './mutations';

jest.mock('@whitbread-eos/api', () => ({
  ...jest.requireActual('@whitbread-eos/api'),
  updateProfileDetailsQuery: () => 'mutation updateProfileDetails',
}));

const mockFetchResponse = {
  data: {},
} as any;

const mockOkStatus = { value: true };

const mockSuperRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
const TEST_TOKEN = 'test-auth-token-placeholder';
const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    accessLevel: mockSuperRole,
    tethered: false,
  },
};

const createMockResponse = (jsonFn: () => Promise<any>, ok = true): Response => {
  return {
    json: jsonFn,
    ok,
    headers: new Headers(),
    redirected: false,
    status: ok ? 200 : 400,
    statusText: ok ? 'OK' : 'Bad Request',
    type: 'basic',
    url: 'https://example.com',
    clone: function () {
      return this;
    },
    body: null,
    bodyUsed: false,
    arrayBuffer: () => Promise.resolve(new ArrayBuffer(0)),
    blob: () => Promise.resolve(new Blob()),
    formData: () => Promise.resolve(new FormData()),
    text: () => Promise.resolve(''),
  } as Response;
};

global.fetch = jest.fn(() =>
  Promise.resolve(createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value))
);

describe('Server mutations', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should call addPayAppCard', async () => {
    mockFetchResponse.data = null;
    const result = await addPayAppCard('123', '1', 'EN', {}, mockToken as any);
    expect(result).toEqual({ errors: undefined, status: 'fail' });

    mockFetchResponse.data = { addApplicationCard: { cardGuid: '123' } };
    const successResult = await addPayAppCard('123', '1', 'EN', {}, mockToken as any);
    expect(successResult).toEqual({ status: 'success', cardGuid: '123' });
  });

  it('should call deletePayAppCard', async () => {
    mockFetchResponse.data = null;
    const result = await deletePayAppCard('123', '1', '123', mockToken as any);
    expect(result).toEqual({ errors: undefined, status: 'fail' });

    mockFetchResponse.data = { deleteApplicationCard: '' };
    const successResult = await deletePayAppCard('123', '1', '123', mockToken as any);
    expect(successResult).toEqual({ status: 'success' });
  });

  it('should call activatePibaCard with error', async () => {
    mockFetchResponse.data = null;
    const result = await activatePibaCard('123', '1', 'EN', mockToken as any);
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call activatePibaCard with success', async () => {
    mockFetchResponse.data = { activateInnBPIBACard: 'success' };
    const result = await activatePibaCard('123', '1', 'EN', mockToken as any);
    expect(result).toEqual({ status: 'success' });
  });

  it('should call addNewEmployee with error', async () => {
    mockFetchResponse.data = null;
    const result = await addNewEmployee('1', 'EN', { firstName: 'TEST' }, mockToken as any);
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call addNewEmployee with success', async () => {
    mockFetchResponse.data = {
      addEmployee:
        'http://opera-api.dev.opera.whitbread.digital:443/companies/admin/COMP_80eade56-67a5-4eb9-ac07-2e6fe5c24739/employees/EMPL_7b7cf83f-bf0f-4e6d-bcb7-b342f4ad6669',
    };
    const result = await addNewEmployee('1', 'EN', { firstName: 'TEST' }, mockToken as any);
    expect(result).toEqual({
      status: 'success',
      employeeId: 'EMPL_7b7cf83f-bf0f-4e6d-bcb7-b342f4ad6669',
    });
  });

  it('should call updateEmployeeDetails', async () => {
    mockFetchResponse.data = null;
    const result = await updateEmployeeDetails(
      '1',
      '1',
      'EN',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateEmployeeDetails and fail', async () => {
    mockFetchResponse.data = { updateEmployee: null };
    mockFetchResponse.errors = [{ message: '{"code":"1"}' }];
    const result = await updateEmployeeDetails(
      '1',
      '1',
      'EN',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });

    mockFetchResponse.errors = [
      {
        message: '{"code":"2518"}',
      },
    ];

    const lastTMResult = await updateEmployeeDetails(
      '1',
      '1',
      'EN',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(lastTMResult).toEqual({ error: '2518', status: 'fail' });
  });

  it('should call updateEmployeeDetails with mock valid data', async () => {
    mockFetchResponse.data = { updateEmployee: '' };
    const result = await updateEmployeeDetails(
      '1',
      '1',
      'EN',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });

  it('should call businessTetherLogin', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      businessTetherLogin: 'abc',
    };
    const result = await businessTetherLogin('abc', 'xyz', 'GB' as Scheme);
    expect(result).toEqual('abc');
  });

  it('should call directDebit', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      directDebit: 'abc',
    };
    const result = await directDebit('abc', '', '', '', '');
    expect(result.data).toBe('abc');
  });

  it('should call directDebit and fail', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      directDebit: undefined,
    };
    const result = await directDebit('abc', '', '', '', '');
    expect(result.status).toBe(requestStatus.fail);
  });

  it('should call initializePayApplication', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      initializeApplication: 'abc',
    };
    const result = await initializePayApplication('abc', 'xyz', 'GB' as Scheme);
    expect(result).toEqual('abc');
  });

  it('should call sendActivationEmail', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      sendActivationEmail: 'abc',
    };
    const result = await sendActivationEmail('abc', 'xyz', LOCALES.EN, 'test@test.com', '1');
    expect(result).toEqual('abc');
  });

  it('should call bulkUploadEmployees', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {};
    const result = await bulkUploadEmployees('', '', new File([], 'abc'), LOCALES.EN);
    expect(result?.ok).toEqual(true);
  });

  it('should call bulkUploadEmployees and return false if it fails', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.data = {};
    const result = await bulkUploadEmployees('', '', new File([], 'abc'), LOCALES.DE);
    expect(result?.ok).toEqual(false);
  });

  it('should call updateAppContactDetails', async () => {
    mockOkStatus.value = true;
    const result = await updateAppContactDetails('abc', {} as UpdateAppContactDetailsCriteria);
    expect(result).toEqual({ errors: undefined, status: 'fail' });
  });

  it('should call updateAppContactDetails with non-null result', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      updateAppContactDetails: {},
    };
    const result = await updateAppContactDetails('abc', {} as UpdateAppContactDetailsCriteria);
    expect(result).toEqual({ status: 'success' });
  });
});

describe('updateBookingAlerts', () => {
  const mockCompanyId = 'COMP_123';
  const mockBookingAlerts = {
    unitedKingdom: '10',
    greaterLondon: '20',
    germanyIreland: '30',
  };
  const mockTokenString = TEST_TOKEN;

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    mockOkStatus.value = true;
  });

  it('should successfully update booking alerts', async () => {
    mockFetchResponse.data = { updateBookingAlerts: '' };

    const result = await updateBookingAlerts(mockCompanyId, mockBookingAlerts, mockTokenString);

    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should handle GraphQL errors when updating booking alerts', async () => {
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    mockFetchResponse.data = null;

    const result = await updateBookingAlerts(mockCompanyId, mockBookingAlerts, mockTokenString);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle unexpected response format when updating booking alerts', async () => {
    mockFetchResponse.data = { updateBookingAlerts: 'some_unexpected_value' };

    const result = await updateBookingAlerts(mockCompanyId, mockBookingAlerts, mockTokenString);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle null data in response when updating booking alerts', async () => {
    mockFetchResponse.data = null;

    const result = await updateBookingAlerts(mockCompanyId, mockBookingAlerts, mockTokenString);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });
});

it('should call updateAppCompanyDetails', async () => {
  mockOkStatus.value = true;
  const result = await updateAppCompanyDetails('abc', {} as UpdateAppCompanyDetailsCriteria);
  expect(result).toEqual({ errors: undefined, status: 'fail' });
});

it('should call updateAppCompanyDetails with non-null result', async () => {
  mockOkStatus.value = true;
  mockFetchResponse.data = {
    updateAppCompanyDetails: {},
  };
  const result = await updateAppCompanyDetails('abc', {} as UpdateAppCompanyDetailsCriteria);
  expect(result).toEqual({ status: 'success' });
});

it('should call updateResumeUrl', async () => {
  mockOkStatus.value = true;
  const result = await updateResumeUrl('abc', '', '', '');
  expect(result).toEqual(undefined);
});

it('should call viewCustomerInvoices', async () => {
  const mockAccount: CustomerAccountDetails = {
    accountName: 'Test Account',
    accountNumber: '123456',
    tetheredGuid: 'tethered-guid',
    schemeCustomerId: 123,
    scheme: 'DE' as Scheme,
  };
  mockOkStatus.value = true;
  const result = await viewCustomerInvoices('123', mockAccount, '2024-01-01', '2025-12-31', 1, 15);
  expect(result).toEqual(undefined);
});

describe('updateContactPreferences', () => {
  const mockToken = TEST_TOKEN;
  const mockPreferences = {
    optIn: true,
    secondPartyOptIn: false,
    thirdPartyVendorsOptIn: true,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    mockOkStatus.value = true;
    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should successfully update contact preferences for EN locale', async () => {
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'en-gb', mockPreferences);

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        cache: 'no-cache',
        body: expect.stringContaining('mutation updateEmailPreferences'),
      })
    );

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables).toEqual({
      request: {
        brandCodes: ['PINN'],
        customer: {
          language: 'en',
          countryOfResidence: 'GB',
          firstName: '',
          lastName: '',
          title: '',
          userId: '',
        },
        doubleOptIn: false,
        optIn: true,
        secondPartyOptIn: false,
        thirdPartyVendorsOptIn: true,
        sourceDetails: {
          channel: 'BB',
          journey: 'PERMISSIONCENTRE',
          locale: 'UK',
        },
      },
    });

    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should successfully update contact preferences for DE locale', async () => {
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'de-de', mockPreferences);

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables).toEqual({
      request: {
        brandCodes: ['PINN'],
        customer: {
          language: 'de',
          countryOfResidence: 'DE',
          firstName: '',
          lastName: '',
          title: '',
          userId: '',
        },
        doubleOptIn: true,
        optIn: true,
        secondPartyOptIn: false,
        thirdPartyVendorsOptIn: true,
        sourceDetails: {
          channel: 'BB',
          journey: 'PERMISSIONCENTRE',
          locale: 'DE',
        },
      },
    });

    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should handle GraphQL errors', async () => {
    mockFetchResponse.data = null;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    mockOkStatus.value = false;

    const result = await updateContactPreferences(mockToken, 'en', mockPreferences);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle unexpected response format', async () => {
    mockFetchResponse.data = { updateEmailPreferences: null };

    const result = await updateContactPreferences(mockToken, 'en', mockPreferences);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should use default values for missing preferences', async () => {
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'en-gb', {});

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables).toEqual({
      request: {
        brandCodes: ['PINN'],
        customer: {
          language: 'en',
          countryOfResidence: 'GB',
          firstName: '',
          lastName: '',
          title: '',
          userId: '',
        },
        doubleOptIn: false,
        optIn: false,
        secondPartyOptIn: false,
        thirdPartyVendorsOptIn: false,
        sourceDetails: {
          channel: 'BB',
          journey: 'PERMISSIONCENTRE',
          locale: 'UK',
        },
      },
    });

    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));

    const result = await updateContactPreferences(mockToken, 'en', mockPreferences);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle non-200 response status', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'en', mockPreferences);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle JSON parse errors', async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve(createMockResponse(() => Promise.reject(new Error('Invalid JSON')), true))
    );

    const result = await updateContactPreferences(mockToken, 'en', mockPreferences);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle empty response data', async () => {
    mockFetchResponse.data = {};

    const result = await updateContactPreferences(mockToken, 'en', mockPreferences);

    expect(result).toEqual({
      status: requestStatus.fail,
      error: requestErrors.generic,
    });
  });

  it('should handle undefined preferences', async () => {
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'en', {} as any);

    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should handle null preferences', async () => {
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'en', {} as any);

    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should handle partial preferences', async () => {
    mockFetchResponse.data = { updateEmailPreferences: '' };

    const result = await updateContactPreferences(mockToken, 'en', { optIn: true });

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables.request).toMatchObject({
      optIn: true,
      secondPartyOptIn: false,
      thirdPartyVendorsOptIn: false,
    });

    expect(result).toEqual({ status: requestStatus.success });
  });
});

describe('updateProfileDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call updateProfileDetails', async () => {
    mockFetchResponse.data = null;
    const result = await updateProfileDetails(
      'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateProfileDetails and fail', async () => {
    mockFetchResponse.data = { updateProfile: null };
    mockFetchResponse.errors = [{ message: '{"code":"1"}' }];
    const result = await updateProfileDetails(
      'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });

    mockFetchResponse.errors = [{ message: '{"code":"037"}' }];
  });

  it('should return profileErrorCodes for contact detail errors', async () => {
    mockFetchResponse.data = { updateProfileDetails: null };
    mockFetchResponse.errors = [
      {
        message: JSON.stringify({
          code: '7102',
          details: [
            'WorldLine errors when updating contact details for tetheredUserGuid abc: 2, 4, 10',
          ],
        }),
      },
    ];
    const result = await updateProfileDetails(
      'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({
      error: requestErrors.generic,
      profileErrorCodes: [2, 4, 10],
      status: requestStatus.fail,
    });
  });

  it('should call updateProfileDetails with mock valid data', async () => {
    mockFetchResponse.data = {
      updateProfileDetails: {
        success: true,
        customerId: 'EMPL_ee6a91a7-9732-4d91-83e6-9efb48f57ca9',
      },
    };
    const result = await updateProfileDetails(
      'EMPL_9a6329da-6c4b-4c9b-9ab1-db0e03a8b220',
      { firstName: 'TEST' },
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });
});
describe('updateCompanyDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call updateCompanyDetails', async () => {
    mockFetchResponse.data = null;
    const result = await updateCompanyDetails(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      {
        companyName: 'test_company',
        alternateCompanyName: 'test_company',
        companyAddress: {
          addressLine1: 'address1',
          addressLine2: 'address2',
          addressLine3: 'address3',
          addressLine4: 'address4',
          addressLine5: 'address5',
        },
        mainContact: {
          employeeId: 'EMPL_123141',
          position: 'Owner',
          firstName: 'Test',
          lastName: 'Test',
          emailAddress: 'test@yopmail.com',
          phoneNumber: '+4477777321321',
          mobileNumber: '',
        },
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateCompanyDetails and fail', async () => {
    mockFetchResponse.data = { updateCompanyDetails: null };
    mockFetchResponse.errors = [{ errorInfo: { code: '1' } }];
    const result = await updateCompanyDetails(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      {
        companyName: 'test_company',
        alternateCompanyName: 'test_company',
        companyAddress: {
          addressLine1: 'address1',
          addressLine2: 'address2',
          addressLine3: 'address3',
          addressLine4: 'address4',
          addressLine5: 'address5',
        },
        mainContact: {
          employeeId: 'EMPL_123141',
          position: 'Owner',
          firstName: 'Test',
          lastName: 'Test',
          emailAddress: 'test@yopmail.com',
          phoneNumber: '+4477777321321',
          mobileNumber: '',
        },
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateCompanyDetails with mock valid data', async () => {
    mockFetchResponse.data = {
      updateCompanyDetails: '',
    };
    const result = await updateCompanyDetails(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      {
        companyName: 'test_company',
        alternateCompanyName: 'test_company',
        companyAddress: {
          addressLine1: 'address1',
          addressLine2: 'address2',
          addressLine3: 'address3',
          addressLine4: 'address4',
          addressLine5: 'address5',
        },
        mainContact: {
          employeeId: 'EMPL_123141',
          position: 'Owner',
          firstName: 'Test',
          lastName: 'Test',
          emailAddress: 'test@yopmail.com',
          phoneNumber: '+4477777321321',
          mobileNumber: '',
        },
      },
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });
});
describe('updateBusinessQuestions', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call updateBusinessQuestions', async () => {
    mockFetchResponse.data = null;
    const result = await updateBusinessQuestions(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'purchase_order_number',
      {
        questionId: 'purchase_order_number',
        label: 'Purchase order number 22222',
        mandatory: false,
        managementHeader: 'Purchase order number',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateBusinessQuestions and fail', async () => {
    mockFetchResponse.data = { updateBusinessQuestions: null };
    mockFetchResponse.errors = [{ errorInfo: { code: '1' } }];
    const result = await updateBusinessQuestions(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'asd',
      {
        questionId: null,
        label: 'Purchase order number 22222',
        mandatory: false,
        managementHeader: 'Purchase order number',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateBusinessQuestions with mock valid data', async () => {
    mockFetchResponse.data = {
      updateBusinessQuestions: '',
    };
    const result = await updateBusinessQuestions(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      '1',
      {
        questionId: null,
        label: 'Purchase order number 22222',
        mandatory: false,
        managementHeader: 'Purchase order number',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });
});
describe('updateCompanyUserQuestion', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call updateCompanyUserQuestion', async () => {
    mockFetchResponse.data = null;
    const result = await updateCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'COQU_123',
      {
        questionId: 'COQU_123',
        label: 'Test question',
        mandatory: false,
        managementHeader: 'Test question',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateCompanyUserQuestion and fail', async () => {
    mockFetchResponse.data = { updateCompanyUserQuestion: null };
    mockFetchResponse.errors = [{ errorInfo: { code: '1' } }];
    const result = await updateCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'COQU_123',
      {
        questionId: null,
        label: 'Test question',
        mandatory: false,
        managementHeader: 'Test question',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateCompanyUserQuestion with mock valid data', async () => {
    mockFetchResponse.data = {
      updateCompanyUserQuestion: '',
    };
    const result = await updateCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'COQU_123',
      {
        questionId: 'COQU_123',
        label: 'Test question',
        mandatory: false,
        managementHeader: 'Test question',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });
});
describe('updateBookingAllowances', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call updateBookingAllowances with mock valid data', async () => {
    mockFetchResponse.data = {
      updateBookingAllowances: '',
    };
    const result = await updateBookingAllowances(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      bookingAllowancesMocks,
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });

  it('should call updateBookingAllowances', async () => {
    mockFetchResponse.data = null;
    const result = await updateBookingAllowances(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      bookingAllowancesMocks,
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call updateBookingAllowances and fail', async () => {
    mockFetchResponse.data = { updateBookingAllowances: null };
    mockFetchResponse.errors = [{ errorInfo: { code: '1' } }];
    const result = await updateBookingAllowances(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      bookingAllowancesMocks,
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });
});

describe('createCompanyUserQuestion', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call createCompanyUserQuestion', async () => {
    mockFetchResponse.data = null;
    const result = await createCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      {
        questionId: 'COQU_123',
        label: 'Test question',
        mandatory: false,
        managementHeader: 'Test question',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call createCompanyUserQuestion and fail', async () => {
    mockFetchResponse.data = { createCompanyUserQuestion: null };
    mockFetchResponse.errors = [{ errorInfo: { code: '1' } }];
    const result = await createCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      {
        questionId: null,
        label: 'Test question',
        mandatory: false,
        managementHeader: 'Test question',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call createCompanyUserQuestion with mock valid data', async () => {
    mockFetchResponse.data = {
      createCompanyUserQuestion: '',
    };
    const result = await createCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      {
        questionId: 'COQU_123',
        label: 'Test question',
        mandatory: false,
        managementHeader: 'Test question',
        active: true,
        location: 'R',
        managementInformationAnswer: { answerType: null, answers: null },
        type: null,
        positionId: 0,
      },
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });
});
describe('deleteCompanyUserQuestion', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });
  it('should call deleteCompanyUserQuestion', async () => {
    mockFetchResponse.data = null;
    const result = await deleteCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'COQU_123',
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call deleteCompanyUserQuestion and fail', async () => {
    mockFetchResponse.data = { deleteCustomQuestion: null };
    mockFetchResponse.errors = [{ errorInfo: { code: '1' } }];
    const result = await deleteCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      null,
      mockToken as any
    );
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should call deleteCompanyUserQuestion with mock valid data', async () => {
    mockFetchResponse.data = {
      deleteCustomQuestion: '',
    };
    const result = await deleteCompanyUserQuestion(
      'COMP_-6c4b-4c9b-9ab1-db0e03a8b220',
      'COQU_123',
      mockToken as any
    );
    expect(result).toEqual({ status: 'success' });
  });
});

describe('resetMemorableWord', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should call resetMemorableWord', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      resetMemorableWord: {
        resultCode: 'OK',
      },
    };
    const result = await resetMemorableWord('abc', 'Whitbread10', 'xyz', 'GB');
    expect(result).toEqual({ status: 'success' });
  });

  it('should call resetMemorableWord and get error', async () => {
    const result = await resetMemorableWord('abc', 'Whitbread10', 'xyz', 'GB');
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });
});

jest.mock('../../utils/getLoggedInUserInfo', () =>
  jest.fn().mockReturnValue({ cdhCompanyId: 'COMP_123' })
);

describe('saveCard', () => {
  const mockToken = TEST_TOKEN;
  const mockEmployeeId = 'emp-123';
  const mockProfileDetails = {
    contactDetail: {
      address: {
        line1: '123 Test St',
        line2: 'Suite 100',
        countryCode: 'GB',
        postCode: '12345',
        companyName: 'Test Company',
        type: 'BUSINESS',
      },
    },
  };
  const mockOnSuccess = jest.fn();
  const mockOnError = jest.fn();
  const mockT = jest.fn((key) => key);

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    mockOkStatus.value = true;

    Object.defineProperty(window, 'location', {
      value: {
        pathname: '/en/some-path',
        origin: 'https://test.com',
      },
      writable: true,
    });

    jest.spyOn(edge, 'getRandomTracingId').mockReturnValue('mock-request-id');
  });

  it('should successfully save a card with company ID', async () => {
    mockFetchResponse.data = {
      saveCard: {
        paymentRedirect: 'redirect-url',
        providerUrl: 'provider-url',
        template: 'template',
        sessionId: 'session-123',
      },
    };

    const result = await saveCard({
      token: mockToken,
      employeeId: mockEmployeeId,
      profileDetails: mockProfileDetails,
      onSuccess: mockOnSuccess,
      onError: mockOnError,
      t: mockT,
      language: 'en',
      isPreferenceCard: true,
    });

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        body: expect.stringContaining('saveCard'),
        headers: expect.objectContaining({
          Authorization: `Bearer ${mockToken}`,
        }),
      })
    );

    expect(result).toEqual(mockFetchResponse.data.saveCard);
    expect(mockOnSuccess).toHaveBeenCalledWith(mockFetchResponse.data.saveCard);
    expect(mockOnError).not.toHaveBeenCalled();
  });

  it('should handle PIBA card type correctly', async () => {
    mockFetchResponse.data = {
      saveCard: {
        paymentRedirect: 'redirect-url',
        providerUrl: 'provider-url',
        template: 'template',
        sessionId: 'session-123',
      },
    };

    await saveCard({
      token: mockToken,
      employeeId: mockEmployeeId,
      profileDetails: mockProfileDetails,
      onSuccess: mockOnSuccess,
      onError: mockOnError,
      t: mockT,
      isPiba: true,
      language: 'en',
      isPreferenceCard: true,
    });

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        body: expect.stringContaining('"cardType":"PIBA"'),
        headers: expect.objectContaining({
          Authorization: `Bearer ${mockToken}`,
        }),
      })
    );
  });

  it('should handle German language path correctly', async () => {
    Object.defineProperty(window, 'location', {
      value: {
        pathname: '/de/some-path',
        origin: 'https://test.com',
      },
      writable: true,
    });

    mockFetchResponse.data = {
      saveCard: {
        paymentRedirect: 'redirect-url',
        providerUrl: 'provider-url',
        template: 'template',
        sessionId: 'session-123',
      },
    };

    await saveCard({
      token: mockToken,
      employeeId: mockEmployeeId,
      profileDetails: mockProfileDetails,
      onSuccess: mockOnSuccess,
      onError: mockOnError,
      t: mockT,
      language: 'de',
      isPreferenceCard: true,
    });

    const fetchCall = (global.fetch as jest.Mock).mock.calls[0][1];
    const body = JSON.parse(fetchCall.body);
    expect(body.variables.initiateSaveCardRequest.language).toBe('de');
  });

  it('should handle errors and call onError callback', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'Failed to save card' }];

    try {
      await saveCard({
        token: mockToken,
        employeeId: mockEmployeeId,
        profileDetails: mockProfileDetails,
        onSuccess: mockOnSuccess,
        onError: mockOnError,
        t: mockT,
        language: 'en',
        isPreferenceCard: true,
      });
    } catch (error) {
      expect(error).toBeDefined();
    }

    expect(mockOnSuccess).not.toHaveBeenCalled();
    expect(mockOnError).toHaveBeenCalled();
  });

  it('should call addCardPIBAMutation', async () => {
    mockFetchResponse.data = null;
    const result = await addCardPIBAMutation('mockData', mockToken as any);
    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });

  it('should return phone number invalid error code when WL validation fails', async () => {
    mockFetchResponse.data = null;
    mockFetchResponse.errors = [
      {
        message: JSON.stringify({
          code: requestErrors.phoneNumberInvalid,
          details: ['Phone number did not pass validation'],
        }),
      },
    ];

    const result = await addCardPIBAMutation('mockData', mockToken as any);

    expect(result).toEqual({
      error: 'generic',
      status: 'fail',
      errorCode: requestErrors.phoneNumberInvalid,
    });
  });

  it('should return generic error when WL errors do not match phone number invalid code', async () => {
    mockFetchResponse.data = null;
    mockFetchResponse.errors = [
      { message: JSON.stringify({ code: '999', details: ['Other error'] }) },
    ];

    const result = await addCardPIBAMutation('mockData', mockToken as any);

    expect(result).toEqual({ error: 'generic', status: 'fail' });
  });
});
describe('businessTether', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
  });

  it('should call businessTether and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      businessTether: { guid: 'tether-success' },
    };
    const result = await businessTether(
      'linkCode',
      'linkId',
      'memorableWord',
      true,
      mockToken as any
    );
    expect(result).toEqual({ guid: 'tether-success' });
  });

  it('should call businessTether and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await businessTether(
      'linkCode',
      'linkId',
      'memorableWord',
      true,
      mockToken as any
    );
    expect(result).toBeNull();
  });

  it('should call businessTether and handle null response', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      businessTether: null,
    };
    const result = await businessTether(
      'linkCode',
      'linkId',
      'memorableWord',
      true,
      mockToken as any
    );
    expect(result).toBeNull();
  });

  it('should call businessTether and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await businessTether(
      'linkCode',
      'linkId',
      'memorableWord',
      true,
      mockToken as any
    );
    expect(result).toBeNull();
  });
});

describe('deleteApplication', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should call deleteApplication and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      deleteApplication: {
        status: 'success',
      },
    };
    const result = await deleteApplication(
      'applicationId123',
      'applicationGuid123',
      'GB' as Scheme,
      mockToken as any
    );
    expect(result.status).toEqual('success');
  });

  it('should call deleteApplication and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await deleteApplication(
      'applicationId123',
      'applicationGuid123',
      'GB' as Scheme,
      mockToken as any
    );
    expect(result).toBeNull();
  });

  it('should call deleteApplication and handle null response', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      deleteApplication: null,
    };
    const result = await deleteApplication(
      'applicationId123',
      'applicationGuid123',
      'GB' as Scheme,
      mockToken as any
    );
    expect(result).toBeNull();
  });

  it('should call deleteApplication and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await deleteApplication(
      'applicationId123',
      'applicationGuid123',
      'GB' as Scheme,
      mockToken as any
    );
    expect(result).toBeNull();
  });

  it('should call deleteApplication with DE locale and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      deleteApplication: {
        status: 'success',
      },
    };
    const result = await deleteApplication(
      'applicationId123',
      'applicationGuid123',
      'DE' as Scheme,
      mockToken as any
    );
    expect(result.status).toEqual('success');
  });
});

describe('resendActivationEmail', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should call resendActivationEmail and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      resendActivationEmail: 'something',
    };
    const result = await resendActivationEmail('email', 'company', 'en');
    expect(result).toEqual('something');
  });

  it('should call resendActivationEmail and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await resendActivationEmail('email', 'company', 'en');
    expect(result).toBeNull();
  });

  it('should call resendActivationEmail and handle null response', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      resendActivationEmail: null,
    };
    const result = await resendActivationEmail('email', 'company', 'en');
    expect(result).toBeNull();
  });

  it('should call resendActivationEmail and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await resendActivationEmail('email', 'company', 'en');
    expect(result).toBeNull();
  });
});

describe('InnBRegistrationStepOne', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should call InnBRegistrationStepOne and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      innBRegistrationStepOneRequest: {
        status: 'success',
      },
    };

    const mockRequest = {
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      companyName: 'Test Company',
    };

    const result = await InnBRegistrationStepOne(mockRequest);

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        body: expect.stringContaining('innBRegistrationStepOneRequest'),
        headers: expect.objectContaining({
          'Content-Type': 'application/json',
        }),
      })
    );

    expect(result).toEqual(mockFetchResponse.data);
  });

  it('should call InnBRegistrationStepOne and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];

    const mockRequest = {
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      companyName: 'Test Company',
    };

    const result = await InnBRegistrationStepOne(mockRequest);

    expect(result).toBeNull();
  });

  it('should call InnBRegistrationStepOne and handle null response', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      innBRegistrationStepOneRequest: null,
    };

    const mockRequest = {
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      companyName: 'Test Company',
    };

    const result = await InnBRegistrationStepOne(mockRequest);

    expect(result.innBRegistrationStepOneRequest).toBeNull();
  });

  it('should call InnBRegistrationStepOne and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));

    const mockRequest = {
      email: 'test@test.com',
      firstName: 'Test',
      lastName: 'User',
      companyName: 'Test Company',
    };

    const result = await InnBRegistrationStepOne(mockRequest);

    expect(result).toBeNull();
  });
});

describe('updateMarketingPreferences', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should call updateMarketingPreferences and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      updateMarketingPreferences: '',
    };

    const mockRequest = {
      brandCodes: ['PINN'],
      optIn: true,
      doubleOptIn: true,
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Pod',
      countryOfResidence: 'DE',
      nationality: 'DE',
      language: 'de',
      customerId: 'testdevoptin11@mailinator.com',
      channel: 'WEB',
      journey: 'SIGNUP',
      locale: 'DE',
    };

    const result = await updateMarketingPreferences(mockRequest);

    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        body: expect.stringContaining('updateMarketingPreferences'),
        headers: expect.objectContaining({
          'Content-Type': 'application/json',
        }),
      })
    );

    expect(result).toEqual(mockFetchResponse);
  });

  it('should call updateMarketingPreferences and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];

    const mockRequest = {
      brandCodes: ['PINN'],
      optIn: true,
      doubleOptIn: true,
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Pod',
      countryOfResidence: 'DE',
      nationality: 'DE',
      language: 'de',
      customerId: 'testdevoptin11@mailinator.com',
      channel: '',
      journey: 'SIGNUP',
      locale: 'DE',
    };

    const result = await updateMarketingPreferences(mockRequest);

    expect(result).toBeNull();
  });

  it('should call updateMarketingPreferences and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));

    const mockRequest = {
      brandCodes: ['PINN'],
      optIn: true,
      doubleOptIn: true,
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Pod',
      countryOfResidence: 'DE',
      nationality: 'DE',
      language: 'de',
      customerId: 'testdevoptin11@mailinator.com',
      channel: 'WEB',
      journey: 'SIGNUP',
      locale: 'DE',
    };

    const result = await updateMarketingPreferences(mockRequest);
    expect(result).toBeNull();
  });

  it('should call updateMarketingPreferences with partial preferences', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      updateMarketingPreferences: '',
    };

    const mockRequest = {
      brandCodes: ['PINN'],
      optIn: true,
      doubleOptIn: true,
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Pod',
      countryOfResidence: 'DE',
      nationality: 'DE',
      language: 'de',
      customerId: 'testdevoptin11@mailinator.com',
      channel: 'WEB',
      journey: 'SIGNUP',
      locale: 'DE',
    };

    const result = await updateMarketingPreferences(mockRequest);

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables).toMatchObject({
      brandCodes: ['PINN'],
      optIn: true,
      doubleOptIn: true,
      title: 'Ms',
      firstName: 'Test',
      lastName: 'Pod',
      countryOfResidence: 'DE',
      nationality: 'DE',
      language: 'de',
      customerId: 'testdevoptin11@mailinator.com',
      channel: 'WEB',
      journey: 'SIGNUP',
      locale: 'DE',
    });

    expect(result).toEqual(mockFetchResponse);
  });
});

describe('registrationStepTwo', () => {
  it('should call registrationStepTwo', async () => {
    mockFetchResponse.data = {
      innBRegistrationStepTwo: {
        email: 'test@test.com',
      },
    };

    const mockParams = {
      activationKey: 'abc',
      title: 'Mr',
      firstName: 'Jon',
      lastName: 'Doe',
      phoneNumber: '+4414811700000000',
      password: 'Password1',
    };

    const result = await registrationStepTwo(mockParams);
    expect(result).toEqual({ email: 'test@test.com' });
  });
});

describe('updateCDHCard', () => {
  it('should call updateCDHCard', async () => {
    mockFetchResponse.data = {
      updatePaymentCard: '{success=true}',
    };

    const mockParams = {
      companyId: '123',
      cardId: '123',
      cardLabel: '123',
      cardType: '123',
      cardNumber: '123',
      expiryDate: '123',
      cardHolderName: '123',
      cardToken: '123',
      billingAddress: { line1: 'abc' },
      cnpRequired: true,
      cnpBusinessAccountPassword: '123',
    };

    const result = await updateCDHCard('123', mockParams);
    expect(result).toEqual({ status: 'success' });
  });
});

describe('deleteCDHCard', () => {
  it('should call deleteCDHCard', async () => {
    mockFetchResponse.data = {
      deleteCompanyCard: '',
    };

    const result = await deleteCDHCard('123', '123', '123');
    expect(result).toEqual({ status: 'success' });
  });

  it('should call deleteCDHCard and fail', async () => {
    mockFetchResponse.data = {
      deleteCompanyCard: 'failed',
    };

    const result = await deleteCDHCard('123', '123', '123');
    expect(result).toEqual({ status: 'fail', error: 'generic' });
  });
});

describe('authenticateRegistration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should call authenticateRegistration and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      authenticateRegistration: {
        registrationPrePopulatedItems: {
          emailAdress: 'test@example.com',
        },
      },
    };
    const result = await authenticateRegistration(mockToken as any, 'testRegistrationCode');
    expect(result.registrationPrePopulatedItems.emailAdress).toEqual('test@example.com');
  });

  it('should call authenticateRegistration and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await authenticateRegistration(mockToken as any, 'testRegistrationCode');
    expect(result).toBeNull();
  });

  it('should call authenticateRegistration and handle null response', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      authenticateRegistration: null,
    };
    const result = await authenticateRegistration(mockToken as any, 'testRegistrationCode');
    expect(result).toBeNull();
  });

  it('should call authenticateRegistration and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await authenticateRegistration(mockToken as any, 'testRegistrationCode');
    expect(result).toBeNull();
  });
});

describe('shareApplication', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    mockOkStatus.value = true;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should successfully share an application when all parameters are provided', async () => {
    mockFetchResponse.data = {
      shareApplication: {
        status: 'success',
        message: 'Application shared successfully',
      },
    };

    const result = await shareApplication(
      'app123',
      'guid123',
      12345,
      'token123',
      'test@example.com',
      'John Doe'
    );

    expect(result).toEqual({
      status: 'success',
      message: 'Application shared successfully',
    });
  });

  it('should throw error when required parameters are missing', async () => {
    await expect(shareApplication('app123', 'guid123', 12345, 'token123')).rejects.toThrow(
      'Email and full name are required for sharing application'
    );

    await expect(
      shareApplication('', 'guid123', 12345, 'token123', 'test@example.com', 'John Doe')
    ).rejects.toThrow('Application ID and GUID are required');
  });
});

describe('removeParticipant', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should successfully remove participant with numeric ID', async () => {
    mockFetchResponse.data = {
      removeParticipant: {
        status: 'success',
        message: 'Participant removed successfully',
      },
    };

    const result = await removeParticipant('app123', 'guid123', 12345, 'token123');

    expect(result).toEqual({
      status: 'success',
      message: 'Participant removed successfully',
    });

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables.removeParticipantRequest.employeeId).toBe(12345);
  });

  it('should successfully remove participant with string ID', async () => {
    mockFetchResponse.data = {
      removeParticipant: {
        status: 'success',
        message: 'Participant removed successfully',
      },
    };

    const result = await removeParticipant('app123', 'guid123', '12345', 'token123');

    expect(result).toEqual({
      status: 'success',
      message: 'Participant removed successfully',
    });

    const body = JSON.parse((global.fetch as jest.Mock).mock.calls[0][1].body);
    expect(body.variables.removeParticipantRequest.employeeId).toBe(12345);
  });
});

describe('approveRejectEmployee', () => {
  const mockToken = TEST_TOKEN;
  const mockApproveRejectRequest = {
    employeeId: 'EMPL_123',
    approve: true,
    reason: 'Valid reason',
  } as any;

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    mockOkStatus.value = true;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when approveRejectEmployee is successful', async () => {
    mockFetchResponse.data = { approveRejectEmployee: '' };
    const result = await approveRejectEmployee(mockApproveRejectRequest, mockToken);
    expect(result).toEqual({ status: requestStatus.success });
  });

  it('should return fail when approveRejectEmployee returns unexpected value', async () => {
    mockFetchResponse.data = { approveRejectEmployee: 'unexpected' };
    const result = await approveRejectEmployee(mockApproveRejectRequest, mockToken);
    expect(result).toEqual({ status: requestStatus.fail, error: requestErrors.generic });
  });

  it('should return fail when there are GraphQL errors', async () => {
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    mockFetchResponse.data = null;
    const result = await approveRejectEmployee(mockApproveRejectRequest, mockToken);
    expect(result).toEqual({ status: requestStatus.fail, error: requestErrors.generic });
  });

  it('should return fail when data is null', async () => {
    mockFetchResponse.data = null;
    const result = await approveRejectEmployee(mockApproveRejectRequest, mockToken);
    expect(result).toEqual({ status: requestStatus.fail, error: requestErrors.generic });
  });
});

describe('submitRegistration', () => {
  const mockRegistrationDetails = {
    title: 'Mr',
    forename: 'Test',
    surname: 'Test',
    emailAddress: 'mockEmail@email.com',
    memorableWord: 'mockMemorableWord',
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;
    mockOkStatus.value = true;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should call submitRegistration and return success', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      submitRegistration: {
        tetherDetails: {
          tetheredUserGuid: 'tetheredUserGuid',
        },
      },
    };
    const result = await submitRegistration(
      mockToken as any,
      'testRegistrationCode',
      mockRegistrationDetails
    );
    expect(result.tetherDetails.tetheredUserGuid).toEqual('tetheredUserGuid');
  });

  it('should call submitRegistration and handle errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await submitRegistration(
      mockToken as any,
      'testRegistrationCode',
      mockRegistrationDetails
    );
    expect(result).toBeNull();
  });

  it('should call submitRegistration and handle null response', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      submitRegistration: null,
    };
    const result = await submitRegistration(
      mockToken as any,
      'testRegistrationCode',
      mockRegistrationDetails
    );
    expect(result).toBeNull();
  });

  it('should call submitRegistration and handle network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await submitRegistration(
      mockToken as any,
      'testRegistrationCode',
      mockRegistrationDetails
    );
    expect(result).toBeNull();
  });
});

describe('forgotPassword', () => {
  const mockLanguage = 'en';
  const mockInnBusiness = true;
  const mockForgottenPasswordRequest = {
    username: 'test@example.com',
  } as ForgottenPasswordRequest;

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when forgotPassword is successful', async () => {
    mockFetchResponse.data = { forgotPassword: { success: true } };
    const result = await forgotPassword(
      mockLanguage,
      mockInnBusiness,
      mockForgottenPasswordRequest
    );
    expect(result).toEqual({ success: true });
  });

  it('should return null when forgotPassword returns null', async () => {
    mockFetchResponse.data = { forgotPassword: null };
    const result = await forgotPassword(
      mockLanguage,
      mockInnBusiness,
      mockForgottenPasswordRequest
    );
    expect(result).toBeNull();
  });

  it('should return null when there are GraphQL errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await forgotPassword(
      mockLanguage,
      mockInnBusiness,
      mockForgottenPasswordRequest
    );
    expect(result).toBeNull();
  });

  it('should return null when there are network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await forgotPassword(
      mockLanguage,
      mockInnBusiness,
      mockForgottenPasswordRequest
    );
    expect(result).toBeNull();
  });
});

describe('resetPassword', () => {
  const mockLanguage = 'en';
  const mockResetPasswordRequest = {
    customerId: 'test@test.com',
    newPassword: 'NewPassword123',
  } as ResetPasswordRequest;

  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {
      resetPassword: {
        passwordChanged: true,
      },
    };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when resetPassword is successful', async () => {
    mockFetchResponse.data.resetPassword = {
      passwordChanged: true,
    };
    const result = await resetPassword(mockLanguage, mockResetPasswordRequest);
    expect(result).toBeTruthy();
  });

  it('should return null when resetPassword returns null', async () => {
    mockFetchResponse.data.resetPassword = { passwordChanged: null };
    const result = await resetPassword(mockLanguage, mockResetPasswordRequest);
    expect(result).toBeNull();
  });

  it('should return null when there are GraphQL errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await resetPassword(mockLanguage, mockResetPasswordRequest);
    expect(result).toBeNull();
  });

  it('should return null when there are network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await resetPassword(mockLanguage, mockResetPasswordRequest);
    expect(result).toBeNull();
  });
});

describe('validateResetKey', () => {
  const mockValidateResetKeyRequest = {
    resetKey: 'test',
  } as ValidateResetKeyRequest;
  const mockValidateResetKeyResponse = {
    emailAddress: 'test@test.com',
    valid: true,
  } as ValidateResetKeyResponse;

  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {
      validateResetKey: mockValidateResetKeyResponse,
    };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when validateResetKey is successful', async () => {
    mockFetchResponse.data = {
      validateResetKey: mockValidateResetKeyResponse,
    };
    const result = await validateResetKey(mockValidateResetKeyRequest);
    expect(result).toEqual(mockValidateResetKeyResponse);
  });

  it('should return null when validateResetKey returns null', async () => {
    mockFetchResponse.data = { validateResetKey: null };
    const result = await validateResetKey(mockValidateResetKeyRequest);
    expect(result).toBeNull();
  });

  it('should return null when there are GraphQL errors', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await validateResetKey(mockValidateResetKeyRequest);
    expect(result).toBeNull();
  });

  it('should return null when there are network errors', async () => {
    global.fetch = jest.fn().mockRejectedValueOnce(new Error('Network error'));
    const result = await validateResetKey(mockValidateResetKeyRequest);
    expect(result).toBeNull();
  });
});

describe('submitApplication', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {
      submitApplicationV1: {
        message: 'submited',
      },
    };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when submitApplication is successful', async () => {
    const result = await submitApplication('1', '1', '1', '1', '1', '1', true, true, '1');
    expect(result).toEqual({ status: 'success' });
  });

  it('should return fail when submitApplication is failed', async () => {
    mockFetchResponse.data.submitApplicationV1 = null;
    const result = await submitApplication('1', '1', '1', '1', '1', '1', true, true, '1');
    expect(result).toEqual({ status: 'fail' });
  });
});

describe('updatePIBACardMutation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {
      updateInnBPIBACard: {
        message: 'submitted',
      },
    };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when updatePIBACardMutation is successful', async () => {
    const result = await updatePIBACardMutation('1', '1');
    expect(result).toEqual({ data: { message: 'submitted' }, status: 'success' });
  });

  it('should return fail when updatePIBACardMutation is failed', async () => {
    mockFetchResponse.data.updateInnBPIBACard = null;
    const result = await updatePIBACardMutation('1', '1');
    expect(result).toEqual({ status: 'fail', error: 'generic' });
  });
});

describe('cancelAndReplacePIBACardMutation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {
      cancelAndReplaceInnBPIBACard: {
        cancelledCardDetails: {
          cardHolderName: 'Mr Test User',
          cardId: 39166,
          status: 'CANCELLED',
        },
      },
    };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when cancelAndReplaceInnBPIBACard is successful', async () => {
    const result = await cancelAndReplacePIBACardMutation('1', '1');
    expect(result).toEqual({
      data: {
        cancelledCardDetails: {
          cardHolderName: 'Mr Test User',
          cardId: 39166,
          status: 'CANCELLED',
        },
      },
      status: 'success',
    });
  });

  it('should return fail when cancelAndReplaceInnBPIBACard is failed', async () => {
    mockFetchResponse.data.cancelAndReplaceInnBPIBACard = null;
    const result = await cancelAndReplacePIBACardMutation('1', '1');
    expect(result).toEqual({ status: 'fail', error: 'generic' });
  });
});

describe('replaceCardMutation', () => {
  const replaceCardData = {
    tetheredUserGuid: '2aa58811-8e98-413d-887b-d8e845a988a2',
    scheme: 'GB' as Scheme,
    cardId: '14199',
    replaceCardRequest: {
      address: {
        addressLine1: 'line1',
        addressLine2: 'line2',
        postcode: 'ABC DEF',
        countryCode: 'GB',
      },
      cancelCard: false,
      contactDetails: { title: 'Mr', foreName: 'Lucian', lastName: 'Test' },
      shouldDespatchToCardholder: true,
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = { replaceCard: 'Card replaced successfully' };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when replaceCard is successful', async () => {
    const result = await replaceCardMutation(replaceCardData, '1');
    expect(result).toEqual({
      data: 'Card replaced successfully',
      status: 'success',
    });
  });

  it('should return fail when replaceCard is failed', async () => {
    mockFetchResponse.data.replaceCard = null;
    const result = await replaceCardMutation(replaceCardData, '1');
    expect(result).toEqual({ status: 'fail', error: 'generic' });
  });
});

describe('resendCodeMutation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = {
      inviteCardHolder: 'OK',
    };

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return success when resendCodeMutation is successful', async () => {
    const result = await resendCodeMutation('1', '1');
    expect(result).toEqual({
      data: 'OK',
      status: 'success',
    });
  });

  it('should return fail when resendCodeMutation is failed', async () => {
    mockFetchResponse.data.inviteCardHolder = null;
    const result = await resendCodeMutation('1', '1');
    expect(result).toEqual({ status: 'fail', error: 'generic' });
  });
});

describe('downloadBookingInvoice', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockOkStatus.value = true;
    mockFetchResponse.errors = undefined;
    mockFetchResponse.data = null;

    global.fetch = jest.fn(() =>
      Promise.resolve(
        createMockResponse(() => Promise.resolve(mockFetchResponse), mockOkStatus.value)
      )
    );
  });

  it('should return download data when downloadBookingInvoice is successful', async () => {
    mockFetchResponse.data = {
      downloadBookingInvoice: {
        invoices: [
          {
            bookingRef: 'AWM7115824',
            url: 'https://example.com/Invoice_GAA9674785_Smith.pdf',
            expiresAt: '2026-04-17T10:00:00Z',
            fileName: 'Invoice_GAA9674785_Smith.pdf',
            mimeType: 'application/pdf',
            language: 'en',
            invoiceMeta: {
              invoiceNumber: 'INV-001',
              issuedDate: '2026-04-10',
              hotelId: '12345',
            },
          },
        ],
      },
    };
    const result = await downloadBookingInvoice(
      {
        bookingRef: ['AWM7115824'],
        lang: 'en',
        channel: 'PI',
        hotelBrand: 'premierinn',
        subChannel: 'WEB',
      },
      'mock-token'
    );

    // Verify fetch was called with correct parameters
    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          Authorization: 'Bearer mock-token',
          'Content-Type': 'application/json',
        }),
      })
    );

    // Verify the request body contains the correct variables
    const fetchCall = (global.fetch as jest.Mock).mock.calls[0];
    const requestBody = JSON.parse(fetchCall[1].body);
    expect(requestBody.variables).toEqual({
      downloadBookingInvoiceRequest: {
        bookingRef: ['AWM7115824'],
        lang: 'en',
        channel: 'PI',
        hotelBrand: 'premierinn',
        subChannel: 'WEB',
      },
    });

    expect(result).toEqual({
      data: {
        invoices: [
          {
            bookingRef: 'AWM7115824',
            url: 'https://example.com/Invoice_GAA9674785_Smith.pdf',
            expiresAt: '2026-04-17T10:00:00Z',
            fileName: 'Invoice_GAA9674785_Smith.pdf',
            mimeType: 'application/pdf',
            language: 'en',
            invoiceMeta: {
              invoiceNumber: 'INV-001',
              issuedDate: '2026-04-10',
              hotelId: '12345',
            },
          },
        ],
      },
      errors: undefined,
    });
  });

  it('should return null when downloadBookingInvoice data is null', async () => {
    mockFetchResponse.data = {
      downloadBookingInvoice: null,
    };
    const result = await downloadBookingInvoice(
      {
        bookingRef: ['AWM7115824'],
        lang: 'en',
        channel: 'PI',
        hotelBrand: 'premierinn',
        subChannel: 'WEB',
      },
      'mock-token'
    );

    // Verify fetch was called with correct authentication
    expect(global.fetch).toHaveBeenCalledWith(
      expect.any(String),
      expect.objectContaining({
        method: 'POST',
        headers: expect.objectContaining({
          Authorization: 'Bearer mock-token',
        }),
      })
    );

    expect(result).toEqual({ data: null, errors: undefined });
  });

  it('should handle errors gracefully', async () => {
    mockOkStatus.value = false;
    mockFetchResponse.errors = [{ message: 'GraphQL Error' }];
    const result = await downloadBookingInvoice(
      {
        bookingRef: ['AWM7115824'],
        lang: 'en',
        channel: 'PI',
        hotelBrand: 'premierinn',
        subChannel: 'WEB',
      },
      'mock-token'
    );
    expect(result).toBeNull();
  });

  it('should handle undefined optional parameters in request', async () => {
    mockFetchResponse.data = {
      downloadBookingInvoice: {
        invoices: [
          {
            bookingRef: 'AWM7115824',
            url: 'https://example.com/invoice.pdf',
            fileName: 'Invoice.pdf',
          },
        ],
      },
    };

    const result = await downloadBookingInvoice(
      {
        bookingRef: ['AWM7115824'],
        lang: 'en',
        channel: 'PI',
        hotelBrand: 'premierinn',
        subChannel: '', // subChannel is optional
      },
      'mock-token'
    );

    // Verify fetch was called
    expect(global.fetch).toHaveBeenCalled();

    // Verify the request body - subChannel should not be included if undefined
    const fetchCall = (global.fetch as jest.Mock).mock.calls[0];
    const requestBody = JSON.parse(fetchCall[1].body);
    expect(requestBody.variables).toEqual({
      downloadBookingInvoiceRequest: {
        bookingRef: ['AWM7115824'],
        lang: 'en',
        channel: 'PI',
        hotelBrand: 'premierinn',
        subChannel: '',
      },
    });

    expect(result).toBeDefined();
    expect(result?.data?.invoices).toHaveLength(1);
  });
});

import useCompanyDetails from './use-company-details';

jest.mock('./use-request', () => ({
  ...jest.requireActual('./use-request'),
  useRestQueryRequest: () => mockUseRestQueryRequest,
}));

jest.mock('../getters/auth', () => ({
  ...jest.requireActual('../getters/auth'),
  getAuthCookie: () => 'the auth cookie',
}));

const mockUseRestQueryRequest = {
  isLoading: false,
  isError: false,
  error: { message: '' },
  data: {
    requestedCompany: {
      companyDetails: {
        companyName: 'Bills Group LTD',
        alternateCompanyName: 'Bills Group LTD',
        numberOfEmployees: 4,
        companyAddress: {
          addressLine1: '3 Anthony Road',
          addressLine2: 'Newquay',
          addressLine3: '',
          addressLine4: 'Cornwall',
          addressLine5: '',
          postCode: 'TR4 5AS',
          country: 'GB',
        },
        mainEmployee: {
          id: '1',
          emailAddress: 'traveling.bgl@mailinator.com',
          position: '',
          phoneNumber: '+445555555555',
          mobileNumber: '',
          textConfirmation: false,
          title: 'Miss',
          firstName: 'Boatyness',
          lastName: 'McBoatss',
        },
      },
      paymentDetails: {
        profileLocked: false,
        allowIndividualCards: false,
        paymentCards: [
          {
            cardId: 1,
            cardLabel: 'Mastercard',
            cardType: 'MD',
            nameOnCard: 'Ella sc',
            cardNumber: '************1100',
            startDate: '',
            expiryDate: '0428',
            billingAddress: {
              addressLine1: '3 Anthony Road',
              addressLine2: 'Newquay',
              addressLine3: '',
              addressLine4: 'Cornwall',
              addressLine5: '',
              postCode: 'TR4 5AS',
              country: 'GB',
            },
            cardNotPresentRequired: false,
          },
        ],
      },
      bookingAllowances: {
        maxDinnerBudgets: {
          uKWide: {
            amount: 0,
            currency: 'GBP',
          },
          greaterLondon: {
            amount: 0,
            currency: 'GBP',
          },
          ireland: {
            amount: 0,
            currency: 'EUR',
          },
        },
        extrasCodes: ['1', '2', '3', '4', '5'],
        upsellItemsAllowed: ['11', '15', '12', '17', '18', '135', '136', '137'],
        allowAlcohol: true,
        allowCarParking: true,
        allowAdditionalCosts: true,
        allowPremierSaverRates: true,
        allowIndividualCards: true,
        maxNumberOfNights: 14,
      },
      bookingAlerts: {
        rateCaps: {
          uKWide: {
            amount: 0,
            currency: 'GBP',
          },
          greaterLondon: {
            amount: 0,
            currency: 'GBP',
          },
          ireland: {
            amount: 0,
            currency: 'EUR',
          },
        },
        bookingAlertHotels: [''],
        recipientEmailAddresses: [''],
        dayOfArrival: false,
        weekendArrival: false,
        passThroughWeekend: false,
      },
      companyManagementDetails: {
        purchaseOrderManagement: {
          label: '',
          mandatory: false,
          managementHeader: '',
          active: false,
          managementInformationAnswer: {},
        },
        customerReferenceManagement: {
          label: '',
          mandatory: false,
          managementHeader: '',
          active: false,
          managementInformationAnswer: {},
        },
        userDefinedManagement: [],
      },
    },
    companyCellCodes: [
      {
        type: '1',
        description: 'BFLEX',
      },
    ],
    allowCentralCreditCard: true,
    marketingAllowed: false,
    companyLockedForEditing: false,
    success: true,
  },
};

const params = {
  companyId: '35086',
  sessionId: '5ib3z8UxGPQRH0Ox',
  employeeId: '1',
};

describe('useCompanyDetails', () => {
  it('should return company details with company name', () => {
    const expectedCompanyName = 'Bills Group LTD';
    const companyDetails = useCompanyDetails(
      params.companyId,
      params.sessionId,
      params.employeeId,
      true
    );
    expect(companyDetails.requestedCompany.companyDetails.companyName).toBe(expectedCompanyName);
  });

  it('should return company details with cdh on false', () => {
    const expectedCompanyName = 'Bills Group LTD';
    const companyDetails = useCompanyDetails(
      params.companyId,
      params.sessionId,
      params.employeeId,
      true
    );
    expect(companyDetails.requestedCompany.companyDetails.companyName).toBe(expectedCompanyName);
  });
});

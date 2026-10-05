import { BUSINESS_BOOKER_USER_ROLES, EmployeeStatus, AddressInfo } from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import {
  capitalizeFirstLetter,
  formatHDPUrl,
  formatIBAssetsUrl,
  getLabelType,
  getStyling,
  resolveAndDownloadBlob,
  parseRegistrationQuestions,
  formatAccountNumber,
  ParseDateToYMD,
  getDefaultSwitchState,
  mapSwitchState,
  getVariant,
  normalizeAddress,
  findError,
  getSavedCardType,
  parseAnswersObj,
} from './formatters';

const mockRawQuestions = {
  purchaseOrderManagement: {
    questionId: '2',
    label: 'Purchase order number?',
    mandatory: false,
    managementHeader: '123',
    active: true,
    location: 'R',
    managementInformationAnswer: {},
    positionId: 0,
  },
  customerReferenceManagement: {
    questionId: '1',
    label: 'Customer reference?',
    mandatory: true,
    managementHeader: 'Customer reference',
    active: true,
    location: 'R',
    managementInformationAnswer: { answerType: 'F' },
    type: 'customer reference',
    positionId: 0,
  },
  userDefinedManagement: [
    {
      questionId: 'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95',
      label: 'Mandatory dropdown?',
      mandatory: true,
      managementHeader: 'Label for question',
      active: true,
      location: 'R',
      managementInformationAnswer: [Object],
      positionId: 1,
    },
    {
      questionId: 'COQU_17a382ab-a741-475c-bf71-685e2269977b',
      label: 'What are you?',
      mandatory: false,
      managementHeader: '222222',
      active: true,
      location: 'R',
      managementInformationAnswer: [Object],
      positionId: 2,
    },
    {
      questionId: 'COQU_304451a9-44d4-4a22-b019-b47121c0bb40',
      label: 'When Booking question?',
      mandatory: false,
      managementHeader: '12334343',
      active: true,
      location: 'B',
      managementInformationAnswer: [Object],
      positionId: 3,
    },
  ],
};

const mockFetchResponse = {
  data: {},
};

const baseUrl = 'https://localhost:3000/en-gb/homepage';
const mockOkStatus = { value: true };

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
    headers: new Headers(),
    redirected: false,
    status: 200,
    statusText: 'OK',
    type: 'basic',
    url: '',
    clone: jest.fn(),
    body: null,
    bodyUsed: false,
    arrayBuffer: jest.fn(),
    blob: jest.fn(),
    formData: jest.fn(),
    text: jest.fn(),
  } as Response)
);

const mockUseRouter = jest.fn();
jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => mockUseRouter(),
}));

const mockSuperRole = BUSINESS_BOOKER_USER_ROLES.SUPER;
const mockToken = {
  email: 'test@test.com',
  companyId: 'test',
  business: {
    accessLevel: mockSuperRole,
    tethered: false,
  },
};
const mockCookieData = {
  value: mockToken,
};
const mockCookieStore = {
  get: () => mockCookieData,
} as unknown as ReturnType<typeof cookies>;
const mockHeadersWbUrl: string | null = 'http://test.com?a=1';

jest.mock('next/headers', () => ({
  cookies: () => mockCookieStore,
  headers: () => ({ get: () => mockHeadersWbUrl }),
}));

jest.mock('nanoid', () => ({
  nanoid: () => 'id',
}));

jest.mock('../../utils/decodeIdToken', () => (token: string) => token);

jest.mock('../../utils/unleash', () => ({
  getUnleashTogglesServerOrClient: jest.fn(),
}));

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => jest.fn(),
}));

window.URL.createObjectURL = jest.fn();
window.URL.revokeObjectURL = jest.fn();
window.open = jest.fn();
describe('Server formatters', () => {
  it('should call formatIBAssetsUrl', async () => {
    const result = formatIBAssetsUrl('/pathToImage');
    expect(result).toContain('pathToImage');
  });
  it('should call formatIBAssetsUrl with no param', async () => {
    const result = formatIBAssetsUrl();
    expect(result).toContain('undefined');
  });

  it('should call resolveAndDownloadBlob', () => {
    const obj = { test: 'testt' };
    const blob = new Blob([JSON.stringify(obj)], {
      type: 'application/json',
    });
    const result = resolveAndDownloadBlob(blob, 'test', '.test');
    expect(result).toBe(undefined);
  });
});

describe('formatHDPUrl function', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return the same URL if not "&"', () => {
    expect(formatHDPUrl(baseUrl)).toEqual(baseUrl);
  });
  it('should return the same URL if number of params not match number of pairs', () => {
    const url = `${baseUrl}?param1=value&param2=value&param3=value`;
    expect(formatHDPUrl(url)).toEqual(url);
  });
  it('should format ARRdd abd ARRmm parameters correctly', () => {
    const url = `${baseUrl}?date=2024-09-18&ARRdd=18&ARRmm=9`;
    const expected = `${baseUrl}?date=2024-09-18&ARRdd=18&ARRmm=09`;
    expect(formatHDPUrl(url)).toEqual(expected);
  });
  it('should handle multiple parameters', () => {
    const url = `${baseUrl}?param1=value1&ARRdd=18&param2=value2&ARRmm=9`;
    const expected = `${baseUrl}?param1=value1&ARRdd=18&param2=value2&ARRmm=09`;
    expect(formatHDPUrl(url)).toEqual(expected);
  });
});
describe('capitalizeFirstLetter', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should return only first letter as capital', () => {
    const result = capitalizeFirstLetter(EmployeeStatus.Active);

    expect(result).toBe('Active');
  });
});
describe('getStyling', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('EmployeeStatus Inactive - should return background', () => {
    const background = getStyling(EmployeeStatus.Inactive);

    expect(background).toBe('bg-warning');
  });
  it('EmployeeStatus Deactivated - should return background', () => {
    const background = getStyling(EmployeeStatus.Deactivated);

    expect(background).toBe('bg-error');
  });
  it('EmployeeStatus Active - should return background', () => {
    const background = getStyling(EmployeeStatus.Active);

    expect(background).toBe('bg-success');
  });

  it('EmployeeStatus Suspended - should return background', () => {
    const background = getStyling(EmployeeStatus.Suspended);

    expect(background).toBe('bg-error');
  });

  it('EmployeeStatus Suspended - should return background', () => {
    const background = getStyling(EmployeeStatus.Purged);

    expect(background).toBe('bg-error');
  });

  it('EmployeeStatus undefined - should return default value ""', () => {
    const background = getLabelType(undefined);
    expect(background).toBe('');
  });
});
describe('getLabelType', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('EmployeeStatus Inactive - should return all statuses, label 1, label 2', () => {
    const firstLabel = getLabelType(EmployeeStatus.Inactive, true);

    expect(firstLabel).toBe('userMgmt.account.status.inactive');

    const secondLabel = getLabelType(EmployeeStatus.Inactive, false);

    expect(secondLabel).toBe('userMgmt.manageEmployees.seachresults.status.inactive');
  });
  it('EmployeeStatus Deactivated - should return all statuses, label 1, label 2 ', () => {
    const firstLabel = getLabelType(EmployeeStatus.Deactivated, false);

    expect(firstLabel).toBe('userMgmt.manageEmployees.seachresults.status.deactivated');

    const secondLabel = getLabelType(EmployeeStatus.Deactivated, true);

    expect(secondLabel).toBe('userMgmt.account.status.deactivated');
  });
  it('EmployeeStatus Active - should return all statuses, label 1, label 2r', () => {
    const firstLabel = getLabelType(EmployeeStatus.Active, false);

    expect(firstLabel).toBe('userMgmt.manageEmployees.seachresults.status.active');

    const secondLabel = getLabelType(EmployeeStatus.Active, true);

    expect(secondLabel).toBe('userMgmt.account.status.active');
  });
  it('EmployeeStatus Suspended - should return label if isEmployeeStatus is not given ', () => {
    const firstLabel = getLabelType(EmployeeStatus.Suspended);
    expect(firstLabel).toBe('userMgmt.manageEmployees.seachresults.status.deactivated');
  });
  it('EmployeeStatus Purged - should return label if isEmployeeStatus is not given ', () => {
    const firstLabel = getLabelType(EmployeeStatus.Purged);
    expect(firstLabel).toBe('userMgmt.manageEmployees.seachresults.status.deactivated');
  });

  it('EmployeeStatus Active - should return label if isEmployeeStatus is not given ', () => {
    const firstLabel = getLabelType(EmployeeStatus.Active);
    expect(firstLabel).toBe('userMgmt.manageEmployees.seachresults.status.active');
  });
  it('EmployeeStatus undefined - should return default value ""', () => {
    const firstLabel = getLabelType(undefined, true);
    expect(firstLabel).toBe('');
  });

  it('parseRegistrationQuestions', () => {
    const questions = parseRegistrationQuestions(mockRawQuestions);
    expect(questions).toEqual([
      {
        answer: '',
        id: 'purchaseOrderAnswer',
        label: 'Purchase order number?',
        mandatory: false,
        type: 'text',
      },
      {
        answer: '',
        id: 'customerReferenceAnswer',
        label: 'Customer reference?',
        mandatory: true,
        type: 'text',
      },
      {
        answer: '',
        id: 'COQU_76f3817f-4940-43cc-89f8-7c1caf406f95',
        label: 'Mandatory dropdown?',
        mandatory: true,
        type: 'text',
      },
      {
        answer: '',
        id: 'COQU_17a382ab-a741-475c-bf71-685e2269977b',
        label: 'What are you?',
        mandatory: false,
        type: 'text',
      },
    ]);
  });

  it('ParseDateToYMD', () => {
    const questions = ParseDateToYMD(new Date('04/14/2022'));
    expect(questions).toEqual('2022-04-14');
  });

  it('ParseDateToYMD with undefined', () => {
    const questions = ParseDateToYMD(undefined);
    expect(questions).toEqual(undefined);
  });
});
describe('formatAccountNumber', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should format account number with spaces', () => {
    const accountNumber = '1234567890123456';
    const formatted = formatAccountNumber(accountNumber);
    expect(formatted).toBe('1234 5678 9012 3456');
  });

  it('should return empty string if account number is empty', () => {
    const accountNumber = '';
    const formatted = formatAccountNumber(accountNumber);
    expect(formatted).toBe('');
  });

  it('should return the same account number if it is less than 4 digits', () => {
    const accountNumber = '123';
    const formatted = formatAccountNumber(accountNumber);
    expect(formatted).toBe('123');
  });

  it('should format account number correctly if it is not a multiple of 4', () => {
    const accountNumber = '12345678901';
    const formatted = formatAccountNumber(accountNumber);
    expect(formatted).toBe('1234 5678 901');
  });
});
describe('getDefaultSwitchState', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return default state with all false when upsellItemsAllowed is empty', () => {
    const upsellItemsAllowed: string[] = [];
    const result = getDefaultSwitchState(upsellItemsAllowed);
    expect(result).toEqual({
      premierInnBreakfast: false,
      continentalBreakfast: false,
      mealDeal: false,
      hubBreakfast: false,
      ultimateWifi: false,
    });
  });

  it('should return default state with true for premierInnBreakfast when upsellItemsAllowed contains 11', () => {
    const upsellItemsAllowed: string[] = ['11'];
    const result = getDefaultSwitchState(upsellItemsAllowed);
    expect(result).toEqual({
      premierInnBreakfast: true,
      continentalBreakfast: false,
      mealDeal: false,
      hubBreakfast: false,
      ultimateWifi: false,
    });
  });

  it('should return default state with true for multiple items when upsellItemsAllowed contains multiple values', () => {
    const upsellItemsAllowed: string[] = ['11', '12', '17'];
    const result = getDefaultSwitchState(upsellItemsAllowed);
    expect(result).toEqual({
      premierInnBreakfast: true,
      continentalBreakfast: true,
      mealDeal: true,
      hubBreakfast: false,
      ultimateWifi: false,
    });
  });

  it('should return default state with true for ultimateWifi when upsellItemsAllowed contains any of 135, 136, 137', () => {
    const upsellItemsAllowed: string[] = ['135', '136', '137'];
    const result = getDefaultSwitchState(upsellItemsAllowed);
    expect(result).toEqual({
      premierInnBreakfast: false,
      continentalBreakfast: false,
      mealDeal: false,
      hubBreakfast: false,
      ultimateWifi: true,
    });
  });

  it('should return default state with all false when upsellItemsAllowed contains unknown values', () => {
    const upsellItemsAllowed: string[] = ['999', '888'];
    const result = getDefaultSwitchState(upsellItemsAllowed);
    expect(result).toEqual({
      premierInnBreakfast: false,
      continentalBreakfast: false,
      mealDeal: false,
      hubBreakfast: false,
      ultimateWifi: false,
    });
  });
});
describe('mapSwitchState', () => {
  it('should return an empty array when switchState is empty', () => {
    const switchState = {} as any;
    const result = mapSwitchState(switchState);
    expect(result).toEqual([]);
  });

  it('should return correct upsell items for given switchState', () => {
    const switchState = {
      premierInnBreakfast: true,
      continentalBreakfast: false,
      mealDeal: true,
      hubBreakfast: false,
      ultimateWifi: false,
    };
    const result = mapSwitchState(switchState);
    expect(result).toEqual(['11', '17']);
  });

  it('should return correct upsell items when multiple switchState values are true', () => {
    const switchState = {
      premierInnBreakfast: true,
      continentalBreakfast: true,
      mealDeal: true,
      hubBreakfast: true,
      ultimateWifi: true,
    };
    const result = mapSwitchState(switchState);
    expect(result).toEqual(['11', '12', '17', '18', '135', '136', '137']);
  });

  it('should return an empty array when all switchState values are false', () => {
    const switchState = {
      premierInnBreakfast: false,
      continentalBreakfast: false,
      mealDeal: false,
      hubBreakfast: false,
      ultimateWifi: false,
    };
    const result = mapSwitchState(switchState);
    expect(result).toEqual([]);
  });

  it('should handle unknown switchState keys gracefully', () => {
    const switchState = {
      unknownKey: true,
      premierInnBreakfast: true,
    } as any;
    const result = mapSwitchState(switchState);
    expect(result).toEqual(['11']);
  });
});

describe('getVariant', () => {
  it('should return the field prefixed with the variant when variant is provided', () => {
    const result = getVariant('fieldName', 'variantName');
    expect(result).toBe('variantName.fieldName');
  });

  it('should return only the field when variant is an empty string', () => {
    const result = getVariant('fieldName', '');
    expect(result).toBe('fieldName');
  });

  it('should return only the field when variant is undefined', () => {
    const result = getVariant('fieldName', undefined as unknown as string);
    expect(result).toBe('fieldName');
  });

  it('should handle special characters in the field and variant', () => {
    const result = getVariant('field.Name', 'variant-Name');
    expect(result).toBe('variant-Name.field.Name');
  });

  it('should return only the field when variant is null', () => {
    const result = getVariant('fieldName', null as unknown as string);
    expect(result).toBe('fieldName');
  });
});
describe('normalizeAddress', () => {
  it('should replace null values with empty strings', () => {
    const address: AddressInfo = {
      addressLine1: 'test',
      addressLine2: '',
      addressLine3: null,
      addressLine4: null,
      addressLine5: null,
      country: 'GB',
      postCode: 'asdzxc',
    };

    const normalized = normalizeAddress(address);

    expect(normalized).toEqual({
      addressLine1: 'test',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressLine5: '',
      country: 'GB',
      postCode: 'asdzxc',
    });
  });

  it('should handle an empty address object', () => {
    const address: AddressInfo = {} as AddressInfo;

    const normalized = normalizeAddress(address);

    expect(normalized).toEqual({});
  });
});

describe('findError', () => {
  it('should return undefined if the object is null or undefined', () => {
    expect(findError('some.path', null)).toBeUndefined();
    expect(findError('some.path', undefined)).toBeUndefined();
  });

  it('should return undefined if the object is empty', () => {
    expect(findError('some.path', {})).toBeUndefined();
  });

  it('should return undefined if the path does not exist in the object', () => {
    const obj = { some: { other: { key: 'value' } } };
    expect(findError('non.existent.path', obj)).toBeUndefined();
  });

  it('should return undefined if the path exists but does not contain a message property', () => {
    const obj = { some: { path: { key: 'value' } } };
    expect(findError('some.path', obj)).toBeUndefined();
  });

  it('should return the message if the path exists and contains a message property', () => {
    const obj = { some: { path: { message: 'Error message' } } };
    expect(findError('some.path', obj)).toBe('Error message');
  });
});

describe('getSavedCardType', () => {
  it('should return PIBA or CARD', () => {
    expect(getSavedCardType('AT')).toBe('KEEP_PIBA');
    expect(getSavedCardType('VS')).toBe('KEEP_CARD');
    expect(getSavedCardType()).toBe(undefined);
  });
});

describe('parseAnswersObj', () => {
  it('should parse answers object correctly', () => {
    const mockData = {
      customerReferenceAnswer: '123345',
      purchaseOrderAnswer: '123345',
      'COQU_7bc823b1-40cc-4ec6-8102-f9fc818a72a2': {
        displayValue: 'test123',
        value: '1',
      },
      'COQU_8933007d-a8d3-4426-bb26-d4dba18a47a9': 'TEST123',
      'COQU_2aac133e-1e87-4063-a1f1-02bbf741521f': 'TEST-TWO',
      'COQU_04c85e49-f28c-49e4-a569-ca94d76d0806': {
        displayValue: '1',
        value: '1',
      },
      'COQU_a46ad234-5fbe-4d5f-a64b-53a7254be861': {
        displayValue: '2',
        value: '2',
      },
    };

    const mockResponse = {
      customerReferenceAnswer: '123345',
      purchaseOrderAnswer: '123345',
      userDefinedAnswers: [
        {
          miAnswer: '123345',
          miID: 'customerReferenceAnswer',
        },
        {
          miAnswer: '123345',
          miID: 'purchaseOrderAnswer',
        },
        {
          miAnswer: '1',
          miID: 'COQU_7bc823b1-40cc-4ec6-8102-f9fc818a72a2',
        },
        {
          miAnswer: 'TEST123',
          miID: 'COQU_8933007d-a8d3-4426-bb26-d4dba18a47a9',
        },
        {
          miAnswer: 'TEST-TWO',
          miID: 'COQU_2aac133e-1e87-4063-a1f1-02bbf741521f',
        },
        {
          miAnswer: '1',
          miID: 'COQU_04c85e49-f28c-49e4-a569-ca94d76d0806',
        },
        {
          miAnswer: '2',
          miID: 'COQU_a46ad234-5fbe-4d5f-a64b-53a7254be861',
        },
      ],
    };

    expect(parseAnswersObj(mockData)).toEqual(mockResponse);
  });
});

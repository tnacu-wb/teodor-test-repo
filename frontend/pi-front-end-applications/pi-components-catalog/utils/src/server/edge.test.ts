import { BUSINESS_BOOKER_USER_ROLES, Channel } from '@whitbread-eos/api';

import {
  getChannelByToken,
  getDetailsFromToken,
  getRandomTracingId,
  TOKEN_COMPANY_FIELD,
  TOKEN_CUSTOMER_FIELD,
  TOKEN_EMAIL_FIELD,
  TOKEN_EMPLOYEE_FIELD,
} from './edge';

jest.mock('./getters');

jest.mock('crypto', () => ({
  ...jest.requireActual('crypto'),
  randomBytes: () => 'testrandom',
}));

// Mock React's cache function which is not available in Jest environment
jest.mock('react', () => ({
  ...jest.requireActual('react'),
  cache: (fn: any) => fn,
}));

jest.mock('nanoid', () => ({
  nanoid: () => 'id',
}));

jest.mock('../utils/decodeIdToken', () => (token: any) => token);

describe('getDetailsFromToken', () => {
  const mockTokenBase = {
    [TOKEN_COMPANY_FIELD]: 'COMPANY_ID',
    [TOKEN_EMPLOYEE_FIELD]: 'EMPLOYEE_ID',
    [TOKEN_EMAIL_FIELD]: 'EMAIL',
    profile: {
      accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
    },
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return correct details for SUPER access level', () => {
    const token = { ...mockTokenBase, profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER } };
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: 'COMPANY_ID',
      employeeId: 'EMPLOYEE_ID',
      customerId: undefined,
      email: 'EMAIL',
      profile: {
        accessLevel: 'SUPER',
      },
      accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
      isTravelManager: true,
      isBooker: false,
      isSelfBooker: false,
      isGuest: false,
      isBusinessPayManager: false,
      isBusinessPayUser: false,
    });
  });

  it('should return correct details for BOOKER access level', () => {
    const token = {
      ...mockTokenBase,
      profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.BOOKER },
    };
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: 'COMPANY_ID',
      employeeId: 'EMPLOYEE_ID',
      customerId: undefined,
      email: 'EMAIL',
      profile: {
        accessLevel: 'BOOKER',
      },
      accessLevel: BUSINESS_BOOKER_USER_ROLES.BOOKER,
      isTravelManager: false,
      isBooker: true,
      isSelfBooker: false,
      isGuest: false,
      isBusinessPayManager: false,
      isBusinessPayUser: false,
    });
  });

  it('should return correct details for SELF access level', () => {
    const token = { ...mockTokenBase, profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.SELF } };
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: 'COMPANY_ID',
      employeeId: 'EMPLOYEE_ID',
      customerId: undefined,
      email: 'EMAIL',
      profile: {
        accessLevel: 'SELF',
      },
      accessLevel: BUSINESS_BOOKER_USER_ROLES.SELF,
      isTravelManager: false,
      isBooker: false,
      isSelfBooker: true,
      isGuest: false,
      isBusinessPayManager: false,
      isBusinessPayUser: false,
    });
  });

  it('should return correct details for STAYER access level', () => {
    const token = {
      ...mockTokenBase,
      profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.STAYER },
    };
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: 'COMPANY_ID',
      employeeId: 'EMPLOYEE_ID',
      customerId: undefined,
      email: 'EMAIL',
      profile: {
        accessLevel: 'STAYER',
      },
      accessLevel: BUSINESS_BOOKER_USER_ROLES.STAYER,
      isTravelManager: false,
      isBooker: false,
      isSelfBooker: false,
      isGuest: true,
      isBusinessPayManager: false,
      isBusinessPayUser: false,
    });
  });

  it('should return correct details for BUSINESS_PAY_MANAGER access level', () => {
    const token = {
      ...mockTokenBase,
      profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER },
    };
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: 'COMPANY_ID',
      employeeId: 'EMPLOYEE_ID',
      customerId: undefined,
      email: 'EMAIL',
      profile: {
        accessLevel: 'BUSINESS_PAY_MANAGER',
      },
      accessLevel: BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER,
      isTravelManager: false,
      isBooker: false,
      isSelfBooker: false,
      isGuest: false,
      isBusinessPayManager: true,
      isBusinessPayUser: false,
    });
  });

  it('should return correct details for BUSINESS_PAY_USER access level', () => {
    const token = {
      ...mockTokenBase,
      profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER },
    };
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: 'COMPANY_ID',
      employeeId: 'EMPLOYEE_ID',
      customerId: undefined,
      email: 'EMAIL',
      profile: {
        accessLevel: 'BUSINESS_PAY_USER',
      },
      accessLevel: BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER,
      isTravelManager: false,
      isBooker: false,
      isSelfBooker: false,
      isGuest: false,
      isBusinessPayManager: false,
      isBusinessPayUser: true,
    });
  });

  it('should handle missing fields gracefully', () => {
    const token = {};
    const result = getDetailsFromToken(token as any);
    expect(result).toEqual({
      companyId: undefined,
      employeeId: undefined,
      customerId: undefined,
      email: undefined,
      profile: {},
      accessLevel: undefined,
      isTravelManager: false,
      isBooker: false,
      isSelfBooker: false,
      isGuest: false,
      isBusinessPayManager: false,
      isBusinessPayUser: false,
    });
  });

  it('should extract customerId when present', () => {
    const token = {
      ...mockTokenBase,
      [TOKEN_CUSTOMER_FIELD]: 'CUSTOMER_ID',
      profile: { accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER },
    };
    const result = getDetailsFromToken(token as any);
    expect(result.customerId).toBe('CUSTOMER_ID');
  });
});

describe('getChannelByToken', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should return null when token is empty string', () => {
    const result = getChannelByToken('' as any);
    expect(result).toBeNull();
  });

  it('should return null when token is falsy (undefined)', () => {
    const result = getChannelByToken(undefined as any);
    expect(result).toBeNull();
  });

  it('should return Channel.Bb for a business booker token with isBusiness flag', () => {
    const token = {
      [TOKEN_COMPANY_FIELD]: 'COMPANY_ID',
      [TOKEN_EMPLOYEE_FIELD]: 'EMPLOYEE_ID',
      profile: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        isBusiness: true,
      },
    };
    const result = getChannelByToken(token as any);
    expect(result).toBe(Channel.Bb);
  });

  it('should return Channel.Pi for a PI customer token (customerId, no isBusiness, no accessLevel)', () => {
    const token = {
      [TOKEN_CUSTOMER_FIELD]: 'CUSTOMER_ID',
      profile: {},
    };
    const result = getChannelByToken(token as any);
    expect(result).toBe(Channel.Pi);
  });

  it('should return null when customerId present but isBusiness is true', () => {
    const token = {
      [TOKEN_CUSTOMER_FIELD]: 'CUSTOMER_ID',
      profile: {
        isBusiness: true,
      },
    };
    const result = getChannelByToken(token as any);
    expect(result).toBeNull();
  });

  it('should return null when customerId present but accessLevel is set', () => {
    const token = {
      [TOKEN_CUSTOMER_FIELD]: 'CUSTOMER_ID',
      profile: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        isBusiness: false,
      },
    };
    const result = getChannelByToken(token as any);
    expect(result).toBeNull();
  });

  it('should return null when BB token missing companyId', () => {
    const token = {
      [TOKEN_EMPLOYEE_FIELD]: 'EMPLOYEE_ID',
      profile: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        isBusiness: true,
      },
    };
    const result = getChannelByToken(token as any);
    expect(result).toBeNull();
  });

  it('should return null when BB token missing employeeId', () => {
    const token = {
      [TOKEN_COMPANY_FIELD]: 'COMPANY_ID',
      profile: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        isBusiness: true,
      },
    };
    const result = getChannelByToken(token as any);
    expect(result).toBeNull();
  });

  it('should return null when BB token has isBusiness false', () => {
    const token = {
      [TOKEN_COMPANY_FIELD]: 'COMPANY_ID',
      [TOKEN_EMPLOYEE_FIELD]: 'EMPLOYEE_ID',
      profile: {
        accessLevel: BUSINESS_BOOKER_USER_ROLES.SUPER,
        isBusiness: false,
      },
    };
    const result = getChannelByToken(token as any);
    expect(result).toBeNull();
  });

  it('should return null for an empty token object', () => {
    const token = { profile: {} };
    const result = getChannelByToken(token as any);
    expect(result).toBeNull();
  });
});

describe('getRandomTracingId', () => {
  it('should return the mocked nanoid value', () => {
    const result = getRandomTracingId();
    expect(result).toBe('id');
  });
});

describe('exported constants', () => {
  it('should export TOKEN_COMPANY_FIELD', () => {
    expect(TOKEN_COMPANY_FIELD).toBe('https://premierinn.com/companyAccountId');
  });

  it('should export TOKEN_EMPLOYEE_FIELD', () => {
    expect(TOKEN_EMPLOYEE_FIELD).toBe('https://premierinn.com/employeeAccountId');
  });

  it('should export TOKEN_EMAIL_FIELD', () => {
    expect(TOKEN_EMAIL_FIELD).toBe('https://premierinn.com/email');
  });

  it('should export TOKEN_CUSTOMER_FIELD', () => {
    expect(TOKEN_CUSTOMER_FIELD).toBe('https://premierinn.com/customerAccountId');
  });
});

import { BUSINESS_BOOKER_USER_ROLES } from '@whitbread-eos/api';
import { cookies } from 'next/headers';

import {
  getCardManagementLabels,
  getFooterLabels,
  getInitials,
  getInnBusinessHeaderLabels,
  getUserManagementLabels,
  getProfileManagementLabels,
  getCompanyManagementLabels,
  getHomepageLabels,
  getSpendingLabels,
  getPayApplicationLabels,
  getAuthLabels,
  getNotificationsLabels,
  getInnBusinessLayoutLabels,
  getContactUsLabels,
} from './labels';

const mockFetchResponse = {
  data: {},
};

const mockOkStatus = { value: true };

global.fetch = jest.fn(() =>
  Promise.resolve({
    json: () => Promise.resolve(mockFetchResponse),
    ok: mockOkStatus.value,
    headers: new Headers(),
    redirected: false,
    status: mockOkStatus.value ? 200 : 500,
    statusText: mockOkStatus.value ? 'OK' : 'Internal Server Error',
    type: 'basic',
    url: 'http://test.com',
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
describe('Server labels getters', () => {
  it('should call getFooterLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      footer: { value: 'footerValues' },
    };
    const result = await getFooterLabels('en');
    expect(result.value).toEqual('footerValues');
  });

  it('should call getFooterLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      footer: { value: 'footerValues' },
    };
    const result = await getFooterLabels('de');
    expect(result.value).toEqual('footerValues');
  });

  it('should call getInnBusinessHeaderLabels with DE', async () => {
    mockFetchResponse.data = {
      headerInformation: { innBusinessHeader: { value: 'getInnBusinessHeaderLabelsDE' } },
    };
    const result = await getInnBusinessHeaderLabels('de');
    expect(result.value).toEqual('getInnBusinessHeaderLabelsDE');
  });

  it('should call getInnBusinessHeaderLabels', async () => {
    mockFetchResponse.data = {
      headerInformation: { innBusinessHeader: { value: 'getInnBusinessHeaderLabels' } },
    };
    const result = await getInnBusinessHeaderLabels('en');
    expect(result.value).toEqual('getInnBusinessHeaderLabels');
  });

  it('should call getInnBusinessHeaderLabels and return null', async () => {
    mockOkStatus.value = false;
    const result = await getInnBusinessHeaderLabels('en');
    expect(result).toEqual(null);
  });

  it('should call getInnBusinessHeaderLabels with data undefined', async () => {
    mockFetchResponse.data = undefined;
    const result = await getInnBusinessHeaderLabels('en');
    expect(result).toEqual(null);
  });

  it('should call getCardManagementLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { cardManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getCardManagementLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getInnBLayoutLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { layoutEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getInnBusinessLayoutLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getUserManagementLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { userManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getUserManagementLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getUserManagementLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { userManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getUserManagementLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getProfileManagementLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { profileManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getProfileManagementLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getProfileManagementLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { profileManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getProfileManagementLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getCompanyManagementLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { companyManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getCompanyManagementLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getCompanyManagementLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { companyManagementEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getCompanyManagementLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getHomepageLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { homepageEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getHomepageLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getHomepageLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { homepageEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getHomepageLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getSpendingLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { spendingReportingEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getSpendingLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getSpendingLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { spendingReportingEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getSpendingLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getPayApplicationLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { payApplicationEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getPayApplicationLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getPayApplicationLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { payApplicationEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getPayApplicationLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getInitials', () => {
    const result = getInitials('John', 'Smith');
    expect(result).toEqual('JS');
  });

  it('should call getInitials when firstName is null', () => {
    const result = getInitials(null, 'Smith');
    expect(result).toEqual('S');
  });

  it('should call getInitials when lastName is null', () => {
    const result = getInitials('John', null);
    expect(result).toEqual('J');
  });

  it('should call getAuthLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { authEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getAuthLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getAuthLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { authEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getAuthLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getAuthLabels and return null when fetch fails', async () => {
    mockOkStatus.value = false;
    const result = await getAuthLabels('en');
    expect(result).toEqual(null);
  });

  it('should call getAuthLabels and return null when data is undefined', async () => {
    mockFetchResponse.data = undefined;
    const result = await getAuthLabels('en');
    expect(result).toEqual(null);
  });

  it('should call getNotificationsLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { notificationsEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getNotificationsLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getNotificationsLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { notificationsEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getNotificationsLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getContactUsLabels', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { innbContactUsEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getContactUsLabels('en');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getContactUsLabels for DE', async () => {
    mockOkStatus.value = true;
    mockFetchResponse.data = {
      getPageData: { innbContactUsEndpoint: JSON.stringify({ label: 'Test label' }) },
    };
    const result = await getContactUsLabels('de');
    expect(result).toEqual({ label: 'Test label' });
  });

  it('should call getContactUsLabels and return null when fetch fails', async () => {
    mockOkStatus.value = false;
    const result = await getContactUsLabels('en');
    expect(result).toEqual(null);
  });
});

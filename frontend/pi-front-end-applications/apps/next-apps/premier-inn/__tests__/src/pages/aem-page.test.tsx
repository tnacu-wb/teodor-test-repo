import { decodeIdToken } from '@whitbread-eos/utils';

import AEMPage, { getServerSideProps } from '~pages/aem-page';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('@whitbread-eos/organisms', () => ({
  PISearchContainer: () => <div data-testid="SearchContainer"></div>,
}));

// Mock the router
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    router: {},
    query: { page: '' },
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    testFeature: true,
  })),
  decodeIdToken: jest.fn(),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();

// Mocking cookies to return what we want
jest.mock('cookies', () => {
  return function () {
    return { get: mockGetCookie, set: mockSetCookie };
  };
});

describe('AEM page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should match the snapshot with a given pageLayout', () => {
    const { container } = render(<AEMPage pageLayout="www.google.com" />);
    expect(container).toMatchSnapshot();
  });

  it('should match the snapshot with pageLayout as false', () => {
    const { container } = render(<AEMPage pageLayout={false} />);
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale and no idTokenCookie', async () => {
    // No cookie returned
    mockGetCookie.mockReturnValue(undefined);

    const serverSideResponse = await getServerSideProps({ locale: 'gb' } as any);
    expect(serverSideResponse).toMatchSnapshot();
    // decodeIdToken not called because no token
    expect(decodeIdToken).not.toHaveBeenCalled();
  });

  it('should execute getServerSideProps with gb locale and a page query', async () => {
    mockGetCookie.mockReturnValue(undefined);

    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { page: 'www.google.com' },
    } as any);
    expect(serverSideResponse).toMatchSnapshot();
    // decodeIdToken not called because no token
    expect(decodeIdToken).not.toHaveBeenCalled();
  });
});

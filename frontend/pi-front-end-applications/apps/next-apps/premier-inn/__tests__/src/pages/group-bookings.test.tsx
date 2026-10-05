import React from 'react';

import GroupBookingsPage, { getServerSideProps } from '~pages/group-bookings';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/group-bookings', () => ({
  Page: () => <div data-testid="MockPage" />,
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  SecondaryHDPLayout: ({ children }: any) => <div data-testid="SecondaryHDPLayout">{children}</div>,
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
  getUnleashToggles: jest.fn().mockResolvedValue({}),
  useFeatureToggle: jest.fn(),
  QueriesLogger: jest.fn().mockImplementation(() => ({
    prefetchQuery: jest.fn().mockImplementation((queryKey, queryFn) => {
      queryFn();
      return Promise.resolve({
        hotels: {},
      });
    }),
    logQueries: jest.fn(),
  })),
  axiosRequest: jest.fn().mockImplementation(() => Promise.resolve({ data: {} })),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

jest.mock('cookies', () => {
  return function () {
    return {
      get: (key: string) => {
        if (key === 'id_token_cookie') return 'mockToken';
        if (key === 'WB-SESSION-ID') return 'mock-session-id';
        return key;
      },
    };
  };
});

describe('Group Bookings Page', () => {
  it('should match the snapshot', () => {
    const { container } = render(GroupBookingsPage.getLayout(<GroupBookingsPage />));
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'GetStaticContent' },
    });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({
      locale: 'de',
    });
    expect(serverSideResponse).toMatchSnapshot();
  });
});

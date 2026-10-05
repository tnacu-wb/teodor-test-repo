import { useFeatureToggle, getUnleashToggles } from '@whitbread-eos/utils';

import AmendDetailsPage, { getServerSideProps } from '~pages/amend/details';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('@auth0/nextjs-auth0', () => ({
  getSession: jest.fn(),
  withPageAuthRequired: ({ getServerSideProps }: any) => {
    return async (ctx: any) =>
      getServerSideProps({
        locale: 'gb',
        query: { key: 'test' },
        res: {
          getHeader: jest.fn(),
          setHeader: jest.fn(),
        },
        req: { headers: { host: '', connection: '' } },
      });
  },
}));

jest.mock('~page-helper/amend/details', () => ({
  createAmendPiDataLoaderFn: jest.fn(async () => ({
    pcksQueryInput: {
      endDate: '',
      basketReferenceId: '',
      nightsNumber: 1,
      childrenNumber: 1,
      startDate: '',
      country: '',
      hotelId: '',
      language: '',
      bookingFlowId: '',
      adultsNumber: 1,
    },
    confirmationInput: {
      basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
      bookingReference: 'AWM8159458',
      country: 'gb',
      language: 'en',
    },
  })),
  Page: () => <div data-testid="DetailsPage" />,
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
  getUnleashToggles: jest.fn(() =>
    Promise.resolve({
      testFeature: true,
    })
  ),
  logger: () => ({
    info: jest.fn(),
    error: jest.fn(),
    warn: jest.fn(),
    debug: jest.fn(),
  }),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('~components', () => ({
  DefaultLayout: ({ children }: any) => <div data-testid="DefaultLayout">{children}</div>,
}));

describe('Amend Details page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      AmendDetailsPage.getLayout(
        <AmendDetailsPage
          pcksQueryInput={{
            endDate: '',
            basketReferenceId: '',
            nightsNumber: 1,
            childrenNumber: 1,
            startDate: '',
            country: '',
            hotelId: '',
            language: '',
            bookingFlowId: '',
            adultsNumber: 1,
          }}
          confirmationInput={{
            basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
            bookingReference: 'AWM8159458',
            country: 'gb',
            language: 'en',
          }}
          featureToggles={{ testFeature: true }}
        />
      )
    );
    expect(useFeatureToggle).toBeCalledWith({ testFeature: true });
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    const mockContext = {
      locale: 'gb',
      query: {
        bookingReference: 'AWM8159458',
        basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
      },
      req: {
        headers: { host: 'localhost:3000', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/amend/details',
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    const mockContext = {
      locale: 'de',
      query: {
        bookingReference: 'AWM8159458',
        basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
      },
      req: {
        headers: { host: 'localhost:3000', connection: 'keep-alive' },
        cookies: {},
      },
      res: {
        getHeader: jest.fn(),
        setHeader: jest.fn(),
      },
      resolvedUrl: '/amend/details',
    };
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps(mockContext);
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });
});

import { Channel, BOOKING_SUBCHANNEL } from '@whitbread-eos/api';
import { useFeatureToggle, getUnleashToggles } from '@whitbread-eos/utils';

import GuestDetailsPage, { getServerSideProps } from '~pages/guest-details';
import { mockGetI18NLabels, mockGetProxyOptions, mockGetSession, render } from '~utils/test-utils';
import { auth0 } from '../../../src/lib/auth0';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('../../../src/lib/auth0', () => ({
  auth0: {
    getSession: jest.fn(),
  },
}));

jest.mock('~page-helper/guest-details', () => ({
  createGuestDetailsCcuiDataLoaderFn: () => ({}),
  Page: () => <div data-testid="MockPage" />,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  ErrorBoundary: ({ children }: any) => <div data-testid="ErrorBoundary">{children}</div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: () => mockServerSideCustomLocale,
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    feature1: true,
  })),
  getI18nLabels: jest.fn(() => mockGetI18NLabels()),
  getSession: jest.fn(() => mockGetSession()),
  getProxyOptions: jest.fn(() => mockGetProxyOptions()),
}));

jest.mock('~components', () => ({
  GuestDetailsLayout: ({ children }: any) => <div data-testid="GuestDetailsLayout">{children}</div>,
}));

describe('Guest Details page', () => {
  it('should match the snapshot', () => {
    const { container } = render(
      GuestDetailsPage.getLayout(
        <GuestDetailsPage
          biQueryInput={{
            language: '',
            country: '',
            basketReference: '',
            bookingChannelCriteria: {
              language: 'EN',
              channel: Channel.Pi,
              subchannel: BOOKING_SUBCHANNEL.WEB,
            },
          }}
          pcksQueryInput={{
            language: '',
            hotelId: '',
            country: '',
            adultsNumber: 1,
            basketReferenceId: '',
            bookingFlowId: '',
            childrenNumber: 1,
            nightsNumber: 1,
            endDate: '',
            startDate: '',
          }}
          hiQueryInput={{ language: 'en', country: 'gb', hotelId: '' }}
          featureToggles={{ feature1: true }}
        />
      )
    );
    expect(useFeatureToggle).toBeCalledWith({ feature1: true });
    expect(container).toMatchSnapshot();
  });
  it('should execute getServerSideProps with gb locale', async () => {
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());
    const serverSideResponse = await getServerSideProps({
      locale: 'gb',
      query: { key: 'test' },
      res: {},
      req: { headers: { host: '', connection: '' } },
    } as any);
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    (auth0.getSession as jest.Mock).mockReturnValue(mockGetSession());
  const serverSideResponse = await getServerSideProps({
    locale: 'de',
    query: { key: 'test' },
    res: {},
    req: { headers: { host: '', connection: '' } },
  } as any);
    expect(getUnleashToggles).toBeCalled();
    expect(serverSideResponse).toMatchSnapshot();
  });
});

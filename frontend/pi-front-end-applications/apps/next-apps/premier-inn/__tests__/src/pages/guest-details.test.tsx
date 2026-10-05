import { Channel, BOOKING_SUBCHANNEL, FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';
import { GetServerSidePropsContext } from 'next';
import React from 'react';

import GuestDetailsPage, { getServerSideProps } from '~pages/guest-details';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/guest-details', () => ({
  createGuestDetailsPiDataLoaderFn: () => ({}),
  Page: () => <div data-testid="MockPage" />,
}));

const mockGetAuth0TokenAndEmail = jest.fn().mockResolvedValue({ accessToken: null, email: null });
jest.mock('../../../src/lib/getAuth0Token', () => ({
  getAuth0TokenAndEmail: (...args: any[]) => mockGetAuth0TokenAndEmail(...args),
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
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  useFeatureToggle: jest.fn(),
  getUnleashToggles: jest.fn(() => ({
    release_pi_bb_account_serv_2_serv: true,
    release_pi_bb_ccui_get_basket_details_redis: true,
  })),
  GLOBALS: {
    locale: {
      GB: 'gb',
      DE: 'de',
    },
  },
}));

jest.mock('~components', () => ({
  GuestDetailsLayout: ({ children }: any) => <div data-testid="GuestDetailsLayout">{children}</div>,
}));

describe('Guest Details page', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

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
          featureToggles={{ release_pi_bb_account_serv_2_serv: true }}
          query={{
            reservationId: '12',
            BRAND: 'PI',
          }}
        />
      )
    );
    expect(useFeatureToggle).toBeCalledWith({ release_pi_bb_account_serv_2_serv: true });
    expect(container).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    const context = {
      req: {},
      res: {},
      query: {
        reservationId: '12345',
      },
      locale: 'gb',
    } as unknown as GetServerSidePropsContext;

    const serverSideResponse = await getServerSideProps(context);
    expect(serverSideResponse).toEqual(
      expect.objectContaining({
        props: expect.any(Object),
      })
    );
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with gb locale', async () => {
    const context = {
      req: {},
      res: {},
      query: {
        reservationId: '12345',
      },
      locale: 'gb',
    } as unknown as GetServerSidePropsContext;
    const serverSideResponse = await getServerSideProps(context);

    expect(serverSideResponse).toEqual(
      expect.objectContaining({
        props: expect.any(Object),
      })
    );
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    const { getUnleashToggles } = require('@whitbread-eos/utils');

    (getUnleashToggles as jest.Mock).mockImplementationOnce(() => ({
      release_pi_bb_account_serv_2_serv: true,
      release_pi_bb_ccui_get_basket_details_redis: false,
    }));

    const context = {
      req: {},
      res: {},
      query: {
        reservationId: '12345',
      },
      locale: 'de',
    } as unknown as GetServerSidePropsContext;

    const serverSideResponse = await getServerSideProps(context);

    expect(serverSideResponse).toEqual(
      expect.objectContaining({
        props: expect.any(Object),
      })
    );
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should fetch Auth0 token and email when FT_PI_AUTH0_LOGIN is enabled', async () => {
    const { getUnleashToggles } = require('@whitbread-eos/utils');
    (getUnleashToggles as jest.Mock).mockResolvedValue({
      [FT_PI_AUTH0_LOGIN]: true,
      release_pi_bb_ccui_get_basket_details_redis: false,
    });
    mockGetAuth0TokenAndEmail.mockResolvedValueOnce({
      accessToken: 'mock-token',
      email: 'test@example.com',
    });

    const context = {
      req: {},
      res: {},
      query: { reservationId: '12345' },
      locale: 'gb',
    } as unknown as GetServerSidePropsContext;

    const serverSideResponse = await getServerSideProps(context);

    expect(mockGetAuth0TokenAndEmail).toHaveBeenCalled();
    expect(serverSideResponse).toEqual(expect.objectContaining({ props: expect.any(Object) }));
  });

  it('should skip Auth0 token fetch when FT_PI_AUTH0_LOGIN is disabled', async () => {
    const { getUnleashToggles } = require('@whitbread-eos/utils');
    (getUnleashToggles as jest.Mock).mockResolvedValue({
      [FT_PI_AUTH0_LOGIN]: false,
      release_pi_bb_ccui_get_basket_details_redis: false,
    });

    const context = {
      req: {},
      res: {},
      query: { reservationId: '12345' },
      locale: 'gb',
    } as unknown as GetServerSidePropsContext;

    await getServerSideProps(context);

    expect(mockGetAuth0TokenAndEmail).not.toHaveBeenCalled();
  });
});

import React from 'react';

import ChooseRoomTypePage, { getServerSideProps } from '~pages/hotels/choose-roomtype';
import { render } from '~utils/test-utils';

const mockServerSideCustomLocale = { language: 'gb', country: '' };

jest.mock('~page-helper/hotel-details/choose-roomtype', () => ({
  createChooseRoomTypePiDataLoaderFn: () => ({}),
  ChooseRoomTypePagePI: () => <div data-testid="ChooseRoomTypePage" />,
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
  getUnleashToggles: jest.fn(() => ({
    release_pi_bb_account_serv_2_serv: true,
  })),
}));

describe('Choose your Room type page', () => {
  it('should match the snapshot', () => {
    const { container } = render(ChooseRoomTypePage.getLayout(<ChooseRoomTypePage />));
    expect(container).toMatchSnapshot();
  });
  it('should execute getServerSideProps with gb locale', async () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'gb' });
    expect(serverSideResponse).toMatchSnapshot();
  });

  it('should execute getServerSideProps with de locale', async () => {
    mockServerSideCustomLocale.language = 'de';
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    //@ts-ignore
    const serverSideResponse = await getServerSideProps({ locale: 'de' });
    expect(serverSideResponse).toMatchSnapshot();
  });
});

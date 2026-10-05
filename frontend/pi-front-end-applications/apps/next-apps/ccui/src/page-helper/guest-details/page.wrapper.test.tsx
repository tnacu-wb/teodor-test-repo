// eslint-disable-next-line @typescript-eslint/ban-ts-comment
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import * as React from 'react';

import { render } from '~utils/test-utils';

import Page from './page.wrapper';

const queryClient = new ReactQuery.QueryClient();

jest.mock('@whitbread-eos/utils', () => ({
  logger: {
    info: jest.fn(),
  },
}));

jest.mock('./page.ccui', () => {
  const mockCCUISearchContainer = () => {
    return <div data-testid="PAGE-CCUI">CCUI Search Container</div>;
  };
  return mockCCUISearchContainer;
});

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  SEO: () => <div />,
}));

const mockObj = {
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
    bookingChannelCriteria: {
      language: 'EN',
      channel: 'CCUI' as any,
      subchannel: 'WEB',
    },
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 1,
    basketReferenceId: '12',
    bookingFlowId: 'booking-hub',
    childrenNumber: 0,
    country: 'GB',
    endDate: '2023-02-24',
    hotelId: 'LONKIN',
    language: 'en',
    nightsNumber: 1,
    startDate: '2023-02-23',
  },
};

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockRouter = {
  push: jest.fn(),
  query: {
    ARRdd: '1',
    ARRmm: '11',
    ARRyyyy: '2025',
    NIGHTS: '2',
    ROOMS: '1',
    ADULT1: '1',
    CHILD1: '0',
    COT1: '0',
    INTTYP1: 'DB',
    slug: ['england', 'greater-london', 'london', 'london-beckton.html'],
  },
  asPath:
    '/en/hotels/england/greater-london/london/london-beckton.html?ARRdd=1&ARRmm=11&ARRyyyy=2025&NIGHTS=2&ROOMS=1&ADULT1=1&CHILD1=0&COT1=0&INTTYP1=DB',
};

describe('Page', () => {
  beforeAll(() => {
    mockUseRouter.mockReturnValue(mockRouter);
  });

  it('should render CCUI Page', async () => {
    const { getByTestId } = render(
      <Page {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByTestId('PAGE-CCUI')).toBeInTheDocument();
  });
});

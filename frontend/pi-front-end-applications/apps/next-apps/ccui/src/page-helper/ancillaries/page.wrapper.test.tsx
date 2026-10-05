// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore-file
import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Channel } from '@whitbread-eos/api';

import Page from './page.wrapper';

//region Mock Objects
const mockObj = {
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
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
    bookingChannelCriteria: {
      channel: Channel.Ccui,
      subchannel: '',
    },
  },
  setAnalyticsUser: jest.fn(),
};
const mockRouter = jest.fn();
const queryClient = new ReactQuery.QueryClient();
//endregion

//region Jest Mock
jest.mock('./page.ccui', () => {
  const MockComponentCCUI = () => <div data-testid="PAGE-CCUI">Page CCUI</div>;
  return MockComponentCCUI;
});

jest.mock('@whitbread-eos/utils', () => ({
  logger: {
    info: jest.fn(),
  },
}));
//endregion

//region Unit Tests
describe('Page', () => {
  it('should render CCUI Page', async () => {
    const { getByTestId } = render(
      <Page {...mockObj} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByTestId('PAGE-CCUI')).toBeInTheDocument();
  });
});
//endregion

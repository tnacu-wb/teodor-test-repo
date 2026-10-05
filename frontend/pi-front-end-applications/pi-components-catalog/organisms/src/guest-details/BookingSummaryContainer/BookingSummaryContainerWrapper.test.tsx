import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import BookingSummaryContainerWrapper from './BookingSummaryContainerWrapper.component';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));
const Component = (variant: 'mobile' | 'desktop' | undefined) => {
  const props = {
    queryClient: new QueryClient(),
    packages: '',
    bkngData: {
      bookingInformation: {
        hotelId: 'MANOLD',
        totalCost: 59,
        currencyCode: 'GBP',
        bookingFlowId: 'booking-a1',
        infoMessages: [
          '<p>Pay now or on arrival, fully refundable with free cancellation up to 1pm on the day of arrival</p>\n',
        ],
        reservationByIdList: [
          {
            roomStay: {
              adultsNumber: 1,
              childrenNumber: 0,
              arrivalDate: '2022-08-29',
              departureDate: '2022-08-30',
              ratePlanCode: 'STANDARD',
              rateExtraInfo: { rateName: 'standard' },
              roomExtraInfo: { roomType: 'Double', roomName: 'Premier Plus double' },
              accessibleRoom: { isAccessible: false, phoneNumber: '0333 321 1315' },
            },
          },
        ],
        upgradeToFlex: { amount: 10, currency: 'EUR', flexRateCode: 'GB' },
      },
    },
    hiData: '',
    basketReferenceId: '',
    taxesMessage: '',
    t: () => {
      return 'default';
    },
    language: 'en',
    biQueryInput: '',
  };

  return (
    <QueryClientProvider client={new QueryClient()}>
      <BookingSummaryContainerWrapper {...props} variant={variant} />
    </QueryClientProvider>
  );
};

describe('BookingSummartContainerWrapper ', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
    });
  });

  it('should render BookingSummartContainerWrapper ', function () {
    const { queryByTestId } = render(<Component />);
    expect(queryByTestId('BookingSummaryContainer')).toBeTruthy();
  });
});

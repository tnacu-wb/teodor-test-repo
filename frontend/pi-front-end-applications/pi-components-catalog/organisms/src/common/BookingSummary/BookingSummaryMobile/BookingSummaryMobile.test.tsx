import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../../utils/test-utils';
import type { Props } from './BookingSummaryMobile';
import BookingSummaryMobile from './BookingSummaryMobile';

const mockProps: Props = {
  prefixDataTestId: 'BookingSummary',
  reservationDetails: {
    currency: 'EUR',
    noRooms: 1,
    noNights: 1,
    arrivalDate: '2022-10-01',
    departureDate: '2022-10-02',
  },
  bookingSummaryData: {
    hotelInformation: { hotelName: 'hotelNameText', hotelAddress: ['addr1'] },
    totalCost: {
      initialTotalCost: 0,
      amount: 0,
      currency: 'EUR',
      showVATMessage: false,
      donations: 0,
      meals: { childrenMeals: [], adultsMeals: [] },
    },
    rateInformation: { rate: 'Standard', noRooms: 1, noNights: 1 },
  },
  totalCostAmount: 0,
  t: (key: string) => {
    switch (key) {
      case 'hoteldetails.bookingsummary.room':
        return 'hoteldetails.bookingsummary.room';
      case 'booking.summary.night':
        return 'booking.summary.night';
      case 'booking.summary.rate':
        return 'Rate:';
      default:
        return 'default';
    }
  },
  language: 'en',
};

describe('BookingSummaryMobile', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render a <BookingSummaryMobile> with default props and soft bundles', async function () {
    const { queryByTestId } = render(
      <BookingSummaryMobile {...mockProps} isSoftBundlesVisible={true} />
    );
    expect(queryByTestId('BookingSummary-SectionWrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-SectionHeader')).toBeTruthy();
  });

  it('should render a <BookingSummaryMobile> with default props', async function () {
    const { queryByTestId, getByTestId } = render(<BookingSummaryMobile {...mockProps} />);
    const expectedHeader =
      '1 hoteldetails.bookingsummary.room, 1 booking.summary.night | 01 Oct - 02 Oct | €0.00 | Rate: Standard';

    expect(queryByTestId('BookingSummary-SectionWrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-SectionHeader')).toBeTruthy();
    expect(getByTestId('BookingSummary-HeaderLine1Line').textContent).toBe(expectedHeader);

    const chevronIconDown = queryByTestId('isNotExpanded');
    expect(chevronIconDown).toBeTruthy();

    fireEvent.click(chevronIconDown as Element);
    await waitFor(() => {
      expect(queryByTestId('isExpanded')).toBeTruthy();
    });
  });

  it('should render a <BookingSummaryMobile> with noNight 2 and noRooms 2', function () {
    const expectedHeader = '2 default, 2 default | 01 Oct - 02 Oct | €0.00 | Rate: Standard';
    const { queryByTestId, getByTestId } = render(
      <BookingSummaryMobile
        {...mockProps}
        reservationDetails={{ ...mockProps.reservationDetails, noNights: 2, noRooms: 2 }}
      />
    );

    expect(queryByTestId('BookingSummary-SectionWrapper')).toBeTruthy();
    expect(getByTestId('BookingSummary-HeaderLine1Line').textContent).toBe(expectedHeader);
  });

  it('should render a <BookingSummaryMobile> without departure and arrival date', function () {
    const { getByTestId } = render(
      <BookingSummaryMobile
        {...mockProps}
        reservationDetails={{
          ...mockProps.reservationDetails,
          departureDate: '',
          arrivalDate: '',
        }}
      />
    );

    const expectedHeader =
      '1 hoteldetails.bookingsummary.room, 1 booking.summary.night |  -  | €0.00 | Rate: Standard';
    expect(getByTestId('BookingSummary-HeaderLine1Line').textContent).toBe(expectedHeader);
  });
});

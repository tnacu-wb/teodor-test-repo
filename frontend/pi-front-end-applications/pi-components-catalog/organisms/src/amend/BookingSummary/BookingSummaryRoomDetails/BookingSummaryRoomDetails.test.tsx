import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import { BookingSummaryRoomDetails } from './BookingSummaryRoomDetails';

const baseProps = {
  language: 'en',
  baseDataTestId: 'BookingSummaryRoomDetails',
  bookingSummaryLabels: {
    mealsLabel: 'Meals',
    extrasLabel: 'Extras',
  },
  roomsAndGuestsLabels: {
    roomLabel: 'Room',
    roomModalLabels: {
      roomAvailabilityLabels: {
        adult: 'adult',
        adults: 'adults',
        child: 'child',
        children: 'children',
      },
    },
  },
  reservation: {
    roomStay: {
      adultsNumber: 2,
      childrenNumber: 1,
      roomExtraInfo: { roomName: 'Double Room' },
      roomPrice: 120,
    },
    reservationGuestList: [{ nameTitle: 'Mr', givenName: 'John', surName: 'Doe' }],
  },
  roomNumber: 0,
  currency: 'GBP',
  roomPackages: {
    selectedMeals: {
      adultsMeals: [],
      childrenMeals: [],
    },
    selectedExtrasList: {
      packagesSelection: [
        {
          id: 'DBPROS',
          noOfSelections: 1,
        },
        {
          id: 'FI24HR',
          noOfSelections: 1,
        },
      ],
    },
  },
  noNights: 1,
  extrasItemsPrices: {
    bOfProseccoPrice: 20,
    wifiPrice: 10,
  },
};
describe('BookingSummaryRoomDetails', () => {
  it('renders Prosecco in extras with correct price', () => {
    const { getByTestId } = render(<BookingSummaryRoomDetails {...baseProps} />);
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-DBPROS')).toHaveTextContent(
      'ancillaries.extras.DBPROS.name'
    );
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-price-DBPROS')).toHaveTextContent(
      '£20.00'
    );
  });

  it('renders WIFI in extras with correct price', () => {
    const { getByTestId } = render(<BookingSummaryRoomDetails {...baseProps} />);
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-FI24HR')).toHaveTextContent(
      'ancillaries.extras.FI24HR.name'
    );
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-price-FI24HR')).toHaveTextContent(
      '£10.00'
    );
  });

  it('does not render Prosecco if not in packagesSelection', () => {
    const propsWithoutProsecco = {
      ...baseProps,
      roomPackages: {
        ...baseProps.roomPackages,
        selectedExtrasList: { packagesSelection: [] },
      },
    };
    const { queryByTestId } = render(<BookingSummaryRoomDetails {...propsWithoutProsecco} />);
    expect(queryByTestId('BookingSummaryRoomDetails-room-0-extras-DBPROS')).not.toBeInTheDocument();
  });

  it('renders other extras correctly alongside Prosecco', () => {
    const propsWithMultipleExtras = {
      ...baseProps,
      roomPackages: {
        ...baseProps.roomPackages,
        selectedExtrasList: {
          packagesSelection: [
            { id: 'HSCKIN', noOfSelections: 1 },
            { id: 'DBPROS', noOfSelections: 1 },
          ],
        },
      },
      extrasItemsPrices: {
        ...baseProps.extrasItemsPrices,
        eciPrice: 15,
      },
    };
    const { getByTestId } = render(<BookingSummaryRoomDetails {...propsWithMultipleExtras} />);
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-HSCKIN')).toHaveTextContent(
      'ancillaries.extras.HSCKIN.name'
    );
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-price-HSCKIN')).toHaveTextContent(
      '£15.00'
    );
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-DBPROS')).toHaveTextContent(
      'ancillaries.extras.DBPROS.name'
    );
    expect(getByTestId('BookingSummaryRoomDetails-room-0-extras-price-DBPROS')).toHaveTextContent(
      '£20.00'
    );
  });
});

import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import type { Props } from './BookingSummary.component';
import BookingSummary from './BookingSummary.component';

const mockProps: Props = {
  variant: 'mobile',
  reservationDetails: {
    currency: 'EUR',
    noRooms: 1,
    noNights: 2,
    arrivalDate: '2022-10-01',
    departureDate: '2022-10-03',
  },
  bookingSummaryData: {
    hotelInformation: { hotelName: 'hotelNameText', hotelAddress: ['addr1'] },
    rateInformation: {
      rate: 'Flex',
      noRooms: 1,
      noNights: 2,
      rateDescription: 'Pay Now, No changes',
    },
    roomInformation: [
      {
        accessibleRoom: {
          isAccessible: true,
          phoneNumber: '0333 321 3104',
        },
        nrAdults: 2,
        nrChildren: 1,
        roomType: 'Double',
        roomName: 'Double',
        selectedMeals: {
          adultsMeals: [
            {
              id: 'BFADBF',
              noSelections: 1,
              price: 9.5,
              title: 'Premier Inn Breakfast',
            },
          ],
          childrenMeals: [
            {
              id: 'BFCHDF',
              noSelections: 1,
              title: 'Free breakfast for kids',
            },
          ],
        },
      },
    ],
    totalCost: {
      initialTotalCost: 50,
      amount: 2000,
      currency: 'EUR',
      showVATMessage: false,
      donations: 0,
      meals: [
        {
          childrenMeals: [
            {
              id: 'BFCHDF',
              noSelections: 1,
              title: 'Free breakfast for kids',
            },
          ],
          adultsMeals: [
            {
              id: 'BFADBF',
              noSelections: 1,
              price: 9.5,
              title: 'Premier Inn Breakfast',
            },
          ],
        },
      ],
    },
    stayDatesInformation: {
      noNights: 2,
      arrivalDate: '2022-10-01',
      departureDate: '2022-10-03',
    },
  },
  t: () => {
    return 'default';
  },
  language: 'en',
  infoMessages: ['<p>Info Message</p>'],
  isExtrasDisplayed: true,
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('BookingSummaryHotelDetailsInfo', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render a <BookingSummary> with default props', function () {
    const { getByTestId } = render(<BookingSummary {...mockProps} />);

    expect(getByTestId('BookingSummary-MobileVariant-SectionWrapper')).toBeInTheDocument();
  });

  it('should render a <BookingSummary> with default props and soft bundles', function () {
    const { getByTestId } = render(<BookingSummary {...mockProps} isSoftBundlesVisible={true} />);

    expect(getByTestId('BookingSummary-MobileVariant-SectionWrapper')).toBeInTheDocument();
  });

  it('should render a <BookingSummary> with default props', function () {
    mockProps.variant = 'desktop';
    const { getByTestId } = render(<BookingSummary {...mockProps} />);
    expect(getByTestId('BookingSummary-DesktopVariant-Wrapper')).toBeInTheDocument();
  });
  it('should render a <BookingSummary> with desktop variant and no cost for meals', function () {
    mockProps.variant = 'desktop';
    const { getByTestId } = render(
      <BookingSummary
        {...mockProps}
        bookingSummaryData={{
          totalCost: {
            currency: 'EUR',
            showVATMessage: false,
            meals: [
              {
                childrenMeals: [
                  {
                    id: 'BFCHDF',
                    noSelections: 1,
                    title: 'Free breakfast for kids',
                  },
                ],
                adultsMeals: [
                  {
                    id: 'BFADBF',
                    noSelections: 1,
                    title: 'Premier Inn Breakfast',
                  },
                ],
              },
            ],
          },
        }}
      />
    );
    expect(getByTestId('BookingSummary-DesktopVariant-Wrapper')).toBeInTheDocument();
  });

  it('should render a <BookingSummary> with desktop variant and wihout total cost object', function () {
    mockProps.variant = 'desktop';
    const { getByTestId } = render(
      <BookingSummary
        {...mockProps}
        bookingSummaryData={{
          totalCost: {
            currency: 'EUR',
            showVATMessage: false,
            meals: undefined,
          },
        }}
      />
    );
    expect(getByTestId('BookingSummary-DesktopVariant-Wrapper')).toBeInTheDocument();
  });
  it('should render a <BookingSummary> with desktop variant and wihout total cost object', function () {
    mockProps.variant = 'desktop';
    const { getByTestId } = render(
      <BookingSummary
        {...mockProps}
        bookingSummaryData={{
          totalCost: undefined,
        }}
      />
    );
    expect(getByTestId('BookingSummary-DesktopVariant-Wrapper')).toBeInTheDocument();
  });

  it('should render a <BookingSummary> with desktop variant all fields correctly displayed', function () {
    mockProps.variant = 'desktop';
    const { getByTestId } = render(<BookingSummary {...mockProps} />);

    expect(
      getByTestId('BookingSummary-DesktopVariant-HotelInformation-HotelAddress')
    ).toHaveTextContent('addr1');
    expect(getByTestId('BookingSummary-DesktopVariant-TotalCost-CostAmount')).toHaveTextContent(
      '€69'
    );
    expect(getByTestId('BookingSummary-DesktopVariant-RateInformation-Label')).toHaveTextContent(
      'default: Flex'
    );
    expect(
      getByTestId('BookingSummary-DesktopVariant-RateInformation-RateDescription')
    ).toHaveTextContent('Pay Now, No changes');
    expect(
      getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-ArrivalDate')
    ).toHaveTextContent('Sat 01 Oct 2022');
    expect(
      getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-DepartureDate')
    ).toHaveTextContent('Mon 03 Oct 2022');
    expect(
      getByTestId('BookingSummary-DesktopVariant-StayDatesInformation-NightsNumber')
    ).toHaveTextContent('2 default');
    expect(
      getByTestId('BookingSummary-DesktopVariant-RoomInformation-RoomNumber')
    ).toHaveTextContent('default 1 (Double)');
    expect(
      getByTestId('BookingSummary-DesktopVariant-RoomInformation-AdultsNumber')
    ).toHaveTextContent('2 default');
    expect(
      getByTestId('BookingSummary-DesktopVariant-RoomInformation-ChildrenNumber')
    ).toHaveTextContent('1 default');
    expect(
      getByTestId('BookingSummary-DesktopVariant-RoomInformation-AdultMeal')
    ).toHaveTextContent('Premier Inn Breakfast default 1 default');
    expect(
      getByTestId('BookingSummary-DesktopVariant-RoomInformation-ChildrenMeal')
    ).toHaveTextContent('Free breakfast for kids default 1 default');
  });

  describe('Accessible Notification component', () => {
    it('should render <Notification> component if isAccessible is true', function () {
      const { queryByTestId } = render(<BookingSummary {...mockProps} />);
      expect(queryByTestId('Alert')).toBeTruthy();
    });

    it('should NOT render <Notification> component if isAccessible is false', function () {
      mockProps.bookingSummaryData.roomInformation[0].accessibleRoom.isAccessible = false;
      const { queryByTestId } = render(<BookingSummary {...mockProps} />);
      expect(queryByTestId('Alert')).toBeFalsy();
    });
  });
});

import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import type { Props } from './BookingSummaryCard';
import BookingSummaryCard from './BookingSummaryCard';

const mockProps: Props = {
  prefixDataTestId: 'BookingSummary',
  isCityTaxBreakdownEnabled: true,
  bookingSummaryData: {
    hotelInformation: { hotelName: 'hotelNameText', hotelAddress: ['addr1'] },
    totalCost: {
      initialTotalCost: 0,
      amount: 0,
      currency: 'EUR',
      showVATMessage: false,
      donations: 0,
      meals: [{ childrenMeals: [], adultsMeals: [] }],
    },
    showAutocompleteMealsNotification: true,
    stayDatesInformation: {
      arrivalDate: '2022-06-10',
      departureDate: '2022-06-11',
      noNights: 2,
    },
    cityTaxTotal: 10,
    rateInformation: { rate: '', noRooms: 1, noNights: 1, rateTags: [] as string[] },
    roomInformation: [
      {
        accessibleRoom: {
          isAccessible: true,
          phoneNumber: '0333 321 3104',
        },
        nrAdults: 2,
        nrChildren: 1,
        roomType: 'Double',
        roomName: 'roomName',
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
  },
  totalCostAmount: 0,
  t: () => {
    return 'default';
  },
  language: 'en',
  isExtrasDisplayed: true,
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const mockCookies = {
  bundles: 'false',
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    useFeatureToggle: () => ({
      release_pi_promo_code_landing_page: true,
      release_pi_display_soft_bundles: true,
    }),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
  };
});

describe('BookingSummaryCard', () => {
  it('should render a <BookingSummaryCard> with default props', function () {
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-HotelInformation-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-TotalCost-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-RateInformation-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-StayDatesInformation-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-PreselectedMealNotification')).toBeTruthy();
  });

  it('should render a <BookingSummaryCard> with default props and soft bundles', function () {
    const { queryByTestId } = render(
      <BookingSummaryCard {...mockProps} isSoftBundlesVisible={true} />
    );

    expect(queryByTestId('BookingSummary-Wrapper')).toBeTruthy();
  });

  it('should not render Preselected Meal Notification', function () {
    mockProps.bookingSummaryData.showAutocompleteMealsNotification = false;
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);
    expect(queryByTestId('BookingSummary-PreselectedMealNotification')).toBeFalsy();
  });

  it('should render <Notification> component if isAccessible is true', function () {
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);
    expect(queryByTestId('Alert')).toBeTruthy();
  });

  it('should NOT render <Notification> component if isAccessible is false', function () {
    mockProps.bookingSummaryData.roomInformation = [
      {
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 3104',
        },
        nrAdults: 2,
        nrChildren: 1,
        roomType: 'Double',
        roomName: 'roomName',
      },
    ];
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);
    expect(queryByTestId('Alert')).toBeFalsy();
  });

  it('should render a <BookingSummaryCard> without hotelInformation', function () {
    mockProps.bookingSummaryData.hotelInformation = null;
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-HotelInformation-Wrapper')).toBeFalsy();
  });

  it('should render a <BookingSummaryCard> without rateInformation', function () {
    mockProps.bookingSummaryData.rateInformation = null;
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-RateInformation-Wrapper')).toBeFalsy();
  });

  it('should render a <BookingSummaryCard> without totalCost', function () {
    mockProps.bookingSummaryData.totalCost = null;
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-TotalCost-Wrapper')).toBeFalsy();
  });
  it('should render a <BookingSummaryCard> without stay dates', function () {
    mockProps.bookingSummaryData.stayDatesInformation = null;
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-StayDatesInformation-Wrapper')).toBeFalsy();
  });

  it('should not render a Donations text when donation is 0', function () {
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-Donations')).toBeFalsy();
  });

  it('should render a Donations text when donation is selected', function () {
    mockProps.bookingSummaryData.totalCost = {
      initialTotalCost: 0,
      amount: 0,
      currency: 'EUR',
      showVATMessage: false,
      donations: 3,
      meals: [{ childrenMeals: [], adultsMeals: [] }],
    };
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);

    expect(queryByTestId('BookingSummary-Donations')).toBeTruthy();
  });

  it('should not render a Promo tag rate tags are empty', function () {
    mockProps.bookingSummaryData.rateInformation = {
      rate: '',
      noRooms: 1,
      noNights: 1,
      rateTags: [],
    };
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);
    expect(queryByTestId('hdp_discountPromoTag')).toBeFalsy();
  });

  it('should render a Promo tag when rate tags are available ', function () {
    mockProps.bookingSummaryData.totalCost = {
      initialTotalCost: 0,
      amount: 0,
      currency: 'EUR',
      showVATMessage: false,
      donations: 3,
      meals: [{ childrenMeals: [], adultsMeals: [] }],
    };
    mockProps.bookingSummaryData.rateInformation = {
      rate: '',
      noRooms: 1,
      noNights: 1,
      rateTags: ['10% discount'],
    };
    const { queryByTestId } = render(<BookingSummaryCard {...mockProps} />);
    expect(queryByTestId('hdp_discountPromoTag')).toBeTruthy();
  });
});

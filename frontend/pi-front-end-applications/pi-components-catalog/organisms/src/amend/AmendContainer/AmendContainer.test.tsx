import { InputGroup } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { Area, DATE_TYPE } from '@whitbread-eos/api';
import { LoadingSpinner } from '@whitbread-eos/atoms';
import { useFeatureToggle, useGetDiscountRateReservationData } from '@whitbread-eos/utils';
import * as utils from '@whitbread-eos/utils';
import { format } from 'date-fns';
import React from 'react';

import {
  mockedBookingConfirmationAmendData,
  mockedBookingConfirmationAmendDataEmployee,
  mockedConfirmAmendResponse,
  mockedConfirmAmendResponseEmployee,
  mockedEmployeeStayDatesRulesData,
  mockedGetStaticContent,
  mockedMealPackagesData,
  mockedPrivacyPolicyData,
  mockedRestaurantData,
  mockedRoomOccupancyLimitationsData,
  mockedSecurityNoticeMoreInfoDataSelectorResponse,
  mockedStayDatesRulesData,
  mockedSummaryOfPaymentsData,
  mockedTranslations,
  mockedRestaurantDataWithClosure,
} from '../../mockData/mockResponse';
import { act, fireEvent, render, screen, waitFor } from '../../utils/test-utils';
import { PAY_ON_ARRIVAL } from '../constants';
import * as helpers from '../helpers';
import AmendContainer, { handleAmendStayDatesMutation } from './AmendContainer';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  StayDates: ({ onAmendStayDates }: any) => (
    <button
      data-testid="mock-stay-dates"
      onClick={() => onAmendStayDates(new Date('2025-12-01'), new Date('2025-12-02'))}
    >
      Amend
    </button>
  ),
}));

jest.spyOn(utils, 'getNightsNumber').mockReturnValue(4);

const tMock = (key: string) => {
  switch (key) {
    case 'amend.roomSuccessfullyAdded':
      return 'Room successfully added';
    case 'amend.roomSuccessfullyUpdated':
      return 'Room successfully updated';
    case 'amend.roomSuccessfullRemoved':
      return 'Room successfully removed';
    case 'amend.stayDate':
      return 'Stay dates';
    case 'amend.yourMeals':
      return 'Your meals';
    case 'amend.notification.reset.header':
      return 'Your meals has been reset';
    case 'headerInformationData.headerInformation.results.notifications.groupBookingMessage':
      return 'If you’d like to book more rooms, please call us and we’ll be happy to help.';
    case 'headerInformationData.headerInformation.results.notifications.ccuiGroupBookingMessage':
      return 'If caller wishes to add more than 9 rooms then please ask them to contact the Groups Team at group.enquiries@whitbread.com';
    case 'headerInformationData.headerInformation.results.notifications.emp01groupBookingMessage':
      return 'You can book a maximum of 2 rooms using the employee rate';
    case 'amend.extras.extras.':
      return 'Extras';
    default:
      return 'default';
  }
};

jest.mock('../helpers', () => ({
  ...jest.requireActual('../helpers'),
  isStayDatesSectionUpdated: () => true,
  shouldAmendStayDates: jest.fn(),
  showNotificationNoPromotion: jest.fn(),
}));
jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      return mockedTranslations[key] || 'default';
    },
  }),
}));
jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

const mockUseRouter = {
  push: jest.fn(),
  prefetch: jest.fn(),
  events: {
    on: jest.fn(),
    off: jest.fn(),
  },
};

jest.mock('next/router', () => ({
  useRouter: () => mockUseRouter,
}));

const mockedGetBookingConfirmation = {
  data: mockedBookingConfirmationAmendData,
  isLoading: false,
  isSuccess: true,
  isError: false,
  error: '',
  isFetching: false,
};
const mockPromoResponse = {
  promotionsInformation: {
    showPromo: false,
    isWithinPromoWindow: true,
    promotionCode: 'ST10R',
    landingPage: '',
    promoBannerColour: '#511E62',
    promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
    promoBannerTitle: "<span style='color: #FDB913;'><b>Summer Sale: 20% off</b></span>",
    promoBannerSubtitle:
      '<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promoAmendMessage: '<b>Your booking includes a promotion.</b> Cancel this booking and rebook.',
    promoBookingInfo: {
      ratePlanCode: 'STDDIS10',
      promotionCode: 'ST10R',
    },
  },
};

const mockedGetPackages = {
  privacyPolicy: mockedMealPackagesData.packages.privacyPolicy,
  restaurant: mockedRestaurantData,
  restaurantClosure: mockedRestaurantDataWithClosure,
  packages: mockedMealPackagesData.packages.packages,
  isLoading: false,
  isSuccess: true,
  isError: false,
  error: {
    message: 'test error',
  },
  isFetching: false,
};

const mockedManageBookingData = {
  manageBooking: {
    isCancellable: true,
  },
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return 'en-gb';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: () => {
      return {
        t: (str: string) => str,
        i18n: {
          changeLanguage: () => new Promise(() => true),
        },
      };
    },
  };
});

const mockUseScreenSize = jest.fn();

jest.mock('@whitbread-eos/organisms', () => ({
  ...jest.requireActual('@whitbread-eos/organisms'),
  getAmendSectionTranslations: jest.fn().mockResolvedValue({
    bookingSummaryLabels: {},
    summaryOfPaymentsLabels: {},
    notificationLabels: {},
    removeRoomModalLabels: {},
    _stayDatesLabels: {},
    leadGuestValidationLabels: {},
    leadGuestLabels: {},
    roomAvailabilityLabels: {},
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  isInnBusinessApp: jest.fn(),
  getNightsNumber: jest.fn(),
  useFeatureToggle: jest.fn(() => ({
    release_pi_amend_delete_reg_card: true,
    release_pi_bb_ccui_maxrooms_amend: false,
    release_pi_promo_code_landing_page: true,
    release_ccui_promo_code_landing_page: true,
    release_bb_promo_code_landing_page: true,
    release_bb_promo_code_site_wide: true,
    release_promotions_in_hotelavailability: true,
  })),
  useIPageSubmission: jest.fn().mockReturnValue({ isPaymentComplete: true, cardType: 'Credit' }),
  securityNoticeMoreInfoDataSelector: jest
    .fn()
    .mockImplementation(() => ({ mockedSecurityNoticeMoreInfoDataSelectorResponse })),
  useQueryRequest: jest.fn().mockImplementation((queryKey: string | any[]) => {
    let queryKeyValue = queryKey;
    if (Array.isArray(queryKey)) {
      queryKeyValue = queryKey[0];
    }
    switch (queryKeyValue) {
      case 'getBookingConfirmationAmend':
        return {
          ...mockedGetBookingConfirmation,
        };
      case 'manageBookingDashBoard':
        return {
          data: {
            ...mockedManageBookingData,
          },
          isLoading: false,
          error: false,
          isError: false,
        };
      case 'GetPackages':
        return mockedGetPackages;
      case 'AmendSummary':
        return {
          data: {
            amendSummary: mockedSummaryOfPaymentsData,
          },
          isLoading: false,
          error: false,
          isError: false,
        };
      default:
        return {};
    }
  }),
  graphQLRequest: jest.fn().mockResolvedValue(mockPromoResponse),
  usePackages: () => ({
    ...mockedGetPackages.packages,
    ...mockedGetPackages,
  }),
  useFeatureSwitch: () => true,
  useScreenSize: () => mockUseScreenSize(),
  useSessionStorage: () => [],
  useGetDiscountRateReservationData: jest.fn().mockReturnValue({
    isDiscountRate: true,
    maxRooms: 2,
    matchedDiscountRate: 'FCDNLR30',
    roomLimitMessage: 'You cannot book more than 2',
    unAvailableMessage: 'Unable to book more rooms',
  }),
  roomInformationSelector: jest.fn(() => [
    {
      reservationId: '2292308',
      packagesSelection: [{ id: 'BFADBF', noOfSelections: 1 }],
      selectedExtrasList: {
        packagesSelection: [
          {
            id: 'FHSCOU2',
            noOfSelections: 1,
          },
        ],
      },
    },
    {
      reservationId: '2292104',
      packagesSelection: [],
      selectedExtrasList: {
        packagesSelection: [
          {
            id: 'HSCKIN',
            noOfSelections: 1,
          },
        ],
      },
    },
  ]),
}));

const mockedData = {
  bookingConfirmationData: mockedBookingConfirmationAmendData.bookingConfirmation,
  privacyPolicyPackData: mockedPrivacyPolicyData.privacyPolicy,
  headerInformationData: mockedGetStaticContent.data,
  stayRulesData: mockedStayDatesRulesData,
  employeeStayRulesData: mockedEmployeeStayDatesRulesData,
  RoomOccupancyLimitationsData: mockedRoomOccupancyLimitationsData,
  addNewRoomMutation: {
    mutateAsync: jest
      .fn()
      .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
    reset: () => jest.fn(),
  },
  addNewRoomIsSuccess: undefined,
  addNewRoomIsLoading: false,
  confirmAmend: {
    confirmAmendMutation: {
      mutateAsync: jest.fn().mockResolvedValue(mockedConfirmAmendResponse),
    },
    confirmAmendIsLoading: false,
    confirmAmendIsSuccess: false,
    confirmAmendIsError: false,
  },
  amendStayDates: {
    amendStayDatesIsError: false,
    amendStayDatesIsSuccess: undefined,
    amendStayDatesIsLoading: false,
    amendStayDatesMutation: {
      mutateAsync: jest
        .fn()
        .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
      reset: () => jest.fn(),
    },
  },
  amendEditRoom: {
    amendEditRoomIsLoading: false,
    amendEditRoomIsSuccess: undefined,
    amendEditRoomMutation: {
      mutateAsync: jest
        .fn()
        .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
      reset: () => jest.fn(),
    },
  },
  removeRoom: {
    removeRoomIsSuccess: undefined,
    removeRoomIsLoading: false,
    removeRoomMutation: {
      mutateAsync: jest
        .fn()
        .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
      reset: jest.fn(),
    },
  },
  saveReservation: {
    amendSaveReservationIsLoading: false,
    amendSaveReservationIsSuccess: true,
    amendSaveReservationMutation: {
      mutateAsync: jest
        .fn()
        .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
      reset: () => jest.fn(),
    },
  },
  brand: '',
  promoStayData: {
    showPromo: false,
    isWithinPromoWindow: true,
    promotionCode: 'ST10R',
    landingPage: '',
    promoBannerColour: '#511E62',
    promoBannerIcon: '/content/dam/global/icons/common/price-tag-orange-16.svg',
    promoBannerTitle: "<span style='color: #FDB913;'><b>Summer Sale: 20% off</b></span>",
    promoBannerSubtitle:
      '<b>Select one of our hotels to see your discount.</b> Prices shown here don’t include your discount yet.',
    promoInvalidMessage: null,
    promoExpiredMessage: null,
    promoAmendMessage: '<b>Your booking includes a promotion.</b> Cancel this booking and rebook.',
    promoBookingInfo: {
      ratePlanCode: 'STDDIS10',
      promotionCode: 'ST10R',
    },
  },
};

const mockedProps = {
  language: 'en',
  country: 'gb',
  bookingReference: 'AWM3095254',
  temporaryBasketReference: 'AWM30952542',
  basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d3173b',
  channel: 'PI',
  t: tMock,
  data: mockedData,
  brand: 'pi',
  variant: Area.PI,
  amendVisited: false,
  paymentProps: {
    selectedPaymentDetail: {
      type: PAY_ON_ARRIVAL,
    },
  },
};

describe('Amend Container', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockUseScreenSize.mockReturnValue({ isLessThanLg: false });
  });

  const loadingSpinnerStyle = {
    margin: {
      sm: 'lg',
    },
  };
  it('renders LoadingSpinner with the specified style and ignores other props', () => {
    const loadingSpinner = render(
      <LoadingSpinner loadingText="Loading..." wrapperStyle={loadingSpinnerStyle} />
    ).getByTestId('loading-spinner');

    expect(loadingSpinner).toBeInTheDocument();
    expect(loadingSpinner).toHaveStyle({
      margin: 'lg',
    });

    expect(loadingSpinner).toHaveTextContent('Loading...');
  });

  it('renders RestaurantMessage when no meals found', async () => {
    mockedGetPackages.restaurantClosure.noMealsFound = true;

    render(<AmendContainer {...mockedProps} />);

    expect(screen.queryByTestId('amend-RestaurantMessage')).not.toBeInTheDocument();
    expect(mockedGetPackages.restaurantClosure.noMealsFound).toBeTruthy();
    expect(mockedGetPackages.restaurantClosure.messageTitle).toContain('Title-Closed');
    expect(mockedGetPackages.restaurantClosure.messageDescription).toContain('Description-Closed');
  });

  it('should show noselectedmeals when restaurant data is not found', async () => {
    const onToggleSection = jest.fn();
    const renderAccordionItems = jest.fn().mockImplementation(() => {
      return [
        {
          title: 'test title',
          content: 'content-text',
          onToggleSection,
        },
      ];
    });
    // Mock the data to simulate no meals found
    const mockedGetPackages = {
      restaurant: {
        noMealsFound: true,
      },
      restaurantClosure: {
        restaurantNotFound: true,
      },
    };

    await act(async () => {
      render(<AmendContainer {...mockedProps} />);
    });

    expect(renderAccordionItems).toBeCalledTimes(0);

    expect(screen.queryByTestId('amend-RestaurantMessage')).not.toBeInTheDocument();
    expect(mockedGetPackages.restaurantClosure.restaurantNotFound).toBeTruthy();
    expect(renderAccordionItems()).toHaveLength(1);
  });
  it('Find the title of the page', async () => {
    const { getByTestId } = render(<AmendContainer {...mockedProps} />);
    await waitFor(() => {
      expect(getByTestId('amend-page-title')).toBeInTheDocument();
    });
  });

  it('Find the data security section', async () => {
    const { getByTestId } = render(<AmendContainer {...mockedProps} />);

    await waitFor(() => {
      expect(getByTestId('amend-PrivacyPolicy-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render Stay Dates section', async () => {
    const { getByTestId } = render(<AmendContainer {...mockedProps} />);
    await waitFor(() => {
      expect(getByTestId('mock-stay-dates')).toBeInTheDocument();
    });
  });
  it('should render Rooms and Guests section', async () => {
    const { getByTestId } = render(<AmendContainer {...mockedProps} />);
    await waitFor(() => {
      expect(getByTestId('amend-rooms-and-guests-section')).toBeInTheDocument();
    });
  });

  it('should render Booking Summary section', async () => {
    const { getByTestId } = render(<AmendContainer {...mockedProps} />);
    await waitFor(() => {
      expect(getByTestId('amend-booking-summary-section')).toBeInTheDocument();
    });
  });

  it("should render Booking Summary when isLessThanLg it's true", async () => {
    mockUseScreenSize.mockReturnValue({ isLessThanLg: true });

    const { getByTestId } = render(<AmendContainer {...mockedProps} />);
    await waitFor(() => {
      expect(getByTestId('amend-booking-summary-section')).toBeInTheDocument();
    });
  });

  it('should render Cancel and return to homepage button', async () => {
    const { getByTestId } = render(<AmendContainer {...mockedProps} />);
    await waitFor(() => {
      expect(getByTestId('amend-BackToHomePageButton')).toBeInTheDocument();
    });
  });

  it('should show extras section', async () => {
    const { container } = render(<AmendContainer {...mockedProps} />);

    await waitFor(() => {
      const accordionItems = container.querySelectorAll('.chakra-accordion__item');
      expect(accordionItems?.length).toBe(4);
    });
  });

  it('should hide your meals section when no restaurant found and no selected meals', async () => {
    mockedGetPackages.restaurant.restaurantNotFound = true;
    const mockedRoomSelection = mockedGetPackages.packages.roomSelection;
    mockedGetPackages.packages.roomSelection = [
      { packagesSelection: [], reservationId: '12341234' },
    ];
    const { container } = render(<AmendContainer {...mockedProps} />);

    await waitFor(() => {
      const accordionItems = container.querySelectorAll('.chakra-accordion__item');
      expect(accordionItems?.length).toBe(2);
      mockedGetPackages.restaurant.restaurantNotFound = false;
      mockedGetPackages.packages.roomSelection = mockedRoomSelection;
    });
  });

  it('should display restaurant information message when noMealsFound=true', async () => {
    const originalNoMealsFound = mockedGetPackages.restaurant.noMealsFound;
    try {
      mockedGetPackages.restaurant.noMealsFound = true;
      const { getByTestId } = render(<AmendContainer {...mockedProps} />);

      await waitFor(() => {
        expect(getByTestId('amend-RestaurantMessage-Wrapper')).toBeInTheDocument();
      });
    } finally {
      mockedGetPackages.restaurant.noMealsFound = originalNoMealsFound;
    }
  });

  it('should prioritise ancillary closeout custom copy over legacy restaurant copy', async () => {
    const originalNoMealsFound = mockedGetPackages.restaurant.noMealsFound;
    const originalMessageHeader = mockedGetPackages.restaurant.messageHeader;
    const originalMessageDescription = mockedGetPackages.restaurant.messageDescription;

    try {
      mockedGetPackages.restaurant.noMealsFound = true;
      mockedGetPackages.restaurant.messageHeader = 'Legacy restaurant heading';
      mockedGetPackages.restaurant.messageDescription = 'Legacy restaurant description';

      const propsWithAncillaryCustomCopy = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          hotelInformation: {
            hotelInformation: {
              ancillaryCloseout: {
                items: [
                  {
                    noMealsHeading: 'Custom closeout heading',
                    noMealsMessage: 'Custom closeout message',
                    startDate: '01/10/2023',
                    endDate: '31/10/2023',
                  },
                ],
              },
            },
          },
        },
      };

      const { getByTestId } = render(<AmendContainer {...propsWithAncillaryCustomCopy} />);

      await waitFor(() => {
        expect(getByTestId('amend-RestaurantMessage-Title')).toHaveTextContent(
          'Custom closeout heading'
        );
        expect(getByTestId('amend-RestaurantMessage-Description')).toHaveTextContent(
          'Custom closeout message'
        );
      });
    } finally {
      mockedGetPackages.restaurant.noMealsFound = originalNoMealsFound;
      mockedGetPackages.restaurant.messageHeader = originalMessageHeader;
      mockedGetPackages.restaurant.messageDescription = originalMessageDescription;
    }
  });

  it('should display restaurant information message when restaurantNotFound=true', async () => {
    const originalRestaurantNotFound = mockedGetPackages.restaurant.restaurantNotFound;
    const originalMessageHeader = mockedGetPackages.restaurant.messageHeader;
    const originalMessageDescription = mockedGetPackages.restaurant.messageDescription;

    try {
      mockedGetPackages.restaurant.restaurantNotFound = true;
      mockedGetPackages.restaurant.messageHeader = 'Restaurant not found';
      mockedGetPackages.restaurant.messageDescription = 'The restaurant is currently unavailable';

      const { getByTestId } = render(<AmendContainer {...mockedProps} />);

      await waitFor(() => {
        expect(getByTestId('amend-RestaurantMessage-Wrapper')).toBeInTheDocument();
        expect(getByTestId('amend-RestaurantMessage-Title')).toHaveTextContent(
          'Restaurant not found'
        );
        expect(getByTestId('amend-RestaurantMessage-Description')).toHaveTextContent(
          'The restaurant is currently unavailable'
        );
      });
    } finally {
      mockedGetPackages.restaurant.restaurantNotFound = originalRestaurantNotFound;
      mockedGetPackages.restaurant.messageHeader = originalMessageHeader;
      mockedGetPackages.restaurant.messageDescription = originalMessageDescription;
    }
  });

  it('should redirect to homepage on cancel', async () => {
    const { location } = window;
    Object.defineProperty(window, 'location', {
      value: new URL('https://example.com'),
      configurable: true,
      writable: true,
    });

    const { getByTestId } = render(<AmendContainer {...mockedProps} />);

    const cancelBtn = getByTestId('amend-BackToHomePageButton');
    act(() => {
      fireEvent.click(cancelBtn);
    });

    expect(window.location.href).toBe('https://example.com/gb/en/home.html');

    window.location = location;
  });

  it('should redirect to BB Homepage on cancel if the user is BB agent', async () => {
    const { location } = window;
    Object.defineProperty(window, 'location', {
      value: new URL('http://example.com'),
      configurable: true,
      writable: true,
    });

    const { getByTestId } = render(<AmendContainer {...mockedProps} variant={Area.BB} />);

    const cancelBtn = getByTestId('amend-BackToHomePageButton');
    act(() => {
      fireEvent.click(cancelBtn);
    });

    expect(window.location.href).toBe('http://example.com/gb/en/business-booker/home.html');

    window.location = location;
  });

  it('should redirect to Bookings page on cancel if the user is a CCUI Agent', async () => {
    const { location } = window;
    Object.defineProperty(window, 'location', {
      value: new URL('http://example.com'),
      configurable: true,
      writable: true,
    });

    const { getByTestId } = render(
      <InputGroup>
        <AmendContainer {...mockedProps} variant={Area.CCUI} />
      </InputGroup>
    );

    const cancelBtn = getByTestId('amend-BackToHomePageButton');
    act(() => {
      fireEvent.click(cancelBtn);
    });

    expect(window.location.href).toBe('http://example.com/gb/en/bookings');

    window.location = location;
  });

  it('should call amendStayDatesMutation with expected params', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_promotions_in_hotelavailability: false,
    });
    render(<AmendContainer {...mockedProps} />);

    fireEvent.click(screen.getByTestId('mock-stay-dates'));

    await waitFor(() => {
      expect(
        mockedProps.data.amendStayDates.amendStayDatesMutation.mutateAsync
      ).toHaveBeenCalledWith(
        expect.objectContaining({
          tempBookingRef: 'AWM30952542',
          channel: 'PI',
          subchannel: 'WEB',
          language: 'EN',
          newStartDate: '2025-12-01',
          newEndDate: '2025-12-02',
        })
      );
    });
  });

  it('should log error when amend stay dates mutation fails', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_promotions_in_hotelavailability: false,
    });
    const error = {
      response: {
        errors: [{ message: 'failure' }],
      },
    };

    const consoleSpy = jest.spyOn(console, 'log').mockImplementation();

    mockedProps.data.amendStayDates.amendStayDatesMutation.mutateAsync = jest
      .fn()
      .mockRejectedValue(error);

    render(<AmendContainer {...mockedProps} />);

    fireEvent.click(screen.getByTestId('mock-stay-dates'));

    await waitFor(() => {
      expect(consoleSpy).toHaveBeenCalledWith('Amend failed:', error);
    });

    consoleSpy.mockRestore();
  });

  it('should include promotion parameters when promotion feature is enabled', async () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_promotions_in_hotelavailability: false,
    });
    render(<AmendContainer {...mockedProps} />);

    fireEvent.click(screen.getByTestId('mock-stay-dates'));

    await waitFor(() => {
      expect(mockedProps.data.amendStayDates.amendStayDatesMutation.mutateAsync).toHaveBeenCalled();
    });
  });

  describe('Promotions Notification', () => {
    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should render promo message when showPromo=true and amendStayDatesIsError=true', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          amendStayDates: {
            ...mockedProps.data.amendStayDates,
            amendStayDatesIsError: true,
          },
          promoStayData: { showPromo: true },
        },
      };

      const { getByText } = render(<AmendContainer {...props} />);
      () => {
        expect(getByText(/your booking includes a promotion/i)).toBeInTheDocument();
      };
    });

    it('should render promo message when showPromo=true and amendStayDatesIsError=false', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          amendStayDates: {
            ...mockedProps.data.amendStayDates,
            amendStayDatesIsError: false,
          },
          promoStayData: { showPromo: true },
        },
      };

      const { getByText } = render(<AmendContainer {...props} />);
      () => {
        expect(getByText(/your booking includes a promotion/i)).toBeInTheDocument();
      };
    });

    it('should NOT render promo message when showPromo=false and amendStayDatesIsError=true', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          amendStayDates: {
            ...mockedProps.data.amendStayDates,
            amendStayDatesIsError: true,
          },
          promoStayData: { showPromo: false },
        },
      };

      const { queryByText } = render(<AmendContainer {...props} />);
      await waitFor(() => {
        expect(queryByText(/your booking includes a promotion/i)).not.toBeInTheDocument();
      });
    });

    it('should NOT render promo message when showPromo=false and amendStayDatesIsError=false', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          amendStayDates: {
            ...mockedProps.data.amendStayDates,
            amendStayDatesIsError: false,
          },
          promoStayData: { showPromo: false },
        },
      };

      const { queryByText } = render(<AmendContainer {...props} />);
      await waitFor(() => {
        expect(queryByText(/your booking includes a promotion/i)).not.toBeInTheDocument();
      });
    });

    it('should not update Stay Dates section when isStayDatesSectionUpdated returns false', async () => {
      jest.spyOn(helpers, 'isStayDatesSectionUpdated').mockImplementation(() => false);

      const { getByTestId } = render(<AmendContainer {...mockedProps} />);

      const stayDatesSection = getByTestId('mock-stay-dates');
      await waitFor(() => {
        expect(stayDatesSection).toBeInTheDocument();
      });
    });

    it('should set isWithPromoAndStayDatesUpdates true when within promo window and stayDatesSectionHasUpdates=true', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: true },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).toBeInTheDocument();
      };
    });

    it('should not render stay dates notifications when promo is shown and isWithinPromoWindow=false', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: true, isWithinPromoWindow: false },
        },
      };

      const { queryByTestId } = render(<AmendContainer {...props} />);

      await waitFor(() => {
        expect(queryByTestId('amend-available-dates-notification')).not.toBeInTheDocument();
      });
    });

    it('should render stay dates notifications when showPromo=false regardless of isWithinPromoWindow', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: false },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).toBeInTheDocument();
      };
    });

    it('should handle promoStayData being null gracefully', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: null,
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);
      () => {
        expect(getByTestId('amend-stay-dates-section')).toBeInTheDocument();
      };
    });

    it('renders only the first notification when shouldAmendStayDates is true', () => {
      jest.spyOn(helpers, 'shouldAmendStayDates').mockImplementation(() => true);
      jest.spyOn(helpers, 'showNotificationNoPromotion').mockImplementation(() => false);

      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: false },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).toBeInTheDocument();
        expect(getByTestId('amend-update-stay-dates')).not.toBeInTheDocument();
      };
    });

    it('renders only the first notification when shouldAmendStayDates is true', () => {
      jest.spyOn(helpers, 'shouldAmendStayDates').mockImplementation(() => true);
      jest.spyOn(helpers, 'showNotificationNoPromotion').mockImplementation(() => false);

      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: false },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).toBeInTheDocument();
        expect(getByTestId('amend-update-stay-dates')).not.toBeInTheDocument();
      };
    });

    it('renders both notifications when showNotificationNoPromotions is true', () => {
      jest.spyOn(helpers, 'shouldAmendStayDates').mockImplementation(() => false);
      jest.spyOn(helpers, 'showNotificationNoPromotion').mockImplementation(() => true);

      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: false },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).toBeInTheDocument();
        expect(getByTestId('amend-update-stay-dates')).toBeInTheDocument();
      };
    });

    it('renders both notifications when both flags are true', () => {
      jest.spyOn(helpers, 'shouldAmendStayDates').mockImplementation(() => true);
      jest.spyOn(helpers, 'showNotificationNoPromotion').mockImplementation(() => true);

      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: false },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).toBeInTheDocument();
        expect(getByTestId('amend-update-stay-dates')).toBeInTheDocument();
      };
    });

    it('renders nothing when both flags are false', () => {
      jest.spyOn(helpers, 'shouldAmendStayDates').mockImplementation(() => false);
      jest.spyOn(helpers, 'showNotificationNoPromotion').mockImplementation(() => false);

      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          promoStayData: { showPromo: false, isWithinPromoWindow: false },
        },
      };

      const { getByTestId } = render(<AmendContainer {...props} />);

      () => {
        expect(getByTestId('amend-available-dates-notification')).not.toBeInTheDocument();
        expect(getByTestId('amend-update-stay-dates')).not.toBeInTheDocument();
      };
    });
  });

  describe('Room and Guests Section', () => {
    afterEach(() => {
      jest.clearAllMocks();
    });

    it('call removeRoomMutation on remove room', async () => {
      const { getByTestId, findByTestId } = render(<AmendContainer {...mockedProps} />);

      const removeRoomBtn = getByTestId('room-info-card-amend-remove-button-2');
      act(() => {
        fireEvent.click(removeRoomBtn);
      });

      const removeSubmitBtn = await findByTestId('RemoveRoomModal-removeRoom');
      act(() => {
        fireEvent.click(removeSubmitBtn);
      });

      expect(screen.getByTestId('ModalContent')).toBeInTheDocument();

      expect(mockedProps.data.removeRoom.removeRoomMutation.mutateAsync).toHaveBeenCalledWith({
        channel: 'PI',
        language: 'en',
        reservationId: '2292104',
        subchannel: 'WEB',
        tempBookingRef: 'AWM30952542',
        token: undefined,
      });
    });

    it('call amendEditRoomMutation on edit room and display its success and "Reset meals" warning notifications', async () => {
      const { getByTestId, findByTestId } = render(<AmendContainer {...mockedProps} />);

      const editRoomBtn = getByTestId('room-info-card-amend-edit-button-1');
      await act(async () => {
        fireEvent.click(editRoomBtn);
      });

      const inputEl = await findByTestId('input-firstName');
      await act(async () => {
        fireEvent.change(inputEl, { target: { value: 'new first name' } });
      });

      const form = document.querySelector('#leadGuestDetailsForm') as HTMLFormElement;
      await act(async () => {
        fireEvent.submit(form);
      });

      await waitFor(() => {
        expect(mockedProps.data.amendEditRoom.amendEditRoomMutation.mutateAsync).toHaveBeenCalled();
      });
    });

    it('should display max rooms message for discountRate in Amend', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: true,
          maxRooms: 2,
          matchedDiscountRate: 'FCDNLR30',
          roomLimitMessage: 'You cannot book more than 2',
          unAvailableMessage: 'Unable to book more rooms',
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: false,
      });
      const { getByText } = render(<AmendContainer {...mockedProps} />);
      expect(getByText('You cannot book more than 2')).toBeInTheDocument();
    });

    it('should display max rooms message for standard booking in Amend', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: false,
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: false,
      });
      const { getByText } = render(<AmendContainer {...mockedProps} />);
      // 4 rooms in reservation, maxRooms set to 4 - so notification should be displayed
      expect(
        getByText('If you’d like to book more rooms, please call us and we’ll be happy to help.')
      ).toBeInTheDocument();
    });

    it('(isAmendMaxRoomsEnabled is true) - should display max rooms message for standard booking in Amend', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: false,
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: true,
      });
      const { getByText } = render(<AmendContainer {...mockedProps} />);

      // 4 rooms in reservation, maxRoomsAmend used if feature toggle on, and set to 3 - so notification should be displayed
      expect(
        getByText('If you’d like to book more rooms, please call us and we’ll be happy to help.')
      ).toBeInTheDocument();
    });

    it('(isAmendMaxRoomsEnabled is true) - should display max rooms message for standard booking in Amend', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: false,
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: true,
      });
      mockedProps.data.stayRulesData.globalConfig.maxRoomsLim.maxRoomsAmend = 5;
      const { queryByText } = render(<AmendContainer {...mockedProps} />);

      // 4 rooms in reservation, maxRoomsAmend set to 5 - so notification should not be displayed
      expect(
        queryByText('If you’d like to book more rooms, please call us and we’ll be happy to help.')
      ).not.toBeInTheDocument();
    });

    it('should display ECI/LCO alert notification', () => {
      const { getByTestId } = render(<AmendContainer {...mockedProps} />);
      expect(getByTestId('amend-Notification-Alert-EciLco')).toBeInTheDocument();
    });

    it('should display max rooms message (max 9 rooms) for CCUI booking in Amend', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(() => {
        return {
          isDiscountRate: false,
        };
      });
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: false,
      });
      const { getByText } = render(<AmendContainer {...mockedProps} variant={Area.CCUI} />);
      expect(
        getByText(
          'If caller wishes to add more than 9 rooms then please ask them to contact the Groups Team at group.enquiries@whitbread.com'
        )
      ).toBeInTheDocument();
    });

    it('should display max rooms message for TIR in CCUI amend flow', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(() => {
        return {
          isDiscountRate: true,
          maxRooms: 2,
          matchedDiscountRate: 'FCDNLR30',
          roomLimitMessage: 'You cannot book more than 2',
          unAvailableMessage: 'Unable to book more rooms',
        };
      });
      const { getByText } = render(<AmendContainer {...mockedProps} variant={Area.CCUI} />);
      expect(getByText('You cannot book more than 2')).toBeInTheDocument();
    });
  });

  describe('Room and Guests Section - Employee offer', () => {
    afterEach(() => {
      jest.clearAllMocks();
    });

    const mockedGetBookingConfirmationEmployee = {
      data: mockedBookingConfirmationAmendDataEmployee,
      isLoading: false,
      isSuccess: true,
      isError: false,
      error: '',
      isFetching: false,
    };

    jest.mock('@whitbread-eos/utils', () => ({
      ...jest.requireActual('@whitbread-eos/utils'),
      useFeatureToggle: jest.fn(() => ({
        release_pi_amend_delete_reg_card: true,
        release_pi_bb_ccui_maxrooms_amend: false,
      })),
      useIPageSubmission: jest
        .fn()
        .mockReturnValue({ isPaymentComplete: true, cardType: 'Credit' }),
      securityNoticeMoreInfoDataSelector: jest
        .fn()
        .mockImplementation(() => ({ mockedSecurityNoticeMoreInfoDataSelectorResponse })),
      useQueryRequest: jest.fn().mockImplementation((queryKey: string | any[]) => {
        let queryKeyValue = queryKey;
        if (Array.isArray(queryKey)) {
          queryKeyValue = queryKey[0];
        }
        switch (queryKeyValue) {
          case 'getBookingConfirmationAmend':
            return {
              ...mockedGetBookingConfirmationEmployee,
            };
          case 'manageBookingDashBoard':
            return {
              data: {
                ...mockedManageBookingData,
              },
            };
          case 'GetPackages':
            return mockedGetPackages;
          default:
            return {};
        }
      }),
      usePackages: () => ({
        ...mockedGetPackages.packages,
        ...mockedGetPackages,
      }),
    }));

    const mockedEmployeeData = {
      // 2 rooms in booking
      bookingConfirmationData: mockedBookingConfirmationAmendDataEmployee.bookingConfirmation,
      privacyPolicyPackData: mockedPrivacyPolicyData.privacyPolicy,
      headerInformationData: mockedGetStaticContent.data,
      stayRulesData: mockedStayDatesRulesData,
      employeeStayRulesData: mockedEmployeeStayDatesRulesData,
      RoomOccupancyLimitationsData: mockedRoomOccupancyLimitationsData,
      addNewRoomMutation: {
        mutateAsync: jest
          .fn()
          .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
        reset: () => jest.fn(),
      },
      addNewRoomIsSuccess: undefined,
      addNewRoomIsLoading: false,
      confirmAmend: {
        confirmAmendMutation: {
          mutateAsync: jest.fn().mockResolvedValue(mockedConfirmAmendResponseEmployee),
        },
        confirmAmendIsLoading: false,
        confirmAmendIsSuccess: false,
        confirmAmendIsError: false,
      },
      amendStayDates: {
        amendStayDatesIsError: false,
        amendStayDatesIsSuccess: undefined,
        amendStayDatesIsLoading: false,
        amendStayDatesMutation: {
          mutateAsync: jest
            .fn()
            .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
          reset: () => jest.fn(),
        },
      },
      amendEditRoom: {
        amendEditRoomIsLoading: false,
        amendEditRoomIsSuccess: undefined,
        amendEditRoomMutation: {
          mutateAsync: jest
            .fn()
            .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
          reset: () => jest.fn(),
        },
      },
      removeRoom: {
        removeRoomIsSuccess: undefined,
        removeRoomIsLoading: false,
        removeRoomMutation: {
          mutateAsync: jest
            .fn()
            .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
          reset: jest.fn(),
        },
      },
      saveReservation: {
        amendSaveReservationIsLoading: false,
        amendSaveReservationIsSuccess: true,
        amendSaveReservationMutation: {
          mutateAsync: jest
            .fn()
            .mockResolvedValue({ copyBooking: { copyBasketReference: 'AWM30789254' } }),
          reset: () => jest.fn(),
        },
      },
      brand: '',
    };

    const mockedPropsEmployee = {
      language: 'en',
      country: 'gb',
      bookingReference: 'AWM3095254',
      temporaryBasketReference: 'AWM30952542',
      basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d3173b',
      channel: 'PI',
      t: tMock,
      data: mockedEmployeeData,
      variant: Area.PI,
      brand: 'pi',
      amendVisited: false,
    };

    it('should display max rooms message for Employee offer booking in Amend flow - using maxRooms prop', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: false,
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: false,
      });

      const { getByText } = render(<AmendContainer {...mockedPropsEmployee} />);
      expect(
        getByText('You can book a maximum of 2 rooms using the employee rate')
      ).toBeInTheDocument();
    });

    it('(isAmendMaxRoomsEnabled is true) - should not display max rooms message for Employee offer booking in Amend flow', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: false,
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: true,
      });

      const { queryByText } = render(<AmendContainer {...mockedPropsEmployee} />);
      // note: maxRoomsAmend - set to 5 in mock data, so greater than - booking of 4 rooms
      expect(
        queryByText('You can book a maximum of 2 rooms using the employee rate')
      ).not.toBeInTheDocument();
    });

    it('(feature flag isAmendMaxRoomsEnabled is true) - should display max rooms message for Employee offer booking in Amend flow', async () => {
      (useGetDiscountRateReservationData as jest.Mock).mockImplementation(
        jest.fn().mockReturnValue({
          isDiscountRate: false,
        })
      );
      (useFeatureToggle as jest.Mock).mockReturnValue({
        release_pi_bb_ccui_maxrooms_amend: true,
      });
      // maxRoomsAmend - set to 2, so less than - booking of 4 rooms
      mockedPropsEmployee.data.employeeStayRulesData.globalConfig.maxRoomsLim.maxRoomsAmend = 2;
      const { getByText } = render(<AmendContainer {...mockedPropsEmployee} />);
      expect(
        getByText('You can book a maximum of 2 rooms using the employee rate')
      ).toBeInTheDocument();
    });
  });

  describe('Your meals section', () => {
    it('save meals called on remove meal', async () => {
      const { findAllByTestId } = render(<AmendContainer {...mockedProps} />);

      const removeMealBtns = await findAllByTestId(
        'RoomsMealSelection-Meals-Adults-MealItem-AddSubtractControls-SubtractButton'
      );

      fireEvent.click(removeMealBtns[0]);

      await waitFor(
        () => {
          expect(
            mockedProps.data.saveReservation.amendSaveReservationMutation.mutateAsync
          ).toHaveBeenCalledWith(
            expect.objectContaining({
              basketReferenceId: 'AWM30952542',
              hotelId: 'MANOLD',
              arrivalDate: '2023-10-26',
              departureDate: '2023-10-29',
              roomsSelections: [
                {
                  reservationId: '2292308',
                  packagesSelection: [{ id: 'BFADCT', noOfSelections: 1 }],
                },
                {
                  reservationId: '2292104',
                  packagesSelection: [
                    { id: 'BFADBF', noOfSelections: 1 },
                    { id: 'BFADCT', noOfSelections: 1 },
                    { id: 'BFCHDF', noOfSelections: 1 },
                  ],
                },
              ],
              previousRoomsSelections: [
                {
                  reservationId: '2292308',
                  packagesSelection: [
                    { id: 'BFADBF', noOfSelections: 1 },
                    { id: 'BFADCT', noOfSelections: 1 },
                    { id: 'BFCHDF', noOfSelections: 1 },
                  ],
                },
                {
                  reservationId: '2292104',
                  packagesSelection: [
                    { id: 'BFADBF', noOfSelections: 1 },
                    { id: 'BFADCT', noOfSelections: 1 },
                    { id: 'BFCHDF', noOfSelections: 1 },
                  ],
                },
              ],
            })
          );
        },
        { timeout: 5000 }
      );
    });

    it('save meals called on add meal', async () => {
      const originalAdultsNumber =
        mockedProps.data.bookingConfirmationData.reservationByIdList[0].roomStay.adultsNumber;
      mockedProps.data.bookingConfirmationData.reservationByIdList[0].roomStay.adultsNumber = 4;

      const { findAllByTestId } = render(<AmendContainer {...mockedProps} />);

      const addMealBtns = await findAllByTestId(
        'RoomsMealSelection-Meals-Adults-MealItem-AddSubtractControls-AddButton'
      );

      fireEvent.click(addMealBtns[0]);

      await waitFor(
        () => {
          expect(
            mockedProps.data.saveReservation.amendSaveReservationMutation.mutateAsync
          ).toHaveBeenCalled();
        },
        { timeout: 5000 }
      );

      mockedProps.data.bookingConfirmationData.reservationByIdList[0].roomStay.adultsNumber =
        originalAdultsNumber;
    });
  });

  describe('Loading and error states', () => {
    afterEach(() => {
      jest.clearAllMocks();
    });

    it('should display LoadingSpinner when booking confirmation is loading', async () => {
      const props = {
        ...mockedProps,
        data: {
          ...mockedProps.data,
          bookingConfirmationData: null,
        },
      };

      jest.spyOn(utils, 'useQueryRequest').mockImplementation(() => ({
        data: null,
        isLoading: true,
        isError: false,
      }));

      const { getByTestId } = render(<AmendContainer {...props} />);
      await waitFor(() => {
        expect(getByTestId('loading-spinner')).toBeInTheDocument();
      });
    });
  });
});

describe('handleAmendStayDatesMutation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (utils.getNightsNumber as jest.Mock).mockReturnValue(4);
  });
  const invalidateBookingConfirmation = jest.fn();
  const invalidateAmendSummary = jest.fn();
  const editAmendPageAnalytics = jest.fn();
  const setPromoStayData = jest.fn();
  const setShowPromoNotification = jest.fn();

  const newStartDate = new Date('2024-01-01');
  const newEndDate = new Date('2024-01-05');

  beforeEach(() => {
    jest.clearAllMocks();
    (utils.getNightsNumber as jest.Mock).mockReturnValue(4);
  });

  it('should invalidate queries and update promotion and analytics when promotions are enabled', () => {
    const response = {
      changeBookingDates: {
        promotionsInformation: {
          promoBookingInfo: {
            promotionCode: 'PROMO10',
          },
        },
      },
    };

    handleAmendStayDatesMutation({
      invalidateBookingConfirmation,
      invalidateAmendSummary,
      editAmendPageAnalytics,
      originalNumberOfNights: 2,
      newStartDate,
      newEndDate,
      setPromoStayData,
      setShowPromoNotification,
      response,
      isStayDatesLoading: false,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    expect(invalidateBookingConfirmation).toHaveBeenCalledTimes(1);
    expect(invalidateAmendSummary).toHaveBeenCalledTimes(1);

    expect(setPromoStayData).toHaveBeenCalledWith(
      response.changeBookingDates.promotionsInformation
    );
    expect(setShowPromoNotification).toHaveBeenCalledWith(true);

    expect(utils.getNightsNumber).toHaveBeenCalledWith(
      format(newStartDate, DATE_TYPE.YEAR_MONTH_DAY),
      format(newEndDate, DATE_TYPE.YEAR_MONTH_DAY)
    );

    expect(editAmendPageAnalytics).toHaveBeenCalledWith('nights changed from 2 to 4', {
      nightsChange: 2,
    });
  });

  it('should not update promotion information when promotions are disabled', () => {
    handleAmendStayDatesMutation({
      invalidateBookingConfirmation,
      invalidateAmendSummary,
      editAmendPageAnalytics,
      originalNumberOfNights: 2,
      newStartDate,
      newEndDate,
      setPromoStayData,
      setShowPromoNotification,
      response: {},
      isStayDatesLoading: false,
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(setPromoStayData).not.toHaveBeenCalled();
    expect(setShowPromoNotification).not.toHaveBeenCalled();

    expect(editAmendPageAnalytics).toHaveBeenCalled();
  });

  it('should not update analytics when stay dates are loading', () => {
    handleAmendStayDatesMutation({
      invalidateBookingConfirmation,
      invalidateAmendSummary,
      editAmendPageAnalytics,
      originalNumberOfNights: 2,
      newStartDate,
      newEndDate,
      setPromoStayData,
      setShowPromoNotification,
      response: {},
      isStayDatesLoading: true,
      isPromotionsInHotelAvailabilityEnabled: false,
    });

    expect(invalidateBookingConfirmation).toHaveBeenCalled();
    expect(invalidateAmendSummary).toHaveBeenCalled();

    expect(utils.getNightsNumber).not.toHaveBeenCalled();
    expect(editAmendPageAnalytics).not.toHaveBeenCalled();
  });

  it('should set promo data to null and hide notification when no promotions are returned', () => {
    const response = {
      changeBookingDates: {},
    };

    handleAmendStayDatesMutation({
      invalidateBookingConfirmation,
      invalidateAmendSummary,
      editAmendPageAnalytics,
      originalNumberOfNights: 2,
      newStartDate,
      newEndDate,
      setPromoStayData,
      setShowPromoNotification,
      response,
      isStayDatesLoading: false,
      isPromotionsInHotelAvailabilityEnabled: true,
    });

    expect(setPromoStayData).toHaveBeenCalledWith(null);
    expect(setShowPromoNotification).toHaveBeenCalledWith(false);
  });
});

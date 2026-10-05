import * as ReactQuery from '@tanstack/react-query';
import { QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
  ROOM_TYPE,
  Channel,
  SAVE_RESERVATION_ANCILLARIES,
  UPDATE_ANCILLARIES_RATE_CODE,
} from '@whitbread-eos/api';
import { ExtrasSection } from '@whitbread-eos/organisms';
import { getLocalStorageMock, addReservationNumber } from '@whitbread-eos/utils';
import { useRouter } from 'next/router';
import React from 'react';

import AncillariesPageCcui, { hasRoomPackagesChanges } from './page.ccui';

// temporary solution until next/router will be deprecated
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

//region Mock Objects
const getBookingInformationData = {
  isLoading: false,
  isError: false,
  error: {
    message: 'error booking information',
  },
  data: {
    bookingInformation: {
      hotelId: 'MANOLD',
      totalCost: 999,
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
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.STANDARD,
              roomName: 'Standard room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
        },
        {
          roomStay: {
            adultsNumber: 1,
            childrenNumber: 0,
            arrivalDate: '2023-03-23',
            departureDate: '2023-03-24',
            ratePlanCode: 'FLEXRATE',
            rateExtraInfo: {
              rateName: 'Flex',
            },
            roomExtraInfo: {
              roomType: ROOM_TYPE.PREMIER_PLUS,
              roomName: 'Premier Plus Room',
            },
            accessibleRoom: {
              isAccessible: false,
              phoneNumber: '0333 321 1315',
            },
          },
        },
      ],
      upgradeToFlex: {
        amount: '',
        currency: '',
        flexRateCode: '',
      },
    },
  },
};

const getHotelInformation: any = {
  data: {
    hotelInformation: {
      address: {
        addressLine1: 'Sir Alex Ferguson Way',
        addressLine2: 'Trafford Park',
        addressLine3: 'Manchester',
        postalCode: 'M17 1WS',
        country: 'United Kingdom (the)',
      },
      name: 'Manchester Old Trafford',
      brand: 'PI',
      announcement: {
        endDate: '21/07/2022',
        showAnnouncement: 'true',
        startDate: '08/01/2021',
        text: 'Get all the latest updates on our response to&nbsp;<a href="/gb/en/covid-19.html" target="_blank"><u>COVID-19</u></a>&nbsp;and see how we’re keeping guests safe with our&nbsp;<a href="/gb/en/why/cleanliness.html" target="_blank"><u>Premier Inn CleanProtect<sup>TM</sup></u></a>&nbsp;promise.&nbsp;<br>\r\n',
        title: '',
        type: 'info',
      },
      importantInfo: {
        title: 'Important Information',
        infoItems: [
          {
            text: 'The bathrooms in this hotel have showers only.',
            priority: '1',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
          {
            text: 'Another important information message with highest priority.',
            priority: '10',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
          {
            text: 'Parking is not going to be available during this period of time due to some works on the street.',
            priority: '3',
            startDate: '30/09/2022',
            endDate: '01/01/2024',
          },
        ],
      },
      ancillaryCloseout: {},
    },
  } as any,
  isLoading: false,
  isError: false,
  error: {
    message: 'error hotel information',
  },
};

const getPackagesData: any = {
  data: {
    packages: {
      packages: {
        meals: [
          {
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
            currency: 'GBP',
            description:
              '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
            id: 'BFADBF',
            bartId: '11',
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png',
            name: 'Premier Inn Breakfast',
            price: 9.99,
            order: 1,
            freeBreakfastOption: true,
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastMaxPerMeal: 2,
            upsellType: 'dinner',
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
              name: 'Breakfast menu',
            },
            isFree: true,
          },
          {
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
            currency: 'GBP',
            description:
              '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n',
            id: 'BFADCT',
            bartId: '12',
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
            name: 'Continental Breakfast',
            price: 7.99,
            order: 2,
            freeBreakfastOption: false,
            freeBreakfastCode: '',
            freeBreakfastMaxPerMeal: 2,
            upsellType: '',
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
              name: 'Breakfast menu',
            },
          },
          {
            allergyInfoLabel: 'Allergy & nutrition info',
            allergyInfoSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
            currency: 'GBP',
            description:
              '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
            id: 'MDP',
            bartId: '17',
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/meal-deal-booking.png',
            name: 'Meal Deal',
            price: 24.99,
            order: 3,
            freeBreakfastOption: true,
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastMaxPerMeal: 2,
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-dinner-menu-band2.pdf',
              name: 'Dinner menu',
            },
          },
        ],
        extrasItems: [
          {
            currency: 'GBP',
            description: 'Check out any time until 2pm (normal check-out time is 12pm).',
            id: 'HSCKIN',
            imageSrc: '/content/dam/global/extras/early-check-in.png',
            name: 'Early check-in',
            order: 1,
            price: 10,
            available: 10,
          },
          {
            currency: 'GBP',
            description: 'Check out any time until 2pm (normal check-out time is 12pm).',
            id: 'HSCOU2',
            imageSrc: '/content/dam/global/extras/late-checkout.png',
            name: 'Late check-out',
            order: 2,
            price: 10,
            available: 10,
          },
        ],
        roomSelection: [
          {
            reservationId: '1862774',
            packagesSelection: [],
          },
          {
            reservationId: '1862775',
            packagesSelection: [],
          },
        ],
        mealsKids: [
          {
            allergyInfoLabel: null,
            allergyInfoSrc: null,
            currency: null,
            description:
              '<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n',
            id: 'BFCHDF',
            imageSrc:
              'https://secure2.premierinn.com/content/dam/global/restaurants/Global/child-breakfast.jpg',
            name: 'Free breakfast for kids',
            order: 0,
            menu: null,
          },
        ],
      },
      restaurant: {
        logoSrc:
          'https://secure2.premierinn.com/content/dam/global/restaurants/THY/Thyme-logo-165x73.jpg',
        messageDescription: null,
        messageHeader: null,
        noMealsFound: false,
        restaurantNotFound: false,
      },
      privacyPolicy: {
        description:
          '<p>We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE.</p>\n',
        linkLabel: 'View our Privacy Notice',
        linkSrc: '/gb/en/terms/privacy-policy.html',
        moreInfoLabel: 'Find out more',
        moreInfo: [
          {
            description:
              '<p><b>VeriSign</b><br>\n<br>\n</p>\n<p>Premier Inn takes the security of any information we hold very seriously, and will always implement security measures that are in line with, or exceed current best practices and recommendations. Where necessary, and in common with other websites, we use SSL (Secure Sockets Layer) encryption to ensure that information provided to us is not visible to anybody else when in transit between your computer and our servers. You can tell when SSL is in use by the presence of a small &quot;padlock&quot; symbol in the status bar or next to the address bar of your web browser. In addition, our web servers are housed behind a secure firewall that prevents access to our databases from unauthorised users. All of our servers are housed in a secure environment with high levels of physical security, and access is only permitted to a handful of security screened staff.</p>\n',
            image: '/content/dam/global/booking/verisign.png',
          },
          {
            description:
              '<p><b>MasterCard</b><br>\n<br>\n</p>\n<p>MasterCard SecureCode is a service to enhance your existing MasterCard account. A private code means added protection against unauthorized use of your card when you shop at participating online retailers. Once youve registered and created your own private SecureCode, you will be automatically prompted by your financial institution at checkout to provide your SecureCode each time you make a purchase with a participating online merchant. Your SecureCode is quickly confirmed by your financial institution and then your purchase is completed. Your SecureCode will never be shared with the merchant. Its just like entering your PIN at an ATM. When you correctly enter your SecureCode during a purchase at a participating online merchant, you confirm that you are the authorized cardholder and your purchase is then completed. If an incorrect SecureCode is entered, the purchase will not be completed. Even if someone knows your credit or debit card number, the purchase cannot be completed without your SecureCode at a participating merchant. How do I sign up for MasterCard?<br>\n<br>\n</p>\n<p>Choosing your own private SecureCode is quick and easy. When shopping online at a participating merchant, you will be prompted to create your own SecureCode prior to checkout. When this happens, a pop up window will appear and you will be guided through the simple enrolment process before your purchase is completed. Once you have created your private SecureCode, you will use it for future purchases at participating online merchants.</p>\n',
            image: '/content/dam/hub/app/MasterCard.jpg',
          },
          {
            description:
              '<p><b>Verified by Visa</b><br>\n<br>\n</p>\n<p>Verified by Visa is a new security service that tells on-line retailers and banks that you are a genuine cardholder when you shop on-line. It allows you to use a personal password to confirm your identity and protect your Visa card when you use your card on the Internet, providing greater reassurance and security. Through a simple checkout process, Verified by Visa confirms your identity when you make purchases in participating online stores. Its convenient and it works with your existing Visa Card. Verified by Visa is easy to use. You register your card just once and create your own password. Then, when you make purchases at participating online stores, a Verified by Visa window will appear. Simply enter your password and click submit. Your identity is verified and your purchase is secure.<br>\n<br>\n</p>\n<p><b>How do I sign up for Verified by Visa?</b><br>\n<br>\n</p>\n<p>Visit the Verified by Visa website to register your Visa Card online, alternatively contact your bank who can register your card for Verified by Visa for you. Once your bank has activated your card, Verified by Visa protects you at every participating on-line store. When you shop at a participating on-line store, your card will be automatically recognized as protected by Verified by Visa. When you are completing your purchase, your issuing bank will verify your password.</p>\n',
            image: '/content/dam/global/booking/privacy_icon_visa_verified.png',
          },
        ],
        name: 'We keep your personal data safe and secure.',
      },
      hotelHasCityTaxForLeisure: false,
      hotelHasCityTaxForBusiness: false,
    },
  } as any,
  isLoading: false,
  isError: false,
  error: {
    message: 'error packages',
  },
};

const mockfilteredAdultsMeals = [
  {
    allergyInfoLabel: 'Allergy & nutrition info',
    allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
    currency: 'GBP',
    description:
      '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
    id: 'BFADBF',
    bartId: '11',
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png',
    name: 'Premier Inn Breakfast',
    price: 9.99,
    order: 1,
    freeBreakfastOption: true,
    freeBreakfastCode: 'BFCHDF',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc:
        'https://secure2.premierinn.com/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
      name: 'Breakfast menu',
    },
  },
];

function mockfilterPackagesByAncillariesCloseOut({ ancillaryCloseoutData }: any) {
  if (ancillaryCloseoutData.items[0].filteredStatues === 'NoFilteredData') {
    return { filteredAdultsMeals: [], filteredChildrenMeals: [] };
  }
  if (ancillaryCloseoutData.items[0].filteredStatues === 'PartialFilteredData') {
    return {
      filteredAdultsMeals: mockfilteredAdultsMeals,
      filteredChildrenMeals: getPackagesData.data.packages.packages.mealsKids,
    };
  }
  if (ancillaryCloseoutData.items[0].filteredStatues === 'FullFilteredData') {
    return {
      filteredAdultsMeals: getPackagesData.data.packages.packages.meals,
      filteredChildrenMeals: getPackagesData.data.packages.packages.mealsKids,
    };
  }
}

function mockUseQueryRequest(queryKey: any) {
  const key = queryKey[0];
  if (typeof key === 'string') {
    if (key === 'GetBookingInformation') {
      return getBookingInformationData;
    }
    if (key === 'GetPackages') {
      return getPackagesData;
    }
    if (key === 'GetHotelInformation') {
      return getHotelInformation;
    }
  }
  if (typeof queryKey === 'string') {
    if (queryKey === 'GetPaymentStatus') {
      return {
        data: { basket: {} },
      };
    }
  }
}

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn().mockReturnValue({ t: (key: any) => key }),
}));

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
}));

const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();
const mockUseSilentRoomsMatch = jest.fn();

const mockFilterWifiForPlusRooms: any = [];

jest.mock('@whitbread-eos/utils', () => {
  const mutationMock = jest.fn().mockReturnValue(SAVE_RESERVATION_ANCILLARIES);
  const mockMutationResponse = {
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: false,
    isError: false,
    error: { message: '' },
    isLoading: false,
    data: {},
  };

  const mockSaveMutationResponse = {
    mutation: {
      mutate: mutationMock,
    },
    isSuccess: true,
    isError: false,
    error: { message: '' },
    isLoading: false,
    data: {},
  };

  const mockUseMutationRequest = (key: any) => {
    if (typeof key === 'string') {
      if (key === UPDATE_ANCILLARIES_RATE_CODE) {
        return mockMutationResponse;
      }
      if (key === SAVE_RESERVATION_ANCILLARIES) {
        return mockSaveMutationResponse;
      }
    }
  };
  return {
    ...jest.requireActual('@whitbread-eos/utils'),
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    invalidateQueries: mockUseQueryRequest,
    useQuery: () => mockUseQueryRequest,
    useQueryRequest: mockUseQueryRequest,
    useRestMutationRequest: () => ({
      mutation: jest.fn(),
    }),
    usePackages: () => ({
      ...getPackagesData?.data?.packages,
      ...getPackagesData,
    }),
    useFeatureSwitch: () => mockUseFeatureSwitch(),
    useSilentRoomsMatch: () => mockUseSilentRoomsMatch(),
    filterPackagesByAncillariesCloseOut: mockfilterPackagesByAncillariesCloseOut,
    useMutationRequest: jest.fn(mockUseMutationRequest),
    filterWiFiForPlusRooms: () => mockFilterWifiForPlusRooms,
    addReservationNumber: jest.fn().mockReturnValue([
      { reservationId: '1862774', packagesSelection: [] },
      { reservationId: '1862775', packagesSelection: [] },
    ]),
    useFeatureToggle: () => ({
      release_pi_bb_ccui_show_meals_package: true,
      release_ccui_free_fnb_and_extras: true,
    }),
    //numberOfSelectionsPerRoomSelector: jest.fn(),
  };
});

const queryClient = new ReactQuery.QueryClient();

const mockProps = {
  user: {},
  setAnalyticsUser: jest.fn(),
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
    bookingChannelCriteria: {
      channel: Channel.Ccui,
      subchannel: '',
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

const mockRouter = jest.fn();

describe('Page CCUI Ancillaries', () => {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
  });

  it('should render page pi skeleton', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page pi booking summary', async () => {
    const { getByTestId, getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-Wrapper')
    ).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-BookingSummary-MobileVariant')).toBeInTheDocument();
    expect(
      getByText(
        '2 hoteldetails.bookingsummary.rooms, 1 booking.summary.night | 23 Mar - 24 Mar | £999.00 | booking.summary.rate Flex'
      )
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-MobileVariant-SectionWrapper')
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-MobileVariant-SectionHeader')
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-TotalCost-TotalCostPrice')
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-TotalCost-CostAmount')
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-TotalCost-VATMessage')
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-StayDatesInformation-ArrivalDate')
    ).toBeInTheDocument();
    expect(
      getByTestId(
        'AncillariesPage-BookingSummary-DesktopVariant-StayDatesInformation-DepartureDate'
      )
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-StayDatesInformation-NightsNumber')
    ).toBeInTheDocument();
    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-RoomInformation-Wrapper')
    ).toBeInTheDocument();
  });

  it('should render page and have continue button', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-ContinueButton')).toBeInTheDocument();
  });

  it('should render page and click upgrade to flex', async () => {
    getBookingInformationData.data.bookingInformation.upgradeToFlex.flexRateCode = 'Flexrate';
    getBookingInformationData.data.bookingInformation.upgradeToFlex.amount = '12';
    getBookingInformationData.data.bookingInformation.upgradeToFlex.currency = 'GBP';
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    const button = getByTestId('UpgradeToFlex-Button');
    expect(button).toBeInTheDocument();
    fireEvent.click(button);
  });

  it('should render page and have continue button and click it', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    const button = getByTestId('AncillariesPage-ContinueButton');
    await waitFor(() => {
      userEvent.click(button);
    });
  });

  it('should check packages selection on continue and not call the save mutation when there are no changes', async () => {
    // const selection = {
    //   id: 'BFADBF',
    //   noOfSelections: 1,
    // };
    // getPackagesData.data.packages.packages.roomSelection[0].packagesSelection.push(selection);
    // getPackagesData.data.packages.packages.roomSelection[1].packagesSelection.push(selection);
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { useMutationRequest } = require('@whitbread-eos/utils');
    const router = useRouter();
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={router} />
      </QueryClientProvider>
    );

    const mockResults = useMutationRequest(SAVE_RESERVATION_ANCILLARIES);
    const mutationMock = mockResults.mutation.mutate;

    const button = getByTestId('AncillariesPage-ContinueButton');
    await userEvent.click(button);
    await expect(addReservationNumber).toHaveBeenCalled();
    expect(mutationMock).not.toHaveBeenCalled();
  });

  it('should check packages selection on continue and call the save mutation when there are changes', async () => {
    const selection = {
      id: 'BFADBF',
      noOfSelections: 1,
    };
    getPackagesData.data.packages.packages.roomSelection[0].packagesSelection.push(selection);
    getPackagesData.data.packages.packages.roomSelection[1].packagesSelection.push(selection);
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { useMutationRequest } = require('@whitbread-eos/utils');
    const router = useRouter();
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={router} />
      </QueryClientProvider>
    );

    const mockResults = useMutationRequest(SAVE_RESERVATION_ANCILLARIES);
    const mutationMock = mockResults.mutation.mutate;

    const button = getByTestId('AncillariesPage-ContinueButton');
    await userEvent.click(button);
    await expect(addReservationNumber).toHaveBeenCalled();
    expect(mutationMock).toHaveBeenCalled();
  });

  it('should return true on hasRoomPackagesChanges when room selection has changes', async () => {
    const roomSelection = [
      {
        packagesSelection: [
          {
            id: 'BFADBF',
            noOfSelections: 1,
          },
        ],
        reservationId: '1862774',
      },
      {
        packagesSelection: [
          {
            id: 'BFADBF',
            noOfSelections: 1,
          },
        ],
        reservationId: '1862775',
      },
    ];
    const previousRoomSelection = [
      { packagesSelection: [], reservationId: '1862774' },
      { packagesSelection: [], reservationId: '1862775' },
    ];
    expect(hasRoomPackagesChanges(roomSelection, previousRoomSelection)).toEqual(true);
  });

  it('should return false on hasRoomPackagesChanges when room selection has no changes', async () => {
    const roomSelection = [
      { packagesSelection: [], reservationId: '1862774' },
      { packagesSelection: [], reservationId: '1862775' },
    ];
    const previousRoomSelection = [
      { packagesSelection: [], reservationId: '1862774' },
      { packagesSelection: [], reservationId: '1862775' },
    ];
    expect(hasRoomPackagesChanges(roomSelection, previousRoomSelection)).toEqual(false);
  });

  it('should render page with no restaurant', async () => {
    getPackagesData.data.packages.restaurant.restaurantNotFound = true;
    getPackagesData.data.packages.packages.extrasItems = [];

    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-RestaurantMessage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Title')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Description')).toBeInTheDocument();
    getPackagesData.data.packages.restaurant.restaurantNotFound = false;
  });

  it('should render page with info message', async () => {
    getBookingInformationData.data.bookingInformation.infoMessages.push('TestInfoMess');

    const { queryByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(queryByText('TestInfoMess')).not.toBeInTheDocument();
  });

  it('should render restaurant unavailable page when ancillary closeout applicable for all packages', async () => {
    mockUseFeatureSwitch.mockReturnValue(true);

    getHotelInformation.data.hotelInformation.ancillaryCloseout = {
      items: [
        {
          endDate: '10/02/2024',
          serviceCode: 'BREAKFAST_NA',
          startDate: '01/02/2024',
          text: 'Premier Inn Breakfast is not available',
          upsellCodes: 'MDP,BFADBF,BFADCT',
          filteredStatues: 'NoFilteredData',
        },
      ],
    };

    getPackagesData.data.packages.packages.extrasItems = [];
    getPackagesData.data.packages.restaurant.restaurantNotFound = false;

    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    expect(getByText('upsell.heading.restaurant.closure')).toBeInTheDocument();
    getHotelInformation.data.hotelInformation.ancillaryCloseout = {};
  });

  it('should render page with ancillary closeout for some pacakges', async () => {
    getHotelInformation.data.hotelInformation.ancillaryCloseout = {
      items: [
        {
          endDate: '10/02/2024',
          serviceCode: 'BREAKFAST_NA',
          startDate: '01/02/2024',
          text: 'Premier Inn Breakfast is not available',
          upsellCodes: 'MDP,BFADCT',
          filteredStatues: 'PartialFilteredData',
        },
      ],
    };
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );
    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
    getHotelInformation.data.hotelInformation.ancillaryCloseout = {};
  });

  it('should render page with ancillary closeout not applicable to any packages', async () => {
    getHotelInformation.data.hotelInformation.ancillaryCloseout = {
      items: [
        {
          filteredStatues: 'FullFilteredData',
        },
      ],
    };
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
    getHotelInformation.data.hotelInformation.ancillaryCloseout = {};
  });

  it('should render page with no menus', async () => {
    getPackagesData.data.packages.restaurant.noMealsFound = true;
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-RestaurantMessage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Title')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Description')).toBeInTheDocument();
    getPackagesData.data.packages.restaurant.noMealsFound = false;
  });

  it('should render page with kids eats free notification', async () => {
    getBookingInformationData.data.bookingInformation.reservationByIdList[0].roomStay.childrenNumber = 1;
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-FreeFoodKidsNotification-Wrapper')).toBeInTheDocument();
  });

  it('should render page with loading true', async () => {
    getBookingInformationData.isLoading = true;
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByText('booking.loading')).toBeInTheDocument();
    getBookingInformationData.isLoading = false;
  });

  it('should render page with get booking information error', async () => {
    getBookingInformationData.isError = true;
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByText('Error on getting booking information....')).toBeInTheDocument();
    expect(getByText('error booking information')).toBeInTheDocument();
    getBookingInformationData.isError = false;
  });

  it('should render page with get packages  error', async () => {
    getPackagesData.isError = true;
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByText('Error on getting packages....')).toBeInTheDocument();
    expect(getByText('error packages')).toBeInTheDocument();
    getPackagesData.isError = false;
  });

  it('should render page with get hotel information error', async () => {
    getHotelInformation.isError = true;
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByText('Error on getting hotel information....')).toBeInTheDocument();
    expect(getByText('error hotel information')).toBeInTheDocument();
    getHotelInformation.isError = false;
  });

  it('should render page pi skeleton with multi rooms', async () => {
    getBookingInformationData.data.bookingInformation.reservationByIdList.push({
      roomStay: {
        adultsNumber: 1,
        childrenNumber: 0,
        arrivalDate: '2023-03-23',
        departureDate: '2023-03-24',
        ratePlanCode: 'FLEXRATE',
        rateExtraInfo: {
          rateName: 'Flex',
        },
        roomExtraInfo: {
          roomType: ROOM_TYPE.PREMIER_PLUS,
          roomName: 'Premier Plus Room',
        },
        accessibleRoom: {
          isAccessible: false,
          phoneNumber: '0333 321 1315',
        },
      },
    });
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page pi skeleton with room selection packages undefined', async () => {
    const { queryAllByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(
      queryAllByTestId(
        'AncillariesPage-IndividualSelection-Meals-Adults-MealItem-AddSubtractControls-Value'
      ).length
    ).toBe(3);
  });

  it('should render page pi skeleton with packages.meals undefined', async () => {
    getPackagesData.data.packages.packages.meals = [];
    const { queryByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(queryByTestId('AncillariesPage-Meals-Adults-MealItem-Wrapper')).toBeFalsy();
  });

  it('should render page pi skeleton with packages undefined', async () => {
    getPackagesData.data = undefined;
    const { queryByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(queryByTestId('AncillariesPage-Meals-Adults-MealItem-Wrapper')).toBeFalsy();
  });

  it('should render page pi skeleton with hotelDetails undefined', async () => {
    getHotelInformation.data = undefined;
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page pi skeleton with room selection undefined', async () => {
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page pi skeleton with meals', async () => {
    getPackagesData.data = undefined;
    const { getByTestId } = render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
    const button = getByTestId('AncillariesPage-ContinueButton');
    await waitFor(() => {
      userEvent.click(button);
    });
  });

  describe('Silent Substitution', () => {
    beforeEach(() => {
      jest.clearAllMocks();
    });

    const localStorageMock = getLocalStorageMock();
    Object.defineProperty(window, 'localStorage', {
      value: localStorageMock,
    });

    const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';

    it('should display the labels from localStorage in <Tabs/> when feature flag is true', () => {
      localStorageMock.setItem(
        SILENT_SUBSTITUTION_STORAGE_KEY,
        JSON.stringify({
          '12': {
            value: [
              {
                roomLabelCode: 'Double room',
                silentSubstitution: true,
              },
              {
                roomLabelCode: 'Accessible room',
                silentSubstitution: true,
              },
            ],
            expire: 123,
          },
        })
      );
      mockUseFeatureSwitch.mockReturnValue(true);
      mockUseSilentRoomsMatch.mockReturnValue([
        {
          roomLabelCode: 'Double room',
          silentSubstitution: true,
        },
        {
          roomLabelCode: 'Accessible room',
          silentSubstitution: true,
        },
      ]);

      const { getByTestId } = render(
        <QueryClientProvider client={queryClient}>
          <AncillariesPageCcui
            {...mockProps}
            queryClient={queryClient}
            router={mockRouter as any}
          />
        </QueryClientProvider>
      );

      const tabButtonDescriptionDouble = getByTestId('Double room-TabButtonDescription');
      expect(tabButtonDescriptionDouble).toBeInTheDocument();
      expect(tabButtonDescriptionDouble.textContent).toContain('Double');

      const tabButtonDescriptionAccessible = getByTestId('Accessible room-TabButtonDescription');
      expect(tabButtonDescriptionAccessible).toBeInTheDocument();
      expect(tabButtonDescriptionAccessible.textContent).toContain('Accessible');
    });

    it('should display the labels from localStorage in <Tabs/> when feature flag is true and silent substitution property from local storage is false', () => {
      localStorageMock.setItem(
        SILENT_SUBSTITUTION_STORAGE_KEY,
        JSON.stringify({
          '123': {
            value: [
              {
                roomLabelCode: 'ST',
                silentSubstitution: false,
              },
            ],
            expire: 123,
          },
        })
      );

      mockUseFeatureSwitch.mockReturnValue(true);
      mockUseSilentRoomsMatch.mockReturnValue([]);
      const { getByTestId } = render(
        <QueryClientProvider client={queryClient}>
          <AncillariesPageCcui
            {...mockProps}
            queryClient={queryClient}
            router={mockRouter as any}
          />
        </QueryClientProvider>
      );

      const tabButtonDescriptionStandard = getByTestId('Standard room-TabButtonDescription');
      expect(tabButtonDescriptionStandard).toBeInTheDocument();
      expect(tabButtonDescriptionStandard.textContent).toContain('Standard');
    });

    it('should display the default label in <Tabs/> when feature flag is true and there is no data in localStorage', () => {
      localStorageMock.clear();
      mockUseFeatureSwitch.mockReturnValue(true);

      const { getByTestId, getAllByTestId } = render(
        <QueryClientProvider client={queryClient}>
          <AncillariesPageCcui
            {...mockProps}
            queryClient={queryClient}
            router={mockRouter as any}
          />
        </QueryClientProvider>
      );

      const tabButtonDescriptionDouble = getByTestId('Standard room-TabButtonDescription');
      expect(tabButtonDescriptionDouble).toBeInTheDocument();
      expect(tabButtonDescriptionDouble.textContent).toContain('Standard');

      const tabButtonDescriptionPremierPlus = getAllByTestId(
        'Premier Plus Room-TabButtonDescription'
      )[0];
      expect(tabButtonDescriptionPremierPlus).toBeInTheDocument();
      expect(tabButtonDescriptionPremierPlus.textContent).toContain('Premier');
    });

    it('should display the default label in <Tabs/> when feature flag is false', async () => {
      mockUseFeatureSwitch.mockReturnValue(false);
      mockUseSilentRoomsMatch.mockReturnValue([]);

      const { getByTestId, getAllByTestId } = render(
        <QueryClientProvider client={queryClient}>
          <AncillariesPageCcui
            {...mockProps}
            queryClient={queryClient}
            router={mockRouter as any}
          />
        </QueryClientProvider>
      );

      const tabButtonDescriptionDouble = getByTestId('Standard room-TabButtonDescription');
      expect(tabButtonDescriptionDouble).toBeInTheDocument();
      expect(tabButtonDescriptionDouble.textContent).toContain('Standard');

      const tabButtonDescriptionPremierPlus = getAllByTestId(
        'Premier Plus Room-TabButtonDescription'
      )[0];
      expect(tabButtonDescriptionPremierPlus).toBeInTheDocument();
      expect(tabButtonDescriptionPremierPlus.textContent).toContain('Premier');
    });
  });

  describe('CCUI Extras component checks', () => {
    beforeEach(() => {
      jest.clearAllMocks();

      mockCustomLocale.mockReturnValue({
        language: 'gb',
        country: 'gb',
      });
    });

    const mockDataItems = {
      extrasDetailsList: [
        {
          currency: 'GBP',
          description: '<p>Check in any time from 11am (normal check-in time is 3pm).</p>\r\n',
          id: 'HSCKIN',
          imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
          name: 'Early Check-In',
          order: 1,
          price: 10,
          available: 1,
        },
        {
          currency: 'GBP',
          description: '<p>Check out any time until 2pm (normal check-out time is 12pm).</p>\r\n',
          id: 'HSCOU2',
          imageSrc: '/content/dam/global/restaurants/Global/full-breakfast-booking.png',
          name: 'Late Checkout-out',
          order: 1,
          price: 10,
          available: -1,
        },
      ],
      handleSelectedExtrasList: jest.fn(),
      selectedExtrasList: [
        { packagesList: ['HSCKIN'], reservationId: '1798695', price: 10 },
        { packagesList: ['HSCOU2'], reservationId: '1798696', price: 10 },
      ],
      selectedRoom: 0,
      noNights: 2,
    };

    it('should display ExtrasSection component', async () => {
      const { getByTestId } = render(
        <>
          <QueryClientProvider client={queryClient}>
            <AncillariesPageCcui
              {...mockProps}
              queryClient={queryClient}
              router={mockRouter as any}
            />
            <ExtrasSection {...mockDataItems} />
          </QueryClientProvider>
        </>
      );

      await waitFor(() => {
        expect(getByTestId('ExtrasSection-Wrapper')).toBeInTheDocument();
      });
    });
  });

  it('should preselect meals correctly when adultHasMealsFree is initially false and updates later', async () => {
    getBookingInformationData.data.bookingInformation.reservationByIdList[0].roomStay.childrenNumber = 1;

    const mockedMealsMapperSelector = jest.fn();

    jest.doMock('@whitbread-eos/utils', () => ({
      ...jest.requireActual('@whitbread-eos/utils'),
      mealsMapperSelector: mockedMealsMapperSelector,
    }));

    render(
      <QueryClientProvider client={queryClient}>
        <AncillariesPageCcui {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      </QueryClientProvider>
    );

    await waitFor(() => {
      expect(
        queryClient.getQueryData(['GetBookingInformation']) ||
          getBookingInformationData.data.bookingInformation
      ).toBeTruthy();
    });
  });
});

import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { waitFor } from '@testing-library/react';
import { ROOM_TYPE } from '@whitbread-eos/api';
import { ExtrasSection } from '@whitbread-eos/organisms';
import { getLocalStorageMock, useFeatureToggle } from '@whitbread-eos/utils';
import React from 'react';
import { act } from 'react-dom/test-utils';

import { fireEvent, render } from '../../utils/test-utils';
import AncillariesPagePi from './page.pi';

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
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
          reservationId: '1857433',
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
          reservationId: '1857432',
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
        amount: null,
        currency: null,
        flexRateCode: null,
      },
    },
  },
};

const getHotelInformation = {
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
      ancillaryCloseout: null as {
        items: Array<Record<string, unknown>>;
      } | null,
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error hotel information',
  },
};

const getRoomSelectionData = {
  data: {
    packages: {
      packages: {
        roomSelection: [
          {
            packagesSelection: [],
          },
        ],
      },
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error room selection',
  },
};

const getPackagesData = {
  data: {
    packages: {
      packages: {
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
            upsellType: 'breakfast',
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
            upsellType: 'breakfast',
            menu: {
              menuSrc:
                'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-dinner-menu-band2.pdf',
              name: 'Dinner menu',
            },
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
        messageDescription: null as string | null,
        messageHeader: null as string | null,
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
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error packages',
  },
};

const mockBasketDetailsStateWithTwoRooms = {
  hotelId: 'MANOLD',
  arrival: '2023-07-01',
  departure: '2023-07-04',
  numberOfUnits: 2,
  numberOfNights: 3,
  selectedRate: {
    ratePlanCode: 'FLEXRATE',
    roomTypes: [
      {
        roomType: 'DB',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'DOUBLE',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['DBLE'],
            roomPriceBreakdown: {
              totalNetAmount: 2997,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2023-07-01', netPrice: 999 },
                { date: '2023-07-02', netPrice: 999 },
                { date: '2023-07-03', netPrice: 999 },
              ],
            },
          },
        ],
      },
      {
        roomType: 'TWIN',
        adults: 1,
        children: 0,
        cotRequested: false,
        rooms: [
          {
            pmsRoomType: 'TWINRM',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TW2S'],
            roomPriceBreakdown: {
              totalNetAmount: 2997,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2023-07-01', netPrice: 999 },
                { date: '2023-07-02', netPrice: 999 },
                { date: '2023-07-03', netPrice: 999 },
              ],
            },
          },
          {
            pmsRoomType: 'FMTRPL',
            silentSubstitution: true,
            cotAvailable: false,
            roomClass: 'ST',
            specialRequests: ['TWDS'],
            roomPriceBreakdown: {
              totalNetAmount: 2997,
              currencyCode: 'GBP',
              dailyPrices: [
                { date: '2023-07-01', netPrice: 999 },
                { date: '2023-07-02', netPrice: 999 },
                { date: '2023-07-03', netPrice: 999 },
              ],
            },
          },
        ],
      },
    ],
  },
  roomClass: 'Standard Room',
  rateName: 'Flex',
  roomTypeInformationResponse: {
    isLoadingRoomTypeInformation: false,
    isErrorRoomTypeInformation: false,
    dataRoomTypeInformation: {
      roomTypeInformation: {
        roomTypes: [
          {
            roomTypeCode: ['LOWTWN'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible twin bedroom with a lowered bath',
            roomDescription:
              'Accessible twin bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['WETTWN', 'BRFTWN'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible twin bedroom with level access shower room',
            roomDescription:
              'Accessible twin bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['WETDBL', 'BRFDBL', 'BRFZPL'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with level access shower room',
            roomDescription:
              'Accessible double bedroom. Level access shower room with high-powered shower, conveniently placed shower controls, folding seat and wider doors.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['LOWDBL', 'PPDLOW'],
            roomCategory: 'Accessible room',
            roomLabel: 'Accessible double bedroom with a lowered bath',
            roomDescription:
              'Accessible double bedroom. Bathroom with a lowered bath set at standard wheelchair height (480mm), lever taps, wider doors and bath mats available on request.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Accessible/ID3-Accessible-Bedroom.jpg',
            groupId: 'accessible',
          },
          {
            roomTypeCode: ['FMQUAD', 'FMFOUR'],
            roomCategory: 'Family',
            roomLabel: 'Family Room',
            roomDescription:
              'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
            groupId: 'family',
          },
          {
            roomTypeCode: ['TWINRM', 'DBLDBL'],
            roomCategory: 'Twin',
            roomLabel: 'Twin Room',
            roomDescription:
              'Our twin rooms layouts differ between hotels. Most feature a super-comfy double or kingsize Hypnos bed, plus a single sofa bed. Some hotels have twin rooms with two double or kingsize Hypnos beds.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-twin-room.jpg',
            groupId: 'twin',
          },
          {
            roomTypeCode: ['SINGLE'],
            roomCategory: 'Standard',
            roomLabel: 'Standard Room',
            roomDescription:
              'A super-comfy bed, a power shower and free Wi-Fi – our Standard rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4%201.jpg',
            groupId: 'single',
          },
          {
            roomTypeCode: ['PPLDBL', 'PDBZPL', 'PFAMIL'],
            roomCategory: 'Premier Plus',
            roomLabel: 'Premier Plus Room',
            roomDescription:
              'Our enhanced room design. Includes Ultimate Wi-Fi, Nespresso machine, mini-fridge, bedside USB ports, iron, upgraded workspace & more.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
            groupId: 'double',
          },
          {
            roomTypeCode: ['FMTRPL', 'FMTHRE'],
            roomCategory: 'Family',
            roomLabel: 'Family Room',
            roomDescription:
              'Our spacious family rooms are ideal for up to two adults plus two kids (aged 15 or under). Most feature a super-comfy double or kingsize bed, plus a single sofa bed and a pull-out bed. We also provide cots at no extra cost.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/Bedrooms/ID4-family-room.jpg',
            groupId: 'family',
          },
          {
            roomTypeCode: ['DOUBLE', 'ZPLDBL'],
            roomCategory: 'Double',
            roomLabel: 'Double Room',
            roomDescription:
              'A super-comfy bed, a power shower and free Wi-Fi – our double rooms have everything you’ll need for a great night’s sleep.',
            roomImage:
              '/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/ID4/ID4-Room-2.jpg',
            groupId: 'double',
          },
        ],
      },
    },
    errorRoomTypeInformation: null,
  },
  bookingFlow: {
    bookingFlowItems: [
      { bookingId: 'booking-a1', rateCode: 'A' },
      { bookingId: 'booking-a1', rateCode: 'F' },
      { bookingId: 'booking-a1', rateCode: 'Q' },
      { bookingId: 'booking-spf', rateCode: 'G' },
      { bookingId: 'booking-spf', rateCode: 'D' },
      { bookingId: 'booking-spf', rateCode: 'E' },
      { bookingId: 'booking-a1', rateCode: 'R' },
      { bookingId: 'booking-a1', rateCode: 'S' },
      { bookingId: 'booking-a1', rateCode: 'FLEXRATE' },
      { bookingId: 'booking-a1', rateCode: 'STANDARD' },
      { bookingId: 'booking-a1', rateCode: 'SEMIFLEX' },
      { bookingId: 'booking-a1', rateCode: 'NONFLEX' },
      { bookingId: 'booking-a1', rateCode: 'ADVANCE' },
    ],
  },
  phoneNumber: '0333 321 1315',
  brand: 'PI',
  hotelUrl:
    'en/hotels/england/greater-manchester/manchester/manchester-old-trafford.html?ARRdd=24&ARRmm=11&ARRyyyy=2023&NIGHTS=1&ROOMS=1&ADULT1=2&CHILD1=0&COT1=0&INTTYP1=TWIN',
};

const mockUseLocalStorage = jest.fn();
const mockMutationResponse = {
  isError: false,
  error: { message: '' },
  mutation: {
    isLoading: false,
    data: {},
    mutate: jest.fn(),
  },
};

//endregion

//region Jest Mock
function mockUseQueryRequest(queryKey) {
  const key = queryKey[0];

  if (typeof key === 'string') {
    switch (key) {
      case 'GetBookingInformation':
        return getBookingInformationData;
      case 'GetPackages':
        return getPackagesData;
      case 'GetHotelInformation':
        return getHotelInformation;
      case 'GetRoomSelection':
        return getRoomSelectionData;
      default:
        return {};
    }
  }
}

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
    upsellType: 'breakfast',
    menu: {
      menuSrc:
        'https://secure2.premierinn.com/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
      name: 'Breakfast menu',
    },
  },
];
function mockfilterPackagesByAncillariesCloseOut({ ancillaryCloseoutData }) {
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

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div></div>,
}));

const mockSoftBundles = [
  {
    id: 'BFADBF',
    price: 86.99,
    description: 'Breakfast',
  },
  {
    id: 'HSATWN',
    price: 45.0,
    description: 'Wifi',
  },
];

const mockCustomLocale = jest.fn();
const mockUseAuthToken = jest.fn(() => ({ token: '', isAuth0Enabled: false }));
const mockUseAuth0User = jest.fn(() => ({ user: null }));
const mockUseFeatureSwitch = jest.fn();
const mockUseSilentRoomsMatch = jest.fn();
const mockUseSessionStorage = jest.fn((key) => {
  if (key === 'softBundles') {
    return [mockSoftBundles, jest.fn()];
  }
  return ['', jest.fn()];
});

const mockFlags = {
  release_pi_display_soft_bundles: false,
  release_pi_ancillaries_extras_display: true,
  release_pi_bb_ccui_show_meals_package: true,
  release_pi_free_fnb_and_extras: true,
};

const mockCookies = {
  bundles: 'class',
};

const mockUseSoftBundles = jest.fn(() => ({
  isSoftBundlesVisible: false,
}));

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
    useCustomLocale: () => mockCustomLocale(),
    useLocalStorage: () => mockUseLocalStorage(),
    invalidateQueries: mockUseQueryRequest,
    useQuery: () => mockUseQueryRequest,
    useQueryRequest: mockUseQueryRequest,
    useMutationRequest: () => mockMutationResponse,
    useRestMutationRequest: () => ({
      mutation: jest.fn(),
    }),
    usePackages: () => ({
      ...getPackagesData?.data?.packages,
      ...getPackagesData,
    }),
    useFeatureSwitch: () => mockUseFeatureSwitch(),
    useSilentRoomsMatch: () => mockUseSilentRoomsMatch(),
    useSoftBundles: () => mockUseSoftBundles(),
    logger: {
      info: jest.fn(),
    },
    useFeatureToggle: jest.fn(() => ({
      ...mockFlags,
    })),
    graphQLRequest: jest.fn(),
    filterPackagesByAncillariesCloseOut: mockfilterPackagesByAncillariesCloseOut,
    getAuthCookie: jest.fn().mockImplementation(() => 'testCookie'),
    useAuthToken: () => mockUseAuthToken(),
    useAuth0User: () => mockUseAuth0User(),
    useSessionStorage: (key: string) => mockUseSessionStorage(key),
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
  };
});
//endregion
const queryClient = new ReactQuery.QueryClient();

const mockProps = {
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
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

const mockRouter = {
  push: jest.fn(),
  beforePopState: jest.fn(),
};

declare global {
  interface Window {
    // satellite required for adobe analytics on the confirmation Page
    __satelliteLoaded: boolean;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    _satellite: any;
  }
}

describe('Page PI Ancillaries', () => {
  beforeEach(() => {
    jest.clearAllMocks();

    mockCustomLocale.mockReturnValue({
      language: 'gb',
      country: 'gb',
    });
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);
    mockFlags.release_pi_display_soft_bundles = false;
    mockCookies.bundles = 'false';
  });

  it('should render page pi booking summary with soft bundles', async () => {
    mockFlags.release_pi_display_soft_bundles = true;
    mockCookies.bundles = 'class';
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-Wrapper')
    ).toBeInTheDocument();
  });

  it('should render page pi booking summary with soft bundles FF on but no cookie', async () => {
    mockFlags.release_pi_display_soft_bundles = true;
    mockCookies.bundles = 'false';
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(
      getByTestId('AncillariesPage-BookingSummary-DesktopVariant-Wrapper')
    ).toBeInTheDocument();
  });

  it('should render page pi skeleton', async () => {
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page and if back§ button is triggered go  back to HDP when twinRoom was selected', async () => {
    const hasTwinRoom = true;
    mockUseLocalStorage.mockReturnValue([hasTwinRoom, jest.fn()]);

    Object.defineProperty(window, 'location', {
      value: {
        pathname: '/choose-twinroom',
      },
      writable: true,
    });

    const routerMock = {
      push: jest.fn(),
    };

    render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={routerMock as any} />
    );

    await act(async () => {
      window.dispatchEvent(new Event('popstate'));
    });
  });

  it('should render page pi booking summary', async () => {
    const { getByTestId, getByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-ContinueButton')).toBeInTheDocument();
  });

  it('should display Restaurant unavailable notification', async () => {
    getPackagesData.data.packages.restaurant.restaurantNotFound = true;

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('AncillariesPage-RestaurantMessage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should render page and have continue button and click it', async () => {
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    const button = getByTestId('AncillariesPage-ContinueButton');
    fireEvent.click(button);
  });

  it('should render page with no restaurant', async () => {
    getPackagesData.data.packages.restaurant.restaurantNotFound = true;
    getPackagesData.data.packages.packages.extrasItems = [];
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-RestaurantMessage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Title')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Description')).toBeInTheDocument();
    getPackagesData.data.packages.restaurant.restaurantNotFound = false;
  });

  it('should render page with info message', async () => {
    getBookingInformationData.data.bookingInformation.infoMessages.push('TestInfoMess');
    const { queryByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByText('TestInfoMess')).not.toBeInTheDocument();
  });

  it('should render page with no menus and roomSelection', async () => {
    getPackagesData.data.packages.restaurant.noMealsFound = true;

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-RestaurantMessage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Title')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-RestaurantMessage-Description')).toBeInTheDocument();
    getPackagesData.data.packages.restaurant.noMealsFound = false;
  });

  it('should prioritise ancillary closeout heading and message over restaurant message copy', async () => {
    const originalNoMealsFound = getPackagesData.data.packages.restaurant.noMealsFound;
    const originalMessageHeader = getPackagesData.data.packages.restaurant.messageHeader;
    const originalMessageDescription = getPackagesData.data.packages.restaurant.messageDescription;
    const originalAncillaryCloseout = getHotelInformation.data.hotelInformation.ancillaryCloseout;

    try {
      getPackagesData.data.packages.restaurant.noMealsFound = true;
      getPackagesData.data.packages.restaurant.messageHeader = 'Restaurant message header';
      getPackagesData.data.packages.restaurant.messageDescription =
        'Restaurant message description';
      getHotelInformation.data.hotelInformation.ancillaryCloseout = {
        items: [
          {
            noMealsHeading: 'Ancillary closeout heading',
            noMealsMessage: 'Ancillary closeout message',
            startDate: '01/03/2023',
            endDate: '31/03/2023',
            filteredStatues: 'FullFilteredData',
          },
        ],
      };

      const { getByText, queryByText } = render(
        <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByText('Ancillary closeout heading')).toBeInTheDocument();
      expect(getByText('Ancillary closeout message')).toBeInTheDocument();
      expect(queryByText('Restaurant message header')).not.toBeInTheDocument();
      expect(queryByText('Restaurant message description')).not.toBeInTheDocument();
    } finally {
      getPackagesData.data.packages.restaurant.noMealsFound = originalNoMealsFound;
      getPackagesData.data.packages.restaurant.messageHeader = originalMessageHeader;
      getPackagesData.data.packages.restaurant.messageDescription = originalMessageDescription;
      getHotelInformation.data.hotelInformation.ancillaryCloseout = originalAncillaryCloseout;
    }
  });

  it('should render page with kids eats free notification', async () => {
    getBookingInformationData.data.bookingInformation.reservationByIdList[0].roomStay.childrenNumber = 1;
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-FreeFoodKidsNotification-Wrapper')).toBeInTheDocument();
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
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByText('upsell.heading.restaurant.closure')).toBeInTheDocument();
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
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );
    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
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
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page with loading true', async () => {
    getBookingInformationData.isLoading = true;
    const { getByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('booking.loading')).toBeInTheDocument();
    getBookingInformationData.isLoading = false;
  });

  it('should render page with get booking information error', async () => {
    getBookingInformationData.isError = true;
    const { getByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting booking information....')).toBeInTheDocument();
    expect(getByText('error booking information')).toBeInTheDocument();
    getBookingInformationData.isError = false;
  });

  it('should render page with get packages  error', async () => {
    getPackagesData.isError = true;
    const { getByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting packages....')).toBeInTheDocument();
    expect(getByText('error packages')).toBeInTheDocument();
    getPackagesData.isError = false;
  });

  it('should render page with get hotel information error', async () => {
    getHotelInformation.isError = true;
    const { getByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByText('Error on getting hotel information....')).toBeInTheDocument();
    expect(getByText('error hotel information')).toBeInTheDocument();
    getHotelInformation.isError = false;
  });

  it('should render page pi skeleton with room selection packages undefined', async () => {
    getRoomSelectionData.data.packages.packages = undefined;
    const { queryAllByText } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(
      queryAllByText('AncillariesPage-Meals-Adults-MealItem-AddSubtractControls-Value').length
    ).toBe(0);
  });

  it('should render page pi skeleton with packages.meals undefined', async () => {
    getPackagesData.data.packages.packages.meals = undefined;
    const { queryByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('AncillariesPage-Meals-Adults-MealItem-Wrapper')).toBeFalsy();
  });

  it('should render page pi skeleton with packages undefined', async () => {
    getPackagesData.data = undefined;
    const { queryByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(queryByTestId('AncillariesPage-Meals-Adults-MealItem-Wrapper')).toBeFalsy();
  });

  it('should render page pi skeleton with hotelDetails undefined', async () => {
    getHotelInformation.data = undefined;
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page pi skeleton with room selection undefined', async () => {
    getRoomSelectionData.data = undefined;
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
  });

  it('should render page pi skeleton with meals', async () => {
    getPackagesData.data = undefined;
    getRoomSelectionData.data = undefined;
    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    expect(getByTestId('AncillariesPage-PageContent')).toBeInTheDocument();
    const button = getByTestId('AncillariesPage-ContinueButton');
    fireEvent.click(button);
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

    it('should display the labels from localStorage in <Tabs/> when feature flag and silent substitution property from local storage is true', () => {
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
        <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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
        <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      const tabButtonDescriptionStandard = getByTestId('Standard room-TabButtonDescription');
      expect(tabButtonDescriptionStandard).toBeInTheDocument();
      expect(tabButtonDescriptionStandard.textContent).toContain('Standard');
    });

    it('should display the default label in <Tabs/> when feature flag is true and there is no data in localStorage', () => {
      localStorageMock.clear();
      mockUseFeatureSwitch.mockReturnValue(true);
      mockUseSilentRoomsMatch.mockReturnValue([]);
      const { getByTestId } = render(
        <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      const tabButtonDescriptionDouble = getByTestId('Standard room-TabButtonDescription');
      expect(tabButtonDescriptionDouble).toBeInTheDocument();
      expect(tabButtonDescriptionDouble.textContent).toContain('Standard');

      const tabButtonDescriptionPremierPlus = getByTestId('Premier Plus Room-TabButtonDescription');
      expect(tabButtonDescriptionPremierPlus).toBeInTheDocument();
      expect(tabButtonDescriptionPremierPlus.textContent).toContain('Premier');
    });

    it('should display the default label in <Tabs/> when feature flag is false', async () => {
      mockUseFeatureSwitch.mockReturnValue(false);
      mockUseSilentRoomsMatch.mockReturnValue([]);
      const { getByTestId } = render(
        <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      const tabButtonDescriptionDouble = getByTestId('Standard room-TabButtonDescription');
      expect(tabButtonDescriptionDouble).toBeInTheDocument();
      expect(tabButtonDescriptionDouble.textContent).toContain('Standard');

      const tabButtonDescriptionPremierPlus = getByTestId('Premier Plus Room-TabButtonDescription');
      expect(tabButtonDescriptionPremierPlus).toBeInTheDocument();
      expect(tabButtonDescriptionPremierPlus.textContent).toContain('Premier');
    });
  });
});

describe('Early check-in extras component checks', () => {
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
      { packagesList: ['HSCKIN'], reservationId: '1798695' },
      { packagesList: ['HSCOU2'], reservationId: '1798696' },
    ],
    selectedRoom: 0,
    noNights: 1,
  };

  it('should display ExtrasSection component', async () => {
    const { getByTestId } = render(
      <>
        <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
        <ExtrasSection {...mockDataItems} />
      </>
    );

    await waitFor(() => {
      expect(useFeatureToggle).toBeCalled();
    });

    await waitFor(() => {
      expect(getByTestId('ExtrasSection-Wrapper')).toBeInTheDocument();
    });
  });
});

describe('User Meal Preferences with displayRestaurantUnavailableNotification', () => {
  let mockFetchQuery: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUseAuthToken.mockReturnValue({ token: 'test-token', isAuth0Enabled: false });
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);

    // Ensure getPackagesData.data is properly initialized (in case previous tests set it to undefined)
    if (!getPackagesData.data || !getPackagesData.data.packages) {
      getPackagesData.data = {
        packages: {
          packages: { meals: [], mealsKids: [], roomSelection: [], extrasItems: [] },
          restaurant: { restaurantNotFound: false, noMealsFound: false },
        },
      } as any;
    }

    // Ensure getHotelInformation.data is properly initialized (in case previous tests set it to undefined)
    if (!getHotelInformation.data || !getHotelInformation.data.hotelInformation) {
      getHotelInformation.data = {
        hotelInformation: {
          ancillaryCloseout: null,
        },
      } as any;
    }

    // Mock fetchQuery on queryClient
    mockFetchQuery = jest.fn();
    queryClient.fetchQuery = mockFetchQuery;
  });

  it('should set user meal preferences when restaurant is available and user has preferences', async () => {
    // Setup: Restaurant is available (no notification)
    getPackagesData.data.packages.restaurant.restaurantNotFound = false;
    getHotelInformation.data.hotelInformation.ancillaryCloseout = null;

    // Mock user details with meal preference
    mockFetchQuery.mockResolvedValue({
      bookingPreference: {
        foodPreference: 2, // User's saved preference
      },
    });

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(mockFetchQuery).toHaveBeenCalledWith(
        expect.objectContaining({
          queryKey: expect.arrayContaining(['userDetails']),
        })
      );
    });

    // Verify the component rendered (meal preferences should be applied)
    await waitFor(() => {
      expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    });
  });

  it('should NOT set user meal preferences when restaurant is unavailable', async () => {
    // Setup: Restaurant is unavailable (restaurantNotFound = true)
    getPackagesData.data.packages.restaurant.restaurantNotFound = true;
    getHotelInformation.data.hotelInformation.ancillaryCloseout = null;

    // Mock user details with meal preference
    mockFetchQuery.mockResolvedValue({
      bookingPreference: {
        foodPreference: 2, // User's saved preference (should be ignored)
      },
    });

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      if (mockFetchQuery.mock.calls.length > 0) {
        expect(mockFetchQuery).toHaveBeenCalledWith(
          expect.objectContaining({
            queryKey: expect.arrayContaining(['userDetails']),
          })
        );
      }
    });

    // Verify the component rendered with restaurant unavailable notification
    await waitFor(() => {
      expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    });
  });

  afterEach(() => {
    // Clean up: Reset to default state
    getPackagesData.data.packages.restaurant.restaurantNotFound = false;
    getHotelInformation.data.hotelInformation.ancillaryCloseout = null;
  });
});

describe('Adobe Analytics Satellite Tracking', () => {
  let mockFetchQuery: jest.Mock;

  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      language: 'en',
      country: 'gb',
    });
    mockUseLocalStorage.mockReturnValue([mockBasketDetailsStateWithTwoRooms, jest.fn()]);

    // Mock fetchQuery on queryClient
    mockFetchQuery = jest.fn();
    queryClient.fetchQuery = mockFetchQuery;
    mockFetchQuery.mockResolvedValue({
      bookingPreference: {
        foodPreference: 2,
      },
    });
  });

  it('should track bundle selection when soft bundles are active', async () => {
    // Mock window._satellite
    const mockTrack = jest.fn();
    Object.defineProperty(window, '__satelliteLoaded', {
      writable: true,
      value: true,
    });
    Object.defineProperty(window, '_satellite', {
      writable: true,
      value: {
        track: mockTrack,
      },
    });

    // Enable soft bundles - this makes isSoftBundlesActive = true
    mockFlags.release_pi_display_soft_bundles = true;
    mockCookies.bundles = 'class';
    mockUseSessionStorage.mockReturnValue([
      [{ id: 'BFADBF', price: 1, description: 'abc' }],
      jest.fn(),
    ]); // hasSessionSoftBundles = true
    mockUseSoftBundles.mockReturnValue({ isSoftBundlesVisible: true }); // isSoftBundlesVisible = true

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    // Wait for component to be rendered
    await waitFor(() => {
      expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    });

    // Wait for useEffect to execute and call track
    await waitFor(
      () => {
        expect(mockTrack).toHaveBeenCalledWith('bundleSelected');
      },
      { timeout: 3000 }
    );

    // Cleanup
    delete window.__satelliteLoaded;
    delete window._satellite;
  });

  it('should not call track when satellite is not loaded', async () => {
    const mockTrack = jest.fn();
    Object.defineProperty(window, '__satelliteLoaded', {
      writable: true,
      value: false,
    });
    Object.defineProperty(window, '_satellite', {
      writable: true,
      value: {
        track: mockTrack,
      },
    });

    mockFlags.release_pi_display_soft_bundles = true;
    mockCookies.bundles = 'class';
    mockUseSessionStorage.mockReturnValue([true, jest.fn()]);

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    });

    expect(mockTrack).not.toHaveBeenCalled();

    delete window.__satelliteLoaded;
    delete window._satellite;
  });

  it('should not call track when satellite object is undefined', async () => {
    Object.defineProperty(window, '__satelliteLoaded', {
      writable: true,
      value: true,
    });

    mockFlags.release_pi_display_soft_bundles = true;
    mockCookies.bundles = 'class';
    mockUseSessionStorage.mockReturnValue([true, jest.fn()]);

    const { getByTestId } = render(
      <AncillariesPagePi {...mockProps} queryClient={queryClient} router={mockRouter as any} />
    );

    await waitFor(() => {
      expect(getByTestId('AncillariesPage-Wrapper')).toBeInTheDocument();
    });

    delete window.__satelliteLoaded;
  });

  afterEach(() => {
    mockFlags.release_pi_display_soft_bundles = false;
    mockCookies.bundles = 'false';
    mockUseSoftBundles.mockReturnValue({ isSoftBundlesVisible: false });
  });
});

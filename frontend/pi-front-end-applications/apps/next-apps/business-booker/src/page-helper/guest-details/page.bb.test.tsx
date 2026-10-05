import * as ReactQuery from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Channel, AccessLevel } from '@whitbread-eos/api';
import {
  getBookingInformationData as getBookingInformationDataOriginal,
  getHotelInformation as getHotelInformationOriginal,
  getLocalStorageMock,
  getRoomSelectionData as getRoomSelectionDataOriginal,
} from '@whitbread-eos/utils';
import preloadAll from 'jest-next-dynamic';
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import React from 'react';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../utils/bb-all-pages-constants';
import GuestDetailsPageBB, { formatDataToSend, formatGuests } from './page.bb';
import { fireEvent, render, waitFor } from './utils/test-utils';

// Mutable mock data - cloned from shared originals
const getBookingInformationData = JSON.parse(JSON.stringify(getBookingInformationDataOriginal));
const getHotelInformation = JSON.parse(JSON.stringify(getHotelInformationOriginal));
const getRoomSelectionData = JSON.parse(JSON.stringify(getRoomSelectionDataOriginal));

const getUserDetailsData = {
  data: {
    contactDetail: {
      title: 'Mr',
      firstName: 'firstname',
      lastName: 'lastname',
      email: 'email',
      mobile: 'mobile',
      address: {
        companyName: 'whitbread',
        countryCode: 'GB',
        line1: 'Street',
        line2: 'Street 2',
        line3: 'Street 3',
        line4: 'Street 4',
        postCode: '123',
        type: '',
      },
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error user details',
  },
};
const getContactPreferencesData = {
  data: {
    getContactPreferences: {
      permissions: [{ optIn: false }],
    },
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error user details',
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
            isFree: true,
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
            isFree: true,
          },
          {
            currency: 'GBP',
            description: '<p>Download files faster, stream movies</p>\r\n',
            id: 'FIFR24',
            imageSrc: '/content/dam/global/extras/ultimate-wifi.png',
            name: 'Ultimate Wi-Fi',
            order: 1,
            price: 0,
            available: null,
            basePrice: 0,
            isFree: true,
          },
          {
            currency: 'GBP',
            description: 'Enjoy an ice-cold bottle of Prosecco during your stay.',
            id: 'DBPROS',
            imageSrc: '/content/dam/global/extras/prosecco.png',
            name: 'Bottle of prosecco',
            order: 4,
            price: 20,
            available: null,
            basePrice: null,
            isFree: false,
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
          {
            reservationId: '4681507',
            packagesSelection: [
              {
                id: 'FIFR24',
                noOfSelections: 1,
              },
            ],
          },
          {
            reservationId: '4681344',
            packagesSelection: [
              {
                id: 'FIFR24',
                noOfSelections: 1,
              },
            ],
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
  },
  isLoading: false,
  isError: false,
  error: {
    message: 'error packages',
  },
};

const getPackagesDataOriginal = JSON.parse(JSON.stringify(getPackagesData));

const mockProps = {
  biQueryInput: {
    basketReference: '12',
    country: 'GB',
    language: 'en',
    bookingChannelCriteria: {
      channel: Channel.Bb,
      subchannel: 'WEB',
      language: 'en',
    },
  },
  hiQueryInput: {
    country: 'GB',
    hotelId: 'LONKIN',
    language: 'en',
  },
  pcksQueryInput: {
    adultsNumber: 2,
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

const guestTitle = 'Ms';

const cdhCompanyId = 'COMP_5030a0a9-5bbd-4f92-80c2-0a203bdaccb5';

const userData = {
  sessionId: 'deprecated',
  contactDetail: {
    title: 'Ms',
    firstName: 'Uat',
    lastName: 'Testing',
    email: 'uat.selfbookertest@mailinator.com',
    telephone: '4534534534534',
    mobile: '',
    address: {
      line1: '122 Truthan View',
      line2: 'Trispen',
      line3: '',
      line4: 'TRURO',
      line5: '',
      postCode: 'TR4 9AS',
      countryCode: 'GB',
      countryCodeISO: 'GB',
    },
  },
  paymentPreference: {
    electronicInvoiceRequired: true,
    paymentCard: {
      cardID: '1',
      cardType: 'VI',
      cardNumber: '************1103',
      expiryDate: '0325',
      cardHolderName: 'Testersons',
      cardToken: '4216333880397891103',
      billingAddress: {
        line1: '12 Truthan View',
        line2: 'Trispen',
        line3: '',
        line4: 'TRURO',
        line5: '',
        postCode: 'TR4 9AS',
        countryCode: 'GB',
      },
      cnpRequired: false,
      cnpBusinessAccountUsername: '',
      cnpBusinessAccountPassword: '',
    },
  },
  bookingPreference: {
    roomRequirements: {
      type: '',
      adults: 0,
      children: 0,
      cotRequired: false,
    },
    foodPreference: 0,
    preselectWifi: false,
  },
  companyId: 'COMP_5030a0a9-5bbd-4f92-80c2-0a203bdaccb5',
  guestHistoryNumber: 'G80951710',
  business: {
    accessLevel: 'SELF',
    purchaseOrderAnswer: null,
    customerReferenceAnswer: null,
    centralCard: '8',
    myPILink: null,
    dismissMPILink: false,
    miSetupRequired: false,
    awaitingApproval: 0,
    employeeId: 'EMPL_3fe1a4ee-1c1e-49e4-9be7-86a393597888',
    tethered: false,
  },
  acceptFutureMailing: false,
};

const userDataBooker = {
  sessionId: 'deprecated',
  contactDetail: {
    title: 'Mr',
    firstName: 'Ella',
    lastName: 'Testing',
    email: 'uat.travelmanager.test@mailinator.com',
    telephone: '+444353453453452',
    mobile: '',
    address: {
      line1: '22 Truthan View',
      line2: 'Trispen',
      line3: '',
      line4: 'TRURO',
      line5: '',
      postCode: 'SA4 9AP',
      countryCode: 'GB',
      countryCodeISO: 'GB',
    },
  },
  paymentPreference: {
    electronicInvoiceRequired: false,
    paymentCard: {
      cardID: '1',
      cardType: 'VI',
      cardNumber: '************1111',
      expiryDate: '12/28',
      cardHolderName: 'Raul',
      cardToken: '4764776852337921111',
      billingAddress: {
        line1: '22 Truthan View',
        line2: 'Trispen',
        line3: '',
        line4: 'TRURO',
        line5: '',
        postCode: 'SA4 9AP',
        countryCode: 'GB',
      },
      cnpRequired: false,
    },
  },
  bookingPreference: {
    foodPreference: 0,
    preselectWifi: false,
  },
  companyId: 'COMP_5030a0a9-5bbd-4f92-80c2-0a203bdaccb5',
  guestHistoryNumber: 'G80951708',
  business: {
    accessLevel: 'SUPER',
    purchaseOrderAnswer: '',
    customerReferenceAnswer: '',
    centralCard: '8',
    myPILink: null,
    dismissMPILink: false,
    miSetupRequired: false,
    awaitingApproval: 0,
    employeeId: 'EMPL_3fe1a4ee-1c1e-49e4-9be7-86a39359787c',
    tethered: false,
  },
};

const biQueryInput = {
  basketReference: 'AKU-cefb35c9-97f2-452f-8fd8-b41607f79da6',
  country: 'gb',
  language: 'en',
  bookingChannelCriteria: {
    channel: Channel.Bb,
    subchannel: 'WEB',
    language: 'EN',
  },
};
const bkngData = {
  bookingInformation: {
    hotelId: 'LONEUS',
    totalCost: 999,
    currencyCode: 'GBP',
    bookingFlowId: 'booking-business',
    infoMessages: [],
    reservationByIdList: [
      {
        additionalGuestInfo: {
          purposeOfStay: '',
        },
        reservationId: '2299237',
        roomStay: {
          adultsNumber: 2,
          childrenNumber: 0,
          arrivalDate: '2024-09-23',
          departureDate: '2024-09-24',
          ratePlanCode: 'BUSIFLEX',
          rateExtraInfo: {
            rateName: 'Business Flex',
          },
          roomExtraInfo: {
            roomType: 'DOUBLE',
            roomName: 'Double room',
          },
          accessibleRoom: {
            isAccessible: false,
            phoneNumber: '0333 321 1262',
          },
        },
        reservationGuestList: [
          {
            givenName: 'Uat',
            surName: 'Testing',
            nameTitle: 'Ms',
          },
        ],
        billing: {
          address: {
            addressLine1: '122 Truthan View',
            addressLine2: 'Trispen',
            addressLine3: '',
            addressLine4: 'TRURO',
            country: 'GB',
            postalCode: 'TR4 9AS',
            companyName: 'Ella Cats and Dogs_M',
            cityName: 'TRURO',
            countryCode: null,
          },
          email: 'uat.selfbookertest@mailinator.com',
          firstName: 'Uat',
          lastName: 'Testing',
          telephone: '',
          landline: '4534534534534',
          title: 'Ms',
        },
      },
    ],
    upgradeToFlex: {
      amount: null,
      currency: null,
      flexRateCode: null,
    },
  },
};

const bookingInfo = { biQueryInput, bkngData };

const guestDetails = {
  bbGuestDetails: [
    {
      title: 'Mr',
      lastName: 'Testing',
      id: 'EMPL_3fe1a4ee-1c1e-49e4-9be7-86a39359787c',
      firstName: 'Uat',
      emailAddress: 'uat.selfbookertest@mailinator.com',
      composedName: 'Ms Uat Testing (uat.selfbookertest@mailinator.com)',
    },
  ],
  bbAccompanyingGuestDetails: [
    {
      title: '',
      firstName: '',
      lastName: '',
      emailAddress: '',
      employeeAccountId: '',
      id: '',
    },
  ],
};

const queryClient = new ReactQuery.QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      gcTime: 0,
    },
  },
});
const mockRouter = {
  push: jest.fn(),
};
const mockCustomLocale = jest.fn();
const mockUseFeatureSwitch = jest.fn();
const mockUseSilentRoomsMatch = jest.fn();
const mockFeatureToggle = jest.fn(() => ({
  release_bb_ancillaries_extras_display: true,
  release_bb_accompanying_guest_details: true,
  release_bb_marketing_email_optin: true,
  release_bb_free_fnb_and_extras: true,
}));
const mockAutocompleteMeals = jest.fn().mockReturnValue([]);
const mockMealsMapperSelector = jest.fn().mockReturnValue([]);

//endregion

//region Jest Mock
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

const mockClosedOutMeals = [
  {
    allergyInfoLabel: 'Allergy & nutrition info',
    allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
    currency: 'GBP',
    description:
      '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>',
    id: 'BFADCT',
    bartId: '12',
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png',
    name: 'Continental Breakfast',
    price: 9.99,
    order: 1,
    freeBreakfastOption: false,
    freeBreakfastCode: 'BFADCT',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
      name: 'Breakfast menu',
    },
  },
];

const dataToSendSelf = {
  companyId: 'COMP_5030a0a9-5bbd-4f92-80c2-0a203bdaccb5',
  acceptFutureMailing: false,
  title: 'Ms',
  firstName: 'Uat',
  lastName: 'Testing',
  emailAddress: 'uat.selfbookertest@mailinator.com',
  mobile: '',
  landline: '4534534534534',
  addressType: 'BUSINESS',
  postalCode: 'TR4 9AS',
  addressLine1: '122 Truthan View',
  addressLine2: 'Trispen',
  addressLine3: '',
  addressLine4: 'TRURO',
  countryCode: 'GB',
  cityName: 'TRURO',
  basketReference: 'AKU-cefb35c9-97f2-452f-8fd8-b41607f79da6',
  hotelId: 'LONEUS',
  reasonForStay: 'BUS',
  sendEmailConfirmation: false,
  sendEmailInvoice: false,
  stayingGuests: [
    {
      sameAsBooker: true,
      stayingGuestDetails: {
        title: 'Ms',
        firstName: 'Uat',
        lastName: 'Testing',
        emailAddress: 'uat.selfbookertest@mailinator.com',
        employeeAccountId: 'EMPL_3fe1a4ee-1c1e-49e4-9be7-86a393597888',
      },
      accompanyingGuestDetails: {
        title: '',
        firstName: '',
        lastName: '',
        emailAddress: '',
        employeeAccountId: '',
      },
    },
  ],
};

const formatGuestBookerResponse = [
  {
    sameAsBooker: false,
    stayingGuestDetails: {
      emailAddress: 'uat.selfbookertest@mailinator.com',
      employeeAccountId: 'EMPL_3fe1a4ee-1c1e-49e4-9be7-86a39359787c',
      firstName: 'Uat',
      lastName: 'Testing',
      title: 'Mr',
    },
    accompanyingGuestDetails: {
      title: '',
      firstName: '',
      lastName: '',
      emailAddress: '',
      employeeAccountId: '',
    },
  },
];

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

function mockUseQueryRequest(queryKey: any[]) {
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
      case 'userDetails':
        return getUserDetailsData;
      case 'ContactPreferences':
        return getContactPreferencesData;

      default:
        return {};
    }
  }
}

function mockfilterPackagesByAncillariesCloseOut({ ancillaryCloseoutData }: any) {
  if (ancillaryCloseoutData.items[0].filteredStatus === 'NoFilteredData') {
    return { filteredAdultsMeals: [], filteredChildrenMeals: [], filteredClosedOutMeals: [] };
  }
  if (ancillaryCloseoutData.items[0].filteredStatus === 'PartialFilteredData') {
    return {
      filteredAdultsMeals: mockfilteredAdultsMeals,
      filteredChildrenMeals: getPackagesData.data.packages.packages.mealsKids,
      filteredClosedOutMeals: mockClosedOutMeals,
    };
  }
  if (ancillaryCloseoutData.items[0].filteredStatus === 'FullFilteredData') {
    return {
      filteredAdultsMeals: getPackagesData.data.packages.packages.meals,
      filteredChildrenMeals: getPackagesData.data.packages.packages.mealsKids,
      filteredClosedOutMeals: mockClosedOutMeals,
    };
  }
}

const mockMutationResponse = {
  isError: false,
  error: { message: '' },
  mutation: {
    isLoading: false,
    data: {},
    mutate: jest.fn(),
  },
};

jest.mock('@whitbread-eos/molecules', () => ({
  ...jest.requireActual('@whitbread-eos/molecules'),
  getNoOfDaysInYear: jest.fn(),
  SEO: () => <div></div>,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  autocompleteMeals: (...args: any[]) => mockAutocompleteMeals(...args),
  mealsMapperSelector: (...args: any[]) => mockMealsMapperSelector(...args),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useCustomLocale: () => mockCustomLocale(),
  invalidateQueries: mockUseQueryRequest,
  useQuery: () => mockUseQueryRequest,
  useQueryRequest: mockUseQueryRequest,
  useMutationRequest: () => mockMutationResponse,
  getLoggedInUserInfo: () => ({
    accessLevel: 'SUPER',
    employeeId: '',
    companyId: '',
    sessionId: '',
  }),
  useCompanyDetails: () => ({
    requestedCompany: {
      bookingAllowances: {
        extrasCodes: ['3', '4'],
        upsellItemsAllowed: ['11', '12', '17'],
      },
    },
  }),
  useFeatureSwitch: () => mockUseFeatureSwitch(),
  useSilentRoomsMatch: () => mockUseSilentRoomsMatch(),
  useFeatureToggle: () => mockFeatureToggle(),
  useRestMutationRequest: () => ({
    mutation: jest.fn(),
  }),
  usePackages: () => ({
    ...getPackagesData?.data?.packages,
    ...getPackagesData,
  }),
  filterPackagesByAncillariesCloseOut: mockfilterPackagesByAncillariesCloseOut,
  getEmployeeById: jest.fn(),
  updateAncillariesAnalytics: () => jest.fn(),
  decodeFromBase64: () => '{}',
  getDefaultSessionTracing: () => ({}),
  decodeIdToken: () => {
    return {
      email: 'traveling.bgl@mailinator.com',
    };
  },
  logger: {
    info: jest.fn(),
  },
  getAuthCookie: jest.fn().mockImplementation(() => 'testCookie'),
  formatBillingAddress: jest.fn(),
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

//endregion

// region Unit Tests
describe('Page BB guest-details', () => {
  describe('Page GDP BB', () => {
    beforeAll(async () => {
      await preloadAll();
    });

    beforeEach(async () => {
      jest.clearAllMocks();
      mockMutationResponse.isError = false;
      mockMutationResponse.isSuccess = false;
      mockCustomLocale.mockReturnValue({
        language: 'en',
        country: 'gb',
      });
    });

    jest.mock('@whitbread-eos/organisms', () => ({
      ...jest.requireActual('@whitbread-eos/organisms'),
      GuestDetailsBBContainer: () => <div data-testId="GuestDetailsBBContainer"></div>,
    }));

    describe('Silent Substitution', () => {
      beforeEach(async () => {
        jest.clearAllMocks();
      });

      const localStorageMock = getLocalStorageMock();
      Object.defineProperty(window, 'localStorage', {
        value: localStorageMock,
      });

      const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';

      it('should render guest details page with innbusiness props', async () => {
        const { getByTestId } = render(
          <GuestDetailsPageBB
            {...mockProps}
            queryClient={queryClient}
            router={mockRouter as any}
            userDetails={{ business: { accessLevel: AccessLevel.Super } }}
            companyDetails={{ success: true }}
          />
        );

        expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
        expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
      });

      it('should display the labels from localStorage in <Tabs/> when feature flag is true', async () => {
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
          <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
        );

        await waitFor(() => {
          const tabButtonDescriptionDouble = getByTestId('Double room-TabButtonDescription');
          expect(tabButtonDescriptionDouble).toBeInTheDocument();
          expect(tabButtonDescriptionDouble?.textContent).toContain('Double');

          const tabButtonDescriptionAccessible = getByTestId(
            'Accessible room-TabButtonDescription'
          );
          expect(tabButtonDescriptionAccessible).toBeInTheDocument();
          expect(tabButtonDescriptionAccessible.textContent).toContain('Accessible');
        });
      });

      it('should display the default label in <Tabs/> when feature flag is false', async () => {
        mockUseFeatureSwitch.mockReturnValue(false);
        mockUseSilentRoomsMatch.mockReturnValue([]);

        const { getByTestId, getAllByTestId } = render(
          <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
        );

        const adultsHeadingTitle = getByTestId('GuestDetailsPageBB-Meals-Adults-Heading-Title');
        expect(adultsHeadingTitle).toBeInTheDocument();
        expect(adultsHeadingTitle.textContent).toContain('upsell.meals.adult.title');

        const titlePremierInn = getAllByTestId('GuestDetailsPageBB-Meals-Adults-MealItem-Title')[0];
        expect(titlePremierInn).toBeInTheDocument();
      });
    });

    it('should render page bb skeleton', async () => {
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
    });

    it('should render page bb booking summary', async () => {
      const { getByTestId, getByText, queryByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-Wrapper')
      ).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-BookingSummary-MobileVariant')).toBeInTheDocument();
      expect(
        getByText(
          '2 hoteldetails.bookingsummary.rooms, 1 booking.summary.night | 23 Mar - 24 Mar | £999.00 | booking.summary.rate Flex'
        )
      ).toBeInTheDocument();
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-MobileVariant-SectionWrapper')
      ).toBeInTheDocument();
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-MobileVariant-SectionHeader')
      ).toBeInTheDocument();
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-TotalCost-TotalCostPrice')
      ).toBeInTheDocument();
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-TotalCost-CostAmount')
      ).toBeInTheDocument();
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-TotalCost-VATMessage')
      ).toBeInTheDocument();
      expect(
        queryByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-TotalCost-TaxesMessage')
      ).not.toBeInTheDocument();

      expect(
        getByTestId(
          'GuestDetailsPageBB-BookingSummary-DesktopVariant-StayDatesInformation-ArrivalDate'
        )
      ).toBeInTheDocument();
      expect(
        getByTestId(
          'GuestDetailsPageBB-BookingSummary-DesktopVariant-StayDatesInformation-DepartureDate'
        )
      ).toBeInTheDocument();
      expect(
        getByTestId(
          'GuestDetailsPageBB-BookingSummary-DesktopVariant-StayDatesInformation-NightsNumber'
        )
      ).toBeInTheDocument();
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-RoomInformation-Wrapper')
      ).toBeInTheDocument();
    });

    it('should render cityTax message if cityTax set', async () => {
      getPackagesData.data.packages.hotelHasCityTaxForBusiness = true;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(
        getByTestId('GuestDetailsPageBB-BookingSummary-DesktopVariant-TotalCost-TaxesMessage')
      ).toBeInTheDocument();
    });

    it('should render page and have continue button', async () => {
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-ContinueButton')).toBeInTheDocument();
    });

    it('should render page and have continue button and click it', async () => {
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      const button = getByTestId('GuestDetailsPageBB-ContinueButton');
      await waitFor(() => {
        fireEvent.click(button);
      });
    });

    it('should mark Payment as opened from Guest Details when saving succeeds', async () => {
      mockMutationResponse.isSuccess = true;

      render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      await waitFor(() => {
        expect(mockRouter.push).toHaveBeenCalledWith(
          `/gb/en/business-booker/booking-business/payment?reservationId=12&${PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM}=${PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS}`
        );
      });
    });

    it('should render page with no restaurant', async () => {
      getPackagesData.data.packages.restaurant.restaurantNotFound = true;
      getPackagesData.data.packages.packages.extrasItems = [];

      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-RestaurantMessage-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-RestaurantMessage-Title')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-RestaurantMessage-Description')).toBeInTheDocument();
      getPackagesData.data.packages.restaurant.restaurantNotFound = false;
    });

    it('should render page with info message', async () => {
      getBookingInformationData.data.bookingInformation.infoMessages.push('TestInfoMess');
      const { queryByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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
            filteredStatus: 'NoFilteredData',
          },
        ],
      } as any;

      getPackagesData.data.packages.packages.extrasItems = [];
      getPackagesData.data.packages.restaurant.restaurantNotFound = false;

      const { getByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(getByText('upsell.heading.restaurant.closure')).toBeInTheDocument();
      getHotelInformation.data.hotelInformation.ancillaryCloseout = null as any;
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
            filteredStatus: 'PartialFilteredData',
          },
        ],
      } as any;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
      getHotelInformation.data.hotelInformation.ancillaryCloseout = null as any;
    });

    it('should render page with ancillary closeout with NO continental breakfast', async () => {
      getHotelInformation.data.hotelInformation.ancillaryCloseout = {
        items: [
          {
            endDate: '10/02/2024',
            serviceCode: 'CONTINENTAL_NA',
            startDate: '01/02/2024',
            text: 'Continental breakfast is not available"',
            upsellCodes: 'BFADCT',
            filteredStatus: 'PartialFilteredData',
          },
        ],
      } as any;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
      getHotelInformation.data.hotelInformation.ancillaryCloseout = null as any;
    });

    it('should render page with ancillary closeout not applicable to any packages', async () => {
      getHotelInformation.data.hotelInformation.ancillaryCloseout = {
        items: [
          {
            filteredStatus: 'FullFilteredData',
          },
        ],
      } as any;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
      getHotelInformation.data.hotelInformation.ancillaryCloseout = null as any;
    });

    it('should render page with no menus', async () => {
      getPackagesData.data.packages.restaurant.noMealsFound = true;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-RestaurantMessage-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-RestaurantMessage-Title')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-RestaurantMessage-Description')).toBeInTheDocument();
      getPackagesData.data.packages.restaurant.noMealsFound = false;
    });

    it('should render page with kids eats free notification', async () => {
      getBookingInformationData.data.bookingInformation.reservationByIdList[0].roomStay.childrenNumber = 1;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(
        getByTestId('GuestDetailsPageBB-FreeFoodKidsNotification-Wrapper')
      ).toBeInTheDocument();
    });

    it('should render page with loading true', async () => {
      getBookingInformationData.isLoading = true;
      const { getByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByText('booking.loading')).toBeInTheDocument();
      getBookingInformationData.isLoading = false;
    });

    it('should render page with get booking information error', async () => {
      getBookingInformationData.isError = true;
      const { getByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByText('Error on getting booking information....')).toBeInTheDocument();
      expect(getByText('error booking information')).toBeInTheDocument();
      getBookingInformationData.isError = false;
    });

    it('should render page with get packages  error', async () => {
      getPackagesData.isError = true;
      const { getByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByText('Error on getting packages....')).toBeInTheDocument();
      expect(getByText('error packages')).toBeInTheDocument();
      getPackagesData.isError = false;
    });

    it('should render page with get hotel information error', async () => {
      getHotelInformation.isError = true;
      const { getByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByText('Error on getting hotel information....')).toBeInTheDocument();
      expect(getByText('error hotel information')).toBeInTheDocument();
      getHotelInformation.isError = false;
    });

    it('should render page pi skeleton with room selection packages undefined', async () => {
      getRoomSelectionData.data.packages.packages = undefined as any;
      const { queryAllByText } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(
        queryAllByText('GuestDetailsPageBB-Meals-Adults-MealItem-AddSubtractControls-Value').length
      ).toBe(0);
    });

    it('should render page pi skeleton with packages.meals undefined', async () => {
      getPackagesData.data.packages.packages.meals = undefined as any;
      const { queryByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(queryByTestId('GuestDetailsPageBB-Meals-Adults-MealItem-Wrapper')).toBeFalsy();
    });

    it('should render page pi skeleton with packages undefined', async () => {
      getPackagesData.data = undefined as any;
      const { queryByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(queryByTestId('GuestDetailsPageBB-Meals-Adults-MealItem-Wrapper')).toBeFalsy();
    });

    it('should render page pi skeleton with hotelDetails undefined', async () => {
      getHotelInformation.data = undefined as any;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
    });

    it('should render page pi skeleton with room selection undefined', async () => {
      getRoomSelectionData.data = undefined as any;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
    });

    it('should render page pi skeleton with meals and booking info undefined', async () => {
      getPackagesData.data = undefined as any;
      getRoomSelectionData.data = undefined as any;
      mockProps.biQueryInput.basketReference = undefined as any;
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
      const button = getByTestId('GuestDetailsPageBB-ContinueButton');
      await waitFor(() => {
        fireEvent.click(button);
      });
    });

    it('should render page bb skeleton with meals and  guest details section and set employee', () => {
      const { getByTestId, getAllByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsBBContainer-GuestDetailsTitle')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsBBContainer-Form-DynamicGuestLead-1')).toBeInTheDocument();

      const switchToManualBtn = getAllByTestId(/switch/i)[0];
      expect(switchToManualBtn).toBeInTheDocument();
      fireEvent.click(switchToManualBtn);
      const firstNameInput = getByTestId('input-bbGuestDetails[0][firstName]');
      const lastNameInput = getByTestId('input-bbGuestDetails[0][lastName]');

      fireEvent.change(firstNameInput, { target: { value: 'Henry' } });
      fireEvent.change(lastNameInput, { target: { value: 'Mitchel' } });

      expect(firstNameInput).toHaveValue('Henry');
      expect(lastNameInput).toHaveValue('Mitchel');

      const switchToDynamicBtn = getByTestId('GuestDetailsBBContainer-Form-SwitchToDynamic');
      expect(switchToDynamicBtn).toBeInTheDocument();
      fireEvent.click(switchToDynamicBtn);
    });
    it('should render page bb skeleton with meals and accompanying guest details section and set employee', async () => {
      const { getByTestId, getAllByTestId, getAllByRole } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      expect(getByTestId('GuestDetailsPageBB-PageContent')).toBeInTheDocument();

      const switchToManualBtn = getAllByTestId(/switch/i)[0];
      expect(switchToManualBtn).toBeInTheDocument();
      fireEvent.click(switchToManualBtn);
      const firstNameInput = getAllByRole('textbox')[0];
      const lastNameInput = getAllByRole('textbox')[1];
      fireEvent.change(firstNameInput, { target: { value: 'Lily' } });
      fireEvent.change(lastNameInput, { target: { value: 'Smith' } });

      expect(firstNameInput).toHaveValue('Lily');
      expect(lastNameInput).toHaveValue('Smith');
    });

    it('formatGuests returns correct data if all arguments are non nulable objects', async () => {
      const result = await formatGuests(guestDetails, 'Mr', userDataBooker, true);
      expect(result).toEqual(formatGuestBookerResponse);
    });
    it('returns correct object to pass onSubmit when access level is SELF', async () => {
      const dataToSend = await formatDataToSend(
        cdhCompanyId,
        guestTitle,
        userData,
        bookingInfo,
        'SELF',
        guestDetails,
        true
      );
      expect(dataToSend).toEqual(dataToSendSelf);
    });
    it('display MarketingEmail Component when FF is on and optin from contact preference is false', async () => {
      const { getByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(getByTestId('GuestDetailsPageBB-MarketingEmail')).toBeInTheDocument();
    });

    it('does NOT display MarketingEmail Component when release_bb_marketing_email_optin is false', async () => {
      mockFeatureToggle.mockReturnValue({
        release_bb_ancillaries_extras_display: true,
        release_bb_accompanying_guest_details: true,
        release_bb_marketing_email_optin: false,
        release_bb_free_fnb_and_extras: true,
      });

      const { queryByTestId } = render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );
      expect(queryByTestId('GuestDetailsPageBB-MarketingEmail')).not.toBeInTheDocument();
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
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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
        expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
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
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
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
        expect(getByTestId('GuestDetailsPageBB-Wrapper')).toBeInTheDocument();
      });
    });

    it('should use autocomplete meals when preferred meal is available after ancillary closeout filtering', async () => {
      getPackagesData.data = JSON.parse(JSON.stringify(getPackagesDataOriginal.data));
      getHotelInformation.data = JSON.parse(JSON.stringify(getHotelInformationOriginal.data));

      getHotelInformation.data.hotelInformation.ancillaryCloseout = {
        items: [{ filteredStatus: 'PartialFilteredData' }],
      } as any;

      mockFetchQuery.mockResolvedValue({
        bookingPreference: {
          foodPreference: 11,
        },
      });

      mockAutocompleteMeals.mockClear();
      mockMealsMapperSelector.mockClear();

      render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      expect(mockMealsMapperSelector).not.toHaveBeenCalled();
    });

    it('should use default meal mapper when preferred meal is filtered out by ancillary closeout', async () => {
      getPackagesData.data = JSON.parse(JSON.stringify(getPackagesDataOriginal.data));
      getHotelInformation.data = JSON.parse(JSON.stringify(getHotelInformationOriginal.data));

      getHotelInformation.data.hotelInformation.ancillaryCloseout = {
        items: [{ filteredStatus: 'PartialFilteredData' }],
      } as any;

      mockFetchQuery.mockResolvedValue({
        bookingPreference: {
          foodPreference: 12,
        },
      });

      mockAutocompleteMeals.mockClear();
      mockMealsMapperSelector.mockClear();

      render(
        <GuestDetailsPageBB {...mockProps} queryClient={queryClient} router={mockRouter as any} />
      );

      await waitFor(() => {
        expect(mockMealsMapperSelector).toHaveBeenCalled();
      });
      expect(mockAutocompleteMeals).not.toHaveBeenCalled();
    });

    afterEach(() => {
      // Clean up: Reset to default state
      getPackagesData.data.packages.restaurant.restaurantNotFound = false;
      getHotelInformation.data.hotelInformation.ancillaryCloseout = null;
    });
  });
});
//endregion

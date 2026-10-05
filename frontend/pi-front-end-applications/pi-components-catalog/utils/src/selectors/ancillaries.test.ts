/* eslint-disable @typescript-eslint/no-explicit-any */
import { waitFor } from '@testing-library/react';
import type {
  RateExtraInfo,
  ReservationById,
  MealItemExtension,
  MealKids,
  Menu,
  Packages,
  SecurityInfoItem,
  RoomSelection,
  SelectedMealsPerRoom,
  MealItem,
  GuestCountUpdateRateCode,
} from '@whitbread-eos/api';
import { CITYTAX_PACKAGE, InfoItem, PrivacyPolicy, ROOM_TYPE } from '@whitbread-eos/api';

import { getLocalStorageMock } from '../getters/getLocalStorageMock';
import {
  addReservationNumber,
  adultsMealsSelector,
  autocompleteMeals,
  calculateTotalCostRoomSelection,
  checkIfExistPreselection,
  childrenMealsSelector,
  enhanceMeal,
  enhancePrivacyPolicyMoreInfoItem,
  enhanceSelectedMeals,
  formatImportantNotes,
  freeBreakfastMaxAllowance,
  freeBreakfastMaxSelectedAllowance,
  getImportantMessages,
  getUniqueRoomProperties,
  isCityTaxAvailable,
  mealsMapperSelector,
  menusSelector,
  numberOfSelectionsPerRoomSelector,
  roomInformationSelector,
  securityNoticeMoreInfoDataSelector,
  selectedMealsPerRoomSelector,
  shouldDisplayAutocompleteNotification,
  sortMealsByOrderOrName,
  sortMealsByReservationId,
  transformBartIdToOperaId,
  filterPackagesByAncillariesCloseOut,
  getCurrentReservationStorageData,
  extrasPackagesMapperSelector,
  addOrRemoveExtrasRoom,
  roomPackageSelection,
  addOrRemoveAllRoomsExtras,
  validateMealAssetsSrc,
} from './ancillaries';

const mockDataPCKSPackages: Packages = {
  meals: [
    {
      currency: 'GBP',
      id: 'ABCDE',
      bartId: '2',
      price: 9.5,
      name: 'Premier Inn Breakfast',
      description: 'All you can eat breakfast',
      allergyInfoLabel: 'Allergy & nutrition info',
      totalPrice: 10,
      totalPriceForEntireStay: 10,
      imageSrc: 'https://secure2.premierinn.com/image.png',
      allergyInfoSrc: 'https://secure2.premierinn.com/allergy',
      order: 2,
      freeBreakfastOption: true,
      freeBreakfastCode: 'VWXYZ',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
    {
      allergyInfoLabel: 'Allergy & nutrition info',
      allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
      currency: 'GBP',
      description:
        '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n',
      id: 'BFADCT',
      bartId: '3',
      imageSrc: '/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
      name: 'Continental Breakfast',
      order: 3,
      price: 7.5,
      totalPrice: 10,
      totalPriceForEntireStay: 10,
      freeBreakfastOption: false,
      freeBreakfastCode: 'VWXYZ',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: 'src2',
        name: 'name',
      },
    },
    {
      allergyInfoLabel: 'Allergy & nutrition info',
      allergyInfoSrc: '/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
      currency: 'GBP',
      description:
        '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
      id: 'MDP',
      bartId: '1',
      imageSrc: '/content/dam/global/restaurants/Global/meal-deal-booking.png',
      name: 'Meal Deal',
      order: 1,
      price: 24.99,
      totalPrice: 10,
      totalPriceForEntireStay: 10,
      freeBreakfastOption: true,
      freeBreakfastCode: 'VWXYZ',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: 'src3',
        name: 'name',
      },
    },
  ],
  mealsKids: [
    {
      name: 'Premier Inn Breakfast',
      id: 'VWXYZ',
      imageSrc: 'https://secure2.premierinn.com/image.png',
      description: 'All you can eat breakfast',
      allergyInfoSrc: 'https://secure2.premierinn.com/allergy',
      allergyInfoLabel: 'Allergy & nutrition info',
      order: 0,
      menu: {
        menuSrc: 'src4',
        name: 'name',
      },
    },
  ],
  roomSelection: [
    {
      packagesSelection: [
        {
          id: 'MDP',
          noOfSelections: 1,
        },
      ],
    },
    {
      packagesSelection: [
        {
          id: 'VWXYZ',
          noOfSelections: 2,
        },
      ],
    },
  ],
};

const mockMultiMenuArray: Menu[] = [
  {
    menuSrc: 'https://secure2.premierinn.comsrc1',
    name: 'name',
  },
  {
    menuSrc: 'https://secure2.premierinn.comsrc2',
    name: 'name',
  },
  {
    menuSrc: 'https://secure2.premierinn.comsrc3',
    name: 'name',
  },
  {
    menuSrc: 'https://secure2.premierinn.comsrc4',
    name: 'name',
  },
];

const moreInfoSecurityItems: SecurityInfoItem[] = [
  {
    image: '/content/dam/global/booking/verisign.png',
    description:
      '<p><b>VeriSign<br></b></p><p>Premier Inn takes the… to a handful of security screened staff.<br></p>',
  },
];

const enhancedPrivacyPolicyMoreInfoData: SecurityInfoItem[] = [
  {
    image: 'https://secure2.premierinn.com/content/dam/global/booking/verisign.png',
    description:
      '<p><b>VeriSign<br></b></p><p>Premier Inn takes the… to a handful of security screened staff.<br></p>',
  },
  {
    image: 'https://secure2.premierinn.com/content/dam/hub/app/MasterCard.jpg',
    description:
      '<p><b>MasterCard<br></b></span></p><p>MasterCard S…chases at participating online merchants.<br></p>',
  },
  {
    image:
      'https://secure2.premierinn.com/content/dam/global/booking/privacy_icon_visa_verified.png',
    description:
      '<p><b>Verified by Visa<br></b></p><p>Verified by V…ssuing bank will verify your password.</span></p>',
  },
];

const rawMeals: MealItemExtension[] = [
  {
    currency: 'GBP',
    id: 'ABCDE',
    price: 9.5,
    name: 'Premier Inn Breakfast',
    description: 'All you can eat breakfast',
    allergyInfoLabel: 'Allergy & nutrition info',
    totalPrice: 10,
    totalPriceForEntireStay: 10,
    imageSrc: '/image.png',
    allergyInfoSrc: '/allergy',
    order: 2,
    bartId: '2',
    freeBreakfastOption: true,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src',
      name: 'name',
    },
  },
  {
    allergyInfoLabel: 'Allergy & nutrition info',
    allergyInfoSrc: '/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
    currency: 'GBP',
    description:
      '<p>A lighter start with tasty pastries, American pancakes, fruit and cereals. Includes smoothies and juices.</p>\r\n',
    id: 'BFADCT',
    imageSrc: '/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
    name: 'Continental Breakfast',
    order: 3,
    bartId: '3',
    totalPrice: 7.5,
    totalPriceForEntireStay: 10,
    price: 7.5,
    freeBreakfastOption: false,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src',
      name: 'name',
    },
  },
  {
    allergyInfoLabel: 'Allergy & nutrition info',
    allergyInfoSrc: '/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
    currency: 'GBP',
    description:
      '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
    id: 'MDP',
    imageSrc: '/content/dam/global/restaurants/Global/meal-deal-booking.png',
    name: 'Meal Deal',
    order: 1,
    bartId: '1',
    price: 24.99,
    totalPrice: 24.99,
    totalPriceForEntireStay: 10,
    freeBreakfastOption: true,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src',
      name: 'name',
    },
  },
];

const mockMealsSelections: SelectedMealsPerRoom = {
  adults: ['MDP'],
  children: ['BFCHDF'],
};

const mockAdultsMeals: MealItemExtension[] = [
  {
    allergyInfoLabel: 'Allergy & nutrition info',
    allergyInfoSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
    currency: 'GBP',
    description:
      '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
    id: 'MDP',
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/meal-deal-booking.png',
    name: 'Meal Deal',
    order: 1,
    bartId: '1',
    price: 24.99,
    totalPrice: 49.98,
    totalPriceForEntireStay: 10,
    freeBreakfastOption: true,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src',
      name: 'name',
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
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
    name: 'Continental Breakfast',
    order: 3,
    bartId: '3',
    totalPrice: 7.5,
    totalPriceForEntireStay: 10,
    price: 7.5,
    freeBreakfastOption: false,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src',
      name: 'name',
    },
  },
];

const mockChildrenMeals: MealKids[] = [
  {
    allergyInfoLabel: '',
    allergyInfoSrc: '',
    description:
      '<p>Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast or Meal Deal.</p>\r\n',
    id: 'BFCHDF',
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/child-breakfast.jpg',
    name: 'Free breakfast for kids',
    order: 0,
    menu: {
      menuSrc: 'src',
      name: 'name',
    },
  },
];

const mockSelectedExtrasList = [
  {
    packagesList: [],
    reservationId: '1857495',
    price: 0,
  },
];

const mockReservationListItems: ReservationById[] = [
  {
    reservationId: '1857495',
    roomStay: {
      accessibleRoom: {
        isAccessible: true,
        phoneNumber: '0333 321 3104',
      },
      adultsNumber: 1,
      arrivalDate: '2022-06-20',
      childrenNumber: 0,
      departureDate: '2022-06-22',
      infoMessages: [],
      rateName: 'Flex',
      roomType: 'Double',
      rateExtraInfo: {
        rateName: 'Flex',
      } as RateExtraInfo,
      ratePlanCode: 'FLEXRATE',
      roomExtraInfo: {
        roomType: 'Double',
        roomName: 'Double',
        roomDescription: '',
      },
    },
    billing: {
      address: {
        addressLine1: '',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        country: '',
        postalCode: '',
      },
      email: '',
      firstName: '',
      lastName: '',
      telephone: '',
      title: '',
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: '',
      },
    ],
    depositPolicies: [
      {
        policyCode: '',
        amountPaid: {
          amount: 0,
          currencyCode: '',
        },
        amountDue: {
          amount: 0,
          currencyCode: '',
        },
      },
    ],
  },
];

// TODO: check this at the end - 13.03
const mockReservationListItemsMultipleAdultsAndChildren: ReservationById[] = [
  {
    roomStay: {
      accessibleRoom: {
        isAccessible: true,
        phoneNumber: '0333 321 3104',
      },
      adultsNumber: 2,
      arrivalDate: '2022-06-20',
      childrenNumber: 1,
      departureDate: '2022-06-22',
      infoMessages: [],
      rateName: 'Flex',
      roomType: 'Double',
      rateExtraInfo: {
        rateName: 'Flex',
      } as RateExtraInfo,
      ratePlanCode: 'FLEXRATE',
      roomExtraInfo: {
        roomType: 'Double',
        roomName: 'Double',
        roomDescription: '',
      },
    },
    billing: {
      address: {
        addressLine1: '',
        addressLine2: '',
        addressLine3: '',
        addressLine4: '',
        country: '',
        postalCode: '',
      },
      email: '',
      firstName: '',
      lastName: '',
      telephone: '',
      title: '',
    },
    reservationGuestList: [
      {
        givenName: '',
        surName: '',
      },
    ],
    depositPolicies: [
      {
        policyCode: '',
        amountPaid: {
          amount: 0,
          currencyCode: '',
        },
        amountDue: {
          amount: 0,
          currencyCode: '',
        },
      },
    ],
  },
];

const privacyPolicy = {
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
} as PrivacyPolicy;

const expectPrivacyPolicy = {
  description:
    '<p>We need to collect and keep some mandatory information in order to process your booking. Full details about how we use your data are set out in our Privacy notice. Premier Inn Hotels Limited (company no. 5137608) is a member of the Whitbread Group, the parent of which is Whitbread Group PLC (company no. 29423). Registered office: Whitbread Court, Houghton Hall Business Park, Porz Avenue, Dunstable LU5 5XE.</p>\n',
  linkLabel: 'View our Privacy Notice',
  linkSrc: 'https://secure2.premierinn.com/gb/en/terms/privacy-policy.html',
  moreInfoLabel: 'Find out more',
  moreInfo: [
    {
      description:
        '<p><b>VeriSign</b><br>\n<br>\n</p>\n<p>Premier Inn takes the security of any information we hold very seriously, and will always implement security measures that are in line with, or exceed current best practices and recommendations. Where necessary, and in common with other websites, we use SSL (Secure Sockets Layer) encryption to ensure that information provided to us is not visible to anybody else when in transit between your computer and our servers. You can tell when SSL is in use by the presence of a small &quot;padlock&quot; symbol in the status bar or next to the address bar of your web browser. In addition, our web servers are housed behind a secure firewall that prevents access to our databases from unauthorised users. All of our servers are housed in a secure environment with high levels of physical security, and access is only permitted to a handful of security screened staff.</p>\n',
      image: 'https://secure2.premierinn.com/content/dam/global/booking/verisign.png',
    },
    {
      description:
        '<p><b>MasterCard</b><br>\n<br>\n</p>\n<p>MasterCard SecureCode is a service to enhance your existing MasterCard account. A private code means added protection against unauthorized use of your card when you shop at participating online retailers. Once youve registered and created your own private SecureCode, you will be automatically prompted by your financial institution at checkout to provide your SecureCode each time you make a purchase with a participating online merchant. Your SecureCode is quickly confirmed by your financial institution and then your purchase is completed. Your SecureCode will never be shared with the merchant. Its just like entering your PIN at an ATM. When you correctly enter your SecureCode during a purchase at a participating online merchant, you confirm that you are the authorized cardholder and your purchase is then completed. If an incorrect SecureCode is entered, the purchase will not be completed. Even if someone knows your credit or debit card number, the purchase cannot be completed without your SecureCode at a participating merchant. How do I sign up for MasterCard?<br>\n<br>\n</p>\n<p>Choosing your own private SecureCode is quick and easy. When shopping online at a participating merchant, you will be prompted to create your own SecureCode prior to checkout. When this happens, a pop up window will appear and you will be guided through the simple enrolment process before your purchase is completed. Once you have created your private SecureCode, you will use it for future purchases at participating online merchants.</p>\n',
      image: 'https://secure2.premierinn.com/content/dam/hub/app/MasterCard.jpg',
    },
    {
      description:
        '<p><b>Verified by Visa</b><br>\n<br>\n</p>\n<p>Verified by Visa is a new security service that tells on-line retailers and banks that you are a genuine cardholder when you shop on-line. It allows you to use a personal password to confirm your identity and protect your Visa card when you use your card on the Internet, providing greater reassurance and security. Through a simple checkout process, Verified by Visa confirms your identity when you make purchases in participating online stores. Its convenient and it works with your existing Visa Card. Verified by Visa is easy to use. You register your card just once and create your own password. Then, when you make purchases at participating online stores, a Verified by Visa window will appear. Simply enter your password and click submit. Your identity is verified and your purchase is secure.<br>\n<br>\n</p>\n<p><b>How do I sign up for Verified by Visa?</b><br>\n<br>\n</p>\n<p>Visit the Verified by Visa website to register your Visa Card online, alternatively contact your bank who can register your card for Verified by Visa for you. Once your bank has activated your card, Verified by Visa protects you at every participating on-line store. When you shop at a participating on-line store, your card will be automatically recognized as protected by Verified by Visa. When you are completing your purchase, your issuing bank will verify your password.</p>\n',
      image:
        'https://secure2.premierinn.com/content/dam/global/booking/privacy_icon_visa_verified.png',
    },
  ],
  name: 'We keep your personal data safe and secure.',
} as PrivacyPolicy;

const expectAdultMealsSelector = [
  {
    allergyInfoLabel: 'Allergy & nutrition info',
    allergyInfoSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/pi-band2-allergy-nutrition.pdf',
    currency: 'GBP',
    description:
      '<p>Save up to 20% off your bill with our Meal Deal offer! Enjoy a delicious two-course dinner, a selected drink* and wake up to our famous, unlimited all-you-can-eat Premier Inn Breakfast the next day.</p>\r\n',
    id: 'MDP',
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/meal-deal-booking.png',
    name: 'Meal Deal',
    order: 1,
    bartId: '1',
    price: 24.99,
    totalPrice: 49.98,
    totalPriceForEntireStay: 49.98,
    freeBreakfastOption: true,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src3',
      name: 'name',
    },
  },
  {
    currency: 'GBP',
    id: 'ABCDE',
    price: 9.5,
    name: 'Premier Inn Breakfast',
    description: 'All you can eat breakfast',
    allergyInfoLabel: 'Allergy & nutrition info',
    totalPrice: 19,
    totalPriceForEntireStay: 19,
    imageSrc: 'https://secure2.premierinn.com/image.png',
    allergyInfoSrc: 'https://secure2.premierinn.com/allergy',
    order: 2,
    bartId: '2',

    freeBreakfastOption: true,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src1',
      name: 'name',
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
    imageSrc:
      'https://secure2.premierinn.com/content/dam/global/restaurants/Global/continental-breakfast-booking.png',
    name: 'Continental Breakfast',
    order: 3,
    bartId: '3',

    totalPrice: 15,
    totalPriceForEntireStay: 15,
    price: 7.5,
    freeBreakfastOption: false,
    freeBreakfastCode: 'VWXYZ',
    freeBreakfastMaxPerMeal: 2,
    menu: {
      menuSrc: 'src2',
      name: 'name',
    },
  },
] as MealItemExtension[];

const importantMessages = [
  {
    text: 'The bathrooms in this hotel have showers only.',
    priority: '1',
    startDate: '30/09/2022',
    endDate: '01/01/2024',
  },
  {
    text: 'Mess',
    priority: '2',
    startDate: '30/09/2022',
    endDate: '01/01/2024',
  },
] as InfoItem[];

const arrivalDate = '2023-01-27';
const departureDate = '2023-01-28';

const roomTypes = [
  {
    roomStay: {
      adultsNumber: 1,
      childrenNumber: 0,
      arrivalDate: '2023-01-27',
      departureDate: '2023-01-28',
      ratePlanCode: 'FLEXRATE',
      rateExtraInfo: {
        rateName: 'Flex',
      },
      roomExtraInfo: {
        roomType: ROOM_TYPE.DOUBLE,
        roomName: 'Double room',
      },
      accessibleRoom: {
        isAccessible: false,
        phoneNumber: '0333 321 1315',
      },
    },
  },
] as ReservationById[];

const mockDataMeals = {
  arrivalDate: '2023-12-01',
  departureDate: '2023-12-14',
  ancillaryCloseoutData: {
    items: [
      {
        text: 'Restaurant Close',
        startDate: '02/12/2023',
        endDate: '04/12/2023',
        upsellCodes: '',
      },
    ],
  },
  adultsMeals: [
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
  ],
  childrenMeals: [
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
  ],
  filteredClosedOutMeals: [
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
  ],
};

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: 'https://secure2.premierinn.com',
  },
}));

afterEach(() => {
  jest.clearAllMocks();
});

const localStorageMock = getLocalStorageMock();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
});

const mockExtras = {
  id: 'HSCKIN',
  price: 10,
  selectedExtrasList: [
    { packagesList: ['HSCKIN'], reservationId: '1798695', price: 10 },
    { packagesList: ['HSCOU2'], reservationId: '1798696', price: 10 },
  ],
  selectedRoom: 0,
};

describe('ancillaries selectors', () => {
  describe('formatImportantNotes Method', () => {
    it('should return important notes order by priority', () => {
      expect(formatImportantNotes(importantMessages, arrivalDate, departureDate)).toEqual(
        'The bathrooms in this hotel have showers only.<br/>Mess'
      );
    });

    it('should return important notes order by priority with departure after period', () => {
      const departureDate = '2025-09-30';
      expect(formatImportantNotes(importantMessages, arrivalDate, departureDate)).toEqual(
        'The bathrooms in this hotel have showers only.<br/>Mess'
      );
    });

    it('should return important notes order by priority', () => {
      importantMessages[0].priority = '3';
      expect(formatImportantNotes(importantMessages, arrivalDate, departureDate)).toEqual(
        'Mess<br/>The bathrooms in this hotel have showers only.'
      );
    });

    it('should return important notes order by priority with arrival in past and departure in future', () => {
      const arrivalDate = '2021-01-27';
      const departureDate = '2026-01-28';
      expect(formatImportantNotes(importantMessages, arrivalDate, departureDate)).toEqual(
        'Mess<br/>The bathrooms in this hotel have showers only.'
      );
    });

    it('should return important notes order by priority with arrival and departure in future', () => {
      const arrivalDate = '2026-01-27';
      const departureDate = '2026-01-28';
      expect(formatImportantNotes(importantMessages, arrivalDate, departureDate)).toEqual('');
    });
  });

  describe('getImportantMessages Method', () => {
    const freshImportantMessages = [
      {
        text: 'The bathrooms in this hotel have showers only.',
        priority: '1',
        startDate: '30/09/2022',
        endDate: '01/01/2024',
      },
      {
        text: 'Mess',
        priority: '2',
        startDate: '30/09/2022',
        endDate: '01/01/2024',
      },
    ] as InfoItem[];

    it('should return formatted message array when notes are present', () => {
      expect(getImportantMessages(freshImportantMessages, arrivalDate, departureDate)).toEqual([
        'The bathrooms in this hotel have showers only.<br/>Mess',
      ]);
    });

    it('should return htmlText when provided and fall back to text', () => {
      const importantMessages = [
        {
          text: 'Plain text note',
          htmlText: '<p>HTML note</p>',
          priority: '1',
          startDate: '30/09/2022',
          endDate: '01/01/2024',
        },
        {
          text: 'Fallback text note',
          priority: '2',
          startDate: '30/09/2022',
          endDate: '01/01/2024',
        },
      ] as InfoItem[];

      expect(getImportantMessages(importantMessages, arrivalDate, departureDate)).toEqual([
        '<p>HTML note</p><br/>Fallback text note',
      ]);
    });

    it('should always hide items with hideOnBookingFlow set to true', () => {
      const bookingFlowMessages = [
        {
          text: 'Visible note',
          priority: '1',
          startDate: '30/09/2022',
          endDate: '01/01/2024',
          hideOnBookingFlow: false,
        },
        {
          text: 'Hidden note',
          priority: '2',
          startDate: '30/09/2022',
          endDate: '01/01/2024',
          hideOnBookingFlow: true,
        },
      ] as InfoItem[];

      expect(getImportantMessages(bookingFlowMessages, arrivalDate, departureDate)).toEqual([
        'Visible note',
      ]);
    });

    it('should return empty array when notes are missing', () => {
      expect(getImportantMessages(undefined, arrivalDate, departureDate)).toEqual([]);
    });

    it('should return empty array when arrivalDate is undefined', () => {
      expect(getImportantMessages(freshImportantMessages, undefined, departureDate)).toEqual([]);
    });

    it('should return empty array when departureDate is undefined', () => {
      expect(getImportantMessages(freshImportantMessages, arrivalDate, undefined)).toEqual([]);
    });
  });

  describe('getUniqueRoomProperties Method', () => {
    it('should return roomType property', () => {
      expect(getUniqueRoomProperties(roomTypes)).toEqual(ROOM_TYPE.DOUBLE);
    });

    it('should return roomName property', () => {
      expect(getUniqueRoomProperties(roomTypes, 'roomName')).toEqual('Double room');
    });
  });

  describe('securityNoticeMoreInfoDataSelector Method', () => {
    it('should return a valid privacy policy object', () => {
      expect(securityNoticeMoreInfoDataSelector(privacyPolicy)).toEqual(expectPrivacyPolicy);
    });

    it('should return a valid privacy policy object with linkSrc undefined', () => {
      privacyPolicy.linkSrc = undefined as any;
      expectPrivacyPolicy.linkSrc = '';
      expect(securityNoticeMoreInfoDataSelector(privacyPolicy)).toEqual(expectPrivacyPolicy);
    });
  });

  describe('enhancePrivacyPolicyMoreInfoItem Method', () => {
    it('should add the correct domain to image path', () => {
      expect(enhancePrivacyPolicyMoreInfoItem(moreInfoSecurityItems as SecurityInfoItem[])).toEqual(
        [enhancedPrivacyPolicyMoreInfoData[0]]
      );
    });

    it('should return empty array if given object is not valid', () => {
      expect(enhancePrivacyPolicyMoreInfoItem([])).toEqual([]);
    });

    it('should return empty array if given object is undefined', () => {
      expect(enhancePrivacyPolicyMoreInfoItem(undefined as any)).toEqual([]);
    });
  });

  describe('adultsMealsSelector Method', () => {
    it('should return meal object in correct order with correct domain added to assets path and total price calculated', () => {
      expect(adultsMealsSelector(mockDataPCKSPackages.meals, 2, 1)).toEqual(
        expectAdultMealsSelector
      );
    });

    it('should return meals with free meals at the top when free meals exist', () => {
      const response = adultsMealsSelector(mockDataPCKSPackages.meals, 2, 1);

      const freeMeals = response.filter((meal) => meal.isFree);
      const paidMeals = response.filter((meal) => !meal.isFree);

      expect(response).toEqual([...freeMeals, ...paidMeals]);
    });

    it('should calculate totalPriceForEntireStay correctly', () => {
      const response = adultsMealsSelector(mockDataPCKSPackages.meals, 2, 2);

      response.forEach((meal) => {
        expect(meal.totalPriceForEntireStay).toBe(meal.price ? meal.price * 2 * 2 : 0);
      });
    });

    it('should return mapped meals as-is when no free meals exist', () => {
      const mealsWithoutFree = mockDataPCKSPackages.meals.map((meal) => ({
        ...meal,
        isFree: false,
      }));

      const response = adultsMealsSelector(mealsWithoutFree, 2, 1);

      expect(response.some((meal) => meal.isFree)).toBeFalsy();
      expect(response).toEqual(response.sort(sortMealsByOrderOrName));
    });

    it('should return empty array if given parameter is invalid', () => {
      expect(adultsMealsSelector([], 2, 1)).toEqual([]);
    });

    it('should return empty array if given parameter is undefined', () => {
      expect(adultsMealsSelector(undefined, 2, 1)).toEqual([]);
    });

    it('should return empty array when noTotalAdults is undefined and meals is undefined', () => {
      expect(adultsMealsSelector(undefined, 2)).toEqual([]);
    });
    it('should return free meals first followed by non-free meals', () => {
      const meals = [
        {
          id: 'PAID_MEAL',
          name: 'Paid Meal',
          price: 100,
          isFree: false,
          order: 2,
        },
        {
          id: 'FREE_MEAL',
          name: 'Free Meal',
          price: 0,
          isFree: true,
          order: 1,
        },
      ] as MealItem[];

      const response = adultsMealsSelector(meals, 2, 1);

      expect(response.map((meal) => meal.id)).toEqual(['FREE_MEAL', 'PAID_MEAL']);
    });
  });

  describe('childrenMealsSelector Method', () => {
    it('should return meal object in correct order with correct domain added to assets path', () => {
      expect(childrenMealsSelector(mockDataPCKSPackages.mealsKids)).toEqual([
        {
          name: 'Premier Inn Breakfast',
          id: 'VWXYZ',
          imageSrc: 'https://secure2.premierinn.com/image.png',
          description: 'All you can eat breakfast',
          allergyInfoSrc: 'https://secure2.premierinn.com/allergy',
          allergyInfoLabel: 'Allergy & nutrition info',
          order: 0,
          menu: {
            menuSrc: 'src4',
            name: 'name',
          },
        },
      ]);
    });

    it('should return empty array if given parameter is invalid', () => {
      expect(childrenMealsSelector([])).toEqual([]);
    });

    it('should return empty array if given parameter is invalid', () => {
      expect(childrenMealsSelector([])).toEqual([]);
    });

    it('should return empty array, because childrenMeals is undefined', () => {
      expect(childrenMealsSelector(undefined as any)).toEqual([]);
    });
  });

  describe('menusSelector Method', () => {
    it('should add domain to menuSrc if it has valid path', () => {
      expect(menusSelector(mockDataPCKSPackages.meals, mockDataPCKSPackages.mealsKids)).toEqual(
        mockMultiMenuArray
      );
    });

    it('should return empty array if given parameters are invalid', () => {
      expect(menusSelector([], [])).toEqual([]);
    });
  });

  describe('enhanceMeal Method', () => {
    it('should add domain to allergyInfoSrc and imageSrc if they have valid paths', () => {
      expect(enhanceMeal(rawMeals[0])).toEqual({
        ...rawMeals[0],
        allergyInfoSrc: 'https://secure2.premierinn.com/allergy',
        imageSrc: 'https://secure2.premierinn.com/image.png',
      });
    });

    it('should return empty string for allergyInfoSrc and imageSrc if they have invalid paths', () => {
      expect(enhanceMeal({ ...rawMeals[0], allergyInfoSrc: '', imageSrc: '' })).toEqual({
        ...rawMeals[0],
        allergyInfoSrc: '',
        imageSrc: '',
      });
    });

    it('should return the same URL if allergyInfoSrc is already a full URL', () => {
      const meal = { allergyInfoSrc: 'https://secure2.premierinn.com' };
      expect(validateMealAssetsSrc(meal)).toEqual('https://secure2.premierinn.com');
    });

    it('should not return the same URL if allergyInfoSrc is not a full URL', () => {
      const meal = { allergyInfoSrc: '/somePath/resource' };
      expect(validateMealAssetsSrc(meal)).not.toEqual('/somePath/resource');
      expect(validateMealAssetsSrc(meal)).toEqual(
        'https://secure2.premierinn.com/somePath/resource'
      );
    });
  });

  describe('enhanceSelectedMeals Method', () => {
    it('should format the selected meals object with adults meals and children meals with number of selections', () => {
      expect(enhanceSelectedMeals(mockMealsSelections, mockAdultsMeals, mockChildrenMeals)).toEqual(
        {
          adultsMeals: [
            {
              id: 'MDP',
              noSelections: 1,
              price: 24.99,
              title: 'Meal Deal',
            },
          ],
          childrenMeals: [
            {
              id: 'BFCHDF',
              noSelections: 1,
              title: 'Free breakfast for kids',
            },
          ],
        }
      );
    });

    it('should format the selected meals object with adults meals and children meals with number of selections and price for adultMeals will be 0', () => {
      mockAdultsMeals[0].price = undefined as any;

      expect(enhanceSelectedMeals(mockMealsSelections, mockAdultsMeals, mockChildrenMeals)).toEqual(
        {
          adultsMeals: [
            {
              id: 'MDP',
              noSelections: 1,
              price: 0,
              title: 'Meal Deal',
            },
          ],
          childrenMeals: [
            {
              id: 'BFCHDF',
              noSelections: 1,
              title: 'Free breakfast for kids',
            },
          ],
        }
      );

      mockAdultsMeals[0].price = 24.99;
    });

    it('should return default object if no selections are made', () => {
      expect(
        enhanceSelectedMeals({ adults: [], children: [] }, mockAdultsMeals, mockChildrenMeals)
      ).toEqual({
        adultsMeals: [],
        childrenMeals: [],
      });
    });

    it('should return default object if no selections are made and adultsMeals and childrenMeals are undefined', () => {
      expect(
        enhanceSelectedMeals({ adults: [], children: [] }, undefined as any, undefined as any)
      ).toEqual({
        adultsMeals: [],
        childrenMeals: [],
      });
    });
  });

  describe('selectedMealsPerRoomSelector Method', () => {
    it('should format the selected meals object for each room with adults meals and children meals with number of selections', () => {
      expect(
        selectedMealsPerRoomSelector([mockMealsSelections], mockAdultsMeals, mockChildrenMeals)
      ).toEqual([
        {
          adultsMeals: [
            {
              id: 'MDP',
              noSelections: 1,
              price: 24.99,
              title: 'Meal Deal',
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
      ]);
    });

    it('should return default object if no selections are made', () => {
      expect(
        selectedMealsPerRoomSelector(
          [{ adults: [], children: [] }],
          mockAdultsMeals,
          mockChildrenMeals
        )
      ).toEqual([
        {
          adultsMeals: [],
          childrenMeals: [],
        },
      ]);
    });

    it('should return default object if no selections is undefined', () => {
      expect(
        selectedMealsPerRoomSelector(undefined as any, mockAdultsMeals, mockChildrenMeals)
      ).toEqual([]);
    });
  });

  describe('roomInformationSelector Method', () => {
    it('should create a list with rooms with correct details', () => {
      expect(
        roomInformationSelector(
          mockReservationListItems,
          [{ adults: ['MDP'], children: [] }],
          mockAdultsMeals,
          mockChildrenMeals,
          mockSelectedExtrasList
        )
      ).toEqual([
        {
          accessibleRoom: {
            isAccessible: true,
            phoneNumber: '0333 321 3104',
          },
          nrAdults: 1,
          nrChildren: 0,
          reservationId: '1857495',
          roomType: 'Double',
          roomName: 'Double',
          selectedExtrasList: {
            packagesList: [],
            price: 0,
            reservationId: '1857495',
          },
          selectedMeals: {
            adultsMeals: [
              {
                id: 'MDP',
                noSelections: 1,
                price: 24.99,
                title: 'Meal Deal',
              },
            ],
            childrenMeals: [],
          },
        },
      ]);
    });

    it('should create a list with rooms with where adultsNumber is undefined', () => {
      mockReservationListItems[0].roomStay.adultsNumber = undefined as any;
      expect(
        roomInformationSelector(
          mockReservationListItems,
          [{ adults: ['MDP'], children: [] }],
          mockAdultsMeals,
          mockChildrenMeals
        )
      ).toEqual([
        {
          accessibleRoom: {
            isAccessible: true,
            phoneNumber: '0333 321 3104',
          },
          nrAdults: 0,
          nrChildren: 0,
          reservationId: '1857495',
          roomType: 'Double',
          selectedExtrasList: undefined,
          roomName: 'Double',
          selectedMeals: {
            adultsMeals: [
              {
                id: 'MDP',
                noSelections: 1,
                price: 24.99,
                title: 'Meal Deal',
              },
            ],
            childrenMeals: [],
          },
        },
      ]);

      mockReservationListItems[0].roomStay.adultsNumber = 1;
    });

    it('should create a list with rooms with correct details for adults and children meals', () => {
      expect(
        roomInformationSelector(
          [
            {
              ...mockReservationListItems[0],
              roomStay: { ...mockReservationListItems[0].roomStay, childrenNumber: 1 },
            },
          ],
          [{ adults: ['MDP'], children: ['BFCHDF'] }],
          mockAdultsMeals,
          mockChildrenMeals
        )
      ).toEqual([
        {
          accessibleRoom: {
            isAccessible: true,
            phoneNumber: '0333 321 3104',
          },
          nrAdults: 1,
          nrChildren: 1,
          reservationId: '1857495',
          roomType: 'Double',
          selectedExtrasList: undefined,
          roomName: 'Double',
          selectedMeals: {
            adultsMeals: [
              {
                id: 'MDP',
                noSelections: 1,
                price: 24.99,
                title: 'Meal Deal',
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
      ]);
    });

    it('should create a list with selected meals with correct details for adults and children when multiple meals for adults are selected', () => {
      expect(
        roomInformationSelector(
          mockReservationListItemsMultipleAdultsAndChildren,
          [{ adults: ['MDP', 'BFADCT'], children: ['BFCHDF'] }],
          mockAdultsMeals,
          mockChildrenMeals
        )
      ).toEqual([
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
                id: 'MDP',
                noSelections: 1,
                price: 24.99,
                title: 'Meal Deal',
              },
              {
                id: 'BFADCT',
                noSelections: 1,
                price: 7.5,
                title: 'Continental Breakfast',
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
      ]);
    });

    it('should return default object if no selections are made and no room information is available', () => {
      expect(
        roomInformationSelector(
          [],
          [{ adults: [''], children: [] }],
          mockAdultsMeals,
          mockChildrenMeals
        )
      ).toEqual([]);
    });
  });

  it('should return object with accessible room', () => {
    expect(
      roomInformationSelector(
        [
          {
            ...mockReservationListItems[0],
            roomStay: {
              ...mockReservationListItems[0].roomStay,
              accessibleRoom: {
                isAccessible: true,
                phoneNumber: '0333 321 3104',
              },
            },
          },
        ],
        [{ adults: ['MDP'], children: ['BFCHDF'] }],
        mockAdultsMeals,
        mockChildrenMeals
      )
    ).toEqual([
      {
        accessibleRoom: {
          isAccessible: true,
          phoneNumber: '0333 321 3104',
        },
        nrAdults: 1,
        nrChildren: 0,
        reservationId: '1857495',
        roomType: 'Double',
        selectedExtrasList: undefined,
        roomName: 'Double',
        selectedMeals: {
          adultsMeals: [
            {
              id: 'MDP',
              noSelections: 1,
              price: 24.99,
              title: 'Meal Deal',
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
    ]);
  });

  describe('sortMealsByOrderOrName Method', () => {
    it('should return -1 if first item is in descending alphabetical order and they have the same order number', () => {
      expect(
        sortMealsByOrderOrName(mockDataPCKSPackages.meals[1], {
          ...mockDataPCKSPackages.meals[0],
          order: 3,
        })
      ).toEqual(-1);
    });

    it('should return 1 if first item is in ascending alphabetical order and they have the same order number', () => {
      expect(
        sortMealsByOrderOrName(
          {
            ...mockDataPCKSPackages.meals[0],
            order: 3,
          },
          mockDataPCKSPackages.meals[1]
        )
      ).toEqual(1);
    });

    it('should return 1 if first item has bigger order number', () => {
      expect(
        sortMealsByOrderOrName(
          { ...mockDataPCKSPackages.meals[0], order: 4 },
          mockDataPCKSPackages.meals[1]
        )
      ).toEqual(1);
    });
  });

  describe('numberOfSelectionsPerRoomSelector Method', () => {
    const mockSelectedMealsPerRoom = [
      {
        adults: ['MDP'],
        children: ['ABC'],
      },
      {
        adults: ['MDP', 'BFADCT'],
        children: ['ABC', 'ABC'],
      },
    ];
    const expectedOutput = [
      {
        packagesSelection: [
          { id: 'MDP', noOfSelections: 1 },
          { id: 'ABC', noOfSelections: 1 },
        ],
      },
      {
        packagesSelection: [
          { id: 'MDP', noOfSelections: 1 },
          { id: 'BFADCT', noOfSelections: 1 },
          { id: 'ABC', noOfSelections: 2 },
        ],
      },
    ];

    it('should return the new object in the correct format', () => {
      expect(numberOfSelectionsPerRoomSelector(mockSelectedMealsPerRoom)).toEqual(expectedOutput);
    });

    it('should return the new object in the correct format when no selections are made for a room', () => {
      expect(
        numberOfSelectionsPerRoomSelector([
          {
            adults: ['MDP'],
            children: ['ABC'],
          },
          {
            adults: [],
            children: [],
          },
        ])
      ).toEqual([
        {
          packagesSelection: [
            { id: 'MDP', noOfSelections: 1 },
            { id: 'ABC', noOfSelections: 1 },
          ],
        },
        {
          packagesSelection: [],
        },
      ]);
    });

    it('should return the new object in the correct format when a single room is available', () => {
      expect(
        numberOfSelectionsPerRoomSelector([
          {
            adults: ['MDP'],
            children: ['ABC'],
          },
        ])
      ).toEqual([
        {
          packagesSelection: [
            { id: 'MDP', noOfSelections: 1 },
            { id: 'ABC', noOfSelections: 1 },
          ],
        },
      ]);
    });
  });

  describe('mealsMapperSelector Method', () => {
    it('should return meals object with correct structure for each room', () => {
      expect(
        mealsMapperSelector(
          mockDataPCKSPackages.meals,
          mockDataPCKSPackages.mealsKids,
          mockDataPCKSPackages.roomSelection
        )
      ).toEqual([
        {
          adults: ['MDP'],
          children: [],
          reservationId: mockDataPCKSPackages.roomSelection[0]?.reservationId,
        },
        {
          adults: [],
          children: ['VWXYZ', 'VWXYZ'],
          reservationId: mockDataPCKSPackages.roomSelection[1]?.reservationId,
        },
      ]);
    });

    it('should return meals object with mealKids', () => {
      mockDataPCKSPackages.mealsKids[0].id = 'DDD';

      expect(
        mealsMapperSelector(
          mockDataPCKSPackages.meals,
          mockDataPCKSPackages.mealsKids,
          mockDataPCKSPackages.roomSelection
        )
      ).toEqual([
        {
          adults: ['MDP'],
          children: [],
          reservationId: mockDataPCKSPackages.roomSelection[0]?.reservationId,
        },
        {
          adults: [],
          children: [],
          reservationId: mockDataPCKSPackages.roomSelection[1]?.reservationId,
        },
      ]);

      mockDataPCKSPackages.mealsKids[0].id = 'VWXYZ';
    });

    it('should return meals object with correct structure for each room if a room has no meals assigned', () => {
      expect(
        mealsMapperSelector(mockDataPCKSPackages.meals, mockDataPCKSPackages.mealsKids, [
          {
            reservationId: '1',
            packagesSelection: [
              {
                id: 'MDP',
                noOfSelections: 1,
              },
            ],
          },
          {
            reservationId: '2',
            packagesSelection: [],
          },
        ])
      ).toEqual([
        {
          adults: ['MDP'],
          children: [],
          reservationId: '1',
        },
        {
          adults: [],
          children: [],
          reservationId: '2',
        },
      ]);
    });

    it('should return meals object with correct structure for each room if no meals are assigned', () => {
      expect(
        mealsMapperSelector(mockDataPCKSPackages.meals, mockDataPCKSPackages.mealsKids, [
          {
            reservationId: '1',
            packagesSelection: [],
          },
          {
            reservationId: '2',
            packagesSelection: [],
          },
        ])
      ).toEqual([
        {
          adults: [],
          children: [],
          reservationId: '1',
        },
        {
          adults: [],
          children: [],
          reservationId: '2',
        },
      ]);
    });

    it('should map free child meals based on children count when isAdultHasMealsFree is true', () => {
      const adultsMeals = [
        {
          id: 'BFAD',
          freeBreakfastOption: true,
          freeBreakfastCode: 'BFCH',
        },
      ] as MealItemExtension[];

      const childrenMeals = [{ id: 'BFCH' }] as MealKids[];

      const roomSelections = [
        {
          reservationId: '1',
          packagesSelection: [
            {
              id: 'BFAD',
              noOfSelections: 2,
            },
          ],
        },
      ] as RoomSelection[];

      const listGuests = {
        childrenNumber: [1],
      } as GuestCountUpdateRateCode;

      expect(
        mealsMapperSelector(adultsMeals, childrenMeals, roomSelections, true, listGuests)
      ).toEqual([
        {
          adults: ['BFAD', 'BFAD'],
          children: ['BFCH'],
          reservationId: '1',
        },
      ]);
    });

    it('should not add free child meals when room has no children', () => {
      const adultsMeals = [
        {
          id: 'BFAD',
          freeBreakfastOption: true,
          freeBreakfastCode: 'BFCH',
        },
      ] as MealItemExtension[];

      const childrenMeals = [{ id: 'BFCH' }] as MealKids[];

      const roomSelections = [
        {
          reservationId: '1',
          packagesSelection: [
            {
              id: 'BFAD',
              noOfSelections: 1,
            },
          ],
        },
      ] as RoomSelection[];

      const listGuests = {
        childrenNumber: [0],
      } as GuestCountUpdateRateCode;

      expect(
        mealsMapperSelector(adultsMeals, childrenMeals, roomSelections, true, listGuests)
      ).toEqual([
        {
          adults: ['BFAD'],
          children: [],
          reservationId: '1',
        },
      ]);
    });

    it('should add child meals when child package exists and room has children', () => {
      const adultsMeals = [{ id: 'ADULT1' }] as MealItemExtension[];

      const childrenMeals = [{ id: 'CHILD1' }] as MealKids[];

      const roomSelections = [
        {
          reservationId: '1',
          packagesSelection: [
            {
              id: 'CHILD1',
              noOfSelections: 2,
            },
          ],
        },
      ] as RoomSelection[];

      const listGuests = {
        childrenNumber: [2],
      } as GuestCountUpdateRateCode;

      expect(
        mealsMapperSelector(adultsMeals, childrenMeals, roomSelections, true, listGuests)
      ).toEqual([
        {
          adults: [],
          children: ['CHILD1', 'CHILD1'],
          reservationId: '1',
        },
      ]);
    });
  });

  const roomSelection = [
    {
      packagesSelection: [
        {
          id: 'MDP',
          noOfSelections: 2,
        },
      ],
    },
    {
      packagesSelection: [],
    },
  ];

  describe('calculateTotalCostRoomSelection Method', () => {
    it('should calculate total cost for a room selection', () => {
      expect(calculateTotalCostRoomSelection(mockDataPCKSPackages.meals, roomSelection, 1)).toEqual(
        49.98
      );
    });

    it('should calculate total cost for a room selection with undefined', () => {
      roomSelection[0].packagesSelection[0].id = 'test';
      expect(calculateTotalCostRoomSelection(mockDataPCKSPackages.meals, roomSelection, 1)).toEqual(
        0
      );
    });
  });

  let roomSelections: RoomSelection[] = [{ packagesSelection: [] }, { packagesSelection: [] }];

  describe('checkIfExistPreselection Method', () => {
    it('should return false because there no previous selections', () => {
      expect(checkIfExistPreselection(roomSelections)).toEqual(false);
    });

    it('should return true because there s no previous selections', () => {
      roomSelections = [
        {
          packagesSelection: [
            {
              noOfSelections: 1,
              id: 'ASD',
            },
          ],
        },
      ];
      expect(checkIfExistPreselection(roomSelections)).toEqual(true);
    });
  });

  const listGuests = {
    adultsNumber: [1, 1, 1, 1],
    childrenNumber: [1, 0, 0, 0],
  };
  const prefearMeal = 'MDP';

  describe('autocompleteMeals Method', () => {
    it('should return a list of selection per room', () => {
      expect(
        autocompleteMeals(
          mockDataPCKSPackages.meals,
          mockDataPCKSPackages.mealsKids,
          listGuests,
          prefearMeal
        )
      ).toEqual([
        {
          adults: ['MDP'],
          children: ['VWXYZ'],
        },
        {
          adults: ['MDP'],
          children: [],
        },
        {
          adults: ['MDP'],
          children: [],
        },
        {
          adults: ['MDP'],
          children: [],
        },
      ]);
    });

    it('should return a list of selection per room with prefUserMeal undefined', () => {
      expect(
        autocompleteMeals(
          mockDataPCKSPackages.meals,
          mockDataPCKSPackages.mealsKids,
          listGuests,
          undefined as any
        )
      ).toEqual([
        {
          adults: [],
          children: [],
        },
        {
          adults: [],
          children: [],
        },
        {
          adults: [],
          children: [],
        },
        {
          adults: [],
          children: [],
        },
      ]);
    });

    const emptyPref = { adultMeal: '', childMeal: '' };

    it('should return a list of selection per room with prefUserMeal undefined', () => {
      expect(
        autocompleteMeals(
          mockDataPCKSPackages.meals,
          mockDataPCKSPackages.mealsKids,
          listGuests,
          emptyPref as any
        )
      ).toEqual([
        {
          adults: [],
          children: [],
        },
        {
          adults: [],
          children: [],
        },
        {
          adults: [],
          children: [],
        },
        {
          adults: [],
          children: [],
        },
      ]);
    });
  });

  const preselectionsMeals: RoomSelection[] = [
    {
      packagesSelection: [],
    },
    {
      packagesSelection: [],
    },
    {
      packagesSelection: [],
    },
    {
      packagesSelection: [],
    },
  ];

  describe('checkIfExistPreselection Method', () => {
    it("should return false because don't exists previous meals selected", () => {
      expect(checkIfExistPreselection(preselectionsMeals)).toEqual(false);
    });

    it('should return true because  exists previous meals selected', () => {
      preselectionsMeals[0].packagesSelection = [{ id: 'ADS', noOfSelections: 1 }];
      expect(checkIfExistPreselection(preselectionsMeals)).toEqual(true);
    });
  });

  const selectedMeals = [
    {
      adults: ['MDP'],
      children: ['BFCHDF'],
    },
    {
      adults: ['MDP'],
      children: [],
    },
    {
      adults: ['MDP'],
      children: [],
    },
    {
      adults: ['MDP'],
      children: [],
    },
  ];

  const prefMeal = {
    adultMeal: 'MDP',
    childMeal: 'BFCHDF',
  };

  describe('shouldDisplayAutocompleteNotification Method', () => {
    it('should return true because pref meal is selected for entire stay for all guests', () => {
      expect(shouldDisplayAutocompleteNotification(listGuests, selectedMeals, prefMeal)).toEqual(
        true
      );
    });

    it('should return false because pref meal is not selected for entire stay for all guests', () => {
      selectedMeals[0].adults = [];
      expect(shouldDisplayAutocompleteNotification(listGuests, selectedMeals, prefMeal)).toEqual(
        false
      );
    });
  });

  describe('transformBartIdToOperaId Method', () => {
    it('should return MDP, associate meal with bart id 1', () => {
      expect(transformBartIdToOperaId('1', mockDataPCKSPackages.meals)).toEqual('MDP');
    });

    it('should return empty string, because dont exist that id', () => {
      expect(transformBartIdToOperaId('5', mockDataPCKSPackages.meals)).toEqual('');
    });
  });

  describe('isCityTaxAvailable Method - check if city tax package is present on roomSelections packagesSelection', () => {
    it('should return true, on city tax package present, and one room, one package and selection > 0', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [{ id: CITYTAX_PACKAGE, noOfSelections: 1 }],
        },
      ];
      const expectedResult = true;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on city tax package present, and one room, one package and selection > 0', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [{ id: CITYTAX_PACKAGE, noOfSelections: 0 }],
        },
      ];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return true, on city tax package present, and one room, multiple packages and selection > 0', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [
            { id: 'TEST1', noOfSelections: 1 },
            { id: CITYTAX_PACKAGE, noOfSelections: 1 },
            { id: 'TEST2', noOfSelections: 1 },
          ],
        },
      ];
      const expectedResult = true;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on city tax package present, and one room, multiple packages and selection = 0', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [
            { id: 'TEST1', noOfSelections: 1 },
            { id: CITYTAX_PACKAGE, noOfSelections: 0 },
            { id: 'TEST2', noOfSelections: 1 },
          ],
        },
      ];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return true, on city tax package present, and multiple rooms, multiple packages and selection > 0', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [{ id: 'TEST1', noOfSelections: 1 }],
        },
        {
          packagesSelection: [
            { id: 'TEST1', noOfSelections: 1 },
            { id: CITYTAX_PACKAGE, noOfSelections: 1 },
            { id: 'TEST2', noOfSelections: 1 },
          ],
        },
        {
          packagesSelection: [{ id: CITYTAX_PACKAGE, noOfSelections: 1 }],
        },
      ];
      const expectedResult = true;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return true, on city tax package present, and multiple rooms, multiple packages and one selection = 0', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [{ id: 'TEST1', noOfSelections: 1 }],
        },
        {
          packagesSelection: [
            { id: 'TEST1', noOfSelections: 1 },
            { id: CITYTAX_PACKAGE, noOfSelections: 0 },
            { id: 'TEST2', noOfSelections: 1 },
          ],
        },
        {
          packagesSelection: [{ id: CITYTAX_PACKAGE, noOfSelections: 1 }],
        },
      ];
      const expectedResult = true;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on city tax package not present, and multiple rooms, multiple packages', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [{ id: 'TEST1', noOfSelections: 1 }],
        },
        {
          packagesSelection: [
            { id: 'TEST1', noOfSelections: 1 },
            { id: 'TEST2', noOfSelections: 1 },
          ],
        },
      ];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on packages Selections empty array', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [],
        },
        {
          packagesSelection: [],
        },
      ];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on packages Selections empty is undefined', () => {
      const expectedResult = false;
      expect(isCityTaxAvailable(undefined)).toEqual(expectedResult);
    });

    it('should return false, on packages Selections null', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: null as any,
        },
        {
          packagesSelection: null as any,
        },
      ];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on empty roomsSelection', () => {
      const initialValue: RoomSelection[] = [];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on null roomsSelection', () => {
      const initialValue: RoomSelection[] = null as any;
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue)).toEqual(expectedResult);
    });

    it('should return false, on falsy isLeisure', () => {
      const initialValue: RoomSelection[] = [
        {
          packagesSelection: [{ id: 'TEST1', noOfSelections: 1 }],
        },
        {
          packagesSelection: [
            { id: 'TEST1', noOfSelections: 1 },
            { id: CITYTAX_PACKAGE, noOfSelections: 0 },
            { id: 'TEST2', noOfSelections: 1 },
          ],
        },
        {
          packagesSelection: [{ id: CITYTAX_PACKAGE, noOfSelections: 1 }],
        },
      ];
      const expectedResult = false;
      expect(isCityTaxAvailable(initialValue, false)).toEqual(expectedResult);
    });
  });

  const adultMeals = [
    {
      allergyInfoLabel: 'Allergy & nutrition info',
      allergyInfoSrc:
        'https://secure2.premierinn.com/content/dam/global/restaurants/Global/breakfast-allergy.pdf',
      currency: 'GBP',
      description:
        '<p>Add our unlimited, all-you-can-eat breakfast and look forward to freshly cooked bacon, fluffy hash browns, succulent sausages, eggs the way you like them, cereals, fresh fruit, croissants and much more.</p>\r\n',
      id: 'BFADBF',
      bartId: '11',
      imageSrc:
        'https://secure2.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png',
      name: 'Premier Inn Breakfast',
      price: 9.5,
      order: 1,
      freeBreakfastOption: true,
      freeBreakfastCode: 'BFCHDF',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
        name: 'Breakfast menu',
      },
      totalPrice: 9.5,
      totalPriceForEntireStay: 0,
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
      price: 7.5,
      order: 2,
      freeBreakfastOption: false,
      freeBreakfastCode: '',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
        name: 'Breakfast menu',
      },
      totalPrice: 7.5,
      totalPriceForEntireStay: 0,
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
        menuSrc: '/content/dam/global/restaurants/Global/pi-dinner-menu-band2.pdf',
        name: 'Dinner menu',
      },
      totalPrice: 24.99,
      totalPriceForEntireStay: 0,
    },
  ];

  describe('freeBreakfastMaxAllowance Method', () => {
    it('should return 4, sum of all freeBreakfastMaxPerMeal', () => {
      expect(freeBreakfastMaxAllowance(adultMeals)).toEqual(4);
    });

    it('should return 0, because adultMeals array is empty', () => {
      expect(freeBreakfastMaxAllowance([])).toEqual(0);
    });
  });

  const adultsMealsSelection = ['BFADBF'];

  describe('freeBreakfastMaxSelectedAllowance Method', () => {
    it('should return 0, because adults meals selection is empty', () => {
      expect(freeBreakfastMaxSelectedAllowance([], adultMeals)).toEqual(0);
    });

    it('should return 0, because adults meals selection is undefined', () => {
      expect(freeBreakfastMaxSelectedAllowance(undefined as any, adultMeals)).toEqual(0);
    });

    it('should return 2, because BFADBF has freeBreakfastMaxPerMeal 2', () => {
      expect(freeBreakfastMaxSelectedAllowance(adultsMealsSelection, adultMeals)).toEqual(2);
    });
  });
});

describe('Add reservation number selector', () => {
  it('should add reservationId to each room correctly', () => {
    const reservationByIdList = [{ reservationId: '1798695', roomStay: mockReservationListItems }];
    const roomSelection = [
      { reservationId: undefined, packagesSelection: [{ id: 'BFADBF', noOfSelections: 1 }] },
    ];
    expect(
      addReservationNumber(roomSelection, reservationByIdList, mockExtras.selectedExtrasList)
    ).toEqual([
      {
        reservationId: '1798695',
        packagesSelection: [
          { id: 'BFADBF', noOfSelections: 1 },
          { id: 'HSCKIN', noOfSelections: 1 },
        ],
      },
    ]);
  });
  it('should add reservationId to each room if meal selection is empty', () => {
    const reservationByIdList = [{ reservationId: '123', roomStay: mockReservationListItems }];
    const roomSelection = [{ reservationId: undefined, packagesSelection: [] }];
    expect(addReservationNumber(roomSelection, reservationByIdList)).toEqual([
      { reservationId: '123', packagesSelection: [] },
    ]);
  });

  it('should call addReservationNumber with no rooms', () => {
    const reservationByIdList = [];
    const roomSelection = [{ reservationId: undefined, packagesSelection: [] }];
    expect(addReservationNumber(roomSelection, reservationByIdList)).toEqual([]);
  });
});

describe('Sort Meals By Reservation Id', () => {
  it('should return the correct order of meals per room', () => {
    const reservationByIdList = [
      { reservationId: '123', roomStay: mockReservationListItems },
      { reservationId: '321', roomStay: mockReservationListItems },
    ];
    const roomSelection = [
      {
        reservationId: '321',
        packagesSelection: [
          { id: 'BFCHDF', noOfSelections: 1 },
          { id: 'BFADBF', noOfSelections: 1 },
        ],
      },
      { reservationId: '123', packagesSelection: [{ id: 'BFADBF', noOfSelections: 1 }] },
    ];
    expect(sortMealsByReservationId(roomSelection, reservationByIdList)).toEqual([
      roomSelection[1],
      roomSelection[0],
    ]);
  });
});

describe('filterPackagesByAncillariesCloseOut Method', () => {
  it('should return filteredAdultsMeals and filteredChildrenMeals arrays with data as arrival and depature date are within ancillary closeout date and no upsellCodes provided', () => {
    expect(filterPackagesByAncillariesCloseOut(mockDataMeals)).toEqual({
      filteredAdultsMeals: [...mockDataMeals.adultsMeals],
      filteredChildrenMeals: [...mockDataMeals.childrenMeals],
      filteredClosedOutMeals: [],
    });
  });

  it('should return filteredAdultsMeals and filteredChildrenMeals arrays with data with arrival and depature date outside of ancillary closeout date and no upsellCodes provided', () => {
    mockDataMeals.arrivalDate = '2023-11-01';
    mockDataMeals.departureDate = '2023-11-14';
    expect(filterPackagesByAncillariesCloseOut(mockDataMeals)).toEqual({
      filteredAdultsMeals: [...mockDataMeals.adultsMeals],
      filteredChildrenMeals: [...mockDataMeals.childrenMeals],
      filteredClosedOutMeals: [],
    });
  });

  it('should return filteredAdultsMeals and filteredChildrenMeals arrays with data with arrival and depature date outside of ancillary closeout date and upsellCodes provided', () => {
    mockDataMeals.arrivalDate = '2023-11-01';
    mockDataMeals.departureDate = '2023-11-14';
    mockDataMeals.ancillaryCloseoutData.items[0].upsellCodes = 'MDP,BFADBF,BBIB,BFADCT,BFCHDF';
    expect(filterPackagesByAncillariesCloseOut(mockDataMeals)).toEqual({
      filteredAdultsMeals: [...mockDataMeals.adultsMeals],
      filteredChildrenMeals: [...mockDataMeals.childrenMeals],
      filteredClosedOutMeals: [],
    });
  });
});

describe('getCurrentReservationStorageData Method', () => {
  const SILENT_SUBSTITUTION_STORAGE_KEY = 'SilentSubstitutionRoomLabels';

  it('should return null if reservationId is empty string', () => {
    expect(getCurrentReservationStorageData('')).toBeNull();
  });
  it('should return the current reservationId object from Local Storage', () => {
    localStorageMock.setItem(
      SILENT_SUBSTITUTION_STORAGE_KEY,
      JSON.stringify({
        basket123: {
          value: [
            {
              roomLabelCode: 'Double room',
              silentSubstitution: true,
            },
          ],
          expire: 123,
        },
      })
    );

    expect(getCurrentReservationStorageData('basket123')).toEqual({
      expire: 123,
      value: [
        {
          roomLabelCode: 'Double room',
          silentSubstitution: true,
        },
      ],
    });
  });
});

describe('extrasPackagesMapperSelector method', () => {
  it('should call the extrasPackagesMapperSelector method with correct props', async () => {
    const mockRoomSelection = [
      {
        reservationId: '1988357',
        packagesSelection: [
          { id: 'HSCKIN', noOfSelections: 1 },
          { id: 'HSCOU2', noOfSelections: 1 },
        ],
      },
      {
        reservationId: '1988358',
        packagesSelection: [],
      },
    ];

    const mockSelectedExtrasListResponse = [
      {
        packagesList: ['HSCKIN', 'HSCOU2'],
        price: 0,
        reservationId: '1988357',
        previousEciSelection: 1,
        previousLcoSelection: 1,
      },
      {
        packagesList: [],
        price: 0,
        reservationId: '1988358',
        previousEciSelection: 0,
        previousLcoSelection: 0,
      },
    ];

    await waitFor(() => {
      expect(extrasPackagesMapperSelector(mockRoomSelection)).toEqual(
        mockSelectedExtrasListResponse
      );
    });
  });
});

describe('addOrRemoveExtrasRoom  method', () => {
  const { id, price, selectedExtrasList, selectedRoom } = mockExtras;

  const mockExtrasResponse = [
    {
      packagesList: ['HSCKIN'],
      reservationId: '1798695',
      price: 10,
    },
    {
      packagesList: ['HSCOU2'],
      reservationId: '1798696',
      price: 10,
    },
  ];
  it('should call the addOrRemoveExtrasRoom method with correct props', async () => {
    await waitFor(() => {
      expect(addOrRemoveExtrasRoom(id, price, selectedExtrasList, selectedRoom)).toEqual(
        mockExtrasResponse
      );
    });
  });
});

describe('roomPackageSelection method', () => {
  it('should call the roomPackageSelection method with correct props', async () => {
    const mockExtrasResponse = [
      {
        packagesSelection: [
          {
            id: 'HSCKIN',
            noOfSelections: 1,
          },
        ],
        reservationId: '1798695',
        price: 10,
      },
      {
        packagesSelection: [
          {
            id: 'HSCOU2',
            noOfSelections: 1,
          },
        ],
        reservationId: '1798696',
        price: 10,
      },
    ];

    await waitFor(() => {
      expect(roomPackageSelection(mockExtras.selectedExtrasList)).toEqual(mockExtrasResponse);
    });
  });
});

describe('addOrRemoveAllRoomsExtras  method', () => {
  const { id, price, selectedExtrasList } = mockExtras;

  const mockExtrasResponse = [
    {
      packagesList: ['HSCKIN'],
      reservationId: '1798695',
      price: 10,
    },
    {
      packagesList: ['HSCOU2', 'HSCKIN'],
      reservationId: '1798696',
      price: 20,
    },
  ];
  it('should call the addOrRemoveAllRoomsExtras method with correct props', async () => {
    await waitFor(() => {
      expect(addOrRemoveAllRoomsExtras(id, price, selectedExtrasList, false)).toEqual(
        mockExtrasResponse
      );
    });
  });

  it('should call the addOrRemoveAllRoomsExtras method with isRemovable true', async () => {
    mockExtrasResponse[0].packagesList = [];
    mockExtrasResponse[0].price = 0;
    mockExtrasResponse[1].packagesList = ['HSCOU2'];
    mockExtrasResponse[1].price = 10;

    await waitFor(() => {
      expect(addOrRemoveAllRoomsExtras(id, price, selectedExtrasList, true)).toEqual(
        mockExtrasResponse
      );
    });
  });
});

describe('mealsMapperSelector - children mapping fix', () => {
  it('should map child meals using children count (not adult package count)', () => {
    const adultsMeals = [
      {
        id: 'A1',
        freeBreakfastOption: true,
        freeBreakfastCode: 'C1',
      },
    ] as any;

    const childrenMeals = [{ id: 'C1' }] as any;

    const roomSelections = [
      {
        reservationId: 'R1',
        packagesSelection: [
          { id: 'A1', noOfSelections: 1 }, // adult count = 1
        ],
      },
    ] as any;

    const listGuests = {
      adultsNumber: [1],
      childrenNumber: [2],
    } as any;

    const result = mealsMapperSelector(
      adultsMeals,
      childrenMeals,
      roomSelections,
      true,
      listGuests
    );

    expect(result[0].children).toEqual(['C1', 'C1']);
  });
});

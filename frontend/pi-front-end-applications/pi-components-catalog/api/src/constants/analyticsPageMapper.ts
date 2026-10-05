import { AnalyticsPageDetails } from '../types/analyticsData';

export const BB_PATHNAME_PREFIX = /\/business-booker|\/booking-business/g;

const PIB_PAGES: AnalyticsPageDetails[] = [
  {
    pathMatch: '/spending',
    pageName: 'Spending and Reporting: Company spending',
    pageType: 'look to book',
    page: 'Spending and reporting',
  },
  {
    pathMatch: '/manage/employees',
    pageName: 'Manage Employees',
    pageType: 'look to book',
    page: 'Manage employees',
  },
  {
    pathMatch: '/manage/allowances',
    pageName: 'Manage Allowances',
    pageType: 'look to book',
    page: 'Booking allowances',
  },
  {
    pathMatch: '/contact-us',
    pageName: 'InnBusiness',
    pageType: 'look to book',
    page: 'Contact us',
  },
  {
    pathMatch: '/manage/questions',
    pageName: 'Employee Questions',
    pageType: 'look to book',
    page: 'Employee questions',
  },
  {
    pathMatch: '/manage/company',
    pageName: 'Company Management: Company Details',
    pageType: 'look to book',
    page: 'Company details',
  },
  {
    pathMatch: '/profile',
    pageName: 'Profile Management',
    pageType: 'look to book',
    page: 'Profile',
  },
  {
    pathMatch: '/manage/cards',
    pageName: 'Card Management',
    pageType: 'look to book',
    page: 'Card management',
  },
  {
    pathMatch: '/guest-details',
    pageName: 'yourdetails',
    pageType: 'booking flow',
    page: 'GDP+Extras',
  },
  {
    pathMatch: '/account/login',
    pageName: 'InnBusiness',
    pageType: 'look to book',
    page: 'Log in',
  },
  {
    pathMatch: '/account/register',
    pageName: 'Create Your Account',
    pageType: 'look to book',
    page: 'Create account',
  },
];

const BASE_PAGES: AnalyticsPageDetails[] = [
  {
    pathMatch: '/search',
    pageName: 'premier inn search results',
    pageType: 'look to book',
    page: 'SRP',
  },
  {
    pathMatch: '/hotels/choose-bathroom',
    pageName: 'premier inn hotel details',
    pageType: 'look to book',
    page: 'HDP',
  },
  {
    pathMatch: '/hotels/choose-twinroom',
    pageName: 'premier inn choose twin room',
    pageType: 'look to book',
    page: 'HDP',
  },
  {
    pathMatch: '/hotels/', //keep general matches at the end of their section (in this case "hotels" matches)
    pageName: 'premier inn hotel details',
    pageType: 'look to book',
    page: 'HDP',
  },
  {
    pathMatch: '/account/dashboard',
    pageName: 'premier inn dashboard bookings',
    pageType: 'look to book',
    page: 'Bookings',
  },
  {
    pathMatch: '/bookings',
    pageName: 'premier inn dashboard bookings',
    pageType: 'look to book',
    page: 'Bookings',
  },
  {
    pathMatch: '/amend/details',
    pageName: 'amend details',
    pageType: 'amend details',
    page: 'Amend',
  },
  {
    pathMatch: '/amend/booking-confirmation',
    pageName: 'amend confirmation',
    pageType: 'amend confirmation',
    page: 'Amend',
  },
  {
    pathMatch: '/amend/payment',
    pageName: 'amend payment',
    pageType: 'amend payment',
    page: 'Amend',
  },
  {
    pathMatch: '/ancillaries',
    pageName: 'extras',
    pageType: 'booking flow',
    page: 'Ancillaries',
  },
  {
    pathMatch: '/guest-details',
    pageName: 'yourdetails',
    pageType: 'booking flow',
    page: 'GDP',
  },
  {
    pathMatch: '/payment',
    pageName: 'payment',
    pageType: 'booking flow',
    page: 'PaymentDetails1',
  },
  {
    pathMatch: '/confirmation',
    pageName: 'confirmation',
    pageType: 'booking flow',
    page: 'Confirmation',
  },
  {
    pathMatch: '/group-bookings',
    pageName: 'Premier Inn: Group Booking Form Request',
    pageType: 'look to book',
    page: 'GroupBooking',
  },
  {
    pathMatch: '/account/register',
    pageName: 'Premier Inn: Create an Account',
    pageType: 'look to book',
    page: 'Register',
  },
  {
    pathMatch: '/price-finder',
    pageName: 'Price Finder',
    pageType: 'look to book',
    page: 'PriceFinder',
  },
  {
    pathMatch: '/addtowallet',
    pageName: 'Web:PI:UK:QR Add to Wallet Apple',
    pageType: 'QR Add to Wallet Apple',
    page: 'AddToWallet',
  },
  {
    pathMatch: '/calendar',
    pageName: 'Price Finder',
    pageType: 'look to book',
    page: 'PriceFinder',
  },
];

export const getAnalyticsPagesMap = (siteType: 'PI' | 'PIB' | 'CCUI'): AnalyticsPageDetails[] => {
  if (siteType === 'PIB') {
    return [...PIB_PAGES, ...BASE_PAGES];
  }

  return BASE_PAGES;
};

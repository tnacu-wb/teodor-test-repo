//Basic Auth
import { BasicAuth, UserAccount } from '@WB-playwright/types';
import { BUSINESS_BOOKER_USER_ROLES, EmployeeStatus } from '@whitbread-eos/api';

export const basicAuth: BasicAuth = {
  username: process.env.PLAYWRIGHT_BASIC_AUTH_USERNAME!,
  password: process.env.PLAYWRIGHT_BASIC_AUTH_PASSWORD!,
};

export const aemAuth: BasicAuth = {
  username: '',
  password: process.env.PLAYWRIGHT_BASIC_AUTH_PASSWORD!,
};

//Inn Business Accounts
export const IB_Guest: UserAccount = {
  email: 'innbusiness_guest@mailinator.com',
  password: process.env.PLAYWRIGHT_ACCOUNT_PASSWORD!,
  title: 'Guest',
  role: BUSINESS_BOOKER_USER_ROLES.STAYER,
  status: EmployeeStatus.Active,
  name: 'Robert',
};

export const IB_Travel_Manager: UserAccount = {
  email: 'innbusiness_travelmanager@mailinator.com',
  password: process.env.PLAYWRIGHT_ACCOUNT_PASSWORD!,
  title: 'Travel Manager',
  role: BUSINESS_BOOKER_USER_ROLES.SUPER,
  status: EmployeeStatus.Active,
  name: 'Cristina',
};

export const IB_Booker: UserAccount = {
  email: 'innbusiness_booker@mailinator.com',
  password: process.env.PLAYWRIGHT_ACCOUNT_PASSWORD!,
  title: 'Booker',
  role: BUSINESS_BOOKER_USER_ROLES.BOOKER,
  status: EmployeeStatus.Active,
  name: 'Cristi',
};

export const IB_Self_Booker: UserAccount = {
  email: 'innbusiness_selfbooker@mailinator.com',
  password: process.env.PLAYWRIGHT_ACCOUNT_PASSWORD!,
  title: 'Self Booker',
  role: BUSINESS_BOOKER_USER_ROLES.SELF,
  status: EmployeeStatus.Active,
  name: 'Laura',
};

export const IB_Tethered_Travel_Manager: UserAccount = {
  email: 'venacch1@mailinator.com',
  password: process.env.PLAYWRIGHT_ACCOUNT_PASSWORD!,
  title: 'Travel Manager',
  role: BUSINESS_BOOKER_USER_ROLES.SUPER,
  status: EmployeeStatus.Active,
  name: 'Vlad',
};
export const IB_LoadMore_TravelManager: UserAccount = {
  email: 'vlad.enache@whitbread.com',
  password: process.env.PLAYWRIGHT_ACCOUNT_PASSWORD!,
  title: 'Travel Manager',
  role: BUSINESS_BOOKER_USER_ROLES.SUPER,
  status: EmployeeStatus.Active,
  name: 'VladE',
};

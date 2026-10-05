import { config } from '@WB-playwright/config';
import Chance from 'chance';
import { getYear } from 'date-fns';

const chance = new Chance();

export const Constants = {
  // country code
  UK_COUNTRY_CODE: 'gb',
  IRELAND_COUNTRY_CODE: 'ie',
  GERMANY_COUNTRY_CODE: 'de',
  ISLE_OF_MAN_COUNTRY_CODE: 'iom',

  // hotel brands
  PI: { name: 'PI', nameLowercase: 'pi' },
  PID: { name: 'PID', nameLowercase: 'pid' },
  HUB: { name: 'HUB', nameLowercase: 'hub' },
  ZIP: { name: 'ZIP', nameLowercase: 'zip' },

  // date formats
  DAY_MONTH_NAME_YEAR_DATE: 'dd MMM yy',
  ISO_DAY_DATE_FORMAT: 'yyyy-MM-dd',

  VISA_CARD: {
    holderName: `${chance.first()} ${chance.last()}`,
    cardNumber: '4111 1111 1111 1103',
    cvv: '999',
    expiryMonth: '01',
    expiryYear: `${getYear(new Date()) + 1}`,
    cardType: 'VISA',
  },

  PIBA_1: {
    holderName: `${chance.first()} ${chance.last()}`,
    cardNumber: '3089 5001 1003 1300 017',
    expiryMonth: '03',
    expiryYear: `${getYear(new Date()) + 1}`,
    cardType: 'PIBA',
  },

  SINGLE: { name: config.LANGUAGE === 'en' ? 'Single' : 'Einzel', id: 'SB' },
  DOUBLE: { name: config.LANGUAGE === 'en' ? 'Double' : 'Doppel', id: 'DB' },
  ACCESSIBLE: { name: config.LANGUAGE === 'en' ? 'Accessible' : 'Rollstuhlgerecht', id: 'DIS' },
  FAMILY: { name: config.LANGUAGE === 'en' ? 'Family' : 'Familie', id: 'FAM' },
  TWIN: { name: config.LANGUAGE === 'en' ? 'Twin' : 'Zweibett', id: 'TWIN' },
};

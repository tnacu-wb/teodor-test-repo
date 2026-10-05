import { Constants } from '@WB-playwright/constants';
import { Location } from '@WB-playwright/types';

/**
 * The locations used for testing as an enum
 */
export const Locations: Record<string, Location> = {
  BANGOR: {
    name: 'Bangor',
    suggestion: 'Bangor, UK',
    id: 'ChIJ_zQzgLUAZUgRv06vzs7KqrA',
    countryCode: Constants.UK_COUNTRY_CODE,
  },
  DRESDEN: {
    name: 'Dresden',
    suggestion: 'Dresden, Germany',
    id: 'ChIJqdYaECnPCUcRsP6IQsuxIQQ',
    countryCode: Constants.GERMANY_COUNTRY_CODE,
  },
  DUBLIN: {
    name: 'Dublin',
    suggestion: 'Dublin',
    id: 'ChIJL6wn6oAOZ0gRoHExl6nHAAo',
    countryCode: Constants.IRELAND_COUNTRY_CODE,
  },
  FRANKFURT: {
    name: 'Frankfurt',
    suggestion: 'Frankfurt, Germany',
    id: 'ChIJxZZwR28JvUcRAMawKVBDIgQ',
    countryCode: Constants.GERMANY_COUNTRY_CODE,
  },
  FREIBURG: {
    name: 'Freiburg',
    suggestion: 'Freiburg im Breisgau, Germany',
    id: 'ChIJZdYLViYbkUcRsFffpbdrHwQ',
    countryCode: Constants.GERMANY_COUNTRY_CODE,
  },
  LONDON: {
    name: 'London',
    suggestion: 'London, UK',
    id: 'ChIJdd4hrwug2EcRmSrV3Vo6llI',
    countryCode: Constants.UK_COUNTRY_CODE,
  },
  WEST_SUSSEX: {
    name: 'West Sussex',
    suggestion: 'West Sussex, UK',
    id: 'ChIJf5cWR0NY30cRwn257Oarqa4',
    countryCode: Constants.UK_COUNTRY_CODE,
  },
};

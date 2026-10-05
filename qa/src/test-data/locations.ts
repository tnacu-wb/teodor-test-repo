import { Constants } from './constants';

/** Location used when searching for hotels. */
export interface Location {
  name: string;
  suggestion: string;
  id: string;
  countryCode: string;
}

/** Collection of locations used for testing hotel search. */
export class Locations {
  private constructor() {}

  static readonly BANGOR: Location = { name: 'Bangor', suggestion: 'Bangor, UK', id: 'ChIJ_zQzgLUAZUgRv06vzs7KqrA', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly BERLIN: Location = { name: 'Berlin, Germany', suggestion: 'Berlin, Germany', id: 'ChIJAVkDPzdOqEcRcDteW0YgIQQ', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly CARDIFF: Location = { name: 'Cardiff', suggestion: 'Cardiff, UK', id: 'ChIJ9VPsNNQCbkgRDmeGZdsGNBQ', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly DRESDEN: Location = { name: 'Dresden', suggestion: 'Dresden, Germany', id: 'ChIJqdYaECnPCUcRsP6IQsuxIQQ', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly DUBLIN: Location = { name: 'Dublin', suggestion: 'Dublin', id: 'ChIJL6wn6oAOZ0gRoHExl6nHAAo', countryCode: Constants.IRELAND_COUNTRY_CODE };
  static readonly DUESSELDORF: Location = { name: 'Düsseldorf', suggestion: 'Düsseldorf, Germany', id: 'ChIJB1lG8XvJuEcRsHMqSvxgJwQ', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly DUNFERMLINE: Location = { name: 'Dunfermline', suggestion: 'Dunfermline, UK', id: 'ChIJHwzVgSzMh0gRzt496KFQHVQ', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly DURHAM: Location = { name: 'Durham', suggestion: 'Durham, UK', id: 'ChIJwbHYJaUqfEgRK0Ui9dVGimc', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly EDINBURGH: Location = { name: 'Edinburgh', suggestion: 'Edinburgh, UK', id: 'ChIJIyaYpQC4h0gRJxfnfHsU8mQ', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly FRANKFURT: Location = { name: 'Frankfurt', suggestion: 'Frankfurt, Germany', id: 'ChIJxZZwR28JvUcRAMawKVBDIgQ', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly FREIBURG: Location = { name: 'Freiburg', suggestion: 'Freiburg im Breisgau, Germany', id: 'ChIJZdYLViYbkUcRsFffpbdrHwQ', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly IPSWICH_NORTH: Location = { name: 'Ipswich', suggestion: 'Ipswich, UK', id: 'ChIJh9jVHoYH2UcRqUItfazD3TM', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly LEEDS: Location = { name: 'Leeds', suggestion: 'Leeds, UK', id: 'ChIJmb1k2ko-eUgRqdwTAv26rVE', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly LONDON: Location = { name: 'London', suggestion: 'London, UK', id: 'ChIJdd4hrwug2EcRmSrV3Vo6llI', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly MANCHESTER: Location = { name: 'Manchester', suggestion: 'Manchester, UK', id: 'ChIJ2_UmUkxNekgRqmv-BDgUvtk', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly MANCHESTER_STRETFORD: Location = { name: 'Old Trafford, Stretford, Manchester, UK', suggestion: 'Old Trafford, Stretford, Manchester, UK', id: 'ChIJq6L2mOGte0gR-RguFh56a6A', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly MUNICH: Location = { name: 'Munich', suggestion: 'Munich, Germany', id: 'ChIJ2V-Mo_l1nkcRfZixfUq4DAE', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly NOTTINGHAM: Location = { name: 'Nottingham', suggestion: 'Nottingham, UK', id: 'ChIJzXkHOdIyeEgRFdsZGSBjgBA', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly STUTTGART: Location = { name: 'Stuttgart', suggestion: 'Stuttgart, Germany', id: 'ChIJ5TCOcRa2tEcR5l6Gth3tL4Q', countryCode: Constants.GERMANY_COUNTRY_CODE };
  static readonly SANDWOOD_BAY_BEACH: Location = { name: 'Sandwood Bay Beach', suggestion: 'Sandwood Bay Beach, Lairg, UK', id: 'ChIJF86L8lAzkEgR7cudWc0ZOT0', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly WATFORD: Location = { name: 'Watford', suggestion: 'Watford, UK', id: 'ChIJW8P8CMkUdkgR8TSX67MbUl0', countryCode: Constants.UK_COUNTRY_CODE };
  static readonly WEST_SUSSEX: Location = { name: 'West Sussex', suggestion: 'West Sussex, UK', id: 'ChIJf5cWR0NY30cRwn257Oarqa4', countryCode: Constants.UK_COUNTRY_CODE };
}

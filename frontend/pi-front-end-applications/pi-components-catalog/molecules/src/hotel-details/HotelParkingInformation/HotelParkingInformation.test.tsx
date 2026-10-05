import '@testing-library/jest-dom';
import React from 'react';

import { render } from '../../utils/test-utils';
import HotelParkingInformation from './HotelParkingInformation';

const mockUseTranslation = jest.fn();
const mockUseTranslationResponse = () => {
  return {
    t: (key: string) => {
      switch (key) {
        case 'search.new.premierInn':
          return 'Premier Inn';
        case 'hoteldetails.mapparking.title':
          return 'Parking at';
        case 'searchresults.list.hotel.loading':
          return 'Loading...';
        default:
          return '';
      }
    },
  };
};

const mockUseStaticHotelInformation = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatInnerHTMLAssetUrls: () => 'parking information',
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => mockUseTranslation(),
}));

const defaultHookReturn = {
  parkingDescription: 'parking information',
  isLoading: false,
  isError: false,
  error: null,
  brand: 'PI',
  title: 'Dublin City Centre (Temple Bar) hotel',
};

describe('HotelParkingInformation', () => {
  beforeEach(() => {
    mockUseTranslation.mockReturnValue(mockUseTranslationResponse());
    mockUseStaticHotelInformation.mockReturnValue(defaultHookReturn);
  });
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('renders HotelParkingInformation with default props', () => {
    const { getByText } = render(<HotelParkingInformation />);
    expect(getByText('parking information')).toBeInTheDocument();
  });

  it('should render HotelParkingInformation', () => {
    const { getByTestId } = render(<HotelParkingInformation />);
    expect(getByTestId('hdp_parking-Section')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isLoading: true,
    });
    const { getByText } = render(<HotelParkingInformation />);
    expect(getByText('Loading...')).toBeInTheDocument();
  });

  it('should render an error message if isError is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(<HotelParkingInformation />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render HotelParkingInformation title', () => {
    const { getByText } = render(<HotelParkingInformation />);
    expect(
      getByText('Parking at Premier Inn Dublin City Centre (Temple Bar) hotel')
    ).toBeInTheDocument();
  });

  it('should render parking information', () => {
    const { getByText } = render(<HotelParkingInformation />);
    expect(getByText('parking information')).toBeInTheDocument();
  });
});

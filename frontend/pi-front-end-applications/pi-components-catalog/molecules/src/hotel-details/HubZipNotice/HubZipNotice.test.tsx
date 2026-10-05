import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import { HubZipNotice } from './HubZipNotice';

const mockUseStaticHotelInformation = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
}));

beforeEach(() => {
  mockUseStaticHotelInformation.mockReturnValue({
    brand: 'ZIP',
    isLoading: false,
    isError: false,
    error: null,
  });
});

describe('HubZipNotice', () => {
  it('should render HubZipNotice', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByTestId('hub-zip-notice')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      isLoading: true,
      isError: false,
      error: null,
      brand: '',
    });
    const { getByText } = render(<HubZipNotice />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      isLoading: false,
      isError: true,
      error: { message: 'Error' },
      brand: '',
    });
    const { getByText } = render(<HubZipNotice />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render hub notice', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'HUB',
      isLoading: false,
      isError: false,
      error: null,
    });
    const { getByTestId } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByTestId('logo-container-hub')).toBeInTheDocument();
  });

  it('should render zip notice', () => {
    const { getByTestId } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByTestId('logo-container-zip')).toBeInTheDocument();
  });

  it('should render HUB notice title', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'HUB',
      isLoading: false,
      isError: false,
      error: null,
    });
    const { getByTestId, getByText } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByTestId('hdp_hubZipNoticeTitle')).toBeInTheDocument();
    expect(getByText('hoteldetails.hub.alertText')).toBeInTheDocument();
  });

  it('should render ZIP notice title', () => {
    const { getByTestId, getByText } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByTestId('hdp_hubZipNoticeTitle')).toBeInTheDocument();
    expect(getByText('hoteldetails.priceFighter.alertText')).toBeInTheDocument();
  });

  it('should render notice subtitle', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'HUB',
      isLoading: false,
      isError: false,
      error: null,
    });
    const { getByText } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByText('hoteldetails.hub.tagline1')).toBeInTheDocument();
  });

  it('should render notice description', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: 'HUB',
      isLoading: false,
      isError: false,
      error: null,
    });
    const { getByText } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(getByText('hoteldetails.hub.tagline2')).toBeInTheDocument();
  });

  it('should render nothing if no brand is passed', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      brand: '',
      isLoading: false,
      isError: false,
      error: null,
    });
    const { queryByTestId } = render(
      <ChakraProvider>
        <HubZipNotice />
      </ChakraProvider>
    );
    expect(queryByTestId('hub-zip-notice')).toEqual(null);
  });
});

import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import { HotelContactInformation } from './HotelContactInformation';

const mockUseStaticHotelInformation = jest.fn();
const mockUseCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: (value: string) => value,
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
  useCustomLocale: () => mockUseCustomLocale(),
}));

const hotelContactResponse = {
  contactDetails: { phone: '0000 111 2222' },
  brand: '',
  isLoading: false,
  isError: false,
  error: null,
};

describe('HotelContactInformation', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue(hotelContactResponse);
    mockUseCustomLocale.mockReturnValue({ language: 'en' });
  });

  it('renders HotelContactInformationQueryWrapper with default props', () => {
    const { getByTestId } = render(<HotelContactInformation />);
    expect(getByTestId('hotel-details-contact')).toBeInTheDocument();
  });

  it('should render HotelContactInformation', () => {
    const { getByTestId } = render(<HotelContactInformation />);
    expect(getByTestId('hotel-details-contact')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...hotelContactResponse,
      isLoading: true,
    });

    const { getByText } = render(<HotelContactInformation />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...hotelContactResponse,
      error: { message: 'Error' },
      isError: true,
    });

    const { getByText } = render(<HotelContactInformation />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render contact information', () => {
    const { getByText, getByTestId } = render(<HotelContactInformation />);
    expect(getByTestId('hotel-details-contact')).toBeInTheDocument();
    expect(getByText('contactModule.Email')).toBeInTheDocument();
  });

  it('should render German contact information', () => {
    mockUseCustomLocale.mockReturnValue({ language: 'de' });

    const { getByTestId } = render(<HotelContactInformation />);
    expect(getByTestId('hotel-details-contact')).toBeInTheDocument();
  });
});

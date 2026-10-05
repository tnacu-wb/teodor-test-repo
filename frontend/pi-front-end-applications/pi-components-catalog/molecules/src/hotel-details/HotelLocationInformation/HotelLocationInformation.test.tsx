import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelLocationInformation from './HotelLocationInformation';

const mockAddress = {
  addressLine1: '27-29 Red Lion Street',
  addressLine2: 'Holborn',
  addressLine3: 'London',
  addressLine4: 'London',
  country: 'England',
  postalCode: 'WC1R 4PS',
};
const mockSatNavDirections = 'WC1R 4PS';
const mockWhatThreeWords = '///truth.drums.bowls';

const defaultHookReturn = {
  address: mockAddress,
  satNavDirections: mockSatNavDirections,
  whatThreeWords: mockWhatThreeWords,
  isLoading: false,
  isError: false,
  error: null,
};

const mockUseStaticHotelInformation = jest.fn(() => defaultHookReturn);

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
}));

describe('HotelLocationInformation', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue(defaultHookReturn);
  });

  it('renders HotelLocationInformation with default props', () => {
    const { getByTestId } = render(<HotelLocationInformation />);
    expect(getByTestId('hdp_hotelLocationAddress')).toBeInTheDocument();
  });

  it('should render HotelLocationInformation', () => {
    const { getByTestId } = render(<HotelLocationInformation />);
    expect(getByTestId('hdp_hotelLocationAddress')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({ ...defaultHookReturn, isLoading: true });
    const { getByText } = render(<HotelLocationInformation />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(<HotelLocationInformation />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render hotel location information', () => {
    const { getByText, getAllByText } = render(<HotelLocationInformation />);
    expect(getByText('27-29 Red Lion Street, Holborn, London')).toBeInTheDocument();
    expect(getAllByText('WC1R 4PS')[0]).toBeInTheDocument();
    expect(getByText('///truth.drums.bowls')).toBeInTheDocument();
  });

  it('render address format based on country - Deutschland', () => {
    const mockAddressDeutshland = {
      addressLine1: 'Dr.-Külz-Ring 15A',
      addressLine2: 'Dresden',
      addressLine3: '',
      addressLine4: '',
      country: 'Deutschland',
      postalCode: '01067',
    };

    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      address: mockAddressDeutshland,
    });

    const { getByText } = render(<HotelLocationInformation />);

    expect(getByText('Dr.-Külz-Ring 15A, 01067 Dresden')).toBeInTheDocument();
  });

  it('render address format based on country - Germany', () => {
    const mockAddressGermany = {
      addressLine1: 'Elbestraße 7',
      addressLine2: '60329 Frankfurt/Main',
      addressLine3: '',
      addressLine4: '',
      country: 'Germany',
      postalCode: '',
    };

    mockUseStaticHotelInformation.mockReturnValue({
      ...defaultHookReturn,
      address: mockAddressGermany,
    });

    const { getByText } = render(<HotelLocationInformation />);

    expect(getByText('Elbestraße 7, 60329 Frankfurt/Main')).toBeInTheDocument();
  });
});

import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import { HotelDescriptionInformation } from './HotelDescriptionInformation';

const mockUseStaticHotelInformation = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: (value: string) => value,
  useStaticHotelInformation: () => mockUseStaticHotelInformation(),
}));

const hotelDescriptionResponse = {
  hotelDescription: 'hotel description',
  isLoading: false,
  isError: false,
  error: null,
};

describe('HotelDescriptionInformation', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue(hotelDescriptionResponse);
  });

  it('renders HotelDescriptionInformation with default data', () => {
    const { getByText } = render(<HotelDescriptionInformation />);
    expect(getByText('hotel description')).toBeInTheDocument();
  });

  it('should render HotelDescriptionInformation', () => {
    const { getByTestId } = render(<HotelDescriptionInformation />);
    expect(getByTestId('hdp_description-Section')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...hotelDescriptionResponse,
      isLoading: true,
    });

    const { getByText } = render(<HotelDescriptionInformation />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue({
      ...hotelDescriptionResponse,
      error: { message: 'Error' },
      isError: true,
    });

    const { getByText } = render(<HotelDescriptionInformation />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render description information', () => {
    const { getByText } = render(<HotelDescriptionInformation />);
    expect(getByText('hotel description')).toBeInTheDocument();
  });
});

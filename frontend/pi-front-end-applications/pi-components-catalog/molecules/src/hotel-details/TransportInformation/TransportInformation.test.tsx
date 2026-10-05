import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import TransportInformation from './TransportInformation';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: jest.fn(() => ({
    transportInformation: ['test'],
    isLoading: false,
    isError: false,
    error: null,
  })),
}));

describe('TransportInformation', () => {
  it('should render TransportInformation', () => {
    const { getByTestId } = render(<TransportInformation />);
    expect(getByTestId('transport-information-section')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    const { useStaticHotelInformation } = jest.requireMock('@whitbread-eos/utils');
    useStaticHotelInformation.mockReturnValueOnce({
      transportInformation: ['test'],
      isLoading: true,
      isError: false,
      error: null,
    });
    const { getByText } = render(<TransportInformation />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    const { useStaticHotelInformation } = jest.requireMock('@whitbread-eos/utils');
    useStaticHotelInformation.mockReturnValueOnce({
      transportInformation: ['test'],
      isLoading: false,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(<TransportInformation />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render transport information', () => {
    const { getByTestId } = render(<TransportInformation />);
    expect(getByTestId('transport-information-section')).toBeInTheDocument();
  });

  it('should display "Read more" link for a height bigger than 95', () => {
    const { queryByText, getByTestId, rerender } = render(<TransportInformation />);
    const listElement = getByTestId('transport-information-list');
    jest.spyOn(listElement, 'clientHeight', 'get').mockImplementation(() => 100);

    rerender(<TransportInformation />);
    expect(queryByText('Read more') || queryByText('hoteldetails.readmore')).toBeTruthy();
  });

  it('should display "Read less" after clicking on the "Read more" link', () => {
    const { queryByText, getByTestId, rerender } = render(<TransportInformation />);
    const listElement = getByTestId('transport-information-list');
    jest.spyOn(listElement, 'clientHeight', 'get').mockImplementation(() => 100);

    rerender(<TransportInformation />);
    const readMoreLink = queryByText('Read more') || queryByText('hoteldetails.readmore');
    if (readMoreLink) {
      fireEvent.click(readMoreLink);
    }
    expect(queryByText('Read less') || queryByText('hoteldetails.readless')).toBeInTheDocument();
  });

  it('should not show "Read more" for a height less than 95', () => {
    const { queryByText } = render(<TransportInformation />);
    expect(queryByText('Read more') || queryByText('Read less')).toBeNull();
  });

  it('should not show transport information step if it is not passed', () => {
    const { useStaticHotelInformation } = jest.requireMock('@whitbread-eos/utils');
    useStaticHotelInformation.mockReturnValueOnce({
      transportInformation: [],
      isLoading: false,
      isError: false,
      error: null,
    });
    const { queryByText } = render(<TransportInformation />);
    expect(queryByText('test')).toBeNull();
  });
});

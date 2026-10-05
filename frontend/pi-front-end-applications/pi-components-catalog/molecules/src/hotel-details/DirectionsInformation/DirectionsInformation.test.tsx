import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import DirectionsInformation from './DirectionsInformation';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatInnerHTMLAssetUrls: () => 'test',
  useStaticHotelInformation: jest.fn(() => ({
    directions: 'test',
    isLoading: false,
    isError: false,
    error: null,
  })),
}));

describe('DirectionsInformation', () => {
  it('should render DirectionsInformation', () => {
    const { getByTestId } = render(<DirectionsInformation />);
    expect(getByTestId('directions-section')).toBeInTheDocument();
  });

  it('should render a loading message if isLoading prop is true', () => {
    const useStaticHotelInformation =
      jest.requireMock('@whitbread-eos/utils').useStaticHotelInformation;
    useStaticHotelInformation.mockReturnValueOnce({
      directions: null,
      isLoading: true,
      isError: false,
      error: null,
    });
    const { getByText } = render(<DirectionsInformation />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    const useStaticHotelInformation =
      jest.requireMock('@whitbread-eos/utils').useStaticHotelInformation;
    useStaticHotelInformation.mockReturnValueOnce({
      directions: null,
      isLoading: false,
      isError: true,
      error: { message: 'Error' },
    });
    const { getByText } = render(<DirectionsInformation />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render directions information', () => {
    const { getByTestId } = render(<DirectionsInformation />);
    expect(getByTestId('directions-section')).toBeInTheDocument();
  });

  it('should not render directions information if no data is passed', () => {
    const useStaticHotelInformation =
      jest.requireMock('@whitbread-eos/utils').useStaticHotelInformation;
    useStaticHotelInformation.mockReturnValueOnce({
      directions: '',
      isLoading: false,
      isError: false,
      error: null,
    });
    const { queryByTestId } = render(<DirectionsInformation />);
    expect(queryByTestId('directions-section')).toBeNull();
  });

  it('should display "Read more" link for a height bigger than 95', () => {
    const { getByText, queryByText, rerender } = render(<DirectionsInformation />);
    const textElement = getByText('test');
    jest.spyOn(textElement, 'clientHeight', 'get').mockImplementation(() => 100);

    rerender(<DirectionsInformation />);
    expect(queryByText('Read more') || queryByText('hoteldetails.readmore')).toBeTruthy();
  });

  it('should display "Read less" after clicking on the "Read more" link', () => {
    const { getByText, queryByText, rerender } = render(<DirectionsInformation />);
    const textElement = getByText('test');
    jest.spyOn(textElement, 'clientHeight', 'get').mockImplementation(() => 100);

    rerender(<DirectionsInformation />);
    const readMoreLink = queryByText('Read more') || queryByText('hoteldetails.readmore');
    if (readMoreLink) {
      fireEvent.click(readMoreLink);
    }
    expect(queryByText('Read less') || queryByText('hoteldetails.readless')).toBeInTheDocument();
  });

  it('should not show "Read more" for a height less than 95', () => {
    const { queryByText } = render(<DirectionsInformation />);
    expect(queryByText('Read more') || queryByText('Read less')).toBeNull();
  });
});

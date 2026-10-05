import '@testing-library/jest-dom';

import { render, RenderOptions } from '../../../utils/test-utils';
import BookingSummaryHotelDetailsInfo, { Props } from './BookingSummaryHotelDetailsInfo';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useSemanticTypography: jest.fn(() => (legacyTypography: object) => legacyTypography),
}));

const mockProps: Props = {
  prefixDataTestId: 'BookingSummary',
  hotelName: 'hotelNameText',
  hotelAddress: ['add1', 'add2'],
};

describe('BookingSummaryHotelDetailsInfo', () => {
  it('should render a <BookingSummaryHotelDetailsInfo> with default props', function () {
    const { getByText, queryByTestId } = render(<BookingSummaryHotelDetailsInfo {...mockProps} />);

    expect(queryByTestId('BookingSummary-HotelInformation-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-HotelInformation-HotelName')).toBeTruthy();
    expect(queryByTestId('BookingSummary-HotelInformation-HotelAddress')).toBeTruthy();
    expect(getByText('hotelNameText')).toBeInTheDocument();

    mockProps.hotelAddress.forEach((address: string) => {
      expect(getByText(address)).toBeInTheDocument();
    });
  });

  it('should render a <BookingSummaryHotelDetailsInfo> without a prefix for data test id', function () {
    mockProps.prefixDataTestId = null;
    const { queryByTestId } = render(<BookingSummaryHotelDetailsInfo {...mockProps} />);

    expect(queryByTestId('HotelInformation-Wrapper')).toBeTruthy();
    expect(queryByTestId('BookingSummary-HotelInformation-Wrapper')).toBeFalsy();
  });

  it('should render a <BookingSummaryHotelDetailsInfo> on mobile screen size', function () {
    const { getByText } = render(<BookingSummaryHotelDetailsInfo {...mockProps} />, {
      initialAppData: { screenSize: 'mobile' },
    } as Omit<RenderOptions, 'wrapper'>);

    expect(getByText(mockProps.hotelAddress.join(', '))).toBeInTheDocument();
  });
});

import '@testing-library/jest-dom';
import type { HIAvailabilityRates, HIRatesInformation, HIRoomRate } from '@whitbread-eos/api';
import { useStaticHotelInformation } from '@whitbread-eos/utils';

import { render } from '../../../utils/test-utils';
import { SoldOutNotification } from './SoldOutNotification';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useStaticHotelInformation: jest.fn(),
}));

const mockUseStaticHotelInformation = jest.mocked(useStaticHotelInformation);

const mockedProps = {
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: false,
    isErrorHotelAvailability: false,
    errorHotelAvailability: { message: '' },
    dataHotelAvailability: {} as HIAvailabilityRates,
  },
  isHotelOpeningSoon: false,
};

const soldOutNotificationProps = {
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: false,
    errorHotelAvailability: null,
    isErrorHotelAvailability: false,
    dataHotelAvailability: {
      country: 'gb',
      language: 'en',
      brand: 'PI',
      hotelId: 'KINPTI',
      hotelAvailability: {
        available: false,
        limitedAvailability: false,
        hotelId: '',
        startDate: '',
        endDate: '',
        roomRates: [] as HIRoomRate[],
      },
      ratesInformation: {} as HIRatesInformation,
    } as HIAvailabilityRates,
  },
  isHotelOpeningSoon: false,
};

describe('SoldOutNotification', () => {
  beforeEach(() => {
    mockUseStaticHotelInformation.mockReturnValue({
      name: 'London City (Old Street) hotel',
    } as any);
  });

  it('renders SoldOutNotification with default props', () => {
    const { getByTestId } = render(<SoldOutNotification {...mockedProps} />);
    expect(getByTestId('soldout-notification')).toBeInTheDocument();
  });

  it('should render SoldOutNotification', () => {
    mockUseStaticHotelInformation.mockReturnValueOnce({
      name: 'London City (Old Street) hotel',
    } as any);
    const { getByTestId } = render(<SoldOutNotification {...mockedProps} />);
    expect(getByTestId('soldout-notification')).toBeInTheDocument();
  });

  it('should render nothing when isLoading prop is true', () => {
    const { queryByTestId } = render(
      <SoldOutNotification
        {...soldOutNotificationProps}
        hotelAvailabilityResponse={{
          ...soldOutNotificationProps.hotelAvailabilityResponse,
          isLoadingHotelAvailability: true,
        }}
      />
    );
    expect(queryByTestId('soldout-notification')).not.toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    const { getByText } = render(
      <SoldOutNotification
        {...soldOutNotificationProps}
        hotelAvailabilityResponse={{
          ...soldOutNotificationProps.hotelAvailabilityResponse,
          errorHotelAvailability: { message: 'Error' },
          isErrorHotelAvailability: true,
        }}
      />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render the notification text', () => {
    const { getByTestId, getByText } = render(
      <SoldOutNotification {...soldOutNotificationProps} />
    );
    expect(getByTestId('soldout-notification')).toBeInTheDocument();
    expect(
      getByText('London City (Old Street) hotel hoteldetails.unavailableText')
    ).toBeInTheDocument();
  });

  it('should render nothing when isHotelOpeningSoon is true', () => {
    const { queryByTestId } = render(
      <SoldOutNotification {...soldOutNotificationProps} isHotelOpeningSoon />
    );
    expect(queryByTestId('soldout-notification')).toBeNull();
  });

  it('should render nothing when isHotelOpeningSoon is false and available flag is true', () => {
    soldOutNotificationProps.hotelAvailabilityResponse.dataHotelAvailability.hotelAvailability.available = true;
    const { queryByTestId } = render(
      <SoldOutNotification {...soldOutNotificationProps} isHotelOpeningSoon={false} />
    );
    expect(queryByTestId('soldout-notification')).toBeInTheDocument();
  });

  it('should show the notification when isHotelOpeningSoon is false and available flag is false', () => {
    soldOutNotificationProps.hotelAvailabilityResponse.dataHotelAvailability.hotelAvailability.available = false;
    const { getByTestId } = render(
      <SoldOutNotification {...soldOutNotificationProps} isHotelOpeningSoon={false} />
    );
    expect(getByTestId('soldout-notification')).toBeInTheDocument();
  });
});

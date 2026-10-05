import '@testing-library/jest-dom';
import type { HIAvailabilityRates } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import { OpeningSoonNotification } from './OpeningSoonNotification';

const mockedProps = {
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: false,
    isErrorHotelAvailability: false,
    errorHotelAvailability: { message: '' },
    dataHotelAvailability: {} as HIAvailabilityRates,
  },
  isHotelOpeningSoon: true,
  hotelName: 'London City (Old Street) hotel',
  hotelOpeningDate: '2023-03-31T00:00:00.000+01:00',
  language: 'en',
};

const openingSoonNotificationProps = {
  hotelAvailabilityResponse: {
    isLoadingHotelAvailability: false,
    isErrorHotelAvailability: false,
    errorHotelAvailability: { message: '' },
    dataHotelAvailability: {} as HIAvailabilityRates,
  },
  hotelName: 'London City (Old Street) hotel',
  hotelOpeningDate: '2023-03-31T00:00:00.000+01:00',
  language: 'en',
  isHotelOpeningSoon: true,
};

describe('OpeningSoonNotification', () => {
  it('renders OpeningSoonNotificationQueryWrapper with default props', () => {
    const { getByTestId } = render(<OpeningSoonNotification {...mockedProps} />);
    expect(getByTestId('opening-soon-notification')).toBeInTheDocument();
  });

  it('should render OpeningSoonNotification', () => {
    const { getByTestId } = render(<OpeningSoonNotification {...mockedProps} />);
    expect(getByTestId('opening-soon-notification')).toBeInTheDocument();
  });

  it('should render nothing when isLoading prop is true', () => {
    const { queryByTestId } = render(
      <OpeningSoonNotification
        {...openingSoonNotificationProps}
        hotelAvailabilityResponse={{
          ...openingSoonNotificationProps.hotelAvailabilityResponse,
          isLoadingHotelAvailability: true,
        }}
      />
    );
    expect(queryByTestId('opening-soon-notification')).not.toBeInTheDocument();
  });

  it('should render an error message if isError prop is true', () => {
    const { getByText } = render(
      <OpeningSoonNotification
        {...openingSoonNotificationProps}
        hotelAvailabilityResponse={{
          ...openingSoonNotificationProps.hotelAvailabilityResponse,
          errorHotelAvailability: { message: 'Error' },
          isErrorHotelAvailability: true,
        }}
      />
    );
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('should render opening soon notification in English', () => {
    const { getByTestId, getByText } = render(
      <OpeningSoonNotification {...openingSoonNotificationProps} />
    );
    const formattedOpeningDate = new Date(
      openingSoonNotificationProps.hotelOpeningDate
    ).toLocaleDateString('en-GB', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });

    expect(getByTestId('opening-soon-notification')).toBeInTheDocument();
    expect(
      getByText(
        `${openingSoonNotificationProps.hotelName} hoteldetails.openingDateText ${formattedOpeningDate}`
      )
    ).toBeInTheDocument();
  });

  it('should render opening soon notification in German', () => {
    const { getByTestId, getByText } = render(
      <OpeningSoonNotification {...openingSoonNotificationProps} language="de" />
    );

    const formattedOpeningDate = new Date(
      openingSoonNotificationProps.hotelOpeningDate
    ).toLocaleDateString('de-DE', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    });

    expect(getByTestId('opening-soon-notification')).toBeInTheDocument();
    expect(
      getByText(
        `${openingSoonNotificationProps.hotelName} hoteldetails.openingDateText ${formattedOpeningDate}`
      )
    ).toBeInTheDocument();
  });

  it('should render nothing when isHotelOpeningSoon is false', () => {
    const { queryByTestId } = render(
      <OpeningSoonNotification {...openingSoonNotificationProps} isHotelOpeningSoon={false} />
    );
    expect(queryByTestId('opening-soon-notification')).toBeNull();
  });
});

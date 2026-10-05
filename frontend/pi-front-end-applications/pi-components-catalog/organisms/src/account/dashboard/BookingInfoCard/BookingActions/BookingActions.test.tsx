import '@testing-library/jest-dom';
import { Area, BC_RESERVATION_STATUS, BOOKING_TYPE } from '@whitbread-eos/api';

import { fireEvent, render } from '../../../../utils/test-utils';
import BookingActions, { Props } from './BookingActions.component';
import {
  linksForCancelledBookings,
  linksForCCUI,
  linksForPIPastBookings,
  linksForPIUpcomingBookings,
  mockAction,
  mockConfig,
} from './mockLinks';

const mockProps: Props = {
  bookingReference: 'AQPR1437',
  operaConfNumber: '',
  area: Area.PI,
  bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
  dpaInfo: { dpaPassed: false, dpaOverride: true },
  config: mockConfig,
  overridenUserInfo: {
    reservationOverrideReasons: {
      callerName: '',
      managerName: '',
      reasonName: '',
      reasonCode: '',
    },
    reservationOverridden: false,
  },
  baseDataTestId: 'BookingActions',
  hideBookingStatus: undefined,
  isBICHeaderBookingStatusEnabled: false,
};

const Component = (props) => {
  return <BookingActions {...props} />;
};

describe('BookingActions', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('it should render the BookingActions component with the bookingReference', () => {
    const { getByText } = render(<Component {...mockProps} />);
    expect(getByText('AQPR1437')).toBeInTheDocument();
    expect(getByText('dashboard.bookings.bookingReference')).toBeInTheDocument();
  });

  it('it should render the BookingActions component with the operaConfirmationNumber for PI app.', () => {
    const { getByText } = render(<Component {...{ ...mockProps, operaConfNumber: '50676838' }} />);
    expect(getByText('50676838')).toBeInTheDocument();
    expect(getByText('account.dashboard.booking.confirmationNumber:')).toBeInTheDocument();
  });

  it('it should render the BookingActions component with basket status cancelled', () => {
    const { getByText } = render(
      <Component {...{ ...mockProps, bookingStatus: BC_RESERVATION_STATUS.CANCELLED }} />
    );
    expect(getByText('AQPR1437')).toBeInTheDocument();
  });

  it('should not show booking status inside BookingActions when isBICHeaderBookingStatusEnabled is true', () => {
    const { queryByText } = render(
      <Component
        {...{
          ...mockProps,
          isBICHeaderBookingStatusEnabled: true,
          bookingStatus: BC_RESERVATION_STATUS.CANCELLED,
        }}
      />
    );
    expect(queryByText('dashboard.bookings.cancelled')).not.toBeInTheDocument();
  });

  it('should show booking status inside BookingActions when isBICHeaderBookingStatusEnabled is false', () => {
    const { getByText } = render(
      <Component
        {...{
          ...mockProps,
          isBICHeaderBookingStatusEnabled: false,
          bookingStatus: BC_RESERVATION_STATUS.CANCELLED,
        }}
      />
    );
    expect(getByText('dashboard.bookings.cancelled')).toBeInTheDocument();
  });

  it('it should render the BookingActions component with area CCUI', () => {
    const { getByText } = render(
      <Component
        {...{ ...mockProps, area: Area.CCUI, bookingStatus: BC_RESERVATION_STATUS.COMPLETED }}
      />
    );

    mockConfig.forEach((config) => {
      expect(getByText(config.title)).toBeInTheDocument();
    });
  });

  it('it should render the BookingActions component with area CCUI and active links', () => {
    const { getByText } = render(
      <Component
        {...{ ...mockProps, area: Area.CCUI, bookingStatus: BC_RESERVATION_STATUS.COMPLETED }}
      />
    );

    linksForCCUI.forEach((config) => {
      expect(getByText(config.title)).toBeInTheDocument();

      const activeLink = getByText(config.title);
      fireEvent.click(activeLink);

      expect(config.action).toBeCalled();
    });
  });

  it('it should render the BookingActions component with the operaConfirmationNumber for CCUI app', () => {
    const { getByText } = render(
      <Component {...{ ...mockProps, operaConfNumber: '50676838', area: Area.CCUI }} />
    );
    expect(getByText('50676838')).toBeInTheDocument();
    expect(getByText('ccui.managebooking.operaConfirmation:')).toBeInTheDocument();
  });

  it('it should render certain links for cancelled reservations', () => {
    const { getByText } = render(
      <Component
        {...{
          ...mockProps,
          config: linksForCancelledBookings,
          basketReference: 'AKU2084403',
          area: Area.CCUI,
          bookingStatus: BC_RESERVATION_STATUS.CANCELLED,
        }}
      />
    );
    linksForCancelledBookings.forEach((config) => {
      expect(getByText(config.title)).toBeInTheDocument();
    });
  });

  it('it should render the Succes icon when the reservation has been overriden', () => {
    const { getByText, getByTestId } = render(
      <Component
        {...{
          ...mockProps,
          area: Area.CCUI,
          bookingStatus: BC_RESERVATION_STATUS.COMPLETED,
          overridenUserInfo: {
            reservationOverrideReasons: {
              callerName: 'test testsdfsd',
              managerName: 'test manager',
              reasonName: 'DUP',
            },
            reservationOverridden: true,
          },
        }}
      />
    );

    mockConfig.forEach((config) => {
      expect(getByText(config.title)).toBeInTheDocument();

      if (config.key === 'overridePolicies') {
        const succesIcon = getByTestId('BookingActions-OverridenSuccess');
        expect(succesIcon).toBeInTheDocument();
      }
    });
  });

  it('should render the BookingActions with Change Payment method disabled', () => {
    const { getByText } = render(
      <Component {...{ ...mockProps, area: Area.CCUI, config: linksForCCUI }} />
    );

    mockConfig.forEach((config) => {
      // expect(getByText(config.title)).toBeInTheDocument();
      if (config.key === 'changePaymentMethod') {
        config.isLinkEnabled = false;
        expect(getByText('Change Payment Method')).toHaveStyle(
          'color: var(--chakra-colors-darkGrey1)'
        );
      }
    });
  });
});

describe('BookingActions rendered from BookingHistory', () => {
  it('should render the BookingActions with area PI and without Upcoming status', () => {
    const { queryByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          dpaInfo: undefined,
          config: linksForPIUpcomingBookings,
        }}
      />
    );

    expect(queryByText('Upcoming')).not.toBeInTheDocument();
  });

  it('should render the BookingActions with action null', () => {
    const { queryByText: queryByTextPi } = render(
      <Component
        {...{
          ...mockProps,
          overridenUserInfo: { ...{ ...mockProps.overridenUserInfo, reservationOverridden: true } },
          dpaInfo: undefined,
          config: [{ ...linksForPIUpcomingBookings[0], action: null }],
        }}
      />
    );

    const { queryByText: queryByTextCcui } = render(
      <Component
        {...{
          ...mockProps,
          area: Area.CCUI,
          overridenUserInfo: null,
          dpaInfo: undefined,
          config: [{ ...linksForPIUpcomingBookings[0], action: null }],
        }}
      />
    );

    expect(queryByTextCcui('Upcoming')).not.toBeInTheDocument();
    expect(queryByTextPi('Upcoming')).not.toBeInTheDocument();
  });

  it('should render the BookingActions with type equal to header', () => {
    const { queryByText } = render(
      <Component
        {...{
          ...mockProps,
          area: Area.CCUI,
          overridenUserInfo: null,
          dpaInfo: undefined,
          config: [{ ...linksForPIUpcomingBookings[0], type: 'header', action: null }],
        }}
      />
    );

    expect(queryByText('Upcoming')).not.toBeInTheDocument();
  });

  it('should render the BookingActions with area PI and with booking reference number & resend confirmation link', () => {
    const { getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          dpaInfo: undefined,
          config: linksForPIUpcomingBookings,
        }}
      />
    );
    expect(getByText('AQPR1437')).toBeInTheDocument();
    expect(getByText('Resend Confirmation')).toBeInTheDocument();
  });

  it('should render the BookingActions component with area PI and clickable links', () => {
    const { getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          dpaInfo: undefined,
          config: linksForPIUpcomingBookings,
        }}
      />
    );

    linksForPIUpcomingBookings.forEach((config) => {
      expect(getByText(config.title)).toBeInTheDocument();

      const activeLink = getByText(config.title);
      fireEvent.click(activeLink);
      expect(config.action).toBeCalled();
    });
  });

  it('should render the BookingActions with area PI and with resend confirmation with style color', () => {
    const { getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          dpaInfo: undefined,
          config: linksForPIUpcomingBookings,
        }}
      />
    );
    expect(getByText('Resend Confirmation')).toHaveStyle(
      'color: var(--chakra-colors-btnSecondaryEnabled)'
    );
    expect(getByText('Resend Confirmation')).toHaveStyle('cursor: pointer');
  });

  it('should render the BookingActions with area PI for Past bookings', () => {
    const { getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          dpaInfo: undefined,
          config: linksForPIPastBookings,
          bookingType: BOOKING_TYPE.PAST,
        }}
      />
    );
    expect(getByText('Resend Invoice')).toHaveStyle(
      'color: var(--chakra-colors-btnSecondaryEnabled)'
    );
    expect(getByText('Resend Invoice')).toHaveStyle('cursor: pointer');
  });
});

describe('Button and Icon Rendering', () => {
  const DownloadIcon = () => <svg data-testid="download-icon" />;
  const ChangeArrowsIcon = () => <svg data-testid="changearrows-icon" />;

  const mockButtonConfig = [
    {
      title: 'Download Invoice',
      key: 'downloadInvoice',
      type: 'link',
      action: mockAction,
      isLinkEnabled: true,
      isButton: true,
      icon: DownloadIcon,
    },
    {
      title: 'Resend Invoice',
      key: 'resendInvoice',
      type: 'link',
      action: mockAction,
      isLinkEnabled: true,
      isButton: true,
      icon: ChangeArrowsIcon,
    },
    {
      title: 'Resend Confirmation',
      key: 'resendConfirmation',
      type: 'link',
      action: mockAction,
      isLinkEnabled: true,
      isButton: false,
    },
  ];

  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should render Button when isButton is true', () => {
    const { getByRole } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[0]],
        }}
      />
    );

    const button = getByRole('button', { name: /Download Invoice/i });
    expect(button).toBeInTheDocument();
    expect(button.tagName).toBe('BUTTON');
  });

  it('should render Text when isButton is false', () => {
    const { getByText, queryByRole } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[2]],
        }}
      />
    );

    expect(getByText('Resend Confirmation')).toBeInTheDocument();
    expect(queryByRole('button', { name: /Resend Confirmation/i })).not.toBeInTheDocument();
  });

  it('should render Text when isButton is undefined', () => {
    const { getByText, queryByRole } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [
            {
              ...mockButtonConfig[2],
              isButton: undefined,
            },
          ],
        }}
      />
    );

    expect(getByText('Resend Confirmation')).toBeInTheDocument();
    expect(queryByRole('button', { name: /Resend Confirmation/i })).not.toBeInTheDocument();
  });

  it('should render Icon when icon prop is provided', () => {
    const { getByTestId } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[0]],
        }}
      />
    );

    expect(getByTestId('download-icon')).toBeInTheDocument();
  });

  it('should render Icon for multiple buttons with different icons', () => {
    const { getByTestId } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[0], mockButtonConfig[1]],
        }}
      />
    );

    expect(getByTestId('download-icon')).toBeInTheDocument();
    expect(getByTestId('changearrows-icon')).toBeInTheDocument();
  });

  it('should not render Icon when icon prop is undefined', () => {
    const { queryByTestId } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [
            {
              ...mockButtonConfig[0],
              icon: undefined,
            },
          ],
        }}
      />
    );

    expect(queryByTestId('download-icon')).not.toBeInTheDocument();
  });

  it('should call onClick handler when Button is clicked', () => {
    const { getByRole } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[0]],
        }}
      />
    );

    const button = getByRole('button', { name: /Download Invoice/i });
    fireEvent.click(button);

    expect(mockAction).toHaveBeenCalled();
  });

  it('should apply button styles when isButton is true', () => {
    const { getByRole, getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[0]],
        }}
      />
    );

    const button = getByRole('button', { name: /Download Invoice/i });
    expect(button).toContainElement(getByText('Download Invoice'));
  });

  it('should render both icon and text in button', () => {
    const { getByRole, getByTestId, getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [mockButtonConfig[0]],
        }}
      />
    );

    const button = getByRole('button', { name: /Download Invoice/i });
    expect(button).toContainElement(getByTestId('download-icon'));
    expect(button).toContainElement(getByText('Download Invoice'));
  });

  it('should render buttons and text links together in mixed config', () => {
    const { getByRole, getByText } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: mockButtonConfig,
        }}
      />
    );

    // Buttons
    expect(getByRole('button', { name: /Download Invoice/i })).toBeInTheDocument();
    expect(getByRole('button', { name: /Resend Invoice/i })).toBeInTheDocument();

    // Text link
    const textLink = getByText('Resend Confirmation');
    expect(textLink).toBeInTheDocument();
    expect(textLink.tagName).not.toBe('BUTTON');
  });

  it('should handle button with disabled state', () => {
    const { getByRole } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [
            {
              ...mockButtonConfig[0],
              isLinkEnabled: false,
            },
          ],
        }}
      />
    );

    const button = getByRole('button', { name: /Download Invoice/i });
    expect(button).toBeInTheDocument();
    expect(button).toBeDisabled();
  });

  it('should render icon inside text link when isButton is false but icon is provided', () => {
    const { getByText, getByTestId } = render(
      <Component
        {...{
          ...mockProps,
          hideBookingStatus: true,
          config: [
            {
              ...mockButtonConfig[2],
              icon: DownloadIcon,
            },
          ],
        }}
      />
    );

    const textElement = getByText('Resend Confirmation');
    expect(textElement).toBeInTheDocument();
    expect(getByTestId('download-icon')).toBeInTheDocument();
  });
});

import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import RateNotifications, { RateNotificationsProps } from './RateNotifications.component';

jest.mock('../AccessibleRoomNotification', () => {
  return {
    __esModule: true,
    default: (props: { data: string }) => (
      <div data-testid="accessible-room-notification">{props.data}</div>
    ),
  };
});
jest.mock('../CotNotification', () => {
  return {
    __esModule: true,
    default: (props: { cotAvailable: boolean }) => (
      <div data-testid="cot-notification">
        {props.cotAvailable ? 'Cot Available' : 'Cot Not Available'}
      </div>
    ),
  };
});
jest.mock('../SilentSubstitutionNotificationWrapper', () => {
  return {
    __esModule: true,
    default: (props: { brand: string }) => (
      <div data-testid="silent-substitution-notification">{props.brand}</div>
    ),
  };
});

const defaultProps: RateNotificationsProps = {
  currentClassRoomTypes: [
    { id: '1', roomTypeCode: 'STD', roomTypeName: 'Standard', adults: 2, children: 0 }, // minimal mock
  ] as any,
  isNonSilentSubstituNotificPerRoomClassEnabled: false,
  brand: 'Premier Inn',
  roomTypeInformationResponse: {} as any,
  hasAccessibleRoom: false,
  accessibilityInfo: undefined,
  cot: { requested: false, available: false },
  specialRoomLimitMessage: (
    <div data-testid="special-room-limit-message">Special Limit Message</div>
  ) as any,
};

describe('RateNotifications', () => {
  it('renders SilentSubstitutionNotificationWrapper when currentClassRoomTypes is provided and isNonSilentSubstituNotificPerRoomClassEnabled is false', () => {
    render(<RateNotifications {...defaultProps} />);
    expect(screen.getByTestId('silent-substitution-notification')).toHaveTextContent(
      defaultProps.brand
    );
  });

  it('does not render SilentSubstitutionNotificationWrapper if isNonSilentSubstituNotificPerRoomClassEnabled is true', () => {
    render(
      <RateNotifications {...defaultProps} isNonSilentSubstituNotificPerRoomClassEnabled={true} />
    );
    expect(screen.queryByTestId('silent-substitution-notification')).not.toBeInTheDocument();
  });

  it('does not render SilentSubstitutionNotificationWrapper if currentClassRoomTypes is empty', () => {
    render(<RateNotifications {...defaultProps} currentClassRoomTypes={[]} />);
    expect(screen.queryByTestId('silent-substitution-notification')).not.toBeInTheDocument();
  });

  it('renders AccessibleRoomNotification when hasAccessibleRoom is true', () => {
    render(
      <RateNotifications
        {...defaultProps}
        hasAccessibleRoom={true}
        accessibilityInfo={{ text: 'Accessible info' } as any}
      />
    );
    expect(screen.getByTestId('accessible-room-notification')).toHaveTextContent('Accessible info');
  });

  it('renders AccessibleRoomNotification with empty string if accessibilityInfo is undefined', () => {
    render(
      <RateNotifications {...defaultProps} hasAccessibleRoom={true} accessibilityInfo={undefined} />
    );
    expect(screen.getByTestId('accessible-room-notification')).toHaveTextContent('');
  });

  it('does not render AccessibleRoomNotification if hasAccessibleRoom is false', () => {
    render(<RateNotifications {...defaultProps} hasAccessibleRoom={false} />);
    expect(screen.queryByTestId('accessible-room-notification')).not.toBeInTheDocument();
  });

  it('renders CotNotification when cot.requested is true and shows availability', () => {
    render(<RateNotifications {...defaultProps} cot={{ requested: true, available: true }} />);
    expect(screen.getByTestId('cot-notification')).toHaveTextContent('Cot Available');
  });

  it('renders CotNotification when cot.requested is true and cot is not available', () => {
    render(<RateNotifications {...defaultProps} cot={{ requested: true, available: false }} />);
    expect(screen.getByTestId('cot-notification')).toHaveTextContent('Cot Not Available');
  });

  it('does not render CotNotification if cot.requested is false', () => {
    render(<RateNotifications {...defaultProps} cot={{ requested: false, available: false }} />);
    expect(screen.queryByTestId('cot-notification')).not.toBeInTheDocument();
  });

  it('renders the specialRoomLimitMessage component', () => {
    render(<RateNotifications {...defaultProps} />);
    expect(screen.getByTestId('special-room-limit-message')).toHaveTextContent(
      'Special Limit Message'
    );
  });

  it('works with all features rendered at once', () => {
    render(
      <RateNotifications
        {...defaultProps}
        currentClassRoomTypes={[{ id: '1' } as any]}
        isNonSilentSubstituNotificPerRoomClassEnabled={false}
        hasAccessibleRoom={true}
        accessibilityInfo={{ text: 'Some info' } as any}
        cot={{ requested: true, available: true }}
      />
    );
    expect(screen.getByTestId('silent-substitution-notification')).toBeInTheDocument();
    expect(screen.getByTestId('accessible-room-notification')).toBeInTheDocument();
    expect(screen.getByTestId('cot-notification')).toHaveTextContent('Cot Available');
    expect(screen.getByTestId('special-room-limit-message')).toBeInTheDocument();
  });
});

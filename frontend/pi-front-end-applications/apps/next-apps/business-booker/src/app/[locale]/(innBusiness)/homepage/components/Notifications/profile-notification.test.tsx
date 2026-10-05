import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { ProfileNotification } from './profile-notification';

describe('ProfileNotification', () => {
  const defaultProps = {
    locale: LOCALES.EN,
    title: 'Profile Update Required',
    subtitle: 'Please update your profile to continue.',
    linkLabel: 'Update Profile',
    isVisible: true,
  };

  it('should render the notification when isVisible is true', () => {
    render(<ProfileNotification {...defaultProps} />);
    expect(screen.getByTestId('Notifications-ProfileUpdateRequired')).toBeInTheDocument();
    expect(screen.getByText(defaultProps.title)).toBeInTheDocument();
    expect(screen.getByText(defaultProps.subtitle)).toBeInTheDocument();
    expect(screen.getByText(defaultProps.linkLabel)).toBeInTheDocument();
  });

  it('should not render the notification when isVisible is false', () => {
    render(<ProfileNotification {...defaultProps} isVisible={false} />);
    expect(screen.queryByTestId('Notifications-ProfileUpdateRequired')).not.toBeInTheDocument();
  });

  it('should close the notification when the close button is clicked', () => {
    render(<ProfileNotification {...defaultProps} />);
    const closeButton = screen.getByTestId('Notifications-ProfileUpdateRequired-CloseButton');
    fireEvent.click(closeButton);
    expect(screen.queryByTestId('Notifications-ProfileUpdateRequired')).not.toBeInTheDocument();
  });

  it('should render the link with the correct href', () => {
    render(<ProfileNotification {...defaultProps} />);
    const link = screen.getByText(defaultProps.linkLabel);
    expect(link).toHaveAttribute('href', '/en-gb/profile');
  });
});

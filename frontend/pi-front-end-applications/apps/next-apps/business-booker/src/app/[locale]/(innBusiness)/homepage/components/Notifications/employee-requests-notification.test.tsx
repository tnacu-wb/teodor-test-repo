import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { EmployeeRequestsNotification } from './employee-requests-notification';

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (str: string) => str,
  }),
  getPathForLocale: jest.fn(() => '/mocked-path'),
  cn: (...args: any[]) => args.filter(Boolean).join(' '),
  renderSanitizedHtml: jest.fn((html: string) => html),
}));

describe('EmployeeRequestsNotification', () => {
  it('should render the notification with the correct count', () => {
    render(<EmployeeRequestsNotification locale={LOCALES.EN} count={5} />);

    expect(screen.getByTestId('Notifications-EmployeeRequests')).toBeInTheDocument();
    expect(
      screen.getByText('notifications.notification.employeeRequests.subtitle')
    ).toBeInTheDocument();
    expect(
      screen.getByText('notifications.notification.employeeRequests.manage.link')
    ).toBeInTheDocument();
  });

  it('should close the notification when the close button is clicked', () => {
    render(<EmployeeRequestsNotification locale={LOCALES.EN} count={3} />);

    const closeButton = screen.getByTestId('Notifications-EmployeeRequests-CloseButton');
    fireEvent.click(closeButton);

    expect(screen.queryByTestId('Notifications-EmployeeRequests')).not.toBeInTheDocument();
  });

  it('should render the link with the correct href', () => {
    render(<EmployeeRequestsNotification locale={LOCALES.EN} count={2} />);

    const link = screen.getByText('notifications.notification.employeeRequests.manage.link');
    expect(link).toHaveAttribute('href', '/mocked-path');
  });

  it('should not render anything if isVisible is false', () => {
    const { rerender } = render(<EmployeeRequestsNotification locale={LOCALES.EN} count={1} />);

    const closeButton = screen.getByTestId('Notifications-EmployeeRequests-CloseButton');
    fireEvent.click(closeButton);

    rerender(<EmployeeRequestsNotification locale={LOCALES.EN} count={1} />);
    expect(screen.queryByTestId('Notifications-EmployeeRequests')).not.toBeInTheDocument();
  });
});

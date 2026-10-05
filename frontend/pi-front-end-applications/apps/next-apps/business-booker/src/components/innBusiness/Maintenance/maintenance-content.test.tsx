import '@testing-library/jest-dom';
import { act, render, screen, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { MaintenanceContent } from './maintenance-content';

jest.mock('@whitbread-eos/utils', () => ({
  cn: jest.fn(),
  formatIBAssetsUrl: () => {
    return '/';
  },
  useTranslation: jest.fn(() => ({
    t: jest.fn((key) => key),
  })),
  getPathForLocale: () => {
    return '/homepage';
  },
}));

const mockProps = {
  locale: LOCALES.EN,
  icons: {
    'icon.alert.message': '/content/dam/global/icons/common/alert.svg',
  },
};

describe('MaintenanceContent', () => {
  it('should render the component', async () => {
    render(<MaintenanceContent {...mockProps} />);

    expect(screen.getByText('application.sent.failed.message')).toBeInTheDocument();
    expect(screen.getByText('application.sent.error')).toBeInTheDocument();
    expect(screen.getByText('payapp.backToHome')).toBeInTheDocument();
    expect(screen.getByTestId('Maintenance-alert-icon')).toBeInTheDocument();
    const backToHomeLink = screen.getByTestId('Maintenance-back-to-home-link');
    expect(backToHomeLink).toHaveAttribute('href', '/homepage');
    const backToHomeButton = screen.getByTestId('Maintenance-back-to-home-button');
    expect(backToHomeButton).not.toBeDisabled();
    await act(async () => {
      fireEvent.click(backToHomeButton);
    });
    expect(backToHomeButton).toBeDisabled();
    expect(screen.getByTestId('Maintenance-header')).toBeInTheDocument();
    expect(screen.getByTestId('Maintenance-IB-logo')).toBeInTheDocument();
    expect(screen.getByTestId('Maintenance-IB-logo-link')).toBeInTheDocument();
  });
});

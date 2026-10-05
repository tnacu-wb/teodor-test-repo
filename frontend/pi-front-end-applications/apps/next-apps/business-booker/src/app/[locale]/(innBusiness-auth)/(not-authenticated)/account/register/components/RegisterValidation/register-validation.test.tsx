import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { useWizardContext } from '@whitbread-eos/layout';

import AccountExistsManager from '../AccountExistsManager/account-exists-manager';
import AccountExistsNoManager from '../AccountExistsNoManager/account-exists-no-manager';
import ConfirmationEmail from '../ConfirmationEmail/confirmation-email';
import RegisterValidation from './register-validation';

jest.mock('@whitbread-eos/layout', () => ({
  useWizardContext: jest.fn(),
}));

jest.mock('../AccountExistsManager/account-exists-manager', () =>
  jest.fn(() => <div>AccountExistsManager</div>)
);
jest.mock('../AccountExistsNoManager/account-exists-no-manager', () =>
  jest.fn(() => <div>AccountExistsNoManager</div>)
);
jest.mock('../ConfirmationEmail/confirmation-email', () =>
  jest.fn(() => <div>ConfirmationEmail</div>)
);

describe('RegisterValidation', () => {
  const mockUseWizardContext = useWizardContext as jest.Mock;

  it('renders AccountExistsManager when existingEmployee is true', () => {
    mockUseWizardContext.mockReturnValue({
      wizardState: { existingEmployee: true, existingCompany: false },
    });

    render(<RegisterValidation locale={LOCALES.EN} />);

    expect(screen.getByText('AccountExistsManager')).toBeInTheDocument();
    expect(AccountExistsManager).toHaveBeenCalledWith({ locale: LOCALES.EN }, undefined);
  });

  it('renders AccountExistsNoManager when existingCompany is true and existingEmployee is false', () => {
    mockUseWizardContext.mockReturnValue({
      wizardState: { existingEmployee: false, existingCompany: true },
    });

    render(<RegisterValidation locale={LOCALES.EN} />);

    expect(screen.getByText('AccountExistsNoManager')).toBeInTheDocument();
    expect(AccountExistsNoManager).toHaveBeenCalledWith({ locale: LOCALES.EN }, undefined);
  });

  it('renders ConfirmationEmail when neither existingEmployee nor existingCompany is true', () => {
    mockUseWizardContext.mockReturnValue({
      wizardState: { existingEmployee: false, existingCompany: false },
    });

    render(<RegisterValidation locale={LOCALES.EN} />);

    expect(screen.getByText('ConfirmationEmail')).toBeInTheDocument();
    expect(ConfirmationEmail).toHaveBeenCalledWith({ locale: LOCALES.EN }, undefined);
  });

  describe('Analytics tracking', () => {
    beforeEach(() => {
      delete (window as any)._satellite;
    });

    it('should track "signUpExistingCompany" event when existingCompany is true', () => {
      const mockTrack = jest.fn();
      (window as any)._satellite = { track: mockTrack };

      mockUseWizardContext.mockReturnValue({
        wizardState: { existingEmployee: false, existingCompany: true },
      });

      render(<RegisterValidation locale={LOCALES.EN} />);

      expect(mockTrack).toHaveBeenCalledWith('signUpExistingCompany');
      expect(mockTrack).toHaveBeenCalledTimes(1);
    });

    it('should track "signUpComplete" event when existingEmployee is false and existingCompany is false', () => {
      const mockTrack = jest.fn();
      (window as any)._satellite = { track: mockTrack };

      mockUseWizardContext.mockReturnValue({
        wizardState: { existingEmployee: false, existingCompany: false },
      });

      render(<RegisterValidation locale={LOCALES.EN} />);

      expect(mockTrack).toHaveBeenCalledWith('signUpComplete');
      expect(mockTrack).toHaveBeenCalledTimes(1);
    });

    it('should not track any event when existingEmployee is true', () => {
      const mockTrack = jest.fn();
      (window as any)._satellite = { track: mockTrack };

      mockUseWizardContext.mockReturnValue({
        wizardState: { existingEmployee: true, existingCompany: false },
      });

      render(<RegisterValidation locale={LOCALES.EN} />);

      expect(mockTrack).not.toHaveBeenCalled();
    });

    it('should handle when _satellite is not available on window', () => {
      mockUseWizardContext.mockReturnValue({
        wizardState: { existingEmployee: false, existingCompany: false },
      });

      // Ensure _satellite is undefined
      expect(() => {
        render(<RegisterValidation locale={LOCALES.EN} />);
      }).not.toThrow();
    });
  });
});

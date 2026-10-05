import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import { LOCALES } from '@whitbread-eos/api';

import { render, waitFor, act, mockUseTranslation, screen } from '../../../../../utils/test-utils';
import LocationInput from './LocationInput.component';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    useTranslation: mockUseTranslation,
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

const mockOnChange = jest.fn();

const mockProps = {
  location: '',
  onChange: mockOnChange,
  placeholder: 'Where to?',
  locationIcon: '/',
  clearIcon: '/',
  onBlur: jest.fn(),
  onFocus: jest.fn(),
  errorIcon: '/',
};

class ResizeObserver {
  observe() {
    return true;
  }
  unobserve() {
    return true;
  }
  disconnect() {
    return true;
  }
}

describe('IB Location Input component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.location = '';
    (window as any).ResizeObserver = ResizeObserver;
  });

  it('should render location input component', () => {
    const { getByTestId } = render(<LocationInput {...mockProps} />);
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();
  });

  it('should render location input component with default location value', () => {
    mockProps.location = 'London';
    const { getByTestId } = render(<LocationInput {...mockProps} />);
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();
    expect(getByTestId('IB-Location-Input')).toHaveValue('London');
  });
  it('should render location input component, calling clear', async () => {
    mockProps.location = 'London';
    const { getByTestId } = render(<LocationInput {...mockProps} />);
    const clearButton = getByTestId('IB-Location-Clear');
    const input = getByTestId('IB-Location-Input');
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();
    expect(input).toHaveValue('London');

    act(() => {
      userEvent.click(clearButton);
    });

    await waitFor(() => {
      expect(mockOnChange).toHaveBeenCalled();
    });
  });

  it('should render location input component and type', async () => {
    const { getByTestId } = render(<LocationInput {...mockProps} />);
    const input = getByTestId('IB-Location-Input');
    act(() => {
      userEvent.click(input);
      userEvent.type(input, 'Euston');
    });
    expect(getByTestId('IB-Location-Container')).toBeInTheDocument();

    await waitFor(() => {
      expect(mockOnChange).toHaveBeenCalled();
    });
  });

  it('should render location input component and auto focus input if its mobile', () => {
    const { getByTestId } = render(
      <LocationInput {...mockProps} isMobileDialogVisible={true} mobile />
    );
    const input = getByTestId('IB-Location-Input-Mobile');
    expect(input).toHaveFocus();
  });

  describe('Clear icon', () => {
    it('should clear input when clear icon is clicked', () => {
      const user = userEvent;
      const onChange = jest.fn();
      const props = { ...mockProps, location: 'London', onChange };

      render(<LocationInput {...props} />);
      const clearIcon = screen.getByTestId('IB-Location-Clear');
      user.click(clearIcon);
      expect(onChange).toHaveBeenCalledWith('');
    });

    it('should clear input and focus when Enter key is pressed on clear icon', () => {
      const user = userEvent;
      const onChange = jest.fn();
      const props = { ...mockProps, location: 'London', onChange };

      render(<LocationInput {...props} />);

      const clearIcon = screen.getByTestId('IB-Location-Clear');
      clearIcon.focus();
      user.keyboard('{Enter}');

      expect(onChange).toHaveBeenCalledWith('');
      const input = screen.getByTestId('IB-Location-Input');
      expect(input).toHaveFocus();
    });

    it('should clear input and focus when Space key is pressed on clear icon', () => {
      const user = userEvent;
      const onChange = jest.fn();
      const props = { ...mockProps, location: 'London', onChange };
      render(<LocationInput {...props} />);

      const clearIcon = screen.getByTestId('IB-Location-Clear');
      clearIcon.focus();
      user.keyboard(' ');

      expect(onChange).toHaveBeenCalledWith('');
      const input = screen.getByTestId('IB-Location-Input');
      expect(input).toHaveFocus();
    });

    it('should not clear input when other keys are pressed on clear icon', () => {
      const user = userEvent;
      const onChange = jest.fn();
      const props = { ...mockProps, location: 'London', onChange };
      render(<LocationInput {...props} />);

      const clearIcon = screen.getByTestId('IB-Location-Clear');
      clearIcon.focus();
      user.keyboard('{Tab}');
      expect(onChange).not.toHaveBeenCalled();
    });

    it('should make clear icon focusable when location has value', () => {
      const props = { ...mockProps, location: 'London' };

      render(<LocationInput {...props} />);
      const clearIcon = screen.getByTestId('IB-Location-Clear');
      expect(clearIcon).toHaveAttribute('tabIndex', '0');
    });

    it('should make clear icon not focusable when location is empty', () => {
      const props = { ...mockProps, location: '' };

      render(<LocationInput {...props} />);
      const clearIcon = screen.getByTestId('IB-Location-Clear');
      expect(clearIcon).toHaveAttribute('tabIndex', '-1');
    });
  });

  describe('Error state and accessibility', () => {
    it('should set aria-invalid to true when showError is true', () => {
      const props = { ...mockProps, showError: true };
      render(<LocationInput {...props} />);

      const input = screen.getByTestId('IB-Location-Input');
      expect(input).toHaveAttribute('aria-invalid', 'true');
    });

    it('should set aria-invalid to false when showError is false or undefined', () => {
      const props = { ...mockProps, showError: false };
      render(<LocationInput {...props} />);

      const input = screen.getByTestId('IB-Location-Input');
      expect(input).toHaveAttribute('aria-invalid', 'false');
    });

    it('should link input to desktop error message with aria-describedby when showError is true', () => {
      const props = { ...mockProps, showError: true };
      render(<LocationInput {...props} />);

      const input = screen.getByTestId('IB-Location-Input');
      expect(input.getAttribute('aria-describedby')).toContain('location-error-message');
    });

    it('should link input to mobile error message with aria-describedby when showError is true and mobile is true', () => {
      const props = { ...mockProps, showError: true, mobile: true };
      render(<LocationInput {...props} />);

      const input = screen.getByTestId('IB-Location-Input-Mobile');
      expect(input.getAttribute('aria-describedby')).toContain('location-error-message-mobile');
    });

    it('should not have aria-describedby when showError is false', () => {
      const props = { ...mockProps, showError: false };
      render(<LocationInput {...props} />);

      const input = screen.getByTestId('IB-Location-Input');
      expect(input).not.toHaveAttribute('aria-describedby');
    });
  });
});

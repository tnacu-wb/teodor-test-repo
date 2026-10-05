import { act } from '@testing-library/react-hooks';
import { ComponentProps } from 'react';

import { axe, render, screen, userEvent } from '../../utils/test-utils';
import mockedCountries from './CountriesList/mocks/countries.json';
import PhoneInput from './PhoneInput.component';

/**
 * @TODO:
 * - add suport for absolute path or alias instead of relative path
 * - create a basic tests runner to avoid duplication of common tests
 * - add jest-axe or other library to test basic accessibility issues
 * - add @testing-library/react-hooks for hooks testing support
 */
jest.mock('./CountriesList', () => ({
  __esModule: true,
  default: jest.fn(() => <div>CountriesList</div>),
}));

const randomStringValue = () => Math.random().toString(36).slice(2);

const defaultProps: ComponentProps<typeof PhoneInput> = {
  countries: mockedCountries,
  placeholder: 'Mobile number',
  name: 'phone',
  onChange: jest.fn(),
  onBlur: jest.fn(),
  isAltStyle: false,
};

describe('PhoneInput', () => {
  it('should merge custom classNames to the targeted element', async () => {
    const className = randomStringValue();
    const { container } = render(<PhoneInput {...defaultProps} className={className} />);
    expect(container.firstChild).toHaveClass(className);
  });

  it('should spread properties to the targeted element', async () => {
    const testProp = 'data-t-props-spread';
    const value = randomStringValue();
    const { container } = render(<PhoneInput {...defaultProps} {...{ [testProp]: value }} />);
    expect(container.firstChild).toHaveAttribute(testProp, value);
  });

  it('should not have basic accessibility issues', async () => {
    const { container } = render(
      <PhoneInput
        {...defaultProps}
        value={{
          countryCode: mockedCountries[0].countryCode,
          dialingCode: mockedCountries[0].dialingCode,
          phone: '',
        }}
      />
    );
    expect(await axe(container)).toHaveNoViolations();
  });

  it('should display phone number and dial code values', () => {
    const country = mockedCountries[2];
    const phoneValue = {
      countryCode: country.countryCode,
      dialingCode: country.dialingCode,
      phone: '123456789',
    };
    render(<PhoneInput {...defaultProps} value={phoneValue} />);
    expect(screen.getByText(phoneValue.dialingCode)).toBeVisible();
    expect(screen.getByRole('textbox')).toHaveValue(phoneValue.phone);
  });

  it('should open CountriesList on dial code section click', () => {
    render(<PhoneInput {...defaultProps} />);
    expect(screen.queryByText('CountriesList')).toBeNull();
    act(() => {
      userEvent.click(screen.getByRole('button', { name: /select country code/i }));
    });
    expect(screen.getByText('CountriesList')).toBeInTheDocument();
  });

  it('should call onChange on input value change', () => {
    const phoneNumber = '1';
    render(<PhoneInput {...defaultProps} />);
    userEvent.type(screen.getByRole('textbox'), phoneNumber);
    expect(defaultProps.onChange).toHaveBeenCalledWith({
      countryCode: '',
      dialingCode: '',
      phone: phoneNumber,
    });
  });

  it('should fallback to default aria-label when no label or placeholder is provided', () => {
    render(
      <PhoneInput {...defaultProps} label={undefined} placeholder={undefined} isAltStyle={true} />
    );

    expect(screen.getByLabelText('Phone number')).toBeInTheDocument();
  });

  it('should render label when isAltStyle is false and label is provided', () => {
    render(<PhoneInput {...defaultProps} label="Phone Number" disabled={false} />);

    const label = screen.getByTestId('PhoneInput-phone-label');
    expect(label).toBeInTheDocument();
    expect(label).toHaveTextContent('Phone Number');
  });

  describe('Country Selector Keyboard Accessibility', () => {
    it('should render country selector as keyboard focusable button', () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      expect(countrySelector).toBeInTheDocument();
    });

    it('should receive keyboard focus when tabbing through form', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      await userEvent.tab();

      expect(countrySelector).toHaveFocus();
    });

    it('should open dropdown when Enter key is pressed', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      countrySelector.focus();
      expect(screen.queryByText('CountriesList')).toBeNull();

      await userEvent.keyboard('{Enter}');

      expect(screen.getByText('CountriesList')).toBeInTheDocument();
    });

    it('should open dropdown when Space key is pressed without scrolling page', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      countrySelector.focus();
      expect(screen.queryByText('CountriesList')).toBeNull();

      // Space key should open dropdown without scrolling (preventDefault called)
      await userEvent.keyboard(' ');

      expect(screen.getByText('CountriesList')).toBeInTheDocument();
    });

    it('should close dropdown on Escape key when open', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      countrySelector.focus();

      await userEvent.keyboard('{Enter}');
      expect(screen.getByText('CountriesList')).toBeInTheDocument();

      await userEvent.keyboard('{Escape}');
      expect(screen.queryByText('CountriesList')).toBeNull();
    });

    it('should have accessible label with current country', () => {
      const country = mockedCountries[0];
      const phoneValue = {
        countryCode: country.countryCode,
        dialingCode: country.dialingCode,
        phone: '',
      };
      render(<PhoneInput {...defaultProps} value={phoneValue} />);
      const escapedDialingCode = country.dialingCode.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
      const countrySelector = screen.getByRole('button', {
        name: new RegExp(`change country code.*${country.countryName}.*${escapedDialingCode}`, 'i'),
      });

      expect(countrySelector).toHaveAttribute(
        'aria-label',
        `Change country code. Currently ${country.countryName} ${country.dialingCode}`
      );
    });

    it('should have default aria-label when no country selected', () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      expect(countrySelector).toHaveAttribute('aria-label', 'Select country code');
    });

    it('should not be keyboard focusable when disabled', () => {
      render(<PhoneInput {...defaultProps} disabled={true} />);
      const countrySelector = screen.getByTestId('PhoneInput-countrySelector');

      expect(countrySelector).toHaveAttribute('tabIndex', '-1');
    });

    it('should not activate on keyboard when disabled', async () => {
      render(<PhoneInput {...defaultProps} disabled={true} />);
      const countrySelector = screen.getByTestId('PhoneInput-countrySelector');

      countrySelector.focus();
      expect(screen.queryByText('CountriesList')).toBeNull();

      await userEvent.keyboard('{Enter}');

      expect(screen.queryByText('CountriesList')).toBeNull();
    });

    it('should update aria-expanded when dropdown opens and closes', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });

      expect(countrySelector).toHaveAttribute('aria-expanded', 'false');

      await userEvent.click(countrySelector);

      expect(countrySelector).toHaveAttribute('aria-expanded', 'true');
      expect(screen.getByText('CountriesList')).toBeInTheDocument();
    });
  });

  describe('Phone Input Focus Restoration', () => {
    beforeEach(() => {
      jest.useFakeTimers();
    });

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should refocus phone input when dropdown closes', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });
      const phoneInput = screen.getByRole('textbox');

      await userEvent.click(countrySelector);
      expect(screen.getByText('CountriesList')).toBeInTheDocument();

      await userEvent.click(countrySelector);
      expect(screen.queryByText('CountriesList')).toBeNull();

      act(() => {
        jest.advanceTimersByTime(0);
      });

      expect(phoneInput).toHaveFocus();
    });

    it('should NOT focus phone input when dropdown opens', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });
      const phoneInput = screen.getByRole('textbox');

      expect(phoneInput).not.toHaveFocus();

      await userEvent.click(countrySelector);
      expect(screen.getByText('CountriesList')).toBeInTheDocument();

      act(() => {
        jest.advanceTimersByTime(0);
      });

      expect(phoneInput).not.toHaveFocus();
    });

    it('should NOT focus phone input on initial render', () => {
      render(<PhoneInput {...defaultProps} />);
      const phoneInput = screen.getByRole('textbox');

      act(() => {
        jest.advanceTimersByTime(0);
      });

      expect(phoneInput).not.toHaveFocus();
    });

    it('should handle focus asynchronously via setTimeout', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });
      const phoneInput = screen.getByRole('textbox');

      await userEvent.click(countrySelector);
      await userEvent.click(countrySelector);

      expect(phoneInput).not.toHaveFocus();

      act(() => {
        jest.advanceTimersByTime(0);
      });

      expect(phoneInput).toHaveFocus();
    });

    it('should refocus phone input when dropdown closed via Escape key', async () => {
      render(<PhoneInput {...defaultProps} />);
      const countrySelector = screen.getByRole('button', { name: /select country code/i });
      const phoneInput = screen.getByRole('textbox');

      countrySelector.focus();
      await userEvent.keyboard('{Enter}');
      expect(screen.getByText('CountriesList')).toBeInTheDocument();

      await userEvent.keyboard('{Escape}');
      expect(screen.queryByText('CountriesList')).toBeNull();

      act(() => {
        jest.advanceTimersByTime(0);
      });

      expect(phoneInput).toHaveFocus();
    });
  });
});

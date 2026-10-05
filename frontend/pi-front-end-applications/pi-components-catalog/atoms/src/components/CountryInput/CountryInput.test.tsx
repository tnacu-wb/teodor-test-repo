import { act, render, screen } from '@testing-library/react';

import { userEvent } from '../../utils/test-utils';
import CountryInput from './CountryInput.component';

const mockCountries = [
  {
    countryCode: 'GB',
    countryName: 'United Kingdom',
    flagSrc: '/flags/gb.png',
    countryCodeLegacy: '',
    dialingCode: '',
    passportRequired: false,
  },
  {
    countryCode: 'AT',
    countryName: 'Austria',
    flagSrc: '/flags/at.png',
    countryCodeLegacy: '',
    dialingCode: '',
    passportRequired: false,
  },
];

describe('CountryInput', () => {
  it('renders with default country', () => {
    render(
      <CountryInput
        countries={mockCountries}
        value={{ countryCode: 'GB' }}
        onChange={jest.fn()}
        label="Country"
        name="country"
        formatAssetsUrl={(url: string) => url}
        dataTestId="country-input"
      />
    );
    expect(screen.getByDisplayValue('United Kingdom')).toBeInTheDocument();
    expect(screen.getByTestId('country-input-countryName')).toHaveValue('United Kingdom');
  });

  it('renders with Austria as selected country', () => {
    render(
      <CountryInput
        countries={mockCountries}
        value={{ countryCode: 'AT' }}
        onChange={jest.fn()}
        label="Country"
        name="country"
        formatAssetsUrl={(url: string) => url}
        dataTestId="country-input"
      />
    );
    expect(screen.getByDisplayValue('Austria')).toBeInTheDocument();
    expect(screen.getByTestId('country-input-countryName')).toHaveValue('Austria');
  });

  it('shows correct chevron icons when opening and closing dropdown', async () => {
    render(
      <CountryInput
        countries={mockCountries}
        value={{ countryCode: 'GB' }}
        onChange={jest.fn()}
        label="Country"
        name="country"
        formatAssetsUrl={(url: string) => url}
        dataTestId="country-input"
      />
    );
    // Dropdown closed
    expect(screen.getByLabelText('chevron-icon-down')).toBeInTheDocument();

    // Open dropdown
    userEvent.click(screen.getByRole('button'));
    expect(await screen.findByLabelText('chevron-icon-up')).toBeInTheDocument();

    // Close dropdown by clicking again
    userEvent.click(screen.getByRole('button'));
    expect(await screen.findByLabelText('chevron-icon-down')).toBeInTheDocument();
  });

  it('calls onChange when a country is selected', async () => {
    const handleChange = jest.fn();
    render(
      <CountryInput
        countries={mockCountries}
        value={{ countryCode: 'GB' }}
        onChange={handleChange}
        label="Country"
        name="country"
        formatAssetsUrl={(url: string) => url}
        dataTestId="country-input"
      />
    );
    // Open dropdown and select Austria
    act(() => {
      userEvent.click(screen.getByRole('button'));
    });

    userEvent.click(screen.getByText('Austria'));
    expect(handleChange).toHaveBeenCalledWith(expect.objectContaining({ countryCode: 'AT' }));
  });
});

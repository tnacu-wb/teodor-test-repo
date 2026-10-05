import { type Country } from '@whitbread-eos/api';
import { useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';

import { render, userEvent } from '../../utils/test-utils';
import CountrySelector from './CountrySelector';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: jest.fn(),
  useQueryRequest: jest.fn(),
  useSemanticTypography: jest.fn(() => (legacyTypography) => legacyTypography),
  formatAssetsUrl: jest.fn((url) => url),
  getSortedCountriesByCurrentLang: jest.fn((countries) => countries),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
}));

const mockUseCustomLocale = useCustomLocale as jest.Mock;
const mockUseQueryRequest = useQueryRequest as jest.Mock;

const mockOnChange = jest.fn();

const defaultProps = {
  onChange: mockOnChange,
  hasError: false,
  selectedId: '',
  showIcon: true,
  styles: {},
  isDisabled: false,
};

const mockCountries: Country[] = [
  {
    countryCode: 'GB',
    countryCodeLegacy: 'G ',
    countryName: 'United Kingdom (the)',
    passportRequired: false,
    dialingCode: '44',
    flagSrc: 'gb-flag.png',
  },
  {
    countryCode: 'DE',
    countryCodeLegacy: 'D',
    countryName: 'Germany',
    passportRequired: false,
    dialingCode: '49',
    flagSrc: 'de-flag.png',
  },
];

describe('CountrySelector', () => {
  beforeEach(() => {
    mockUseCustomLocale.mockReturnValue({ language: 'en', country: 'GB' });
    mockUseQueryRequest.mockReturnValue({
      data: { countries: { countries: mockCountries } },
      isSuccess: true,
    });
  });

  it('calls onChange correctly for default country', async () => {
    const { getByText } = render(<CountrySelector {...defaultProps} />);
    const dropdown = getByText('booking.country');
    await userEvent.click(dropdown);
    expect(getByText('United Kingdom (the)')).toBeInTheDocument();
  });

  it('calls onChange correctly when a country is selected', async () => {
    const { getByText } = render(<CountrySelector {...defaultProps} selectedId="DE" />);
    const dropdown = getByText('booking.country');
    await userEvent.click(dropdown);
    expect(getByText('Germany')).toBeInTheDocument();
  });
});

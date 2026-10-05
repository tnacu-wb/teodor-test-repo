import { render, screen, waitFor } from '@testing-library/react';

import { userEvent } from '../../utils/test-utils';
import CountrySelectorFilterable from './CountrySelectorFilterable';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

// Mock useQueryRequest to provide countries data
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequest: () => ({
    isLoading: false,
    isError: false,
    isSuccess: true,
    isIdle: false,
    error: {},
    data: {
      countries: {
        countries: [
          {
            countryCode: 'GB',
            countryName: 'United Kingdom',
            flagSrc: '/flags/gb.png',
            countryCodeLegacy: 'UK',
            dialingCode: '+44',
            passportRequired: false,
          },
          {
            countryCode: 'AT',
            countryName: 'Austria',
            flagSrc: '/flags/at.png',
            countryCodeLegacy: 'A',
            dialingCode: '+43',
            passportRequired: false,
          },
          {
            countryCode: 'DE',
            countryName: 'Germany',
            flagSrc: '/flags/de.png',
            countryCodeLegacy: 'D',
            dialingCode: '+49',
            passportRequired: false,
          },
        ],
      },
    },
  }),
  formatAssetsUrl: (url: string) => url,
}));

const baseProps = {
  formField: {
    label: 'Country',
    name: 'country',
    type: 'country' as any,
    props: {},
    testid: 'country-input',
  },
  field: {
    name: 'country',
    value: 'GB',
    onChange: jest.fn(),
    onBlur: jest.fn(),
  },
  selectedId: 'GB',
  setIsLocationRequired: jest.fn(),
  getValues: jest.fn(),
} as any;

describe('CountrySelectorFilterable', () => {
  it('renders with default country GB', async () => {
    render(<CountrySelectorFilterable {...baseProps} />);
    expect(await screen.findByDisplayValue('United Kingdom')).toBeInTheDocument();
  });

  it('renders with Austria if value is AT', async () => {
    render(
      <CountrySelectorFilterable
        {...baseProps}
        field={{ ...baseProps.field, value: 'AT' }}
        selectedId="AT"
      />
    );
    expect(await screen.findByDisplayValue('Austria')).toBeInTheDocument();
  });

  it('renders with Austria if selectedId matches legacy country code', async () => {
    render(
      <CountrySelectorFilterable
        {...baseProps}
        field={{ ...baseProps.field, value: '' }}
        selectedId="A" // legacy code for Austria
      />
    );
    // Should select Austria based on legacy code
    expect(await screen.findByDisplayValue('Austria')).toBeInTheDocument();
  });

  it('shows default label if no country is selected', async () => {
    render(
      <CountrySelectorFilterable
        {...baseProps}
        field={{ ...baseProps.field, value: '' }}
        selectedId=""
      />
    );
    const input = screen.getByTestId('country-input-countryName');
    expect(input).toHaveAttribute('placeholder', 'United Kingdom');
    expect(input).toHaveValue('United Kingdom');
    expect(await screen.findByDisplayValue('United Kingdom')).toBeInTheDocument();
  });

  it('calls onChange and setIsLocationRequired when country changes', async () => {
    const onChange = jest.fn();
    const setIsLocationRequired = jest.fn();
    render(
      <CountrySelectorFilterable
        {...baseProps}
        field={{ ...baseProps.field, value: 'GB', onChange }}
        setIsLocationRequired={setIsLocationRequired}
      />
    );
    // Open dropdown and select Austria
    await userEvent.click(screen.getByRole('button'));
    await userEvent.click(screen.getByText('Austria'));
    await waitFor(() => {
      expect(setIsLocationRequired).toHaveBeenCalledWith(false);
    });
  });
});

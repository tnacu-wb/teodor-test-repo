import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';

import { AddressDropdown } from '~components/innBusiness/forms/CompanyAddressForm/AddressDropdown';

const mockProps = {
  onAddressChange: () => {
    return;
  },
  dropdownOptions: [
    {
      value: 'Flat 1, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY',
      displayValue: 'Flat 1, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY',
    },
    {
      value: 'Flat 3, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY',
      displayValue: 'Flat 3, Belgravia Court, 33 Ebury Street, LONDON SW1W 0NY',
    },
  ],
  postCode: 'test postcode',
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCountriesList: () => {
      return;
    },
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    getFormattedAddress: () => {
      return;
    },
  };
});

describe('AddressDropdown Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render AddressDropdown component ', async () => {
    const { getByTestId } = render(<AddressDropdown {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm-AddressDropdown')).toBeInTheDocument();
    });
  });
});

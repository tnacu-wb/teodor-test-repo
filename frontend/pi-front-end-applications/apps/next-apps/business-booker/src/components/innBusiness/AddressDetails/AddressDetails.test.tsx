import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { Language } from '@whitbread-eos/api';

import { AddressDetails } from '~components/innBusiness/AddressDetails/AddressDetails';

const mockProps = {
  addressInfo: {
    addressLine1: '123 Hover street',
    addressLine2: '',
    addressLine3: '',
    addressLine4: 'London',
    addressLine5: '',
    country: 'GB',
    postCode: 'test postcode',
  },
  companyName: '',
  isCompanyInformation: false,
  language: 'en' as Language,
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    getCountryName: serverUtils.getCountryName,
    cn: jest.fn(),
    getCountriesList: () => {
      return [{ countryCode: 'GB', countryName: 'United Kingdom (the)' }];
    },
  };
});

describe('AddressDetails Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render AddressDetails component', () => {
    const { getByTestId } = render(<AddressDetails {...mockProps} />);

    expect(getByTestId('AddressDetails')).toBeInTheDocument();
  });

  it('should render AddressDetails component', async () => {
    const { getByTestId } = render(<AddressDetails {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('AddressDetails-countryName')).toBeInTheDocument();
      expect(getByTestId('AddressDetails-countryName')).toHaveTextContent('United Kingdom (the)');
    });
  });

  it('should render AddressDetails component with companyName', () => {
    mockProps.isCompanyInformation = true;
    mockProps.companyName = 'My company';
    const { getByTestId } = render(<AddressDetails {...mockProps} />);

    expect(getByTestId('AddressDetails')).toBeInTheDocument();
  });
});

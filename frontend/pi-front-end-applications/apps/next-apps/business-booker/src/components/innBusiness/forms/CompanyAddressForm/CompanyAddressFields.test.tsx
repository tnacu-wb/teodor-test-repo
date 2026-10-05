import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import { CompanyAddressFields } from './CompanyAddressFields';

const mockProps = {
  addressData: {
    addressLine1: '123 Hover street',
    addressLine2: '',
    addressLine3: '',
    addressLine4: 'London',
    addressLine5: '',
    country: 'GB',
    postCode: 'test postcode',
  },
  icons: { icon: 'test1' },
  language: 'en',
  onSubmit: (data: any) => {
    return data;
  },
  onOpen: () => {
    return;
  },
  variant: 'companyAddress',
};

let mockedPostCodeAddresses: { id: string; addressText: string }[] | null = null;

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

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
    getFormattedAddress: () => {
      return;
    },
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    getPostCodeAddresses: () => {
      return mockedPostCodeAddresses;
    },
    getVariant: () => {
      return 'variantName.fieldName';
    },
    findError: serverUtils.findError,
    addressSchema: jest.fn(),
  };
});

jest.mock('react-hook-form', () => ({
  useFormContext: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    watch: jest.fn(),
    setValue: jest.fn(),
  }),
  useForm: () => ({
    control: {},
    formState: { errors: {} },
    trigger: jest.fn(),
    clearErrors: jest.fn(),
    watch: jest.fn(),
    setValue: jest.fn(),
  }),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('CompanyAddressFields Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyAddressFields component and click on manual address', async () => {
    mockedPostCodeAddresses = [{ id: 'adasdzxc', addressText: 'Street Test' }];
    const { getByTestId } = render(<CompanyAddressFields {...(mockProps as any)} />);

    const companyAddressForm = getByTestId('CompanyAddressForm-findAddress');
    const manualAddressButton = getByTestId('IB-ManualAddress-Button');
    await waitFor(async () => {
      expect(companyAddressForm).toBeInTheDocument();
      expect(manualAddressButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(manualAddressButton);
    });
  });
  it('should render CompanyAddressFields component click on input, type something and then click on findAddress', async () => {
    mockedPostCodeAddresses = [{ id: 'adasdzxc', addressText: 'Street Test' }];

    const { getByTestId } = render(<CompanyAddressFields {...(mockProps as any)} />);

    const companyAddressForm = getByTestId('CompanyAddressForm-findAddress');
    const companyAddressFindAddressButton = getByTestId('CompanyAddressForm-findAddressButton');
    const manualAddressButton = getByTestId('IB-ManualAddress-Button');
    const postCodeFormInput = getByTestId('postCode-Form-Input');

    await waitFor(async () => {
      expect(companyAddressForm).toBeInTheDocument();
      expect(manualAddressButton).toBeInTheDocument();
      expect(companyAddressFindAddressButton).toBeInTheDocument();
    });

    await waitFor(async () => {
      expect(postCodeFormInput).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.change(postCodeFormInput, { target: { value: 'bl0 ole' } });
    });

    fireEvent.click(companyAddressFindAddressButton);
  });
});

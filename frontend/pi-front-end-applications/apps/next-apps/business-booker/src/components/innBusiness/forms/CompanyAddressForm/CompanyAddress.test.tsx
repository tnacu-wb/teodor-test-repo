import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { addressSchema } from '@whitbread-eos/utils/server';
import { act } from 'react-dom/test-utils';

import { CompanyAddress } from './CompanyAddress';

const mockOnAddressChange = jest.fn();
const mockParseAsync = jest.fn().mockResolvedValue({});
const mockSuperRefine = jest.fn(function () {
  return {
    parseAsync: mockParseAsync,
    superRefine: mockSuperRefine,
  };
});

const createMockSchema = () => ({
  parseAsync: mockParseAsync,
  superRefine: mockSuperRefine,
});
const mockProps: any = {
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
  onAddressChange: mockOnAddressChange,
  postalCode: 'test postcode',
  onOpen: () => {
    return;
  },
  isCompanyDetailsContainer: false,
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
    getVariant: (fieldName: string, variant?: string) => {
      return variant ? `${variant}.${fieldName}` : fieldName;
    },
    findError: serverUtils.findError,

    addressSchema: jest.fn(() => createMockSchema()),
  };
});

const mockedAddressSchema = addressSchema as jest.Mock;

describe('CompanyAddress Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockParseAsync.mockResolvedValue({});
  });

  it('should apply profile postcode validation only when isProfilePage is true', async () => {
    const { getByTestId } = render(
      <CompanyAddress {...(mockProps as any)} isCompanyDetailsContainer isProfilePage />
    );

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm-company-details-container')).toBeInTheDocument();
    });

    expect(mockedAddressSchema).toHaveBeenCalled();
    expect(mockSuperRefine).toHaveBeenCalled();
  });

  it('should not apply profile postcode validation when isProfilePage is false', async () => {
    const { getByTestId } = render(
      <CompanyAddress {...(mockProps as any)} isCompanyDetailsContainer isProfilePage={false} />
    );

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm-company-details-container')).toBeInTheDocument();
    });

    expect(mockedAddressSchema).toHaveBeenCalled();
    expect(mockSuperRefine).not.toHaveBeenCalled();
  });

  it('should render CompanyAddress component and click on search for new addresses', async () => {
    const { getByTestId } = render(<CompanyAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm')).toBeInTheDocument();
      expect(getByTestId('CompanyAddressForm-search-for-new-address')).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(getByTestId('CompanyAddressForm-search-for-new-address'));
    });
  });

  it('should trigger onAddressChnage handler', async () => {
    const { getByTestId } = render(<CompanyAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('CompanyAddressForm-search-for-new-address'));
    });

    await waitFor(async () => {
      expect(getByTestId('postCode-Form-Input')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.change(getByTestId('postCode-Form-Input'), { target: { value: 'bl0 ole' } });
      fireEvent.click(getByTestId('CompanyAddressForm-findAddressButton'));
    });

    await waitFor(async () => {
      expect(mockOnAddressChange).toHaveBeenCalled();
    });
  });

  it('should render CompanyAddress component with postcodes', async () => {
    mockedPostCodeAddresses = [{ id: 'adasdzxc', addressText: 'Street Test' }];

    const { getByTestId } = render(<CompanyAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm')).toBeInTheDocument();
      expect(getByTestId('CompanyAddressForm-search-for-new-address')).toBeInTheDocument();
    });

    await waitFor(async () => {
      fireEvent.click(getByTestId('CompanyAddressForm-search-for-new-address'));
    });
  });

  it('should render CompanyAddress component and click on find postcode', async () => {
    mockedPostCodeAddresses = [{ id: 'adasdzxc', addressText: 'Street Test' }];

    const { getByTestId } = render(<CompanyAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('CompanyAddressForm-search-for-new-address'));
    });

    await waitFor(async () => {
      expect(getByTestId('postCode-Form-Input')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.change(getByTestId('postCode-Form-Input'), { target: { value: 'bl0 ole' } });
      fireEvent.click(getByTestId('CompanyAddressForm-findAddressButton'));
    });

    await waitFor(async () => {
      expect(getByTestId('selectAddress-IB-Form-Select-Button')).toBeInTheDocument();
    });
  });

  it('should render CompanyAddress component for company Details Container, isCompanyDetailsContainer = true ', async () => {
    mockProps.isCompanyDetailsContainer = true;
    const { getByTestId } = render(<CompanyAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm-company-details-container')).toBeInTheDocument();
    });
  });

  it('should render default if no address data is given', async () => {
    mockProps.addressData = null;

    render(<CompanyAddress {...(mockProps as any)} />);
  });

  it('should call onDirtyChange when postcode changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    const { getByTestId } = render(<CompanyAddress {...(propsWithMock as any)} />);

    await waitFor(async () => {
      expect(getByTestId('IB-ManualAddress-Button')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('IB-ManualAddress-Button'));
    });

    expect(getByTestId('Manual-Postcode-Form-Input')).toBeInTheDocument();

    await act(async () => {
      fireEvent.change(getByTestId('Manual-Postcode-Form-Input'), { target: { value: 'lll ddd' } });
    });

    await waitFor(() => {
      expect(mockOnDirtyChange).toHaveBeenCalledWith(true);
    });
  });

  it('should call onDirtyChange when address line 1 changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    const { getByTestId } = render(<CompanyAddress {...(propsWithMock as any)} />);

    await waitFor(async () => {
      expect(getByTestId('IB-ManualAddress-Button')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('IB-ManualAddress-Button'));
    });

    expect(getByTestId('Address-Line-1-Form-Input')).toBeInTheDocument();

    await act(async () => {
      fireEvent.change(getByTestId('Address-Line-1-Form-Input'), {
        target: { value: 'Test Address Line 1' },
      });
    });

    await waitFor(() => {
      expect(mockOnDirtyChange).toHaveBeenCalledWith(true);
    });
  });

  it('should not call onDirtyChange when no input changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    const { getByTestId } = render(<CompanyAddress {...(propsWithMock as any)} />);

    await waitFor(async () => {
      expect(getByTestId('IB-ManualAddress-Button')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(mockOnDirtyChange).not.toHaveBeenCalled();
    });
  });

  describe('postcode format validation', () => {
    const { addressSchema: realAddressSchema } = jest.requireActual(
      '@whitbread-eos/utils/server'
    ) as any;

    const profileProps = {
      addressData: {
        addressLine1: '123 Test Street',
        addressLine2: '',
        addressLine3: '',
        addressLine4: 'London',
        addressLine5: '',
        country: 'GB',
        postCode: 'SW1A 1AA',
      },
      icons: { icon: 'test1' },
      language: 'en',
      onSubmit: jest.fn(),
      postalCode: 'SW1A 1AA',
      onOpen: jest.fn(),
      isCompanyDetailsContainer: true,
      isProfilePage: true,
    };

    beforeEach(() => {
      mockedAddressSchema.mockImplementation(realAddressSchema);
    });

    const openManualForm = async (getByTestId: (id: string) => HTMLElement) => {
      await waitFor(() => getByTestId('IB-ManualAddress-Button'));
      await act(async () => {
        fireEvent.click(getByTestId('IB-ManualAddress-Button'));
      });
    };

    const changePostcodeAndBlur = async (
      getByTestId: (id: string) => HTMLElement,
      value: string
    ) => {
      await waitFor(() => getByTestId('Manual-Postcode-Form-Input'));
      const postcodeInput = getByTestId('Manual-Postcode-Form-Input');
      await act(async () => {
        fireEvent.change(postcodeInput, { target: { value } });
        fireEvent.blur(postcodeInput);
      });
    };

    it('should show error for invalid GB postcode when isProfilePage is true', async () => {
      const { getByTestId, findByText } = render(<CompanyAddress {...(profileProps as any)} />);

      await openManualForm(getByTestId);
      await changePostcodeAndBlur(getByTestId, 'SW15 5NN InvalidText');

      await findByText('users.userMgmt.employee.add.companyAddress.error.invalidPostcode');
    });

    it('should not show error for valid GB postcode when isProfilePage is true', async () => {
      const { getByTestId, queryByText } = render(<CompanyAddress {...(profileProps as any)} />);

      await openManualForm(getByTestId);
      await changePostcodeAndBlur(getByTestId, 'SW1A 1AA');

      await waitFor(() => {
        expect(
          queryByText('users.userMgmt.employee.add.companyAddress.error.invalidPostcode')
        ).not.toBeInTheDocument();
      });
    });

    // For DE country ManualAddress auto-opens via isDEForm useEffect — no button click needed
    it('should show error for invalid DE postcode when isProfilePage is true', async () => {
      const deProps = {
        ...profileProps,
        addressData: { ...profileProps.addressData, country: 'DE' },
        postalCode: '10115',
      };

      const { getByTestId, findByText } = render(<CompanyAddress {...(deProps as any)} />);

      await changePostcodeAndBlur(getByTestId, 'INVALID');

      await findByText('users.userMgmt.employee.add.companyAddress.error.invalidPostcode');
    });

    it('should not show error for valid DE postcode when isProfilePage is true', async () => {
      const deProps = {
        ...profileProps,
        addressData: { ...profileProps.addressData, country: 'DE' },
        postalCode: '10115',
      };

      const { getByTestId, queryByText } = render(<CompanyAddress {...(deProps as any)} />);

      await changePostcodeAndBlur(getByTestId, '10115');

      await waitFor(() => {
        expect(
          queryByText('users.userMgmt.employee.add.companyAddress.error.invalidPostcode')
        ).not.toBeInTheDocument();
      });
    });

    it('should not show error for invalid postcode when isProfilePage is false', async () => {
      const nonProfileProps = { ...profileProps, isProfilePage: false };

      const { getByTestId, queryByText } = render(<CompanyAddress {...(nonProfileProps as any)} />);

      await openManualForm(getByTestId);
      await changePostcodeAndBlur(getByTestId, 'SW15 5NN InvalidText');

      await waitFor(() => {
        expect(
          queryByText('users.userMgmt.employee.add.companyAddress.error.invalidPostcode')
        ).not.toBeInTheDocument();
      });
    });
  });
});

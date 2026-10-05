import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import { FindAddress } from '~components/innBusiness/forms/CompanyAddressForm/FindAddress';

const mockProps = {
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
  companyName: 'My company',
};

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
    getCountryName: () => {
      return 'United Kingdom (the)';
    },
    findError: serverUtils.findError,
  };
});

describe('FindAddress Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render FindAddress component ', async () => {
    const { getByTestId } = render(<FindAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm-findAddress')).toBeInTheDocument();
    });
  });

  it('should render FindAddress without options', async () => {
    mockProps.dropdownOptions = [];
    const { getByTestId } = render(<FindAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyAddressForm-findAddress')).toBeInTheDocument();
    });
  });

  it('should call onDirtyChange when postcode changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    const { getByPlaceholderText } = render(<FindAddress {...(propsWithMock as any)} />);

    const postCodeInput = getByPlaceholderText(
      'userMgmt.employee.add.companyAddress.postcode'
    ) as HTMLInputElement;

    expect(postCodeInput).toBeInTheDocument();

    await act(async () => {
      fireEvent.change(postCodeInput, {
        target: { value: 'SW1A 1AA' },
      });
    });

    await waitFor(() => {
      expect(mockOnDirtyChange).toHaveBeenCalledWith(true);
    });
  });
});

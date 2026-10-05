import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';

import { TypeOfAddress } from '~components/innBusiness/forms/CompanyAddressForm/TypeOfAddress';

const mockProps = {
  companyName: 'My company',
  onAddressTypeChange: jest.fn(),
  onSubmit: jest.fn(),
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
  };
});
global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('TypeOfAddress Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render TypeOfAddress component ', async () => {
    const { getByTestId } = render(<TypeOfAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('AddressType-form')).toBeInTheDocument();
    });
  });
  it('should render TypeOfAddress component  without company name', async () => {
    mockProps.companyName = '';
    const { getByTestId } = render(<TypeOfAddress {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('AddressType-form')).toBeInTheDocument();
    });
  });
});

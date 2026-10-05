import '@testing-library/jest-dom';
import { render, waitFor, fireEvent, act } from '@testing-library/react';
import { PropsWithChildren } from 'react';
import { FormProvider, useForm } from 'react-hook-form';

import { ManualAddress } from '~components/innBusiness/forms/CompanyAddressForm/ManualAddress';
import { userEvent } from '~utils/test-utils';

const mockProps = {
  icons: { 'icon.notification.error': '/', 'icon.chevron.down': '/' },
  language: 'en',
  onOpen: () => {
    return;
  },
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
    getVariant: () => {
      return 'variantName.fieldName';
    },
    findError: serverUtils.findError,
  };
});

const FormProviderWrapper = ({ children }: PropsWithChildren<unknown>) => {
  const methods = useForm({
    defaultValues: {
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressLine5: '',
      country: '',
      postCode: '',
    },
  });

  return <FormProvider {...methods}>{children}</FormProvider>;
};

describe('ManualAddress Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ManualAddress component ', async () => {
    const { getByTestId } = render(
      <FormProviderWrapper>
        <ManualAddress {...(mockProps as any)} />
      </FormProviderWrapper>
    );

    await waitFor(async () => {
      expect(getByTestId('IB-ManualAddress-Button')).toBeInTheDocument();
    });
  });

  it('should render ManualAddress component and click manual button', async () => {
    const { getByTestId } = render(
      <FormProviderWrapper>
        <ManualAddress {...(mockProps as any)} />
      </FormProviderWrapper>
    );

    await waitFor(async () => {
      expect(getByTestId('IB-ManualAddress-Button')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('IB-ManualAddress-Button'));
    });
  });

  it('should render ManualAddress component and click manual button DE', async () => {
    mockProps.language = 'de';
    const { getByTestId } = render(
      <FormProviderWrapper>
        <ManualAddress {...(mockProps as any)} />
      </FormProviderWrapper>
    );

    await waitFor(async () => {
      expect(getByTestId('IB-ManualAddress-Button')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('IB-ManualAddress-Button'));
    });

    await waitFor(async () => {
      expect(getByTestId('Address-Line-1-Form-Input')).toBeInTheDocument();
    });

    await act(async () => {
      const inputLine1 = getByTestId('Address-Line-1-Form-Input');
      inputLine1.focus();
      fireEvent.change(inputLine1, { target: { value: '' } });
      await userEvent.tab();

      const inputPostcode = getByTestId('Manual-Postcode-Form-Input');
      inputPostcode.focus();
      fireEvent.change(inputPostcode, { target: { value: '' } });
      await userEvent.tab();
    });
  });
});

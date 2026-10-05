import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';
import { useFormContext } from 'react-hook-form';

import { userEvent } from '~utils/test-utils';

import { PartnerDetailsForm } from './partner-details-form';

const mockProps = {
  icons: {},
  titleValues: JSON.stringify(['Mr', 'Mrs', 'Miss']),
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    findError: serverUtils.findError,
  };
});

jest.mock('react-hook-form', () => ({
  useFormContext: jest.fn(),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('PartnerDetailsForm Component', () => {
  const mockTrigger = jest.fn();
  const mockClearErrors = jest.fn();
  beforeEach(() => {
    (useFormContext as jest.Mock).mockReturnValue({
      control: {},
      formState: { errors: {} },
      trigger: mockTrigger,
      clearErrors: mockClearErrors,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render PartnerDetailsForm component', async () => {
    const { getByTestId } = render(<PartnerDetailsForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId(`PartnerDetailsForm-container`)).toBeInTheDocument();
      expect(getByTestId('numberOfPartners-Form-Input')).toBeInTheDocument();
      expect(getByTestId('foreName-Form-Input')).toBeInTheDocument();
      expect(getByTestId('lastName-Form-Input')).toBeInTheDocument();
    });
    await act(async () => {
      const input = getByTestId('foreName-Form-Input');
      input.focus();
      await userEvent.tab();
    });
    await act(async () => {
      const input = getByTestId('lastName-Form-Input');
      input.focus();
      await userEvent.tab();
    });
  });
});

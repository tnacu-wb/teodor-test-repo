import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';
import { useFormContext } from 'react-hook-form';

import { userEvent } from '~utils/test-utils';

import { CompanyRegistrationNumber } from './company-registration-number';

const mockProps = {
  onLookupSuccess: jest.fn(),
  scheme: 'GB',
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
    findError: serverUtils.findError,
  };
});

jest.mock('react-hook-form', () => ({
  useFormContext: jest.fn(),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('CompanyRegistrationNumber Component', () => {
  const mockTrigger = jest.fn();
  const mockClearErrors = jest.fn();
  beforeEach(() => {
    (useFormContext as jest.Mock).mockReturnValue({
      control: {},
      formState: { errors: {} },
      trigger: mockTrigger,
      clearErrors: mockClearErrors,
      watch: jest.fn(),
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyRegistrationNumber component ', async () => {
    const { getByTestId } = render(<CompanyRegistrationNumber {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('CompanyRegistrationNumber-Form-Input')).toBeInTheDocument();
    });

    await act(async () => {
      const input = getByTestId('CompanyRegistrationNumber-Form-Input');
      input.focus();
      await userEvent.tab();
    });
  });
});

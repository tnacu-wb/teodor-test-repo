import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';
import { useFormContext } from 'react-hook-form';

import { userEvent } from '~utils/test-utils';

import { CompanyNameOfEmployeeForm } from './company-name-of-employee';

const mockProps = {
  companyName: 'My company',
  onSubmit: jest.fn(),
  titleValues: JSON.stringify(['Mr', 'Mrs', 'Miss']),
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

describe('CompanyNameOfEmployeeForm Component', () => {
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

  it('should render CompanyNameOfEmployeeForm component ', async () => {
    const { getByTestId } = render(<CompanyNameOfEmployeeForm {...(mockProps as any)} />);

    const title = getByTestId('titleEmployee-IB-Form-Select');
    const firstName = getByTestId('firstNameEmployee-Form-Input');
    const lastName = getByTestId('lastNameEmployee-Form-Input');

    await waitFor(async () => {
      expect(getByTestId('CompanyNameOfEmployeeForm-form')).toBeInTheDocument();
      expect(title).toBeInTheDocument();
      expect(firstName).toBeInTheDocument();
      expect(lastName).toBeInTheDocument();
    });

    await act(async () => {
      title.click();
    });

    await act(async () => {
      firstName.focus();
      await userEvent.tab();
    });
    await act(async () => {
      lastName.focus();
      await userEvent.tab();
    });
  });
});

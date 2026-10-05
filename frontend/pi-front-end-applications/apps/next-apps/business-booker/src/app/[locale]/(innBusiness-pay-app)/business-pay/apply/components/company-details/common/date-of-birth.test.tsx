import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';
import { useFormContext } from 'react-hook-form';

import { userEvent } from '~utils/test-utils';

import { DateOfBirthForm } from './date-of-birth';

const mockProps = {
  companyName: 'My company',
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

jest.mock('react-hook-form', () => ({
  useFormContext: jest.fn(),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('DateOfBirthForm Component', () => {
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

  it('should render DateOfBirthForm component ', async () => {
    const { getByTestId } = render(<DateOfBirthForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('DateOfBirthForm-container')).toBeInTheDocument();
      expect(getByTestId('DateOfBirthForm-day-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('DateOfBirthForm-month-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('DateOfBirthForm-year-IB-Form-Select')).toBeInTheDocument();
    });

    await act(async () => {
      const input = getByTestId('DateOfBirthForm-day-IB-Form-Select');
      input.focus();
      await userEvent.tab();
    });
  });
});

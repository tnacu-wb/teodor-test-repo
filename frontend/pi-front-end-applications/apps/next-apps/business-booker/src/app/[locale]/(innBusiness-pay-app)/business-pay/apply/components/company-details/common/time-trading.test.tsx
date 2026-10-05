import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';
import { useFormContext } from 'react-hook-form';

import { userEvent } from '~utils/test-utils';

import { TimeTradingForm } from './time-trading';

const mockProps = {
  companyName: 'My company',
  onSubmit: jest.fn(),
  timeTrading: '0-6months, 7-12 months',
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

describe('TimeTradingForm Component', () => {
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

  it('should render TimeTradingForm component ', async () => {
    const { getByTestId } = render(<TimeTradingForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId('TimeTradingForm-container')).toBeInTheDocument();
      expect(getByTestId('TimeTradingForm-timeTradingId-IB-Form-Select')).toBeInTheDocument();
    });

    await act(async () => {
      const input = getByTestId('TimeTradingForm-timeTradingId-IB-Form-Select');
      input.focus();
      await userEvent.tab();
    });
  });
});

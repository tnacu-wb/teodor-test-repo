import '@testing-library/jest-dom';
import { render, waitFor, act } from '@testing-library/react';
import { useFormContext } from 'react-hook-form';

import { userEvent } from '~utils/test-utils';

import { RegisteredCharityNumberForm } from './registered-charity-number';

const mockProps = {
  icons: {},
  onSubmit: jest.fn(),
  registeredCharityNumber: '123456',
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

describe('RegisteredCharityNumberForm Component', () => {
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

  it('should render RegisteredCharityNumberForm component', async () => {
    const { getByTestId } = render(<RegisteredCharityNumberForm {...(mockProps as any)} />);

    await waitFor(async () => {
      expect(getByTestId(`RegisteredCharityNumberForm-container`)).toBeInTheDocument();
      expect(getByTestId('RegisteredCharityNumberForm-Form-Input')).toBeInTheDocument();
    });
    await act(async () => {
      const input = getByTestId('RegisteredCharityNumberForm-Form-Input');
      input.focus();
      await userEvent.tab();
    });
  });
});

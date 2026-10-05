import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';

import { userEvent } from '~utils/test-utils';

import { CompanyEmailForm } from './CompanyEmailForm';

const mockProps = {
  onSubmit: () => true,
  icons: {},
};

describe('CompanyEmailForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyEmailForm component', async () => {
    const { getByTestId } = render(<CompanyEmailForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('CompanyEmail-Form')).toBeInTheDocument();
      expect(getByTestId('CompanyEmail-Form-Input')).toBeInTheDocument();
    });

    await act(async () => {
      const input = getByTestId('CompanyEmail-Form-Input');
      input.focus();
      fireEvent.change(input, { target: { value: '' } });
      await userEvent.tab();
    });
  });
});

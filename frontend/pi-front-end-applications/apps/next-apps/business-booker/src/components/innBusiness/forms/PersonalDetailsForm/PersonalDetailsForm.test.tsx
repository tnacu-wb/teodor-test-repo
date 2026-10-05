import '@testing-library/jest-dom';
import { render, waitFor, act, fireEvent } from '@testing-library/react';
import { Language } from '@whitbread-eos/api';

import { userEvent } from '~utils/test-utils';

import { PersonalDetailsForm } from './PersonalDetailsForm';

const mockProps = {
  onSubmit: (data: any) => {
    return data;
  },
  language: 'en' as Language,
  icons: { icon: 'test' },
  userDetails: {
    title: 'MR',
    firstName: 'test',
    lastName: 'test',
    emailAddress: 'test@test.test',
    phoneNumber: '+44123456789',
    mobileNumber: '+44123456789',
  },
  formRef: { current: document.createElement('form') },
};

describe('PersonalDetailsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render PersonalDetailsForm component with user details/EDIT', async () => {
    const { getByTestId } = render(<PersonalDetailsForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Title-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('First-Name-Form-Input')).toBeInTheDocument();
      expect(getByTestId('Last-Name-Form-Input')).toBeInTheDocument();
      expect(getByTestId('Email-Form-Input')).toBeInTheDocument();
      expect(getByTestId('Phone-Number-IB-Form-Select')).toBeInTheDocument();
    });

    await act(async () => {
      const titleInput = getByTestId('Title-IB-Form-Select');
      titleInput.focus();
      await userEvent.tab();

      const firstNameInput = getByTestId('First-Name-Form-Input');
      firstNameInput.focus();
      fireEvent.change(firstNameInput, { target: { value: '' } });
      await userEvent.tab();

      const lastNameInput = getByTestId('Last-Name-Form-Input');
      lastNameInput.focus();
      fireEvent.change(lastNameInput, { target: { value: '' } });
      await userEvent.tab();

      const emailInput = getByTestId('Email-Form-Input');
      emailInput.focus();
      fireEvent.change(emailInput, { target: { value: '' } });
      await userEvent.tab();

      const phoneNumberInput = getByTestId('Phone-Number-Form-Input');
      phoneNumberInput.focus();
      fireEvent.change(phoneNumberInput, { target: { value: '' } });
      await userEvent.tab();

      const mobileNumberInput = getByTestId('Alternate-Phone-Number-Form-Input');
      mobileNumberInput.focus();
      fireEvent.change(mobileNumberInput, { target: { value: '' } });
      await userEvent.tab();
    });
  });

  it('should render PersonalDetailsForm component with no user details/ADD', async () => {
    const { getByTestId } = render(<PersonalDetailsForm {...mockProps} />);
    await waitFor(() => {
      expect(getByTestId('Title-IB-Form-Select')).toBeInTheDocument();
    });
  });

  it('should call onDirtyChange when phone number changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    const user = userEvent.setup();
    const { getByTestId } = render(<PersonalDetailsForm {...propsWithMock} />);

    const phoneNumberInput = getByTestId('Phone-Number-Form-Input');
    await user.type(phoneNumberInput, '1234567890');

    await waitFor(() => {
      expect(mockOnDirtyChange).toHaveBeenCalledWith(true);
    });
  });

  it('should call onDirtyChange when title changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    const user = userEvent.setup();
    const { getByTestId } = render(<PersonalDetailsForm {...propsWithMock} />);

    const titleInput = getByTestId('Title-IB-Form-Select');
    titleInput.focus();
    await user.tab();

    const nameInput = getByTestId('First-Name-Form-Input');
    await user.type(nameInput, 'John');

    await waitFor(() => {
      expect(mockOnDirtyChange).toHaveBeenCalledWith(true);
    });
  });

  it('should not call onDirtyChange when nothing changes', async () => {
    const mockOnDirtyChange = jest.fn();
    const propsWithMock = {
      ...mockProps,
      onDirtyChange: mockOnDirtyChange,
    };
    render(<PersonalDetailsForm {...propsWithMock} />);

    await waitFor(() => {
      expect(mockOnDirtyChange).not.toHaveBeenCalled();
    });
  });

  it('should show and clear server-side errors', async () => {
    const { rerender, queryByText } = render(
      <PersonalDetailsForm {...mockProps} serverErrors={{ firstName: 'Server error' }} />
    );

    await waitFor(() => {
      expect(queryByText('Server error')).toBeInTheDocument();
    });

    rerender(<PersonalDetailsForm {...mockProps} serverErrors={{}} />);

    await waitFor(() => {
      expect(queryByText('Server error')).not.toBeInTheDocument();
    });
  });
});

import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';
import React from 'react';

import { act, fireEvent, render, screen, userEvent, waitFor } from '../../utils/test-utils';
import NewPassword from './NewPassword.component';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestMutationRequest: () => resetPasswordMutationMock,
}));

const resetPasswordMutationMock = {
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  data: {},
  error: null,
};

describe('NewPassword', function () {
  const baseDataTestId = 'NewPassword';
  const defaultValues = { email: '', password: '', confirmPassword: '' };
  const labels = {
    login: {
      business: {
        bookingsInvalidEmailMsg: 'Please enter a valid email address',
        bookingLoginRequiredText: 'Please fill in this field.',
        bookingEmailMaxLengthMsg: 'Maximum length for e-mail is 50 characters',
      },
      invalidEmail: 'Please enter a valid e-mail address.',
    },
    resetPassword: {
      criteria: '8 characters minimum, 1 number required, 1 capital letter, No special characters',
      criteriaDescription: 'Your password should be',
      email: 'Email',
      invalidPassword: 'Invalid password',
      invalidPasswordConfirmation: 'Password does not match',
      logInButton: 'Log in to Myaccount',
      password: 'Password',
      passwordConfirmation: 'Confirm password',
      passwordMin: 'This password should have at least 10 characters',
      passwordRequired: 'This field is required',
      passwordRequirementsAllowed:
        'Allowed characters: All upper case characters (A-Z), all lower case characters (a-z), Digits (0-9), Any / All Non alphanumeric (!@#$%^&*)',
      passwordRequirementsIdentical: 'Not containing more than 2 identical characters in a row',
      passwordRequirementsMin: 'At least 10 characters in length',
      resetPasswordTitle: 'Enter the new password for My Account',
      submitButton: 'Submit',
      successMessage: 'Your account password was successfuly reset',
    },
  };

  const Component = () => {
    return (
      <NewPassword
        defaultValues={defaultValues}
        toggleLoginModal={jest.fn()}
        token={'123asd'}
        isBusinessBooker={false}
        labels={labels}
      />
    );
  };

  beforeEach(() => {
    resetPasswordMutationMock.isError = false;
    resetPasswordMutationMock.data = {};
    resetPasswordMutationMock.error = null;
  });

  it('should render NewPassword component', async () => {
    const { getByTestId } = render(<Component />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    });
  });

  it('should show error when any field is not filled in', async () => {
    const { getByTestId, getByText } = render(<Component />);
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));

    await act(async () => {
      fireEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(getByText(labels.resetPassword.passwordRequired)).toBeInTheDocument();
    });
  });

  it('should show error when the email is not valid', async () => {
    const { getByTestId, getByLabelText, getByText } = render(<Component />);
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));
    const emailInput = getByLabelText(labels.resetPassword.email);

    await act(async () => {
      userEvent.type(emailInput, 'test@test');
      userEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(getByText(labels.login.invalidEmail)).toBeInTheDocument();
    });
  });

  it('should show error when the password does not have 10 characters', async () => {
    const { getByTestId, getByLabelText, getByText } = render(<Component />);
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));
    const passwordInput = getByLabelText(labels.resetPassword.password);

    await act(async () => {
      await userEvent.type(passwordInput, 'abc');
      await userEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(getByText(labels.resetPassword.passwordMin)).toBeInTheDocument();
    });
  });

  it('should show error when the password has repeating characters', async () => {
    const { getByTestId, getByLabelText, getByText } = render(<Component />);
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));
    const passwordInput = getByLabelText(labels.resetPassword.password);

    await act(async () => {
      await userEvent.type(passwordInput, 'TestingThisOneee');
      await userEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(getByText(labels.resetPassword.invalidPassword)).toBeInTheDocument();
    });
  });

  it('should show error when the passwords do not match', async () => {
    const { getByTestId, getByLabelText, getByText } = render(<Component />);
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));
    const passwordInput = getByLabelText(labels.resetPassword.password);
    const confirmPassInput = getByLabelText(labels.resetPassword.passwordConfirmation);

    await act(async () => {
      await userEvent.type(passwordInput, 'ThisIsAStrongPassword1');
      await userEvent.type(confirmPassInput, 'ThisIsAStrongPassword12');
      await userEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(getByText(labels.resetPassword.invalidPasswordConfirmation)).toBeInTheDocument();
    });
  });

  it('should show success notification when submit is successful', async () => {
    resetPasswordMutationMock.data = {
      passwordChanged: true,
    };

    render(<Component />);

    await waitFor(() => {
      expect(screen.getByText(labels.resetPassword.successMessage)).toBeInTheDocument();
    });
  });

  it('should show error notification when submit failed', async () => {
    resetPasswordMutationMock.isError = true;

    render(<Component />);

    await waitFor(() => {
      expect(screen.getByText('booking.login.changePassword.error')).toBeInTheDocument();
    });
  });
});

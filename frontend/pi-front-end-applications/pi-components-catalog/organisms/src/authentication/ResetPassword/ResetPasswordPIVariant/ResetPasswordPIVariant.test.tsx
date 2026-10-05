import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';
import React from 'react';

import { fireEvent, render, screen, waitFor } from '../../../utils/test-utils';
import { mockedAuthenticationLabels } from '../../mockResponse';
import ResetPasswordPIVariant from './ResetPasswordPIVariant.component';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestMutationRequest: () => mockedMutationRequest(),
}));

const baseDataTestId = 'ResetPassword';
const setIsLoginForm = jest.fn();
const onClose = jest.fn();
const defaultValues = { email: '' };
const mockedMutationRequest = jest.fn();

const mockUseMutationResponse = {
  mutation: {
    mutate: jest.fn(),
  },
  isLoading: false,
  isError: false,
  isSuccess: true,
  isIdle: false,
  data: {},
  error: null,
};

const Component = () => {
  return (
    <ResetPasswordPIVariant
      setIsLoginForm={setIsLoginForm}
      defaultValues={defaultValues}
      labels={mockedAuthenticationLabels}
    />
  );
};

const ComponentOnBooking = () => {
  return (
    <ResetPasswordPIVariant
      onClose={onClose}
      defaultValues={defaultValues}
      labels={mockedAuthenticationLabels}
      isBookingFlow={true}
    />
  );
};

describe('Reset Password PI', function () {
  beforeEach(() => {
    jest.clearAllMocks();

    mockedMutationRequest.mockReturnValue(mockUseMutationResponse);
  });

  it('should render ResetPassword component', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should render ResetPassword without containing all the values from the labels', () => {
    const modifiedLabels = {
      ...mockedAuthenticationLabels,
      login: {
        ...mockedAuthenticationLabels.login,
        business: {
          ...mockedAuthenticationLabels.login.business,
          bookingLoginRequiredText: null,
          bookingsInvalidEmailMsg: null,
          bookingEmailMaxLengthMsg: null,
        },
      },
    };

    const { getByTestId } = render(
      <ResetPasswordPIVariant
        setIsLoginForm={setIsLoginForm}
        defaultValues={defaultValues}
        labels={modifiedLabels}
      />
    );
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should render ResetPassword component with empty input field', () => {
    const { getByTestId, getByLabelText } = render(<Component />);
    const resetPasswordEmailAddress = getByLabelText('Email address');

    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    expect(resetPasswordEmailAddress).toHaveValue('');
  });

  it('should cancel ResetPassword screen and display login form', () => {
    const { getByTestId } = render(<Component />);
    const cancelLink = getByTestId(formatDataTestId(baseDataTestId, 'Cancel'));
    fireEvent.click(cancelLink);
    expect(setIsLoginForm).toHaveBeenCalledWith(true);
  });

  it('should cancel ResetPassword screen and close the modal in booking flow', async () => {
    const { getByTestId } = render(<ComponentOnBooking />);
    const cancelLink = getByTestId(formatDataTestId(baseDataTestId, 'Cancel'));

    fireEvent.click(cancelLink);
    expect(onClose).toHaveBeenCalled();
  });

  it('should show error when email is not filled in', async () => {
    const { getByTestId } = render(<Component />);
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(
        screen.getByText(mockedAuthenticationLabels.login.business.bookingLoginRequiredText)
      ).toBeInTheDocument();
    });
  });

  it('should show success screen when submit is successful', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      data: {
        success: true,
      },
    });

    render(<Component />);

    await waitFor(() => {
      expect(
        screen.getByText("We've sent you an email with instructions to reset your password")
      ).toBeInTheDocument();
    });
  });

  it('should show error when submit failed', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      isError: true,
    });

    render(<Component />);

    await waitFor(() => {
      expect(
        screen.getByText('Looks like something went wrong, please try again later')
      ).toBeInTheDocument();
    });
  });

  it('should trigger onSubmit function', async () => {
    const mutateMock = jest.fn();

    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      mutation: {
        mutate: mutateMock,
      },
      isSuccess: true,
    });

    const { getByTestId } = render(<Component />);

    const emailInput = getByTestId('input-email');
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'Submit'));

    fireEvent.change(emailInput, { target: { value: 'test@gmail.com' } });
    fireEvent.click(submitBtn);

    await waitFor(() => {
      expect(mutateMock).toHaveBeenCalled;
    });
  });

  it('should display "Back to Login" in confirmation view, after successfully submitting reset form - modal', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      data: {
        success: true,
      },
    });

    const { getByText } = render(<Component />);

    await waitFor(() => {
      const backButton = getByText('Back to Login');
      expect(backButton).toBeInTheDocument();
    });
  });

  it('should display "Back to Your Details" in confirmation view, after successfully submitting reset form - booking flow', async () => {
    mockedMutationRequest.mockReturnValue({
      ...mockUseMutationResponse,
      data: {
        success: true,
      },
    });

    const { getByText } = render(<ComponentOnBooking />);

    await waitFor(() => {
      const backButton = getByText('Back to Your Details');
      expect(backButton).toBeInTheDocument();
    });
  });
});

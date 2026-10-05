import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { fireEvent, render, screen, waitFor } from '../../../utils/test-utils';
import { mockedAuthenticationLabels } from '../../mockResponse';
import ResetPasswordBBVariant from './ResetPasswordBBVariant.component';

const baseDataTestId = 'ResetPassword';
const setIsLoginForm = jest.fn();
const defaultValues = { email: '' };
const mockedMutationRequest = jest.fn();

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));
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

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestMutationRequest: () => mockedMutationRequest(),
}));

const Component = () => {
  return (
    <ResetPasswordBBVariant
      setIsLoginForm={setIsLoginForm}
      defaultValues={defaultValues}
      labels={mockedAuthenticationLabels}
    />
  );
};

describe('Reset Password BB', function () {
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
      <ResetPasswordBBVariant
        setIsLoginForm={setIsLoginForm}
        defaultValues={defaultValues}
        labels={modifiedLabels}
      />
    );
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
  });

  it('should cancel ResetPassword screen', () => {
    const { getByTestId } = render(<Component />);
    const cancelLink = getByTestId(formatDataTestId(baseDataTestId, 'Cancel'));
    fireEvent.click(cancelLink);
    expect(setIsLoginForm).toHaveBeenCalledWith(true);
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
      data: { success: true },
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
});

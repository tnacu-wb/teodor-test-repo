import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { fireEvent, render, screen, waitFor, act } from '../../../utils/test-utils';
import { mockedAuthenticationLabels } from '../../mockResponse';
import LoginBBVariant from './LoginBBVariant.component';

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: { locale: 'en' },
    };
  },
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
  }),
}));

const baseDataTestId = 'Login';
const setIsLoginForm = jest.fn();
const toggleLoginModal = jest.fn();
const defaultValues = { email: '', password: '' };

const mockedProps = {
  setIsLoginForm,
  toggleLoginModal,
  defaultValues,
  showRegisterNotification: false,
  hasRegisteredSuccessfully: false,
  labels: mockedAuthenticationLabels,
};

const Component = (props) => {
  return <LoginBBVariant {...mockedProps} {...props} />;
};

describe('Login Modal', function () {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render Login component', async () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();

    window.postMessage(
      {
        action: 'userLoggedIn',
        data: 'userLoggedIn',
      },
      '*'
    );

    await act(async () => {
      fireEvent(
        window,
        new MessageEvent('message', {
          data: { action: 'userLoggedIn', data: 'userLoggedIn' },
          origin: 'localhost',
        })
      );
    });
  });

  it('should render Login modal title', async () => {
    const { getByTestId } = render(<Component />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Title'))).toBeInTheDocument();
    });
  });

  it('should render Email input', async () => {
    const { getByTestId } = render(<Component />);
    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Email'))).toBeInTheDocument();
    });
  });

  it('should render Password input', async () => {
    const { getByTestId } = render(<Component />);
    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Password'))).toBeInTheDocument();
    });
  });

  it('should render Business booker notification', async () => {
    const { getByTestId } = render(<Component />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'BBNotification'))).toBeInTheDocument();
      expect(getByTestId('BBNotification-AlertDescription')).toBeInTheDocument();
    });
  });

  it('should render register success notification', async () => {
    const { getByTestId, queryByTestId } = render(
      <Component showRegisterNotification={true} hasRegisteredSuccessfully={true} />
    );

    await waitFor(() => {
      expect(
        getByTestId(formatDataTestId(baseDataTestId, 'BBSuccessNotification'))
      ).toBeInTheDocument();
      expect(queryByTestId(formatDataTestId(baseDataTestId, 'BBNotification'))).toBeNull();
    });
  });

  it('should render register error notification', async () => {
    const { getByTestId, queryByTestId } = render(
      <Component showRegisterNotification={true} hasRegisteredSuccessfully={false} />
    );

    await waitFor(() => {
      expect(
        getByTestId(formatDataTestId(baseDataTestId, 'BBErrorNotification'))
      ).toBeInTheDocument();
      expect(getByTestId('Login-BBErrorNotificationDescription')).toBeInTheDocument();
      expect(queryByTestId(formatDataTestId(baseDataTestId, 'BBNotification'))).toBeNull();
    });
  });

  it('should cancel Login screen', async () => {
    const { getByTestId } = render(<Component />);
    const forgotPassLink = getByTestId(formatDataTestId(baseDataTestId, 'ResetPassLink'));

    await act(async () => {
      fireEvent.click(forgotPassLink);
    });

    await waitFor(() => {
      expect(setIsLoginForm).toHaveBeenCalledWith(false);
    });
  });

  it('should show error when email and password are not filled in', async () => {
    const { getByTestId } = render(<Component />);
    const loginBtn = getByTestId(formatDataTestId(baseDataTestId, 'ButtonLogin'));

    await act(async () => {
      fireEvent.click(loginBtn);
    });

    await waitFor(() => {
      expect(
        screen.getAllByText(mockedAuthenticationLabels.login.business.bookingLoginRequiredText)
          .length
      ).toBe(2);
    });
  });

  it('should show error when email is wrong', async () => {
    defaultValues.email = 'abc';
    const { getByTestId } = render(<Component />);
    const loginBtn = getByTestId(formatDataTestId(baseDataTestId, 'ButtonLogin'));

    await act(async () => {
      fireEvent.click(loginBtn);
    });

    await waitFor(() => {
      expect(
        screen.getByText(mockedAuthenticationLabels.login.business.bookingsInvalidEmailMsg)
      ).toBeInTheDocument();
    });
  });

  it('should call onSubmit', async () => {
    const postMessageSpy = jest.fn();

    document.getElementById = jest.fn().mockReturnValue({
      contentWindow: {
        postMessage: postMessageSpy,
      },
    });

    const { getByTestId } = render(<Component />);

    const emailInput = getByTestId('input-email');
    const passwordInput = getByTestId('input-password');
    const submitBtn = getByTestId(formatDataTestId(baseDataTestId, 'ButtonLogin'));

    await act(async () => {
      fireEvent.change(emailInput, { target: { value: 'test@gmail.com' } });
      fireEvent.change(passwordInput, { target: { value: 'A12bcd!@Ef' } });
      fireEvent.click(submitBtn);
    });

    await waitFor(() => {
      expect(postMessageSpy).toHaveBeenCalledWith(
        JSON.stringify({
          action: 'login',
          username: 'test@gmail.com',
          password: 'A12bcd!@Ef',
          isBusiness: true,
        }),
        'localhost'
      );
    });
  });
});

import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { fireEvent, render, screen, waitFor, act } from '../../../utils/test-utils';
import { mockedAuthenticationLabels } from '../../mockResponse';
import LoginPIVariant from './LoginPIVariant.component';

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
    useFeatureSwitch: () => true,
  }),
}));

const mockCustomLocale = jest.fn();
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
}));

const baseDataTestId = 'Login';
const setIsLoginForm = jest.fn();
const toggleLoginModal = jest.fn();
const defaultValues = { email: '', password: '' };

const mockedProps = {
  setIsLoginForm,
  toggleLoginModal,
  defaultValues,
  labels: mockedAuthenticationLabels,
};

const Component = () => {
  return <LoginPIVariant {...mockedProps} />;
};

describe('Login Modal PI', function () {
  beforeEach(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
    });
  });

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

  it('should show signup link when language is gb', async () => {
    const { getByTestId } = render(<Component />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'SignUpLink'))).toBeInTheDocument();
    });
  });

  it('should show signup link when language is de', async () => {
    mockCustomLocale.mockReturnValue({ country: 'de' });
    const { getByTestId } = render(<Component />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'SignUpLink'))).toBeInTheDocument();
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
        }),
        'localhost'
      );
    });
  });

  it('should call onSubmit and handle loginError', async () => {
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

    const errorMessage = {
      origin: 'localhost',
      data: JSON.stringify({ action: 'loginError' }),
    };

    await waitFor(() => {
      window.dispatchEvent(new MessageEvent('message', errorMessage));
      expect(screen.getByTestId('AlertTitle')).toBeInTheDocument();
    });
  });

  it('should render LoginPIVariant without containing all the values from the labels', async () => {
    const mockedPropsModified = {
      ...mockedProps,
      labels: {
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
      },
    };
    const { getByTestId } = render(<LoginPIVariant {...mockedPropsModified} />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
    });
  });

  it('should render LoginPIVariant with headerInfoData and correct url for en', async () => {
    mockCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    const mockedPropsModified = {
      ...mockedProps,
      headerInfoData: {
        content: {
          authentication: {
            login: {
              business: {
                businessDomain: 'https://business.dit.premierinn.digital',
                businessLogin: 'Log in to Premier Inn Business here',
                travelForBusiness: 'Travelling for business?',
              },
            },
          },
        },
      },
    };
    const { getByTestId } = render(<LoginPIVariant {...mockedPropsModified} />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'BusinessLink'))).toBeInTheDocument();
      expect(getByTestId(formatDataTestId(baseDataTestId, 'BusinessLink'))).toHaveAttribute(
        'href',
        'https://business.dit.premierinn.digital/en-gb/account/login?intcmp=piLogInModalLink'
      );
    });
  });

  it('should render LoginPIVariant with headerInfoData and correct url for de', async () => {
    mockCustomLocale.mockReturnValue({ country: 'de', language: 'de' });
    const mockedPropsModified = {
      ...mockedProps,
      headerInfoData: {
        content: {
          authentication: {
            login: {
              business: {
                businessDomain: 'https://business.dit.premierinn.digital',
                businessLogin: 'Log in to Premier Inn Business here',
                travelForBusiness: 'Travelling for business?',
              },
            },
          },
        },
      },
    };
    const { getByTestId } = render(<LoginPIVariant {...mockedPropsModified} />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'BusinessLink'))).toBeInTheDocument();
      expect(getByTestId(formatDataTestId(baseDataTestId, 'BusinessLink'))).toHaveAttribute(
        'href',
        'https://business.dit.premierinn.digital/de-de/account/login?intcmp=piLogInModalLink'
      );
    });
  });

  it('should render LoginPIVariant with headerInfoData and without businessDomain', async () => {
    const mockedPropsModified = {
      ...mockedProps,
      headerInfoData: {
        content: {
          authentication: {
            login: {
              business: {
                businessDomain: null,
                businessLogin: 'Log in to Premier Inn Business here',
                travelForBusiness: 'Travelling for business?',
              },
            },
          },
        },
      },
    };
    const { queryByTestId } = render(<LoginPIVariant {...mockedPropsModified} />);

    await waitFor(() => {
      expect(
        queryByTestId(formatDataTestId(baseDataTestId, 'BusinessLink'))
      ).not.toBeInTheDocument();
      expect(screen.getByText('Travelling for business?')).toBeInTheDocument();
    });
  });

  it('should render LoginPIVariant without headerInfoData', async () => {
    const mockedPropsModified = {
      ...mockedProps,
      headerInfoData: null,
    };
    const { getByTestId } = render(<LoginPIVariant {...mockedPropsModified} />);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();
      expect(
        screen.queryByTestId(formatDataTestId(baseDataTestId, 'BusinessLink'))
      ).not.toBeInTheDocument();
    });
  });
});

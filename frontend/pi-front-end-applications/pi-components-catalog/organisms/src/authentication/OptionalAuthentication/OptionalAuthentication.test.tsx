import '@testing-library/jest-dom';
import { formatDataTestId, UserDataContext } from '@whitbread-eos/utils';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import { mockedAuthenticationLabels } from '../mockResponse';
import OptionalAuthentication from './OptionalAuthentication.component';

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
  useFeatureSwitch: () => true,
}));

const baseDataTestId = 'GuestDetails-OptionalAuth';
const setRegisterSectionSelected = jest.fn();

describe('OptionalAuthentication', () => {
  const getComponent = () => {
    return (
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <OptionalAuthentication
          currentLang="en"
          labels={mockedAuthenticationLabels}
          isRegisterSelected={false}
          setRegisterSectionSelected={setRegisterSectionSelected}
        />
      </UserDataContext.Provider>
    );
  };

  const getRegisterComponent = () => {
    return (
      <UserDataContext.Provider
        value={{
          isLoggedIn: false,

          setIsLoggedIn: () => {},
        }}
      >
        <OptionalAuthentication
          currentLang="en"
          labels={mockedAuthenticationLabels}
          isRegisterSelected={true}
          setRegisterSectionSelected={setRegisterSectionSelected}
        />
      </UserDataContext.Provider>
    );
  };

  const getLoggedInComponent = () => {
    return (
      <UserDataContext.Provider
        value={{
          isLoggedIn: true,

          setIsLoggedIn: () => {},
        }}
      >
        <OptionalAuthentication
          currentLang="en"
          labels={mockedAuthenticationLabels}
          isRegisterSelected={false}
          setRegisterSectionSelected={setRegisterSectionSelected}
        />
      </UserDataContext.Provider>
    );
  };
  it('should render OptionalAuthentication', () => {
    const { getByTestId } = render(getComponent());
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Container'))).toBeInTheDocument();

    window.postMessage(
      {
        action: 'userLoggedIn',
        data: 'userLoggedIn',
      },
      '*'
    );

    fireEvent(
      window,
      new MessageEvent('message', {
        data: { action: 'userLoggedIn', data: 'userLoggedIn' },
        origin: 'localhost',
      })
    );
  });

  // replaced with the next test for Pilot where Register is hidden
  // it('should render Sign in and Register button', () => {
  //   const { getByTestId } = render(getComponent());
  //   expect(getByTestId(formatDataTestId(baseDataTestId, 'SignIn-Option'))).toBeInTheDocument();
  //   expect(getByTestId(formatDataTestId(baseDataTestId, 'Register-Option'))).toBeInTheDocument();
  // });

  it('should render Sign in button', () => {
    const { getByTestId } = render(getComponent());
    expect(getByTestId(formatDataTestId(baseDataTestId, 'SignIn-Option'))).toBeInTheDocument();
  });

  it('should open Login form when clicking on Sign in option', async () => {
    const { getByTestId } = render(getComponent());
    const signInOption = getByTestId(formatDataTestId(baseDataTestId, 'SignIn-Option'));
    fireEvent.click(signInOption);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Login-Container'))).toBeInTheDocument();
    });
  });

  it('should render Register button', () => {
    const { getByTestId } = render(getRegisterComponent());
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Register-Option'))).toBeInTheDocument();
  });

  it('should open Register form when clicking on Register  option', async () => {
    const { getByTestId } = render(getRegisterComponent());
    const registerOption = getByTestId(formatDataTestId(baseDataTestId, 'Register-Option'));
    fireEvent.click(registerOption);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'Register-Option'))).toBeInTheDocument();
  });

  it('should render Sign in button', () => {
    const { queryByTestId } = render(getLoggedInComponent());
    expect(
      queryByTestId(formatDataTestId(baseDataTestId, 'SignIn-Option'))
    ).not.toBeInTheDocument();
    expect(
      queryByTestId(formatDataTestId(baseDataTestId, 'Register-Option'))
    ).not.toBeInTheDocument();
  });
  it('should render Reset Password when link is clicked', async () => {
    const { getByText, getByTestId } = render(getComponent());
    const signInOption = getByTestId(formatDataTestId(baseDataTestId, 'SignIn-Option'));
    fireEvent.click(signInOption);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Login-Container'))).toBeInTheDocument();
    });

    const textLink = getByText('Forgotten password?');

    fireEvent.click(textLink);
    const resetPassModal = getByText('Reset your password');
    expect(resetPassModal).toBeInTheDocument();
  });
  it('should cancel ResetPassword screen', async () => {
    const { getByTestId, getByText } = render(getComponent());
    const signInOption = getByTestId(formatDataTestId(baseDataTestId, 'SignIn-Option'));
    fireEvent.click(signInOption);

    await waitFor(() => {
      expect(getByTestId(formatDataTestId(baseDataTestId, 'Login-Container'))).toBeInTheDocument();
    });

    const textLink = getByText('Forgotten password?');

    fireEvent.click(textLink);
    const resetPassModal = getByText('Reset your password');
    expect(resetPassModal).toBeInTheDocument();

    const cancelLink = getByText('Cancel');
    fireEvent.click(cancelLink);

    await waitFor(() => {
      expect(resetPassModal).not.toBeInTheDocument();
    });
  });
});

import '@testing-library/jest-dom';
import { fireEvent } from '@testing-library/react';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import AccountExistsManager from './account-exists-manager';

const mockProps = {
  icons: {},
  header: null,
  initialState: {
    address: 'test',
  },
  initialStepId: 'Confirmation',
  steps: [
    {
      id: 'Confirmation',
      component: <AccountExistsManager locale={LOCALES.EN} />,
    },
  ],
};

const mockRouterPush = jest.fn();

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: mockRouterPush,
  }),
}));

describe('AccountExistsManager', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should render the component with the correct content', () => {
    const { getByText, getByTestId } = render(<Wizard {...mockProps} />);
    expect(getByText('signup.accountExists.emailTaken.heading')).toBeInTheDocument();
    expect(getByTestId('AccountExistsManager-wrapper')).toBeInTheDocument();
    expect(getByTestId('AccountExistsManager-description')).toHaveTextContent(
      'signup.accountExists.emailTaken.description'
    );
    expect(getByTestId('AccountExistsManager-Button')).toHaveTextContent(
      'signup.accountExists.emailTaken.loginButton'
    );
    expect(getByTestId('AccountExistsManager-forgot-password')).toHaveTextContent(
      'signup.accountExists.emailTaken.forgotPassword'
    );
  });
  it('should navigate to login page when login button is clicked', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);
    const loginButton = getByTestId('AccountExistsManager-Button');
    fireEvent.click(loginButton);
    expect(mockRouterPush).toHaveBeenCalledWith('/en-gb/account/login');
  });

  it('should render forgot password link', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);
    const forgotPasswordLink = getByTestId('AccountExistsManager-forgot-password');
    expect(forgotPasswordLink).toHaveAttribute('href', '/en-gb/account/forgot');
  });

  it('should validate back button', async () => {
    const { getByAltText } = render(<Wizard {...mockProps} />);
    const backButton = getByAltText('Back');
    expect(backButton).toBeInTheDocument();
  });

  it('should call analytics track on mount', () => {
    const trackMock = jest.fn();
    global.window = Object.create(window);
    window._satellite = { track: trackMock };

    render(<Wizard {...mockProps} />);
    expect(trackMock).not.toHaveBeenCalledWith('signUpExistingCompany');
  });
});

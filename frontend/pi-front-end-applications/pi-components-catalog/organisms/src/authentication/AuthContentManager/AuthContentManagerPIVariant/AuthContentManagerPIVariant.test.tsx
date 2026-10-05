import '@testing-library/jest-dom';

import { fireEvent, render, screen } from '../../../utils/test-utils';
import { mockedAuthenticationLabels } from '../../mockResponse';
import AuthContentManagerPIVariant from './AuthContentManagerPIVariant.component';

const language = {
  router: {
    locale: 'en',
  },
};

const mockedModalProps = {
  isLoginModalOpen: true,
  toggleLoginModal: jest.fn(),
  labels: mockedAuthenticationLabels,
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => language,
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
  }),
}));

describe('AuthContentManagerPIVariant', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render AuthContentManagerPIVariant with login item', () => {
    render(<AuthContentManagerPIVariant {...mockedModalProps} />);
    const modal = screen.getByTestId('Header-Auth-ModalContent');
    expect(modal).toBeDefined();
  });

  it('should render AuthContentManagerPIVariant with login title', () => {
    render(<AuthContentManagerPIVariant {...mockedModalProps} />);
    const modal = screen.getByText('Log into your Premier Inn account');
    expect(modal).toBeDefined();
  });

  it('should render Reset Password when link is clicked', () => {
    render(<AuthContentManagerPIVariant {...mockedModalProps} />);
    const textLink = screen.getByText('Forgotten password?');

    fireEvent.click(textLink);
    const resetPassModal = screen.getByText('Reset your password');

    expect(resetPassModal).toBeDefined();
  });
});

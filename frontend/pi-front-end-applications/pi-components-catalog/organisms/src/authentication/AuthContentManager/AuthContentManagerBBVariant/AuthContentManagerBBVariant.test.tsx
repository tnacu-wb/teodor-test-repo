import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { fireEvent, render, screen } from '../../../utils/test-utils';
import { mockedAuthenticationLabels } from '../../mockResponse';
import AuthContentManagerBBVariant from './AuthContentManagerBBVariant.component';

const baseLoginDataTestId = 'Login';
const baseResetPasswordDataTestId = 'ResetPassword';

const language = {
  router: {
    locale: 'en',
  },
};

const mockedModalProps = {
  isLoginModalOpen: true,
  showRegisterNotification: false,
  hasRegisteredSuccessfully: false,
  toggleLoginModal: jest.fn(),
  labels: mockedAuthenticationLabels,
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({ language, query: { redirect: '/business-booker' } }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useRestMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
  }),
}));

describe('AuthContentManagerBBVariant', () => {
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render AuthContentManagerBBVariant with login item', () => {
    render(<AuthContentManagerBBVariant {...mockedModalProps} />);
    const modal = screen.getByTestId('BB-Header-Auth-ModalContent');
    expect(modal).toBeDefined();
  });

  it('should render AuthContentManagerBBVariant with login title', () => {
    render(<AuthContentManagerBBVariant {...mockedModalProps} />);
    const modal = screen.getByTestId(formatDataTestId(baseLoginDataTestId, 'Title'));
    expect(modal).toBeDefined();
  });

  it('should render Reset Password when link is clicked', () => {
    render(<AuthContentManagerBBVariant {...mockedModalProps} />);
    const textLink = screen.getByTestId(formatDataTestId(baseLoginDataTestId, 'ResetPassLink'));

    fireEvent.click(textLink);
    const resetPassModal = screen.getByTestId(
      formatDataTestId(baseResetPasswordDataTestId, 'Notification')
    );

    expect(resetPassModal).toBeDefined();
  });
});

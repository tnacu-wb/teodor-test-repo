import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import EmailInputModal, { Props } from './EmailInputModal.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
}));

type useMutationRequestType = {
  isLoading: boolean;
  isSuccess: boolean;
};

const mockUseMutationRequest: useMutationRequestType = {
  isLoading: false,
  isSuccess: false,
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => mockUseMutationRequest,
  useFeatureSwitch: () => true,
}));

const handleEmailChange = jest.fn();
const handleOnSubmit = jest.fn();
const checkIfValid = jest.fn();
const handleOnModalClose = jest.fn();
const handleCustomerConsentChange = jest.fn();

const modalContent = {
  baseDataTestId: 'CreateMyPiAccount',
  title: 'Confirm Email address',
  description: 'Please enter an email for us to send confirmation to.',
  emailLabel: 'Send confirmation by email',
  emailPlaceholder: 'premierinn@whitbread.com',
  emailErrorMsg: 'Please enter a valid email address',
  submitBtn: 'Create My PI account',
  successNotif: 'Please follow the instructions received via email to set up your My PI Account',
  errorNotif: 'Unable to trigger email. Please try again.',
  customerConsent: 'Guest consent to receive MyPI Registration email',
};

const mockProps = {
  modalContent,
  isModalVisible: true,
  email: '',
  handleEmailChange,
  handleOnSubmit,
  showNotification: false,
  isLoading: false,
  checkIfValid,
  handleOnModalClose,
  hasError: false,
  hasCustomerContent: true,
  customerConsent: false,
  handleCustomerConsentChange,
} as unknown as Props;

describe('EmailInputModal component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('it should render the EmailInputModal with default props', () => {
    const { getByText, getByTestId } = render(<EmailInputModal {...mockProps} />);
    expect(getByTestId('CreateMyPiAccount-ModalContainer')).toBeInTheDocument();
    expect(getByTestId('CreateMyPiAccount-Email')).toBeInTheDocument();
    expect(getByTestId('CreateMyPiAccount-ModalButton')).toBeInTheDocument();
    expect(getByText('Send confirmation by email')).toBeInTheDocument();
    expect(getByText('Create My PI account')).toBeInTheDocument();
    expect(getByText('Guest consent to receive MyPI Registration email')).toBeInTheDocument();
  });

  it('it should render the EmailInputModal and button should be disabled', async () => {
    const { getByText } = render(<EmailInputModal {...mockProps} />);

    const sendButton = getByText('Create My PI account');
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(sendButton).toBeDisabled();
    });
  });

  it('it should render the EmailInputModal and after entering invalid email address error message should be displayed', async () => {
    const { getByText } = render(<EmailInputModal {...{ ...mockProps, hasError: true }} />);
    expect(getByText('Please enter a valid email address')).toBeInTheDocument();
  });

  it('it should render the EmailInputModal and click close modal', () => {
    const { getByTestId } = render(<EmailInputModal {...mockProps} />);

    const closeModal = getByTestId('ModalCloseButton');
    fireEvent.click(closeModal);
    expect(handleOnModalClose).toBeCalled();
  });

  it('it should render the EmailInputModal and success notification', () => {
    const { getByTestId } = render(
      <EmailInputModal
        {...{ ...mockProps, showNotification: true, isEmailTriggeringSuccess: true }}
      />
    );
    expect(getByTestId('CreateMyPiAccount-SuccessNotification')).toBeInTheDocument();
  });

  it('it should render the EmailInputModal and error notification', () => {
    const { getByTestId } = render(
      <EmailInputModal
        {...{ ...mockProps, showNotification: true, isEmailTriggeringError: true }}
      />
    );
    expect(getByTestId('CreateMyPiAccount-ErrorNotification')).toBeInTheDocument();
  });

  it('it should render the EmailInputModal and button should be disabled if request is loading', async () => {
    const { getByText } = render(
      <EmailInputModal {...mockProps} email="aaa@aaa.aa" hasError={false} isLoading={true} />
    );

    const sendButton = getByText('Create My PI account');
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(sendButton).toBeDisabled();
    });
  });

  it('it should render the EmailInputModal and button should be enable if email input is filled and customer consent is checked', async () => {
    const { getByText, getByRole, rerender } = render(
      <EmailInputModal
        {...mockProps}
        email="aaa@aaa.aa"
        hasError={false}
        isLoading={false}
        customerConsent={false}
      />
    );

    const consentCheckbox = getByRole('checkbox');
    fireEvent.click(consentCheckbox);
    rerender(
      <EmailInputModal
        {...mockProps}
        email="aaa@aaa.aa"
        hasError={false}
        isLoading={false}
        customerConsent={true}
      />
    );

    const sendButton = getByText('Create My PI account');

    await waitFor(() => {
      expect(sendButton).toBeEnabled();
    });
  });

  it('it should render the EmailInputModal and button should call the mutation', async () => {
    const { getByText } = render(
      <EmailInputModal {...mockProps} email="aaa@aaa.aa" customerConsent={true} />
    );

    const sendButton = getByText('Create My PI account');
    fireEvent.click(sendButton);

    await waitFor(() => {
      expect(handleOnSubmit).toBeCalled();
    });
  });
});

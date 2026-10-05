import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render, userEvent } from '../../../../utils/test-utils';
import ResendConfirmationModalContainer from './ResendConfirmationModal.container';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
  useMutationRequest: () => mockUseMutationRequest,
}));

type useMutationRequestType = {
  isLoading: boolean;
  isSuccess: boolean;
  mutation: any;
};

const mockUseMutationRequest: useMutationRequestType = {
  mutation: {
    mutate: jest.fn(),
  },
  isSuccess: false,
  isLoading: false,
};

const clearRequestMockup = (): void => {
  mockUseMutationRequest.isLoading = false;
  mockUseMutationRequest.isSuccess = false;
};
const handleOnModalClose = jest.fn();

describe('<ResendConfirmationModalContainer>', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    clearRequestMockup();
  });

  it('should render ResendConfirmationModalContainer', () => {
    const { getByTestId } = render(
      <ResendConfirmationModalContainer
        isModalVisible
        onModalClose={jest.fn()}
        basketReference="1234"
        hotelId="abc"
      />
    );
    expect(getByTestId('ResendConfirmationContainer')).toBeInTheDocument();
  });

  it('should render success notification if mutation was successful', async () => {
    mockUseMutationRequest.isSuccess = true;

    const { getByTestId } = render(
      <ResendConfirmationModalContainer
        isModalVisible
        basketReference="ABC"
        onModalClose={jest.fn()}
        hotelId="123"
      />
    );
    expect(getByTestId('ResendConfirmationEmail-SuccessNotification')).toBeInTheDocument();
  });

  it('should render the ResendConfirmationModal and click close modal', () => {
    const { getByTestId } = render(
      <ResendConfirmationModalContainer
        isModalVisible
        basketReference="ABC"
        onModalClose={handleOnModalClose}
        hotelId="123"
      />
    );

    const closeModal = getByTestId('ModalCloseButton');
    fireEvent.click(closeModal);
    expect(handleOnModalClose).toBeCalled();
  });

  it('handleEmailChange updates email state', () => {
    const updatedEmail = 'test@example.com';
    const { getByPlaceholderText } = render(
      <ResendConfirmationModalContainer
        isModalVisible
        bookingReference="1234"
        onModalClose={handleOnModalClose}
        hotelId="abc"
      />
    );

    const emailInput = getByPlaceholderText(
      'dashboard.bookings.emailPlaceholder'
    ) as HTMLInputElement;
    fireEvent.change(emailInput, { target: { value: updatedEmail } });
    expect(emailInput.value).toBe(updatedEmail);
  });

  it('resendConfirmation triggers the mutation correctly', async () => {
    const { getByTestId, getByPlaceholderText, getByText } = render(
      <ResendConfirmationModalContainer
        isModalVisible={true}
        onModalClose={() => ({})}
        bookingReference="test"
        hotelId="manold"
      />
    );

    const emailInput = getByPlaceholderText('dashboard.bookings.emailPlaceholder');

    fireEvent.change(emailInput, { target: { value: 'test@example.com' } });
    fireEvent.blur(emailInput);

    const resendConfirmationButton = getByTestId('ResendConfirmationEmail-ModalButton');

    expect(resendConfirmationButton).not.toHaveAttribute('disabled');
    fireEvent.click(resendConfirmationButton);
    mockUseMutationRequest.isSuccess = true;

    expect(getByText('dashboard.bookings.resendConfirmationInstructions')).toBeInTheDocument();
  });

  it('the email address is not valid', async () => {
    const { findByTestId, getByPlaceholderText } = render(
      <ResendConfirmationModalContainer
        isModalVisible={true}
        onModalClose={() => ({})}
        bookingReference="test"
        hotelId="manold"
      />
    );

    const emailInput = getByPlaceholderText('dashboard.bookings.emailPlaceholder');
    await userEvent.type(emailInput, 'test');
    await userEvent.tab();

    const resendConfirmationButton = await findByTestId('ResendConfirmationEmail-ModalButton');

    // Simulate typing for trigger handleOnChange
    await userEvent.type(emailInput, 'test@hello');
    await userEvent.tab();
    expect(resendConfirmationButton).toBeDisabled();
  });
});

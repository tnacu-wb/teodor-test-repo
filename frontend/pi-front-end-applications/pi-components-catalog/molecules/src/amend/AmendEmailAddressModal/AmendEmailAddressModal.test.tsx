import '@testing-library/jest-dom';
import React from 'react';

import { render, fireEvent, userEvent } from '../../utils/test-utils';
import AmendEmailAddressModal from './AmendEmailAddressModal.component';

const handleOnModalClose = jest.fn();

describe('<AmendEmailAddressModal>', () => {
  it('should render AmendEmailAddressModal', () => {
    const { getByTestId } = render(
      <AmendEmailAddressModal
        isModalVisible
        bookingEmail="email@email.com"
        onConfirmChanges={jest.fn()}
        onModalClose={jest.fn()}
      />
    );
    expect(getByTestId('AmendEmailAddress')).toBeInTheDocument();
  });

  it('it should render the AmendEmailAddressModal and click close modal', () => {
    const { getByTestId } = render(
      <AmendEmailAddressModal
        isModalVisible
        bookingEmail="email@email.com"
        onConfirmChanges={jest.fn()}
        onModalClose={handleOnModalClose}
      />
    );

    const closeModal = getByTestId('ModalCloseButton');
    fireEvent.click(closeModal);
    expect(handleOnModalClose).toBeCalled();
  });
  it('it should render the AmendEmailAddressModal and enter in email field', () => {
    const { getByTestId } = render(
      <AmendEmailAddressModal
        isModalVisible
        bookingEmail="email@email.com"
        onConfirmChanges={jest.fn()}
        onModalClose={handleOnModalClose}
        setEmailCallback={jest.fn()}
      />
    );

    const closeModal = getByTestId('ModalCloseButton');
    fireEvent.click(closeModal);
    expect(handleOnModalClose).toBeCalled();

    const emailInput = getByTestId('input-emailAddress');
    userEvent.type(emailInput, 'abc@xyz.com');
  });
});

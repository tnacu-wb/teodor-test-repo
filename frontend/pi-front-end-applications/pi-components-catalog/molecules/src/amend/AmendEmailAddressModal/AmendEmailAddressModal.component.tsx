import { Box } from '@chakra-ui/react';
import { isEmailValid } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { useState } from 'react';

import EmailInputModal from '../../common/EmailInputModal';

interface Props {
  isModalVisible: boolean;
  bookingEmail: string;
  onConfirmChanges: () => void;
  onModalClose: () => void;
  setEmailCallback: React.Dispatch<React.SetStateAction<string>>;
}

export default function AmendEmailAddressModal({
  isModalVisible,
  bookingEmail,
  onConfirmChanges,
  onModalClose,
  setEmailCallback,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const [email, setEmail] = useState(bookingEmail);
  const [hasError, setHasError] = useState(false);

  const modalContent = {
    baseDataTestId: 'AmendEmailAddress',
    title: t('ccui.account.amendEmail.title'),
    description: t('ccui.account.confirmEmail.text'),
    emailLabel: t('ccui.account.emailConfirmation'),
    emailPlaceholder: t('amend.emailAddress'),
    emailErrorMsg: t('ccui.account.error.email.invalid'),
    submitBtn: t('amend.confirmChanges'),
    successNotif: t('dashboard.bookings.resendConfirmationSuccess'),
    inputLabel: t('ccui.account.emailInputLabel'),
  };

  const checkIfValid = () => {
    if (!isEmailValid(email)) {
      setHasError(true);
    } else {
      setHasError(false);
    }
  };

  const handleOnClose = () => {
    setEmail(bookingEmail);
    setHasError(false);
    onModalClose();
  };

  const handleEmailChange = (value: string) => {
    setEmail(value);
    setEmailCallback(value);
  };

  return (
    <Box data-testId="AmendEmailAddress">
      <EmailInputModal
        modalContent={modalContent}
        isModalVisible={isModalVisible}
        email={email}
        handleEmailChange={(email) => handleEmailChange(email)}
        handleOnSubmit={onConfirmChanges}
        showNotification={false}
        isLoading={false}
        checkIfValid={checkIfValid}
        handleOnModalClose={handleOnClose}
        hasError={hasError}
        hasCustomerContent={false}
      />
    </Box>
  );
}

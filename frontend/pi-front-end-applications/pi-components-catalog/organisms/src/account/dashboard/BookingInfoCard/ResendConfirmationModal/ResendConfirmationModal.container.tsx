import { Box } from '@chakra-ui/react';
import { RESEND_CONFIRMATION_EMAIL } from '@whitbread-eos/api';
import { EmailInputModal } from '@whitbread-eos/molecules';
import { isEmailValid, useMutationRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useCallback, useEffect, useState } from 'react';

interface Props {
  isModalVisible: boolean;
  onModalClose: () => void;
  basketReference: string;
  hotelId: string;
}

export default function ResendConfirmationModal({
  isModalVisible,
  onModalClose,
  basketReference,
  hotelId,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [email, setEmail] = useState('');
  const [hasError, setHasError] = useState(false);
  const [showNotification, setShowNotification] = useState(false);

  const modalContent = {
    baseDataTestId: 'ResendConfirmationEmail',
    title: t('dashboard.bookings.resendConfirmationHeading'),
    description: t('dashboard.bookings.resendConfirmationInstructions'),
    emailLabel: t('dashboard.bookings.confirmationEmail'),
    emailPlaceholder: t('dashboard.bookings.emailPlaceholder'),
    emailErrorMsg: t('dashboard.bookings.resendConfirmationInstructions'),
    submitBtn: t('dashboard.bookings.sendConfirmation'),
    successNotif: t('dashboard.bookings.resendConfirmationSuccess'),
  };

  const {
    mutation: resendConfirmationMutation,
    isSuccess: resendConfirmationIsSuccess,
    isLoading: resendConfirmationIsLoading,
  } = useMutationRequest(RESEND_CONFIRMATION_EMAIL);

  useEffect(() => {
    if (resendConfirmationIsSuccess) {
      setShowNotification(true);
    }
  }, [resendConfirmationIsSuccess]);

  useEffect(() => {
    setHasError(false);
  }, []);

  const handleEmailChange = (email: string) => {
    return setEmail(email);
  };

  const handleOnClose = () => {
    setEmail('');
    setHasError(false);
    setShowNotification(false);
    onModalClose();
  };

  const resendConfirmation = useCallback(() => {
    resendConfirmationMutation.mutate({
      resendConfirmationRequest: {
        email: email,
        hotelId: hotelId,
        bookingReference: basketReference,
        sessionId: '',
        sourceSystem: 'OPERA',
      },
    });
  }, [basketReference, email, hotelId, resendConfirmationMutation]);

  const handleOnChange = (email: string) => {
    hasError && setHasError(false);
    handleEmailChange(email);
  };

  const checkIfValid = () => {
    if (!isEmailValid(email)) {
      setHasError(true);
    }
  };

  return (
    <Box data-testid="ResendConfirmationContainer">
      <EmailInputModal
        modalContent={modalContent}
        isModalVisible={isModalVisible}
        email={email}
        handleEmailChange={handleOnChange}
        handleOnSubmit={resendConfirmation}
        showNotification={showNotification}
        isLoading={resendConfirmationIsLoading}
        checkIfValid={checkIfValid}
        handleOnModalClose={handleOnClose}
        hasError={hasError}
        hasCustomerContent={false}
        isEmailTriggeringSuccess={resendConfirmationIsSuccess}
      />
    </Box>
  );
}

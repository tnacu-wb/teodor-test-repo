import { Box } from '@chakra-ui/react';
import {
  isEmailValid,
  useCustomLocale,
  useRestMutationRequest,
  getAuthCookie,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useEffect, useState } from 'react';

import EmailInputModal from '../../common/EmailInputModal';

interface Props {
  isModalVisible: boolean;
  onModalClose: () => void;
}

export default function CreateMyPiAccountModalContainer({
  isModalVisible,
  onModalClose,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const { language } = useCustomLocale();
  const [email, setEmail] = useState('');
  const [customerConsent, setCustomerConsent] = useState(false);
  const [hasEmailError, setHasEmailError] = useState(false);
  const [showNotification, setShowNotification] = useState(false);
  const [triggeringSuccessResponse, setTriggeringSuccessResponse] = useState(false);
  const idTokenCookie = getAuthCookie();

  const { publicRuntimeConfig = {} } = getConfig() || {};

  const modalContent = {
    baseDataTestId: 'CreateMyPiAccount',
    title: t('ccui.account.confirmEmail.title'),
    description: t('ccui.account.confirmEmail.text'),
    emailLabel: t('ccui.account.emailConfirmation'),
    inputLabel: t('ccui.account.emailInputLabel'),
    emailPlaceholder: t('ccui.email.placeholder'),
    emailErrorMsg: t('ccui.account.error.email.invalid'),
    submitBtn: t('ccui.account.createAccount'),
    successNotif: t('ccui.account.emailConfirmation.success'),
    errorNotif: t('ccui.account.emailConfirmation.error'),
    customerConsent: t('ccui.account.customerConsent'),
  };

  const handleEmailChange = (email: string) => {
    return setEmail(email);
  };

  const handleCustomerConsentChange = (value: boolean) => {
    return setCustomerConsent(value);
  };

  const {
    mutation,
    data,
    isSuccess: isEmailTriggeringSuccess,
    isError: isEmailTriggeringError,
  } = useRestMutationRequest(
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/emails/ccui/myPiAccount?email=${email}&language=${language}`,
    'POST',
    { Authorization: `Bearer ${idTokenCookie}` }
  );

  const sendEmail = () => {
    mutation.mutate({
      email,
      language,
    });
    setShowNotification(true);
  };

  useEffect(() => {
    if (isEmailTriggeringSuccess) {
      setTriggeringSuccessResponse(data.success);
    }
  }, [isEmailTriggeringSuccess]);

  const handleOnChange = (email: string) => {
    hasEmailError && setHasEmailError(false);
    handleEmailChange(email);
  };

  const checkIfValid = () => {
    if (!isEmailValid(email)) {
      setHasEmailError(true);
    }
  };

  const handleOnClose = () => {
    if (mutation) {
      mutation.reset();
    }

    setEmail('');
    setHasEmailError(false);
    setShowNotification(false);
    onModalClose();
    setTriggeringSuccessResponse(false);
    setCustomerConsent(false);
  };

  return (
    <Box data-testid="CreateMyPiAccountContainer">
      <EmailInputModal
        modalContent={modalContent}
        isModalVisible={isModalVisible}
        closeOnOverlayClick={false}
        email={email}
        handleEmailChange={handleOnChange}
        handleOnSubmit={sendEmail}
        showNotification={showNotification}
        isLoading={false}
        checkIfValid={checkIfValid}
        handleOnModalClose={handleOnClose}
        hasError={hasEmailError}
        hasCustomerContent={true}
        customerConsent={customerConsent}
        handleCustomerConsentChange={handleCustomerConsentChange}
        isEmailTriggeringSuccess={triggeringSuccessResponse}
        isEmailTriggeringError={isEmailTriggeringError}
      />
    </Box>
  );
}

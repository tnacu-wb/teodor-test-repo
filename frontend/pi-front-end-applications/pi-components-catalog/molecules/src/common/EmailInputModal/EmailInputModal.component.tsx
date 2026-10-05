import { BoxProps, TextProps, Box, Flex, Text, Divider } from '@chakra-ui/react';
import { FS_CONFIRM_EMAIL_ADDRESS_CCUI } from '@whitbread-eos/api';
import {
  Input,
  Button,
  ButtonProps,
  ModalVariants,
  Notification,
  Tick24,
  Checkbox,
  Info,
} from '@whitbread-eos/atoms';
import { formatDataTestId, isEmailValid, useFeatureSwitch } from '@whitbread-eos/utils';
import { ChangeEvent } from 'react';

type ModalContent = {
  baseDataTestId: string;
  title: string;
  description: string;
  emailLabel: string;
  emailPlaceholder: string;
  emailErrorMsg: string;
  submitBtn: string;
  successNotif: string;
  errorNotif?: string;
  customerConsent?: string;
  inputLabel?: string;
};

export interface Props {
  modalContent: ModalContent;
  isModalVisible: boolean;
  closeOnOverlayClick?: boolean;
  email: string;
  handleEmailChange: (param: string) => void;
  handleOnSubmit: () => void;
  showNotification: boolean;
  isLoading: boolean;
  hasError: boolean;
  checkIfValid: () => void;
  handleOnModalClose: () => void;
  hasCustomerContent: boolean;
  customerConsent?: boolean;
  handleCustomerConsentChange?: (param: boolean) => void;
  isEmailTriggeringSuccess?: boolean;
  isEmailTriggeringError?: boolean;
}

export default function EmailInputModal({
  modalContent,
  isModalVisible,
  closeOnOverlayClick = true,
  email,
  handleEmailChange,
  handleOnSubmit,
  showNotification,
  isLoading,
  checkIfValid,
  handleOnModalClose,
  hasError,
  hasCustomerContent,
  customerConsent,
  handleCustomerConsentChange,
  isEmailTriggeringSuccess = false,
  isEmailTriggeringError = false,
}: Readonly<Props>) {
  const { baseDataTestId } = modalContent;

  const isConfirmEmailAddressEnabled = useFeatureSwitch({
    featureSwitchKey: FS_CONFIRM_EMAIL_ADDRESS_CCUI,
    fallbackValue: false,
  });

  const isCustomerConsentCheckboxDisplayed =
    hasCustomerContent && handleCustomerConsentChange && isConfirmEmailAddressEnabled;

  const emailInputError = hasError && modalContent.emailErrorMsg;

  return (
    <ModalVariants
      onClose={handleOnModalClose}
      variant="info"
      isOpen={isModalVisible}
      variantProps={{ title: '', delimiter: false }}
      closeOnOverlayClick={closeOnOverlayClick}
      headerStyles={modalHeaderStyles}
      headerContentStyles={modalHeaderContentStyles}
    >
      <Box {...modalStyles}>{renderModalContent()}</Box>
    </ModalVariants>
  );

  function renderModalContent() {
    return (
      <Flex flexDir="column" data-testid={formatDataTestId(baseDataTestId, 'ModalContainer')}>
        <Divider {...dividerStyle} />
        {showNotification && renderEmailTriggeringNotification()}
        <Text {...titleStyle} mt="0">
          {modalContent.title}
        </Text>
        <Text {...emailInputModalDescriptionStyle}>{modalContent.description}</Text>
        <Text {...emailLabelStyle}>{modalContent.emailLabel}</Text>
        <Box {...inputStyles} data-testid={formatDataTestId(baseDataTestId, 'Email')}>
          <Input
            type="text"
            placeholderText={modalContent.emailPlaceholder}
            name="emailAddress"
            onBlur={checkIfValid}
            useTooltip
            extraTooltipStyles={extraTooltipStyles}
            error={emailInputError}
            value={email}
            onChange={handleEmailChange}
            isDisabled={isEmailTriggeringSuccess}
            label={modalContent.inputLabel}
          />
        </Box>
        {isCustomerConsentCheckboxDisplayed && (
          <Box
            data-testid={formatDataTestId(baseDataTestId, 'CustomerConsentCheckbox')}
            mt={emailInputError ? '74px' : 'md'}
          >
            <Checkbox
              onChange={(e: ChangeEvent<HTMLInputElement>) => {
                handleCustomerConsentChange(e.target.checked);
              }}
              isChecked={customerConsent}
              isDisabled={isEmailTriggeringSuccess}
            >
              <Text {...customerConsentLabelStyle}>{modalContent.customerConsent}</Text>
            </Checkbox>
          </Box>
        )}
        <Button
          {...confirmationButtonStyle}
          isDisabled={isButtonDisabled()}
          data-testid={formatDataTestId(baseDataTestId, 'ModalButton')}
          onClick={handleOnSubmit}
          variant="primary"
        >
          {modalContent.submitBtn}
        </Button>
      </Flex>
    );
  }

  function isButtonDisabled() {
    const shouldDisableBecauseOfCustomerConsent = hasCustomerContent && !customerConsent;
    const shouldDisableBecauseOfEmailTriggeringSuccess =
      hasCustomerContent && customerConsent && isEmailTriggeringSuccess;

    return (
      !isEmailValid(email) ||
      isLoading ||
      shouldDisableBecauseOfCustomerConsent ||
      shouldDisableBecauseOfEmailTriggeringSuccess
    );
  }
  function renderEmailTriggeringNotification() {
    if (isEmailTriggeringSuccess) {
      return (
        <Box mb="lg" data-testid={formatDataTestId(baseDataTestId, 'SuccessNotification')}>
          <Notification
            maxWidth="full"
            variant="success"
            status="success"
            description={
              <Text as="span" {...successMessageNotificationStyle}>
                {modalContent.successNotif}
              </Text>
            }
            svg={<Tick24 />}
          />
        </Box>
      );
    } else if (isEmailTriggeringError && modalContent.errorNotif) {
      return (
        <Box mb="lg" data-testid={formatDataTestId(baseDataTestId, 'ErrorNotification')}>
          <Notification
            maxWidth="full"
            variant="error"
            status="error"
            description={modalContent.errorNotif}
            svg={<Info color="var(--chakra-colors-error)" />}
          />
        </Box>
      );
    }
  }
}

const confirmationButtonStyle = {
  variant: 'secondary',
  w: '100%',
  mt: 'md',
} as ButtonProps;

const successMessageNotificationStyle = {
  fontSize: 'sm',
  lineHeight: '2',
  color: 'darkGrey1',
  fontFamily: 'body',
} as TextProps;

const customerConsentLabelStyle = {
  fontSize: 'sm',
  lineHeight: '2',
  color: 'darkGrey1',
  fontWeight: 'normal',
} as TextProps;

const emailLabelStyle = {
  fontSize: 'lg',
  lineHeight: '2',
  color: 'darkGrey1',
  fontWeight: 'semibold',
  mt: 'lg',
  mb: 'md',
} as TextProps;

const modalStyles = {
  w: '36rem',
  px: 'lg',
  pb: 'lg',
} as BoxProps;

const titleStyle = {
  mt: 'md',
  color: 'darkGrey1',
  fontSize: '3xl',
  lineHeight: '4',
  fontWeight: 'semibold',
} as TextProps;

const emailInputModalDescriptionStyle = {
  mt: 'sm',
  color: 'darkGrey2',
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
} as TextProps;

const extraTooltipStyles = {
  width: 'full',
};

const inputStyles = {
  w: '420px',
};

const dividerStyle = {
  mb: 'lg',
  borderColor: 'lightGrey4',
};

const modalHeaderStyles = {
  padding: '0px',
};

const modalHeaderContentStyles = {
  mx: 'lg',
  mt: 'lg',
  mb: 'sm',
};

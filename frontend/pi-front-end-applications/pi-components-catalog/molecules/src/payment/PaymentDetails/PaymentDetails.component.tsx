import { Box, BoxProps, Flex, Text } from '@chakra-ui/react';
import {
  HotelBrand,
  PaymentOption,
  PaymentMethodsAvailable,
  PiCardType,
  FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA,
} from '@whitbread-eos/api';
import { Info, Notification, RadioButton, Alert, PaymentRadioButton } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  renderSanitizedHtml,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import React, { Dispatch, SetStateAction, useEffect } from 'react';

interface Props extends BoxProps {
  hideHeader?: boolean;
  selectedPaymentDetail: PaymentOption;
  selectedPaymentType: PaymentMethodsAvailable;
  setSelectedPaymentDetail: Dispatch<SetStateAction<PaymentOption>>;
  t: (x: string, y?: { [key: string]: string }) => string;
  isCCUI?: boolean;
  errorMessagePayment?: string;
  hotelBrand?: string;
  isA2cPaymentPage?: boolean;
  styles?: {
    containerStyles?: BoxProps;
  };
  disablePaymentOptions?: boolean;
  isPaymentRedesignEnabled?: boolean;
}

const PAYMENT_DETAILS = {
  PAY_NOW: 'PAY_NOW',
  PAY_ON_ARRIVAL: 'PAY_ON_ARRIVAL',
  RESERVE_WITHOUT_CARD: 'RESERVE_WITHOUT_CARD',
  RESERVE_WITH_CARD: 'RESERVED_WITH_CARD',
} as { [key: string]: string };

export default function PaymentDetails({
  isCCUI,
  hideHeader,
  selectedPaymentType,
  selectedPaymentDetail,
  setSelectedPaymentDetail,
  errorMessagePayment,
  hotelBrand,
  t,
  styles,
  disablePaymentOptions,
  isPaymentRedesignEnabled,
}: Readonly<Props>) {
  const { paymentOptions } = selectedPaymentType;
  const baseDataTestId = 'CardDetails';
  const getTypographyProps = useSemanticTypography();
  const { [FT_PI_PIB_CCUI_ENABLE_HUB_HOTELS_POA]: isHubPOAEnabled } = useFeatureToggle();

  const findAndSelectFirstOptionAvailableOfPaymentDetail = () => {
    if (
      selectedPaymentDetail.type === 'default' ||
      paymentOptions?.some(
        (m: PaymentOption) =>
          m?.type === selectedPaymentDetail.type && m?.enabled === selectedPaymentDetail.enabled
      )
    ) {
      return;
    }

    const firstOptionAvailable = paymentOptions?.find((m: PaymentOption) => m?.enabled);
    if (firstOptionAvailable) {
      setSelectedPaymentDetail(firstOptionAvailable);
    }
  };

  useEffect(() => {
    findAndSelectFirstOptionAvailableOfPaymentDetail();
  }, [selectedPaymentType]);

  const isGermanHotel = hotelBrand === HotelBrand.PID;
  const shouldHidePaymentDetails =
    !disablePaymentOptions && hotelBrand === HotelBrand.HUB && !isHubPOAEnabled;

  return (
    <Box
      {...{
        ...(isPaymentRedesignEnabled ? newPaymentDetailsWrapperStyle : paymentDetailsWrapperStyle),
        display: shouldHidePaymentDetails ? 'none' : 'block',
      }}
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
    >
      {!hideHeader && (
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'Title')}
          {...titleStyle}
          {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
        >
          {t('booking.payment.saverHeading')}
        </Text>
      )}
      {errorMessagePayment && (
        <Notification
          prefixDataTestId="Payment-Error"
          variant="error"
          status="error"
          description={<Box>{renderSanitizedHtml(errorMessagePayment)}</Box>}
          svg={<Info color="var(--chakra-colors-error)" />}
          wrapperStyles={errorNotificationStyle}
        />
      )}
      {shouldShowDisclaimer(selectedPaymentType, isCCUI) && (
        <Text
          data-testid={formatDataTestId(baseDataTestId, 'Disclaimer1')}
          mb="xl"
          {...getTypographyProps({}, disclaimerSemanticTypography)}
        >
          {t('paymentOptions.title')}
        </Text>
      )}
      {/* The notification is shown during Planet outage and Reserve without card is the only option shown */}
      {disablePaymentOptions && (
        <Notification
          description={t('paymentOptions.RESERVE_WITHOUT_CARD_NOTIFICATION')}
          maxWidth="full"
          status="warning"
          variant="alert"
          svg={<Alert />}
          wrapperStyles={{ mb: 'var(--chakra-space-lg)' }}
        />
      )}
      {selectedPaymentType.name !== '' && (
        <Box
          {...(isPaymentRedesignEnabled ? newRadioWrapperStyle : radioWrapperStyle)}
          {...styles?.containerStyles}
          data-testid={formatDataTestId(baseDataTestId, 'RadioContainer')}
          role="radiogroup"
          aria-label={t('booking.payment.saverHeading')}
        >
          <Flex
            sx={{ display: isPaymentRedesignEnabled ? 'flex' : 'contents' }}
            {...{ direction: { base: 'column', lg: 'row' }, wrap: 'wrap' }}
          >
            {paymentOptions?.map((option, index) => {
              const isRadioChecked = () => {
                return selectedPaymentDetail?.type === 'default'
                  ? isCCUI && paymentOptions?.filter((el) => el?.enabled)?.length === 2
                    ? false
                    : option ===
                      (paymentOptions?.find(
                        (el) => el?.type === PAYMENT_DETAILS.PAY_ON_ARRIVAL && el?.enabled
                      ) ?? paymentOptions?.find((el) => el?.enabled))
                  : option && selectedPaymentDetail?.type === PAYMENT_DETAILS[option.type];
              };
              const isSameSelectedOption =
                selectedPaymentDetail?.type === option?.type &&
                selectedPaymentDetail?.enabled === option?.enabled &&
                selectedPaymentDetail?.order === option?.order;

              if (isRadioChecked() && option?.type && !isSameSelectedOption) {
                setSelectedPaymentDetail(option);
              }
              return renderRadioButton(
                option,
                index,
                isRadioChecked,
                setSelectedPaymentDetail,
                paymentOptions,
                t,
                isCCUI,
                isGermanHotel,
                baseDataTestId,
                isPaymentRedesignEnabled
              );
            })}
          </Flex>
        </Box>
      )}
    </Box>
  );
}

const renderRadioButton = (
  option: PaymentOption,
  index: number,
  isRadioChecked: () => boolean,
  setSelectedPaymentDetail: Dispatch<SetStateAction<PaymentOption>>,
  paymentOptions: PaymentOption[],
  t: (x: string, y?: { [key: string]: string }) => string,
  isCCUI: boolean | undefined,
  isGermanHotel: boolean,
  baseDataTestId: string,
  isPaymentRedesignEnabled: boolean | undefined
) => {
  const WrapperComponent = isPaymentRedesignEnabled ? PaymentRadioButton : RadioButton;
  const wrapperProps = isPaymentRedesignEnabled
    ? { maximumWidth: `${100 / paymentOptions.length}%` }
    : {};

  return (
    <WrapperComponent
      {...wrapperProps}
      listIndex={paymentOptions && index === paymentOptions.length - 1 ? 'last' : index}
      onChange={() => setSelectedPaymentDetail(option)}
      isChecked={isRadioChecked()}
      isDisabled={!option?.enabled}
      key={option?.type}
      display="flex"
      type={option?.type}
      name="payment-detail"
      spacing="1rem"
    >
      <Flex direction="column" w="full" gap="xs">
        <PaymentOptionText option={option} t={t} isCCUI={isCCUI} baseDataTestId={baseDataTestId} />
        <PaymentOptionDescription
          option={option}
          t={t}
          isCCUI={isCCUI}
          isGermanHotel={isGermanHotel}
          baseDataTestId={baseDataTestId}
        />
      </Flex>
    </WrapperComponent>
  );
};

const PaymentOptionText = ({
  option,
  t,
  isCCUI,
  baseDataTestId,
}: {
  option: PaymentOption;
  t: (x: string, y?: { [key: string]: string }) => string;
  isCCUI: boolean | undefined;
  baseDataTestId: string;
}) => {
  const getTypographyProps = useSemanticTypography();

  return (
    <Text
      color="darkGrey1"
      {...getTypographyProps({ fontWeight: 'semibold' }, optionTextSemanticTypography)}
      data-testid={formatDataTestId(baseDataTestId, `payment-option-${option?.type}`)}
    >
      {isCCUI
        ? t(
            `ccui.paymentOption.${
              option?.type === PAYMENT_DETAILS.PAY_NOW ? 'payNow' : 'payOnArrival'
            }`
          )
        : t(`paymentOptions.${option?.type}`)}
    </Text>
  );
};

const PaymentOptionDescription = ({
  option,
  t,
  isCCUI,
  isGermanHotel,
  baseDataTestId,
}: {
  option: PaymentOption;
  t: (x: string, y?: { [key: string]: string }) => string;
  isCCUI: boolean | undefined;
  isGermanHotel: boolean;
  baseDataTestId: string;
}) => {
  const getTypographyProps = useSemanticTypography();

  if (!option?.type || isCCUI) return null;

  return (
    <Text
      data-testid={formatDataTestId(baseDataTestId, `payment-option-${option?.type}-desc`)}
      {...getTypographyProps({}, optionDescriptionSemanticTypography)}
    >
      {option?.type === PAYMENT_DETAILS.RESERVE_WITHOUT_CARD && !isGermanHotel
        ? t(`paymentOptions.${option?.type}_DESC_GB`)
        : t(`paymentOptions.${option?.type}_DESC`)}
    </Text>
  );
};

const shouldShowDisclaimer = (
  selectedPaymentType: PaymentMethodsAvailable,
  isCCUI: boolean | undefined
) =>
  (selectedPaymentType.name !== 'PIBA' && !isCCUI) ||
  (selectedPaymentType.type !== PiCardType.NEW_PIBA && !isCCUI);

const titleStyle = { display: 'inline-block', mb: 'md' };

const titleLegacyTypography = { fontSize: '2xl', fontWeight: 'semibold' };

const titleSemanticTypography = {
  textStyle: 'heading-m',
} as const;

const disclaimerSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const optionTextSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as const;

const optionDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;
const paymentDetailsWrapperStyle = {
  w: { mobile: 'full', md: '45rem', lg: '50.5rem', xl: '54rem' },
  mb: '5xl',
};
const radioWrapperStyle = {
  w: { mobile: 'full', sm: '25.063rem', md: '27.563rem', lg: '24.5rem', xl: '26.25rem' },
};

const newPaymentDetailsWrapperStyle = {
  w: 'full',
  mb: '5xl',
};
const newRadioWrapperStyle = {
  w: 'full',
};

const errorNotificationStyle = {
  mt: 'sm',
  mb: 'lg',
  sx: {
    a: {
      fontWeight: 'bold',
    },
  },
};

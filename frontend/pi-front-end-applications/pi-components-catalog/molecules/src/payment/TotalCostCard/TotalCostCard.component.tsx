import { Box, Text, Flex, BoxProps, FlexProps, Divider } from '@chakra-ui/react';
import {
  PaymentOption,
  RatePlanTotalCost,
  TermsAndCondition,
  AddressGuestInput,
  PaymentMethod,
  PAYPAL_PAYMENT,
  APGP_PAYMENT,
  paymentOptions,
} from '@whitbread-eos/api';
import {
  Card,
  Button,
  Notification,
  Info,
  PaypalWBButton,
  PaypalWBProps,
  PencePrice,
} from '@whitbread-eos/atoms';
import {
  useCustomLocale,
  formatDataTestId,
  formatUrlTermsConditions,
  displayPaymentHelpText,
  renderSanitizedHtml,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState, Dispatch, SetStateAction } from 'react';

interface Props extends BoxProps {
  ratePlan: RatePlanTotalCost;
  selectedPaymentDetail: PaymentOption;
  hotelName: string;
  isError: boolean;
  error: unknown;
  isLoading: boolean;
  data: TermsAndCondition;
  isBillingAddressDisplayed: boolean;
  errorMessagePayment?: string;
  continueToNextStep: (billingAddress?: AddressGuestInput) => void;
  isAmendPage?: boolean;
  setValidateQuestions?: Dispatch<SetStateAction<boolean>>;
  hasError?: boolean;
  selectedPaymentType?: PaymentMethod;
  paypalOptions?: PaypalWBProps;
  additionalAmountLabel?: string;
  isDatatransEnabled?: boolean;
  datatransPayPalButton?: React.ReactNode;
  datatransWalletButton?: React.ReactNode;
}

export default function TotalCostCard({
  hotelName,
  ratePlan,
  data,
  isError,
  isLoading,
  error,
  isBillingAddressDisplayed,
  selectedPaymentDetail,
  errorMessagePayment,
  continueToNextStep,
  isAmendPage,
  setValidateQuestions,
  hasError,
  selectedPaymentType,
  paypalOptions,
  additionalAmountLabel,
  isDatatransEnabled,
  datatransPayPalButton,
  datatransWalletButton,
}: Readonly<Props>) {
  const { currency: currencyCode, amount: netTotal } = ratePlan.totalCost;
  const [termsAndConditions, setTermsAndConditions] = useState('');

  const { t } = useTranslation(['common']);
  const { language: currentLang } = useCustomLocale();

  const baseDataTestId = 'TotalCost';
  const getTypographyProps = useSemanticTypography();

  useEffect(() => {
    if (!isLoading) {
      setTermsAndConditions(formatUrlTermsConditions(data?.termsAndConditions?.text));
    }
  }, [data, isLoading]);

  return (
    <Box data-testid={formatDataTestId(baseDataTestId, 'Container')} {...totalCostBoxStyle}>
      {isError ? (
        <Text>{(error as Error).message}</Text>
      ) : (
        <Card padding="xmd">
          <Flex {...totalCostFlexStyle}>
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'label')}
              {...totalCostTitleStyle}
              {...getTypographyProps(
                totalCostTitleLegacyTypography,
                totalCostTitleSemanticTypography
              )}
            >
              {isAmendPage ? additionalAmountLabel : t('booking.confirmation.totalCost')}
            </Text>
            <Text
              {...totalCostValueStyle}
              {...getTypographyProps(
                totalCostValueLegacyTypography,
                totalCostValueSemanticTypography
              )}
              data-testid={formatDataTestId(baseDataTestId, 'Currency')}
            >
              <PencePrice
                currency={currencyCode}
                price={parseFloat(netTotal)}
                language={currentLang}
                size="m"
              />
            </Text>
            <Text
              data-testid={formatDataTestId(baseDataTestId, 'HotelName')}
              {...hotelNameStyle}
              {...getTypographyProps(hotelNameLegacyTypography, hotelNameSemanticTypography)}
            >
              {hotelName}
            </Text>
            <Divider {...dividerStyle} />
            <Flex direction="column">
              {selectedPaymentDetail?.type !== paymentOptions.RESERVE_WITHOUT_CARD &&
                selectedPaymentType?.type !== paymentOptions.RESERVE_WITHOUT_CARD && (
                  <Text
                    data-testid={formatDataTestId(baseDataTestId, 'CVVNotification')}
                    {...informAboutCvvStyle}
                    {...getTypographyProps(
                      informAboutCvvLegacyTypography,
                      informAboutCvvSemanticTypography
                    )}
                  >
                    {selectedPaymentType
                      ? t(`${displayPaymentHelpText(selectedPaymentType)}`)
                      : t('cc.cvvRequired.info')}
                  </Text>
                )}
              <Box
                data-testid="termsAndConditions"
                sx={termsAndConditionsStyle}
                className="formatLinks"
                {...getTypographyProps(
                  termsAndConditionsLegacyTypography,
                  termsAndConditionsSemanticTypography
                )}
              >
                {renderSanitizedHtml(termsAndConditions)}
              </Box>
              {PAYPAL_PAYMENT === selectedPaymentType?.name ? (
                isDatatransEnabled ? (
                  datatransPayPalButton || null
                ) : (
                  <PaypalWBButton {...paypalOptions} />
                )
              ) : APGP_PAYMENT === selectedPaymentType?.type && isDatatransEnabled ? (
                datatransWalletButton || null
              ) : (
                <Button
                  type="submit"
                  form="billingAddressForm"
                  size="md"
                  {...buttonStyle}
                  data-testid="submitButton"
                  variant="primary"
                  onClick={onclickHandler}
                >
                  {selectedPaymentDetail?.type === paymentOptions.RESERVE_WITHOUT_CARD
                    ? t('ccui.payment.confirmBooking.button')
                    : isAmendPage
                      ? t('amend.confirmChanges')
                      : t('terms.continueText.paymentDetails')}
                </Button>
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
            </Flex>
          </Flex>
        </Card>
      )}
    </Box>
  );

  function onclickHandler() {
    setValidateQuestions?.(true);

    if (!isBillingAddressDisplayed) {
      !hasError && continueToNextStep();
    }
  }
}

export function renderPrice({
  currentLanguage = 'en',
  currency,
  price,
}: {
  currentLanguage: string | undefined;
  currency: string | undefined;
  price: number;
}) {
  const GBP_CURRENCY = new Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
  }).format(price);
  const EUR_CURRENCY = new Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'EUR',
  }).format(price);

  switch (currentLanguage) {
    case 'en': {
      switch (currency) {
        case 'GBP': {
          return GBP_CURRENCY;
        }
        case 'EUR': {
          return EUR_CURRENCY;
        }
        default: {
          return GBP_CURRENCY;
        }
      }
    }
    case 'de': {
      switch (currency) {
        case 'GBP': {
          return GBP_CURRENCY;
        }
        case 'EUR': {
          return new Intl.NumberFormat('de-DE', { style: 'currency', currency: 'EUR' }).format(
            price
          );
        }
        default: {
          return `${currency}${price.toFixed(2)}`;
        }
      }
    }
    default: {
      switch (currency) {
        case 'GBP': {
          return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'GBP' }).format(
            price
          );
        }
        case 'EUR': {
          return new Intl.NumberFormat('en-GB', { style: 'currency', currency: 'EUR' }).format(
            price
          );
        }
        default: {
          return GBP_CURRENCY;
        }
      }
    }
  }
}

const totalCostBoxStyle = {
  my: 'xl',
  w: { mobile: 'full', md: '45rem', lg: '50.5rem', xl: '54rem' },
};

const totalCostFlexStyle = {
  w: 'full',
  direction: 'column',
  p: { mobile: '0', md: 'xmd', lg: 'xmd', xl: 'xmd' },
} as FlexProps;

const totalCostTitleStyle = {
  mb: 'xs',
};

const totalCostTitleLegacyTypography = {
  lineHeight: '3',
  fontWeight: 'semibold',
  fontSize: 'md',
};

const totalCostTitleSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as const;

const totalCostValueStyle = {
  mb: 'sm',
};

const totalCostValueLegacyTypography = {
  lineHeight: '4',
  fontWeight: 'bold',
  fontSize: '3xl',
};

const totalCostValueSemanticTypography = {
  textStyle: 'heading-m',
} as const;

const hotelNameStyle = {};

const hotelNameLegacyTypography = {
  lineHeight: '3',
  fontWeight: 'semibold',
  fontSize: 'lg',
};

const hotelNameSemanticTypography = {
  textStyle: 'body-l-emphasis',
} as const;

const dividerStyle = {
  my: 'xl',
  borderColor: 'lightGrey2',
};

const informAboutCvvStyle = {
  mb: 'lg',
};

const informAboutCvvLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
};

const informAboutCvvSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const termsAndConditionsStyle = {
  a: {
    textDecoration: 'underline',
    color: 'zipSecondary',
  },
};

const termsAndConditionsLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
};

const termsAndConditionsSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const buttonStyle = {
  mt: 'lg',
  w: { mobile: 'full', lg: '18rem', xl: '19.3125rem' },
};

const errorNotificationStyle = {
  mt: 'lg',
  sx: {
    a: {
      fontWeight: 'bold',
    },
  },
};

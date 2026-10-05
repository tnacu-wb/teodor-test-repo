import { InfoOutlineIcon } from '@chakra-ui/icons';
import { Box, BoxProps, Flex, Image, Spinner, Text } from '@chakra-ui/react';
import {
  CardType,
  BusinessCardType,
  PaymentOption,
  PaymentMethod,
  PaymentMethods,
  Area,
  PiCardType,
  PAYPAL_PAYMENT,
  PIBA_NOT_ALLOWED_HOTEL_KEYS,
  FS_ENABLE_APPLEPAY_GOOGLEPAY,
  APPLE,
  GOOGLE,
  APGP_PAYMENT,
  PibaConditionalObj,
  PiCardSubType,
  FT_BB_ENABLE_PAYPAL,
  FT_PI_ENABLE_PAYPAL,
  paymentOptions,
  AcceptedCardType,
} from '@whitbread-eos/api';
import { Notification, Badge, RadioButton } from '@whitbread-eos/atoms';
import {
  formatAssetsUrl,
  formatDataTestId,
  getCardEnding,
  isUrl,
  displayMethodType,
  isApplePayConfigured,
  useFeatureToggle,
  useFeatureSwitch,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useEffect, useMemo, useState } from 'react';

interface Props extends BoxProps {
  data: PaymentMethods;
  isLoading: boolean;
  isError: boolean;
  error: unknown;
  onPaymentTypeClick: Dispatch<SetStateAction<PaymentMethod>>;
  selectedPaymentType: PaymentMethod;
  t: (x: string, y?: { [key: string]: string }) => string;
  disabledOptions?: string[];
  hiddenPaymentMethodTypes?: Array<{ name?: string; type: string }>;
  variant?: Area;
  amendPaymentCard?: PaymentMethod;
  styles?: {
    containerStyles?: BoxProps;
  };
  initialPaymentType?: string;
}

// Component only covers leisure users
export default function PaymentTypeLed({
  isLoading,
  isError,
  error,
  data,
  selectedPaymentType,
  onPaymentTypeClick,
  t,
  disabledOptions,
  hiddenPaymentMethodTypes,
  variant,
  amendPaymentCard,
  initialPaymentType,
  styles,
}: Readonly<Props>) {
  const baseDataTestId = 'PaymentTypeLed';
  const FT_ENABLE_PAYPAL = variant === Area.BB ? FT_BB_ENABLE_PAYPAL : FT_PI_ENABLE_PAYPAL;
  const { [FT_ENABLE_PAYPAL]: isPayPalEnabled } = useFeatureToggle();
  const isApplePayGooglePayEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_APPLEPAY_GOOGLEPAY,
    fallbackValue: false,
  });
  const [paymentData, setPaymentData] = useState(data);

  useEffect(() => {
    const firstEnabledMethod =
      amendPaymentCard ?? paymentData?.paymentMethods.find((pm: PaymentMethod) => pm.enabled);

    if (variant === Area.PI || variant === Area.BB) {
      firstEnabledMethod && onPaymentTypeClick(firstEnabledMethod);
    }
  }, [paymentData]);

  const isPaymentMethodEnabled = ({ type, enabled }: PaymentMethod) => {
    switch (type) {
      case PAYPAL_PAYMENT:
        return enabled && isPayPalEnabled;
      case APGP_PAYMENT:
        return enabled && isApplePayGooglePayEnabled;
      default:
        return enabled;
    }
  };

  useEffect(() => {
    if (initialPaymentType && paymentData) {
      const getInitialPaymentType = paymentData.paymentMethods.find(
        (pm: PaymentMethod) => pm.type === initialPaymentType
      );
      getInitialPaymentType && onPaymentTypeClick(getInitialPaymentType);
    }
  }, []);

  function removeApplePayGoolgePay() {
    const values = [APPLE, GOOGLE];
    const updateData = paymentData.paymentMethods.filter(
      (value: any) => !values.includes(value.name)
    );
    return updateData;
  }

  useEffect(() => {
    const applePay = paymentData.paymentMethods.find(
      (method: PaymentMethod) => method?.name === APPLE
    );
    const googlePay = paymentData.paymentMethods.find(
      (method: PaymentMethod) => method?.name === GOOGLE
    );

    const reserveWithoutCard = paymentData.paymentMethods.find((method: PaymentMethod) =>
      method?.paymentOptions?.some(
        (option: PaymentOption) =>
          option.type === paymentOptions.RESERVE_WITHOUT_CARD && option.enabled
      )
    );
    if (applePay || googlePay) {
      const pageUrl = window.location.href;
      const walletConfigured = !pageUrl.includes('localhost') ? isApplePayConfigured() : true;
      const acceptedCardTypesAPGP: CardType[] = [];
      const getPaymentMethod = (payment: PaymentMethod | undefined, type: string) => {
        if (!payment?.enabled) return null;

        return {
          type,
          name: type === 'AP' ? 'Apple Pay' : 'Google Pay',
          logoSrc: payment?.logoSrc ?? '',
        };
      };

      const applePayObj = getPaymentMethod(applePay, 'AP');
      const googlePayObj = getPaymentMethod(googlePay, 'GP');

      if (walletConfigured && applePayObj) {
        acceptedCardTypesAPGP.push(applePayObj);
      }

      if (googlePayObj) {
        acceptedCardTypesAPGP.push(googlePayObj);
      }

      const applePayGooglePayObject: PaymentMethod = {
        name: t('cc.APGP'),
        type: APGP_PAYMENT,
        subType: '',
        order: applePay?.order ?? googlePay?.order ?? 0,
        logoSrc: '',
        enabled: applePay?.enabled || googlePay?.enabled || false,
        cnpPreSelected: applePay?.cnpPreSelected || googlePay?.cnpPreSelected || false,
        cnpOptionAvailable: applePay?.cnpOptionAvailable || googlePay?.cnpOptionAvailable || false,
        acceptedCardTypes: acceptedCardTypesAPGP,
        card: applePay?.card || googlePay?.card || undefined,
        paymentOptions: applePay?.paymentOptions || googlePay?.paymentOptions || [],
        reasons: [],
      };

      const updatedPayment = removeApplePayGoolgePay();
      setPaymentData(() => ({
        paymentMethods: [...(updatedPayment || []), applePayGooglePayObject],
      }));
    }

    if (reserveWithoutCard) {
      const reserveWithoutCardObject: PaymentMethod = {
        name: paymentOptions.RESERVE_WITHOUT_CARD,
        type: paymentOptions.RESERVE_WITHOUT_CARD,
        subType: '',
        order: paymentData?.paymentMethods?.length + 1,
        logoSrc: '',
        enabled: reserveWithoutCard?.enabled || false,
        cnpPreSelected: reserveWithoutCard?.cnpPreSelected || false,
        cnpOptionAvailable: reserveWithoutCard?.cnpOptionAvailable || false,
        acceptedCardTypes: [],
        card: undefined,
        paymentOptions: [{ type: paymentOptions.RESERVE_WITHOUT_CARD, order: 1, enabled: true }],
        reasons: [],
      };

      setPaymentData((prev) => ({
        paymentMethods: [...prev.paymentMethods, reserveWithoutCardObject],
      }));
    }
  }, [data, t]);

  const disabledReasonsArray: string[] = useMemo(
    () => getDisabledReasonsArray(data?.paymentMethods, t, variant),
    [paymentData.paymentMethods, t]
  );

  if (isError) {
    return <>{(error as Error).message}</>;
  }

  const isPaymentMethodVisible = (paymentMethod: PaymentMethod) => {
    let isVisible = true;
    hiddenPaymentMethodTypes?.forEach((hiddenPaymentMethodType) => {
      if (hiddenPaymentMethodType?.type === paymentMethod.type) {
        if (hiddenPaymentMethodType?.name) {
          if (hiddenPaymentMethodType?.name === paymentMethod.name) {
            isVisible = false;
          }
        } else {
          isVisible = false;
        }
      }
    });

    return isVisible;
  };

  const getPIBALabelInfo = (subType: string | undefined) => {
    const pibaLabelInfoObj: PibaConditionalObj = {
      [PiCardSubType.PIBA_UK]: 'cc.NEW_PIBA.info',
      [PiCardSubType.PIBA_EU]: 'cc.NEW_PIBA_EURO.info',
    };
    const pibaLabelText = subType ? pibaLabelInfoObj[subType] : '';
    return pibaLabelText;
  };

  return (
    <Box
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      {...paymentTypeWrapperStyle}
      {...styles?.containerStyles}
    >
      <Text {...titleStyle} data-testid="payment-type-method_title">
        {t('cc.title.selectPaymentMethod')}
      </Text>
      <Text {...textColor} mb="xl" data-testid="payment-type-method_description">
        {t('cc.subTitle.ChooseHowToPay')}
      </Text>
      {isError && <Text data-testid="payment-type-method_error">{(error as Error).message}</Text>}
      {isLoading ? (
        <Box data-testid="loading">
          <Spinner />
        </Box>
      ) : (
        <>
          {Boolean(disabledReasonsArray?.length) &&
            disabledReasonsArray.map((reason: string, index: number) => (
              <Box
                data-testid={formatDataTestId(baseDataTestId, `ReasonNotification-${index}`)}
                mb="md"
                key={reason}
              >
                <Notification
                  svg={<InfoOutlineIcon />}
                  status="info"
                  description={reason}
                  variant="info"
                  data-testid={formatDataTestId(baseDataTestId, `ReasonNotificationModal-${index}`)}
                  className="assist-no-show"
                />
              </Box>
            ))}

          <Box data-testid="payment-type-method_option-wrapper">
            {amendPaymentCard && renderRadioBtn(amendPaymentCard, 0)}
            {paymentData?.paymentMethods
              .filter((pm: PaymentMethod) => isPaymentMethodEnabled(pm))
              .map((method: PaymentMethod, index: number) => {
                return (
                  isPaymentMethodVisible(method) &&
                  method.enabled && (
                    <Box
                      data-testid={formatDataTestId(
                        'payment-type-method_option',
                        method.name.replace(/ /g, '_')
                      )}
                      key={method.name}
                      className={
                        method.type === PiCardType.SAVED_CARD
                          ? 'sessioncamhidetext assist-no-show'
                          : ''
                      }
                    >
                      {renderRadioBtn(method, index)}
                    </Box>
                  )
                );
              })}
          </Box>
        </>
      )}
    </Box>
  );

  function renderRadioBtn(method: PaymentMethod, index: number) {
    const isDisabled = !method?.paymentOptions?.some((m: PaymentOption) => {
      return m.enabled;
    });

    const visibleAndEnabledPaymentMethods = paymentData.paymentMethods.filter(
      (pm: PaymentMethod) => isPaymentMethodEnabled(pm) && isPaymentMethodVisible(pm)
    );

    const uniquePaymentOptions: string[] = [];
    for (const method of paymentData.paymentMethods) {
      method.enabled &&
        method?.paymentOptions?.forEach((option) => {
          if (option.type !== paymentOptions.RESERVE_WITHOUT_CARD) {
            option.enabled === true &&
              !uniquePaymentOptions.includes(option.type) &&
              uniquePaymentOptions.push(option.type);
          }
        });
    }

    const paymentOptionLeds: string[] | undefined = method?.paymentOptions
      ?.filter((options) => options.enabled)
      .map((option) => {
        return option.type;
      });

    const renderPaymentLeds = () => {
      return uniquePaymentOptions?.length === 1 ? null : (
        <Flex direction="column" gap="xs" justifyContent="center" alignItems="end" height="full">
          <Box>
            {paymentOptionLeds?.includes(paymentOptions.PAY_NOW) && (
              <Badge {...badgeStyles} variant="secondary">
                {t('paymentOptions.PAY_NOW.shortLabel')}
              </Badge>
            )}
          </Box>
          <Box>
            {[paymentOptions.PAY_ON_ARRIVAL, paymentOptions.RESERVE_WITHOUT_CARD].some((option) =>
              paymentOptionLeds?.includes(option)
            ) && (
              <Badge {...badgeStyles} variant="secondary" color="primary" borderColor="primary">
                {t('paymentOptions.PAY_ON_ARRIVAL.shortLabel')}
              </Badge>
            )}
          </Box>
        </Flex>
      );
    };

    const listIndex = index >= visibleAndEnabledPaymentMethods.length - 1 ? 'last' : index;

    const isRadioDisabled =
      (amendPaymentCard && !amendPaymentCard?.enabled) ||
      (amendPaymentCard && method !== amendPaymentCard) ||
      disabledOptions?.includes(method.type) ||
      isDisabled;

    const isRadioChecked = isRadioDisabled
      ? false
      : selectedPaymentType && method.order === selectedPaymentType.order;
    return (
      <Box
        sx={{
          '.chakra-radio__label': {
            w: 'full',
          },
        }}
      >
        <RadioButton
          type={`payment-type-radio-${index}`}
          {...radioBtnStyle}
          listIndex={listIndex}
          onChange={() => {
            onPaymentTypeClick(method);
          }}
          isDisabled={isRadioDisabled}
          isChecked={isRadioChecked}
        >
          <Flex justifyContent={'space-between'}>
            <Box
              data-testid={`payment-type-method_option-entire-subdiv-${index}`}
              {...radioLabelWrapper}
            >
              <Flex
                py="3px"
                justifyContent="space-between"
                data-testid="payment-type-method_option-subdiv-with-text"
                w={'full'}
              >
                <Text
                  {...textColor}
                  fontWeight={isRadioChecked ? 'semibold' : 'normal'}
                  data-testid={`payment-type-method_option-text-${method.name}`}
                >
                  {t(`${displayMethodType(method, variant)}`)}
                  {(method.type === 'SAVED_CARD' || method.type === 'AMEND_SAVED_CARD') &&
                    ` (${getCardEnding(method?.card?.cardNumber)})`}
                </Text>
                {method.card?.logoSrc && (
                  <Image
                    {...radioImgStyle}
                    src={formatAssetsUrl(method.card?.logoSrc)}
                    alt={method.card?.cardType}
                  />
                )}
              </Flex>
              {method.type === PiCardType.NEW_PIBA && (
                <Text>{t(getPIBALabelInfo(method.subType))}</Text>
              )}
              {method.card && (
                <>
                  <Text {...textColor}>{method.card.cardHolderName}</Text>
                  <Text {...textColor} mb="sm">
                    {t('booking.expiryDateShort')} {method.card.expiryMonth}/
                    {method.card.expiryYear}
                  </Text>
                </>
              )}
              {method.type === 'SAVED_CARD' && variant === Area.BB && (
                <Badge {...badgeStyles} variant="secondary">
                  {t(`${displayMethodType(method, variant)}`)}
                </Badge>
              )}
              {method.acceptedCardTypes && (
                <Flex dir="column" flexWrap="wrap" py="1px">
                  {method.acceptedCardTypes.map((el: AcceptedCardType) => (
                    <Image
                      {...radioImgStyle}
                      src={isUrl(el.logoSrc) ? el.logoSrc : formatAssetsUrl(el.logoSrc as string)}
                      alt={el.name}
                      key={`${method.name}-${el.logoSrc}`}
                    />
                  ))}
                </Flex>
              )}
            </Box>
            <Box> {renderPaymentLeds()}</Box>
          </Flex>
        </RadioButton>
      </Box>
    );
  }
}

function getDisabledReasonsArray(
  paymentMethods: PaymentMethod[],
  t: any,
  variant: Area | undefined
) {
  return paymentMethods
    .filter(
      (pm: PaymentMethod) =>
        pm?.reasons?.length && pm.type !== PiCardType.NEW_PIBA && pm.type !== PiCardType.NEW_CARD
    )
    .map((pm: PaymentMethod) => [pm?.reasons?.[0], pm?.card, pm.reasons]) // E.g. [[reason[0], {...}/null],...]
    .reduce((acc: any, curr: any) => {
      let updated;
      const reasons = [...curr[2]];
      // If the label contains cardEndings, there is the expectation that there is a cardNumber to be displayed.
      if (t(`cc.na.${curr[0]}`).includes('cardEnding')) {
        // Only for this key there is also a bb tag that is used for BUSINESS_CENTRALLY_STORED_CARD key
        if (
          curr[0] === 'CARD_EXPIRED_BEFORE_DEPARTURE' &&
          curr[1]?.cardType === BusinessCardType.BUSINESS_CENTRALLY_STORED_CARD
        ) {
          updated = t(`cc.na.${curr[0]}.bb`).replace(
            '{cardEnding}',
            getCardEnding(curr[1]?.cardNumber) ?? ''
          );
        } else if (isPibaNotAllowedForHotel(reasons, variant)) {
          updated = getPibaNotificationText(curr, reasons, t);
        } else {
          updated = t(`cc.na.${curr[0]}`).replace(
            '{cardEnding}',
            getCardEnding(curr[1]?.cardNumber) ?? ''
          );
        }
      } else {
        updated = t(`cc.na.${curr[0]}`);
      }

      acc.push(updated);
      return acc;
    }, [])
    .filter((x: string | undefined, i: any, a: any) => x && a.indexOf(x) == i);
}

function isPibaNotAllowedForHotel(reasons: string[], variant: Area | undefined) {
  const pibaNotAllowedReasons = [
    PIBA_NOT_ALLOWED_HOTEL_KEYS.PIBA_EU_ALLOWED_ONLY_IN_EU,
    PIBA_NOT_ALLOWED_HOTEL_KEYS.PIBA_UK_ALLOWED_ONLY_IN_UK,
  ];
  return variant === Area.BB && reasons.some((reason) => pibaNotAllowedReasons.includes(reason));
}

function getPibaNotificationText(curr: any, reasons: string[], t: any) {
  const pibaNotAllowedTextObj: PibaConditionalObj = {
    PIBA_EU_ALLOWED_ONLY_IN_EU: t(
      `cc.na.${PIBA_NOT_ALLOWED_HOTEL_KEYS.PIBA_EU_ALLOWED_ONLY_IN_EU}`
    ),
    PIBA_UK_ALLOWED_ONLY_IN_UK: t(
      `cc.na.${PIBA_NOT_ALLOWED_HOTEL_KEYS.PIBA_UK_ALLOWED_ONLY_IN_UK}`
    ),
  };
  let updatedValue;
  reasons.forEach((reason: string) => {
    updatedValue = pibaNotAllowedTextObj[reason]?.replace(
      '{cardEnding}',
      getCardEnding(curr[1]?.cardNumber) ?? ''
    );
  });

  return updatedValue;
}

const titleStyle = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  mb: 'md',
  color: 'darkGrey1',
  lineHeight: '2rem',
};

const radioBtnStyle = {
  h: 'fit-content',
  display: 'flex',
  w: 'full',
};

const radioImgStyle = {
  w: '10',
  h: '6',
  mr: 'xs',
  mb: 'xs',
  mt: 'xs',
};

const paymentTypeWrapperStyle = {
  w: { mobile: 'full', xs: 'full', sm: '26.3rem', md: '27.563rem', lg: '24.5rem', xl: '26.25rem' },
  mb: '5xl',
};

const radioLabelWrapper = {
  w: { mobile: '5.3rem', xs: '12rem', md: 'full' },
  ml: 'sm',
};

const badgeStyles = {
  lineHeight: '2',
  display: 'flex',
  width: 'fit-content',
  alignItems: 'center',
  borderColor: 'maroon',
  color: 'maroon',
};

const textColor = {
  color: 'darkGrey1',
};

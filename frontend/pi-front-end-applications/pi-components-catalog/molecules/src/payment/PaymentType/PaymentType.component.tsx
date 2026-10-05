import { InfoOutlineIcon } from '@chakra-ui/icons';
import { Box, BoxProps, Flex, Image, Spinner, StyleProps, Text } from '@chakra-ui/react';
import {
  AcceptedCardType,
  APGP_PAYMENT,
  APPLE,
  Area,
  BusinessCardType,
  FT_BB_ENABLE_PAYPAL,
  FT_PI_ENABLE_PAYPAL,
  FS_ENABLE_APPLEPAY_GOOGLEPAY,
  GOOGLE,
  PaymentMethod,
  PaymentMethods,
  PaymentOption,
  paymentOptions,
  PAYPAL_PAYMENT,
  PIBA_NOT_ALLOWED_HOTEL_KEYS,
  PibaConditionalObj,
  PiCardSubType,
  PiCardType,
} from '@whitbread-eos/api';
import { Badge, Notification, PaymentRadioButton, RadioButton } from '@whitbread-eos/atoms';
import {
  displayMethodType,
  formatAssetsUrl,
  formatDataTestId,
  getCardEnding,
  isApplePayConfigured,
  isUrl,
  useFeatureSwitch,
  useFeatureToggle,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { Dispatch, JSX, SetStateAction, useEffect, useMemo, useRef, useState } from 'react';

interface Props extends BoxProps {
  data: PaymentMethods;
  isLoading: boolean;
  isError: boolean;
  error: unknown;
  onPaymentTypeClick: Dispatch<SetStateAction<PaymentMethod>>;
  selectedPaymentType: PaymentMethod;
  selectedPaymentDetail: PaymentOption;
  t: (x: string, y?: { [key: string]: string }) => string;
  disabledOptions?: string[];
  hiddenPaymentMethodTypes?: Array<{ name?: string; type: string }>;
  variant?: Area;
  amendPaymentCard?: PaymentMethod;
  styles?: {
    containerStyles?: BoxProps;
  };
  initialPaymentType?: string;
  isUsedWithTabs?: boolean;
  onlyShowPayNow?: boolean;
  isPaymentRedesignEnabled?: boolean;
}

// Component only covers leisure users
export default function PaymentType({
  isLoading,
  isError,
  error,
  data,
  selectedPaymentType,
  selectedPaymentDetail,
  onPaymentTypeClick,
  t,
  disabledOptions,
  hiddenPaymentMethodTypes,
  variant,
  amendPaymentCard,
  initialPaymentType,
  styles,
  isUsedWithTabs,
  onlyShowPayNow,
  isPaymentRedesignEnabled,
}: Readonly<Props>) {
  const baseDataTestId = 'PaymentType';
  const getTypographyProps = useSemanticTypography();
  const FT_ENABLE_PAYPAL = variant === Area.BB ? FT_BB_ENABLE_PAYPAL : FT_PI_ENABLE_PAYPAL;
  const { [FT_ENABLE_PAYPAL]: isPayPalEnabled } = useFeatureToggle();
  const isApplePayGooglePayEnabled = useFeatureSwitch({
    featureSwitchKey: FS_ENABLE_APPLEPAY_GOOGLEPAY,
    fallbackValue: false,
  });
  const [paymentData, setPaymentData] = useState(data);
  const isFirstRenderRef = useRef(true);
  const hasSelectedFirstMethodRef = useRef(false);

  // Sync paymentData when data prop changes
  useEffect(() => {
    setPaymentData(data);
  }, [data]);

  // Refresh selectedPaymentType when payment options change (e.g., upgrade to flex)
  useEffect(() => {
    // Skip on first render - let the initial selection effect handle it
    if (isFirstRenderRef.current) {
      isFirstRenderRef.current = false;
      return;
    }

    if (
      !selectedPaymentType?.name ||
      selectedPaymentType.name === '' ||
      !selectedPaymentType?.paymentOptions ||
      !data?.paymentMethods
    ) {
      return;
    }

    // Find matching payment method in fresh data
    const freshMethod = data.paymentMethods.find(
      (m: PaymentMethod) =>
        m.name === selectedPaymentType.name &&
        m.type === selectedPaymentType.type &&
        (m.subType ?? '') === (selectedPaymentType.subType ?? '') &&
        (m.card?.token ?? '') === (selectedPaymentType.card?.token ?? '') &&
        m.order === selectedPaymentType.order
    );

    if (!freshMethod) return;

    const current = selectedPaymentType.paymentOptions || [];
    const fresh = freshMethod.paymentOptions || [];

    // Update if enabled states differ
    const hasChanges =
      current.length !== fresh.length ||
      current.some((opt: PaymentOption, i: number) => opt.enabled !== fresh[i]?.enabled);

    if (hasChanges) {
      onPaymentTypeClick(freshMethod);
    }
  }, [data, selectedPaymentType, onPaymentTypeClick]);

  useEffect(() => {
    // Only select first method on initial load, not on subsequent paymentData changes
    if (hasSelectedFirstMethodRef.current) {
      return;
    }

    const firstEnabledMethod =
      amendPaymentCard ?? paymentData?.paymentMethods?.find((pm: PaymentMethod) => pm.enabled);

    if (selectedPaymentType.name === '' && variant === Area.CCUI) {
      firstEnabledMethod && onPaymentTypeClick(firstEnabledMethod);
      hasSelectedFirstMethodRef.current = true;
    }
    if (variant === Area.PI || variant === Area.BB) {
      firstEnabledMethod && onPaymentTypeClick(firstEnabledMethod);
      hasSelectedFirstMethodRef.current = true;
    }
  }, [paymentData]);

  useEffect(() => {
    if (isUsedWithTabs && (variant === Area.PI || variant === Area.BB)) {
      handleInitialPaymentType();
    }
  }, [selectedPaymentDetail.type]);

  const handleInitialPaymentType = () => {
    const initialPaymentDetail =
      selectedPaymentDetail.type === 'default'
        ? paymentOptions.PAY_ON_ARRIVAL
        : selectedPaymentDetail.type;
    const selectedPaymentMethod =
      amendPaymentCard ??
      paymentData.paymentMethods?.find((pm: PaymentMethod) =>
        pm?.paymentOptions?.some(
          (option: PaymentOption) => option.type === initialPaymentDetail && option.enabled
        )
      );
    selectedPaymentMethod && onPaymentTypeClick(selectedPaymentMethod);
  };

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

  const findAndSelectFirstOptionAvailableOfPaymentType = () => {
    // For CCUI, always check the latest data prop to get fresh payment method configuration
    const methodsToCheck = data?.paymentMethods || paymentData?.paymentMethods || [];

    //if selected option is available for the new payment details then is not needed to select new value
    if (
      selectedPaymentType?.paymentOptions?.some(
        (option) => option?.enabled && option.type === selectedPaymentDetail?.type
      )
    ) {
      return;
    }
    const firstOptionAvailable = methodsToCheck.find(
      (method: PaymentMethod) =>
        method?.paymentOptions?.some(
          (option: PaymentOption) => option?.enabled && option.type === selectedPaymentDetail?.type
        ) && isPaymentMethodVisible(method)
    );
    //if already selected then return
    if (
      firstOptionAvailable?.name === selectedPaymentType?.name ||
      (!firstOptionAvailable && selectedPaymentType?.name === '')
    )
      return;
    if (firstOptionAvailable && firstOptionAvailable?.name !== selectedPaymentType?.name) {
      onPaymentTypeClick(firstOptionAvailable);
    }
  };

  useEffect(() => {
    if (variant === Area.CCUI) {
      findAndSelectFirstOptionAvailableOfPaymentType();
    }
  }, [selectedPaymentDetail]);

  useEffect(() => {
    if (initialPaymentType && paymentData) {
      const getInitialPaymentType = paymentData.paymentMethods?.find(
        (pm: PaymentMethod) => pm.type === initialPaymentType
      );
      getInitialPaymentType && onPaymentTypeClick(getInitialPaymentType);
    }
  }, [initialPaymentType]);

  function removeApplePayGoolgePay() {
    const values = [APPLE, GOOGLE];
    const updateData = paymentData.paymentMethods?.filter(
      (value: any) => !values.includes(value.name)
    );
    return updateData;
  }

  function getPaymentObject(
    applePay: any,
    googlePay: any,
    acceptedCardTypesAPGP: AcceptedCardType[]
  ) {
    const params = [
      'order',
      'enabled',
      'cnpPreSelected',
      'cnpOptionAvailable',
      'card',
      'paymentOptions',
    ];
    const typeOfPayment = applePay ?? googlePay;
    const paymentParams = params.reduce((acc: any, param: string) => {
      acc[param] = typeOfPayment[param];
      return acc;
    }, {});

    return {
      name: t('cc.APGP'),
      type: APGP_PAYMENT,
      subType: '',
      logoSrc: '',
      acceptedCardTypes: acceptedCardTypesAPGP,
      reasons: [],
      ...paymentParams,
    };
  }

  useEffect(() => {
    const applePay = paymentData.paymentMethods?.find(
      (method: PaymentMethod) => method?.name === APPLE
    );
    const googlePay = paymentData.paymentMethods?.find(
      (method: PaymentMethod) => method?.name === GOOGLE
    );
    const reserveWithoutCard = paymentData.paymentMethods?.find((method: PaymentMethod) =>
      method?.paymentOptions?.some(
        (option: PaymentOption) =>
          option.type === paymentOptions.RESERVE_WITHOUT_CARD && option.enabled
      )
    );
    const hasReserveWithoutCardMethod = paymentData.paymentMethods?.some(
      (method: PaymentMethod) => method?.type === paymentOptions.RESERVE_WITHOUT_CARD
    );

    if (applePay || googlePay) {
      const pageUrl = window.location.href;
      const walletConfigured = !pageUrl.includes('localhost') ? isApplePayConfigured() : true;
      const acceptedCardTypesAPGP: AcceptedCardType[] = [];
      if (walletConfigured && applePay?.enabled) {
        acceptedCardTypesAPGP.push({
          type: 'AP',
          name: 'Apple Pay',
          logoSrc: applePay?.logoSrc as string,
        });
      }
      if (googlePay?.enabled) {
        acceptedCardTypesAPGP.push({
          type: 'GP',
          name: 'Google Pay',
          logoSrc: googlePay?.logoSrc as string,
        });
      }

      const updatedPayment = removeApplePayGoolgePay();
      setPaymentData(() => ({
        paymentMethods: [
          ...(updatedPayment || []),
          getPaymentObject(applePay, googlePay, acceptedCardTypesAPGP),
        ],
      }));
    }

    if (isUsedWithTabs && reserveWithoutCard && !hasReserveWithoutCardMethod) {
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
  }, [paymentData, t, isUsedWithTabs]);

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

  const isPaymentMethodInTab = ({ paymentOptions: options }: PaymentMethod) => {
    if (isUsedWithTabs) {
      const result = options?.find((option) => {
        if (
          option?.type === paymentOptions.RESERVE_WITHOUT_CARD &&
          selectedPaymentDetail.type === paymentOptions.PAY_ON_ARRIVAL
        ) {
          return option.enabled;
        }
        return selectedPaymentDetail.type === option.type && option.enabled;
      });
      return result?.enabled;
    }
    return true;
  };

  const getPIBALabelInfo = (subType: string | undefined) => {
    const pibaLabelInfoObj: PibaConditionalObj = {
      [PiCardSubType.PIBA_UK]: 'cc.NEW_PIBA.info',
      [PiCardSubType.PIBA_EU]: 'cc.NEW_PIBA_EURO.info',
    };
    const pibaLabelText = subType ? pibaLabelInfoObj[subType] : '';
    return pibaLabelText;
  };

  let wrapperStyle: StyleProps = paymentTypeWrapperStyle(isPaymentRedesignEnabled);
  let title: JSX.Element | null = (
    <Text
      {...titleStyle}
      {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
      data-testid="payment-type-method_title"
    >
      {t('cc.title')}
    </Text>
  );

  if (isUsedWithTabs) {
    title = null;
    wrapperStyle = paymentTypeTabsWrapperStyle;

    if (onlyShowPayNow) {
      title = (
        <Text
          {...titleStyle}
          {...getTypographyProps(titleLegacyTypography, titleSemanticTypography)}
          data-testid="payment-type-method_title"
        >
          {t('cc.title.selectPaymentMethod')}
        </Text>
      );
      wrapperStyle = tabsWrapperStylePayNowOnly;
    }
  }

  let subtitle = 'cc.subTitle';

  if (isUsedWithTabs) {
    if (selectedPaymentDetail.type === paymentOptions.PAY_NOW) {
      subtitle = 'paymentOptions.PAY_NOW_DESC';
    }
    if (selectedPaymentDetail.type === paymentOptions.PAY_ON_ARRIVAL) {
      subtitle = 'paymentOptions.PAY_ON_ARRIVAL_DESC_LONG';
    }
  }

  return (
    <Box
      data-testid={formatDataTestId(baseDataTestId, 'Container')}
      {...wrapperStyle}
      {...styles?.containerStyles}
    >
      {title}
      <Text
        {...textColor}
        mb={isUsedWithTabs && !onlyShowPayNow ? 'lg' : 'xl'}
        {...getTypographyProps({}, descriptionSemanticTypography)}
        data-testid="payment-type-method_description"
      >
        {t(subtitle)}
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

          <Box
            data-testid="payment-type-method_option-wrapper"
            role="radiogroup"
            aria-label={t('cc.title')}
          >
            {amendPaymentCard && renderRadioBtn(amendPaymentCard, 0)}
            {paymentData?.paymentMethods
              ?.filter(
                (pm: PaymentMethod) =>
                  isPaymentMethodEnabled(pm) &&
                  isPaymentMethodVisible(pm) &&
                  pm.enabled &&
                  isPaymentMethodInTab(pm)
              )
              .map((method: PaymentMethod, index: number) => {
                return renderRadioBtn(method, index);
              })}
          </Box>
        </>
      )}
    </Box>
  );

  function renderRadioBtn(method: PaymentMethod, index: number) {
    const isDisabled = !method.paymentOptions?.some((m: PaymentOption) => {
      return m?.enabled;
    });

    const visibleAndEnabledPaymentMethods = paymentData.paymentMethods?.filter(
      (pm: PaymentMethod) =>
        isPaymentMethodEnabled(pm) && isPaymentMethodVisible(pm) && isPaymentMethodInTab(pm)
    );
    const listIndex = index >= visibleAndEnabledPaymentMethods.length - 1 ? 'last' : index;

    const isRadioDisabled =
      (amendPaymentCard && !amendPaymentCard?.enabled) ||
      (amendPaymentCard && method !== amendPaymentCard) ||
      disabledOptions?.includes(method.type) ||
      isDisabled;

    const isRadioChecked = isRadioDisabled
      ? false
      : selectedPaymentType && method.order === selectedPaymentType.order;

    const radioOptionLegacyTypography = { fontWeight: isRadioChecked ? 'semibold' : 'normal' };
    const radioOptionSemanticTypography = {
      textStyle: isRadioChecked ? 'body-m-emphasis' : 'body-m-regular',
    } as const;

    if (!isPaymentRedesignEnabled)
      return (
        <RadioButton
          type={`payment-type-radio-${index}`}
          {...radioBtnStyle}
          listIndex={listIndex}
          name="payment-type"
          onChange={() => {
            onPaymentTypeClick(method);
          }}
          isDisabled={isRadioDisabled}
          isChecked={isRadioChecked}
          data-testid={formatDataTestId(
            'payment-type-radio_option',
            method.name.replace(/ /g, '_')
          )}
        >
          <Box
            data-testid={formatDataTestId(
              'payment-type-method_option',
              method.name.replace(/ /g, '_')
            )}
            key={method.name}
            className={
              method.type === PiCardType.SAVED_CARD ? 'sessioncamhidetext assist-no-show' : ''
            }
          >
            <Box ml="sm" data-testid={`payment-type-method_option-entire-subdiv-${index}`}>
              <Box py="3px" data-testid="payment-type-method_option-subdiv-with-text">
                <Text
                  {...textColor}
                  {...getTypographyProps(
                    radioOptionLegacyTypography,
                    radioOptionSemanticTypography
                  )}
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
              </Box>
              {method.type === PiCardType.NEW_PIBA && (
                <Text {...getTypographyProps({}, cardDetailSemanticTypography)}>
                  {t(getPIBALabelInfo(method.subType as string))}
                </Text>
              )}
              {method.card && (
                <>
                  <Text {...textColor} {...getTypographyProps({}, cardDetailSemanticTypography)}>
                    {method.card.cardHolderName}
                  </Text>
                  <Text
                    {...textColor}
                    mb="sm"
                    {...getTypographyProps({}, cardDetailSemanticTypography)}
                  >
                    {t('booking.expiryDateShort')} {method.card.expiryMonth}/
                    {method.card.expiryYear}
                  </Text>
                </>
              )}
              {method.type === 'SAVED_CARD' && variant === Area.BB && (
                <Badge {...badgeStyles} variant="secondary">
                  {t(`${displayMethodType(method, Area.BB)}`)}
                </Badge>
              )}
              {method.acceptedCardTypes && (
                <Flex dir="column" flexWrap="wrap" py="1px">
                  {method.acceptedCardTypes?.map((el: AcceptedCardType) => (
                    <Image
                      {...radioImgStyle}
                      src={
                        isUrl(el?.logoSrc) ? el?.logoSrc : formatAssetsUrl(el?.logoSrc as string)
                      }
                      alt={el?.name}
                      key={`${method.name}-${el?.logoSrc}`}
                    />
                  ))}
                </Flex>
              )}
            </Box>
          </Box>
        </RadioButton>
      );
    if (isPaymentRedesignEnabled)
      return (
        <PaymentRadioButton
          type={`payment-type-radio-${index}`}
          listIndex={listIndex}
          name="payment-type"
          onChange={() => {
            onPaymentTypeClick(method);
          }}
          isDisabled={isRadioDisabled}
          isChecked={isRadioChecked}
          data-testid={formatDataTestId(
            'payment-type-radio_option',
            method.name.replace(/ /g, '_')
          )}
        >
          <Box
            data-testid={formatDataTestId(
              'payment-type-method_option',
              method.name.replace(/ /g, '_')
            )}
            key={method.name}
            className={
              method.type === PiCardType.SAVED_CARD ? 'sessioncamhidetext assist-no-show' : ''
            }
          >
            <Flex ml="sm" data-testid={`payment-type-method_option-entire-subdiv-${index}`}>
              <Box py="3px" data-testid="payment-type-method_option-subdiv-with-text">
                <Flex direction="column">
                  <Text
                    {...textColor}
                    display={method.type === 'PAYPAL' || method.type === 'APGP' ? 'flex' : 'block'}
                    {...getTypographyProps(
                      radioOptionLegacyTypography,
                      radioOptionSemanticTypography
                    )}
                    data-testid={`payment-type-method_option-text-${method.name}`}
                  >
                    {t(`${displayMethodType(method, variant)}`)}
                    {(method.type === 'SAVED_CARD' || method.type === 'AMEND_SAVED_CARD') &&
                      ` (${getCardEnding(method?.card?.cardNumber)})`}
                  </Text>
                  {method.card && (
                    <>
                      <Text
                        {...textColor}
                        {...getTypographyProps({}, cardDetailSemanticTypography)}
                      >
                        {method.card.cardHolderName}
                      </Text>
                      <Text
                        {...textColor}
                        mb="sm"
                        {...getTypographyProps({}, cardDetailSemanticTypography)}
                      >
                        {t('booking.expiryDateShort')} {method.card.expiryMonth}/
                        {method.card.expiryYear}
                      </Text>
                    </>
                  )}
                  {method.type === PiCardType.NEW_PIBA && (
                    <Text {...getTypographyProps({}, cardDetailSemanticTypography)}>
                      {t(getPIBALabelInfo(method.subType as string))}
                    </Text>
                  )}
                </Flex>
              </Box>
              <Flex
                wrap="wrap"
                alignItems="flex-end"
                position="absolute"
                right="0"
                gap="1.6rem"
                direction="column"
              >
                {method.card?.logoSrc && (
                  <Image
                    {...radioImgStyle}
                    src={formatAssetsUrl(method.card?.logoSrc)}
                    alt={method.card?.cardType}
                  />
                )}
                {method.type === 'SAVED_CARD' && variant === Area.BB && (
                  <Badge {...badgeStyles} variant="secondary">
                    {t(`${displayMethodType(method, variant)}`)}
                  </Badge>
                )}
              </Flex>

              {method.acceptedCardTypes && (
                <Flex
                  wrap="wrap"
                  py="1px"
                  position={
                    method.type === 'PAYPAL' || method.type === 'APGP'
                      ? 'absolute'
                      : { base: 'static', sm: 'absolute', md: 'absolute' }
                  }
                  justifyContent={
                    (method.type === 'NEW_CARD' || method.type === 'NEW_PIBA') &&
                    variant === Area.BB
                      ? 'flex-end'
                      : undefined
                  }
                  top="0"
                  right="0"
                >
                  {method.acceptedCardTypes?.map((el: AcceptedCardType) => (
                    <Image
                      {...radioImgStyle}
                      src={
                        isUrl(el?.logoSrc) ? el?.logoSrc : formatAssetsUrl(el?.logoSrc as string)
                      }
                      alt={el?.name}
                      key={`${method.name}-${el?.logoSrc}`}
                    />
                  ))}
                </Flex>
              )}
            </Flex>
          </Box>
        </PaymentRadioButton>
      );
  }
}

function getDisabledReasonsArray(
  paymentMethods: PaymentMethod[],
  t: any,
  variant: Area | undefined
) {
  return paymentMethods
    ?.filter(
      (pm: PaymentMethod) =>
        pm?.reasons?.length && pm.type !== PiCardType.NEW_PIBA && pm.type !== PiCardType.NEW_CARD
    )
    .map((pm: PaymentMethod) => pm && [pm.reasons?.[0], pm?.card, pm?.reasons]) // E.g. [[reason[0], {...}/null],...]
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
    ?.filter((x: string | undefined, i: any, a: any) => x && a.indexOf(x) == i);
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
  mb: 'md',
  color: 'darkGrey1',
};

const titleLegacyTypography = {
  fontSize: '2xl',
  fontWeight: 'semibold',
  lineHeight: '2rem',
};

const titleSemanticTypography = {
  textStyle: 'heading-m',
} as const;

const descriptionSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const cardDetailSemanticTypography = {
  textStyle: 'body-m-regular',
} as const;

const radioBtnStyle = {
  h: 'fit-content',
  display: 'flex',
};

const radioImgStyle = {
  w: '10',
  h: '6',
  mr: 'xs',
  mb: 'xs',
};

const paymentTypeWrapperStyle = (isPaymentRedesignEnabled?: boolean) =>
  !isPaymentRedesignEnabled
    ? {
        w: {
          mobile: 'full',
          xs: 'full',
          sm: '26.3rem',
          md: '27.563rem',
          lg: '24.5rem',
          xl: '26.25rem',
        },
        mb: '5xl',
      }
    : { mb: '5xl' };

const paymentTypeTabsWrapperStyle = {
  w: '100%',
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

const tabsWrapperStylePayNowOnly = {
  mb: '4rem',
  width: {
    mobile: '18rem',
    xs: '21.5rem',
    sm: '33.75rem',
    md: '45rem',
    lg: '50.5rem',
    xl: '54rem',
  },
};

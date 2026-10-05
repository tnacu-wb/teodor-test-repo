import { Box, Flex, Grid, GridItem } from '@chakra-ui/react';
import {
  AddressGuestInput,
  QueryHotelInformationArgs,
  INITIATE_PAYPAL_PAYMENT_MUTATION,
  PAYPAL_PAYMENT,
  PackagesCriteria,
  PageName,
  PaymentMethod,
  paymentOptions as PaymentType,
  paymentSteps,
  BASKET_STATUS,
  PaymentOption,
  FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS,
  FT_PI_BB_CCUI_DISABLE_PAYMENTS,
  FT_PI_ENABLE_PAYMENT_REDESIGN,
  FT_PI_BB_NON_GUARANTEED_REMINDER,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT,
  FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK,
  FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
} from '@whitbread-eos/api';
import { LoadingSpinner, PaypalWBProps, PaypalWBButton } from '@whitbread-eos/atoms';
import { BackToPage, INITIAL_GUEST_DETAILS_FORM_DATA, SEO } from '@whitbread-eos/molecules';
import { BillingAddress, BookingSummary } from '@whitbread-eos/organisms';
import {
  buildPaymentParams,
  getPaypalDeviceData,
  getPaypalOptionsParams,
  logicalOrOperator,
  useCustomLocale,
  useLocalStorage,
  useMutationRequest,
  useFeatureToggle,
  updateAncillariesAnalytics,
  renderSanitizedHtml,
  applyDefaultPaymentRestrictions,
  isSecureBookingPage,
  type secureBookingType,
  addBasketIdToCookie,
  usePaymentData,
  usePaymentAnalytics,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import Script from 'next/script';
import { SetStateAction, useCallback, useEffect, useMemo, useRef, useState } from 'react';

import { DatatransBillingAddress } from '~components/payment/datatrans/DatatransBillingAddress';
import { DatatransPaymentButton } from '~components/payment/datatrans/DatatransPaymentButton';
import {
  DatatransSecureFieldsForm,
  DatatransSecureFieldsFormHandle,
} from '~components/payment/datatrans/DatatransSecureFieldsForm';
import { PaymentConfirmSection } from '~components/payment/datatrans/PaymentConfirmSection';
import { PaymentMethodSelector } from '~components/payment/datatrans/PaymentMethodSelector';
import { PaymentOptionToggle } from '~components/payment/datatrans/PaymentOptionToggle';

import {
  PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS,
  PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM,
} from '../../../../utils/pi-all-pages-constants';
import {
  HeaderNotification,
  HotelMessages,
  PaymentErrorNotification,
  PaymentInfoMessages,
} from '../Notifications';
import {
  bookingSummaryDesktopStyle,
  bookingSummaryMobileContainerStyle,
  bookingSummaryMobileTriggerStyle,
  loadingStyle,
  mainPaymentGridStyle,
  pageContentStyle,
  termsAndConditionsStyle,
} from './DatatransPage.styles';

interface Props {
  hiQueryInput: QueryHotelInformationArgs;
  pcksQueryInput: PackagesCriteria;
  basketReference: string | null;
  paypalPaymentData?: PaymentMethod | null;
  paymentStatus?: { basket: { status: BASKET_STATUS } };
}

export function DatatransPage({
  pcksQueryInput,
  hiQueryInput,
  basketReference,
  paypalPaymentData,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const router = useRouter();
  // Show the 3DS failure banner when the user is redirected back after a failed
  // 3DS authorization (page.pi.datatrans.tsx sets this query param on authorize failure).
  const threeDsAuthorizationFailed = router.query['3ds_failed'] === 'true';
  const { language, country } = useCustomLocale();

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  const {
    [FT_PI_ENABLE_PAYMENT_REDESIGN]: isPaymentRedesignEnabled,
    [FT_PI_BB_CCUI_COMPANY_NAME_SPECIAL_CHARACTERS]: isCompanyNameAdvanceEnabled,
    [FT_PI_BB_CCUI_DISABLE_PAYMENTS]: disablePaymentOptions,
    [FT_PI_BB_NON_GUARANTEED_REMINDER]: isSecureBookingFeatureEnabled,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN_ANCILLARIES_GDP_PAYMENT]: isCityTaxBreakdownEnabled,
    [FT_PI_PIB_PAYMENT_BACK_TO_OPTIONS_LINK]: isBackToPaymentOptionsLinkEnabled,
    [FT_PI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePIIDataFromLocalStorageEnabled,
  } = useFeatureToggle();

  const {
    mutation: initiatePaypalPaymentMutation,
    isSuccess: isPaypalSuccess,
    isLoading: isPaypalLoading,
    data: initiatePaypalPaymentMutationData,
    isError: isErrorInitiatePaypalPaymentMutation,
  } = useMutationRequest(INITIATE_PAYPAL_PAYMENT_MUTATION, true);

  // Datatrans Secure Fields state
  const [, setSecureFieldsError] = useState<Error | null>(null);
  const [isSecureFieldsInitialising, setIsSecureFieldsInitialising] = useState(false);
  const [isSecureFieldsSubmitting, setIsSecureFieldsSubmitting] = useState(false);
  const [authorizeError, setAuthorizeError] = useState<boolean>(false);
  const secureFieldsFormRef = useRef<DatatransSecureFieldsFormHandle>(null);

  // Add basket ID to cookie on page load for authorization on confirmation/registration pages
  useEffect(() => {
    if (basketReference) {
      addBasketIdToCookie(basketReference);
    }
  }, [basketReference]);

  const [paymentStepState, setPaymentStepState] = useState(paymentSteps.PAYMENT_DETAILS);
  const isPaymentDetailsStep = paymentStepState === paymentSteps.PAYMENT_DETAILS;

  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState<PaymentOption>({
    type: 'default',
    order: 1,
    enabled: false,
  });
  const [selectedPaymentType, setSelectedPaymentType] = useState({
    name: '',
    type: '',
    subType: '',
    order: 0,
    logoSrc: '',
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: false,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  } as PaymentMethod);

  const paymentCardSelected =
    selectedPaymentType?.type === 'SAVED_CARD'
      ? selectedPaymentType?.card?.type
      : selectedPaymentType.type;

  const [formData] = useLocalStorage('formDetails', INITIAL_GUEST_DETAILS_FORM_DATA);

  const {
    bkngData,
    packages,
    bookingInformation,
    reservationDetails,
    bookingSummaryData,
    cityTaxMessages,
    infoMessages,
    orderedInfoMessages,
    orderedListOfMessagesPaymentType,
    rooms,
    hotelBrand,
    isGermanHotel,
    isBillingAddressDisplayed,
    termsAndConditionsText,
    isLoading: isLoadingPaymentData,
  } = usePaymentData({
    hiQueryInput,
    pcksQueryInput,
    basketReference,
    language,
    country,
    selectedPaymentDetail,
    selectedPaymentType,
    paymentStepState,
    basketDetailsState,
    formData,
    onclickBillingFormHandler,
    t,
  });

  // Save bookingFlowId to sessionStorage so it is available when Datatrans redirects back after 3DS
  useEffect(() => {
    const bookingFlowId = bkngData?.bookingInformation?.bookingFlowId;
    if (bookingFlowId) {
      sessionStorage.setItem('bookingFlowId', bookingFlowId);
    }
  }, [bkngData?.bookingInformation?.bookingFlowId]);

  const billing =
    !isRemovePIIDataFromLocalStorageEnabled && formData?.billing?.differentBillingAddress
      ? formData.billing
      : bkngData?.bookingInformation?.reservationByIdList?.[0]?.billing;

  const [billingAddress, setBillingAddress] = useState<AddressGuestInput | undefined>(
    billing?.address || undefined
  );

  const [sameAsBillingAddress, setSameAsBillingAddress] = useState(true);

  const formattedBillingAddress = [
    billing?.address?.addressLine1,
    billing?.address?.addressLine2,
    billing?.address?.addressLine3,
    billing?.address?.cityName,
    billing?.address?.postalCode,
  ]
    .filter(Boolean)
    .join(', ');

  const currentLang = language;

  const resetMutations = () => {
    if (isErrorInitiatePaypalPaymentMutation) initiatePaypalPaymentMutation.reset();
  };

  const continueToNextStep = () => {
    // Clear any previous authorize error when user retries
    setAuthorizeError(false);
    // Datatrans Secure Fields flow — submit the hosted fields form instead of initiating 3CP
    if (selectedPaymentType?.type === 'NEW_CARD') {
      setIsSecureFieldsSubmitting(true);
      secureFieldsFormRef.current?.submit();
      return;
    }

    resetMutations();
  };

  const handleRedirection = () => {
    if (
      isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled) ||
      router.query[PAYMENT_NAVIGATION_SOURCE_QUERY_PARAM] ===
        PAYMENT_NAVIGATION_SOURCE_GUEST_DETAILS
    ) {
      router.back();
    } else {
      router.push(
        `${country}/${language}${
          bkngData?.bookingInformation?.bookingFlowId
            ? `/${bkngData?.bookingInformation?.bookingFlowId}`
            : ''
        }/guest-details?reservationId=${basketReference}`
      );
    }
  };

  const handleBackClick = () => {
    if (paymentStepState === paymentSteps.CARD_DETAILS) {
      setPaymentStepState(paymentSteps.PAYMENT_DETAILS);
      window.scrollTo(0, 0);
    } else {
      handleRedirection();
    }
  };

  const goToConfirmationPage = () => {
    let queryString = `reservationId=${basketReference}`;
    if (isSecureBookingPage(router?.query as secureBookingType, isSecureBookingFeatureEnabled)) {
      queryString = `reservationId=${basketReference}&secure-booking=true`;
    }

    router
      .push(
        `${country}/${language}${
          bkngData?.bookingInformation?.bookingFlowId
            ? `/${bkngData?.bookingInformation?.bookingFlowId}`
            : ''
        }/confirmation?${queryString}`
      )
      // eslint-disable-next-line no-console
      .catch((error) => console.log(error));
  };

  usePaymentAnalytics({
    paymentStepState,
    basketReference,
    paymentCardSelected,
    selectedPaymentDetail,
    disablePaymentOptions,
    isPaypalSuccess,
    initiatePaypalPaymentMutationData,
    routerQuery: router.query,
    isSecureBookingFeatureEnabled,
    bkngData,
    packages,
    updateAncillariesAnalytics,
  });

  useEffect(() => {
    if (
      isPaypalSuccess &&
      initiatePaypalPaymentMutationData?.initiatePaypalPayment?.status === 'NOT_REQUIRED'
    ) {
      goToConfirmationPage();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isPaypalSuccess, initiatePaypalPaymentMutationData]);

  const handlePaypalPayment = async (nonce: string) => {
    resetMutations();
    const deviceData = await getPaypalDeviceData(paypalPaymentData?.clientToken as string);
    const createPaymentCriteria = buildPaymentParams({
      billingAddress,
      paymentType: PAYPAL_PAYMENT,
      paypalNonce: nonce,
      paypalDeviceData: deviceData,
      hotelId: bookingInformation.hotelId,
      hotelName: bookingSummaryData.hotelInformation?.hotelName,
      arrivalDate: bookingSummaryData?.stayDatesInformation?.arrivalDate,
      departureDate: bookingSummaryData?.stayDatesInformation?.departureDate,
      rooms,
      reservationByIdList: bkngData?.bookingInformation?.reservationByIdList ?? [],
      basketReference: basketReference as string,
      language,
      country,
      billing,
      formData,
      isBillingAddressDisplayed,
      selectedPaymentType,
      selectedPaymentDetail,
      isRemovePIIDataFromLocalStorageEnabled,
      isSecureBooking: isSecureBookingPage(
        router?.query as secureBookingType,
        isSecureBookingFeatureEnabled
      ),
      hotelBrand,
      isGermanHotel,
    });
    initiatePaypalPaymentMutation.mutate({ basketReference, createPaymentCriteria });
  };

  const paypalOptionsData: PaypalWBProps = useMemo(() => {
    const { createBillingAgreement, onApprove, onError } = getPaypalOptionsParams({
      currencyCode: bkngData?.bookingInformation?.currencyCode,
      approveCallBack: handlePaypalPayment,
    });

    return {
      style: {
        layout: 'vertical',
        shape: 'rect',
        label: 'pay',
      },
      disabled: false,
      currency: `${bkngData?.bookingInformation?.currencyCode}`,
      onApprove,
      onError,
      createBillingAgreement,
    };
  }, [
    bkngData?.bookingInformation?.currencyCode,
    paypalPaymentData?.clientToken,
    handlePaypalPayment,
  ]);

  // Datatrans Secure Fields handlers
  const handleSecureFieldsSuccess = useCallback(
    async (data: { transactionId: string; redirect?: string }) => {
      if (data.redirect) {
        // 3DS redirect — keep the button in loading state, page is leaving
        window.location.href = data.redirect;
        return;
      }
      try {
        const response = await fetch('/api/payments/authorize', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ basketId: basketReference }),
        });
        if (!response.ok) throw new Error('Payment authorization failed');
        // Keep loading — router.push is about to redirect, no need to clear
        const bookingFlowId = sessionStorage.getItem('bookingFlowId') ?? '';
        router.push(
          `/${country}/${language}/${bookingFlowId}/confirmation?reservationId=${basketReference}`
        );
      } catch (error) {
        console.error(String(error));
        // Authorize failed — restore the button and reinit the form so the user can retry
        setIsSecureFieldsSubmitting(false);
        setAuthorizeError(true);
        secureFieldsFormRef.current?.reinit();
      }
    },
    [basketReference, country, language, router]
  );

  const handleSecureFieldsError = useCallback((error: Error) => {
    setSecureFieldsError(error);
  }, []);

  const handlePaymentTypeSection = (value: SetStateAction<PaymentMethod>) => {
    const paymentOpts = value as PaymentMethod;
    applyDefaultPaymentRestrictions(
      paymentOpts,
      PaymentType,
      router?.query,
      isSecureBookingFeatureEnabled
    );
    if (paymentOpts.type === 'NEW_CARD') {
      secureFieldsFormRef.current?.resetForm();
    }
    setSelectedPaymentType(value);
  };

  function onclickBillingFormHandler() {
    if (!isBillingAddressDisplayed) continueToNextStep();
  }

  const isLoading = logicalOrOperator(isLoadingPaymentData, isPaypalLoading, isPaypalSuccess);

  if (isLoading) {
    return (
      <Flex {...loadingStyle}>
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  return (
    <>
      <Script src={process.env.NEXT_PUBLIC_APPLEPAY_SCRIPT_URL} strategy="lazyOnload" />
      {/* Load the Datatrans Secure Fields SDK eagerly so it is ready before the user
          selects "Credit / Debit". The hook polls window.SecureFields until available. */}
      <Script
        src={process.env.NEXT_PUBLIC_DATATRANS_SECURE_FIELDS_URL}
        strategy="afterInteractive"
      />
      {/* Load the Datatrans Payment Button SDK eagerly so it is ready before the user
          selects Apple Pay or Google Pay. DatatransPaymentButton polls window.PaymentButton. */}
      <Script
        src={process.env.NEXT_PUBLIC_DATATRANS_PAYMENT_BUTTON_URL}
        strategy="afterInteractive"
      />
      <SEO
        page={PageName.PAYMENT}
        hotelId={bkngData.bookingInformation.hotelId}
        bookingFlowId={bkngData.bookingInformation.bookingFlowId}
        noIndexNoFollow={true}
      />
      <Grid data-testid="paymentPageSection" {...mainPaymentGridStyle}>
        <GridItem {...bookingSummaryMobileContainerStyle}>
          <Flex {...bookingSummaryMobileTriggerStyle}>
            <BookingSummary
              variant="mobile"
              t={t}
              language={currentLang}
              bookingSummaryData={bookingSummaryData}
              reservationDetails={reservationDetails}
              infoMessages={infoMessages}
              taxesMessage={cityTaxMessages?.summaryText}
              isExtrasDisplayed={!!packages?.extrasItems}
              isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
            />
          </Flex>
        </GridItem>

        <GridItem data-testid="paymentPageSection_content" {...pageContentStyle}>
          {isBackToPaymentOptionsLinkEnabled && paymentStepState === paymentSteps.CARD_DETAILS && (
            <BackToPage
              goBack={handleBackClick}
              linkText={t('booking.backToPaymentMethods.text')}
            />
          )}

          {!isBackToPaymentOptionsLinkEnabled && <HeaderNotification />}

          <Box
            hidden={!isPaymentDetailsStep}
            aria-hidden={!isPaymentDetailsStep}
            pointerEvents={isPaymentDetailsStep ? 'auto' : 'none'}
            flex={{ mobile: 1, lg: 'unset' }}
            display="flex"
            flexDirection="column"
          >
            <Box mb="md" flex={1}>
              <PaymentMethodSelector
                basketReference={basketReference || ''}
                selectedId={selectedPaymentType?.type ?? null}
                onChange={() => undefined}
                onMethodSelect={handlePaymentTypeSection}
                setSelectedPaymentDetail={setSelectedPaymentDetail}
              />
            </Box>

            {basketReference && (
              <>
                <PaymentErrorNotification
                  isVisible={threeDsAuthorizationFailed || authorizeError}
                  paymentFailedErrorMessage={t('payment.3ds.authorizationFailed', {
                    defaultValue:
                      "<strong>We couldn't process your payment</strong><br/>Please check your card details or try another card",
                  })}
                />
                <DatatransSecureFieldsForm
                  ref={secureFieldsFormRef}
                  basketId={basketReference}
                  country={country}
                  language={language}
                  isVisible={selectedPaymentType?.type === 'NEW_CARD'}
                  onSuccess={handleSecureFieldsSuccess}
                  onError={handleSecureFieldsError}
                  onInitialisingChange={setIsSecureFieldsInitialising}
                  onSubmitValidationFailed={() => setIsSecureFieldsSubmitting(false)}
                  guestEmail={billing?.email}
                  billingAddress={billingAddress}
                />
              </>
            )}

            {isBillingAddressDisplayed && (
              <DatatransBillingAddress
                address={formattedBillingAddress}
                label={t('billingAddress.title')}
                isChecked={sameAsBillingAddress}
                onChange={setSameAsBillingAddress}
                data-testid="DatatransBillingAddress"
              />
            )}

            {isBillingAddressDisplayed && !sameAsBillingAddress && (
              <BillingAddress
                currentBillingAddress={billing?.address}
                continueToNextStep={continueToNextStep}
                setBillingAddress={setBillingAddress}
                t={t}
                currentLang={currentLang}
                hasError={false}
                isCompanyNameAdvanceEnabled={isCompanyNameAdvanceEnabled}
                horizontalRadioButtons={isPaymentRedesignEnabled}
                isCountryAllowTypingEnabled={true}
                isDatatransPage={true}
              />
            )}

            <PaymentOptionToggle
              options={selectedPaymentType?.paymentOptions ?? []}
              selectedType={selectedPaymentDetail.type}
              onChange={(type: string) =>
                setSelectedPaymentDetail(
                  selectedPaymentType.paymentOptions?.find((o) => o.type === type) ?? {
                    type,
                    order: 1,
                    enabled: true,
                  }
                )
              }
            />

            <PaymentConfirmSection
              totalAmount={
                bookingInformation?.totalCost ? Number(bookingInformation.totalCost) : undefined
              }
              currency={bkngData?.bookingInformation?.currencyCode ?? 'GBP'}
              language={language}
              hotelId={hiQueryInput.hotelId}
              ratePlanCode={bookingInformation?.ratePlanCode}
              onConfirm={() => continueToNextStep()}
              onBack={handleBackClick}
              selectedPaymentType={selectedPaymentType}
              paypalOptions={paypalOptionsData}
              isLoading={
                selectedPaymentType?.type === 'NEW_CARD'
                  ? isSecureFieldsInitialising || isSecureFieldsSubmitting
                  : undefined
              }
              datatransPaymentButtonProps={
                basketReference
                  ? {
                      basketId: basketReference,
                      amount: String(bkngData?.bookingInformation?.totalCost || 0),
                      currencyCode: bkngData?.bookingInformation?.currencyCode || 'GBP',
                      walletType: selectedPaymentType?.type === 'GP' ? 'GOOGLE_PAY' : 'APPLE_PAY',
                      onAuthorization: handleSecureFieldsSuccess,
                      onError: handleSecureFieldsError,
                    }
                  : undefined
              }
            />
          </Box>
        </GridItem>

        <GridItem {...bookingSummaryDesktopStyle}>
          <BookingSummary
            variant="desktop"
            t={t}
            language={currentLang}
            bookingSummaryData={bookingSummaryData}
            reservationDetails={reservationDetails}
            taxesMessage={cityTaxMessages?.summaryText}
            isExtrasDisplayed={!!packages?.extrasItems}
            isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
          />

          {paymentStepState === paymentSteps.PAYMENT_DETAILS && (
            <>
              <Box
                sx={termsAndConditionsStyle}
                data-testid="termsAndConditionsPayment"
                className="formatLinks"
              >
                {renderSanitizedHtml(termsAndConditionsText)}
              </Box>

              {PAYPAL_PAYMENT === selectedPaymentType?.name && (
                <PaypalWBButton {...paypalOptionsData} />
              )}

              {/* Datatrans Apple Pay / Google Pay button in the sidebar */}
              {(selectedPaymentType?.type === 'AP' || selectedPaymentType?.type === 'GP') && (
                <DatatransPaymentButton
                  basketId={basketReference || ''}
                  amount={String(bkngData?.bookingInformation?.totalCost || 0)}
                  currencyCode={bkngData?.bookingInformation?.currencyCode || 'GBP'}
                  walletType={selectedPaymentType?.type === 'GP' ? 'GOOGLE_PAY' : 'APPLE_PAY'}
                  onAuthorization={handleSecureFieldsSuccess}
                  onError={handleSecureFieldsError}
                />
              )}
            </>
          )}

          <HotelMessages messages={orderedInfoMessages} />

          <PaymentInfoMessages messages={orderedListOfMessagesPaymentType} />
        </GridItem>
      </Grid>
    </>
  );
}

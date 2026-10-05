import { Box } from '@chakra-ui/react';
import {
  SummaryOfPaymentsType,
  Query,
  PaymentOption,
  CompanyProfile,
  PaymentMethod,
  EckohParameters,
  PaymentAnalytics,
  CardHolderType,
  AddressGuestInputWithCountry,
  LanguageEnum,
  CardStatus,
  CountryEnum,
  AddressGuestInput,
  TypeOfCaller as TypeOfCallerEnum,
  CcuiCardType,
  GET_ECKOH_STATUS,
  INITIATE_ECKOH_IFRAME,
  PAYMENT_ANALYTICS_KEY,
  paymentOptions,
  EckohPayMethod,
  CcuiConfirmBookingData,
  ShortCountry,
  CcuiConfirmBookingBillingAddress,
  BookingType,
  UPDATE_EMAIL,
  CCUI_INITIATE_PAYMENT_PROCESS,
  BASKET_STATUS,
  BusinessAllowanceCCUItype,
  BookersReferencesDetailsType,
  GET_PAYMENT_STATUS,
  type Claims,
} from '@whitbread-eos/api';
import {
  AccountToCompanyContainer,
  PaymentDetails,
  PaymentTypeContainer,
  TotalCost,
  BackToPage,
  CardPresentSection,
  LaunchEckoh,
  CardHolderName,
  BillingAddress,
  PageLoader,
} from '@whitbread-eos/molecules';
import {
  analytics,
  getA2CBusinessAllowances,
  logicalOrOperator,
  replaceWithEmptyString,
  useCustomLocale,
  useLocalStorage,
  useMutationRequest,
  usePaymentMethod,
  useQueryRequest,
  useSessionStorage,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import { Dispatch, SetStateAction, useRef, useState, useEffect } from 'react';

import { IS_BACK_FROM_CHANGE_PAYMENT_PAGE_CCUI } from './constants';

export interface Props {
  summaryOfPayments: SummaryOfPaymentsType;
  paymentOptionsData: Query;
  basketReference: string;
  user: Claims;
  hiData: any;
  bcData: any;
}

export default function ChangePaymentContainer({
  summaryOfPayments,
  paymentOptionsData,
  basketReference,
  user,
  hiData,
  bcData,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const publicRuntimeConfig = getConfig()?.publicRuntimeConfig;
  const { t } = useTranslation();
  const router = useRouter();

  const { changePaymentBIC } = router.query;
  const {
    a2cDetails,
    emailPreference,
    hotelName,
    hotelCode,
    brand,
    paymentType,
    companyId: compId,
    allowances,
  } = paymentOptionsData?.paymentOptions ?? {};

  const [showTotalCost, setShowTotalCost] = useState<boolean>(!!(hotelName && hotelCode && brand));
  const [companyId, setCompanyId] = useState<string>(compId || '');
  const [companyDetails, setCompanyDetails] = useState<CompanyProfile | null>(
    a2cDetails as CompanyProfile | null
  );
  const cardOptions = [
    CcuiCardType.NEW_CARD,
    CcuiCardType.NEW_PIBA,
    CcuiCardType.NON_GUARANTEED,
    CcuiCardType.ACCOUNT_COMPANY,
  ];
  const defaultPaymentInfo = {
    type: '',
    order: 0,
    enabled: true,
  };

  const disabledCardOptions = cardOptions.filter((option) => option === CcuiCardType.NEW_PIBA);

  const [selectedPaymentType, setSelectedPaymentType] = usePaymentMethod();

  const [selectedPaymentInfo, setSelectedPaymentInfo] = useState<PaymentOption>(defaultPaymentInfo);

  const defaultAllowances = allowances?.values?.map((value) => value?.allowance ?? '');

  const [sendEmailApproval, setSendEmailApproval] = useState<boolean>(true);
  const [acCompanyReference, setACCompanyReference] = useState<string>('');

  const [emailAddress, setEmailAddress] = useState<string | undefined>(
    emailPreference?.emailAddress ?? ''
  );
  const [emailError, setEmailError] = useState<string>('');
  const isEckohRequired =
    selectedPaymentType.type !== CcuiCardType.ACCOUNT_COMPANY &&
    selectedPaymentType.type !== CcuiCardType.NON_GUARANTEED;

  const [hasErrors] = useState<boolean>(false);

  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const isA2ContainerDisplayed =
    selectedPaymentInfo?.type !== paymentOptions.PAY_NOW &&
    selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY;

  const isA2CPayment = selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY;

  const companyReferenceError = useRef(false);
  const setCompanyReferenceError: Dispatch<SetStateAction<boolean>> = (value) => {
    if (typeof value === 'boolean') {
      companyReferenceError.current = value;
    }
  };
  const [acCharges, setACCharges] = useState<string[]>(defaultAllowances ?? []);
  const [companyNumber, setCompanyNumber] = useState<string>('');

  const [typeOfCaller, setTypeOfCaller] = useState<string>('');
  const [cardType, setCardType] = useState<string>('');
  const [disabledCardType, setDisabledCardType] = useState<string>('');
  const [eckohCheckPassed, setEckohCheckPassed] = useState<boolean>(false);

  const [isEnabledEckohQuery, setIsEnabledEckohQuery] = useState<boolean>(true);
  const [isEnabledPaymentStatus, setIsEnabledPaymentStatus] = useState<boolean>(false);
  const [confirmationStarted, setConfirmationStarted] = useState<boolean>(false);

  const [eckohParameters, setEckohParameters] = useState<EckohParameters>();

  const [confAnalytics, setConfAnalytics] = useSessionStorage<PaymentAnalytics>(
    PAYMENT_ANALYTICS_KEY,
    {}
  );
  const [hasChecks, setHasChecks] = useState<boolean>(false);

  const [cardHolderNames, setCardHolderNames] = useState<CardHolderType>({
    firstName: '',
    lastName: '',
  });
  const [, setHasCardHolderNameError] = useState<boolean>(false);

  const [, setIsBackFlag] = useLocalStorage(IS_BACK_FROM_CHANGE_PAYMENT_PAGE_CCUI, false);
  const [companyProfile] = useLocalStorage<CompanyProfile | undefined>('CompanyProfile', undefined);
  const firstRoom = logicalOrOperator(bcData?.reservationByIdList?.[0], {});
  const isCardHolderNameCompleted = isEckohRequired
    ? cardHolderNames.firstName !== '' && cardHolderNames.lastName !== '' && eckohCheckPassed
    : true;
  const billing = firstRoom?.billing;
  const [billingAddress, setBillingAddress] = useState<AddressGuestInputWithCountry>({
    addressLine1: '',
    addressLine2: undefined,
    addressLine3: undefined,
    addressLine4: undefined,
    cityName: undefined,
    postalCode: '',
    countryCode: '',
    companyName: undefined,
    billingAddressSelection: '',
  });

  const [businessAllowances, setBusinessAllowances] = useState({
    totalDinnerBudgetPersonNight: '',
    isAlcoholDinner: false,
    carParking: false,
    ultimateWifi: false,
    mealDeal: false,
    premierInnBreakfast: false,
    continentalBreakfast: false,
  } as BusinessAllowanceCCUItype);
  //Bookers references details section
  const [bookerReferencesDetails, setBookerReferencesDetails] =
    useState<BookersReferencesDetailsType>({
      purchaseOrderNumber: '',
      companyReference: '',
    });

  const hotelInformationDetails = hiData?.hotelInformation;
  // Eckoh status
  const { data: eckohData } = useQueryRequest(
    ['GetEckohStatus'],
    GET_ECKOH_STATUS,
    {
      basketReference,
    },
    {
      enabled: isEnabledEckohQuery,
      gcTime: 0,
      refetchInterval: 3000,
      retry: 3,
      refetchIntervalInBackground: true,
    }
  );

  // Payment status
  const { data: paymentStatus } = useQueryRequest(
    ['GetPaymentStatus'],
    GET_PAYMENT_STATUS,
    {
      basketReference,
    },
    {
      enabled: isEnabledPaymentStatus,
      gcTime: 0,
      refetchInterval: 3000,
      retry: 3,
      refetchIntervalInBackground: true,
    }
  );

  const { mutation: initiateIframeMutation } = useMutationRequest(INITIATE_ECKOH_IFRAME);
  const { mutation: updateEmailMutation } = useMutationRequest(UPDATE_EMAIL, true);
  const {
    mutation: mutationInitiatePaymentProcess,
    error: dataErrorInitiatePaymentProcess,
    isSuccess: mutationInitiatePaymentProcessIsSuccesful,
  } = useMutationRequest(CCUI_INITIATE_PAYMENT_PROCESS, true);

  const onIframeLoad = (time: string) => {
    setConfAnalytics({
      ...confAnalytics,
      paymentLoadTime: time,
      paymentSessionID: '',
      paymentTemplateID: '',
    });
    analytics.update({
      paymentLoadTime: time,
      paymentSessionID: '',
      paymentTemplateID: '',
    });
  };

  const isLoading = logicalOrOperator(
    confirmationStarted,
    paymentStatus?.basket.status === BASKET_STATUS.COMPLETED,
    paymentStatus?.basket.status === BASKET_STATUS.PROCESSING,
    confirmationStarted && paymentStatus?.basket.status === BASKET_STATUS.PAY_PENDING,
    dataErrorInitiatePaymentProcess,
    hasErrors
  );

  const hasCompanyReferenceErrors =
    selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY && companyReferenceError.current;

  useEffect(() => {
    return () => {
      clearInterval(intervalRef.current ?? '');
    };
  }, []);

  // Will run when leaving the current page, on back/forward actions
  useEffect(() => {
    router.beforePopState(({ as }) => {
      if (as !== router.asPath) {
        setIsBackFlag(true);
      }
      return true;
    });

    return () => {
      router.beforePopState(() => true);
    };
  }, [router]);
  useEffect(() => {
    if (billingAddress?.billingAddressSelection === 'CurrentAddress') {
      setBillingAddress(bcData.reservationByIdList?.[0]?.billing?.address);
    }
  }, [billingAddress]);

  useEffect(() => {
    typeOfCaller === '' ? setHasChecks(false) : setHasChecks(true);
    const defaultBusinessAllowances = {
      totalDinnerBudgetPersonNight: '',
      isAlcoholDinner: false,
      carParking: false,
      ultimateWifi: false,
      mealDeal: false,
      premierInnBreakfast: false,
      continentalBreakfast: false,
    };
    switch (selectedPaymentType.type) {
      case CcuiCardType.NEW_CARD:
        setHasChecks(true);
        setTypeOfCaller('');
        setCardType(CardStatus.CARD_PRESENT);
        if (
          selectedPaymentType?.paymentOptions?.[0]?.enabled ||
          selectedPaymentType?.paymentOptions?.[1]?.enabled
        ) {
          setDisabledCardType(CardStatus.CARD_NOT_PRESENT);
        }
        setEckohCheckPassed(false);
        setCardHolderNames({ firstName: '', lastName: '' });

        setBusinessAllowances(defaultBusinessAllowances);
        setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });
        setShowTotalCost(true);

        break;
      case CcuiCardType.ACCOUNT_COMPANY:
        setTypeOfCaller('');
        setHasChecks(true);
        setShowTotalCost(false);
        setEckohParameters(undefined);
        setIsEnabledEckohQuery(false);
        setCardHolderNames({ firstName: '', lastName: '' });
        setBusinessAllowances(defaultBusinessAllowances);
        setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });

        break;
      case CcuiCardType.NON_GUARANTEED:
        setShowTotalCost(true);
        setHasChecks(false);
        setEckohParameters(undefined);
        setIsEnabledEckohQuery(false);
        setCardHolderNames({ firstName: '', lastName: '' });
        setTypeOfCaller(TypeOfCallerEnum.ANY_CUSTOMER);

        break;
    }
  }, [selectedPaymentType]);

  useEffect(() => {
    if (cardType === CardStatus.CARD_PRESENT || cardType === CardStatus.CARD_NOT_PRESENT) {
      const defaultBusinessAllowances = {
        totalDinnerBudgetPersonNight: '',
        isAlcoholDinner: false,
        carParking: false,
        ultimateWifi: false,
        mealDeal: false,
        premierInnBreakfast: false,
        continentalBreakfast: false,
      };
      setEckohCheckPassed(false);
      setCardHolderNames({ firstName: '', lastName: '' });
      setBusinessAllowances(defaultBusinessAllowances);
      setBookerReferencesDetails({ purchaseOrderNumber: '', companyReference: '' });
    }
  }, [cardType]);

  useEffect(() => {
    if (hasErrors || dataErrorInitiatePaymentProcess) {
      router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [hasErrors, dataErrorInitiatePaymentProcess]);

  useEffect(() => {
    setConfAnalytics({
      ...confAnalytics,
      basketReference,
    });
  }, [paymentStatus]);

  useEffect(() => {
    if (
      paymentStatus?.basket.status === BASKET_STATUS.COMPLETED &&
      mutationInitiatePaymentProcessIsSuccesful
    ) {
      setIsEnabledPaymentStatus(false);
      // change routing to BIC
      router.back();
    } else if (paymentStatus?.basket.status === BASKET_STATUS.FAILED && confirmationStarted) {
      router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
      setIsEnabledPaymentStatus(false);
    }
  }, [paymentStatus, confirmationStarted]);

  if (isLoading) {
    return <PageLoader text={t('booking.loading')} />;
  }
  return (
    <>
      {/* Will be implemented in future task, check if discount from payment-ccui could be used />*/}
      {/* {changePaymentBIC && discount &&  <AmendPaymentDiscount value={discount.toString()}}  */}
      <Box {...sectionStyles} data-testid="ChangePaymentContainer">
        <PaymentDetails
          selectedPaymentDetail={selectedPaymentInfo}
          selectedPaymentType={selectedPaymentType}
          setSelectedPaymentDetail={setSelectedPaymentInfo}
          t={t}
          hideHeader={true}
          isCCUI={true}
        />
      </Box>
      <Box {...sectionStyles}>
        <PaymentTypeContainer
          onPaymentTypeClick={setSelectedPaymentType}
          selectedPaymentType={selectedPaymentType}
          selectedPaymentDetail={selectedPaymentInfo}
          styles={{ containerStyles: { w: '100%' } }}
          initialPaymentType={paymentType}
          disabledOptions={disabledCardOptions}
          isFromChangePaymentBIC={!!changePaymentBIC}
        />
        {isA2ContainerDisplayed && (
          <AccountToCompanyContainer
            hotelId={hotelCode ?? ''}
            selectedPaymentDetail={selectedPaymentInfo?.type as string}
            setIsTotalCostVisible={setShowTotalCost}
            setACCharges={setACCharges}
            setCompanyReferenceError={setCompanyReferenceError}
            setCompanyNumber={setCompanyNumber}
            setCompanyId={setCompanyId}
            setCompanyDetails={setCompanyDetails}
            setACCompanyReference={setACCompanyReference}
            isFromChangePaymentBIC={!!changePaymentBIC}
          />
        )}
        {isEckohRequired && (
          <>
            <CardPresentSection
              value={cardType}
              setValue={onCardChange}
              disabledOption={disabledCardType}
              cardType={selectedPaymentType.type}
            />
            <LaunchEckoh
              onSuccess={() => setEckohCheckPassed(true)}
              onFail={() => setEckohCheckPassed(false)}
              setIsEnabledEckohQuery={setIsEnabledEckohQuery}
              initiateIframe={initiateIframe}
              eckohParameters={eckohParameters}
              eckohStatus={eckohData?.eckohRecordingStatus?.status}
              disabledEckoh={!cardType || selectedPaymentInfo?.type === 'default'}
              onIframeLoad={onIframeLoad}
            />
            {eckohCheckPassed && (
              <>
                <CardHolderName
                  cardHolderNames={cardHolderNames}
                  setCardHolderNames={setCardHolderNames}
                  setHasError={setHasCardHolderNameError}
                />
                <BillingAddress
                  currentBillingAddress={billing?.address}
                  continueToNextStep={continueToNextStep}
                  t={t}
                  currentLang={language}
                  setBillingAddress={setBillingAddress}
                  companyProfile={companyProfile}
                />
              </>
            )}
          </>
        )}
      </Box>
      <Box>
        {showTotalCost && (
          <TotalCost
            country={country}
            currencyCode={bcData?.currencyCode}
            emailSection={{
              setEmailAddress,
              emailAddress,
              emailError,
              setEmailError,
            }}
            hotelBrand={brand.toLowerCase()}
            hotelId={hotelCode}
            hotelName={hotelName}
            previousTotalCost={summaryOfPayments.previousTotal}
            totalCost={summaryOfPayments.totalCost}
            setSendEmail={setSendEmailApproval}
            hasAllChecks={hasChecks && isCardHolderNameCompleted && !hasCompanyReferenceErrors}
            language={language}
            sendEmail={sendEmailApproval}
            onConfirmClick={continueWithoutEckoh}
            isCompWithoutEckoh={!isEckohRequired}
            isFromChangePaymentBIC={!!changePaymentBIC}
          />
        )}
        <BackToPage goBack={() => router.back()} linkText={t('booking.summary.back')} />
      </Box>
    </>
  );

  function continueWithoutEckoh() {
    onConfirmBooking(billingAddress);
  }

  function continueToNextStep(billingAddressNew?: AddressGuestInput) {
    if (!hasErrors) {
      onConfirmBooking(billingAddressNew ?? billingAddress);
    } else {
      router.push(`/${country}/${language}/payment-errors?reservationId=${basketReference}`);
    }
  }

  async function onEmailUpdate() {
    try {
      await updateEmailMutation.mutateAsync({
        basketReference,
        updateEmailCriteria: {
          email: emailAddress,
        },
      });
      return true;
    } catch {
      setEmailError(t('ccui.payment.emailAddress.error'));
      return false;
    }
  }

  function getBillingData() {
    const billingAddressCountry =
      bcData.reservationByIdList[0].billing.address.country ||
      bcData.reservationByIdList[0].billing.address.countryCode;
    const paymentProcessCountryFromGD =
      billingAddressCountry === ShortCountry.UK ? ShortCountry.GB : billingAddressCountry;

    const billingData: CcuiConfirmBookingBillingAddress = {
      address: {
        // bookingSummaryData
        addressLine1: bcData?.reservationByIdList[0]?.billing?.address?.addressLine1 ?? '',
        addressLine2: bcData?.reservationByIdList[0]?.billing?.address?.addressLine2 ?? '',
        addressLine3: bcData?.reservationByIdList[0]?.billing?.address?.addressLine3 ?? '',
        addressLine4: bcData?.reservationByIdList[0]?.billing?.address?.addressLine4 ?? '',
        cityName: bcData?.reservationByIdList[0]?.billing?.address?.cityName ?? '',
        postalCode: bcData?.reservationByIdList[0]?.billing?.address.postalCode as string,
        country: paymentProcessCountryFromGD as string,
        companyName: '',
        addressType: '',
      },
      firstName: logicalOrOperator(bcData?.reservationByIdList[0]?.billing?.firstName, ''),
      lastName: logicalOrOperator(bcData?.reservationByIdList[0]?.billing?.lastName, ''),
      email: logicalOrOperator(billing.email, ''),
      title: logicalOrOperator(billing.title, ''),
      telephone: logicalOrOperator(billing.telephone, ''),
      differentBillingAddress:
        billing.differentBillingAddress ||
        billingAddress?.billingAddressSelection === 'DifferentAddress' ||
        isA2CPayment,
    };

    if (isA2CPayment && companyDetails?.address) {
      billingData.address = {
        ...companyDetails.address,
        companyName: companyDetails?.name ?? '',
        addressType: 'BUSINESS',
      };
    }

    return billingData;
  }

  function getPaymentRequest(bA: AddressGuestInput) {
    // Generate random INT number
    const arrayRandom = new Uint32Array(1);
    const randomInt: number = window.crypto.getRandomValues(arrayRandom)[0];
    const paymentProcessCountry =
      (billingAddress.country || billingAddress.countryCode) === ShortCountry.UK
        ? ShortCountry.GB
        : logicalOrOperator(billingAddress.country, billingAddress.countryCode);

    return {
      requestId: randomInt,
      payment: {
        type: selectedPaymentType.name,
        subType: 'MOTO',
        billing: getBillingData(),
        card: {
          cardHolderLastName: cardHolderNames.lastName ?? '',
          cardHolderFirstName: cardHolderNames.firstName ?? '',
          cardHolderAddress: {
            addressLine1: bA.addressLine1,
            addressLine2: bA.addressLine2 ?? null,
            addressLine3: bA.addressLine3 ?? null,
            addressLine4: bA.addressLine4 ?? null,
            cityName: bA.cityName ?? null,
            postalCode: bA.postalCode ?? null,
            country: String(paymentProcessCountry),
          },
        },
      },
      booking: {
        type: selectedPaymentInfo?.type as BookingType,
        journey: 'BOOKING',
        channel: 'CCC',
        language: language,
        businessSite: {
          identifier: bcData.hotelId,
          type: 'HOTEL',
          name: hotelInformationDetails?.name ?? '',
          location: hotelInformationDetails?.address?.addressLine1 ?? '',
        },
      },
    };
  }

  async function onConfirmBooking(bA: AddressGuestInput) {
    // Check card present
    const hasNoCardPresentOption =
      selectedPaymentType.type !== CcuiCardType.ACCOUNT_COMPANY &&
      selectedPaymentType.type !== CcuiCardType.NON_GUARANTEED;

    const cardPresentStatus = hasNoCardPresentOption && cardType === CardStatus.CARD_PRESENT;
    const selectedPaymentTypeName =
      selectedPaymentType.name === CcuiCardType.PIBA_EU
        ? EckohPayMethod.PIBA_DE
        : EckohPayMethod.PIBA_GB;
    const subPaymentType =
      selectedPaymentType.type === CcuiCardType.NEW_PIBA ? selectedPaymentTypeName : null;

    //ACCOUNT TO COMPANY ITEMS
    const isA2CPayment = selectedPaymentType.type === CcuiCardType.ACCOUNT_COMPANY;
    const accountCompanyItems = isA2CPayment
      ? {
          companyNumber,
          companyId,
          charges: acCharges.join(','),
          businessItems: {
            businessAllowances: getA2CBusinessAllowances(acCharges),
            customReferenceNumber: acCompanyReference,
            purchaseOrderNumber: '',
          },
        }
      : {
          companyNumber: '',
          companyId: '',
          charges: '',
          businessItems: null,
        };

    // BOOKING INFORMATION DATA
    const bookingDetails: CcuiConfirmBookingData = {
      paymentOption: selectedPaymentType.type as CcuiCardType,
      subPaymentType: subPaymentType,
      ccuiExtraItems: {
        sendMail: sendEmailApproval ?? false,
        cardPresent: cardPresentStatus,
        accountCompanyItems,
        nonguaranteedItems: {
          typeOfCaller: typeOfCaller ?? null,
        },
        addressCompanyName: bA.companyName ?? null,
        businessItems: {
          businessAllowances: [
            {
              allowance: 'carParking',
              budget: 0,
              isAuthorised: businessAllowances.carParking,
            },
            {
              allowance: 'ultimateWifi',
              budget: 0,
              isAuthorised: businessAllowances.ultimateWifi,
            },
            {
              allowance: 'mealDeal',
              budget: 0,
              isAuthorised: businessAllowances.mealDeal,
            },
            {
              allowance: 'premierInnBreakfast',
              budget: 0,
              isAuthorised: businessAllowances.premierInnBreakfast,
            },
            {
              allowance: 'continentalBreakfast',
              budget: 0,
              isAuthorised: businessAllowances.continentalBreakfast,
            },
            {
              allowance: 'alcohol',
              budget: 0,
              isAuthorised: businessAllowances.isAlcoholDinner,
            },
            {
              allowance: 'dinner',
              budget: +businessAllowances.totalDinnerBudgetPersonNight,
              isAuthorised: +businessAllowances.totalDinnerBudgetPersonNight > 0,
            },
          ],
          customReferenceNumber: bookerReferencesDetails.companyReference,
          purchaseOrderNumber: bookerReferencesDetails.purchaseOrderNumber,
        },
      },
      paymentRequest: getPaymentRequest(bA),
    };

    let businessItems = null;
    if (isA2CPayment) {
      businessItems = bookingDetails.ccuiExtraItems?.accountCompanyItems?.businessItems;
    }
    if (emailAddress && emailAddress !== firstRoom?.billing?.email && !emailError) {
      if (await onEmailUpdate()) {
        handleInitiatePaymentProcess(bookingDetails, businessItems);
      }
    } else {
      handleInitiatePaymentProcess(bookingDetails, businessItems);
    }
  }

  function handleInitiatePaymentProcess(
    bookingDetails: CcuiConfirmBookingData,
    businessItems: any
  ) {
    const cardHolderLastName = bookingDetails.paymentRequest.payment.card.cardHolderLastName;
    const cardHolderFirstName = bookingDetails.paymentRequest.payment.card.cardHolderFirstName;
    const addressLine1 = bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine1;
    const addressLine2 = bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine2;
    const addressLine3 = bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine3;
    const addressLine4 = bookingDetails.paymentRequest.payment.card.cardHolderAddress.addressLine4;
    const cityName = bookingDetails.paymentRequest.payment.card.cardHolderAddress.cityName;
    const postalCode = bookingDetails.paymentRequest.payment.card.cardHolderAddress.postalCode;
    const country = bookingDetails.paymentRequest.payment.card.cardHolderAddress.country;
    mutationInitiatePaymentProcess
      .mutateAsync({
        basketReference: basketReference,
        paymentOption: bookingDetails.paymentOption,
        subPaymentType: bookingDetails.subPaymentType,
        sendMail: bookingDetails.ccuiExtraItems?.sendMail,
        cardPresent: bookingDetails.ccuiExtraItems?.cardPresent,
        companyNumber: bookingDetails.ccuiExtraItems?.accountCompanyItems?.companyNumber,
        companyId: bookingDetails.ccuiExtraItems?.accountCompanyItems?.companyId,
        charges: bookingDetails.ccuiExtraItems?.accountCompanyItems?.charges,
        typeOfCaller: bookingDetails.ccuiExtraItems?.nonguaranteedItems?.typeOfCaller,
        addressCompanyName: bookingDetails.ccuiExtraItems?.addressCompanyName,
        requestId: bookingDetails.paymentRequest.requestId.toString(),
        type: bookingDetails.paymentRequest.payment.type,
        subType: bookingDetails.paymentRequest.payment.subType,
        firstName: bookingDetails.paymentRequest.payment.billing.firstName,
        lastName: bookingDetails.paymentRequest.payment.billing.lastName,
        title: bookingDetails.paymentRequest.payment.billing.title,
        email: emailAddress ?? bookingDetails.paymentRequest.payment.billing.email,
        telephone: bookingDetails.paymentRequest.payment.billing.telephone,
        differentBillingAddress:
          bookingDetails.paymentRequest.payment.billing.differentBillingAddress,
        addressLine1: bookingDetails.paymentRequest.payment.billing.address.addressLine1,
        addressLine2: bookingDetails.paymentRequest.payment.billing.address.addressLine2,
        addressLine3: bookingDetails.paymentRequest.payment.billing.address.addressLine3,
        addressLine4: bookingDetails.paymentRequest.payment.billing.address.addressLine4,
        cityName: bookingDetails.paymentRequest.payment.billing.address.cityName,
        postalCode: bookingDetails.paymentRequest.payment.billing.address.postalCode,
        country: bookingDetails.paymentRequest.payment.billing.address.country,
        companyName: bookingDetails.paymentRequest.payment.billing.address.companyName,
        addressType: bookingDetails.paymentRequest.payment.billing.address.addressType,
        card: {
          cardHolderLastName,
          cardHolderFirstName,
          cardHolderAddress: {
            addressLine1,
            addressLine2,
            addressLine3,
            addressLine4,
            cityName,
            postalCode,
            country,
          },
        },
        bookingType: bookingDetails.paymentRequest.booking.type,
        journey: bookingDetails.paymentRequest.booking.journey,
        channel: bookingDetails.paymentRequest.booking.channel,
        language: bookingDetails.paymentRequest.booking.language,
        identifier: bookingDetails.paymentRequest.booking.businessSite.identifier,
        bookingBusinessSiteType: bookingDetails.paymentRequest.booking.businessSite.type,
        name: bookingDetails.paymentRequest.booking.businessSite.name,
        location: bookingDetails.paymentRequest.booking.businessSite.location,
        businessItems,
      })
      .then(() => {
        setIsEnabledPaymentStatus(true);
      });
    setEckohParameters(undefined);
    setConfirmationStarted(true);
  }

  function initiateIframe() {
    const arrayRandom = new Uint32Array(1);
    const randomInt: number = window.crypto.getRandomValues(arrayRandom)[0];
    const requestId = randomInt?.toString();

    const paymentTypeAPI = selectedPaymentType.name;
    const selectedPaymentMethod = getPaymentMethodType(selectedPaymentType);

    initiateIframeMutation
      .mutateAsync({
        basketReference,
        agentEmail: user.email,
        agentName: user.name,
        country,
        identifier: hotelCode,
        location: hotelInformationDetails?.address?.addressLine1 ?? '',
        hotelName: replaceWithEmptyString(hotelInformationDetails?.name),
        reservationType: 'HOTEL',
        channel: 'PI',
        journey: 'BOOKING',
        language,
        paymentType: selectedPaymentInfo?.type,
        requestId,
        paymentMethod: paymentTypeAPI,
      })
      .then((data: any) => {
        const paymentId = data.initiateEckohPayment?.paymentId;
        setEckohParameters(
          new EckohParameters(
            replaceWithEmptyString(hotelInformationDetails?.name) || '',
            publicRuntimeConfig?.ECKOH_CLIENT_ID || '',
            user.email,
            language as LanguageEnum,
            paymentId ?? '',
            selectedPaymentInfo?.type as string,
            selectedPaymentMethod as CcuiCardType,
            cardType as CardStatus,
            hotelInformationDetails?.country as CountryEnum,
            publicRuntimeConfig?.ECKOH_PROD === 'true' ? `mWB-${hotelCode}` : `mWB-DEFAULT`,
            publicRuntimeConfig?.ECKOH_ENV
          )
        );
        setIsEnabledEckohQuery(true);
        setConfAnalytics({
          ...confAnalytics,
          echoID: paymentId ?? '',
        });
        analytics.update({
          echoID: paymentId ?? '',
        });
      });
  }

  function getPaymentMethodType(selectedPaymentType: PaymentMethod) {
    if (selectedPaymentType.subType != null) {
      return selectedPaymentType.subType;
    } else return selectedPaymentType.type;
  }
  function onCardChange(type: string) {
    setCardType(type);
  }
}

const sectionStyles = {
  width: '24.5rem',
  pt: 'xl',
};

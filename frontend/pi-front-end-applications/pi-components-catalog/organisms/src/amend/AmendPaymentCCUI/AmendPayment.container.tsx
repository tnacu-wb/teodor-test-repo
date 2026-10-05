import { Box } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import type {
  PaymentMethod,
  PreAuthorisedChargesData,
  SummaryOfPaymentsType,
  Query,
  PaymentOption,
} from '@whitbread-eos/api';
import {
  CcuiCardType,
  Channel,
  BOOKING_SUBCHANNEL,
  CONFIRM_AMEND,
  CONFIRM_AMEND_STATUS,
  TEMPORARY_BASKET_KEY,
  BASKET_STATUS,
  AmendConfirmationErrorLS,
  INITIAL_CONFIRM_AMEND_ERROR_KEY,
  INITIAL_CONFIRM_AMEND_ERROR_VALUE,
  PaymentType,
} from '@whitbread-eos/api';
import {
  AccountToCompanyPreAuthorisedCharges,
  AmendPaymentA2CDetails,
  PaymentDetails,
  PaymentTypeContainer,
  PRE_AUTHORISED_CHARGES,
  TotalCost,
  BackToPage,
  PageLoader,
} from '@whitbread-eos/molecules';
import {
  getFindBookingToken,
  useCustomLocale,
  useMutationRequest,
  getA2CBusinessAllowances,
  useSessionStorage,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { Dispatch, SetStateAction, useRef, useState, useEffect } from 'react';

import { PAY_ON_ARRIVAL } from '../constants';
import { getBasketStatus } from '../helpers';

interface Props {
  summaryOfPayments: SummaryOfPaymentsType;
  bookingReference: string;
  originalBasketReference: string;
  temporaryBasketReference: string;
  paymentOptionsData: Query;
}

export default function AmendPaymentContainer({
  summaryOfPayments,
  bookingReference,
  originalBasketReference,
  temporaryBasketReference,
  paymentOptionsData,
}: Readonly<Props>) {
  const { country, language } = useCustomLocale();
  const { t } = useTranslation();
  const router = useRouter();
  const queryClient = useQueryClient();

  const {
    a2cDetails,
    companyRef,
    emailPreference,
    hotelName,
    hotelCode,
    brand,
    paymentType,
    cardPresent,
    allowances,
    companyId,
  } = paymentOptionsData?.paymentOptions ?? {};

  const [, setTemporaryBasketVisited] = useSessionStorage<string>(TEMPORARY_BASKET_KEY, '');
  const [, setConfirmAmendErrorValue] = useSessionStorage<AmendConfirmationErrorLS>(
    INITIAL_CONFIRM_AMEND_ERROR_KEY,
    INITIAL_CONFIRM_AMEND_ERROR_VALUE
  );

  const showTotalCost = hotelName && hotelCode && brand;

  const cardOptions = [
    CcuiCardType.NEW_CARD,
    CcuiCardType.NEW_PIBA,
    CcuiCardType.NON_GUARANTEED,
    CcuiCardType.ACCOUNT_COMPANY,
  ];

  const disabledCardOptions = cardOptions.filter((option) => option !== (paymentType as string));
  const a2cPaymentType =
    paymentType && paymentType === PaymentType.AccountCompany ? 'Account to company' : '';

  const [selectedPaymentType, setSelectedPaymentType] = useState({
    name: a2cPaymentType,
    type: '',
    subType: '',
    order: 0,
    paymentOptions: [{ type: '', order: 0, enabled: true }],
    enabled: false,
    cnpPreSelected: false,
    cnpOptionAvailable: false,
    reasons: [],
  } as PaymentMethod);

  const [selectedPaymentDetail, setSelectedPaymentDetail] = useState<PaymentOption>({
    type: '',
    order: 0,
    enabled: true,
  });

  const initialAllowances = allowances?.values?.map((value) => value?.allowance ?? '');

  const [sendEmail, setSendEmail] = useState<boolean>(true);
  const [acAllowances, setACAllowances] = useState<string[]>(initialAllowances ?? []);
  const [companyReference, setCompanyReference] = useState<string>(companyRef?.value ?? '');
  const [emailAddress, setEmailAddress] = useState<string | undefined>(
    emailPreference?.emailAddress ?? ''
  );
  const [emailError, setEmailError] = useState<string>('');
  const companyReferenceError = useRef(false);
  const { token } = getFindBookingToken();
  const isWithoutEckoh = (paymentType as string) === CcuiCardType.ACCOUNT_COMPANY;

  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const setPreAuthorisedCharges = (charges: PreAuthorisedChargesData[]) => {
    const newCharges: string[] = charges.map((charge) => charge.label);

    setACAllowances(newCharges);
  };

  const selectedCharges = PRE_AUTHORISED_CHARGES?.filter((charge: PreAuthorisedChargesData) =>
    initialAllowances?.includes(charge.label)
  );

  const setCompanyReferenceError: Dispatch<SetStateAction<boolean>> = (value) => {
    if (typeof value === 'boolean') {
      companyReferenceError.current = value;
    }
  };

  const {
    mutation: confirmAmendMutation,
    isLoading: confirmAmendIsLoading,
    isSuccess: confirmAmendIsSuccess,
  } = useMutationRequest(CONFIRM_AMEND, true);

  useEffect(() => {
    setTemporaryBasketVisited(temporaryBasketReference);
  }, []);

  useEffect(() => {
    return () => {
      clearInterval(intervalRef.current ?? '');
    };
  }, []);

  /* confirmAmendIsSuccess => basketStatus polling => display loading spinner until basket is updated */
  if (confirmAmendIsLoading || confirmAmendIsSuccess) {
    return <PageLoader text={t('booking.loading')} />;
  }

  return (
    <>
      {/* Will be implemented in future task, check if discount from payment-ccui could be used />*/}
      {/* {discount &&  <AmendPaymentDiscount value={discount.toString()}}  */}
      <Box {...sectionStyles} data-testid="A2cPaymentContainer">
        <PaymentDetails
          selectedPaymentDetail={selectedPaymentDetail}
          selectedPaymentType={selectedPaymentType}
          setSelectedPaymentDetail={setSelectedPaymentDetail}
          t={t}
          hideHeader={true}
          isCCUI={true}
          isA2cPaymentPage={true}
        />
      </Box>
      <Box {...sectionStyles}>
        <PaymentTypeContainer
          onPaymentTypeClick={setSelectedPaymentType}
          selectedPaymentType={selectedPaymentType}
          selectedPaymentDetail={selectedPaymentDetail}
          styles={{ containerStyles: { w: '100%' } }}
          initialPaymentType={paymentType}
          disabledOptions={disabledCardOptions}
        />
      </Box>
      {a2cDetails?.display && (
        <Box pt="xl">
          <AmendPaymentA2CDetails
            data={{
              number: a2cDetails.number as string,
              name: a2cDetails.name as string,
              address: a2cDetails.address as string,
              postcode: a2cDetails.postcode as string,
            }}
          />
        </Box>
      )}
      {allowances?.display && (
        <Box>
          <AccountToCompanyPreAuthorisedCharges
            setPreAuthorisedCharges={setPreAuthorisedCharges}
            setCompanyReferenceError={setCompanyReferenceError}
            setACCompanyReference={setCompanyReference}
            initialCharges={selectedCharges}
            initialCompanyReference={companyRef?.value}
          />
        </Box>
      )}
      <Box>
        {showTotalCost && (
          <TotalCost
            country={country}
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
            setSendEmail={setSendEmail}
            hasAllChecks={!companyReferenceError.current}
            language={language}
            sendEmail={sendEmail}
            onConfirmClick={handleConfirmChanges}
            isCompWithoutEckoh={isWithoutEckoh}
          />
        )}
        <BackToPage goBack={() => router.back()} linkText={t('booking.summary.back')} />
      </Box>
    </>
  );

  function handleConfirmChanges() {
    const params = {
      tempBookingRef: temporaryBasketReference,
      originalBookingRef: originalBasketReference,
      channel: Channel.Ccui,
      subchannel: BOOKING_SUBCHANNEL.WEB,
      language: language?.toUpperCase(),
      token,
      environment: window.location.origin,
      emailAddress: emailAddress,
      paymentOptionSelected: PAY_ON_ARRIVAL,
      paymentOption: paymentType,
      ccuiExtraItems: {
        sendMail: Boolean(emailPreference?.send),
        cardPresent: Boolean(cardPresent?.value),
        accountCompanyItems: {
          companyNumber: a2cDetails?.number,
          charges: acAllowances?.join(),
          companyId: companyId,
        },
        businessItems: {
          purchaseOrderNumber: '',
          customReferenceNumber: companyReference,
          businessAllowances: getA2CBusinessAllowances(acAllowances),
        },
      },
      paymentRequest: {
        payment: {
          subType: 'MOTO',
          type: selectedPaymentType.name,
        },
      },
    };

    const amendConfirmationURL = new URL(
      `${window.location.origin}/${country}/${language}/amend/booking-confirmation`
    );

    amendConfirmationURL.searchParams.set('bookingReference', bookingReference);
    amendConfirmationURL.searchParams.set('tempBookingReference', temporaryBasketReference);
    amendConfirmationURL.searchParams.set('basketReference', originalBasketReference);
    confirmAmendMutation
      .mutateAsync(params)
      .then(() => {
        intervalRef.current = setInterval(async () => {
          const { basketStatus, basketError } = await getBasketStatus(
            queryClient,
            temporaryBasketReference
          );

          if (basketStatus === BASKET_STATUS.AMENDED) {
            clearInterval(intervalRef.current ?? '');
            amendConfirmationURL.searchParams.set('status', CONFIRM_AMEND_STATUS.success);
            await router.push(amendConfirmationURL);
          } else if (
            basketStatus === BASKET_STATUS.AMEND_FAILED ||
            (basketStatus === BASKET_STATUS.AMENDING && basketError)
          ) {
            clearInterval(intervalRef.current ?? '');
            amendConfirmationURL.searchParams.set('status', CONFIRM_AMEND_STATUS.error);
            await router.push(amendConfirmationURL);
            setConfirmAmendErrorValue(basketError.code);
          }
        }, 1000);
      })
      .catch(() => {
        amendConfirmationURL.searchParams.set('status', CONFIRM_AMEND_STATUS.error);
        router.push(amendConfirmationURL);
      });
  }
}

const sectionStyles = {
  width: '24.5rem',
  pt: 'xl',
};

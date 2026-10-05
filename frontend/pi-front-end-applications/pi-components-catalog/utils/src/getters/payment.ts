import type { BookingSummaryDataProps, PaymentErrorResponse } from '@whitbread-eos/api';
import {
  UserAccessLevels,
  CountryEnum,
  CARD_TYPES,
  ACCOUNT_TO_COMPANY_ALLOWANCES,
} from '@whitbread-eos/api';

import {
  calculateTotalCostRoomSelection,
  extrasPackagesMapperSelector,
  hotelInformationSelector,
  roomInformationSelector,
  roomPackageSelection,
  selectedMealsPerRoomSelector,
} from '../selectors';
import { analytics } from '../services/analyticsService';

interface Props {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [key: string]: any;
}

export const getBookingSummaryData = ({
  hiData,
  bkngData,
  selectedDonation,
  termsAndConditionsData,
  onclickBillingFormHandler,
  onSubmitBtnText,
  firstRoom,
  noNights,
  selectedMeals,
  adultsMeals,
  childrenMeals,
  roomSelection,
  paymentStepState,
  updatedTotalCost,
  rateDescription,
  rateTags,
  cityTaxTotal,
}: Props) => {
  const bookingSummaryData: BookingSummaryDataProps = {
    hotelInformation:
      hiData?.hotelInformation && hotelInformationSelector(hiData?.hotelInformation),
    totalCost: {
      currency: bkngData?.bookingInformation?.currencyCode,
      initialTotalCost:
        updatedTotalCost?.totalCost?.amount -
        calculateTotalCostRoomSelection(adultsMeals, roomSelection, noNights),
      meals: selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals),
      donations: selectedDonation?.unitPrice,
    },
    rateInformation: {
      rate: firstRoom.roomStay?.rateExtraInfo?.rateName,
      noNights: noNights,
      noRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
      rateDescription,
      rateTags,
    },
    stayDatesInformation: {
      arrivalDate: firstRoom.roomStay?.arrivalDate || null,
      departureDate: firstRoom.roomStay?.departureDate || null,
      noNights: noNights,
    },
    roomInformation: roomInformationSelector(
      bkngData?.bookingInformation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(extrasPackagesMapperSelector(roomSelection))
    ),
    cityTaxTotal,
    termsAndConditionsText: termsAndConditionsData?.termsAndConditions?.text,
    onclickBillingFormHandler: onclickBillingFormHandler,
    onSubmitBtnText: onSubmitBtnText,
    paymentStepState,
  };
  return bookingSummaryData;
};

export const getIsBillingAddressDisplayed = ({
  selectedPaymentType,
  selectedPaymentDetail,
  isBb = false,
  formData,
}: Props) =>
  selectedPaymentType?.type !== 'SAVED_CARD' &&
  selectedPaymentDetail?.type !== 'RESERVE_WITHOUT_CARD' &&
  (isBb || !formData?.isBillingAddressDisplayed);

export const getIsDonationsDisplayed = ({ currentLang, hiData }: Props) =>
  currentLang === 'en' && hiData?.hotelInformation?.address?.country !== 'Ireland';

export const getIsDonationInfoBoxDisplayed = ({
  currentLang,
  paymentStepState,
  hiData,
  paymentSteps,
  donationsData,
}: Props) =>
  paymentStepState === paymentSteps.PAYMENT_DETAILS &&
  currentLang === 'en' &&
  hiData?.hotelInformation?.address?.country !== 'Ireland' &&
  donationsData?.donations?.informationBox;

const GREATER_LONDON_COUNTY = 'greater-london';

export const setBusinessAllowancesSections = ({
  displayedSections,
  selectedPaymentType,
  hotelCounty,
  hiData,
}: Props) => {
  const country = hiData?.hotelInformation?.address?.country;
  const isGreaterLondonCountyHotel = hotelCounty === GREATER_LONDON_COUNTY;

  let amount;
  if (country !== CountryEnum.GB) {
    amount = selectedPaymentType?.bookingAllowances?.maxDinnerBudgets?.ireland?.amount;
  } else if (isGreaterLondonCountyHotel) {
    amount = selectedPaymentType?.bookingAllowances?.maxDinnerBudgets?.greaterLondon?.amount;
  } else {
    amount = selectedPaymentType?.bookingAllowances?.maxDinnerBudgets?.ukWide?.amount;
  }

  displayedSections.businessAllowancesSections.amount = amount || 0;
  displayedSections.businessAllowancesSections.allowAlcohol =
    selectedPaymentType?.bookingAllowances?.allowAlcohol || false;
  displayedSections.businessAllowancesSections.allowCarParking =
    selectedPaymentType?.bookingAllowances?.allowCarParking || false;

  if (!amount) {
    displayedSections.businessAllowancesSections.allowDinner = false;
  } else {
    displayedSections.businessAllowancesSections.allowDinner = true;
  }
};

export const setDisplayedSectionsForStoredCard = ({
  selectedPaymentType,
  displayedSections,
  hotelCounty,
}: Props) => {
  if (
    selectedPaymentType?.card?.cardType === 'BUSINESS_CENTRALLY_STORED_CARD' &&
    (selectedPaymentType?.card?.type === 'AT' || selectedPaymentType?.card?.type === 'PI')
  ) {
    // reference details are displayed
    // payment auth is not displayed
    // business allowances are displayed if cnpRequired=true
    displayedSections.paymentAuth = false;
    if (selectedPaymentType?.card?.cnpRequired === true) {
      displayedSections.businessAllowances = true;
      setBusinessAllowancesSections({ displayedSections, selectedPaymentType, hotelCounty });
    } else {
      displayedSections.businessAllowances = false;
    }
    return displayedSections;
  }
};

export const setDisplayedSectionsForPersonalCard = ({
  selectedPaymentType,
  displayedSections,
  hotelCounty,
}: Props) => {
  if (
    selectedPaymentType?.card?.cardType === 'BUSINESS_PERSONAL_STORED_CARD' &&
    CARD_TYPES.includes(selectedPaymentType?.card?.type)
  ) {
    // reference details are displayed
    // payment auth is displayed if cnpRequired=false
    // business allowances are displayed based on payment auth check (if displayed), otherwise it is displayed by default
    if (selectedPaymentType?.card?.cnpRequired === false) {
      displayedSections.paymentAuth = true;
    }
    displayedSections.businessAllowances = true;
    setBusinessAllowancesSections({ displayedSections, selectedPaymentType, hotelCounty });

    return displayedSections;
  }
};

export const setDisplayedSectionsForNormalCard = ({
  displayedSections,
  selectedPaymentType,
  hotelCounty,
}: Props) => {
  if (
    selectedPaymentType?.card?.cardType === 'BUSINESS_CENTRALLY_STORED_CARD' &&
    selectedPaymentType?.card?.type !== 'AT' &&
    selectedPaymentType?.card?.type !== 'PI'
  ) {
    // reference details are displayed
    // payment auth is not displayed
    // business allowances are displayed if cnpRequired=true
    displayedSections.paymentAuth = false;
    if (selectedPaymentType?.card?.cnpRequired === true) {
      displayedSections.businessAllowances = true;
      setBusinessAllowancesSections({ displayedSections, selectedPaymentType, hotelCounty });
    } else {
      displayedSections.businessAllowances = false;
    }
    return displayedSections;
  }
};

export const getIsBBCardDetailsDisplayed = ({
  selectedPaymentType,
  hotelCounty,
  accessLevel,
}: Props) => {
  const displayedSections = {
    referenceDetails: false,
    paymentAuth: false,
    businessAllowances: false,
    businessAllowancesSections: {
      amountDisabled: true,
      amount: 0,
      allowDinner: true,
      allowAlcohol: false,
      allowCarParking: false,
      allowWiFi: false,
    },
  };

  if (accessLevel === UserAccessLevels.SUPER || accessLevel === UserAccessLevels.BOOKER) {
    displayedSections.businessAllowancesSections.amountDisabled = false;
  }

  if (!selectedPaymentType?.bookingAllowances) {
    return displayedSections;
  }

  if (selectedPaymentType?.type === 'NEW_PIBA') {
    displayedSections.referenceDetails = true;
    displayedSections.paymentAuth = true;
    displayedSections.businessAllowances = true;
    setBusinessAllowancesSections({ displayedSections, selectedPaymentType, hotelCounty });

    return displayedSections;
  } else if (selectedPaymentType?.type === 'SAVED_CARD') {
    displayedSections.referenceDetails = true; // NOTE: not displayed for PI4

    // PIBA Centrally stored card
    setDisplayedSectionsForStoredCard({
      selectedPaymentType,
      displayedSections,
      hotelCounty,
    });

    // PIBA Personal stored card
    setDisplayedSectionsForPersonalCard({ selectedPaymentType, displayedSections, hotelCounty });

    // Centraly Stored Normal Card
    setDisplayedSectionsForNormalCard({ selectedPaymentType, displayedSections, hotelCounty });
  }
  return displayedSections;
};

export const getBillingAddress = ({
  countryRouter,
  isBillingAddressDisplayed,
  billingAddress,
  billing,
}: Props) => {
  return countryRouter === 'gb' && isBillingAddressDisplayed
    ? {
        addressLine1: billingAddress?.addressLine1,
        addressLine2: billingAddress?.addressLine2,
        addressLine3: billingAddress?.addressLine3,
        addressLine4: billingAddress?.addressLine4,
        postalCode: billingAddress?.postalCode,
        country: billingAddress?.countryCode
          ? billingAddress?.countryCode
          : billingAddress?.country,
        countryCode: undefined,
        companyName: undefined,
        addressType: billingAddress.addressType ?? 'BUSINESS',
      }
    : {
        addressLine1: billing?.address?.addressLine1,
        addressLine2: billing?.address?.addressLine2,
        addressLine3: billing?.address?.addressLine3,
        addressLine4: billing?.address?.addressLine4,
        cityName: billing?.address?.cityName,
        country: billing?.address?.countryCode || billing?.address?.country,
        postalCode: billing?.address?.postalCode,
        companyName: billing?.address?.companyName,
        addressType: billing.address.addressType ?? 'BUSINESS',
      };
};

export const getPaymentError = ({
  isErrorInitiatePaymentMutation,
  isErrorInitiatePaypalPaymentMutation,
  initiatePaymentMutationError,
  initiatePaypalPaymentMutationError,
  currentReasonForStay,
  hotelBrand,
  t,
}: Props) => {
  if (isErrorInitiatePaymentMutation || isErrorInitiatePaypalPaymentMutation) {
    let errorMessage = null;
    const error =
      initiatePaymentMutationError?.response || initiatePaypalPaymentMutationError?.response;

    const firstError = (error as PaymentErrorResponse).errors[0];
    let parsedErrorMessage;
    let errorType = firstError?.errorInfo?.globalErrTextTemplate;
    if (!errorType) {
      parsedErrorMessage = JSON.parse(firstError?.message ?? '{}');
      errorType = parsedErrorMessage?.globalErrTextTemplate;
    }

    analytics.update({
      validation: errorType,
    });

    if (isErrorInitiatePaypalPaymentMutation) {
      const declineReason = firstError?.errorInfo?.debugMessage ?? parsedErrorMessage?.debugMessage;
      analytics.update({
        paypalDeclined: true,
        paypalDeclineReason: declineReason ?? '',
      });
      return t(`errors.${errorType}`, {
        defaultValue: t('errors.paypal.generic.error'),
      });
    }
    switch (errorType) {
      case 'fraud_check_failed': {
        const reasonForStayLongForm = currentReasonForStay === 'LEI' ? 'leisure' : 'business';
        const errorKey = `errors.booking.fraud.${reasonForStayLongForm}.${hotelBrand?.toLowerCase()}`;
        errorMessage = t(errorKey);
        break;
      }
      case 'errors.payment.generic':
        errorMessage = t('errors.payment.generic');
        break;

      default:
        errorMessage = t('errors.sorry');
        break;
    }

    return errorMessage;
  }
};

export const getA2CBusinessAllowances = (charges: string[]) => [
  {
    allowance: ACCOUNT_TO_COMPANY_ALLOWANCES.BREAKFAST,
    budget: 0,
    isAuthorised: charges.includes(ACCOUNT_TO_COMPANY_ALLOWANCES.BREAKFAST),
  },
  {
    allowance: ACCOUNT_TO_COMPANY_ALLOWANCES.CAR_PARKING,
    budget: 0,
    isAuthorised: charges.includes(ACCOUNT_TO_COMPANY_ALLOWANCES.CAR_PARKING),
  },
];

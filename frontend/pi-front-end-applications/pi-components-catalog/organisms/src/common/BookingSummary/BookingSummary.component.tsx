import {
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  BookingSummaryTotalCostProps,
  BookingSummaryUpgradeToFlexProps,
  BookingSummaryVariantType,
  MealsSelectionDetailsPerRoom,
} from '@whitbread-eos/api';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useCallback, useEffect, useState } from 'react';

import BookingSummaryCard from './BookingSummaryCard';
import BookingSummaryMobile from './BookingSummaryMobile';

export interface Props {
  variant: BookingSummaryVariantType;
  prefixDataTestId?: string;
  bookingSummaryData: BookingSummaryDataProps;
  reservationDetails: BookingDataReservationDetailsProps;
  t: (x: string, y?: { [key: string]: string }) => string;
  language: string | undefined;
  infoMessages?: string[];
  updateToFlex?: BookingSummaryUpgradeToFlexProps;
  announcement?: any;
  isDiscountApplied?: boolean;
  taxesMessage?: string;
  isExtrasDisplayed?: boolean;
  isSoftBundlesVisible?: boolean;
  isCityTaxBreakdownEnabled?: boolean;
}

export default function BookingSummary({
  variant,
  bookingSummaryData,
  reservationDetails,
  t,
  language,
  prefixDataTestId,
  infoMessages,
  announcement,
  isDiscountApplied,
  taxesMessage,
  isExtrasDisplayed,
  isSoftBundlesVisible,
  isCityTaxBreakdownEnabled,
}: Readonly<Props>) {
  const baseDataTestId = formatDataTestId(prefixDataTestId, 'BookingSummary');
  const [totalCostAmount, setTotalCostAmount] = useState(0);
  const calculateTotalCostWithoutDonation = useCallback(
    (totalCostData: BookingSummaryTotalCostProps, noNights: number): number => {
      const initial = totalCostData.initialTotalCost ?? 0;

      const totalMealsPrice = totalCostData?.meals
        ? totalCostData.meals.reduce((prev: number, current: MealsSelectionDetailsPerRoom) => {
            const adultsMealsPrice = current.adultsMeals.reduce((prev, current) => {
              const actualPrice = current.price ?? 0;
              return prev + actualPrice * current.noSelections * noNights;
            }, 0);
            const childrenMealsPrice = current.childrenMeals.reduce((prev, current) => {
              const actualPrice = current.price ?? 0;
              return prev + actualPrice * current.noSelections * noNights;
            }, 0);
            return prev + adultsMealsPrice + childrenMealsPrice;
          }, 0)
        : 0;

      return initial + totalMealsPrice;
    },
    [bookingSummaryData, reservationDetails]
  );

  useEffect(() => {
    setTotalCostAmount(
      bookingSummaryData.totalCost
        ? calculateTotalCostWithoutDonation(
            bookingSummaryData.totalCost,
            reservationDetails.noNights
          )
        : 0
    );
  }, [bookingSummaryData, reservationDetails]);

  if (variant === 'mobile') {
    return (
      <BookingSummaryMobile
        t={t}
        language={language}
        totalCostAmount={totalCostAmount}
        reservationDetails={reservationDetails}
        bookingSummaryData={bookingSummaryData}
        infoMessages={infoMessages}
        prefixDataTestId={`${baseDataTestId}-MobileVariant`}
        announcement={announcement}
        taxesMessage={taxesMessage}
        isExtrasDisplayed={isExtrasDisplayed}
        isSoftBundlesVisible={isSoftBundlesVisible}
        isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
      />
    );
  }

  return (
    <BookingSummaryCard
      t={t}
      language={language}
      totalCostAmount={totalCostAmount}
      bookingSummaryData={bookingSummaryData}
      prefixDataTestId={`${baseDataTestId}-DesktopVariant`}
      currencyCode={reservationDetails.currency}
      isDiscountApplied={isDiscountApplied}
      taxesMessage={taxesMessage}
      isExtrasDisplayed={isExtrasDisplayed}
      isSoftBundlesVisible={isSoftBundlesVisible}
      isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
    />
  );
}

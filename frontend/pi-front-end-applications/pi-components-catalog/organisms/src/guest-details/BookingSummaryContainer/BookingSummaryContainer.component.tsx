import { Box, Text, TextProps } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import type {
  BookingDataReservationDetailsProps,
  BookingSummaryDataProps,
  BookingSummaryVariantType,
} from '@whitbread-eos/api';
import {
  Area,
  ReservationById,
  MealItem,
  MealKids,
  SelectedMealsPerRoom,
  UPDATE_ANCILLARIES_RATE_CODE,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
} from '@whitbread-eos/api';
import { Button, Info, Notification } from '@whitbread-eos/atoms';
import {
  adultsMealsSelector,
  bookingGuestCount,
  calculateTotalCostRoomSelection,
  childrenMealsSelector,
  extrasPackagesMapperSelector,
  formatDataTestId,
  getImportantMessages,
  getNightsNumber,
  hotelInformationSelector,
  mealsMapperSelector,
  renderSanitizedHtml,
  roomInformationSelector,
  roomPackageSelection,
  selectedMealsPerRoomSelector,
  useMutationRequest,
  useLocalStorage,
} from '@whitbread-eos/utils';
import { useCallback, useEffect, useState } from 'react';

import { BookingSummary } from '../../common';

export interface Props {
  packages: any;
  bkngData: any;
  hiData: any;
  biQueryInput: any;
  basketReferenceId: string;
  variant: BookingSummaryVariantType;
  t: (id: string) => string;
  language: string | undefined;
  taxesMessage?: string;
  area?: string;
  isExtrasDisplayed?: boolean;
  submitButtonDisabled?: boolean;
  isCityTaxBreakdownEnabled?: boolean;
  isSoftBundlesVisible?: boolean;
}

export default function BookingSummaryContainer({
  packages,
  bkngData,
  hiData,
  biQueryInput,
  basketReferenceId,
  variant,
  t,
  language,
  taxesMessage,
  area,
  isExtrasDisplayed,
  submitButtonDisabled,
  isCityTaxBreakdownEnabled,
  isSoftBundlesVisible,
}: Readonly<Props>) {
  const queryClient = useQueryClient();
  const firstRoom = bkngData?.bookingInformation?.reservationByIdList[0] || {};
  const baseDataTestId = 'BookingSummaryContainer';
  const noNights = getNightsNumber(
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );
  const [selectedMeals, setSelectedMeals] = useState<SelectedMealsPerRoom[]>([]);
  const { roomSelection = [] } = packages ?? {};

  const adultsMeals: MealItem[] = adultsMealsSelector(packages?.meals, noNights);

  const childrenMeals: MealKids[] = childrenMealsSelector(packages?.mealsKids);

  const reservationDetails: BookingDataReservationDetailsProps = {
    arrivalDate: firstRoom?.roomStay?.arrivalDate || null,
    departureDate: firstRoom?.roomStay?.departureDate || null,
    currency: bkngData?.bookingInformation?.currencyCode,
    noRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
    noNights,
  };
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );
  const { mutation: urcMutation, isSuccess: urcIsSuccess } = useMutationRequest(
    UPDATE_ANCILLARIES_RATE_CODE,
    undefined,
    undefined,
    {
      onSuccess: () => {
        if (area === Area.CCUI) {
          queryClient.invalidateQueries({
            queryKey: [
              'getPaymentMethodsCCUI',
              biQueryInput.language,
              biQueryInput.country,
              biQueryInput.basketReference,
            ],
          });
          return;
        }
        if (area === Area.PI) {
          queryClient.invalidateQueries({
            queryKey: [
              'getPaymentMethods',
              biQueryInput.language,
              biQueryInput.country,
              biQueryInput.basketReference,
            ],
          });
        }
      },
    }
  );

  const updateRateCode = useCallback(() => {
    const flexRateCode = bkngData?.bookingInformation?.upgradeToFlex?.flexRateCode;
    const upgradeCurrency = bkngData?.bookingInformation?.upgradeToFlex?.currency;

    if (!flexRateCode || !upgradeCurrency) {
      return;
    }

    const roomTypes: Array<string> = bkngData?.bookingInformation?.reservationByIdList.map(
      (room: ReservationById) => room?.roomStay?.roomExtraInfo?.roomType
    );
    const guestCount = bookingGuestCount(bkngData?.bookingInformation?.reservationByIdList);

    urcMutation.mutate({
      basketReferenceId: basketReferenceId,
      rateCode: flexRateCode,
      hotelId: bkngData?.bookingInformation?.hotelId,
      startDate: firstRoom.roomStay?.arrivalDate,
      endDate: firstRoom.roomStay?.departureDate,
      roomType: roomTypes,
      currency: upgradeCurrency,
      adultsNumber: guestCount.adultsNumber,
      childrenNumber: guestCount.childrenNumber,
    });
  }, [basketReferenceId, bkngData, firstRoom, urcMutation]);

  const { extrasItems = [] } = packages ?? {};

  const extrasPriceById = new Map<string, number>(
    extrasItems
      .filter((item: any) => typeof item?.id === 'string' && typeof item?.price === 'number')
      .map((item: any) => [item.id, item.price])
  );
  const priceExtrasTotal = roomSelection.reduce((total: number, room: any) => {
    const roomExtrasTotal = (room?.packagesSelection ?? []).reduce(
      (roomTotal: number, packageItem: any) => {
        const unitPrice = extrasPriceById.get(packageItem?.id) ?? 0;
        const qty =
          typeof packageItem?.noOfSelections === 'number' ? packageItem.noOfSelections : 1;
        return roomTotal + unitPrice * qty;
      },
      0
    );
    return total + roomExtrasTotal;
  }, 0);

  const initialTotalCost =
    bkngData?.bookingInformation?.totalCost -
    calculateTotalCostRoomSelection(adultsMeals, roomSelection, noNights);

  const bookingSummaryData: BookingSummaryDataProps = {
    hotelInformation:
      hiData?.hotelInformation && hotelInformationSelector(hiData?.hotelInformation),
    totalCost: {
      showVATMessage: true,
      currency: bkngData?.bookingInformation?.currencyCode,
      initialTotalCost: initialTotalCost,
      meals: selectedMealsPerRoomSelector(selectedMeals, adultsMeals, childrenMeals),
    },
    rateInformation: {
      rate: firstRoom.roomStay?.rateExtraInfo?.rateName,
      noNights: noNights,
      noRooms: bkngData?.bookingInformation?.reservationByIdList?.length,
      rateDescription: basketDetailsState?.rateDescription || '',
      rateTags: basketDetailsState?.rateTags,
    },
    stayDatesInformation: {
      arrivalDate: firstRoom.roomStay?.arrivalDate || null,
      departureDate: firstRoom.roomStay?.departureDate || null,
      noNights: noNights,
    },
    updateToFlex: {
      showUpgradeToFlex:
        basketDetailsState?.rateTags && basketDetailsState?.rateTags?.length > 0
          ? false
          : !!bkngData?.bookingInformation?.upgradeToFlex?.flexRateCode &&
            !!bkngData?.bookingInformation?.upgradeToFlex?.currency,
      currency: bkngData?.bookingInformation?.upgradeToFlex?.currency,
      amount:
        bkngData?.bookingInformation?.upgradeToFlex?.amount - (initialTotalCost - priceExtrasTotal),
      upgradeToFlexCallBack: updateRateCode,
      initialRate: bkngData?.bookingInformation?.totalCost,
    },
    roomInformation: roomInformationSelector(
      bkngData?.bookingInformation?.reservationByIdList,
      selectedMeals,
      adultsMeals,
      childrenMeals,
      roomPackageSelection(extrasPackagesMapperSelector(roomSelection))
    ),
    cityTaxTotal: bkngData?.bookingInformation?.cityTaxTotal || '',
  };

  const listOfImportantMessages: string[] = getImportantMessages(
    hiData?.hotelInformation?.importantInfo?.infoItems,
    firstRoom?.roomStay?.arrivalDate,
    firstRoom?.roomStay?.departureDate
  );

  const infoMessages = [
    ...(listOfImportantMessages?.length > 0 ? [listOfImportantMessages].flat() : []),
  ];

  useEffect(() => {
    if (bkngData?.bookingInformation?.reservationByIdList.length > 0) {
      let preselectedMeals: SelectedMealsPerRoom[] = [];

      if (roomSelection?.length && packages?.meals && packages?.mealsKids) {
        preselectedMeals = mealsMapperSelector(packages?.meals, packages?.mealsKids, roomSelection);
        setSelectedMeals(preselectedMeals);
      }
    }
  }, [bkngData, roomSelection, packages]);

  useEffect(() => {
    if (urcIsSuccess) {
      queryClient.invalidateQueries({
        queryKey: [
          'GetBookingInformation',
          biQueryInput.language,
          biQueryInput.country,
          biQueryInput.basketReference,
        ],
      });
    }
  }, [urcIsSuccess, biQueryInput, queryClient]);

  return (
    <Box data-testid={baseDataTestId}>
      <BookingSummary
        variant={variant}
        t={t}
        language={language}
        reservationDetails={reservationDetails}
        bookingSummaryData={bookingSummaryData}
        infoMessages={infoMessages}
        taxesMessage={taxesMessage}
        isExtrasDisplayed={isExtrasDisplayed}
        isCityTaxBreakdownEnabled={isCityTaxBreakdownEnabled}
        isSoftBundlesVisible={isSoftBundlesVisible}
      />
      {variant == 'desktop' && (
        <>
          <Button
            type="submit"
            form="guestDetailsForm"
            size="full"
            mt="lg"
            variant="primary"
            data-testid="BookingSummary-ContinueButton"
            isDisabled={submitButtonDisabled}
          >
            <Text {...continueTextStyle}>{t('booking.summary.continue')}</Text>
          </Button>
          <Box
            pt="sm"
            data-testid={formatDataTestId(baseDataTestId, 'BookingSummary-InfoMessages')}
          >
            {infoMessages?.map((notification: string) => {
              if (notification.length) {
                return (
                  <Box mt="md" key={notification}>
                    <Notification
                      maxWidth="full"
                      variant="info"
                      status="info"
                      description={
                        <Box className="formatLinks">{renderSanitizedHtml(notification)}</Box>
                      }
                      svg={<Info />}
                    />
                  </Box>
                );
              }
            })}
          </Box>
        </>
      )}
    </Box>
  );
}

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
} as TextProps;

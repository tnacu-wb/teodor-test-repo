import type { BoxProps, GridItemProps, GridProps } from '@chakra-ui/react';
import { Box, Grid, GridItem } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  BOOK_MUTATION,
  Channel,
  GET_HOTEL_INVENTORY_QUERY,
  BookingFlowItem,
  HIVisualDisplayContext,
  FS_SILENT_SUBSTITUTION,
  ReservationRoomType,
  BookingChannelCriteria,
  PageName,
  ACCESSIBLE_BARRIER_FREE,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  Claims,
  RoomReservation,
} from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import {
  AccessibleRoomTypeOptions,
  RoomChoiceGallery,
  BackButton,
  Basket,
  ChooseRoomContinueBtn,
  SEO as Seo,
} from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  getAuthCookie,
  useCustomLocale,
  useLocalStorage,
  useMutationRequest,
  useQueryRequest,
  useFeatureSwitch,
  updateSilentSubstLocalStorage,
  getAccessibleRoomData,
  getSelectedRoomClassCode,
  getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { produce } from 'immer';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

import usePreviousBookingReuse from '~hooks/use-previous-booking-reuse';

interface Props {
  queryClient: QueryClient;
  visualDisplayContext: HIVisualDisplayContext;
  channel: Channel;
  user?: Claims;
  setAnalyticsUser?: any;
}

interface RoomAvailability {
  availableCount: number;
  code: string;
}

export default function ChooseRoomTypePageCCUI({
  queryClient,
  visualDisplayContext,
  channel,
  user,
  setAnalyticsUser,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const baseDataTestId = 'ChooseRoomTypePage';
  const { isLessThanMd, isLessThanLg } = visualDisplayContext;
  const [roomTypeSelections, setRoomTypeSelections] = useState<string[]>([]);
  const [accessibleRoomTypeSelections, setAccessibleRoomTypeSelections] = useState<string[]>([]);
  const [isDisabledContinueBtn, setIsDisabledContinueBtn] = useState<boolean>(false);
  const [resRoomTypes, setResRoomTypes] = useState<ReservationRoomType[]>([]);
  const [selectedPMSRoomTypes, setSelectedPMSRoomTypes] = useState<string[]>([]);
  const [selectedSpecialRequests, setSelectedSpecialRequests] = useState<string[][]>([]);
  const [roomsLabelsForSilentSubst, setRoomsLabelsForSilentSubst] = useState<string[]>([]);
  const [roomAvailability, setRoomAvailability] = useState<RoomAvailability[]>([]);

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  const { [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled } = useFeatureToggle();

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const hotelInventoryQueryKey = [
    'getHotelInventory',
    basketDetailsState.hotelId,
    basketDetailsState.departure,
    basketDetailsState.arrival,
  ];
  const {
    isLoading: isLoadingHotelInventory,
    isError: isErrorHotelInventory,
    data: dataHotelInventory,
    error: errorHotelInventory,
  } = useQueryRequest(
    hotelInventoryQueryKey,
    GET_HOTEL_INVENTORY_QUERY,
    {
      hotelId: basketDetailsState.hotelId,
      dateRangeEnd: basketDetailsState.departure,
      dateRangeStart: basketDetailsState.arrival,
    },
    {
      enabled:
        !!basketDetailsState.hotelId &&
        !!basketDetailsState.departure &&
        !!basketDetailsState.arrival,
    }
  );

  const idTokenCookie = getAuthCookie();

  const {
    mutation: bookRsvMutation,
    isLoading: bookRsvIsLoading,
    isError: bookRsvIsError,
    data: bookRsvData,
    error: bookRsvError,
    isSuccess: bookRsvIsSuccess,
  } = useMutationRequest(BOOK_MUTATION, false, idTokenCookie);

  const bookingFlowId = basketDetailsState.bookingFlow?.bookingFlowItems?.find(
    (bfi: BookingFlowItem) => bfi?.rateCategory === basketDetailsState.selectedRate?.rateCategory
  )?.bookingId;

  const {
    shouldSavePreviousData,
    executeBookingWithReuse,
    isLoading: reuseIsLoading,
    isError: reuseIsError,
    error: reuseError,
    isSuccess: reuseIsSuccess,
  } = usePreviousBookingReuse({
    prevReservationId: basketDetailsState.prevReservationId ?? '',
    variant: 'CCUI',
    queryClient,
  });

  useEffect(() => {
    const shouldNavigate =
      !bookRsvError && bookRsvIsSuccess && bookRsvData && !reuseIsError && reuseIsSuccess;

    if (shouldNavigate) {
      const { basketReference } = bookRsvData?.createReservation || '';
      if (basketReference && resRoomTypes && !!isSilentFeatureFlagEnabled)
        if (typeof window !== 'undefined')
          updateSilentSubstLocalStorage(basketReference, resRoomTypes);

      setIsDisabledContinueBtn(true);
      router.push(`/${country}/${language}/ancillaries?reservationId=${basketReference}`);
    } else {
      const roomTypesInAvailability = basketDetailsState.selectedRate?.roomTypes || [];
      const pmsRoomTypesInAvailability = Array.from(
        new Set(
          roomTypesInAvailability.flatMap((roomType) =>
            roomType.rooms.map((room) => room.pmsRoomType)
          )
        )
      );

      const acessibleRoomAvailablity =
        dataHotelInventory?.hotelInventory?.roomTypeInventories?.filter(
          (inventoryItem: { availableCount: number; code: string }) =>
            inventoryItem.availableCount > 0 &&
            pmsRoomTypesInAvailability.includes(inventoryItem.code)
        );

      setRoomAvailability(acessibleRoomAvailablity);

      setIsDisabledContinueBtn(!acessibleRoomAvailablity?.length);
    }
  }, [
    bookRsvIsSuccess,
    bookRsvError,
    bookRsvData,
    reuseIsSuccess,
    reuseIsError,
    router,
    bookingFlowId,
    country,
    language,
    dataHotelInventory,
  ]);

  useEffect(() => {
    const { selectedPMSRoomTypes: pmsRoomTypes, selectedSpecialRequests: specialRequests } =
      getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests(
        roomTypeSelections,
        accessibleRoomTypeSelections,
        basketDetailsState,
        roomAvailability
      );

    setSelectedPMSRoomTypes(pmsRoomTypes);
    setSelectedSpecialRequests(specialRequests);
    setRoomsLabelsForSilentSubst((labels) =>
      specialRequests.map((requests, idx) =>
        requests?.includes('BFRE')
          ? t('accessible.barrierFree')
          : requests?.includes('WETR') || requests?.includes('LOWB')
            ? t('accessible.room')
            : labels?.[idx]
      )
    );
  }, [roomTypeSelections, accessibleRoomTypeSelections, basketDetailsState, roomAvailability]);

  useEffect(() => {
    if (basketDetailsState.silentSubstitutionLabels?.length)
      setRoomsLabelsForSilentSubst(basketDetailsState.silentSubstitutionLabels);
  }, [basketDetailsState]);

  useEffect(() => {
    if (user && setAnalyticsUser) setAnalyticsUser(user, language);
  }, [user, language, setAnalyticsUser]);

  if (!basketDetailsState.hotelId) {
    return null;
  }

  return (
    <QueryClientProvider client={queryClient}>
      <Seo page={PageName.CYB} />
      <Grid
        {...chooseAccesibleRoomGridStyle}
        data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}
      >
        <GridItem
          {...chooseAccessibleRoomContentStyle}
          data-testid={formatDataTestId(baseDataTestId, 'PageContent')}
        >
          {renderPageContent()}
        </GridItem>

        <GridItem {...chooseAccessibleRoomBasketStyle}>
          <Box sx={{ '& [data-testid="basket"]': { width: '100%' } }}>
            <Basket
              roomClassCode={getSelectedRoomClassCode(basketDetailsState?.roomClass, language)}
              variant={channel}
              channel={channel}
              roomClassIndexFromSelectedRate={0} // default to zero as there no other roomClasses such as premier plus set for this room type
              isCityTaxExempt={false}
              isLastFewRooms={false}
              isHDPBasket={false}
              hasAccessibleRoom={true}
              hasTwinRoomChoice={false}
              shouldDisplayMobileBasket={isLessThanLg}
              roomsLabelsForSilentSubst={roomsLabelsForSilentSubst}
              selectedPMSRoomTypes={selectedPMSRoomTypes}
              selectedSpecialRequests={selectedSpecialRequests}
              rateTags={basketDetailsState?.rateTags}
              {...{
                isLessThanLg,
                bookRsvIsLoading: shouldSavePreviousData
                  ? bookRsvIsLoading || reuseIsLoading
                  : bookRsvIsLoading,
                bookRsvIsError: shouldSavePreviousData
                  ? bookRsvIsError || reuseIsError
                  : bookRsvIsError,
                bookRsvError: bookRsvError || reuseError,
                bookRsvIsSuccess: shouldSavePreviousData
                  ? bookRsvIsSuccess && reuseIsSuccess
                  : bookRsvIsSuccess,
                handleBooking,
                ...basketDetailsState,
                isDisabledContinueBtn,
                isSilentFeatureFlagEnabled,
                setResRoomTypes,
                isCityTaxBreakdownEnabled,
                isCityTaxEnabled: basketDetailsState.isCityTaxEnabled,
              }}
            />
          </Box>

          {basketDetailsState.phoneNumber && (
            <Box mt="lg" data-testid={formatDataTestId(baseDataTestId, 'AccessibleNotification')}>
              <Notification
                maxWidth="full"
                variant="infoGrey"
                status="info"
                isInnerHTML
                description={t('booking.hotel.summary.accessibleContact', {
                  PhoneNumber: { hotelNumber: basketDetailsState.phoneNumber },
                })}
                svg={<Info />}
              />
            </Box>
          )}
        </GridItem>
      </Grid>
    </QueryClientProvider>
  );

  function renderPageContent() {
    return (
      <>
        <RoomChoiceGallery roomType={ACCESSIBLE_BARRIER_FREE} {...{ isLessThanMd, isLessThanLg }} />

        <Box mt={{ base: 'md', xs: 'lg', sm: '3xl', lg: 'xl' }}>
          <AccessibleRoomTypeOptions
            data={getAccessibleRoomData(basketDetailsState.selectedRate?.roomTypes, true)}
            accessibleRoomSelections={accessibleRoomTypeSelections}
            roomTypeSelections={roomTypeSelections}
            onRoomTypeSelection={handleRoomTypeSelection}
            onAccessibleRoomSelection={handleAccessibleRoomSelection}
            hotelInventoryResponse={{
              isLoadingHotelInventory,
              isErrorHotelInventory,
              dataHotelInventory,
              errorHotelInventory,
            }}
            selectedPMSRoomTypes={selectedPMSRoomTypes}
          />
        </Box>

        <Box {...continueButtonSectionStyle}>
          <ChooseRoomContinueBtn
            selectedPMSRoomTypes={selectedPMSRoomTypes}
            selectedSpecialRequests={selectedSpecialRequests}
            {...{
              bookRsvIsLoading,
              bookRsvIsError,
              bookRsvError,
              handleBooking,
              dataTestId: baseDataTestId,
              channel,
              isDisabledContinueBtn,
            }}
          />
        </Box>

        <BackButton prefixDataTestId={baseDataTestId} />
      </>
    );
  }

  function handleBooking(reservations: RoomReservation[], bookingChannel: BookingChannelCriteria) {
    executeBookingWithReuse(bookRsvMutation, reservations, bookingChannel, bookingFlowId);
  }

  function handleRoomTypeSelection(roomIndex: number, selection: string) {
    setRoomTypeSelections((state) =>
      produce(state, (draft) => {
        draft[roomIndex] = selection;
      })
    );
  }

  function handleAccessibleRoomSelection(roomIndex: number, selection: string) {
    setAccessibleRoomTypeSelections((state) =>
      produce(state, (draft) => {
        draft[roomIndex] = selection;
      })
    );
  }
}

const chooseAccesibleRoomGridStyle = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  pb: {
    mobile: 'md',
    md: 'lg',
    lg: 'xl',
    xl: '5xl',
  },
  pt: {
    mobile: '0',
    lg: '5xl',
  },
  m: '0!important',
  templateColumns: {
    mobile: '1fr',
    lg: '1fr auto',
  },
  columnGap: {
    mobile: '0',
    lg: '32',
    xl: '8.5rem',
  },
} as GridProps;

const chooseAccessibleRoomContentStyle = {
  pt: {
    mobile: 'lg',
    md: '2xl',
    lg: '0',
  },
} as GridItemProps;

const chooseAccessibleRoomBasketStyle = {
  w: {
    lg: '72',
    xl: '19.31rem',
  },
} as GridItemProps;

const continueButtonSectionStyle = {
  mt: '3xl',
  width: { mobile: 'full', md: '72' },
} as BoxProps;

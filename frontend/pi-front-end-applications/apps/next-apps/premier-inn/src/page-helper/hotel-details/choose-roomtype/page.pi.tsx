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
  useAuthToken,
  useCustomLocale,
  useLocalStorage,
  useMutationRequest,
  useQueryRequest,
  useFeatureSwitch,
  updateSilentSubstLocalStorage,
  getAccessibleRoomData,
  getSelectedRoomClassCode,
  useFeatureToggle,
  getAccessibleRoomSelectedPMSRoomTypesAndSpecialRequests,
} from '@whitbread-eos/utils';
import { produce } from 'immer';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';

interface Props {
  queryClient: QueryClient;
  visualDisplayContext: HIVisualDisplayContext;
  channel: Channel;
}

interface RoomAvailability {
  availableCount: number;
  code: string;
}

export default function ChooseRoomTypePagePI({
  queryClient,
  visualDisplayContext,
  channel,
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

  const { [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled } = useFeatureToggle();

  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

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
    isSuccess: isSuccessHotelInventory,
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

  const { token } = useAuthToken();

  const {
    mutation: bookRsvMutation,
    isLoading: bookRsvIsLoading,
    isError: bookRsvIsError,
    data: bookRsvData,
    error: bookRsvError,
    isSuccess: bookRsvIsSuccess,
  } = useMutationRequest(BOOK_MUTATION, false, token);

  const bookingFlowId = basketDetailsState.bookingFlow?.bookingFlowItems?.find(
    (bfi: BookingFlowItem) => bfi?.rateCategory === basketDetailsState.selectedRate?.rateCategory
  )?.bookingId;

  useEffect(() => {
    const handleBasketDetails = async () => {
      if (!bookRsvError && bookRsvIsSuccess && bookRsvData) {
        const { basketReference } = bookRsvData?.createReservation || '';

        if (basketReference && resRoomTypes && !!isSilentFeatureFlagEnabled)
          if (typeof window !== 'undefined')
            updateSilentSubstLocalStorage(basketReference, resRoomTypes);

        setIsDisabledContinueBtn(true);
        router.push(
          `/${country}/${language}${
            bookingFlowId ? `/${bookingFlowId}` : ''
          }/ancillaries?reservationId=${basketReference}`
        );
      } else {
        const roomTypesInAvailability = basketDetailsState.selectedRate?.roomTypes;
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

        const usage: any = {};
        const areAllRoomTypesAvailable = basketDetailsState.selectedRate?.roomTypes.every(
          (item) => {
            return item.rooms.some((room) => {
              const inv = dataHotelInventory?.hotelInventory?.roomTypeInventories.find(
                (i: { code: string }) => i.code === room.pmsRoomType
              );
              if (!inv) return false;
              const used = (usage[room.pmsRoomType] || 0) + 1;
              if (used > inv.availableCount) return false;
              usage[room.pmsRoomType] = used;
              return true;
            });
          }
        );

        setIsDisabledContinueBtn(!areAllRoomTypesAvailable);
      }
    };
    handleBasketDetails();
  }, [
    bookRsvIsSuccess,
    bookRsvError,
    bookRsvData,
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
                bookRsvIsLoading,
                bookRsvIsError,
                bookRsvError,
                handleBooking,
                ...basketDetailsState,
                isDisabledContinueBtn,
                isSilentFeatureFlagEnabled,
                setResRoomTypes,
                isCityTaxBreakdownEnabled,
                isCityTaxEnabled: basketDetailsState.isCityTaxEnabled,
              }}
              softBundles={basketDetailsState.softBundles}
              isSoftBundlesVisible={basketDetailsState.isSoftBundlesVisible}
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
          {isSuccessHotelInventory && (
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
          )}
        </Box>

        <Box {...continueButtonSectionStyle}>
          <ChooseRoomContinueBtn
            selectedPMSRoomTypes={selectedPMSRoomTypes}
            selectedSpecialRequests={selectedSpecialRequests}
            softBundles={basketDetailsState.softBundles}
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

  function handleBooking(reservations: Array<unknown>, bookingChannel: BookingChannelCriteria) {
    bookRsvMutation.mutate({ reservations, bookingChannel, bookingFlowId });
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

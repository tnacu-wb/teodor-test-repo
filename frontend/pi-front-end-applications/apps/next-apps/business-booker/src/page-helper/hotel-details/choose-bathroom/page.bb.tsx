import type { BoxProps, GridItemProps, GridProps } from '@chakra-ui/react';
import { Box, Grid, GridItem } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  BASKET_DETAILS_STORAGE_KEY,
  BOOK_MUTATION,
  Channel,
  GET_HOTEL_INVENTORY_QUERY,
  HIVisualDisplayContext,
  ROOM_TYPE,
  FS_SILENT_SUBSTITUTION,
  ReservationRoomType,
  BookingChannelCriteria,
  PageName,
  ACCESSIBLE_ROOM_TYPES,
  FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE,
  loweredBathRoomTypes,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
} from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import {
  AccessibleBathroomOptions,
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
  getBathRoomSelectedPMSRoomTypesAndSpecialRequests,
  getSelectedRoomClassCode,
  getRoomClassTextForGAllery,
  useFeatureToggle,
  getBookingFlowId,
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

export default function ChooseBathroomPageBB({
  queryClient,
  visualDisplayContext,
  channel,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const baseDataTestId = 'ChooseBathroomPage';
  const { isLessThanMd, isLessThanLg } = visualDisplayContext;
  const [roomTypeSelections, setRoomTypeSelections] = useState<string[]>([]);
  const [bathroomSelections, setBathroomSelections] = useState<string[]>([]);
  const [isDisabledContinueBtn, setIsDisabledContinueBtn] = useState<boolean>(false);
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  const [resRoomTypes, setResRoomTypes] = useState<ReservationRoomType[]>([]);
  const [selectedPMSRoomTypes, setSelectedPMSRoomTypes] = useState<string[]>([]);
  const [selectedSpecialRequests, setSelectedSpecialRequests] = useState<string[][]>([]);

  const {
    [FT_PI_CCUI_BB_PREM_PLUS_ACCESSIBLE]: isPremPlusAccFeatureFlag,
    [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled,
  } = useFeatureToggle();

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

  useEffect(() => {
    if (!bookRsvError && bookRsvIsSuccess && bookRsvData) {
      setIsDisabledContinueBtn(true);
      const { basketReference } = bookRsvData?.createReservation || null;
      if (basketReference && resRoomTypes && !!isSilentFeatureFlagEnabled) {
        if (typeof window !== 'undefined') {
          updateSilentSubstLocalStorage(basketReference, resRoomTypes);
        }
      }
      router.push(
        `/${country}/${language}/business-booker/booking-business/guest-details?reservationId=${basketReference}`
      );
    } else {
      const roomTypesInAvailability = basketDetailsState.selectedRate?.roomTypes || [];
      const pmsRoomTypesInAvailability = Array.from(
        new Set(
          roomTypesInAvailability.flatMap((roomType) =>
            roomType.rooms.map((room) => room.pmsRoomType)
          )
        )
      );
      const isAcessibleRoomAvailableObj =
        dataHotelInventory?.hotelInventory?.roomTypeInventories?.find(
          (inventoryItem: { availableCount: number; code: string }) => {
            return (
              ACCESSIBLE_ROOM_TYPES.includes(inventoryItem.code) &&
              inventoryItem.availableCount > 0 &&
              pmsRoomTypesInAvailability.includes(inventoryItem.code)
            );
          }
        );

      isAcessibleRoomAvailableObj
        ? setIsDisabledContinueBtn(false)
        : setIsDisabledContinueBtn(true);

      loweredBathRoomTypes.includes(isAcessibleRoomAvailableObj?.code)
        ? setBathroomSelections([ROOM_TYPE.LOWERED])
        : setBathroomSelections([ROOM_TYPE.WET]);
    }
  }, [bookRsvIsSuccess, bookRsvError, bookRsvData, router, country, language, dataHotelInventory]);

  useEffect(() => {
    const { selectedPMSRoomTypes: pmsRoomTypes, selectedSpecialRequests: specialRequests } =
      getBathRoomSelectedPMSRoomTypesAndSpecialRequests(
        t,
        isPremPlusAccFeatureFlag,
        roomTypeSelections,
        bathroomSelections,
        basketDetailsState,
        language
      );

    setSelectedPMSRoomTypes(pmsRoomTypes);
    setSelectedSpecialRequests(specialRequests);
  }, [roomTypeSelections, bathroomSelections, basketDetailsState]);

  if (!basketDetailsState.hotelId) {
    return null;
  }

  return (
    <QueryClientProvider client={queryClient}>
      <Seo page={PageName.CYB} />
      <Grid {...chooseBathroomGridStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <GridItem
          {...chooseBathroomContentStyle}
          data-testid={formatDataTestId(baseDataTestId, 'PageContent')}
        >
          {renderPageContent()}
        </GridItem>

        <GridItem {...chooseBathroomBasketStyle}>
          <Box sx={{ '& [data-testid="basket"]': { width: '100%' } }}>
            <Basket
              roomClassCode={getSelectedRoomClassCode(basketDetailsState?.roomClass, language)}
              variant="BB"
              channel={channel}
              roomClassIndexFromSelectedRate={0} // default to zero as there no other roomClasses such as premier plus set for this room type
              isCityTaxExempt={false}
              isLastFewRooms={false}
              isHDPBasket={false}
              hasAccessibleRoom={true}
              hasTwinRoomChoice={false}
              shouldDisplayMobileBasket={isLessThanLg}
              roomsLabelsForSilentSubst={basketDetailsState.silentSubstitutionLabels}
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
        <RoomChoiceGallery
          roomType={getRoomClassTextForGAllery(
            basketDetailsState,
            language,
            isPremPlusAccFeatureFlag
          )}
          {...{ isLessThanMd, isLessThanLg }}
        />

        <Box mt={{ base: 'md', xs: 'lg', sm: '3xl', lg: 'xl' }}>
          <AccessibleBathroomOptions
            data={getAccessibleRoomData(basketDetailsState.selectedRate?.roomTypes)}
            bathroomSelections={bathroomSelections}
            roomTypeSelections={roomTypeSelections}
            onRoomTypeSelection={handleRoomTypeSelection}
            onBathroomSelection={handleBathroomSelection}
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

  function handleBooking(reservations: Array<unknown>, bookingChannel: BookingChannelCriteria) {
    const bookingFlowId = getBookingFlowId(basketDetailsState.brand);
    bookRsvMutation.mutate({
      reservations,
      bookingChannel,
      bookingFlowId,
    });
  }

  function handleRoomTypeSelection(roomIndex: number, selection: string) {
    setRoomTypeSelections((state) =>
      produce(state, (draft) => {
        draft[roomIndex] = selection;
      })
    );
  }

  function handleBathroomSelection(roomIndex: number, selection: string) {
    setBathroomSelections((state) =>
      produce(state, (draft) => {
        draft[roomIndex] = selection;
      })
    );
  }
}

const chooseBathroomGridStyle = {
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

const chooseBathroomContentStyle = {
  pt: {
    mobile: 'lg',
    md: '2xl',
    lg: '0',
  },
} as GridItemProps;

const chooseBathroomBasketStyle = {
  w: {
    lg: '72',
    xl: '19.31rem',
  },
} as GridItemProps;

const continueButtonSectionStyle = {
  mt: '3xl',
  width: { mobile: 'full', md: '72' },
} as BoxProps;

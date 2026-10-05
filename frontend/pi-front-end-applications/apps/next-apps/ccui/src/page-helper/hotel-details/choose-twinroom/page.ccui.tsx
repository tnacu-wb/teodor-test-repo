import type { BoxProps, GridItemProps, GridProps } from '@chakra-ui/react';
import { Box, Grid, GridItem } from '@chakra-ui/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import {
  BOOK_MUTATION,
  Channel,
  HIVisualDisplayContext,
  BASKET_DETAILS_STORAGE_KEY,
  BASKET_DETAILS_STATE_INITIAL_VALUE,
  FS_SILENT_SUBSTITUTION,
  ReservationRoomType,
  BookingChannelCriteria,
  BookingFlowItem,
  PageName,
  FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN,
  Claims,
  RoomReservation,
} from '@whitbread-eos/api';
import { Info, Notification } from '@whitbread-eos/atoms';
import {
  RoomChoiceGallery,
  TwinroomOptions,
  BackButton,
  Basket,
  ChooseRoomContinueBtn,
  SEO as Seo,
} from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  useCustomLocale,
  useLocalStorage,
  useMutationRequest,
  getSelectedPMSRoomTypesAndSpecialRequests,
  getTwinRoomSelectionPrices,
  useFeatureSwitch,
  updateSilentSubstLocalStorage,
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

export default function ChooseTwinroomPageCCUI({
  queryClient,
  visualDisplayContext,
  channel,
  user,
  setAnalyticsUser,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const router = useRouter();
  const { language, country } = useCustomLocale();
  const baseDataTestId = 'ChooseTwinroomPage';
  const { isLessThanMd, isLessThanLg } = visualDisplayContext;
  const [twinroomSelections, setTwinroomSelections] = useState<string[]>([]);
  const [isDisabledContinueBtn, setIsDisabledContinueBtn] = useState<boolean>(false);
  const [basketDetailsState] = useLocalStorage(
    BASKET_DETAILS_STORAGE_KEY,
    BASKET_DETAILS_STATE_INITIAL_VALUE
  );

  const [resRoomTypes, setResRoomTypes] = useState<ReservationRoomType[]>([]);
  const [selectedPMSRoomTypes, setSelectedPMSRoomTypes] = useState<string[]>([]);
  const [selectedSpecialRequests, setSelectedSpecialRequests] = useState<string[][]>([]);

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const { [FT_PI_PIB_CCUI_CITY_TAX_BREAKDOWN]: isCityTaxBreakdownEnabled } = useFeatureToggle();

  const {
    mutation: bookRsvMutation,
    isLoading: bookRsvIsLoading,
    isError: bookRsvIsError,
    data: bookRsvData,
    error: bookRsvError,
    isSuccess: bookRsvIsSuccess,
  } = useMutationRequest(BOOK_MUTATION);

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
      if (basketReference && resRoomTypes && !!isSilentFeatureFlagEnabled) {
        if (typeof window !== 'undefined') {
          updateSilentSubstLocalStorage(basketReference, resRoomTypes);
        }
      }
      setIsDisabledContinueBtn(true);
      const redirectUrl = `/${country}/${language}/ancillaries?reservationId=${basketReference}`;
      window.history.replaceState(null, '', redirectUrl);
      router.replace(redirectUrl);
    } else {
      setIsDisabledContinueBtn(false); // enable continue button if error and need to resubmit
    }
  }, [
    bookRsvIsSuccess,
    bookRsvError,
    bookRsvData,
    reuseIsSuccess,
    reuseIsError,
    router,
    country,
    language,
  ]);

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language, setAnalyticsUser]);

  useEffect(() => {
    const { selectedPMSRoomTypes: pmsRoomTypes, selectedSpecialRequests: specialRequests } =
      getSelectedPMSRoomTypesAndSpecialRequests(twinroomSelections, basketDetailsState);

    setSelectedPMSRoomTypes(pmsRoomTypes);
    setSelectedSpecialRequests(specialRequests);
  }, [twinroomSelections, basketDetailsState]);

  if (!basketDetailsState.hotelId) {
    return null;
  }

  return (
    <QueryClientProvider client={queryClient}>
      <Seo page={PageName.CYT} />
      <Grid {...chooseTwinroomGridStyle} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <GridItem
          {...chooseTwinroomContentStyle}
          data-testid={formatDataTestId(baseDataTestId, 'PageContent')}
        >
          {renderPageContent()}
        </GridItem>

        <GridItem {...chooseTwinroomBasketStyle}>
          <Box sx={{ '& [data-testid="basket"]': { width: '100%' } }}>
            <Basket
              variant="CCUI"
              channel={channel}
              roomClassIndexFromSelectedRate={0}
              isCityTaxExempt={false}
              isLastFewRooms={false}
              isHDPBasket={false}
              hasTwinRoomChoice={true}
              hasAccessibleRoom={false}
              shouldDisplayMobileBasket={isLessThanLg}
              twinroomSelections={twinroomSelections}
              roomsLabelsForSilentSubst={basketDetailsState.silentSubstitutionLabels}
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
            <Box mt="lg" data-testid={formatDataTestId(baseDataTestId, 'TwinNotification')}>
              <Notification
                maxWidth="full"
                variant="info"
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
        <RoomChoiceGallery roomType="twinRoom" {...{ isLessThanMd, isLessThanLg }} />
        <Box mt={{ base: 'md', xs: 'lg', sm: '3xl', lg: 'xl' }}>
          <TwinroomOptions
            data={basketDetailsState.selectedRate?.roomTypes}
            twinroomSelections={twinroomSelections}
            onTwinroomSelection={handleTwinroomSelection}
            twinRoomPrices={getTwinRoomSelectionPrices(basketDetailsState?.selectedRate)}
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

  function handleTwinroomSelection(roomIndex: number, selection: string) {
    setTwinroomSelections((state) =>
      produce(state, (draft) => {
        draft[roomIndex] = selection;
      })
    );
  }
}

const chooseTwinroomGridStyle = {
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

const chooseTwinroomContentStyle = {
  pt: {
    mobile: 'lg',
    md: '2xl',
    lg: '0',
  },
} as GridItemProps;

const chooseTwinroomBasketStyle = {
  w: {
    lg: '72',
    xl: '19.31rem',
  },
} as GridItemProps;

const continueButtonSectionStyle = {
  mt: '3xl',
  width: { mobile: 'full', md: '72' },
} as BoxProps;

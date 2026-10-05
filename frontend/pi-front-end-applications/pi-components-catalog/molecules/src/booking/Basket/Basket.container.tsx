import {
  Channel,
  HIRoom,
  HIRoomClass,
  HIRoomRate,
  HIRoomTypeInfoResponse,
  ObjKeyAccessType,
  OfferEnum,
  ReservationRoomType,
  RoomReservation,
  BookingChannelCriteria,
  CompanyProfile,
  CompanyData,
  ANCILLARIES_TABS,
  Area,
  GDP_ACCOMPANYING_GUEST_DETAILS,
  GDP_DIGI_REG_ADDITIONAL_INFO,
  RoomUpgradeContent,
  ACCESSIBLE_ROOM_TYPE,
  PromoKind,
  FT_PI_NO_ROOM_TYPE_SEARCH,
  RoomTypeLabels,
  StandardRoomType,
  ROOM_CODES,
  UserChoice,
  SoftBundles,
  ExtrasId,
} from '@whitbread-eos/api';
import {
  useCustomLocale,
  useLocalStorage,
  getCookie,
  setCookie,
  useFeatureToggle,
  BUNDLE_CHOICE,
  BUNDLE_CHOICE_OPTIONS,
  swapKeysAndValues,
} from '@whitbread-eos/utils';
import type { PromoActionsType } from '@whitbread-eos/utils';
import { useEffect } from 'react';

import BasketComponent from './Basket.component';
import { getTotalReservationAmount } from './Basket.helpers';

export interface BasketProps {
  softBundles?: SoftBundles;
  channel: Channel;
  variant: string;
  roomClassIndexFromSelectedRate: number | undefined;
  roomClassCode?: string;
  roomClass: HIRoomClass;
  isCityTaxExempt: boolean | null;
  isLastFewRooms: boolean | null;
  isHDPBasket: boolean;
  hasAccessibleRoom: boolean;
  hasTwinRoomChoice: boolean;
  shouldDisplayMobileBasket: boolean | undefined | null;
  selectedRate: HIRoomRate;
  hotelId: string;
  arrival: string;
  departure: string;
  numberOfUnits: number;
  numberOfNights: number;
  rateName: string;
  rateTags?: string[];
  roomTypeInformationResponse: HIRoomTypeInfoResponse;
  selectedPMSRoomTypes?: string[];
  selectedSpecialRequests?: string[][];
  isLessThanLg: boolean | undefined;
  bookRsvIsLoading: boolean;
  bookRsvIsError: boolean;
  bookRsvError: unknown;
  bookingFlowId?: string;
  handleBooking: (
    reservations: RoomReservation[],
    bookingChannel: BookingChannelCriteria,
    bookingFlowId?: string
  ) => void;
  brand: string;
  isCityTaxEnabled?: boolean;
  isDisabledContinueBtn?: boolean;
  twinroomSelections?: string[];
  roomsLabelsForSilentSubst?: ObjKeyAccessType;
  isSilentFeatureFlagEnabled?: boolean;
  saveLabelsForSilentSubstAndRedirect?: (value: ReservationRoomType[]) => void;
  setResRoomTypes?: (resRoomType: ReservationRoomType[]) => void;
  setCurrentClassRoomTypes?: (currentClassRoomType: ReservationRoomType[]) => void;
  companyData?: CompanyData;
  selectedRateCategory?: any;
  isPrePopulateBillingAddressEnabled?: boolean;
  upgradeRoomContent?: RoomUpgradeContent;
  promoActions?: PromoActionsType;
  userChoice?: UserChoice[];
  isCityTaxBreakdownEnabled?: boolean;
  isSoftBundlesVisible?: boolean;
  adultsNumber?: number;
  metaSearchConfigs?: { rate: string; code: string }[];
  isRoomOnly?: boolean;
}

interface Mode {
  mode: string;
}

declare global {
  interface Window {
    piConfig: {
      [key: string]: Mode;
      paymentsRedesign: Mode;
      billingAddressCapture: Mode;
      digRegCard: Mode;
      ancillaries: Mode;
      roomPickerRedesign: Mode;
    };
  }
}

export default function Basket({
  channel,
  variant,
  roomClassIndexFromSelectedRate,
  roomClass,
  roomClassCode = '',
  isCityTaxExempt,
  isLastFewRooms,
  isHDPBasket,
  hasAccessibleRoom,
  hasTwinRoomChoice,
  shouldDisplayMobileBasket,
  hotelId,
  arrival,
  departure,
  numberOfUnits,
  selectedRate,
  numberOfNights,
  rateName,
  rateTags,
  roomTypeInformationResponse,
  selectedPMSRoomTypes,
  selectedSpecialRequests,
  isLessThanLg,
  bookRsvIsLoading,
  bookRsvIsError,
  bookRsvError,
  handleBooking,
  bookingFlowId,
  brand,
  isDisabledContinueBtn,
  twinroomSelections,
  roomsLabelsForSilentSubst,
  isSilentFeatureFlagEnabled,
  saveLabelsForSilentSubstAndRedirect,
  setResRoomTypes,
  setCurrentClassRoomTypes,
  companyData,
  selectedRateCategory,
  isPrePopulateBillingAddressEnabled,
  upgradeRoomContent,
  isCityTaxEnabled,
  promoActions,
  userChoice = [],
  softBundles,
  isCityTaxBreakdownEnabled,
  isSoftBundlesVisible,
  adultsNumber,
  metaSearchConfigs,
  isRoomOnly,
}: Readonly<BasketProps>) {
  const { language } = useCustomLocale();

  const ratePlanCode = setSelectedRatePlanCode();
  const { rooms: selectedRateRoomTypeRooms } = selectedRate?.roomTypes?.[0] || {};
  const { [FT_PI_NO_ROOM_TYPE_SEARCH]: isNoRoomTypeSearchEnabled } = useFeatureToggle();
  const roomsIndexes: number[] = selectedRate?.roomTypes?.map((roomType, roomTypeIndex) => {
    const rooms: HIRoom[] = roomType?.rooms || [];

    if (!isHDPBasket) {
      for (let roomIndex = 0; roomIndex < rooms.length; roomIndex++) {
        if (rooms[roomIndex]?.pmsRoomType === selectedPMSRoomTypes?.[roomTypeIndex]) {
          return roomIndex;
        }
      }
    }

    return rooms.findIndex((room: HIRoom) => room.roomClass === roomClassCode);
  });

  const noRoomTypeSearch =
    isNoRoomTypeSearchEnabled &&
    getCookie(BUNDLE_CHOICE) === BUNDLE_CHOICE_OPTIONS.noRoomTypeSearch;

  // Use pmsRoomType (Opera roomType) - for mapping against AEM roomTypeCodes
  const reservationRoomTypes =
    !isSilentFeatureFlagEnabled && selectedPMSRoomTypes?.length
      ? selectedPMSRoomTypes.map((pmsRoomType: string) => {
          return {
            roomLabelCode: pmsRoomType,
            silentSubstitution: false,
            adults: 0,
            children: 0,
            pmsRoomType: '',
            roomClass: roomClassCode,
          };
        })
      : selectedRate?.roomTypes?.map((roomType, roomTypeIndex) => {
          const { pmsRoomType, silentSubstitution } =
            roomType?.rooms[roomsIndexes?.[roomTypeIndex]] || {};
          const shouldSilentSubstitute = !!isSilentFeatureFlagEnabled && silentSubstitution;

          return {
            roomLabelCode: shouldSilentSubstitute
              ? ((roomsLabelsForSilentSubst?.[roomTypeIndex] as string) ?? '')
              : (selectedPMSRoomTypes?.length && selectedPMSRoomTypes[roomTypeIndex]) ||
                pmsRoomType,
            roomClass: roomClassCode,
            silentSubstitution: shouldSilentSubstitute,
            adults: roomType?.adults,
            children: roomType?.children,
            standardRoomType: noRoomTypeSearch ? getRoomTypeLabel(roomType?.roomType) : '',
            pmsRoomType: noRoomTypeSearch
              ? userChoice[roomTypeIndex]?.pmsRoomType
              : roomType?.rooms[roomsIndexes?.[roomTypeIndex]]?.pmsRoomType,
          };
        });

  useEffect(() => {
    setResRoomTypes &&
      isSilentFeatureFlagEnabled &&
      reservationRoomTypes &&
      setResRoomTypes(reservationRoomTypes);
  }, [selectedPMSRoomTypes]);

  useEffect(() => {
    setCurrentClassRoomTypes &&
      reservationRoomTypes &&
      setCurrentClassRoomTypes(reservationRoomTypes);
  }, [roomClass]);

  const { currencyCode } = selectedRateRoomTypeRooms?.[0]?.roomPriceBreakdown || {};
  const dailyPricesPerRoom = selectedRate?.roomTypes?.map(
    (roomType, roomTypeIndex) =>
      roomType?.rooms?.[roomsIndexes?.[roomTypeIndex]]?.roomPriceBreakdown?.dailyPrices
  );

  const isAccessibleType: boolean =
    selectedRate?.roomTypes
      ?.map((roomType) => roomType?.roomType)
      ?.includes(ACCESSIBLE_ROOM_TYPE) ?? false;

  const [, setCompanyProfile] = useLocalStorage<CompanyProfile | undefined>(
    'CompanyProfile',
    undefined
  );

  // HDP default flow
  isHDPBasket && saveLabelsForSilentSubstAndRedirect?.(reservationRoomTypes);

  const totalReservationAmount = getTotalReservationAmount(
    selectedRate?.roomTypes,
    noRoomTypeSearch,
    userChoice,
    roomsIndexes
  );

  const totalCityTaxAmount = selectedRate?.roomTypes
    ?.map(
      (roomType, roomTypeIndex) =>
        roomType?.rooms[roomsIndexes?.[roomTypeIndex]]?.roomPriceBreakdown?.totalCityTaxAmount
    )
    ?.reduce((sum, pricePerRoomType) => sum + (pricePerRoomType ?? 0), 0);

  const cityTaxRoomPrice = selectedRate?.roomTypes
    ?.map(
      (roomType, roomTypeIndex) =>
        roomType?.rooms[roomsIndexes?.[roomTypeIndex]]?.roomPriceBreakdown?.effectiveRateAmount
    )
    ?.reduce((sum, pricePerRoomType) => sum + (pricePerRoomType ?? 0), 0);

  const reservations = selectedRate?.roomTypes?.map((roomType, roomTypeIndex) => ({
    hotelId,
    arrival,
    departure,
    adultsNumber: roomType?.adults,
    childrenNumber: roomType?.children,
    cotRequired: selectedPMSRoomTypes?.length
      ? roomType?.rooms?.find((room) => room?.pmsRoomType === selectedPMSRoomTypes?.[roomTypeIndex])
          ?.cotAvailable
      : roomType?.rooms?.[0]?.cotAvailable,
    roomRates: {
      ratePlanCode,
      pmsRoomType: selectedPMSRoomTypes?.length
        ? selectedPMSRoomTypes?.[roomTypeIndex]
        : roomType?.rooms?.[roomsIndexes?.[roomTypeIndex]]?.pmsRoomType,
      specialRequests: selectedSpecialRequests?.length
        ? selectedSpecialRequests?.[roomTypeIndex]
        : roomType?.rooms?.[roomsIndexes?.[roomTypeIndex]]?.specialRequests,
      startDate: arrival,
      endDate: departure,
      promotionCode: selectedRate.promotionCode,
      promoKind: selectedRate?.promoKind as PromoKind,
    },
    reservationPackages: [
      {
        startDate: arrival,
        endDate: departure,
        quantity: 1,
        packageCode:
          roomType?.rooms?.[roomsIndexes?.[roomTypeIndex]]?.roomPriceBreakdown?.packageCode ?? '',
        unitPrice:
          roomType?.rooms?.[roomsIndexes?.[roomTypeIndex]]?.roomPriceBreakdown?.packageAmount ?? 0,
      },
      ...(softBundles?.softBundleContent ?? []).map((bundle) => ({
        startDate: arrival,
        endDate: departure,
        quantity: Object.values(ExtrasId as unknown as string[]).includes(bundle.id ?? '')
          ? 1
          : roomType?.adults,
        packageCode: bundle.id ?? '',
        unitPrice: bundle.price ?? 0,
      })),
    ].filter((pkg) => pkg.packageCode !== ''),
  }));

  return (
    <BasketComponent
      {...{
        variant,
        channel,
        roomClassIndexFromSelectedRate,
        roomCodes: reservationRoomTypes,
        roomClass,
        isCityTaxExempt,
        isLastFewRooms,
        isHDPBasket,
        hasAccessibleRoom,
        hasTwinRoomChoice,
        shouldDisplayMobileBasket,
        bookRsvIsLoading,
        bookRsvIsError,
        bookRsvError,
        onBookReservation,
        bookingFlowId,
        hotelId,
        arrival,
        departure,
        ratePlanCode,
        numberOfUnits,
        currencyCode,
        numberOfNights,
        totalReservationAmount,
        dailyPricesPerRoom,
        totalCityTaxAmount,
        cityTaxRoomPrice,
        isLessThanLg,
        rateName,
        rateTags,
        roomTypeInformationResponse,
        brand,
        isDisabledContinueBtn,
        twinroomSelections,
        companyData,
        upgradeRoomContent,
        isAccessibleType,
        isCityTaxEnabled,
        promoActions,
        userChoice,
        softBundles,
        isCityTaxBreakdownEnabled,
        isSoftBundlesVisible,
        adultsNumber,
        metaSearchConfigs,
        isRoomOnly,
      }}
    />
  );

  // set cookie for use in A/B test for ancillaries page - scrollable tabs in mobile
  function setScrollableAncillariesTabsCookie() {
    setCookieForABTesting(
      ANCILLARIES_TABS.configName,
      ANCILLARIES_TABS.cookieName,
      ANCILLARIES_TABS.expiryInMinutes,
      channel
    );
  }

  // set cookie for use in A/B test on GDP for Accompanying Guests
  function setAccompanyingGuestCookie() {
    setCookieForABTesting(
      GDP_ACCOMPANYING_GUEST_DETAILS.configName,
      GDP_ACCOMPANYING_GUEST_DETAILS.cookieName,
      GDP_ACCOMPANYING_GUEST_DETAILS.expiryInMinutes,
      channel
    );
  }

  // set cookie for use in A/B test on GDP for Additional Info for DE hotels/any site (Digital Reg Feature)
  function setAdditionalInfoCookie() {
    setCookieForABTesting(
      GDP_DIGI_REG_ADDITIONAL_INFO.configName,
      GDP_DIGI_REG_ADDITIONAL_INFO.cookieName,
      GDP_DIGI_REG_ADDITIONAL_INFO.expiryInMinutes,
      channel
    );
  }

  function onBookReservation(pmsRoomTypes?: string[]) {
    setScrollableAncillariesTabsCookie();
    setAccompanyingGuestCookie();
    setAdditionalInfoCookie();

    const bookingChannel = { channel, subchannel: 'WEB', language: language?.toUpperCase() };

    let currentReservations: RoomReservation[] = reservations;
    if (pmsRoomTypes?.length) {
      currentReservations = reservations.map((room: RoomReservation, index: number) => ({
        ...room,
        roomRates: {
          ...room.roomRates,
          pmsRoomType: pmsRoomTypes[index],
        },
      }));
    }
    handleBooking(currentReservations, bookingChannel as BookingChannelCriteria, bookingFlowId);

    if (isPrePopulateBillingAddressEnabled) {
      // selectedRateCategory?.rateOrder is a temporary solution as waiting for BE to complete work to send Negotiated rate flag DNRQ-68228
      if (selectedRateCategory?.rateOrder === '1') {
        const selectedCompanyData = {
          address: companyData?.companyProfile?.address,
          name: companyData?.companyProfile?.name,
        };
        setCompanyProfile(selectedCompanyData);
      } else {
        localStorage.removeItem('CompanyProfile');
      }
    }
  }

  function setSelectedRatePlanCode() {
    if (selectedRate?.cellCode && selectedRate?.cellCode === OfferEnum.EMPLOYEE) {
      return OfferEnum.EMPLOYEE_RATE_CODE;
    }
    return selectedRate?.ratePlanCode;
  }
}

export const getRoomTypeLabel = (option: string): string => {
  const typeLabels: RoomTypeLabels = {
    single: StandardRoomType.SB,
    double: StandardRoomType.DB,
    accessible: StandardRoomType.DIS,
    twin: StandardRoomType.TWIN,
    family: StandardRoomType.FAM,
  };
  const roomCodes = swapKeysAndValues(ROOM_CODES as any);
  const codeKey = (roomCodes as Record<string, string | undefined>)[option] as
    | keyof RoomTypeLabels
    | undefined;

  const typeName = codeKey ? typeLabels[codeKey] : option;
  return typeName?.charAt(0)?.toUpperCase() + typeName?.slice(1);
};

// Set the cookie on the HDP Page: Can be further refactored if using other than HDP
// This is only to please the SONAR Coverage Report to cover Unit Tests for 1 scenario
export function setCookieForABTesting(
  configName: string,
  cookieName: string,
  expiry: number,
  channel: string
) {
  if (typeof window !== 'undefined') {
    const hasCookie = getCookie(cookieName);
    if (window?.piConfig?.[configName] && !hasCookie && channel?.toLowerCase() !== Area.CCUI) {
      const { mode } = window.piConfig[configName];
      if (mode?.length) {
        setCookie(cookieName, mode, expiry);
      }
    }
  }
}

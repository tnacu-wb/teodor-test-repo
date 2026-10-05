import { useQueryClient } from '@tanstack/react-query';
import {
  AmendHotelAvailabilityData,
  AmendRoomDetailsType,
  AmendRoomModalLabels,
  AmendRoomType,
  AnalyticsRoomDetails,
  Area,
  BOOKING_CHANNEL,
  GET_HOTEL_AVAILABILITY_QUERY,
  GuestDetails,
  HOTEL_AVAILABILITY_BB_QUERY,
  LeadGuestDetailsType,
  ReservationGuestList,
  ReservationLeadGuestType,
  ROOM_TYPE,
  SearchRoomOccupancyLimitationsType,
  Suggestion,
  HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY,
  FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY,
  Customer,
  Channel,
} from '@whitbread-eos/api';
import { DropdownOption, FormProps } from '@whitbread-eos/atoms';
import {
  formatCurrency,
  formatPriceWithDecimal,
  generateSuggestionList,
  getAuthCookie,
  getListOfEmployees,
  getLoggedInUserInfo,
  getRoomTypeOptions,
  graphQLRequest,
  swapKeysAndValues,
  upperOnlyFirst,
  useGetDiscountRateComapnyId,
  useFeatureToggle,
  PromotionsInformation,
} from '@whitbread-eos/utils';
import { useEffect, useMemo, useRef, useState } from 'react';

import { getGuestsPlaceholderString } from '../utilities';
import RoomModalComponent from './RoomModal.component';
import {
  ADULTS_PARAM,
  CHILDREN_PARAM,
  INITIAL_ROOM_DETAILS,
  INITIAL_ROOM_OCCUPANCY,
  ROOM_TYPE_KEY,
  HOTEL_AVAILABILITY_QUERY_KEY,
  HOTEL_AVAILABILITY_BB_QUERY_KEY,
  INITIAL_LEAD_GUEST_DETAILS,
  HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY_KEY,
} from './constants';

export interface Props {
  isOpen: boolean;
  onClose: () => void;
  onSaveNewRoom?: (room: AmendRoomType) => void;
  onUpdateRoom?: (
    selectedRoom: AmendRoomType,
    reservationId: string,
    roomNumber: number,
    analyticsRoomDetails?: AnalyticsRoomDetails
  ) => void;
  title: string;
  roomRules: SearchRoomOccupancyLimitationsType;
  hotelAvailabilityParams: AmendHotelAvailabilityData;
  labels: AmendRoomModalLabels;
  baseDataTestId: string;
  isEdit?: boolean;
  roomDetailsEdit?: AmendRoomDetailsType;
  price?: string;
  reservationGuestList?: ReservationGuestList;
  reservationId?: string;
  roomNumber?: number;
  variant: Area;
  brand: string;
  channel: Channel;
  hotelCountry: string;
  language: string;
  userDetails?: Customer;
  isPromoCodeLandingPageEnabled: boolean;
}

export default function RoomModalContainer({
  isOpen,
  onClose,
  title,
  roomRules,
  hotelAvailabilityParams,
  labels,
  baseDataTestId,
  onSaveNewRoom,
  isEdit = false,
  roomDetailsEdit,
  price,
  reservationGuestList,
  onUpdateRoom,
  reservationId,
  roomNumber,
  variant,
  brand,
  channel,
  hotelCountry,
  language,
  userDetails,
  isPromoCodeLandingPageEnabled,
}: Readonly<Props>) {
  const {
    [FT_PI_BB_CCUI_PROMOTIONS_IN_HOTELAVAILABILITY]: isPromotionsInHotelAvailabilityEnabled,
  } = useFeatureToggle();

  const queryClient = useQueryClient();
  const idTokenCookie = getAuthCookie();
  const userData = getLoggedInUserInfo(idTokenCookie);
  const companyId = userData.operaCompanyId ?? hotelAvailabilityParams.companyId ?? '';
  const currentBookingReservationChannel = hotelAvailabilityParams.channel;

  const discountRateCompanyId = useGetDiscountRateComapnyId(hotelAvailabilityParams);

  const {
    roomDropdownLabels,
    roomAvailabilityLabels,
    leadGuestLabels,
    leadGuestValidationLabels,
    notificationLabels,
    roomDropdownRoomCodes,
  } = labels;
  const { single, double, accessible, family, twin } = roomDropdownLabels;
  const ROOM_LABELS = {
    single,
    double,
    accessible,
    twin,
    family,
  };
  const roomCodes = swapKeysAndValues(roomDropdownRoomCodes as any);
  const roomModalLabels = {
    roomAvailabilityLabels,
    leadGuestLabels,
    leadGuestValidationLabels,
    notificationLabels,
  };

  const initialEditGuestDetails = useMemo(() => {
    return {
      title: upperOnlyFirst(reservationGuestList?.nameTitle ?? ''),
      firstName: reservationGuestList?.givenName ?? '',
      lastName: reservationGuestList?.surName ?? '',
      emailAddress: reservationGuestList?.email ?? '',
      addressLine1: reservationGuestList?.address?.addressLine1 ?? '',
      addressLine2: reservationGuestList?.address?.addressLine2 ?? '',
      addressLine3: reservationGuestList?.address?.addressLine3 ?? '',
      postalCode: reservationGuestList?.address?.postalCode ?? '',
      city: reservationGuestList?.address?.cityName ?? '',
      cityName: reservationGuestList?.address?.cityName ?? '',
      countryCode: reservationGuestList?.address?.countryCode ?? '',
    } as ReservationLeadGuestType;
  }, [
    reservationGuestList?.email,
    reservationGuestList?.givenName,
    reservationGuestList?.nameTitle,
    reservationGuestList?.surName,
    reservationGuestList?.address?.addressLine1,
    reservationGuestList?.address?.addressLine2,
    reservationGuestList?.address?.addressLine3,
    reservationGuestList?.address?.postalCode,
    reservationGuestList?.address?.cityName,
    reservationGuestList?.address?.countryCode,
  ]);

  const roomDetails = useRef(INITIAL_ROOM_DETAILS);
  const isAdultsDecreased = useRef(false);
  const isAvailabilityButtonEnabled = useRef(true);
  const leadGuestDetails = useRef<FormProps['defaultValues']>(INITIAL_LEAD_GUEST_DETAILS);
  const initialRoomType = useRef(roomDetails.current.roomType);
  const initialGuestsNumber = useRef(roomDetailsEdit?.adults ?? roomDetails.current.adults);
  const bbEmployeeList = useRef<Suggestion[]>([]);

  const [guestDetails, setGuestDetails] = useState(initialEditGuestDetails);
  const [isLocationRequired, setIsLocationRequired] = useState(false);
  const [isHotelAvailable, setIsHotelAvailable] = useState({
    displayNotification: false,
    available: false,
  });
  const [roomOccupancyDetails, setRoomOccupancyDetails] = useState(INITIAL_ROOM_OCCUPANCY);
  const [specialRequests, setSpecialRequests] = useState([]);
  const [isBbGuestEdited, setIsBbGuestEdited] = useState<boolean>(false);
  const [promoData, setPromoData] = useState<PromotionsInformation | null>(null);

  const reservationRoom = {
    adults: roomDetails.current.adults,
    children: roomDetails.current.children,
  };
  const roomTypeOptions = getRoomTypeOptions(
    reservationRoom,
    roomRules.roomOccupancyLimitations.roomOccupancies,
    ROOM_LABELS,
    roomCodes
  ).flat();

  useEffect(() => {
    if (variant === Area.BB && isEdit) {
      getListOfEmployees({
        awaitingApproval: false,
        bookingChannel: 'CBT',
        page: 1,
        searchCriteria: '',
        size: 10,
        queryClient: queryClient,
      }).then((data: GuestDetails[]) => {
        bbEmployeeList.current = generateSuggestionList(data, '', []);
      });
    }
  }, [isEdit, variant, queryClient]);

  useEffect(() => {
    if (isEdit) {
      roomDetails.current = roomDetailsEdit ?? INITIAL_ROOM_DETAILS;
      setRoomOccupancyDetails({
        displayRoomOccupancy: true,
        price: price as string,
        guests: getGuestsPlaceholderString(
          roomDetailsEdit?.adults as number,
          roomDetailsEdit?.children as number,
          labels.roomAvailabilityLabels
        ),
      });

      isAvailabilityButtonEnabled.current = false;
      leadGuestDetails.current = initialEditGuestDetails;
    }
  }, [isEdit]);

  const handleChangeDropdown = (chosenOption: DropdownOption | undefined, param: string) => {
    let extraChanges = {};
    if (param === CHILDREN_PARAM) {
      if (Number(chosenOption?.id) > 0) {
        extraChanges = {
          roomType: ROOM_LABELS.family,
          roomTypeCode: roomDropdownRoomCodes.family,
        };
      } else {
        extraChanges = {
          roomType: ROOM_LABELS.double,
          roomTypeCode: roomDropdownRoomCodes.double,
        };
      }
    }

    if (param === ADULTS_PARAM) {
      if (roomDetailsEdit && Number(chosenOption?.id) < roomDetailsEdit?.adults) {
        isAdultsDecreased.current = true;
      }
      if (roomDetailsEdit && Number(chosenOption?.id) === 2) {
        isAdultsDecreased.current = false;
      }
      if (Number(chosenOption?.id) > 0) {
        if (roomDetails.current.children === 0) {
          if (roomDetails.current.roomType === ROOM_LABELS.accessible) {
            extraChanges = {
              roomType: ROOM_LABELS.accessible,
              roomTypeCode: roomDropdownRoomCodes.accessible,
            };
          } else {
            extraChanges = {
              roomType: ROOM_LABELS.double,
              roomTypeCode: roomDropdownRoomCodes.double,
            };
          }
        } else if (roomDetails.current.children > 0) {
          extraChanges = {
            roomType: ROOM_LABELS.family,
            roomTypeCode: roomDropdownRoomCodes.family,
          };
        }
      }
    }

    if (param === ROOM_TYPE_KEY) {
      extraChanges = {
        roomTypeCode: chosenOption?.code,
      };
    }

    const updatedRoomDetails = {
      ...roomDetails.current,
      [param]: chosenOption?.id,
      ...extraChanges,
    };

    roomDetails.current = {
      ...updatedRoomDetails,
    };
    const shouldEnableCheckAvailabilityButton =
      updatedRoomDetails.adults !== roomDetailsEdit?.adults ||
      updatedRoomDetails.children !== roomDetailsEdit?.children ||
      updatedRoomDetails.roomTypeCode !== roomDetailsEdit?.roomTypeCode ||
      updatedRoomDetails.operaRoomType !== roomDetailsEdit?.operaRoomType;
    isAvailabilityButtonEnabled.current = shouldEnableCheckAvailabilityButton;
    setIsHotelAvailable({
      displayNotification: false,
      available: false,
    });
    setRoomOccupancyDetails(INITIAL_ROOM_OCCUPANCY);
  };

  const handleAvailabilityCheck = async () => {
    isAvailabilityButtonEnabled.current = false;

    const params = {
      ...hotelAvailabilityParams,
      rooms: [
        {
          adultsNumber: roomDetails.current.adults,
          childrenNumber: roomDetails.current.children,
          cotRequired: false,
          roomType: roomDetails.current.roomTypeCode,
        },
      ],
      brand: brand,
      ...(isPromotionsInHotelAvailabilityEnabled && { isPromoBox: false }),
    };

    const bbParams = {
      ...params,
      companyId: companyId,
    };

    let HOTEL_AVAILABILITY_QUERY_KEY_PI = HOTEL_AVAILABILITY_QUERY_KEY;
    let GET_HOTEL_AVAILABILITY_QUERY_PI = GET_HOTEL_AVAILABILITY_QUERY;

    if (discountRateCompanyId) {
      params.companyId = discountRateCompanyId.toString();

      HOTEL_AVAILABILITY_QUERY_KEY_PI = HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY_KEY;
      GET_HOTEL_AVAILABILITY_QUERY_PI = HOTEL_AVAILABILITY_DISCOUNT_RATE_QUERY;
    }

    const getHotelAvailability =
      variant === Area.BB || currentBookingReservationChannel === BOOKING_CHANNEL.BB
        ? queryClient.fetchQuery({
            queryKey: [HOTEL_AVAILABILITY_BB_QUERY_KEY, bbParams],
            queryFn: () => graphQLRequest(HOTEL_AVAILABILITY_BB_QUERY, bbParams, idTokenCookie),
            ...{ staleTime: 0 },
          })
        : queryClient.fetchQuery({
            queryKey: [HOTEL_AVAILABILITY_QUERY_KEY_PI, params],
            queryFn: () => graphQLRequest(GET_HOTEL_AVAILABILITY_QUERY_PI, params),
            ...{ staleTime: 0 },
          });

    await getHotelAvailability.then((response) => {
      if (isPromotionsInHotelAvailabilityEnabled) {
        setPromoData(
          (response?.hotelAvailability?.promotionsInformation as PromotionsInformation) ?? null
        );
      }
      const isHotelAvailable =
        response?.hotelAvailability.available && response?.hotelAvailability.roomRates.length > 0;
      setIsHotelAvailable({
        displayNotification: true,
        available: isHotelAvailable,
      });

      if (response?.hotelAvailability?.roomRates?.length) {
        const roomRates = response?.hotelAvailability.roomRates.filter(
          (item: any) =>
            item.ratePlanCode === hotelAvailabilityParams.rateCode ||
            (variant === Area.PI &&
              hotelAvailabilityParams.ratePlanCodes &&
              item?.cellCode === hotelAvailabilityParams.ratePlanCodes[0])
        );

        const requiresPriceChange =
          roomDetailsEdit &&
          [
            ROOM_TYPE.STANDARD_BIGGER,
            ROOM_TYPE.PREMIER_PLUS,
            ROOM_TYPE.PREMIER_PLUS_FAMILY,
            ROOM_TYPE.TWIN,
          ].includes(roomDetailsEdit?.operaRoomType);

        if (roomRates.length) {
          const currencyCode = roomRates[0].roomTypes[0].rooms[0].roomPriceBreakdown.currencyCode;
          const {
            roomPriceBreakdown: { totalNetAmount },
            specialRequests,
          } =
            roomRates[0].roomTypes[0].rooms.find(
              (room: any) =>
                room.pmsRoomType.toUpperCase() === roomDetails.current.roomType.toUpperCase()
            ) ?? roomRates[0].roomTypes[0].rooms[0];

          const roomPrice =
            isEdit && price && !requiresPriceChange
              ? price
              : formatPriceWithDecimal(
                  hotelAvailabilityParams.language,
                  formatCurrency(currencyCode),
                  totalNetAmount,
                  true
                );
          setRoomOccupancyDetails({
            displayRoomOccupancy: true,
            price: roomPrice,
            guests: getGuestsPlaceholderString(
              roomDetails.current.adults,
              roomDetails.current.children,
              labels.roomAvailabilityLabels
            ),
          });

          setSpecialRequests(specialRequests);
        } else {
          setIsHotelAvailable({
            displayNotification: true,
            available: false,
          });
        }
      }
    });
  };
  const onAddNewRoom = (guestDetails: FormProps['defaultValues'] | object) => {
    const newRoom: AmendRoomType = {
      adultsNumber: roomDetails.current.adults,
      childrenNumber: roomDetails.current.children,
      roomType: roomDetails.current.roomTypeCode,
      ...(guestDetails as LeadGuestDetailsType),
      cotRequired: false,
      specialRequests: specialRequests,
    };
    onSaveNewRoom?.(newRoom);
  };

  const onEditRoom = (guestDetails: FormProps['defaultValues']) => {
    let room = {
      ...(guestDetails as LeadGuestDetailsType),
      cotRequired: false,
      adultsNumber: roomDetails.current.adults,
      childrenNumber: roomDetails.current.children,
      roomType: roomDetails.current.roomTypeCode,
    };

    if (!isHotelAvailable.available && roomDetailsEdit) {
      room = {
        ...room,
        adultsNumber: roomDetailsEdit.adults,
        childrenNumber: roomDetailsEdit.children,
        roomType: roomDetailsEdit.operaRoomType,
      };
    }

    let analyticsRoomDetails = {};

    if (initialRoomType.current !== roomDetails.current.roomType) {
      analyticsRoomDetails = {
        ...analyticsRoomDetails,
        isChanged: true,
        roomType: roomDetails.current.roomType,
      };
    }

    if (initialGuestsNumber.current !== roomDetails.current.adults) {
      analyticsRoomDetails = {
        ...analyticsRoomDetails,
        isChanged: true,
        changeAdults: `1 guest has been ${roomDetails.current.adults === 2 ? 'added' : 'removed'}`,
      };
    }

    reservationId &&
      roomNumber &&
      onUpdateRoom?.(room, reservationId, roomNumber, analyticsRoomDetails as AnalyticsRoomDetails);
  };

  const onCloseModal = () => {
    queryClient.cancelQueries({
      queryKey: [
        variant === Area.BB ? HOTEL_AVAILABILITY_BB_QUERY_KEY : HOTEL_AVAILABILITY_QUERY_KEY,
      ],
    });
    onClose();
    const isAddARoomSectionEnabled = !isEdit;
    isAvailabilityButtonEnabled.current = isAddARoomSectionEnabled;
    setIsHotelAvailable({
      displayNotification: false,
      available: false,
    });
    setIsBbGuestEdited(false);
    setRoomOccupancyDetails(INITIAL_ROOM_OCCUPANCY);
    setGuestDetails(initialEditGuestDetails);
    leadGuestDetails.current = { ...initialEditGuestDetails };
    roomDetails.current = roomDetailsEdit ?? INITIAL_ROOM_DETAILS;
    setPromoData(null);

    isAdultsDecreased.current = false;
  };

  const isUpdateBtnDisabled = () => {
    const isGDSectionUpdated =
      leadGuestDetails.current.title !== guestDetails.title ||
      leadGuestDetails.current.firstName !== guestDetails.firstName ||
      leadGuestDetails.current.lastName !== guestDetails.lastName ||
      leadGuestDetails.current.emailAddress !== guestDetails.emailAddress ||
      leadGuestDetails.current.addressLine1 !== guestDetails.addressLine1 ||
      leadGuestDetails.current.addressLine2 !== guestDetails.addressLine2 ||
      leadGuestDetails.current.addressLine3 !== guestDetails.addressLine3;

    if (isEdit) {
      return !(isGDSectionUpdated || isHotelAvailable.available);
    } else {
      return !(isGDSectionUpdated && isHotelAvailable.available);
    }
  };

  return (
    <RoomModalComponent
      isOpen={isOpen}
      onCloseModal={onCloseModal}
      title={title}
      roomRules={roomRules}
      onAvailabilityCheck={handleAvailabilityCheck}
      onChange={handleChangeDropdown}
      onAddNewRoom={onAddNewRoom}
      getFormState={() => {}}
      roomTypeOptions={roomTypeOptions}
      isAvailabilityButtonEnabled={isAvailabilityButtonEnabled.current}
      isHotelAvailable={isHotelAvailable}
      roomDetails={roomDetails.current}
      labels={roomModalLabels}
      baseDataTestId={baseDataTestId}
      roomOccupancyDetails={roomOccupancyDetails}
      leadGuestDetails={leadGuestDetails.current}
      isEdit={isEdit}
      roomNumber={roomNumber}
      onUpdateRoom={onEditRoom}
      setGuestDetails={setGuestDetails}
      isLocationRequired={isLocationRequired}
      setIsLocationRequired={setIsLocationRequired}
      isUpdateBtnDisabled={isUpdateBtnDisabled()}
      variant={variant}
      isAdultsDecreased={isAdultsDecreased.current}
      bbEmployeeList={bbEmployeeList.current}
      hotelCountry={hotelCountry}
      isBbGuestEdited={isBbGuestEdited}
      setIsBbGuestEdited={setIsBbGuestEdited}
      language={language}
      userDetails={userDetails}
      brand={brand}
      channel={channel}
      hotelAvailabilityParams={hotelAvailabilityParams}
      isPromoCodeLandingPageEnabled={isPromoCodeLandingPageEnabled}
      isPromotionsInHotelAvailabilityEnabled={isPromotionsInHotelAvailabilityEnabled}
      promoData={promoData}
      setPromoData={setPromoData}
    />
  );
}

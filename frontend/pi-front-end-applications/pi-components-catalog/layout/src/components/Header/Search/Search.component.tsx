'use client';

import {
  DEFAULT_NUMBER,
  MAX_NUMBER_OF_MONTHS,
  FormInnB,
  SearchRoomType,
  GlobalInnB,
  ROOM_CODES,
  SearchRequestParamsType,
  BOOKING_CHANNEL,
  Area,
  DATE_TYPE,
  BOOKING_SUBCHANNEL,
  URLParams,
  BUSINESS_BOOKER_USER_ROLES,
  Customer,
  SearchRules,
} from '@whitbread-eos/api';
import { DatePickerWithRange as DatePicker } from '@whitbread-eos/atoms/ui';
import { getAuthCookie } from '@whitbread-eos/utils';
import {
  getSearchRedirectLink,
  getHotelInformationIB,
  mapSearchParamsForURL,
  roomOccupancyParamsForURL,
  getHotelAvailabilitiesIB,
  isHotelOpeningSoon,
  staticHotelInformationIB,
  getMultiSearchParamsIB,
  updateSearchParamsIfError,
  isMoreThan364DaysInFuture,
  validateRoomOccupancyConditions,
  getDaysInMonth,
} from '@whitbread-eos/utils/server';
import { isBefore, startOfDay, add, format } from 'date-fns';
import { nanoid } from 'nanoid';
import { useParams, useSearchParams } from 'next/navigation';
import { useState, useEffect } from 'react';
import { DateRange } from 'react-day-picker';

import { Location } from './Location';
import { RoomOccupancy } from './RoomOccupancy';
import SearchButton from './SearchButton';
import { SearchOverlay } from './SearchOverlay';

interface Props {
  mobile?: boolean;
  formLabels: FormInnB | Record<string, never>;
  locale: string;
  language: string;
  addRoomIcon: string;
  icons: Record<string, string>;
  userRole: string;
  globalLabels: GlobalInnB | Record<string, never>;
  hideEditSearch?: () => void;
  onOpenChange?: (open: boolean) => void;
  userDetails: Customer;
  searchRules: SearchRules;
  onSearchButtonClick: (url: string) => void;
}

export const getDefaultRoom = (userDetails: Customer) => {
  const roomRequirements = userDetails.bookingPreference?.roomRequirements;
  let defaultRoom = {
    adults: roomRequirements?.adults || 1,
    children: roomRequirements?.children || 0,
    shouldIncludeCot: !!roomRequirements?.cotRequired,
    roomType: roomRequirements?.type || ROOM_CODES.double,
  };

  const isComboInvalid = validateRoomOccupancyConditions(
    defaultRoom.adults,
    defaultRoom.children,
    defaultRoom.roomType,
    defaultRoom.shouldIncludeCot
  );

  if (isComboInvalid) {
    defaultRoom = {
      adults: 1,
      children: 0,
      shouldIncludeCot: false,
      roomType: ROOM_CODES.double,
    };
  }

  return defaultRoom;
};

const Search = ({
  mobile = false,
  formLabels,
  locale,
  language,
  addRoomIcon,
  icons,
  userRole,
  globalLabels,
  userDetails,
  hideEditSearch = () => {
    return;
  },
  onOpenChange = () => {
    return;
  },
  searchRules,
  onSearchButtonClick,
}: Readonly<Props>) => {
  const idTokenCookie = getAuthCookie();
  const defaultRoom = getDefaultRoom(userDetails);
  const initialRoom = {
    [nanoid()]: defaultRoom,
  };
  //prefill functionality
  const { slug } = useParams();

  const searchParams = useSearchParams();

  const hotelFullSlug = slug ? `/hotels/${Array.isArray(slug) ? slug.join('/') : slug}` : undefined;
  const searchParamsObj: Record<string, string> = {};
  searchParams.forEach((value, key) => {
    searchParamsObj[key] = value;
  });

  const locationFromUrl = searchParamsObj['searchModel.searchTerm'] ?? '';

  //end of prefill functionality

  const [location, setLocation] = useState('');
  const [selectedLocation, setSelectedLocation] = useState({});
  const [rooms, setRooms] = useState<Record<string, SearchRoomType>>(initialRoom);
  const [roomsBeforeEdit, setRoomsBeforeEdit] =
    useState<Record<string, SearchRoomType>>(initialRoom);
  const [showLocationError, setShowLocationError] = useState(false);
  const [showCalendarError, setShowCalendarError] = useState(false);
  const [showRoomOccupancyError, setShowRoomOccupancyError] = useState(false);
  const [locationOpen, setLocationOpen] = useState(false);
  const [datePickerOpen, setDatePickerOpen] = useState(false);
  const [roomOccupancyOpen, setRoomOccupancyOpen] = useState(false);

  const isOverlayVisible = locationOpen || datePickerOpen || roomOccupancyOpen;

  useEffect(() => {
    onOpenChange(isOverlayVisible);
  }, [isOverlayVisible]);

  useEffect(() => {
    if (isOverlayVisible && !mobile) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = '';
    }

    return () => {
      document.body.style.overflow = '';
    };
  }, [isOverlayVisible, mobile]);

  useEffect(() => {
    if (showCalendarError || showRoomOccupancyError || showLocationError) {
      hideEditSearch?.();
    }
  }, [showCalendarError, showRoomOccupancyError, showLocationError]);

  const [date, setDate] = useState<DateRange | undefined>({
    from: undefined,
    to: undefined,
  });
  const [dateFromUrl, setDateFromUrl] = useState<DateRange | undefined>({
    from: undefined,
    to: undefined,
  });
  const handleDateChange = (newDate: DateRange | undefined) => {
    setDate(newDate);
  };

  const handleUpdateRoom = (id: string, newData: SearchRoomType) => {
    setRooms((initialRooms) => ({
      ...initialRooms,
      [id]: { ...newData },
    }));
    setShowRoomOccupancyError(false);
  };

  const handleAddRoom = () => {
    if (Object.keys(rooms).length < searchRules.globalConfig.maxRoomsLim.maxRooms) {
      setRooms((initialRooms) => ({
        ...initialRooms,
        [nanoid()]: defaultRoom,
      }));
      setShowRoomOccupancyError(false);
    }
  };

  const handleRemoveRoom = (roomId: string) => {
    setRooms((initialRooms) => {
      return Object.fromEntries(Object.entries(initialRooms).filter(([id]) => id !== roomId));
    });
    setShowRoomOccupancyError(false);
  };
  const parseDateString = (dateStr: string) => {
    return new Date(dateStr);
  };

  useEffect(() => {
    const getData = async () => {
      if (slug && hotelFullSlug) {
        const hotelInformationSlug = await staticHotelInformationIB(hotelFullSlug, language);
        setLocation(hotelInformationSlug?.name ?? '');
      }
    };
    if ((slug && hotelFullSlug) || locationFromUrl) {
      const { arrival, departure, rooms: roomsFromUrl } = getMultiSearchParamsIB(searchParamsObj);
      setRooms(roomsFromUrl);
      setLocation(locationFromUrl ?? '');
      setDateFromUrl({
        from: parseDateString(arrival),
        to: parseDateString(departure),
      });
    }

    getData();
  }, [slug, hotelFullSlug, language, locationFromUrl]);

  const numberOfNightsURLError =
    searchParamsObj.NIGHTS !== undefined &&
    (isNaN(Number(searchParamsObj.NIGHTS)) ||
      Number(searchParamsObj.NIGHTS) < DEFAULT_NUMBER ||
      Number(searchParamsObj.NIGHTS) > searchRules.maxNightsLimitation.maxNights);

  const day = searchParamsObj.ARRdd;
  const month = searchParamsObj.ARRmm;
  const year = searchParamsObj.ARRyyyy;

  const startDate = new Date(Number(year), Number(month) && Number(month) - 1, Number(day));
  const today = startOfDay(new Date());

  const dateIsValid = day !== undefined || month !== undefined || year !== undefined;

  const dateIsInPastOrFutureError =
    dateIsValid && (isBefore(startDate, today) || isMoreThan364DaysInFuture(startDate, today));

  const dateIsNotNumberError =
    dateIsValid && (isNaN(Number(day)) || isNaN(Number(month)) || isNaN(Number(year)));

  const daysInMonth = getDaysInMonth(Number(month), Number(year));

  const dateFormattedHasError =
    dateIsValid &&
    (Number(month) < DEFAULT_NUMBER ||
      Number(month) > MAX_NUMBER_OF_MONTHS ||
      Number(day) < DEFAULT_NUMBER ||
      Number(day) > daysInMonth);

  const numberOfRooms = Number(searchParamsObj.ROOMS);
  let roomOccupancyError = false;
  const selfBookerCondition =
    userRole === BUSINESS_BOOKER_USER_ROLES.SELF && numberOfRooms > DEFAULT_NUMBER;

  if (
    searchParamsObj.ROOMS &&
    (isNaN(numberOfRooms) ||
      numberOfRooms < DEFAULT_NUMBER ||
      numberOfRooms > searchRules.globalConfig.maxRoomsLim.maxRooms ||
      selfBookerCondition)
  ) {
    roomOccupancyError = true;
  }
  if (!selfBookerCondition) {
    const maxRooms = searchRules.globalConfig.maxRoomsLim.maxRooms;
    for (let roomIndex = 1; roomIndex <= numberOfRooms && roomIndex <= maxRooms; roomIndex++) {
      const adultKey = `${URLParams.adult}${roomIndex}`;
      const childKey = `${URLParams.child}${roomIndex}`;
      const cotKey = `${URLParams.cot}${roomIndex}`;
      const roomTypeKey = `${URLParams.roomType}${roomIndex}`;

      const adult = searchParams.get(adultKey);
      const child = searchParams.get(childKey);
      const cot = searchParams.get(cotKey);
      const roomType = searchParams.get(roomTypeKey);

      roomOccupancyError = validateRoomOccupancyConditions(adult, child, roomType, cot);
    }
  }

  useEffect(() => {
    if (
      numberOfNightsURLError ||
      dateIsInPastOrFutureError ||
      dateIsNotNumberError ||
      dateFormattedHasError
    ) {
      setShowCalendarError(true);
      setDate({ from: undefined, to: undefined });
      setDateFromUrl({ from: undefined, to: undefined });
    }
    if (roomOccupancyError) {
      setShowRoomOccupancyError(true);
      setRooms({
        [nanoid()]: defaultRoom,
      });
    }

    const result = updateSearchParamsIfError(
      searchParams,
      userRole,
      searchRules.globalConfig.maxRoomsLim.maxRooms,
      searchRules.maxNightsLimitation.maxNights
    );
    if (result.shouldUpdate && typeof window !== 'undefined') {
      window.location.replace(result.url);
    }
  }, [
    searchParams,
    numberOfNightsURLError,
    dateIsInPastOrFutureError,
    dateIsNotNumberError,
    dateFormattedHasError,
    roomOccupancyError,
    userRole,
  ]);

  async function handleButtonClick(queryParams: SearchRequestParamsType | undefined) {
    if (!queryParams?.searchTerm) {
      setShowLocationError(true);
      return;
    }

    const country = language === 'en' ? 'gb' : 'de';

    const hotelId = queryParams.code;

    const paramsMappedForURL = mapSearchParamsForURL(queryParams);

    const roomOccupancyParams = roomOccupancyParamsForURL(queryParams);

    const startDate = new Date(
      Number(queryParams.ARRyyyy),
      Number(queryParams.ARRmm && queryParams.ARRmm - 1),
      Number(queryParams.ARRdd)
    );
    const endDate = add(startDate, { days: Number(queryParams.nights) });

    let hotelHasAvailability, hotelIsOpeningSoon, hotelSlugInformation;

    if (!hotelId) {
      hotelHasAvailability = true;
      hotelIsOpeningSoon = false;
    } else {
      const hotelAvailabilitiesParams = {
        hotelId: hotelId,
        arrival: format(startDate, DATE_TYPE.YEAR_MONTH_DAY),
        departure: format(endDate, DATE_TYPE.YEAR_MONTH_DAY),
        brand: Area.PI,
        country: country,
        rooms: queryParams?.rooms?.map((room) => ({
          adultsNumber: room.adults,
          childrenNumber: room.children,
          cotRequired: room.shouldIncludeCot,
          roomType: room.roomType,
        })),
        language: language,
        bookingChannel: {
          channel: BOOKING_CHANNEL.BB,
          language: language?.toUpperCase(),
          subchannel: BOOKING_SUBCHANNEL.WEB,
        },
        channel: BOOKING_CHANNEL.BB,
      };

      const hotelAvailabilities = await getHotelAvailabilitiesIB(
        hotelAvailabilitiesParams.hotelId,
        hotelAvailabilitiesParams.arrival,
        hotelAvailabilitiesParams.departure,
        hotelAvailabilitiesParams.brand,
        hotelAvailabilitiesParams.country,
        hotelAvailabilitiesParams.rooms,
        hotelAvailabilitiesParams.language,
        hotelAvailabilitiesParams.bookingChannel,
        hotelAvailabilitiesParams.channel,
        idTokenCookie
      );
      const hotelInformation = await getHotelInformationIB(hotelId, language);

      hotelSlugInformation = hotelInformation?.links?.detailsPage;
      const hotelOpeningDate = hotelInformation?.hotelOpeningDate || '';
      const selectedDate = new Date(
        `${queryParams.ARRmm}/
      ${queryParams.ARRdd}/
      ${queryParams.ARRyyyy}`
      );
      hotelHasAvailability =
        hotelAvailabilities?.hotelAvailability?.roomRates?.length > 0 &&
        hotelAvailabilities?.hotelAvailability?.available;
      hotelIsOpeningSoon = isHotelOpeningSoon(hotelOpeningDate, selectedDate);
    }

    const URLToRedirect = `ARRdd=${paramsMappedForURL.ARRdd}&ARRmm=${paramsMappedForURL.ARRmm}&ARRyyyy=${paramsMappedForURL.ARRyyyy}&NIGHTS=${paramsMappedForURL.nights}&ROOMS=${paramsMappedForURL.roomsNumber}&${roomOccupancyParams}`;

    const link = await getSearchRedirectLink(
      hotelId,
      hotelHasAvailability,
      hotelIsOpeningSoon,
      hotelSlugInformation,
      language,
      country,
      URLToRedirect,
      paramsMappedForURL,
      queryParams
    );

    onSearchButtonClick(link);
  }

  const handleRoomOccupancyOpen = (open: boolean) => {
    setRoomOccupancyOpen(open);
    if (open) {
      setRoomsBeforeEdit(rooms);
    }
  };

  const handleDiscardRoomsChanges = () => {
    setRooms(roomsBeforeEdit);
  };

  const handleOverlayClose = () => {
    setLocationOpen(false);
    setDatePickerOpen(false);
    setRoomOccupancyOpen(false);
  };

  return (
    <>
      <SearchOverlay isVisible={isOverlayVisible && !mobile} onClose={handleOverlayClose} />
      <div
        data-testid={mobile ? 'IB-Search-Container-Mobile' : 'IB-Search-Container-Desktop'}
        className={mobile ? searchMobileStyle : searchStyle}
      >
        <div className={searchContentStyle}>
          <Location
            location={location}
            setLocation={setLocation}
            labels={formLabels}
            selectedLocation={selectedLocation}
            setSelectedLocation={setSelectedLocation}
            icons={icons}
            mobile={mobile}
            globalLabels={globalLabels}
            hideEditSearch={hideEditSearch}
            onOpenChange={(open: boolean) => setLocationOpen(open)}
            isOpen={locationOpen}
            showError={showLocationError}
            setShowError={(value: boolean) => setShowLocationError(value)}
          />
          <>
            <div
              className={secondRowStyle}
              data-testid={
                mobile
                  ? 'IB-Search-Container-Mobile-Calendar-and-RoomOccupancy-Container'
                  : 'IB-Search-Container-Desktop-Calendar-and-RoomOccupancy-Container'
              }
            >
              <div
                className={datePickerContainerStyle}
                data-testid={
                  mobile
                    ? 'IB-Search-Container-Mobile-Calendar-Date-Picker'
                    : 'IB-Search-Container-Desktop-Calendar-Date-Picker'
                }
              >
                <DatePicker
                  className={datePickerStyle}
                  locale={locale}
                  formLabels={formLabels}
                  icons={icons}
                  mobile={mobile}
                  onDateChange={handleDateChange}
                  dateFromUrl={dateFromUrl}
                  showError={showCalendarError}
                  setShowError={(value: boolean) => setShowCalendarError(value)}
                  onOpenChange={(open: boolean) => setDatePickerOpen(open)}
                  tabindex={0}
                />
              </div>
              <div
                className={roomOccupancyContainerStyle}
                data-testid={
                  mobile
                    ? 'IB-Search-Container-Mobile-Room-Occupancy'
                    : 'IB-Search-Container-Desktop-Room-Occupancy'
                }
              >
                <RoomOccupancy
                  formLabels={formLabels}
                  rooms={rooms}
                  updateRoom={handleUpdateRoom}
                  addRoom={handleAddRoom}
                  removeRoom={handleRemoveRoom}
                  addRoomIcon={addRoomIcon}
                  icons={icons}
                  userRole={userRole}
                  showError={showRoomOccupancyError}
                  onOpenChange={handleRoomOccupancyOpen}
                  maxRooms={searchRules?.globalConfig?.maxRoomsLim?.maxRooms}
                  onDiscardChanges={handleDiscardRoomsChanges}
                  isMobile={mobile}
                />
              </div>
            </div>
            <div
              className={searchButtonContainerStyles}
              data-testid={
                mobile
                  ? 'IB-Search-Container-Mobile-SearchButton'
                  : 'IB-Search-Container-Desktop-SearchButton'
              }
            >
              <SearchButton
                rooms={rooms}
                location={selectedLocation}
                selectedDate={date}
                handleButtonClick={handleButtonClick}
                mobile={mobile}
                formLabels={formLabels}
                dateFromUrl={dateFromUrl}
              />
            </div>
          </>
        </div>
      </div>
    </>
  );
};

export default Search;

const searchStyle = 'mobile:hidden ml-9 mr-9 w-full relative z-40';
const searchMobileStyle = 'hidden mobile:flex mobile:mt-6 mobile:w-full mobile:flex-col';
const searchContentStyle =
  'flex w-full mobile:flex-col [@media(min-width:120rem)]:max-w-[90.625rem]';
const datePickerStyle = 'w-full min-w-64 mobile:min-w-20 tablet:min-w-56';
const datePickerContainerStyle = 'grow mobile:flex-1 mobile:min-w-[7.25rem]';
const roomOccupancyContainerStyle = 'border-lightGrey2 grow mobile:flex-1 mobile:min-w-[7.25rem]';
const secondRowStyle = 'grow-[6] flex mobile:mt-4 mobile:gap-2';
const searchButtonContainerStyles =
  'px-[8px] m-0 border border-l-0 border-lightGrey2 flex items-center hover:outline hover:outline-darkGrey1 hover:outline-1 hover:-outline-offset-1 mobile:px-0 mobile:mt-4 mobile:pb-6 mobile:border-0';

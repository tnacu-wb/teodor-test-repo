import { Flex } from '@chakra-ui/react';
import type {
  AcceptedRoomCodes,
  DatepickerSelectionDate,
  ObjKeyAccessType,
  PromotionOffer,
  ScreenSize,
  SearchAEMTranslationsType,
  SearchPartialTranslationsType,
  SearchPlaceType,
  SearchPropertyType,
  SearchRequestParamsType,
  SearchRoomCodes,
  SearchRoomOccupancyLimitationsType,
  SearchRoomType,
  SearchStayRulesResponseType,
  SearchSuggestions,
  ResponsiveValue,
} from '@whitbread-eos/api';
import { OfferEnum, StandardRoomType } from '@whitbread-eos/api';
import { Button, Icon, InfoMessage, SearchIcon } from '@whitbread-eos/atoms';
import { upperFirst } from '@whitbread-eos/utils';
import { add, differenceInDays } from 'date-fns';
import { nanoid } from 'nanoid';
import { useTranslation } from 'next-i18next';
import { ReactElement, useEffect, useRef, useState } from 'react';

import BookingDatepicker from '../BookingDatepicker';
import LocationPicker from '../LocationPicker';
import RoomPicker from '../RoomPicker';

interface Props {
  defaultLocation?: string;
  searchLocation?: string;
  defaultRooms?: SearchRoomType[];
  ARRdd?: number;
  ARRmm?: number;
  ROOMS?: number;
  ARRyyyy?: number;
  NIGHTS?: number;
  nrOfNights?: number;
  dataRoomOccupancyLimitations: SearchRoomOccupancyLimitationsType;
  dataStayRules: SearchStayRulesResponseType;
  partialTranslations: SearchPartialTranslationsType;
  roomCodes: SearchRoomCodes;
  AEMTranslations: SearchAEMTranslationsType;
  locale?: string;
  screenSize: ScreenSize;
  handleButtonClick: (queryParams: SearchRequestParamsType | undefined) => void | Promise<any>;
  suggestions: SearchSuggestions;
  onLocationInputChange?: (value: string | undefined) => void;
  onLocationInputFocus?: (value: string | undefined) => void;
  onLocationInputClear?: () => void;
  onSelectDates: (props: DatepickerSelectionDate) => void;
  onOccupancyChange?: () => void;
  onIsSearchActive?: (value: boolean) => void;
  numberOfNightsComponent?: ReactElement;
  promotionComponent?: ReactElement;
  handleSetNumberOfNights?: (param: number) => void;
  companyNameComponent?: ReactElement;
  searchStyles: any;
  errorField?: string;
  errorMessage?: string;
  isSearchActive: boolean;
  mappedRoomLabels: ObjKeyAccessType;
  startDate: Date | null;
  endDate: Date | null;
  isDatepickerError?: boolean;
  showMultipleRooms?: boolean;
  isActiveMatchedOffer?: boolean;
  matchedOffer?: PromotionOffer;
  channel?: string;
  isDatePickerFocus?: boolean;
  displayDatesNotification?: boolean;
  searchDisabled?: boolean;
  isPIGroupBookingFormEnabled?: boolean;
  isBBGroupBookingFormEnabled?: boolean;
  isDiscountRateEnabled?: boolean;
  isBarrierFreeLabelEnabled?: boolean;
  marginBottom?: ResponsiveValue;
  disableFlip?: boolean;
}

const FIELDS = {
  datepicker: 'datepicker',
  location: 'location',
  occupancy: 'occupancy',
  numberOfNights: 'numberOfNights',
};

export default function SearchComponent({
  defaultLocation,
  defaultRooms,
  dataStayRules,
  dataRoomOccupancyLimitations,
  partialTranslations,
  roomCodes,
  AEMTranslations,
  locale,
  screenSize,
  handleButtonClick,
  suggestions,
  onIsSearchActive,
  onLocationInputChange,
  onLocationInputClear,
  onLocationInputFocus,
  numberOfNightsComponent,
  promotionComponent,
  companyNameComponent,
  searchStyles,
  onSelectDates,
  onOccupancyChange,
  errorField,
  errorMessage,
  isSearchActive,
  mappedRoomLabels,
  startDate,
  endDate,
  isDatepickerError,
  showMultipleRooms,
  isActiveMatchedOffer,
  matchedOffer,
  channel,
  isDatePickerFocus,
  displayDatesNotification,
  searchDisabled = false,
  isPIGroupBookingFormEnabled,
  isBBGroupBookingFormEnabled,
  isDiscountRateEnabled,
  isBarrierFreeLabelEnabled,
  marginBottom,
  disableFlip,
}: Readonly<Props>) {
  const isGroupBookingFormMessage = isPIGroupBookingFormEnabled || isBBGroupBookingFormEnabled;
  const { t } = useTranslation();

  const { isLessThanLg, isLessThanMd, isLessThanSm, isLessThanXs } = screenSize;
  const [location, setLocation] = useState<SearchPropertyType | SearchPlaceType | undefined>(
    undefined
  );
  const mappedDefaultRooms = mapDefaultRoomsForRoomPicker(defaultRooms);

  const getRoomsInitialState = () => {
    if (mappedDefaultRooms.length) {
      return mappedDefaultRooms;
    }

    return [
      {
        id: nanoid(),
        adults: 1,
        children: 0,
        shouldIncludeCot: false,
        roomType: partialTranslations?.content?.global?.double || StandardRoomType.DB,
        shouldBeAccessible: false,
      },
    ];
  };

  const [rooms, setRooms] = useState<SearchRoomType[]>(getRoomsInitialState());

  const isFirstRender = useRef(true);
  useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      return;
    }
    const mapped = mapDefaultRoomsForRoomPicker(defaultRooms);
    if (mapped.length) {
      setRooms(mapped);
    }
  }, [defaultRooms]);

  const {
    searchWrapper,
    locationPickerStyles,
    inputGroupStyles,
    inputElementStyles,
    bookingDatepickerSize,
    datepickerInputElementStyles,
    iconStyles,
    errorInputGroupStyles,
    errorInputElementStyles,
    errorMarginBottom,
    buttonStyles,
    roomPickerSize,
    roomPickerWrapperStyles,
    roomPickerInputElementStyles,
    locationErrorGroupStyles,
  } = searchStyles;

  const autocompleteStyles = {
    locationPickerStyles,
    inputGroupStyles,
    inputElementStyles,
    errorMarginBottom,
    ...(errorField === FIELDS.location && {
      errorInputGroupStyles,
      errorInputElementStyles,
      locationErrorGroupStyles,
    }),
  };

  const bookingDatepickerStyles = {
    inputGroupStyles,
    bookingDatepickerSize,
    datepickerInputElementStyles,
    iconStyles,
    ...((errorField === FIELDS.datepicker ||
      (errorField === FIELDS.numberOfNights && isDatepickerError)) && {
      errorInputGroupStyles,
      errorInputElementStyles,
      errorMarginBottom,
    }),
  };

  const queryParams = getMappedInputData();

  const maxNumberOfRooms = getMaxNumberOfRooms();
  const groupBookingMessage = isGroupBookingFormMessage
    ? partialTranslations.results.notifications.groupBookingFormPageMessage
    : partialTranslations.results.notifications.groupBookingMessage;

  function getRoomsWarningDescription() {
    if (maxNumberOfRooms === 2 && matchedOffer?.cellCode === OfferEnum.EMPLOYEE)
      return partialTranslations.results.notifications.emp01groupBookingMessage;
    else if (isDiscountRateEnabled && matchedOffer?.corpId)
      return t(`promotion.discountrate.${matchedOffer?.ratePlanCode}.roomlimit`, {
        maxRooms: matchedOffer?.maxRooms,
      });
    else return groupBookingMessage;
  }

  function getCcuiRoomsWarningDescription() {
    if (isDiscountRateEnabled && matchedOffer?.corpId)
      return t(`promotion.discountrate.${matchedOffer?.ratePlanCode}.roomlimit`, {
        maxRooms: matchedOffer?.maxRooms,
      });
    else return partialTranslations.results.notifications.ccuiGroupBookingMessage;
  }
  return (
    <Flex direction="column">
      <Flex
        align="center"
        bgColor="baseWhite"
        {...searchWrapper}
        {...getContainerSpacingStyles(marginBottom)}
        data-testid="search-component"
        onFocus={() => {
          if (onIsSearchActive) {
            onIsSearchActive(true);
          }
        }}
      >
        <LocationPicker
          inputPlaceholder={getLocationPlaceholder()}
          defaultInputValue={defaultLocation}
          isLocationRequired={true}
          hasListDivider={false}
          styles={autocompleteStyles}
          onSelectLocation={handleSelectLocation}
          suggestions={suggestions}
          onInputChange={onLocationInputChange}
          onInputClear={onLocationInputClear}
          onInputFocus={onLocationInputFocus}
          {...(errorField === FIELDS.location && {
            showErrorMessage: true,
            errorMessage: errorMessage,
          })}
        />
        <BookingDatepicker
          displayDateFormat="dd MMM yyyy"
          inputPlaceholderDatepicker={getInputPlaceholderDatepicker()}
          maxNumberOfNights={
            isActiveMatchedOffer && matchedOffer?.numberOfNights
              ? Number(matchedOffer.numberOfNights)
              : dataStayRules && Number(dataStayRules?.maxNightsLimitation?.maxNights)
          }
          maxArrivalDate={
            dataStayRules && Number(dataStayRules?.maxArrivalDateLimitation?.maxArrivalDate)
          }
          datepickerStyles={bookingDatepickerStyles}
          onSelectDates={onSelectDates}
          defaultStartDate={startDate}
          defaultEndDate={endDate}
          partialTranslations={partialTranslations}
          locale={locale}
          isError={isDatepickerError}
          {...((errorField === FIELDS.datepicker ||
            (errorField === FIELDS.numberOfNights && isDatepickerError)) && {
            showErrorMessage: true,
            errorMessage: errorMessage,
          })}
          isLessThanSm={isLessThanSm}
          isDatePickerFocus={isDatePickerFocus}
          displayDatesNotification={displayDatesNotification}
          disableFlip={disableFlip}
        />

        {numberOfNightsComponent}
        <RoomPicker
          showMultipleRooms={showMultipleRooms}
          roomPickerInputElementStyles={roomPickerInputElementStyles}
          boxWrapperStyles={roomPickerWrapperStyles}
          roomPickerSize={roomPickerSize}
          onSubmit={(rooms) => {
            setRooms(rooms);
            onOccupancyChange?.();
          }}
          initialState={rooms}
          maxNumberOfRooms={maxNumberOfRooms}
          labels={{
            roomsWarningTitle: partialTranslations.results.notifications.groupBookingHeader,
            roomsWarningDescription: getRoomsWarningDescription(),
            roomsWarningDescriptionCCUI: getCcuiRoomsWarningDescription(),
            addMoreRoomsLabel: partialTranslations.content.global.addRoom,
            removeRoomButtonLabel: partialTranslations.form.removeRoom,
            doneButtonLabel: partialTranslations.content.global.done,
            adult: partialTranslations.content.global.adult,
            adults: partialTranslations.content.global.adults,
            adultsLabel: AEMTranslations.adultsLabel,
            adultsMaxPerRoomLabel: partialTranslations.form.adultsHelperText,
            child: partialTranslations.content.global.child,
            children: partialTranslations.content.global.children,
            childrenLabel: AEMTranslations.childrenLabel,
            childrenAgeLabel: partialTranslations.form.childrenHelperText,
            cotLimit: partialTranslations.form.cotLimit,
            cotLabel: partialTranslations.form.includeCot,
            room: partialTranslations.content.global.room,
            rooms: partialTranslations.content.global.rooms,
            roomLabel: partialTranslations.content.global.room,
            roomTypeLabel: AEMTranslations.roomTypeLabel,
            single: partialTranslations.content.global.single,
            double: partialTranslations.content.global.double,
            accessible: isBarrierFreeLabelEnabled
              ? (partialTranslations.content.global.accessibleOrBarrierFree ?? '')
              : partialTranslations.content.global.accessible,
            twin: partialTranslations.content.global.twin,
            family: partialTranslations.content.global.family,
            accessibleRoom: AEMTranslations.accessibleRoom ?? '',
          }}
          dataRoomOccupancyLimitations={dataRoomOccupancyLimitations}
          roomCodes={roomCodes}
          screenSize={screenSize}
          {...(errorField === FIELDS.occupancy && {
            showErrorMessage: true,
            errorMessage: errorMessage,
          })}
          channel={channel}
        />
        {isLessThanSm && errorField === FIELDS.occupancy && errorMessage && (
          <InfoMessage infoMessage={errorMessage} otherStyles={alertStyles} />
        )}
        {isLessThanSm &&
          (errorField === FIELDS.datepicker || errorField === FIELDS.numberOfNights) &&
          errorMessage && <InfoMessage infoMessage={errorMessage} />}
        {promotionComponent}
        <Button
          size="md"
          variant="login"
          name="search-button"
          isDisabled={searchDisabled}
          {...buttonStyles}
          mt={{
            base: 'md',
            sm: '0',
          }}
          onClick={() => handleButtonClick(queryParams)}
        >
          {getButtonContent()}
        </Button>
      </Flex>
      {isSearchActive && companyNameComponent}
    </Flex>
  );

  function getMaxNumberOfRooms() {
    if (isActiveMatchedOffer) {
      return Number(matchedOffer?.maxRooms);
    }
    return Number(dataStayRules?.globalConfig?.maxRoomsLim?.maxRooms);
  }

  function getInputPlaceholderDatepicker() {
    if (isDatepickerError) {
      return AEMTranslations?.datepickerInvalidDates;
    }

    return `${AEMTranslations.datepickerCheckinLabel} | ${AEMTranslations.datepickerCheckoutLabel}`;
  }

  function handleSelectLocation(location: SearchPropertyType | SearchPlaceType | undefined) {
    setLocation(location);
  }

  function getMappedInputData() {
    if (!location) {
      return;
    }

    const start = startDate ?? new Date();
    const end = endDate ?? add(new Date(start), { days: 1 });
    const queryParams: SearchRequestParamsType = {
      searchTerm: location.suggestion,
      ARRdd: start.getDate(),
      ARRmm: start.getMonth() + 1,
      ARRyyyy: start.getFullYear(),
      nights: differenceInDays(end, start),
      roomsNumber: rooms.length,
      rooms: rooms,
    };

    if ('placeId' in location) {
      queryParams.placeId = location.placeId;
    }

    if ('code' in location) {
      queryParams.code = location.code;
    }

    if ('geometry' in location) {
      queryParams.location = location.geometry.coordinates;
    }

    if ('brand' in location) {
      queryParams.brand = location.brand;
    }

    return queryParams;
  }

  function getLocationPlaceholder() {
    return isLessThanLg && !isLessThanSm
      ? AEMTranslations.locationPlaceholder
      : partialTranslations?.form?.where;
  }

  function getButtonContent() {
    if (isLessThanSm && !isLessThanXs) {
      return AEMTranslations.submitButtonLabel;
    }

    if (isLessThanMd && !isLessThanSm) {
      return <Icon svg={<SearchIcon color="var(--chakra-colors-baseWhite)" />} />;
    }

    return AEMTranslations.submitButtonLabel;
  }

  function mapDefaultRoomsForRoomPicker(rooms: SearchRoomType[] | undefined) {
    if (!rooms || rooms.length > dataStayRules.maxNightsLimitation.maxNights) return [];
    const filteredRooms = rooms.filter(
      (r) => r.adults && r.children !== null && r.children !== undefined && !isNaN(r.children)
    );
    return filteredRooms.map((r) => {
      const roomTypeCode = r.roomType as AcceptedRoomCodes;
      const roomType = mappedRoomLabels[roomTypeCode];

      if (!Object.keys(mappedRoomLabels).includes(roomTypeCode))
        return { ...r, adults: 1, roomType: mappedRoomLabels.DB, shouldBeAccessible: false };
      return {
        ...r,
        id: nanoid(),
        roomType: upperFirst(roomType),
      };
    });
  }
}

function getContainerSpacingStyles(marginBottom?: ResponsiveValue) {
  const mb = 2;
  return {
    mb: marginBottom ?? {
      mobile: 'var(--chakra-space-56)',
      xs: 'var(--chakra-space-52)',
      sm: `${mb}rem`,
    },
  };
}

const alertStyles = {
  w: 'full',
  zIndex: 1,
  _before: {
    content: '" "',
    position: 'absolute',
    top: '-0.15rem',
    right: '1.15rem',
    width: '1.5rem',
    height: '0.75rem',
    zIndex: '-1',
    transform: 'rotate(-45deg)',
    bgColor: 'tooltipError',
    borderRadius: '3',
  },
};

import { Text } from '@chakra-ui/react';
import type {
  SearchRequestParamsType,
  SearchRoomType,
  SearchPropertyType,
  SearchManagedPlaceType,
  SearchBookingDateType,
  SearchPlaceType,
  DatepickerSelectionDate,
  DatepickerRangeSelectionDate,
  SearchSuggestions,
  SearchStayRulesResponseType,
  SearchRoomOccupancyLimitationsType,
} from '@whitbread-eos/api';
import {
  BOOKING_CHANNEL,
  GET_SEARCH_RULES_QUERY,
  SITE_LEISURE,
  BUSINESS_BOOKER_USER_ROLES,
  FT_BB_GROUP_BOOKING_FORM,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getStaticContent,
  CountryCode,
} from '@whitbread-eos/api';
import { SearchSummary, SearchSummaryProps } from '@whitbread-eos/atoms';
import {
  Search,
  STAY_DETAILS_STATE_INITIAL_VALUE,
  SEARCH_REFERRER_INITIAL_VALUE,
} from '@whitbread-eos/molecules';
import {
  useQueryRequest,
  useScreenSize,
  swapKeysAndValues,
  ternaryCondition,
  logicalAndOperator,
  useCustomLocale,
  useLocalStorage,
  getMappedRooms,
  useUserData,
  useUserDetails,
  getAuthCookie,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { add, isValid } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import debounce from 'lodash/debounce';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import { useState, useEffect } from 'react';

import { getSearchStyles } from '../styles/bb.style';
import { getRoomsPlaceholder, singleHotelSearchBB, appendPromoIdToUrl } from '../utilities';
import {
  formatSummaryDateRange,
  setSearchLocationInLocalStorage,
  mapSearchParamsForURL,
  ERROR_KEYS,
  FIELDS,
  ERROR_FIELDS,
} from '../utilities/searchContainerHelpers';
import { validateSearchData } from '../validations/searchValidation';

export type ValidationArray = { [key: string]: boolean }[];

export interface Props {
  defaultLocation?: string;
  searchLocation?: string;
  defaultRooms?: SearchRoomType[];
  ARRdd?: number;
  ARRmm?: number;
  ARRyyyy?: number;
  ROOMS?: number;
  NIGHTS?: number;
  inputPlaceholder?: string;
  defaultInputValue?: string;
  isLocationRequired?: boolean;
  hasListDivider?: boolean;
  showNoHotelsWarning?: boolean;
  onSelectLocation?: (
    params: SearchPlaceType | SearchPropertyType | SearchManagedPlaceType | undefined
  ) => void;
  isSummaryActive?: boolean;
  variant?: string;
  PROMOID?: string;
}

const MIN_LENGTH_SEARCH_TERM = 3;

export default function SearchContainer({
  searchLocation,
  defaultLocation,
  defaultRooms,
  ARRdd,
  ARRmm,
  ROOMS,
  ARRyyyy,
  NIGHTS,
  isSummaryActive = true,
  variant,
  PROMOID,
}: Readonly<Props>) {
  const initialData: SearchSuggestions = {
    managedPlaces: [],
    places: [],
    properties: [],
  };
  const {
    [FT_BB_GROUP_BOOKING_FORM]: isBBGroupBookingFormEnabled,
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
  } = useFeatureToggle();
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const [items, setItems] = useState<SearchSuggestions | null>(null);
  const [errorCode, setErrorCode] = useState<[string, string] | [undefined, undefined]>(['', '']);

  const [searchConsoleIsActive, setSearchConsoleIsActive] = useState(false);
  const [searchSummaryActive, setSearchSummaryActive] = useState<boolean>(true);

  const [stayDetailsState, setStayDetailsState] = useLocalStorage(
    'StayDetailsState',
    STAY_DETAILS_STATE_INITIAL_VALUE
  );

  const [, setSearchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);

  const router = useRouter();
  const { language, country } = useCustomLocale();
  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn);
  const accessLevel = userData?.business?.accessLevel;
  const { t } = useTranslation();
  const screenSize = useScreenSize();
  const { isLessThanSm, isLessThanMd, isLessThanLg } = screenSize;
  const channel = BOOKING_CHANNEL.BB;
  const idTokenCookie = getAuthCookie();

  const [showMultipleRooms, setShowMultipleRooms] = useState(false);

  useEffect(() => {
    setShowMultipleRooms(
      [BUSINESS_BOOKER_USER_ROLES.STAYER, BUSINESS_BOOKER_USER_ROLES.SELF].includes(accessLevel)
    );
  }, [accessLevel]);
  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

  const placeholderDate = (ARRyyyy &&
    ARRmm &&
    ARRdd &&
    new Date(ARRyyyy, ARRmm - 1, ARRdd)) as Date;
  const firstDefaultEndDate =
    (placeholderDate && add(placeholderDate, { days: (NIGHTS as number) > 0 ? NIGHTS : 1 })) ||
    null;
  const firstDefaultStartDate = placeholderDate || null;

  const today = new Date();
  const tomorrow = add(today, { days: 1 });

  const [startDate, setStartDate] = useState<SearchBookingDateType>(firstDefaultStartDate);
  const [endDate, setEndDate] = useState<SearchBookingDateType>(firstDefaultEndDate);
  const [isNumberOfNightsError, setIsNumberOfNightsError] = useState<boolean>(false);

  let errorKey: string[] | undefined[] | [any, any] = ['', ''];
  const [validationKey, validationField] = errorCode;
  const isDatepickerError = validationField === FIELDS.datepicker;

  const {
    isLoading: isLoadingSearchRules,
    isError: isErrorSearchRules,
    data: dataSearchRules,
    error: errorSearchRules,
  } = useQueryRequest(['getSearchRules', channel], GET_SEARCH_RULES_QUERY, {
    channel,
  });

  const isNumberOfNightsUrlError =
    Number(NIGHTS) < 1 ||
    !NIGHTS ||
    NIGHTS > (dataSearchRules as SearchStayRulesResponseType)?.maxNightsLimitation?.maxNights;

  useEffect(() => {
    if (isDatepickerError) {
      setStartDate(null);
      setEndDate(null);
    }
    if (isNumberOfNightsUrlError) {
      setIsNumberOfNightsError(true);
      setStartDate(today);
      setEndDate(tomorrow);
    }
  }, [isDatepickerError]);

  const query = getStaticContent(isBarrierFreeLabelEnabled);

  const {
    isLoading: isLoadingTranslations,
    isError: isErrorTranslations,
    data: dataTranslations,
    error: errorTranslations,
  } = useQueryRequest(['GetStaticContent', language, country], query, {
    country,
    language,
    site: SITE_LEISURE,
    businessBooker: true,
  });

  useEffect(() => {
    if (defaultLocation) {
      handleLocationInputChange(defaultLocation);
    }
  }, []);

  useEffect(() => {
    if (typeof isSummaryActive === 'boolean') {
      setSearchSummaryActive(isSummaryActive);
    }
  }, [isSummaryActive, defaultLocation, defaultLocation, searchLocation]);

  let URL = router.asPath;

  useEffect(() => {
    if (router.asPath !== URL) {
      router.push(`/${country}${URL}`);
    }
    if (errorKey) {
      const [key, field] = errorKey;
      const [currentKey] = errorCode;
      if (key !== currentKey && key !== undefined && field !== undefined) {
        setErrorCode([key, field]);
      }
    }
  });

  useEffect(() => {
    const [key, field] = errorCode;
    if (key && field) {
      setSearchSummaryActive(false);
      setSearchConsoleIsActive(true);
    }
  }, [errorCode]);

  const isSearchLoading = isLoadingSearchRules || isLoadingTranslations;

  if (isSearchLoading) {
    return <Text data-testid="loading-message">{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isErrorSearchRules) {
    return (
      <Text data-testid="error-search-rules-message">{(errorSearchRules as Error).message}</Text>
    );
  }

  if (isErrorTranslations) {
    return (
      <Text data-testid="error-translations-message">{(errorTranslations as Error).message}</Text>
    );
  }

  const validateSearchDataResults = validateSearchData(
    Number(ARRdd),
    Number(ARRmm),
    Number(ARRyyyy),
    Number(NIGHTS),
    items,
    dataTranslations,
    router,
    (dataSearchRules as SearchStayRulesResponseType)?.maxArrivalDateLimitation?.maxArrivalDate,
    (dataSearchRules as SearchStayRulesResponseType)?.maxNightsLimitation?.maxNights,
    defaultRooms,
    showMultipleRooms,
    (dataSearchRules as SearchStayRulesResponseType)?.globalConfig?.maxRoomsLim?.maxRooms
  );
  errorKey = validateSearchDataResults.errorKey;
  URL = validateSearchDataResults.url;

  const partialTranslations = dataTranslations?.headerInformation || {};
  const roomCodes = dataTranslations?.headerInformation?.config?.roomCodes || {};
  const globalTranslations = partialTranslations?.content?.global || {};
  const swappedRoomCodes = swapKeysAndValues(roomCodes);
  const AEMTranslations = {
    adultsLabel: t('dashboard.bookings.adults'),
    childrenLabel: t('dashboard.bookings.children'),
    roomTypeLabel: t('search.roomType'),
    locationPlaceholder: t('hoteldetails.location'),
    submitButtonLabel: t('search.submit.search'),
    datepickerCheckinLabel: t('account.dashboard.checkin.text'),
    datepickerCheckoutLabel: t('account.dashboard.checkout.text'),
    datepickerInvalidDates: partialTranslations?.datePicker?.invalidDate,
  };

  const errorMessages = {
    [`${ERROR_KEYS.invalidLocation}`]: t('error.blank.location'),
    [`${ERROR_KEYS.invalidDate}`]: isNumberOfNightsError
      ? dataTranslations?.headerInformation?.form?.invalidNights
      : dataTranslations?.headerInformation?.form?.invalidDate,
    [`${ERROR_KEYS.arrivalDateInThePast}`]:
      dataTranslations?.headerInformation?.form?.invalidPastDate,
    [`${ERROR_KEYS.arrivalDateInTheFuture}`]:
      dataTranslations?.headerInformation?.form?.invalidFutureDate,
    [`${ERROR_KEYS.invalidNights}`]: dataTranslations?.headerInformation?.form?.invalidNights,
    [`${ERROR_KEYS.invalidOccupancy}`]: partialTranslations?.form?.invalidRooms,
  };
  const mappedRoomLabels = getMappedRooms(roomCodes, globalTranslations, isBarrierFreeLabelEnabled);
  const message = validationKey && errorMessages[validationKey];

  const searchStyles = getSearchStyles(
    isLessThanSm,
    logicalAndOperator(validationKey !== '', validationKey !== undefined),
    language,
    items?.places,
    validationField
  );

  const searchConsoleActiveHandler = (value: boolean) => {
    setSearchConsoleIsActive(value);
  };

  const translationDateLanguage = {
    locale: ternaryCondition(language === 'en', enGB, de),
  };

  const searchSummaryProps = searchSummaryActive
    ? ({
        handleEdit: setSearchSummaryActive,
        isSummaryActive: searchSummaryActive,
        location: defaultLocation,
        dateInterval:
          isValid(startDate) &&
          (!isLessThanSm
            ? formatSummaryDateRange('dd MMM', startDate, endDate, translationDateLanguage)
            : formatSummaryDateRange('EEE dd MMM', startDate, endDate, translationDateLanguage)),
        roomSummary: getRoomsPlaceholder(defaultRooms as SearchRoomType[], {
          adult: partialTranslations?.content?.global?.adult,
          adults: partialTranslations?.content?.global?.adults,
          child: partialTranslations?.content?.global?.child,
          children: partialTranslations?.content?.global?.children,
          room: partialTranslations?.content?.global?.room,
          rooms: partialTranslations?.content?.global?.rooms,
        }),
        editText: t('amend.edit'),
        isLessThanSm,
        isLessThanMd,
        isLessThanLg,
      } as SearchSummaryProps)
    : ({} as SearchSummaryProps);

  const suggestions = items || initialData;

  return (
    <>
      {!searchSummaryActive ? (
        <Search
          key={router.asPath}
          locale={language}
          onLocationInputChange={debounce(handleLocationInputChange, 300)}
          onLocationInputClear={handleLocationInputClear}
          onLocationInputFocus={handleLocationInputFocus}
          onIsSearchActive={searchConsoleActiveHandler}
          onSelectDates={handleSelectDates}
          onOccupancyChange={handleRoomOccupancyChange}
          suggestions={suggestions}
          roomCodes={swappedRoomCodes}
          errorField={validationField}
          errorMessage={message}
          isSearchActive={searchConsoleIsActive}
          mappedRoomLabels={swapKeysAndValues(mappedRoomLabels)}
          showMultipleRooms={showMultipleRooms}
          isBBGroupBookingFormEnabled={isBBGroupBookingFormEnabled}
          isBarrierFreeLabelEnabled={isBarrierFreeLabelEnabled}
          {...{
            searchLocation,
            ARRdd,
            ARRmm,
            ARRyyyy,
            ROOMS,
            NIGHTS,
            dataStayRules: dataSearchRules as SearchStayRulesResponseType,
            dataRoomOccupancyLimitations: dataSearchRules as SearchRoomOccupancyLimitationsType,
            partialTranslations,
            AEMTranslations,
            screenSize,
            handleButtonClick,
            searchStyles,
            defaultLocation,
            defaultRooms,
            startDate,
            endDate,
            isDatepickerError,
          }}
        />
      ) : (
        <SearchSummary {...searchSummaryProps} />
      )}
    </>
  );

  async function getSuggestions(value: string) {
    const response = await fetch(
      `${publicRuntimeConfig.NEXT_PUBLIC_SNOWDROP_BASE_URL}v1/autocomplete?input=/${value}&gplaces[components]=country:uk|country:de`
    );
    return response.json();
  }

  function handleLocationInputChange(value: string | undefined) {
    if (value && value.trim().length >= MIN_LENGTH_SEARCH_TERM) {
      getSuggestions(value).then((data) => {
        setItems(data);
        if (validationField === FIELDS.location) {
          setErrorCode([undefined, undefined]);
        }
      });
    } else {
      setItems(null);
    }
  }

  function handleLocationInputClear() {
    setItems(null);
    if (validationField === FIELDS.location) {
      setErrorCode([undefined, undefined]);
    }
  }

  function handleLocationInputFocus(searchTerm: string | undefined) {
    if (searchTerm?.length === 0) {
      if (validationField === FIELDS.location) {
        setErrorCode([undefined, undefined]);
      }

      setItems(null);
    }
  }

  function buildRedirectUrl(
    paramsMappedForURL: any,
    roomOccupancyParamsForURL: string,
    promoId?: string
  ) {
    const baseUrl = `ARRdd=${paramsMappedForURL.ARRdd}&ARRmm=${paramsMappedForURL.ARRmm}&ARRyyyy=${paramsMappedForURL.ARRyyyy}&NIGHTS=${paramsMappedForURL.nights}&ROOMS=${paramsMappedForURL.roomsNumber}&${roomOccupancyParamsForURL}`;
    return appendPromoIdToUrl(baseUrl, promoId);
  }

  async function handleButtonClick(queryParams: SearchRequestParamsType | undefined) {
    if (!queryParams?.searchTerm) {
      setErrorCode([ERROR_KEYS.invalidLocation, ERROR_FIELDS.invalidLocation]);
      return;
    }

    const stayDetailsInfo = setSearchLocationInLocalStorage(queryParams, stayDetailsState);

    setStayDetailsState((stayDetailsState) => {
      stayDetailsState.data.info = stayDetailsInfo;
      return stayDetailsState;
    });

    const referrer = {
      data: {
        referrer: router.asPath,
      },
    };
    setSearchReferrer(referrer);

    const paramsMappedForURL = mapSearchParamsForURL(queryParams);
    const roomOccupancyParamsForURL = queryParams?.rooms
      ?.filter((room) => room.roomType)
      .map(
        (room, idx) =>
          `ADULT${idx + 1}=${room.adults}&CHILD${idx + 1}=${room.children}&COT${idx + 1}=${
            room.shouldIncludeCot ? 1 : 0
          }&INTTYP${idx + 1}=${room.roomType && mappedRoomLabels[room.roomType]}`
      )
      .join('&');

    const promoIdParam = PROMOID || paramsMappedForURL?.PROMOID;

    const URLToRedirect = buildRedirectUrl(
      paramsMappedForURL,
      roomOccupancyParamsForURL as string,
      promoIdParam
    );

    const singleHotelSearchLabels = {
      bookingChannel: partialTranslations.config.api.bookingChannel.business,
      mappedRoomLabels: mappedRoomLabels,
    };

    window.location = (await singleHotelSearchBB(
      stayDetailsState,
      paramsMappedForURL,
      singleHotelSearchLabels,
      URLToRedirect,
      queryParams,
      country,
      language,
      BOOKING_CHANNEL.BB,
      variant,
      idTokenCookie
    )) as unknown as Location;
  }

  function handleSelectDates(dates: DatepickerSelectionDate) {
    const [start, end] = dates as DatepickerRangeSelectionDate;
    setStartDate(start);
    setEndDate(end);

    if (validationField === FIELDS.datepicker || validationField === FIELDS.numberOfNights) {
      setErrorCode([undefined, undefined]);
    }
  }

  function handleRoomOccupancyChange() {
    if (validationField === FIELDS.occupancy) {
      setErrorCode([undefined, undefined]);
    }
  }
}

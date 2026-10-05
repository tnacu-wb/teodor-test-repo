import { Text } from '@chakra-ui/react';
import {
  GET_SEARCH_RULES_QUERY,
  SearchCcuiReducer,
  SITE_LEISURE,
  BOOKING_CHANNEL,
  SEARCH_COMPANY_BY_ID,
  SEARCH_COMPANY_BY_ID_OR_CORP_ID,
  SearchStayRulesResponseType,
  SearchRoomOccupancyLimitationsType,
  FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID,
  FT_PI_DISCOUNT_RATE,
  FT_PI_BB_CCUI_BARRIER_FREE_LABEL,
  getStaticContent,
  CountryCode,
} from '@whitbread-eos/api';
import type {
  SearchSuggestions,
  SearchRequestParamsType,
  SearchPlaceType,
  SearchPropertyType,
  SearchManagedPlaceType,
  SearchRoomType,
  SearchBookingDateType,
  DatepickerSelectionDate,
  DatepickerRangeSelectionDate,
  Company,
  PromotionOffer,
} from '@whitbread-eos/api';
import { SearchSummary, SearchSummaryProps } from '@whitbread-eos/atoms';
import {
  STAY_DETAILS_STATE_INITIAL_VALUE,
  SEARCH_REFERRER_INITIAL_VALUE,
  DISTANCE_FROM_SEARCH_INITIAL_VALUE,
} from '@whitbread-eos/molecules';
import {
  useCustomLocale,
  useFeatureToggle,
  useLocalStorage,
  swapKeysAndValues,
  useScreenSize,
  useQueryRequest,
  ternaryCondition,
  logicalAndOperator,
  getMappedRooms,
} from '@whitbread-eos/utils';
import { add, differenceInDays, isValid } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import debounce from 'lodash/debounce';
import { useTranslation } from 'next-i18next';
import getConfig from 'next/config';
import { useRouter } from 'next/router';
import { useState, useEffect, useReducer } from 'react';

import { getSearchStyles } from '../styles/ccui.style';
import {
  getRoomsPlaceholder,
  singleHotelSearchCCUI,
  formatSummaryDateRange,
  setSearchLocationInLocalStorage,
  mapSearchParamsForURL,
  ERROR_KEYS,
  FIELDS,
  ERROR_FIELDS,
  appendPromoIdToUrl,
} from '../utilities';
import { validateSearchData } from '../validations/searchValidation';
import SearchComponent from './Search.component';

export interface Props {
  defaultLocation?: string;
  searchLocation?: string;
  defaultRooms?: SearchRoomType[];
  ARRdd?: number;
  ARRmm?: number;
  ROOMS?: number;
  ARRyyyy?: number;
  NIGHTS?: number;
  CORPID?: string;
  PROMOID?: string;
  COMPID?: string;
  inputPlaceholder?: string;
  defaultInputValue?: string;
  isLocationRequired?: boolean;
  hasListDivider?: boolean;
  onSelectLocation?: (
    params: SearchPlaceType | SearchPropertyType | SearchManagedPlaceType | undefined
  ) => void;
  isSummaryActive?: boolean;
  setIsSearchError?: React.Dispatch<React.SetStateAction<boolean>>;
  isDatePickerFocus?: boolean;
  hideErrorForMinNights?: boolean;
  displayDatesNotification?: boolean;
  prevReservationId?: string;
  deleteSessionStorageValues?: () => void;
}
interface SelectedCompany extends Company {
  searchCriteriaId?: string;
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
  CORPID,
  PROMOID,
  COMPID,
  isSummaryActive = true,
  setIsSearchError,
  isDatePickerFocus,
  hideErrorForMinNights,
  displayDatesNotification,
  prevReservationId,
  deleteSessionStorageValues,
}: Readonly<Props>) {
  const initialData: SearchSuggestions = {
    managedPlaces: [],
    places: [],
    properties: [],
  };

  const placeholderDate = (ARRyyyy &&
    ARRmm &&
    ARRdd &&
    new Date(ARRyyyy, ARRmm - 1, ARRdd)) as Date;
  const firstDefaultEndDate = (placeholderDate && add(placeholderDate, { days: NIGHTS })) || null;
  const firstDefaultStartDate = placeholderDate || null;

  const initialSearchState: SearchCcuiReducer = {
    items: null,
    searchConsoleIsActive: false,
    errorCode: ['', ''],
    searchSummaryActive: true,
    startDate: firstDefaultStartDate,
    endDate: firstDefaultEndDate,
    savedNights: NIGHTS === 0 ? 0 : NIGHTS || 1,
    locationInputError: false,
  };

  const [
    {
      items,
      searchConsoleIsActive,
      errorCode,
      searchSummaryActive,
      startDate,
      endDate,
      savedNights,
      locationInputError,
    },
    updateSearchEvent,
  ] = useReducer((prev: SearchCcuiReducer, next: SearchCcuiReducer) => {
    return { ...prev, ...next };
  }, initialSearchState);

  const [contractRateCompany, setContractRateCompany] = useState<SelectedCompany | null>(null);

  const [stayDetailsState, setStayDetailsState] = useLocalStorage(
    'StayDetailsState',
    STAY_DETAILS_STATE_INITIAL_VALUE
  );

  const [, setDistanceFromSearch] = useLocalStorage(
    'DistanceFromSearch',
    DISTANCE_FROM_SEARCH_INITIAL_VALUE
  );
  const [, setSearchReferrer] = useLocalStorage('SearchReferrer', SEARCH_REFERRER_INITIAL_VALUE);
  const router = useRouter();

  const { language, country } = useCustomLocale();
  const { t } = useTranslation();
  const screenSize = useScreenSize();
  const { isLessThanSm, isLessThanMd } = screenSize;
  const channel = BOOKING_CHANNEL.CCUI;

  const { publicRuntimeConfig = {} } = getConfig() || {};

  const {
    [FT_CCUI_SEARCH_NEGOTIATED_RATES_BY_CORP_ID]: isNegotiatedRatesByCompIdEnabled,
    [FT_PI_DISCOUNT_RATE]: isDiscountRateEnabled,
    [FT_PI_BB_CCUI_BARRIER_FREE_LABEL]: isBarrierFreeLabelEnabledFT,
  } = useFeatureToggle();

  const isBarrierFreeLabelEnabled =
    language === CountryCode.DE ? isBarrierFreeLabelEnabledFT : false;

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
    businessBooker: false,
  });

  const {
    isLoading: isLoadingSearchRules,
    isError: isErrorSearchRules,
    data: dataSearchRules,
    error: errorSearchRules,
  } = useQueryRequest(['getSearchRules', channel], GET_SEARCH_RULES_QUERY, {
    channel,
  });

  const queryKey = isNegotiatedRatesByCompIdEnabled ? 'searchCompany' : 'searchCompanyById';
  const gqlTemplate = isNegotiatedRatesByCompIdEnabled
    ? SEARCH_COMPANY_BY_ID_OR_CORP_ID
    : SEARCH_COMPANY_BY_ID;

  const {
    isLoading: isLoadingCompanyData,
    isError: isErrorCompanyData,
    data: companyData,
  } = useQueryRequest(
    [queryKey, CORPID],
    gqlTemplate,
    {
      id: CORPID,
    },
    { enabled: !!CORPID, retry: false },
    undefined,
    true
  );

  const companyProfileData = isNegotiatedRatesByCompIdEnabled
    ? companyData?.companyProfileById
    : companyData?.companyProfile;

  let errorKey: string[] | undefined[] | [any, any] = ['', ''];
  const [validationKey, validationField] = errorCode as [string, string] | [undefined, undefined];
  const isNumberOfNightsError = validationField === FIELDS.numberOfNights;
  const isDatepickerError = validationField === FIELDS.datepicker;

  let URL = router.asPath;

  const isListViewShow = !!errorCode?.[0]?.length && locationInputError;

  useEffect(() => {
    if (router.asPath !== URL) {
      router.push(`/${country}${URL}`);
    }
    if (errorKey) {
      const [key, field] = errorKey;
      const [currentKey] = errorCode as [string, string] | [undefined, undefined];
      if (key !== currentKey && key !== undefined && field !== undefined) {
        updateSearchEvent({ errorCode: [key, field] });
      }

      isListViewShow && setIsSearchError?.(false);
    }
  }, [errorKey]);

  useEffect(() => {
    const [key, field] = errorCode as [string, string] | [undefined, undefined];
    if (key && field) {
      updateSearchEvent({ searchSummaryActive: false });
    }
    updateSearchEvent({ searchConsoleIsActive: true });
  }, [errorCode]);

  useEffect(() => {
    handleLocationFromUrl(defaultLocation);
  }, []);

  useEffect(() => {
    if (isDatepickerError) {
      updateSearchEvent({ startDate: null });
      updateSearchEvent({ endDate: null });
      updateSearchEvent({ savedNights: 1 });
    }
  }, [isDatepickerError]);

  useEffect(() => {
    handleNrOfNightsURLError();
  }, [isNumberOfNightsError, NIGHTS]);

  useEffect(() => {
    if (typeof isSummaryActive === 'boolean') {
      updateSearchEvent({ searchSummaryActive: isSummaryActive });
    }
  }, [isSummaryActive, defaultLocation, searchLocation]);

  useEffect(() => {
    if (!isLoadingCompanyData && !isErrorCompanyData && companyProfileData) {
      setContractRateCompany({ ...companyProfileData, searchCriteriaId: COMPID || CORPID });
    }
  }, [isLoadingCompanyData, isErrorCompanyData, companyData, companyProfileData, CORPID, COMPID]);

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
    return <Text data-testid="error-translations">{(errorTranslations as Error).message}</Text>;
  }
  const offers: PromotionOffer[] =
    dataTranslations?.headerInformation?.content?.global?.offers || [];

  const findOffer = (offers: PromotionOffer[]) => {
    const corpId = CORPID || contractRateCompany?.corpId;
    return isDiscountRateEnabled && corpId
      ? offers.find((offer) => offer.corpId === corpId)
      : undefined;
  };

  const matchedOffer: PromotionOffer | undefined = findOffer(offers);
  const isActiveOffer = (matchedOffer?: PromotionOffer) =>
    matchedOffer !== undefined && Object.keys(matchedOffer).length > 0;

  const isActiveMatchedOffer: boolean = isActiveOffer(matchedOffer);

  const validateSearchDataResults = validateSearchData(
    Number(ARRdd),
    Number(ARRmm),
    Number(ARRyyyy),
    Number(NIGHTS),
    items as SearchSuggestions | null,
    dataTranslations,
    router,
    (dataSearchRules as SearchStayRulesResponseType)?.maxArrivalDateLimitation?.maxArrivalDate,
    (dataSearchRules as SearchStayRulesResponseType)?.maxNightsLimitation?.maxNights,
    defaultRooms,
    false,
    isActiveMatchedOffer
      ? matchedOffer?.maxRooms
      : (dataSearchRules as SearchStayRulesResponseType)?.globalConfig.maxRoomsLim?.maxRooms,
    true
  );
  errorKey = validateSearchDataResults.errorKey;
  URL = validateSearchDataResults.url;
  const partialTranslations = dataTranslations?.headerInformation;
  const roomCodes = dataTranslations?.headerInformation.config.roomCodes;
  const globalTranslations = partialTranslations.content.global;
  const swappedRoomCodes = swapKeysAndValues(roomCodes);

  const AEMTranslations = {
    adultsLabel: t('dashboard.bookings.adults'),
    childrenLabel: t('dashboard.bookings.children'),
    roomTypeLabel: t('search.roomType'),
    locationPlaceholder: t('hoteldetails.location'),
    submitButtonLabel: t('search.submit.search'),
    datepickerCheckinLabel: t('account.dashboard.checkin.text'),
    datepickerCheckoutLabel: t('account.dashboard.checkout.text'),
    locationErrorLabel: t('error.blank.location'),
    numberOfNightsPlaceholder: t('search.nights'),
    companyNameInputPlaceholder: t('ccui.search.companyName.placeholder'),
    companyIdInputPlaceholder: t('ccui.search.companyId.placeholder'),
    promotionCategoryPlaceholder: t('ccui.search.promotionCategory.placeholder'),
    numberOfNightsErrorMessage: t('ccui.search.nrOfnights.errormessage'),
    datepickerInvalidDates: partialTranslations?.datePicker?.invalidDate,
  };

  const errorMessages = {
    [`${ERROR_KEYS.invalidLocation}`]: t('error.blank.location'),
    [`${ERROR_KEYS.invalidDate}`]: dataTranslations?.headerInformation?.form?.invalidDate,
    [`${ERROR_KEYS.arrivalDateInThePast}`]: t('ccui.search.datepicker.errorpast'),
    [`${ERROR_KEYS.arrivalDateInTheFuture}`]: t('ccui.search.datepicker.errorfuture'),
    [`${ERROR_KEYS.invalidNights}`]: dataTranslations?.headerInformation?.form?.invalidNights,
    [`${ERROR_KEYS.invalidOccupancy}`]: partialTranslations?.form?.invalidRooms,
  };
  const message = validationKey && errorMessages[validationKey];

  const mappedRoomLabels = getMappedRooms(roomCodes, globalTranslations, isBarrierFreeLabelEnabled);

  const searchStyles = getSearchStyles(
    isLessThanSm,
    logicalAndOperator(validationKey !== '', validationKey !== undefined),
    language,
    items?.places,
    validationField
  );

  const translationDateLanguage = {
    locale: ternaryCondition(language === 'en', enGB, de),
  };

  let contractRateSummary = contractRateCompany?.name ?? AEMTranslations.companyIdInputPlaceholder;

  if (isNegotiatedRatesByCompIdEnabled) {
    contractRateSummary = contractRateCompany
      ? `${contractRateCompany.name}/${COMPID || CORPID}`
      : `${AEMTranslations.companyNameInputPlaceholder} or ${AEMTranslations.companyIdInputPlaceholder}`;
  }

  const searchSummaryProps = searchSummaryActive
    ? ({
        handleEdit: () => {
          updateSearchEvent({ searchSummaryActive: false });
        },
        isSummaryActive: searchSummaryActive,
        location: defaultLocation,
        dateInterval:
          isValid(startDate) &&
          startDate &&
          endDate &&
          (!isLessThanSm
            ? formatSummaryDateRange('dd MMM', startDate, endDate, translationDateLanguage)
            : formatSummaryDateRange('EEE dd MMM', startDate, endDate, translationDateLanguage)),
        roomSummary: getRoomsPlaceholder(defaultRooms as SearchRoomType[], {
          adult: partialTranslations.content.global.adult,
          adults: partialTranslations.content.global.adults,
          child: partialTranslations.content.global.child,
          children: partialTranslations.content.global.children,
          room: partialTranslations.content.global.room,
          rooms: partialTranslations.content.global.rooms,
        }),
        numberOfNightsSummary: NIGHTS?.toString(),
        promotionCategorySummary: AEMTranslations.promotionCategoryPlaceholder,
        contractRateSummary,
        editText: t('amend.edit'),
        isLessThanSm,
        isLessThanMd,
      } as SearchSummaryProps)
    : ({} as SearchSummaryProps);

  const suggestions = items || initialData;

  const noOfNightsError = t(
    savedNights
      ? `${AEMTranslations.numberOfNightsErrorMessage}`
      : `${dataTranslations?.headerInformation?.form?.invalidNights}`
  );

  return (
    <>
      {!searchSummaryActive ? (
        <SearchComponent
          key={router.asPath}
          locale={language}
          savedNights={savedNights as number}
          onLocationInputChange={debounce(handleLocationInputChange, 300)}
          onLocationInputClear={handleLocationInputClear}
          onInputLocationFocus={handleLocationInputFocus}
          onSelectDates={handleSelectDates}
          noOfNightsError={noOfNightsError}
          suggestions={suggestions}
          roomCodes={swappedRoomCodes}
          errorField={validationField}
          errorMessage={message}
          isSearchActive={searchConsoleIsActive as boolean}
          mappedRoomLabels={swapKeysAndValues(mappedRoomLabels)}
          isDatepickerError={isDatepickerError}
          onOccupancyChange={handleRoomOccupancyChange}
          channel={channel}
          isDatePickerFocus={isDatePickerFocus}
          contractRateCompanyState={[contractRateCompany, setContractRateCompany]}
          isNegotiatedRatesByCompIdEnabled={isNegotiatedRatesByCompIdEnabled}
          isBarrierFreeLabelEnabled={isBarrierFreeLabelEnabled}
          {...{
            searchLocation,
            defaultLocation,
            defaultRooms,
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
            startDate: startDate as SearchBookingDateType,
            endDate: endDate as SearchBookingDateType,
            handleChangeNumberOfNights,
            hideErrorForMinNights,
            displayDatesNotification,
            isActiveMatchedOffer,
            matchedOffer,
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
        updateSearchEvent({ items: data });
        if (validationField === FIELDS.location) {
          updateSearchEvent({ errorCode: [undefined, undefined] });
        }
      });
    } else {
      updateSearchEvent({ items: null });
    }
    updateSearchEvent({ locationInputError: false });
  }
  function handleLocationFromUrl(value: string | undefined) {
    if (value && value.trim().length >= MIN_LENGTH_SEARCH_TERM) {
      getSuggestions(value).then((data) => {
        updateSearchEvent({ items: data });
        if (validationField === FIELDS.location) {
          updateSearchEvent({
            errorCode: [undefined, undefined],
          });
        }
      });
    } else {
      getSuggestions(value as string).then((data) => {
        updateSearchEvent({ items: data });
        if (validationField === FIELDS.location) {
          updateSearchEvent({
            errorCode: [undefined, undefined],
          });
        }
      });
      updateSearchEvent({ locationInputError: true });
    }
    updateSearchEvent({ locationInputError: true });
  }

  function handleLocationInputClear() {
    updateSearchEvent({ items: null });

    if (validationField === FIELDS.location) {
      updateSearchEvent({ errorCode: [undefined, undefined] });
    }
  }

  function handleLocationInputFocus(searchTerm: string | undefined) {
    if (searchTerm?.length === 0) {
      if (validationField === FIELDS.location) {
        updateSearchEvent({ errorCode: [undefined, undefined] });
      }

      updateSearchEvent({ items: null });
    }
  }

  function handleSelectDates(dates: DatepickerSelectionDate) {
    const [start, end] = dates as DatepickerRangeSelectionDate;

    const setNumberOfNights = (
      displayDatesNotification: boolean | undefined,
      startDate: number | Date,
      endDate: number | Date
    ) => {
      const daysDifference: number = differenceInDays(endDate, startDate);
      if (displayDatesNotification && isNaN(daysDifference)) {
        if (!isValid(endDate) && isValid(startDate)) {
          return 1;
        } else {
          return 0;
        }
      }
      return 1;
    };

    updateSearchEvent({ startDate: start, endDate: end });
    updateSearchEvent({
      savedNights: Number(
        end && start
          ? differenceInDays(end, start)
          : setNumberOfNights(displayDatesNotification, start as Date, end as Date)
      ),
    });

    if (validationField === FIELDS.datepicker || validationField === FIELDS.numberOfNights) {
      updateSearchEvent({ errorCode: [undefined, undefined] });
    }
  }

  function handleChangeNumberOfNights(event: React.ChangeEvent<HTMLInputElement>) {
    const numberOfNights = Number(event.target.value);
    updateSearchEvent({ savedNights: numberOfNights });
    if (!startDate) {
      const today = new Date();
      updateSearchEvent({ startDate: today });
      updateSearchEvent({
        endDate:
          numberOfNights > 0 && numberOfNights < 365
            ? add(today, { days: numberOfNights })
            : endDate,
      });
      updateSearchEvent({ errorCode: [undefined, undefined] });

      return;
    }

    updateSearchEvent({
      endDate:
        numberOfNights > 0 && numberOfNights < 365
          ? add(startDate, { days: numberOfNights })
          : endDate,
    });
    if (validationField === FIELDS.datepicker || validationField === FIELDS.numberOfNights) {
      updateSearchEvent({ errorCode: [undefined, undefined] });
    }
  }

  function handleNrOfNightsURLError() {
    if (isNumberOfNightsError && (NIGHTS as number) > 364) {
      updateSearchEvent({ startDate: placeholderDate });
      updateSearchEvent({ endDate: add(startDate as Date, { days: 1 }) });
      updateSearchEvent({ savedNights: NIGHTS });
    } else if (isNumberOfNightsError && (isNaN(NIGHTS as number) || (NIGHTS as number) <= 0)) {
      updateSearchEvent({ startDate: null });
      updateSearchEvent({ endDate: null });
      updateSearchEvent({ savedNights: 0 });
    }
  }

  function handleRoomOccupancyChange() {
    if (validationField === FIELDS.occupancy) {
      updateSearchEvent({ errorCode: [undefined, undefined] });
    }
  }

  async function handleButtonClick(queryParams: SearchRequestParamsType | undefined) {
    if (!queryParams?.searchTerm || !items || validationField === FIELDS.location) {
      updateSearchEvent({ errorCode: [ERROR_KEYS.invalidLocation, ERROR_FIELDS.invalidLocation] });
      setIsSearchError?.(false);
      return;
    }
    if (savedNights === 0 || (savedNights as number) > 364) {
      setIsSearchError?.(false);
      return;
    }
    if (validationField === FIELDS.datepicker && errorCode) {
      switch (errorCode[0]) {
        case ERROR_FIELDS.invalidDate: {
          updateSearchEvent({
            errorCode: [ERROR_KEYS.invalidDate, ERROR_FIELDS.invalidDate],
          });
          break;
        }
        case ERROR_FIELDS.arrivalDateInTheFuture: {
          updateSearchEvent({
            errorCode: [ERROR_KEYS.arrivalDateInTheFuture, ERROR_FIELDS.arrivalDateInTheFuture],
          });
          break;
        }

        case ERROR_FIELDS.arrivalDateInThePast: {
          updateSearchEvent({
            errorCode: [ERROR_KEYS.arrivalDateInThePast, ERROR_FIELDS.arrivalDateInThePast],
          });
          break;
        }
      }
      setIsSearchError?.(false);
      return;
    }

    if (validationField === FIELDS.occupancy) {
      updateSearchEvent({
        errorCode: [ERROR_KEYS.invalidOccupancy, ERROR_FIELDS.invalidOccupancy],
      });
      setIsSearchError?.(false);
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

    setDistanceFromSearch(DISTANCE_FROM_SEARCH_INITIAL_VALUE);

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

    let URLToRedirect = `ARRdd=${paramsMappedForURL.ARRdd}&ARRmm=${paramsMappedForURL.ARRmm}&ARRyyyy=${paramsMappedForURL.ARRyyyy}&NIGHTS=${paramsMappedForURL.nights}&ROOMS=${paramsMappedForURL.roomsNumber}&${roomOccupancyParamsForURL}`;

    if (contractRateCompany) {
      if (isNegotiatedRatesByCompIdEnabled) {
        const companyId =
          contractRateCompany.corpId !== contractRateCompany.searchCriteriaId
            ? `&COMPID=${contractRateCompany.searchCriteriaId}`
            : '';
        URLToRedirect += `&CORPID=${contractRateCompany.corpId}${companyId}`;
      } else {
        URLToRedirect += `&CORPID=${contractRateCompany.corpId}`;
      }
    }

    if (prevReservationId) {
      URLToRedirect += `&reservationId=${prevReservationId}`;
    }

    URLToRedirect = appendPromoIdToUrl(URLToRedirect, PROMOID);

    const singleHotelSearchLabels = {
      bookingChannel: partialTranslations.config.api.bookingChannel.leisure,
      mappedRoomLabels: mappedRoomLabels,
    };

    deleteSessionStorageValues?.();

    window.location = (await singleHotelSearchCCUI(
      stayDetailsState,
      paramsMappedForURL,
      singleHotelSearchLabels,
      URLToRedirect,
      queryParams,
      country,
      language,
      BOOKING_CHANNEL.CCUI
    )) as unknown as Location;
  }
}

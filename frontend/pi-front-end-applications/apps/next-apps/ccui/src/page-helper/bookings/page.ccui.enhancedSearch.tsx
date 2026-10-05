import {
  Container,
  Grid,
  GridItem,
  GridItemProps,
  GridProps,
  Text,
  TextProps,
} from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  SITE_LEISURE,
  SBForm,
  GET_STATIC_CONTENT,
  BOOKING_TYPE,
  SearchBookingsSessionStorage,
  ENHANCED_SEARCH_BOOKINGS_RESULTS,
  IDV_INITIAL_DATA,
  IDV_STATUS_KEY,
  ResultsCcui,
  FT_CCUI_BOOKING_HISTORY_REDESIGN,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
} from '@whitbread-eos/api';
import type { Claims, EnhancedSearchBookingResults, TableRow, IdvStatus } from '@whitbread-eos/api';
import { Form, FormProps, Info, Notification } from '@whitbread-eos/atoms';
import {
  AgentMemo,
  CCUISearchContainer as SearchContainer,
  ResultListContainer,
} from '@whitbread-eos/organisms';
import {
  encodeToBase64,
  formatDataTestId,
  formatDate,
  getSessionStorageValuesForBookings,
  graphQLRequest,
  updateDashboardAnalytics,
  upperOnlyFirst,
  useCustomLocale,
  useLocalStorage,
  useQueryRequest,
  useFeatureToggle,
  formatPriceWithDecimal,
  formatCurrency,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';
import { useTranslation } from 'next-i18next';
import { NextRouter } from 'next/router';
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from 'react';

import { searchBookingsFormConfig } from './ccuiFormConfig/searchBookingsFormConfig';

interface Props {
  user: Claims;
  router: NextRouter;
  queryClient: QueryClient;
  setAnalyticsUser: any;
}

const DEFAULT_PAGE_SIZE = 10;
const DEFAULT_FORM_VALUES = {
  bookingReference: '',
  bookerLastName: '',
  arrivalDate: '',
  guestLastName: '',
  bookerPostcode: '',
  hotelDetails: { name: '', code: '' },
  hotelLocation: '',
  bookerEmail: '',
  bookerPhone: '',
  cancellationDate: '',
  companyName: '',
  thirdPartyBookingReferenceNumber: '',
  extendedSearchCriteria: '',
  hotelId: '',
};

const DEFAULT_RESULTS_DATA: EnhancedSearchBookingResults = {
  searchData: [],
  hasMore: false,
  limitExceeded: false,
  pageNumber: 1,
  searchResults: 0,
  totalResults: 0,
  isLoading: false,
  isSuccess: false,
  isError: false,
  error: null,
};

function getPersistedValue<T>(key: string, fallback: T): T {
  try {
    const item = window.localStorage.getItem(key);

    return item ? (JSON.parse(item) as T) : fallback;
  } catch {
    return fallback;
  }
}

function createEnhancedSearchQueryVariables(
  searchInputs: SBForm,
  pageNumber: number,
  pageSize: number
) {
  const {
    bookingReference,
    bookerLastName,
    arrivalDate,
    guestLastName,
    bookerEmail,
    hotelId,
    bookerPhone,
    bookerPostcode,
    cancellationDate,
    companyName,
    thirdPartyBookingReferenceNumber,
  } = searchInputs;

  let arrivalDateTo = '';

  if (arrivalDate) {
    const arrivalDateObj = new Date(arrivalDate as string);

    arrivalDateTo = formatDate(
      new Date(arrivalDateObj.setDate(arrivalDateObj.getDate() + 1)).toDateString(),
      'yyyy-MM-dd'
    );
  }

  return {
    bookingReference,
    bookerLastName,
    arrivalDateFrom: arrivalDate,
    arrivalDateTo: arrivalDateTo,
    guestLastName,
    bookerEmail,
    hotelId,
    bookerPhone,
    bookerPostcode,
    cancellationDate,
    companyName,
    thirdPartyBookingReferenceNumber,
    pageSize,
    pageNumber,
    bookingsDatabaseSearch: true,
  };
}

export async function getNewSearchResults(
  queryClient: QueryClient,
  searchInputs: SBForm,
  pageNumber: number,
  pageSize: number
) {
  const queryResult = await queryClient.fetchQuery({
    queryKey: ['GetEnhancedSearchBookingsResults'],
    queryFn: () =>
      graphQLRequest(ENHANCED_SEARCH_BOOKINGS_RESULTS, {
        ...createEnhancedSearchQueryVariables(searchInputs, pageNumber, pageSize),
      }) as Promise<any>,

    staleTime: 0,
  });
  const data = queryResult.searchBookingsCcui;

  return { data };
}

export const getBookingStatus = (status: string, t: any) => {
  const formattedStatus = upperOnlyFirst(status);

  if (formattedStatus === BOOKING_TYPE.UPCOMING) {
    return {
      value: formattedStatus,
      label: upperOnlyFirst(t('account.dashboard.booking.upcoming')),
    };
  }

  if (formattedStatus === BOOKING_TYPE.CANCELLED) {
    return {
      value: formattedStatus,
      label: upperOnlyFirst(t('account.dashboard.booking.cancelled')),
    };
  }

  if (formattedStatus === BOOKING_TYPE.PAST) {
    return {
      value: formattedStatus,
      label: upperOnlyFirst(t('account.dashboard.booking.past')),
    };
  }

  if (formattedStatus === BOOKING_TYPE.CHECKED_IN) {
    return {
      value: formattedStatus,
      label: upperOnlyFirst(t('account.dashboard.booking.checkedIn')),
    };
  }

  // Map the status to Past for any other status
  return {
    value: formattedStatus,
    label: upperOnlyFirst(t('account.dashboard.booking.past')),
  };
};

export const mappedResults = (
  data: ResultsCcui[],
  operaConfNumber: string,
  t: any,
  isBookingHistoryRedesignCCUIEnabled?: boolean,
  language?: string
): TableRow[] => {
  return data.map((obj: ResultsCcui): TableRow => {
    const guest = obj.guests?.[0];
    const booker = obj.booker;
    const guestName = guest ? `${guest.title} ${guest.firstName} ${guest.lastName}` : '';
    const bookerName = booker ? `${booker.title} ${booker.firstName} ${booker.lastName}` : '';
    const status = getBookingStatus(obj.status, t);
    const pms = upperOnlyFirst(obj.sourceSystem);
    const hotelId = obj.hotelId ?? '';
    const bookerLastName = booker?.lastName ?? '';

    const defaultCells = [
      { id: 'BookedFor', value: guestName },
      { id: 'BookedBy', value: bookerName },
      {
        id: 'Hotel',
        value: obj.hotelName || '',
      },
      { id: 'Date', value: obj.arrivalDate || '' },
      // this field will be temporarily hidden until correct values can be retrieved
      // { id: 'Price', value: obj.totalCost || '' },
      { id: 'Status', value: status.value || '', label: status.label || '' },
      { id: 'SourcePms', value: pms || '' },
    ];

    const dateLocale = language === 'de' ? de : enGB;

    // Only format dates if redesign is enabled
    const formattedArrivalDate =
      isBookingHistoryRedesignCCUIEnabled && obj?.arrivalDate
        ? format(new Date(obj.arrivalDate), 'EEE, d LLL yy', { locale: dateLocale })
        : obj.arrivalDate || '';

    const formattedDepartureDate =
      isBookingHistoryRedesignCCUIEnabled && obj?.departureDate
        ? format(new Date(obj.departureDate), 'EEE, d LLL yy', { locale: dateLocale })
        : obj.departureDate || '';

    // Dashboard table redesign table cells - feature toggle - isBookingHistoryRedesignCCUIEnabled
    const redesignCells = [
      { id: 'Hotel', value: obj.hotelName || '' },
      {
        id: 'Date',
        value:
          formattedArrivalDate && formattedDepartureDate
            ? `${formattedArrivalDate} - ${formattedDepartureDate}`
            : obj.arrivalDate || '',
      },
      { id: 'BookedFor', value: guestName },
      {
        id: 'Price',
        value:
          typeof obj.totalCost === 'number' && obj.currencyCode
            ? formatPriceWithDecimal(
                language ?? 'en',
                formatCurrency(obj.currencyCode),
                obj.totalCost,
                true
              )
            : '',
      },
      { id: 'BookedBy', value: bookerName },
      { id: 'Status', value: status.value || '', label: status.label || '' },
    ];

    return {
      cells: isBookingHistoryRedesignCCUIEnabled ? redesignCells : defaultCells,
      bookingReference: obj.bookingReference,
      operaConfNumber,
      hotelId,
      bookerLastName,
    };
  });
};

export const resultsWithChangedStatus = (
  searchData: TableRow[],
  bookingReference: string,
  t: any
) => {
  const formattedData = searchData?.map((item: TableRow) => {
    if (item.bookingReference === bookingReference) {
      return {
        ...item,
        cells: [
          ...item.cells.slice(0, 4),
          {
            id: item.cells[4].id,
            value: 'Cancelled',
            label: upperOnlyFirst(t('account.dashboard.booking.cancelled')),
          },
          ...item.cells.slice(5, item.cells.length),
        ],
      };
    }
    return item;
  });
  return formattedData;
};

export const setInputValuesInSessionStorage = (inputs: SBForm, bookingReferenceRPB?: string) => {
  const valuesSessionStorage = {
    bookingReference: inputs.bookingReference,
    bookerLastName: inputs.bookerLastName,
    arrivalDate: inputs?.arrivalDate
      ? formatDate(new Date(inputs.arrivalDate).toDateString(), 'yyyy-MM-dd')
      : '',
    bookingReferenceRPB,
    guestLastName: inputs.guestLastName,
    bookerPostcode: inputs.bookerPostcode,
    hotelDetails: { name: inputs.hotelDetails?.name, code: inputs.hotelDetails?.code },
    hotelLocation: inputs.hotelLocation,
    bookerEmail: inputs.bookerEmail,
    bookerPhone: inputs.bookerPhone,
    cancellationDate: inputs?.cancellationDate
      ? formatDate(new Date(inputs.cancellationDate).toDateString(), 'yyyy-MM-dd')
      : '',
    companyName: inputs.companyName,
    extendedSearchCriteria: inputs.extendedSearchCriteria,
    thirdPartyBookingReferenceNumber: inputs.thirdPartyBookingReferenceNumber,
    hotelId: inputs.hotelDetails?.code,
  };
  if (typeof window !== 'undefined') {
    const encodedValues = encodeToBase64(JSON.stringify(valuesSessionStorage));
    window.sessionStorage.setItem('ccuiPrevSearchCriteria', String(encodedValues));
  }
};

export default function BookingsPageCcui({
  user,
  router,
  queryClient,
  setAnalyticsUser,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const [searchInputs, setSearchInputs] = useState<SBForm>(DEFAULT_FORM_VALUES);
  const [clearHotelLocation, setClearHotelLocation] = useState<boolean>(false);
  const [clearHotelName, setClearHotelName] = useState<boolean>(false);
  const [clearPhoneField, setClearPhoneField] = useState<boolean>(false);
  const [disableBookerSurname, setDisableBookerSurname] = useState(false);
  const [disableGuestSurname, setDisableGuestSurname] = useState(false);
  const INITIAL_BOOKING_REFERENCE = '';
  const INITIAL_SEARCH_BOOKING_PARAMS = {
    bookerLastName: '',
    guestLastName: '',
    arrivalDate: '',
    bookerPostcode: '',
    hotelDetails: { name: '', code: '' },
    hotelLocation: '',
    bookerEmail: '',
    bookerPhone: '',
    cancellationDate: '',
    companyName: '',
    thirdPartyBookingReferenceNumber: '',
  };
  const baseDataTestId = 'SearchBookingsPage';

  const { [FT_CCUI_BOOKING_HISTORY_REDESIGN]: isBookingHistoryRedesignCCUIEnabled } =
    useFeatureToggle();

  const {
    bookingReference,
    bookerLastName,
    arrivalDate,
    bookingReferenceRPB,
    guestLastName,
    hotelDetails,
    bookerPostcode,
    hotelLocation,
    bookerEmail,
    bookerPhone,
    cancellationDate,
    companyName,
    thirdPartyBookingReferenceNumber,
    extendedSearchCriteria,
    hotelId,
  }: SearchBookingsSessionStorage = getSessionStorageValuesForBookings();

  const [searchBookingFormBookingReference, setSearchBookingFormBookingReference] = useLocalStorage(
    'SearchBookingFormBookingReference',
    bookingReference ?? INITIAL_BOOKING_REFERENCE
  );
  const [searchBookingFormAllParams, setSearchBookingFormAllParams] = useLocalStorage(
    'SearchBookingFormAllParams',
    INITIAL_SEARCH_BOOKING_PARAMS
  );

  const [, setIdvStatus] = useLocalStorage<IdvStatus>(IDV_STATUS_KEY, IDV_INITIAL_DATA);

  const [resultsData, setResultsData] =
    useState<EnhancedSearchBookingResults>(DEFAULT_RESULTS_DATA);

  const [isBackFlag] = useLocalStorage('isBackFromChangePaymentPageCCUI', false);

  const isNavigated =
    typeof window !== 'undefined' &&
    (window.performance.getEntriesByType('navigation')[0] as any).type === 'navigate' &&
    !isBackFlag;

  const { [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePIIDataFromLocalStorageEnabled } =
    useFeatureToggle();

  /**
   * New Form Params
   */
  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>(
    isNavigated
      ? DEFAULT_FORM_VALUES
      : {
          bookingReference: bookingReference ?? searchBookingFormBookingReference,
          bookerLastName: bookerLastName ?? searchBookingFormAllParams.bookerLastName,
          arrivalDate:
            (arrivalDate && new Date(arrivalDate)) ||
            (searchBookingFormAllParams?.arrivalDate &&
              new Date(searchBookingFormAllParams?.arrivalDate)) ||
            '',
          guestLastName: guestLastName ?? searchBookingFormAllParams.guestLastName,
          bookerPostcode: bookerPostcode ?? searchBookingFormAllParams.bookerPostcode,
          hotelDetails: hotelDetails ?? {
            name: searchBookingFormAllParams.hotelDetails?.name,
            code: searchBookingFormAllParams.hotelDetails?.code,
          },
          hotelLocation: hotelLocation ?? searchBookingFormAllParams.hotelLocation,
          bookerEmail: bookerEmail ?? searchBookingFormAllParams.bookerEmail,
          bookerPhone: bookerPhone ?? searchBookingFormAllParams.bookerPhone,
          cancellationDate:
            (cancellationDate && new Date(cancellationDate)) ||
            (searchBookingFormAllParams?.cancellationDate &&
              new Date(searchBookingFormAllParams?.cancellationDate)) ||
            '',
          companyName: companyName ?? searchBookingFormAllParams.companyName,
          thirdPartyBookingReferenceNumber:
            thirdPartyBookingReferenceNumber ??
            searchBookingFormAllParams.thirdPartyBookingReferenceNumber,
          extendedSearchCriteria: extendedSearchCriteria ?? '',
          hotelId: hotelId ?? '',
        }
  );

  const { isLoading, isError, data, error } = useQueryRequest(
    ['GetStaticContent', language, country],
    GET_STATIC_CONTENT,
    {
      country,
      language,
      site: SITE_LEISURE,
      businessBooker: false,
    }
  );
  const globalTranslations = data?.headerInformation?.content?.global;

  useEffect(() => {
    if (clearPhoneField) {
      setClearPhoneField(false);
    }
  }, [clearPhoneField]);

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language]);

  useEffect(() => {
    const typeNavigation = (window.performance.getEntriesByType('navigation')[0] as any).type;

    if (sessionStorage.getItem('ccuiPrevSearchCriteria')) {
      getBookingRefSearchedValues(
        { ...defaultValues },
        bookingReferenceRPB as string
        // eslint-disable-next-line no-console
      ).catch((error) =>
        setResultsData({
          ...resultsData,
          error: error,
          isError: true,
        })
      );
    }

    const persistedBookingReference = getPersistedValue('SearchBookingFormBookingReference', '');
    const persistedAllParams = getPersistedValue(
      'SearchBookingFormAllParams',
      INITIAL_SEARCH_BOOKING_PARAMS
    );
    const persistedIsBackFlag = getPersistedValue('isBackFromChangePaymentPageCCUI', false);

    if (persistedBookingReference && (typeNavigation === 'reload' || persistedIsBackFlag)) {
      window.sessionStorage.removeItem('ccuiPrevSearchCriteria');

      getSearchValues(
        1,
        {
          ...searchInputs,
          bookingReference: persistedBookingReference,
          bookerLastName: persistedAllParams.bookerLastName,
          arrivalDate: persistedAllParams?.arrivalDate,
          guestLastName: persistedAllParams.guestLastName,
          bookerPostcode: persistedAllParams.bookerPostcode,
          hotelDetails: persistedAllParams.hotelDetails,
          hotelLocation: persistedAllParams.hotelLocation,
          bookerEmail: persistedAllParams.bookerEmail,
          bookerPhone: persistedAllParams.bookerPhone,
          cancellationDate: persistedAllParams.cancellationDate,
          companyName: persistedAllParams.companyName,
          thirdPartyBookingReferenceNumber: persistedAllParams.thirdPartyBookingReferenceNumber,
        },
        1,
        true
      );
    }
  }, []);

  useEffect(() => {
    setSearchInputs({ ...defaultValues });

    if (sessionStorage.getItem('ccuiPrevSearchCriteria')) {
      setSearchBookingFormBookingReference(bookingReference as string);
      setSearchBookingFormAllParams({
        bookerLastName: bookerLastName ?? '',
        guestLastName: guestLastName ?? '',
        arrivalDate: arrivalDate ?? '',
        bookerPostcode: bookerPostcode ?? '',
        hotelDetails: { name: hotelDetails ?? '', code: hotelDetails ?? '' },
        hotelLocation: hotelLocation ?? '',
        bookerEmail: bookerEmail ?? '',
        bookerPhone: bookerPhone ?? '',
        cancellationDate: cancellationDate ?? '',
        companyName: companyName ?? '',
        thirdPartyBookingReferenceNumber: thirdPartyBookingReferenceNumber ?? '',
      });
    }
  }, [searchBookingFormBookingReference]);

  const onSubmit = (inputs: SBForm) => {
    setIdvStatus(IDV_INITIAL_DATA);
    let submittedInputs = { ...inputs };

    // format arrivalDate into correct format
    if (submittedInputs?.arrivalDate && submittedInputs?.arrivalDate?.length > 0) {
      const arrival = new Date(submittedInputs.arrivalDate);
      const formatedDate = formatDate(arrival.toDateString(), 'yyyy-MM-dd');
      submittedInputs = { ...submittedInputs, arrivalDate: formatedDate };
    }
    // format cancellationDate into correct format
    if (submittedInputs?.cancellationDate && submittedInputs?.cancellationDate?.length > 0) {
      const cancellationDate = new Date(submittedInputs.cancellationDate);
      const formatedDate = formatDate(cancellationDate.toDateString(), 'yyyy-MM-dd');
      submittedInputs = { ...submittedInputs, cancellationDate: formatedDate };
    }
    // send only hotelId and remove hotelDetails
    if (submittedInputs?.hotelDetails && submittedInputs?.hotelDetails?.name.length > 0) {
      const code = submittedInputs.hotelDetails.code;
      submittedInputs = { ...submittedInputs, hotelId: code };
    }

    setSearchInputs(submittedInputs);
    saveFormDataInLocalStorage(inputs);
    setResultsData(() => ({
      ...resultsData,
      isLoading: true,
      isError: false,
      searchData: [],
    }));
    getSearchValues(1, submittedInputs);
    window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
  };

  const changePage = (pageNumber: number) => {
    window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
    setResultsData(() => ({
      ...resultsData,
      isLoading: true,
    }));

    getSearchValues(pageNumber, searchInputs);
  };

  const onReset = () => {
    if (typeof window !== 'undefined') {
      window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
    }
    setResultsData(DEFAULT_RESULTS_DATA);
    setSearchInputs(DEFAULT_FORM_VALUES);
    setDefaultValues(DEFAULT_FORM_VALUES);
    setSearchBookingFormBookingReference(INITIAL_BOOKING_REFERENCE);
    setSearchBookingFormAllParams(INITIAL_SEARCH_BOOKING_PARAMS);
  };

  const deleteSessionStorageValues = () => {
    if (typeof window !== 'undefined') {
      window.sessionStorage.removeItem('ccuiPrevSearchCriteria');

      setSearchBookingFormBookingReference(INITIAL_BOOKING_REFERENCE);
      setSearchBookingFormAllParams(INITIAL_SEARCH_BOOKING_PARAMS);
    }
  };

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );

  const updateStatusAfterCancel = (bookingReference: string) => {
    const formattedResultsWithChangedStatus = resultsWithChangedStatus(
      resultsData.searchData,
      bookingReference,
      t
    );
    setResultsData({
      ...resultsData,
      searchData: formattedResultsWithChangedStatus,
    });
  };

  const onChange = (values: SBForm) => {
    const shouldDisable = (value: string | undefined) => (value ? value.length > 0 : false);
    const disableBookerSurnameField = shouldDisable(values?.guestLastName);
    const disableGuestSurnameField = shouldDisable(values?.bookerLastName);

    let inputsSearched = { ...values };
    if (values?.arrivalDate) {
      const arrival = new Date(values.arrivalDate);
      const formatedDate = formatDate(arrival.toDateString(), 'yyyy-MM-dd');
      inputsSearched = { ...inputsSearched, arrivalDate: formatedDate };
    }

    if (values?.cancellationDate) {
      const cancellationDate = new Date(values.cancellationDate);
      const formatedDate = formatDate(cancellationDate.toDateString(), 'yyyy-MM-dd');
      inputsSearched = { ...inputsSearched, cancellationDate: formatedDate };
    }
    if (values?.hotelDetails) {
      const code = values.hotelDetails.code;
      inputsSearched = { ...values, hotelId: code };
    }

    setSearchInputs(inputsSearched);

    const updateFieldState = (
      shouldDisable: boolean,
      currentValue: boolean,
      setter: Dispatch<SetStateAction<boolean>>
    ) => {
      if (shouldDisable !== currentValue) {
        setter(shouldDisable);
      }
    };

    updateFieldState(disableBookerSurnameField, disableBookerSurname, setDisableBookerSurname);
    updateFieldState(disableGuestSurnameField, disableGuestSurname, setDisableGuestSurname);
  };

  // TO DO: Change the loading label, when search bookings BE is ready
  if (isLoading) {
    return <Text data-testid="loading-message">{t('searchresults.list.hotel.loading')}</Text>;
  }

  if (isError) {
    return <Text>{(error as Error).message}</Text>;
  }

  return (
    <Container maxW="100%">
      <Grid {...gridStyles} data-testid={formatDataTestId(baseDataTestId, 'Wrapper')}>
        <GridItem
          {...notificationStyles}
          data-testid={formatDataTestId(baseDataTestId, 'Notification')}
        >
          <Notification
            maxWidth="full"
            variant="info"
            status="info"
            title={t('ccui.manageBooking.notification.title')}
            description={t('ccui.manageBooking.notification.description')}
            svg={<Info />}
          />
        </GridItem>
        <GridItem mb="5xl" data-testid={formatDataTestId(baseDataTestId, 'Search')}>
          <SearchContainer
            queryClient={queryClient}
            searchLocation={searchLocation?.toString()}
            ARRdd={Number(ARRdd)}
            ARRmm={Number(ARRmm)}
            ARRyyyy={Number(ARRyyyy)}
            NIGHTS={Number(NIGHTS)}
            ROOMS={Number(ROOMS)}
            isSummaryActive={false}
            deleteSessionStorageValues={() => deleteSessionStorageValues()}
          />
        </GridItem>
        <GridItem>
          <Text {...mainHeaderStyles} data-testid={formatDataTestId(baseDataTestId, 'Header')}>
            {t('ccui.manageBooking.title')}
          </Text>
          <Text
            {...descriptionStyles}
            data-testid={formatDataTestId(baseDataTestId, 'Description')}
          >
            {t('ccui.manageBooking.enhancedSearchDescription')}
          </Text>
        </GridItem>

        <GridItem data-testid={formatDataTestId(baseDataTestId, 'SearchFormWrapper')}>
          <Form
            onChange={onChange}
            {...searchBookingsFormConfig({
              getFormState,
              defaultValues,
              onSubmit,
              onReset,
              baseDataTestId,
              t,
              language,
              setClearPhoneField,
              clearPhoneField,
              disableGuestSurname,
              disableBookerSurname,
              singleDatePickerLabels: {
                todayLabel: globalTranslations.today,
                tomorrowLabel: globalTranslations.tomorrow,
              },
              clearHotelFields: {
                clearHotelLocation,
                clearHotelName,
                setClearHotelLocation,
                setClearHotelName,
              },
              enhancedSearch: true,
            })}
          ></Form>
        </GridItem>
        <GridItem
          {...resultListWrapperStyle}
          data-testid={formatDataTestId(baseDataTestId, 'ResultListContainer')}
        >
          <ResultListContainer
            baseDataTestId={baseDataTestId}
            inputValues={searchInputs}
            resultsData={resultsData}
            changePage={changePage}
            t={t}
            updateStatusAfterCancel={updateStatusAfterCancel}
            setInputValuesInSessionStorage={(
              searchInputs: SBForm,
              bookingReferenceRPB?: string
            ) => {
              setInputValuesInSessionStorage(searchInputs, bookingReferenceRPB);
            }}
            enhancedSearch
            isBookingHistoryRedesignCCUIEnabled={isBookingHistoryRedesignCCUIEnabled}
            isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
          />
        </GridItem>
      </Grid>
      <AgentMemo />
    </Container>
  );

  function saveFormDataInLocalStorage(formData: SBForm) {
    setSearchBookingFormBookingReference(formData?.bookingReference ?? '');
    setSearchBookingFormAllParams({
      bookerLastName: formData?.bookerLastName ?? '',
      guestLastName: formData?.guestLastName ?? '',
      arrivalDate: formData?.arrivalDate
        ? formatDate(new Date(formData.arrivalDate).toDateString(), 'yyyy-MM-dd')
        : '',
      bookerPostcode: formData?.bookerPostcode ?? '',
      hotelDetails: {
        name: formData?.hotelDetails?.name ?? '',
        code: formData?.hotelDetails?.code ?? '',
      },
      hotelLocation: formData?.hotelLocation ?? '',
      bookerEmail: formData?.bookerEmail ?? '',
      bookerPhone: formData?.bookerPhone ?? '',
      cancellationDate: formData?.cancellationDate
        ? formatDate(new Date(formData?.cancellationDate).toDateString(), 'yyyy-MM-dd')
        : '',
      companyName: formData?.companyName ?? '',
      thirdPartyBookingReferenceNumber: formData?.thirdPartyBookingReferenceNumber ?? '',
    });
  }

  function getSearchValues(
    pageNumber: number,
    inputs: SBForm,
    pageSize?: number,
    resetSearch = false
  ) {
    getNewSearchResults(queryClient, { ...inputs }, pageNumber, pageSize ?? DEFAULT_PAGE_SIZE)
      .then(({ data }) => {
        if (data?.results?.map && data.pageResults) {
          updateDashboardAnalytics({
            bookings: data.results,
            totalResults: data.pageResults,
            inputs,
            newSearch: pageNumber === 1,
          });
        }

        const newSearchData =
          data.results.length > 0
            ? mappedResults(
                data.results,
                data.operaConfNumber,
                t,
                isBookingHistoryRedesignCCUIEnabled,
                language
              )
            : [];

        setResultsData(() => ({
          ...resultsData,
          searchData:
            pageNumber === 1 || resetSearch
              ? newSearchData
              : [...resultsData.searchData, ...newSearchData],
          hasMore: data.hasMore,
          limitExceeded: data.responseLimitExceeded,
          pageNumber,
          searchResults: resultsData.searchResults + data.pageResults,
          totalResults: data.searchResults,
          isLoading: false,
          isSuccess: true,
          isError: false,
          error: null,
        }));
      })
      .catch((error) => {
        setResultsData((resultsData) => ({
          ...resultsData,
          isLoading: false,
          isSuccess: false,
          isError: true,
          error: error,
        }));
      });
  }

  async function getBookingRefSearchedValues(inputs: SBForm, bookingRefSearched: string) {
    let pageNumber = 1;
    let newSearchDataArray: TableRow[] = [];
    let bookingRefFound = false;
    let hasMore = true;

    while (!bookingRefFound && hasMore) {
      try {
        const { data } = await getNewSearchResults(
          queryClient,
          { ...inputs },
          pageNumber,
          DEFAULT_PAGE_SIZE
        );
        const newSearchData =
          data.results.length > 0
            ? mappedResults(
                data.results,
                data.operaConfNumber,
                t,
                isBookingHistoryRedesignCCUIEnabled,
                language
              )
            : [];

        newSearchData.forEach((element) => {
          if (element.bookingReference === bookingRefSearched) {
            bookingRefFound = true;
            element.isExpandedByDefault = true;
          }
        });

        newSearchDataArray = newSearchDataArray.concat(newSearchData);
        hasMore = data.hasMore;
        pageNumber++;
      } catch (error) {
        setResultsData((resultsData) => ({
          ...resultsData,
          limitExceeded: false,
          isLoading: false,
          isSuccess: false,
          isError: true,
          error: error,
        }));
        hasMore = false;
      }
    }

    setResultsData((resultsData) => ({
      ...resultsData,
      searchData: newSearchDataArray,
      pageNumber,
      hasMore: hasMore,
      isLoading: false,
      isSuccess: true,
      isError: false,
    }));
  }
}

const gridStyles = {
  w: 'full',
  maxW: 'var(--chakra-space-breakpoint-xl)',
  px: {
    mobile: '0',
    lg: 'lg',
    xl: '5xl',
  },
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
  mx: 'auto',
  my: '0',
} as GridProps;

const notificationStyles = {
  mt: '2xl',
  mb: 'lg',
} as GridItemProps;

const mainHeaderStyles = {
  fontSize: '3xxl',
  fontWeight: 'semibold',
  lineHeight: '5',
  textAlign: 'left',
  color: 'baseBlack',
  mb: 'md',
} as TextProps;

const resultListWrapperStyle = {
  px: {
    mobile: 'md',
    md: 'lg',
    lg: '0',
  },
  pt: {
    mobile: 'lg',
    md: '2xl',
    lg: '0',
  },
} as GridItemProps;

const descriptionStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
  textAlign: 'left',
  color: 'darkGrey1',
  mb: 'md',
} as TextProps;

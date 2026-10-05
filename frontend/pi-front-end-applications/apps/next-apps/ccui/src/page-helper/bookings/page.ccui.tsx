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
import type { TableRow } from '@whitbread-eos/api';
import {
  BOOKING_TYPE,
  BookingResults,
  Claims,
  FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE,
  GET_HOTEL_INFORMATION,
  GET_STATIC_CONTENT,
  SBForm,
  SEARCH_BOOKINGS_RESULTS,
  SearchBooking,
  SearchBookingsSessionStorage,
  SITE_LEISURE,
} from '@whitbread-eos/api';
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
  logicalOrOperator,
  updateDashboardAnalytics,
  upperOnlyFirst,
  useCustomLocale,
  useFeatureToggle,
  useLocalStorage,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { NextRouter } from 'next/router';
import { Dispatch, SetStateAction, useCallback, useEffect, useState } from 'react';

import { searchBookingsFormConfig } from './ccuiFormConfig/searchBookingsFormConfig';

//<editor-fold desc="Imports" defaultstate="collapsed">

//</editor-fold>

interface Props {
  user: Claims;
  router: NextRouter;
  queryClient: QueryClient;
  setAnalyticsUser: any;
}

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

async function getNewSearchResults(
  queryClient: QueryClient,
  inputValues: SBForm,
  limit: number,
  offset: number
) {
  const queryResult = await queryClient.fetchQuery({
    queryKey: [
      'GetSearchBookingsResults',
      inputValues.bookingReference,
      inputValues.bookerLastName,
      inputValues.arrivalDate,
      inputValues.guestLastName,
      inputValues.bookerPostcode,
      inputValues.hotelId,
      inputValues.hotelLocation,
      inputValues.bookerEmail,
      inputValues.bookerPhone,
      inputValues.cancellationDate,
      inputValues.companyName,
      inputValues.thirdPartyBookingReferenceNumber,
      limit,
      offset,
    ],
    queryFn: () =>
      graphQLRequest(SEARCH_BOOKINGS_RESULTS, {
        ...inputValues,
        limit: limit,
        offset: offset,
      }) as Promise<any>,

    staleTime: 0,
  });

  const results = queryResult.searchBookings;
  return { results };
}

async function getHotelInformation(
  queryClient: any,
  bartId: any,
  country: string,
  language: string
) {
  const queryResult = await queryClient.fetchQuery(
    ['GetHotelInformation', bartId, country, language],
    () =>
      graphQLRequest(GET_HOTEL_INFORMATION, {
        hotelId: bartId,
        country: country,
        language: language,
      })
  );
  const name = queryResult.hotelInformation.name;
  return { name };
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

  return {
    value: formattedStatus,
    label: upperOnlyFirst(t('account.dashboard.booking.past')),
  };
};

export const mapedResults = (data: SearchBooking[], t: any): TableRow[] => {
  return data.map((obj: SearchBooking): TableRow => {
    const guest = obj.stayingGuests[0];
    const booker = obj?.booker;
    const guestName = `${guest?.title} ${guest?.firstName} ${guest?.lastName}`;
    const bookerName = booker ? `${booker?.title} ${booker?.firstName} ${booker?.lastName}` : '';
    const status = getBookingStatus(obj.status, t);
    const pms = upperOnlyFirst(obj.sourcePms);

    return {
      cells: [
        { id: 'BookedFor', value: guestName ?? '' },
        { id: 'BookedBy', value: bookerName ?? '' },
        {
          id: 'Hotel',
          value: obj.hotelName ?? '',
        },
        { id: 'Date', value: obj.arrivalDate ?? '' },
        // this field will be temporarily hidden until correct values can be retrieved
        // { id: 'Price', value: obj.totalCost || '' },
        { id: 'Status', value: status.value ?? '', label: status.label ?? '' },
        { id: 'SourcePms', value: pms ?? '' },
      ],
      bookingReference: obj.bookingReference,
    };
  });
};

export const setInputValuesInSessionStorage = (inputs: SBForm, bookingReferenceRPB?: string) => {
  const valuesSessionStorage = {
    bookingReference: inputs.bookingReference,
    bookerLastName: inputs.bookerLastName,
    arrivalDate: inputs?.arrivalDate ? formatDate(inputs?.arrivalDate, 'yyyy-MM-dd') : '',
    bookingReferenceRPB,
    guestLastName: inputs.guestLastName,
    bookerPostcode: inputs.bookerPostcode,
    hotelDetails: { name: inputs.hotelDetails?.name, code: inputs.hotelDetails?.code },
    hotelLocation: inputs.hotelLocation,
    bookerEmail: inputs.bookerEmail,
    bookerPhone: inputs.bookerPhone,
    cancellationDate: inputs?.cancellationDate
      ? formatDate(inputs?.cancellationDate, 'yyyy-MM-dd')
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

export default function BookingsPageCcui({
  user,
  setAnalyticsUser,
  router,
  queryClient,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const { country, language } = useCustomLocale();
  const { searchLocation, ARRdd, ARRmm, ARRyyyy, NIGHTS, ROOMS } = router.query;
  const [searchInputs, setSearchInputs] = useState<SBForm>({});
  const [bartHotelName, setBartHotelName] = useState('');

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
    logicalOrOperator(bookingReference, INITIAL_BOOKING_REFERENCE)
  );
  const [clearHotelLocation, setClearHotelLocation] = useState<boolean>(false);
  const [clearHotelName, setClearHotelName] = useState<boolean>(false);
  const [clearPhoneField, setClearPhoneField] = useState<boolean>(false);

  const [resultsData, setResultsData] = useState({
    hasMore: false,
    bartId: null,
    bartName: '',
    offset: 0,
    searchData: [],
    limitExceeded: false,
    isLoading: false,
    isSuccess: false,
    isError: false,
    error: null,
  } as BookingResults);

  const onSubmit = (inputs: SBForm) => {
    let submittedInputs = { ...inputs };

    // format arrivalDate into correct format
    if (inputs?.arrivalDate && inputs.arrivalDate.length > 0) {
      const formatedDate = formatDate(inputs.arrivalDate, 'yyyy-MM-dd');
      submittedInputs = { ...submittedInputs, arrivalDate: formatedDate };
    }
    // format cancellationDate into correct format
    if (inputs?.cancellationDate && inputs.cancellationDate.length > 0) {
      const formatedDate = formatDate(inputs.cancellationDate, 'yyyy-MM-dd');
      submittedInputs = { ...submittedInputs, cancellationDate: formatedDate };
    }
    // send only hotelId and remove hotelDetails
    if (inputs?.hotelDetails && inputs.hotelDetails.name?.length > 0) {
      const code = inputs.hotelDetails.code;
      submittedInputs = { ...inputs, hotelId: code };
    }

    setSearchInputs(submittedInputs);
    saveFormDataInLocalStorage(inputs);
    setResultsData(() => ({
      ...resultsData,
      isLoading: true,
      searchData: [],
    }));
    getSearchValues(0, submittedInputs);

    window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
  };

  const changePage = (offset: number) => {
    window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
    setResultsData(() => ({
      ...resultsData,
      isLoading: true,
    }));

    getSearchValues(offset, searchInputs);
  };

  const onReset = () => {
    if (typeof window !== 'undefined') {
      window.sessionStorage.removeItem('ccuiPrevSearchCriteria');
    }
    setResultsData(() => ({
      ...resultsData,
      hasMore: false,
      bartId: null,
      bartName: '',
      offset: 0,
      searchData: [],
      limitExceeded: false,
      isLoading: false,
      isSuccess: false,
      isError: false,
      error: null,
    }));
    setSearchInputs({ ...searchInputs, bookingReference: '', bookerLastName: '', arrivalDate: '' });
    setDefaultValues({
      ...defaultValues,
      bookingReference: '',
      bookerLastName: '',
      arrivalDate: '',
    });
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

  useEffect(() => {
    if (clearPhoneField) {
      setClearPhoneField(false);
    }
  }, [clearPhoneField]);

  const { [FT_CCUI_REMOVE_PII_DATA_FROM_LOCAL_STORAGE]: isRemovePIIDataFromLocalStorageEnabled } =
    useFeatureToggle();

  const isNavigated =
    typeof window !== 'undefined' &&
    (window.performance.getEntriesByType('navigation')[0] as any).type === 'navigate';

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

  const [disableBookerSurname, setDisableBookerSurname] = useState(false);
  const [disableGuestSurname, setDisableGuestSurname] = useState(false);

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );
  const baseDataTestId = 'SearchBookingsPage';

  useEffect(() => {
    setAnalyticsUser(user, language);
  }, [user, language]);

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

    if (searchBookingFormBookingReference && typeNavigation === 'reload') {
      window.sessionStorage.removeItem('ccuiPrevSearchCriteria');

      getSearchValues(
        0,
        {
          ...searchInputs,
          bookingReference: searchBookingFormBookingReference,
          bookerLastName: searchBookingFormAllParams.bookerLastName,
          arrivalDate: searchBookingFormAllParams?.arrivalDate,
          guestLastName: searchBookingFormAllParams.guestLastName,
          bookerPostcode: searchBookingFormAllParams.bookerPostcode,
          hotelDetails: searchBookingFormAllParams.hotelDetails,
          hotelLocation: searchBookingFormAllParams.hotelLocation,
          bookerEmail: searchBookingFormAllParams.bookerEmail,
          bookerPhone: searchBookingFormAllParams.bookerPhone,
          cancellationDate: searchBookingFormAllParams.cancellationDate,
          companyName: searchBookingFormAllParams.companyName,
          thirdPartyBookingReferenceNumber:
            searchBookingFormAllParams.thirdPartyBookingReferenceNumber,
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

  const onChange = (values: SBForm) => {
    const shouldDisable = (value: string | undefined) => (value ? value.length > 0 : false);
    const disableBookerSurnameField = shouldDisable(values?.guestLastName);
    const disableGuestSurnameField = shouldDisable(values?.bookerLastName);

    let inputsSearched = { ...values };

    if (values?.arrivalDate) {
      const formatedDate = formatDate(values.arrivalDate, 'yyyy-MM-dd');
      inputsSearched = { ...inputsSearched, arrivalDate: formatedDate };
    }

    if (values?.cancellationDate) {
      const formatedDate = formatDate(values.cancellationDate, 'yyyy-MM-dd');
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
            deleteSessionStorageValues={deleteSessionStorageValues}
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
            {t('ccui.manageBooking.description')}
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
            bartHotelName={bartHotelName}
            t={t}
            updateStatusAfterCancel={updateStatusAfterCancel}
            setInputValuesInSessionStorage={(
              searchInputs: SBForm,
              bookingReferenceRPB?: string
            ) => {
              setInputValuesInSessionStorage(searchInputs, bookingReferenceRPB);
            }}
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
      arrivalDate: formData?.arrivalDate ? formatDate(formData?.arrivalDate, 'yyyy-MM-dd') : '',
      bookerPostcode: formData?.bookerPostcode ?? '',
      hotelDetails: {
        name: formData?.hotelDetails?.name ?? '',
        code: formData?.hotelDetails?.code ?? '',
      },
      hotelLocation: formData?.hotelLocation ?? '',
      bookerEmail: formData?.bookerEmail ?? '',
      bookerPhone: formData?.bookerPhone ?? '',
      cancellationDate: formData?.cancellationDate
        ? formatDate(formData?.cancellationDate, 'yyyy-MM-dd')
        : '',
      companyName: formData?.companyName ?? '',
      thirdPartyBookingReferenceNumber: formData?.thirdPartyBookingReferenceNumber ?? '',
    });
  }

  function getHotelInformationBart(results: any, inputs: SBForm, offset: number) {
    if (results.bookings.length > 0 && results.bookings[0]?.sourcePms === 'BART') {
      getHotelInformation(queryClient, results.bookings[0].hotelId, country, language).then(
        ({ name }) => {
          setBartHotelName(name);
        }
      );
    }
    if (results?.bookings?.map && results.totalResults) {
      updateDashboardAnalytics({
        bookings: results.bookings,
        totalResults: results.totalResults,
        inputs,
        newSearch: offset === 0,
      });
    }

    return results.bookings.length > 0 ? mapedResults(results.bookings, t) : [];
  }

  function getSearchValues(offset: number, inputs: any, limit?: number, resetSearch = false) {
    getNewSearchResults(queryClient, { ...inputs }, limit ?? 10, offset)
      .then(({ results }) => {
        const data = getHotelInformationBart(results, inputs, offset);

        setResultsData(() => ({
          ...resultsData,
          hasMore: results.hasMore,
          offset: results.offset,
          bartId:
            results.bookings.length > 0 && results.bookings[0].sourcePms === 'BART'
              ? results.bookings[0].hotelId
              : null,
          searchData:
            results.offset === 10 || resetSearch || results.bookings.length === 0
              ? data
              : [...resultsData.searchData, ...data],
          limitExceeded: results.responseLimitExceeded,
          isLoading: false,
          isSuccess: true,
          isError: false,
          error: null,
        }));
      })
      .catch((error) => {
        setResultsData((resultsData: any) => ({
          ...resultsData,
          limitExceeded: false,
          isLoading: false,
          isSuccess: false,
          isError: true,
          error: error,
        }));
      });
  }

  async function getBookingRefSearchedValues(inputs: any, bookingRefSearched: string) {
    let i = 0;
    let newSearchDataArray: TableRow[] = [];
    let bookingRefFound = false;
    let hasMore = true;

    while (!bookingRefFound && hasMore) {
      try {
        const { results } = await getNewSearchResults(queryClient, { ...inputs }, 10, i * 10);

        const data = getHotelInformationBart(results, inputs, i * 10);
        data.forEach((element) => {
          if (element.bookingReference === bookingRefSearched) {
            bookingRefFound = true;
            element.isExpandedByDefault = true;
          }
        });

        newSearchDataArray = newSearchDataArray.concat(data);
        hasMore = results.hasMore;
        i++;
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
      offset: i * 10,
      hasMore: hasMore,
      isLoading: false,
      isSuccess: true,
      isError: false,
    }));
  }
}
//<editor-fold desc="Styles" defaultstate="collapsed">
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
//</editor-fold>

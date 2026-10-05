import { useQueryClient } from '@tanstack/react-query';
import type { BookingResults, SBForm } from '@whitbread-eos/api';
import { Area, EnhancedSearchBookingResults } from '@whitbread-eos/api';
import { ErrorBoundary } from '@whitbread-eos/atoms';
import { ResultList } from '@whitbread-eos/molecules';
import { useCustomLocale } from '@whitbread-eos/utils';

import { BookingInfoCard } from '../../account';
import BartBookingInformationCard from '../BartCard/BartBookingInformationCard.container';

export interface Props {
  inputValues: SBForm;
  t: (id: string) => string;
  baseDataTestId: string;
  resultsData: BookingResults | EnhancedSearchBookingResults;
  changePage: (nextPage: number) => void;
  bartHotelName?: string;
  updateStatusAfterCancel?: (basketReference: string) => void;
  setInputValuesInSessionStorage?: (inputValues: SBForm, bookingReferenceRPB?: string) => void;
  enhancedSearch?: boolean;
  isBookingHistoryRedesignCCUIEnabled?: boolean;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

type SearchBookingsCardProps = {
  [key: string]: any; // 👈️ variable key
  bookingReference: string;
  basketReference: string | null;
  area: Area;
  sourcePms: string;
};

type BartProps = {
  bookerLastName?: string;
  arrivalDate?: string;
  bartId?: string;
  bookingReference: string;
  sourcePms: string;
  t: (id: string) => string;
};

export default function ResultsListContainer({
  baseDataTestId,
  inputValues,
  t,
  resultsData,
  changePage,
  bartHotelName = '',
  updateStatusAfterCancel,
  setInputValuesInSessionStorage,
  enhancedSearch = false,
  isBookingHistoryRedesignCCUIEnabled,
  isRemovePIIDataFromLocalStorageEnabled = false,
}: Readonly<Props>) {
  const queryClient = useQueryClient();
  const { language } = useCustomLocale();

  const headerTitles = [
    {
      id: 'BookedFor',
      text: t('ccui.manageBooking.resultList.bookedFor'),
    },
    {
      id: 'BookedBy',
      text: t('ccui.manageBooking.resultList.bookedBy'),
    },
    {
      id: 'Hotel',
      text: t('ccui.manageBooking.resultList.hotel'),
    },
    {
      id: 'Date',
      text: t('ccui.manageBooking.resultList.date'),
    },
    // this column will be temporarily hidden untill correct values can be retrieved
    // {
    //   id: 'Price',
    //   text: t('ccui.manageBooking.resultList.price'),
    // },
    {
      id: 'Status',
      text: t('ccui.manageBooking.resultList.status'),
    },
    {
      id: 'SourcePms',
      text: t('ccui.manageBooking.resultList.sourcePms'),
    },
    {
      id: 'Expand',
      text: '',
    },
  ];

  const headerTitlesRedesign = [
    {
      id: 'Hotel',
      text: t('ccui.manageBooking.resultList.hotel'),
    },
    {
      id: 'Date',
      text: t('ccui.manageBooking.resultList.date'),
    },
    {
      id: 'BookedFor',
      text: t('ccui.manageBooking.resultList.bookedFor'),
    },
    {
      id: 'Price',
      text: t('ccui.manageBooking.resultList.price'),
    },
    {
      id: 'BookedBy',
      text: t('ccui.manageBooking.resultList.bookedBy'),
    },
    {
      id: 'Status',
      text: t('ccui.manageBooking.resultList.status'),
    },
  ];

  const tableColHeadings = isBookingHistoryRedesignCCUIEnabled
    ? headerTitlesRedesign
    : headerTitles;

  const operaConfNumber = resultsData?.searchData[0]?.operaConfNumber;

  const bartCard = (props: BartProps) => {
    return <BartBookingInformationCard {...props} />;
  };

  const operaCard = (props: SearchBookingsCardProps) => {
    return (
      <BookingInfoCard
        {...props}
        operaConfNumber={operaConfNumber}
        updateStatusAfterCancel={updateStatusAfterCancel}
        inputValues={inputValues}
        setInputValuesInSessionStorage={(inputValues: SBForm, bookingReferenceRPB?: string) => {
          setInputValuesInSessionStorage?.(inputValues, bookingReferenceRPB);
        }}
        isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
      />
    );
  };

  const bartId = (resultsData as BookingResults).bartId
    ? (resultsData as BookingResults).bartId
    : null;

  const isCancelledError = resultsData?.error && 'silent' in resultsData.error;
  const loadingResultsError = resultsData?.isError && !isCancelledError;

  const onPageChanged = () => {
    enhancedSearch
      ? changePage((resultsData as EnhancedSearchBookingResults).pageNumber + 1)
      : changePage((resultsData as BookingResults).offset);
  };

  return (
    <ErrorBoundary>
      <ResultList
        queryClient={queryClient}
        loading={resultsData.isLoading}
        isSuccess={resultsData.isSuccess}
        baseDataTestId={baseDataTestId}
        // table rows
        rows={resultsData.searchData}
        t={t}
        // table titles
        headerTitles={tableColHeadings}
        bartHotelName={bartHotelName}
        bartCard={bartCard}
        operaCard={operaCard}
        bookerLastName={inputValues.bookerLastName}
        arrivalDate={inputValues.arrivalDate}
        bartId={bartId}
        error={resultsData.error}
        isError={loadingResultsError}
        hasMore={resultsData.hasMore}
        limitExceeded={resultsData.limitExceeded}
        changePage={onPageChanged}
        language={language}
        updateStatusAfterCancel={updateStatusAfterCancel}
        enhancedSearch={enhancedSearch}
        isBookingHistoryRedesignCCUIEnabled={isBookingHistoryRedesignCCUIEnabled}
      />
    </ErrorBoundary>
  );
}

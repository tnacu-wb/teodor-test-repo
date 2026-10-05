import { Box, StyleProps, TableContainerProps, Text } from '@chakra-ui/react';
import {
  Area,
  BOOKING_TYPE,
  BookingHistoryRequest,
  ScreenSizeValues,
  SOURCE_SYSTEM,
  BookingHistory,
  Booking,
  Query,
  BookingChannelCriteria,
  PageName,
} from '@whitbread-eos/api';
import {
  Info,
  Notification,
  TableList,
  TableListColumn,
  TableListRow,
  TextStat,
  TextStats,
} from '@whitbread-eos/atoms';
import { BookingHistoryFilter, BookingHistoryFilterParams } from '@whitbread-eos/molecules';
import { analytics } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';

import { BookingHistoryInfoCardContainer } from '../BookingInfoCard';

export interface Props {
  tableConfig: TableListColumn[];
  screenSize: ScreenSizeValues;
  pageSize: number;
  onFetch: (params: BookingHistoryRequest) => Promise<Query>;
  bookingChannel: BookingChannelCriteria;
  area?: Area;
  rowStyles?: StyleProps;
  isBookingHistoryRedesignPIAndBBEnabled?: boolean;
}

export default function BookingsHistory({
  tableConfig,
  screenSize,
  pageSize,
  onFetch,
  bookingChannel,
  area = Area.PI,
  rowStyles,
  isBookingHistoryRedesignPIAndBBEnabled,
}: Readonly<Props>) {
  const { t } = useTranslation();
  const [isLoading, setIsLoading] = useState(false);
  const [isError, setIsError] = useState(false);
  const [bookingsData, setBookingsData] = useState<BookingHistory | null>(null);
  const [bookingHasBeenCanceled, setBookingHasBeenCanceled] = useState(false);
  const [params, setParams] = useState<BookingHistoryRequest>({
    pageIndex: 1,
    filterType: '',
    filterValue: '',
    continuationToken: undefined,
  });
  const [continuationToken, setContinuationToken] = useState<string | undefined>(undefined);
  const [invoiceItemsSent, setInvoiceItemsSent] = useState<Array<string>>([]);

  const getBookingHistory = async () => {
    setIsLoading(true);

    try {
      const data = await onFetch(params);
      if (data) {
        const currentBookings: Booking[] =
          params?.pageIndex && params?.pageIndex > 1 && bookingsData?.bookings?.length
            ? bookingsData.bookings
            : [];
        const dataBookings = data?.bookingHistory?.bookings?.length
          ? data?.bookingHistory?.bookings
          : [];
        setBookingsData({
          ...data.bookingHistory,
          bookings: [...currentBookings, ...dataBookings],
          totals:
            params?.pageIndex && params?.pageIndex > 1 && bookingsData?.totals
              ? bookingsData.totals
              : data?.bookingHistory?.totals,
        });
        setContinuationToken(data.bookingHistory?.continuationToken ?? undefined);
        setIsLoading(false);
      }
    } catch (error) {
      setIsLoading(false);
      setIsError(true);
    }
  };

  useEffect(() => {
    getBookingHistory();
  }, [params]);

  useEffect(() => {
    if (bookingsData && !bookingHasBeenCanceled) {
      analytics.update({
        dashboard: {
          cancelledBookings: Number(bookingsData?.totals?.cancelled),
          futureBookings: Number(bookingsData?.totals?.upcoming),
          totalBartBookings: bookingsData?.bookings?.filter(
            (booking: Booking) =>
              booking?.sourceSystem === 'BART' && booking?.bookingStatus !== BOOKING_TYPE.CANCELLED
          ).length,
          totalBookings: Number(bookingsData?.totalSize),
          moreThanNineNights: bookingsData?.bookings?.filter(
            (booking: Booking) =>
              Number(booking?.noOfNights) > 9 && booking?.bookingStatus !== BOOKING_TYPE.CANCELLED
          ).length,
          moreThanFourRooms: bookingsData?.bookings?.filter(
            (booking: Booking) =>
              Number(booking?.noOfRooms) > 4 && booking?.bookingStatus !== BOOKING_TYPE.CANCELLED
          ).length,
          totalOperaBookings: bookingsData?.bookings?.filter(
            (booking: Booking) =>
              booking?.sourceSystem === 'OPERA' && booking?.bookingStatus !== BOOKING_TYPE.CANCELLED
          ).length,
          checkedinBookings: Number(bookingsData?.totals?.checkedIn),
          stayedBookings: Number(bookingsData?.totals?.past),
        },
      });
    }
    setBookingHasBeenCanceled(false);
  }, [bookingsData]);

  const handleFind = (filterParams: BookingHistoryFilterParams) => {
    setParams({
      ...params,
      ...filterParams,
      pageIndex: 1,
      continuationToken: undefined,
    });
    setContinuationToken(undefined);
  };

  const handleClear = () => {
    setParams({
      ...params,
      pageIndex: 1,
      filterType: '',
      filterValue: '',
      continuationToken: undefined,
    });
    setContinuationToken(undefined);
  };

  const handleLoadMore = () => {
    if (!params?.pageIndex) {
      return;
    }
    setParams({
      ...params,
      pageIndex: params.pageIndex + 1,
      continuationToken,
    });
  };

  const createNotificationDescription = (description: string) => {
    const descriptionArray = description.split('.').filter((el) => el.length !== 0);
    return (
      <>
        {descriptionArray.map((descriptionElem: string) => {
          return <Text key={descriptionElem}>{`${descriptionElem}${'.'}`}</Text>;
        })}
      </>
    );
  };

  if (bookingsData === null && isLoading) {
    return null;
  }

  if ((bookingsData && !bookingsData?.bookings?.length && params.filterValue === '') || isError) {
    return (
      <Notification
        svg={<Info />}
        status="info"
        description={t('dashboard.bookings.bookingListEmpty')}
        variant="infoGrey"
      />
    );
  }

  const updateCancelledBooking = (bookingReference: string) => {
    if (!bookingsData?.bookings) {
      return;
    }

    const bookings = [...bookingsData.bookings];

    setBookingsData({
      ...bookingsData,
      bookings: [
        ...bookings.map((booking) => {
          if (booking?.bookingReference === bookingReference) {
            return {
              ...booking,
              bookingStatus: BOOKING_TYPE.CANCELLED,
            };
          }
          return booking;
        }),
      ],
    });
    setBookingHasBeenCanceled(true);
  };

  const renderBookingCard = (rowData: TableListRow) => {
    return (
      <BookingHistoryInfoCardContainer
        rateName={rowData?.rateName as string}
        bookingReference={rowData.bookingReference as string}
        hotelId={rowData.hotelCode as string}
        arrival={rowData.arrivalDate as string}
        guestSurname={rowData.leadGuestSurname as string}
        bookingStatus={(rowData?.bookingStatus as string) ?? ''}
        onCancel={updateCancelledBooking}
        hotelName={rowData.hotelName as string}
        invoiceRecordNumber={rowData.historyRecordNumber as string}
        invoiceItems={{
          sentInvoiceItems: invoiceItemsSent,
          setInvoiceItems: setInvoiceItemsSent,
        }}
        bookedBy={rowData.bookedBy as string}
        bookingChannel={bookingChannel}
        sourceSystem={(rowData?.sourceSystem as SOURCE_SYSTEM) ?? undefined}
        area={area}
      />
    );
  };

  const bookings: Booking[] = bookingsData?.bookings ?? [];
  const hasMore =
    bookingsData?.totalSize && bookingsData?.pageIndex
      ? bookingsData && bookingsData.totalSize > pageSize * bookingsData?.pageIndex
      : false;

  const statOrder = isBookingHistoryRedesignPIAndBBEnabled
    ? (['checkedIn', 'upcoming', 'past', 'cancelled'] as const)
    : (['upcoming', 'checkedIn', 'past', 'cancelled'] as const);

  const stats: TextStat[] = bookingsData
    ? statOrder.map((key) => ({
        count: Number(bookingsData?.totals?.[key] ?? 0),
        text: t(`dashboard.bookings.${key}`),
      }))
    : [];

  return (
    <>
      {!isBookingHistoryRedesignPIAndBBEnabled && stats.length > 0 && <TextStats stats={stats} />}
      {bookingsData && (
        <>
          <BookingHistoryFilter
            t={t}
            onClear={handleClear}
            onFind={handleFind}
            screenSize={screenSize}
            baseTestId="MyDashboard"
          />
          {isBookingHistoryRedesignPIAndBBEnabled && stats.length > 0 && (
            <TextStats stats={stats} />
          )}
          {/* tableConfig consumed here */}
          <TableList
            columns={tableConfig}
            dataTestIdPrefix="MyDashboard"
            rows={bookings as TableListRow[]}
            expandBtnText={t('dashboard.bookings.showDetails')}
            collapseBtnText={t('dashboard.bookings.closeDetails')}
            screenSize={screenSize}
            loadMoreText={t('dashboard.bookings.loadMore')}
            onLoadMore={hasMore ? handleLoadMore : undefined}
            isLoadingMore={isLoading}
            renderExpandedContent={renderBookingCard}
            containerStyles={{ ...stylesTableContainer, mt: 'md' }}
            rowStyles={rowStyles}
            pageName={PageName.DASHBOARD}
            isBookingHistoryRedesignPIAndBBEnabled={isBookingHistoryRedesignPIAndBBEnabled}
          />
          {bookings.length === 0 && params.filterValue !== '' && (
            <Box mt={'3xl'}>
              <Notification
                svg={<Info />}
                status="info"
                description={createNotificationDescription(t('dashboard.bookings.searchNoResults'))}
                variant="info"
              />
            </Box>
          )}
        </>
      )}
    </>
  );
}

const stylesTableContainer = {
  whiteSpace: 'normal',
} as TableContainerProps;

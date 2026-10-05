import {
  Box,
  Flex,
  StyleProps,
  Table,
  TableContainer,
  Tbody,
  Td,
  Text,
  Th,
  Thead,
  Tr,
} from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import type { TableHeader, TableRow } from '@whitbread-eos/api';
import { Alert, Button, Info, Notification } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { JSX } from 'react/jsx-dev-runtime';

import ResultRow from './ResultRow.component';

export interface Props {
  baseDataTestId: string;
  t: (id: string) => string;
  headerTitles: TableHeader[];
  rows: TableRow[] | undefined;
  loading?: boolean;
  error?: any;
  isError?: boolean;
  isSuccess: boolean;
  bartCard: (props: any) => JSX.Element;
  operaCard: (props: any) => JSX.Element;
  queryClient: QueryClient;
  hasMore: boolean;
  changePage: any;
  language: string;
  bookerLastName?: string;
  arrivalDate?: string;
  bartId?: string | null;
  bartHotelName?: string | null;
  limitExceeded: boolean;
  updateStatusAfterCancel?: (basketReference: string) => void;
  enhancedSearch?: boolean;
  isBookingHistoryRedesignCCUIEnabled?: boolean;
}

export default function ResultList({
  bartCard,
  operaCard,
  loading,
  isSuccess,
  rows,
  headerTitles,
  baseDataTestId,
  t,
  isError,
  queryClient,
  bookerLastName,
  arrivalDate,
  bartId,
  hasMore,
  changePage,
  bartHotelName,
  limitExceeded,
  updateStatusAfterCancel,
  enhancedSearch = false,
  isBookingHistoryRedesignCCUIEnabled,
}: Readonly<Props>) {
  const headerTableStyle = (isBookingHistoryRedesignCCUIEnabled?: boolean) =>
    ({
      backgroundColor: 'lightGrey5',
      ...(isBookingHistoryRedesignCCUIEnabled ? {} : { h: '4rem' }),
    }) as StyleProps;
  return (
    <>
      <TableContainer
        data-testid={formatDataTestId(baseDataTestId, 'Table-Container')}
        {...resultContainerStyle}
      >
        <Table>
          <Thead {...headerTableStyle(isBookingHistoryRedesignCCUIEnabled)}>
            <Tr>
              {headerTitles.map((headerTitle: TableHeader) => {
                return (
                  <Th
                    key={headerTitle.id}
                    {...(isBookingHistoryRedesignCCUIEnabled
                      ? headerResultsTextRedesignStyle
                      : headerResultsTextStyle)}
                    data-testid={formatDataTestId(baseDataTestId, `TableHeader-${headerTitle.id}`)}
                  >
                    {headerTitle.text}
                  </Th>
                );
              })}
            </Tr>
          </Thead>

          <Tbody>
            {rows?.map((row: TableRow, i: number) => {
              if (i === 0) row.isExpandedByDefault = true;

              const sourcePms = row.cells.find((cell) => cell.id === 'SourcePms')?.value;
              const rowBookerLastName = enhancedSearch ? row.bookerLastName : bookerLastName;
              const rowArrivalDate = enhancedSearch
                ? (row.cells.find((cell) => cell.id === 'Date')?.value ?? '')
                : arrivalDate;
              const rowBartHotelId =
                enhancedSearch && sourcePms?.toLowerCase() === 'bart' ? row.hotelId : bartId;
              const rowBookingReference = enhancedSearch
                ? row.bookingReference.split('-')[0]
                : row.bookingReference;

              return (
                <ResultRow
                  bartId={rowBartHotelId}
                  bookerLastName={rowBookerLastName}
                  arrivalDate={rowArrivalDate}
                  bartCard={bartCard}
                  operaCard={operaCard}
                  key={row.bookingReference}
                  cells={row.cells}
                  baseDataTestId={baseDataTestId}
                  queryClient={queryClient}
                  bookingReference={rowBookingReference}
                  rowNr={i}
                  t={t}
                  isExpandedByDefault={row.isExpandedByDefault ?? false}
                  bartHotelName={bartHotelName}
                  updateStatusAfterCancel={updateStatusAfterCancel}
                  isBookingHistoryRedesignCCUIEnabled={isBookingHistoryRedesignCCUIEnabled}
                />
              );
            })}
            {loading && (
              <Tr>
                <Td>
                  <Text data-testid={formatDataTestId(baseDataTestId, 'Table-loading-message')}>
                    {t('searchresults.list.hotel.loading')}
                  </Text>
                </Td>
              </Tr>
            )}
          </Tbody>
        </Table>
        {limitExceeded && (
          <Box
            data-testid={formatDataTestId(baseDataTestId, 'LimitExceededNotification')}
            {...notificationStyles}
          >
            <Notification
              maxWidth="full"
              variant="info"
              status="info"
              title={''}
              description={t('ccui.manageBooking.search.maxResults')}
              svg={<Info />}
            />
          </Box>
        )}
        {rows?.length === 0 && !loading && isSuccess && !limitExceeded && (
          <Box
            data-testid={formatDataTestId(baseDataTestId, 'NoBookingNotification')}
            {...notificationStyles}
          >
            <Notification
              maxWidth="full"
              variant="info"
              status="info"
              title={''}
              description={
                enhancedSearch
                  ? t('ccui.manageBooking.enhancedSearch.notFound')
                  : t('ccui.manageBooking.search.notFound')
              }
              svg={<Info />}
            />
          </Box>
        )}
        {isError && (
          <Flex mt="xl">
            <Notification
              status="error"
              description={t('ccui.manageBooking.resultList.errorMessage')}
              variant="alert"
              maxW="full"
              svg={<Alert />}
            />
          </Flex>
        )}
      </TableContainer>
      {hasMore && (
        <Box {...loadMoreContainerStyle}>
          <Button
            color="btnSecondaryEnabled"
            variant="tertiary"
            size="md"
            onClick={() => changePage()}
          >
            {t('dashboard.bookings.loadMore')}
          </Button>
        </Box>
      )}
    </>
  );
}

const loadMoreContainerStyle = {
  textAlign: 'center',
  mt: 'lg',
} as StyleProps;

const resultContainerStyle = {
  mt: '3xl',
} as StyleProps;

const headerResultsTextStyle = {
  fontWeight: 'medium',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseBlack',
  textTransform: 'none',
  fontFamily: 'header',
} as StyleProps;

const headerResultsTextRedesignStyle = {
  fontWeight: '600',
  fontSize: {
    base: 'sm',
  },
  lineHeight: {
    base: '120%',
  },
  color: 'var(--chakra-colors-darkGrey2)',
  textTransform: 'none',
  fontFamily: 'Proxima Nova Sans',
  letterSpacing: 'initial',
  padding: {
    base: 'var(--chakra-space-4) var(--chakra-space-3) var(--chakra-space-4)',
  },
  whiteSpace: 'normal',
  borderBottom: 'var(--chakra-borders-1px)',
  borderColor: 'var(--chakra-colors-lightGrey2)',
  verticalAlign: 'top',
} as StyleProps;

const notificationStyles = {
  mt: 'xl',
  mb: 'lg',
} as StyleProps;

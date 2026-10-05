import { Text, Box, Link, VStack, HStack } from '@chakra-ui/react';
import { Price } from '@whitbread-eos/api';
import { TableListColumn, TableListRow, ChevronDown, ChevronUp } from '@whitbread-eos/atoms';
import { formatCurrency, formatPriceWithDecimal } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { de, enGB } from 'date-fns/locale';

const BOOKING_STATUSES: { [key: string]: string } = {
  future: 'dashboard.bookings.upcoming',
  past: 'dashboard.bookings.past',
  cancelled: 'dashboard.bookings.cancelled',
  checked_in: 'dashboard.bookings.checkedIn',
};

const getDateLocale = (language: string) => {
  if (language === 'de') return de;
  return enGB;
};

const getPriceFromRow = (value: unknown): Price | null | undefined => {
  return value as Price | null | undefined;
};

const formatPrice = (row: TableListRow, language: string): string => {
  const totalCost = getPriceFromRow(row?.totalCost);
  const currency = totalCost?.currency;
  const amount = Number(totalCost?.amount);
  return currency && Number.isFinite(amount)
    ? formatPriceWithDecimal(language, formatCurrency(currency), amount, true)
    : '';
};

const renderExpandCollapseButton = (
  rowIndex: number,
  isExpanded: boolean | undefined,
  onExpand: ((rowIndex: number, shouldCollapse: boolean) => void) | undefined,
  additionalStyles?: React.CSSProperties
): React.ReactNode => {
  if (typeof rowIndex !== 'number' || onExpand === undefined || isExpanded === undefined) {
    return null;
  }

  return (
    <Link
      style={{ display: 'flex', ...additionalStyles }}
      onClick={(e) => {
        e.stopPropagation();
        onExpand(rowIndex, isExpanded);
      }}
      aria-label={isExpanded ? 'Collapse details' : 'Expand details'}
    >
      <Box
        display="inline-flex"
        alignItems="center"
        justifyContent="center"
        borderRadius="full"
        border="1px solid var(--chakra-colors-btnSecondaryEnabled)"
        width="24px"
        height="24px"
        bg="inherit"
      >
        {isExpanded ? (
          <ChevronUp color="var(--chakra-colors-btnSecondaryEnabled)" />
        ) : (
          <ChevronDown color="var(--chakra-colors-btnSecondaryEnabled)" />
        )}
      </Box>
    </Link>
  );
};

const getTableConfig = (
  t: (x: string, y?: { [key: string]: string }) => string,
  language: string,
  isBusinessUser = false
): TableListColumn[] => {
  const tableListColumn: TableListColumn[] = [
    {
      key: 'leadGuest',
      title: t('dashboard.bookings.booked'),
      render: (row: TableListRow): React.ReactNode => {
        return (
          <Text className="sessioncamhidetext assist-no-show" as="span">
            {row?.leadGuest}
          </Text>
        );
      },
    },
    {
      key: 'hotelName',
      title: t('dashboard.bookings.hotel'),
      hideOnMobile: true,
    },
    {
      key: 'arrivalDate',
      title: t('dashboard.bookings.date'),
      render: (row: TableListRow): React.ReactNode => {
        return (
          <>
            <Text as="span">
              {row?.arrivalDate ? format(new Date(row?.arrivalDate), 'EEE d LLL yyyy') : ''}
            </Text>
            <br />
            <Text as="span">{`${row?.noOfNights} ${t(
              row?.noOfNights && row?.noOfNights === '1'
                ? 'dashboard.bookings.night'
                : 'dashboard.bookings.nights'
            )}`}</Text>
          </>
        );
      },
    },
    {
      key: 'totalCost',
      title: t('dashboard.bookings.price'),
      render: (row: TableListRow): React.ReactNode => {
        return <Text as="span">{formatPrice(row, language)}</Text>;
      },
      hideOnMobile: true,
    },
    {
      key: 'bookingStatus',
      title: t('dashboard.bookings.status'),
      hideOnMobile: true,
      render: (row: TableListRow): React.ReactNode => {
        const status = row?.bookingStatus?.toString()?.toLowerCase() ?? '';
        return <Text as="span">{t(BOOKING_STATUSES[status] ?? '')}</Text>;
      },
    },
  ];

  if (isBusinessUser) {
    const itemForBB = {
      key: 'bookedBy',
      title: t('dashboard.bookings.bookedBy'),
      hideOnMobile: true,
      render: (row: TableListRow): React.ReactNode => {
        return (
          <Text className="sessioncamhidetext assist-no-show" as="span">
            {row?.bookedBy}
          </Text>
        );
      },
    };

    tableListColumn.splice(1, 0, itemForBB);
  }

  return tableListColumn;
};

const getTableRedesignDesktopConfig = (
  t: (x: string, y?: { [key: string]: string }) => string,
  language: string,
  isBusinessUser = false
): TableListColumn[] => {
  const dateLocale = getDateLocale(language);
  const tableListColumn: TableListColumn[] = [
    {
      key: 'hotelName',
      title: t('dashboard.bookings.hotel'),
      render: (row: TableListRow): React.ReactNode => (
        <>
          <Text as="span" className="hotel-name">
            {row?.hotelName}
          </Text>
          <br />
          <Text as="span" color="gray.500" fontSize="sm" className="booking-reference">
            {row?.bookingReference &&
              t('dashboard.bookings.bookingReference') + ' ' + row.bookingReference}
          </Text>
        </>
      ),
    },
    {
      key: 'arrivalDate',
      title: t('dashboard.bookings.date'),
      render: (row: TableListRow): React.ReactNode => {
        return (
          <>
            <Text as="span">
              {row?.arrivalDate
                ? format(new Date(row?.arrivalDate), 'EEE, d LLL yy', { locale: dateLocale })
                : ''}
              {' - '}
              {row?.departureDate
                ? format(new Date(row?.departureDate), 'EEE, d LLL yy', { locale: dateLocale })
                : ''}
            </Text>
            <br />
            <Text as="span" className="nights">{`${row?.noOfNights} ${t(
              row?.noOfNights && row?.noOfNights === '1'
                ? 'dashboard.bookings.night'
                : 'dashboard.bookings.nights'
            )}`}</Text>
          </>
        );
      },
    },
    {
      key: 'leadGuest',
      title: t('dashboard.bookings.booked'),
      render: (row: TableListRow): React.ReactNode => {
        return (
          <Text className="sessioncamhidetext assist-no-show" as="span">
            {row?.leadGuest}
          </Text>
        );
      },
    },
    {
      key: 'totalCost',
      title: t('dashboard.bookings.price'),
      render: (row: TableListRow): React.ReactNode => {
        return <Text as="span">{formatPrice(row, language)}</Text>;
      },
    },
    {
      key: 'bookingStatus',
      title: t('dashboard.bookings.status'),
      hideOnMobile: true,
      width: '1%',
      render: (
        row: TableListRow,
        rowIndex: number,
        rowCollapse?: {
          isExpanded: boolean;
          onExpand: (rowIndex: number, shouldCollapse: boolean) => void;
        }
      ): React.ReactNode => {
        const status = row?.bookingStatus?.toString()?.toLowerCase() ?? '';
        const isExpanded = rowCollapse?.isExpanded;
        const onExpand = rowCollapse?.onExpand;
        return (
          <Box display="flex" alignItems="center">
            <Text as="span" className={`booking-status booking-status--${status}`}>
              {t(BOOKING_STATUSES[status] ?? '')}
            </Text>
            {renderExpandCollapseButton(rowIndex, isExpanded, onExpand, {
              marginLeft: 'auto',
              paddingLeft: '10px',
            })}
          </Box>
        );
      },
    },
  ];

  if (isBusinessUser) {
    const itemForBB = {
      key: 'bookedBy',
      title: t('dashboard.bookings.bookedBy'),
      render: (row: TableListRow): React.ReactNode => {
        return (
          <Text className="sessioncamhidetext assist-no-show" as="span">
            {row?.bookedBy}
          </Text>
        );
      },
    };

    // Insert bookedBy before status (which is the last column)
    const statusColumnIndex = tableListColumn.length - 1;
    tableListColumn.splice(statusColumnIndex, 0, itemForBB);
  }

  return tableListColumn;
};

const getDashboardRedesignTabletConfig = (
  t: (x: string, y?: { [key: string]: string }) => string,
  language: string,
  isBusinessUser = false
): TableListColumn[] => {
  const dateLocale = getDateLocale(language);
  const tableListColumn: TableListColumn[] = [
    {
      key: 'bookingDetails',
      title: t('dashboard.bookings.details'),
      render: (row: TableListRow): React.ReactNode => (
        <VStack alignItems="flex-start" gap="0">
          <Text as="span" className="hotel-name" py="xs">
            {row?.hotelName}
          </Text>

          <HStack gap="xs" pb="sm">
            <Text as="span" fontSize="xs">
              {row?.arrivalDate
                ? format(new Date(row?.arrivalDate), 'EEE, d LLL yy', { locale: dateLocale })
                : ''}
              {' - '}
              {row?.departureDate
                ? format(new Date(row?.departureDate), 'EEE, d LLL yy', { locale: dateLocale })
                : ''}
            </Text>
            <Text as="span" className="nights">{`(${row?.noOfNights} ${t(
              row?.noOfNights && row?.noOfNights === '1'
                ? 'dashboard.bookings.night'
                : 'dashboard.bookings.nights'
            )})`}</Text>
          </HStack>

          {isBusinessUser && (
            <Text as="span" {...bookedForStyles}>
              {`${t('dashboard.bookings.booked')}: ${row?.leadGuest}`}
            </Text>
          )}

          <Text as="span" fontSize="xxs" className="booking-reference">
            {`${t('dashboard.bookings.bookingReference')} ${row?.bookingReference}`}
          </Text>
        </VStack>
      ),
    },
    {
      key: 'bookingStatus',
      title: t('dashboard.bookings.status'),
      width: '150px',
      render: (
        row: TableListRow,
        rowIndex: number,
        rowCollapse?: {
          isExpanded: boolean;
          onExpand: (rowIndex: number, shouldCollapse: boolean) => void;
        }
      ): React.ReactNode => {
        const status = row?.bookingStatus?.toString()?.toLowerCase() ?? '';
        const isExpanded = rowCollapse?.isExpanded;
        const onExpand = rowCollapse?.onExpand;

        return (
          <HStack justifyContent="space-between">
            <VStack alignItems="flex-start" gap={isBusinessUser ? '4xl' : '2xl'}>
              <Text as="span" className={`booking-status booking-status--${status}`}>
                {t(BOOKING_STATUSES[status] ?? '')}
              </Text>
              <Text as="span" fontSize="xs" lineHeight="120%">
                {formatPrice(row, language)}
              </Text>
            </VStack>
            {renderExpandCollapseButton(rowIndex, isExpanded, onExpand, {
              alignSelf: 'flex-end',
            })}
          </HStack>
        );
      },
    },
  ];

  return tableListColumn;
};

const getDashboardRedesignMobileConfig = (
  t: (x: string, y?: { [key: string]: string }) => string,
  language: string,
  isBusinessUser = false
): TableListColumn[] => {
  const dateLocale = getDateLocale(language);
  const tableListColumn: TableListColumn[] = [
    {
      key: 'bookingDetails',
      title: t('dashboard.bookings.details'),
      render: (
        row: TableListRow,
        rowIndex: number,
        rowCollapse?: {
          isExpanded: boolean;
          onExpand: (rowIndex: number, shouldCollapse: boolean) => void;
        }
      ): React.ReactNode => {
        const status = row?.bookingStatus?.toString()?.toLowerCase() ?? '';
        const isExpanded = rowCollapse?.isExpanded;
        const onExpand = rowCollapse?.onExpand;

        return (
          <HStack justifyContent="space-between">
            <VStack alignItems="flex-start" gap="0">
              <Text as="span" className={`booking-status booking-status--${status}`} my="xs">
                {t(BOOKING_STATUSES[status] ?? '')}
              </Text>

              <Text as="span" className="hotel-name" py="xs">
                {row?.hotelName}
              </Text>

              <VStack alignItems="flex-start" gap="xs" pb="sm">
                <Text as="span" fontSize="xs">
                  {row?.arrivalDate
                    ? format(new Date(row?.arrivalDate), 'EEE, d LLL yy', { locale: dateLocale })
                    : ''}
                  {' - '}
                  {row?.departureDate
                    ? format(new Date(row?.departureDate), 'EEE, d LLL yy', { locale: dateLocale })
                    : ''}
                </Text>
                <Text as="span" className="nights">{`${row?.noOfNights} ${t(
                  row?.noOfNights && row?.noOfNights === '1'
                    ? 'dashboard.bookings.night'
                    : 'dashboard.bookings.nights'
                )}`}</Text>
              </VStack>

              {isBusinessUser && (
                <Text as="span" {...bookedForStyles}>
                  {`${t('dashboard.bookings.booked')}: ${row?.leadGuest}`}
                </Text>
              )}

              <Text as="span" fontSize="xxs" className="booking-reference">
                {row?.bookingReference &&
                  t('dashboard.bookings.bookingReference') + ' ' + row.bookingReference}
              </Text>
            </VStack>

            {renderExpandCollapseButton(rowIndex, isExpanded, onExpand, {
              alignSelf: 'flex-end',
            })}
          </HStack>
        );
      },
    },
  ];

  return tableListColumn;
};

const bookedForStyles = {
  fontWeight: '400',
  fontSize: 'xxs',
  color: 'var(--chakra-colors-darkGrey2)',
  lineHeight: '140%',
};

export {
  getTableConfig,
  getTableRedesignDesktopConfig,
  getDashboardRedesignTabletConfig,
  getDashboardRedesignMobileConfig,
};

import { Box, Link, StyleProps, Td, Tr, Text } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import { Area, Cell } from '@whitbread-eos/api';
import { ChevronDown, ChevronUp, ErrorBoundary } from '@whitbread-eos/atoms';
import { formatDataTestId, formatDate, useCustomLocale } from '@whitbread-eos/utils';
import { useState } from 'react';

interface Props {
  queryClient?: QueryClient;
  baseDataTestId: string;
  t: (id: string) => string;
  cells: Cell[];
  bookingReference: string;
  rowNr: number;
  isExpandedByDefault: boolean;
  bartCard: (props: any) => JSX.Element;
  operaCard: (props: any) => JSX.Element;
  bookerLastName?: string;
  arrivalDate?: string;
  bartId?: string | null;
  bartHotelName?: string | null;
  updateStatusAfterCancel?: (basketReference: string) => void;
  isBookingHistoryRedesignCCUIEnabled?: boolean;
}

export default function ResultRow({
  cells,
  bookerLastName,
  bookingReference,
  arrivalDate,
  bartId,
  // queryClient,
  baseDataTestId,
  t,
  rowNr,
  isExpandedByDefault,
  bartCard,
  operaCard,
  bartHotelName,
  isBookingHistoryRedesignCCUIEnabled,
}: Readonly<Props>) {
  const [isExpanded, setIsExpanded] = useState(isExpandedByDefault);
  const sourcePms = cells.find((cell) => cell.id === 'SourcePms')?.value;
  const rowCount = cells.length + 1; // Extra row for Open/Close button;
  const bookingType = cells.find((cell) => cell.id === 'Status')?.value;
  const { language } = useCustomLocale();

  const formatHeader = (headerName: string, headerValue: string) => {
    if (headerName === 'Hotel' && headerValue === '') {
      return bartHotelName;
    }
    if (
      headerName === 'Date' &&
      !isBookingHistoryRedesignCCUIEnabled // Only format if feature toggle is NOT enabled
    ) {
      return formatDate(headerValue, 'EEE d LLL yyyy', language);
    }
    return headerValue;
  };

  const handleRowClick = () => {
    if (isBookingHistoryRedesignCCUIEnabled) {
      setIsExpanded((prev) => !prev);
    }
  };

  const rowCellStyle = isBookingHistoryRedesignCCUIEnabled
    ? resultRowCellRedesignStyle(isExpanded)
    : resultRowCellStyle(isExpanded);

  return (
    <>
      <Tr
        data-testid={formatDataTestId(baseDataTestId, `Table-Row-${rowNr}`)}
        onClick={handleRowClick}
        onKeyDown={(e) => {
          if (isBookingHistoryRedesignCCUIEnabled && (e.key === 'Enter' || e.key === ' ')) {
            e.preventDefault();
            setIsExpanded((prev) => !prev);
          }
        }}
        tabIndex={isBookingHistoryRedesignCCUIEnabled ? 0 : undefined}
        style={
          isBookingHistoryRedesignCCUIEnabled
            ? { cursor: 'pointer', userSelect: 'none' }
            : undefined
        }
      >
        {cells.map((cell: Cell) => {
          if (cell.id === 'Hotel') {
            if (isBookingHistoryRedesignCCUIEnabled) {
              return (
                <Td
                  key={cell.id}
                  data-testid={formatDataTestId(baseDataTestId, `Table-Cell-${cell.id}-${rowNr}`)}
                  {...rowCellStyle}
                >
                  <Box as="span" className="hotel-name" display="block">
                    {cell.value}
                  </Box>
                  <Box
                    as="span"
                    color="gray.500"
                    fontSize="sm"
                    display="inlineBlock"
                    className="booking-reference"
                  >
                    {bookingReference &&
                      t('dashboard.bookings.bookingReference') + ' ' + bookingReference}
                  </Box>
                </Td>
              );
            } else {
              return (
                <Td
                  key={cell.id}
                  data-testid={formatDataTestId(baseDataTestId, `Table-Cell-${cell.id}-${rowNr}`)}
                  {...rowCellStyle}
                >
                  <div>{cell.value}</div>
                </Td>
              );
            }
          }

          if (cell.id === 'Status') {
            if (isBookingHistoryRedesignCCUIEnabled) {
              // Combine Status and expand/collapse icon in one cell
              return (
                <Td
                  key={cell.id}
                  data-testid={formatDataTestId(baseDataTestId, `Table-Cell-${cell.id}-${rowNr}`)}
                  {...rowCellStyle}
                  width="1%"
                  whiteSpace={'nowrap'}
                >
                  <Box display="flex" alignItems="center" justifyContent="space-between">
                    {cell?.value && (
                      <Text
                        as="span"
                        className={`booking-status booking-status--${cell.value.toLowerCase()}`}
                      >
                        {cell.label}
                      </Text>
                    )}
                    <Box
                      as="span"
                      ml="xmd"
                      display="inline-flex"
                      alignItems="center"
                      justifyContent="center"
                      borderRadius="full"
                      border="1px solid var(--chakra-colors-btnSecondaryEnabled)"
                      width="24px"
                      height="24px"
                      bg="inherit"
                      style={{ cursor: 'pointer' }}
                    >
                      {!isExpanded ? (
                        <ChevronDown
                          data-testid={formatDataTestId(baseDataTestId, `chevron-down-${rowNr}`)}
                          color="var(--chakra-colors-btnSecondaryEnabled)"
                        />
                      ) : (
                        <ChevronUp
                          data-testid={formatDataTestId(baseDataTestId, `chevron-up-${rowNr}`)}
                          color="var(--chakra-colors-btnSecondaryEnabled)"
                        />
                      )}
                    </Box>
                  </Box>
                </Td>
              );
            } else {
              // Original Status cell
              return (
                <Td
                  key={cell.id}
                  data-testid={formatDataTestId(baseDataTestId, `Table-Cell-${cell.id}-${rowNr}`)}
                  {...rowCellStyle}
                >
                  {cell.label}
                </Td>
              );
            }
          }

          // Default rendering for all other cells
          return (
            <Td
              key={cell.id}
              data-testid={formatDataTestId(baseDataTestId, `Table-Cell-${cell.id}-${rowNr}`)}
              {...rowCellStyle}
            >
              {formatHeader(cell.id, cell.value)}
            </Td>
          );
        })}
        {/* // Only render the separate expand/collapse cell if NOT redesign */}
        {!isBookingHistoryRedesignCCUIEnabled && (
          <Td {...rowCellStyle}>
            <Link
              {...linkStyles}
              data-testid="hdp_basketHideBreakdownLink"
              onClick={() => setIsExpanded(!isExpanded)}
            >
              {isExpanded ? 'Close' : 'Open'}
              <Box ml="xmd">
                {!isExpanded ? (
                  <ChevronDown color="var(--chakra-colors-btnSecondaryEnabled)" />
                ) : (
                  <ChevronUp color="var(--chakra-colors-btnSecondaryEnabled)" />
                )}
              </Box>
            </Link>
          </Td>
        )}
      </Tr>
      {isExpanded && (
        <Tr bgColor="lightGrey5">
          <Td colSpan={rowCount} {...cardRowStyle}>
            <ErrorBoundary errorMessage={getBICErrorBoundaryMessage(t, bookingReference, !bartId)}>
              {bartId
                ? bartCard({
                    bookerLastName,
                    arrivalDate,
                    bartId,
                    bookingReference: bookingReference,
                    sourcePms,
                    t,
                  })
                : operaCard({
                    area: Area.CCUI,
                    bookingReference: bookingReference,
                    basketReference: null,
                    sourcePms,
                    bookingType,
                  })}
            </ErrorBoundary>
          </Td>
        </Tr>
      )}
    </>
  );
}

function getBICErrorBoundaryMessage(
  t: (id: string) => string,
  bookingReference: string,
  isOperaReservation = true
) {
  const errorMessage = isOperaReservation
    ? t('ccui.manageBooking.resultList.bic.operaErrorMessage')
    : t('ccui.manageBooking.resultList.bic.bartErrorMessage');

  return errorMessage?.replace('<bookingReference>', bookingReference);
}

const cardRowStyle = {
  borderTop: 'none',
} as StyleProps;

const resultRowCellStyle = (isExpanded: boolean | undefined) => {
  return {
    borderBottom: isExpanded ? 'none' : 'var(--chakra-borders-1px)',
    fontSize: 'sm',
    paddingTop: 'var(--chakra-space-2)',
    paddingBottom: 'var(--chakra-space-2)',
    verticalAlign: 'top',
  };
};

const linkStyles = {
  display: 'flex',
  alignItems: 'center',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
} as StyleProps;

const resultRowCellRedesignStyle = (isExpanded: boolean) => ({
  backgroundColor: isExpanded ? 'lightGrey5' : 'transparent',
  borderBottom: isExpanded ? 'none' : 'var(--chakra-borders-1px)',
  borderColor: 'var(--chakra-colors-lightGrey2)',
  whiteSpace: 'normal',
  fontWeight: '600',
  fontSize: 'md',
  color: 'var(--chakra-colors-darkGrey1)',
  lineHeight: '120%',
  padding: 'var(--chakra-space-6) var(--chakra-space-3) var(--chakra-space-6)',
  verticalAlign: 'top',
  _hover: {
    cursor: 'pointer',
  },
  sx: {
    '.booking-reference': {
      fontWeight: '400',
      color: 'var(--chakra-colors-darkGrey2)',
      lineHeight: '140%',
    },
    '.hotel-name': {
      fontWeight: '700',
      color: 'var(--chakra-colors-darkGrey3)',
      lineHeight: '120%',
      fontSize: 'lg',
    },
    '.nights': {
      fontWeight: '400',
      color: 'var(--chakra-colors-darkGrey2)',
      lineHeight: '140%',
      fontSize: 'sm',
    },
    '& .booking-status': {
      fontWeight: '600',
      color: 'var(--chakra-colors-darkGrey1)',
      lineHeight: '140%',
      whiteSpace: 'nowrap',
      fontSize: '13px',
      borderRadius: '16px',
      padding: '2px 8px',
      '&--checked-in': { color: '#333333', backgroundColor: '#FEEFD9' },
      '&--upcoming': { color: '#1C8754', backgroundColor: '#E5F2F6' },
      '&--past': { color: '#58595B', backgroundColor: '#DDDDDD' },
      '&--cancelled': { color: '#D90941', backgroundColor: '#FBE6EC' },
    },
  },
});

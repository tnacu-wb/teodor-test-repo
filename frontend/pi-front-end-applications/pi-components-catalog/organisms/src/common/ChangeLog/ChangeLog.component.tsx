import { Text, Grid, GridProps, TextProps, HStack, Box, StyleProps } from '@chakra-ui/react';
import { QueryClient, useQueryClient } from '@tanstack/react-query';
import {
  RETRIEVE_CHANGES_LOG,
  GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
  BCReservationListItem,
  ChangeLogResults,
} from '@whitbread-eos/api';
import {
  ModalVariants,
  DataModalvariantProps,
  LoadingSpinner,
  Dropdown,
  DropdownOption,
  Button,
} from '@whitbread-eos/atoms';
import {
  useQueryRequest,
  useCustomLocale,
  getAuthCookie,
  formatDataTestId,
  graphQLRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import React, { Dispatch, SetStateAction, useEffect, useState } from 'react';

import ChangeLogTable from './ChangeLogTable.component';
import { getTableColumns } from './tableConfig';

interface Props {
  showChangeLogModal: boolean;
  setShowChangeLogModal: Dispatch<SetStateAction<boolean>>;
  bookingReference: string;
}

async function getChangesLogResults(
  queryClient: QueryClient,
  hotelId: string,
  selectedReservationId: string,
  limit: number,
  offset: number
): Promise<ChangeLogResults> {
  const queryResult = await queryClient.fetchQuery({
    queryKey: ['retrieveChangesLog', hotelId, selectedReservationId, limit, offset],
    queryFn: () =>
      graphQLRequest(RETRIEVE_CHANGES_LOG, {
        hotelId,
        reservationId: selectedReservationId,
        limit,
        offset,
      }),
    ...{
      gcTime: 0,
    },
  });

  const results = queryResult.retrieveChangesLog.activityLog;
  return results;
}

const defaultChangeLogResults = {
  activityLog: [],
  hasMore: false,
  limit: 20,
  offset: 0,
  totalPages: 0,
  totalResults: 0,
  isLoadingChangeLog: false,
  isErrorChangeLog: false,
};

function ChangeLog({
  showChangeLogModal,
  setShowChangeLogModal,
  bookingReference,
}: Readonly<Props>) {
  const baseDataTestId = 'ChangeLog';
  const [hotelId, setHotelId] = useState<string>('');
  const [reservationIdList, setReservationIdList] = useState<string[]>([]);
  const [selectedReservationId, setSelectedReservationId] = useState<string>('');
  const [changeLogResults, setChangeLogResults] =
    useState<ChangeLogResults>(defaultChangeLogResults);
  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const idTokenCookie = getAuthCookie();
  const queryClient = useQueryClient();
  const isValidReservation = !!hotelId && reservationIdList?.length && !!selectedReservationId;
  const isMultiRoomReservatoin = reservationIdList?.length > 1;

  const {
    data: bookingData,
    isLoading: isLoadingBookingData,
    isError: isErrorBookingData,
  } = useQueryRequest(
    ['getBookingConfirmationAuthenticated', bookingReference, language, country],
    GET_DASHBOARD_BOOKING_CONFIRMATION_AUTHENTICATED,
    {
      language,
      country,
      bookingReference,
    },
    { enabled: !!idTokenCookie },
    idTokenCookie,
    true
  );

  const enableChangesLogRequest = Boolean(
    !isLoadingBookingData && !isErrorBookingData && isValidReservation
  );

  const fetchChangeLogs = (offset: number, limit: number) => {
    setChangeLogResults(() => ({ ...changeLogResults, isLoadingChangeLog: true }));
    getChangesLogResults(queryClient, hotelId, selectedReservationId, limit, offset)
      .then((results) => {
        if (results) {
          const latestResults = {
            ...results,
            activityLog: [...changeLogResults.activityLog, ...results.activityLog],
            isLoadingChangeLog: false,
            isErrorChangeLog: false,
          };
          setChangeLogResults(() => ({ ...latestResults }));
        }
      })
      .catch(() => {
        setChangeLogResults(() => ({
          ...changeLogResults,
          isErrorChangeLog: true,
          isLoadingChangeLog: false,
        }));
      });
  };

  useEffect(() => {
    if (enableChangesLogRequest) {
      fetchChangeLogs(changeLogResults.offset, changeLogResults.limit);
    }
  }, [hotelId, selectedReservationId, enableChangesLogRequest]);

  useEffect(() => {
    const hotelId = bookingData?.bookingConfirmationAuthenticated?.hotelId;
    const reservationIdList: string[] =
      bookingData?.bookingConfirmationAuthenticated?.reservationByIdList?.map(
        (x: BCReservationListItem) => x.reservationId
      );
    const firstReservationId = reservationIdList?.length ? reservationIdList[0] : '';

    setHotelId(hotelId ?? '');
    setReservationIdList(reservationIdList ?? []);
    setSelectedReservationId(firstReservationId);
  }, [bookingData]);

  const handleDropdown = (option: DropdownOption | undefined) => {
    setChangeLogResults(defaultChangeLogResults);
    setSelectedReservationId((option?.id as string) ?? '');
  };

  const modalHeader = (
    <HStack gap={4}>
      <Text {...headerTitleStyle} data-testid={formatDataTestId(baseDataTestId, 'ModalTitle')}>
        {t('ccui.changeLogModal.title')}
      </Text>
      {isMultiRoomReservatoin && (
        <Dropdown
          dataTestId={formatDataTestId(baseDataTestId, 'MultiRoomDropdown')}
          dropdownStyles={dropdownStyles}
          matchWidth
          selectedId={selectedReservationId}
          onChange={handleDropdown}
          options={reservationIdList.map((reservationId, index) => ({
            label: `${t('ccui.changeLogModal.multiRoomDropdown.room').replace(
              '[roomNumber]',
              String(index + 1)
            )}`,
            id: reservationId,
          }))}
        />
      )}
    </HStack>
  );

  const variantProps: DataModalvariantProps = {
    title: t('ccui.changeLogModal.title'),
    modalHeight: modalHeight,
    header: modalHeader,
    showDelimiter: false,
  };

  const renderModalBody = () => {
    if (isLoadingBookingData || changeLogResults.isLoadingChangeLog) {
      return (
        <Grid {...gridStyles}>
          <LoadingSpinner loadingText={t('ccui.changeLogModal.loading')} />
        </Grid>
      );
    }

    if (isErrorBookingData || changeLogResults.isErrorChangeLog) {
      return (
        <Grid {...gridStyles}>
          <Text {...messageTitleStyles}>{t('ccui.changeLogModal.errorHeading')}</Text>
          <Text {...errorMessageDescriptionStyles}>
            {t('ccui.changeLogModal.errorDescription')}
          </Text>
        </Grid>
      );
    }

    const rows = changeLogResults.activityLog;
    if (!rows?.length) {
      return (
        <Grid {...gridStyles}>
          <Text {...messageTitleStyles}>{t('ccui.changeLogModal.noResults')}</Text>
        </Grid>
      );
    }

    const columns = getTableColumns(t);

    return <ChangeLogTable columns={columns} rows={rows} />;
  };

  const handleLoadMore = () => {
    fetchChangeLogs(changeLogResults.offset, changeLogResults.limit);
  };

  return (
    <ModalVariants
      isOpen={showChangeLogModal}
      onClose={() => {
        setShowChangeLogModal(false);
      }}
      variant="data"
      variantProps={variantProps}
      updatedWidth={modalWidth}
      dataTestId={baseDataTestId}
    >
      {renderModalBody()}
      {changeLogResults.hasMore && !changeLogResults.isLoadingChangeLog && (
        <Box {...loadMoreContainerStyle}>
          <Button
            color="btnSecondaryEnabled"
            variant="tertiary"
            size="md"
            onClick={handleLoadMore}
            data-testid={formatDataTestId(baseDataTestId, 'LoadMoreButton')}
          >
            {t('ccui.changeLogModal.loadMore')}
          </Button>
        </Box>
      )}
    </ModalVariants>
  );
}

const modalWidth = {
  lg: '1224px',
  xl: '1308px',
};

const modalHeight = { lg: '984px' };

const headerTitleStyle = {
  color: 'darkGrey1',
  justify: 'center',
  justifyContent: 'flex-start',
  fontSize: 'lg',
  lineHeight: '3',
  fontFamily: 'header',
  padding: 'var(--chakra-sizes-3-5) 0',
  margin: 0,
};

const dropdownStyles = {
  wrapperStyles: {
    width: '288px',
  },
  menuButtonStyles: {
    borderColor: 'var(--chakra-colors-lightGrey1)',
  },
  menuListStyles: {
    maxHeight: '200px',
  },
};

const loadMoreContainerStyle = {
  textAlign: 'center',
  my: 'lg',
} as StyleProps;

const gridStyles: GridProps = {
  height: '100%',
  justifyContent: 'center',
  alignContent: 'center',
  justifyItems: 'center',
};

const messageTitleStyles: TextProps = {
  color: 'darkGrey1',
  fontSize: '3xl',
  lineHeight: '5',
  fontWeight: 'semibold',
  mb: 'var(--chakra-space-4)',
};

const errorMessageDescriptionStyles: TextProps = {
  color: 'darkGrey1',
  fontSize: 'lg',
  lineHeight: '3',
  fontWeight: 'normal',
};

export default ChangeLog;

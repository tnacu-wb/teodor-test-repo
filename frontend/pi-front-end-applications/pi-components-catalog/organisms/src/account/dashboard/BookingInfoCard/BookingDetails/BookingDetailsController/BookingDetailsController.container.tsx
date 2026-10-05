import { BoxProps, Flex } from '@chakra-ui/react';
import {
  Area,
  BC_RESERVATION_STATUS,
  BOOKING_CHANNEL,
  BOOKING_SUBCHANNEL,
  BOOKING_TYPE,
  DASHBOARD_MANAGE_BOOKING,
  DpaInfo,
  OverridenUserInfo,
} from '@whitbread-eos/api';
import { Alert, LoadingSpinner, Notification } from '@whitbread-eos/atoms';
import {
  formatFindBookingToken,
  getFindBookingToken,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { formatISO } from 'date-fns';
import { useTranslation } from 'next-i18next';
import { useEffect } from 'react';

import { IDVDataProps } from '../../IDVModal';
import BookingDetailsController from './BookingDetailsController.component';

export interface Props {
  hotelId: string;
  basketReference: string | null;
  bookingReference: string;
  idvData?: IDVDataProps;
  setIdvData?: (value: IDVDataProps) => void;
  defaultDataFromBooking?: any;
  area?: Area;
  getBookingStatus?: any;
  bookingStatus: string;
  bookingType?: string;
  dpaInfo?: DpaInfo;
  setDpaInfo?: (value: DpaInfo) => void;
  overridenUserInfo?: OverridenUserInfo;
  inputValues?: any;
  isAmendSuccessful: boolean;
  rateType: string;
  arrivalDate?: string;
  bookingSurname?: string;
  isAmendPage?: boolean;
  operaConfNumber?: string;
  isRemovePIIDataFromLocalStorageEnabled?: boolean;
}

export default function BookingDetailsControllerContainer({
  hotelId,
  basketReference,
  bookingReference,
  area = 'pi' as Area.PI,
  getBookingStatus,
  bookingStatus,
  bookingType,
  idvData,
  setIdvData,
  defaultDataFromBooking,
  dpaInfo,
  setDpaInfo,
  overridenUserInfo,
  inputValues,
  isAmendSuccessful,
  rateType,
  arrivalDate,
  bookingSurname,
  isAmendPage,
  operaConfNumber,
  isRemovePIIDataFromLocalStorageEnabled = false,
}: Readonly<Props>) {
  const date = formatISO(Date.now());
  const { t } = useTranslation();
  const { language } = useCustomLocale();

  const {
    data = {
      manageBooking: {
        isCancellable: false,
        isAmendable: false,
        isRuleCompliant: true,
        aemLabelKey: '',
      },
    },
    isError,
    isLoading,
    error,
    refetch,
  } = useQueryRequest(
    ['manageBookingDashBoard', basketReference, hotelId],
    DASHBOARD_MANAGE_BOOKING,
    {
      cancelInformationCriteria: {
        userDateTime: date,
        bookingChannel: {
          channel: area === 'pi' ? BOOKING_CHANNEL.PI : BOOKING_CHANNEL.CCUI,
          subchannel: BOOKING_SUBCHANNEL.WEB,
          language: language,
        },
        basketReference: basketReference,
        hotelId: hotelId,
        token: formatFindBookingToken(getFindBookingToken().token),
      },
    },
    {
      enabled:
        ((area === Area.PI && bookingStatus !== BC_RESERVATION_STATUS.CANCELLED) ||
          (area === Area.CCUI && (bookingType === BOOKING_TYPE.UPCOMING || isAmendPage))) &&
        !!hotelId,
      staleTime: 0,
      cacheTime: 0,
    }
  );

  useEffect(() => {
    // refetch manageBooking query only when the upcoming reservation has been overriden and idv passed or if the booking got cancelled
    const bookingCanBeRefetched =
      ((bookingType === BOOKING_TYPE.UPCOMING || isAmendPage) &&
        overridenUserInfo?.reservationOverridden &&
        (dpaInfo?.dpaOverride || dpaInfo?.dpaPassed)) ||
      bookingType === BOOKING_TYPE.CANCELLED;

    if (bookingCanBeRefetched) {
      refetch();
    }
  }, [
    overridenUserInfo?.reservationOverridden,
    dpaInfo?.dpaOverride,
    dpaInfo?.dpaPassed,
    bookingType,
    isAmendPage,
  ]);

  if (isError) {
    return (
      <Notification
        status="error"
        description={String(error)}
        variant="alert"
        maxW="full"
        svg={<Alert />}
      />
    );
  }

  if (isLoading) {
    return (
      <Flex {...loadingStyle} data-testid="Loading-BookingDetailsController">
        <LoadingSpinner loadingText={t('booking.loading')} />
      </Flex>
    );
  }

  return (
    <BookingDetailsController
      refetchManageBooking={() => {
        getBookingStatus();
      }}
      manageBookingData={data.manageBooking}
      bookingReference={bookingReference}
      basketReference={basketReference}
      area={area}
      bookingStatus={bookingStatus}
      bookingType={bookingType}
      idvData={idvData}
      setIdvData={setIdvData}
      defaultDataFromBooking={defaultDataFromBooking}
      dpaInfo={dpaInfo}
      setDpaInfo={setDpaInfo}
      inputValues={inputValues}
      isAmendSuccessful={isAmendSuccessful}
      rateType={rateType}
      arrivalDate={arrivalDate}
      bookingSurname={bookingSurname}
      isAmendPage={isAmendPage}
      operaConfNumber={operaConfNumber}
      isRemovePIIDataFromLocalStorageEnabled={isRemovePIIDataFromLocalStorageEnabled}
    />
  );
}

//<editor-fold desc="Styles" defaultstate="collapsed">

const loadingStyle = {
  height: '100%',
  width: '100%',
  left: 0,
  top: 0,
  position: 'fixed',
  zIndex: 999,
  backgroundColor: 'baseWhite',
  textAlign: 'center',
  lineHeight: 3,
  justifyContent: 'center',
  color: 'btnSecondaryEnabled',
  fontSize: 'xl',
  flex: 'none',
  flexGrow: 0,
  order: 1,
  alignSelf: 'stretch',
} as BoxProps;
//</editor-fold>

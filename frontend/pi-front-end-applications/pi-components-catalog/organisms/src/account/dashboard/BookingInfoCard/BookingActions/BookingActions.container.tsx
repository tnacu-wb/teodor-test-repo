import { Box } from '@chakra-ui/react';
import {
  OverridenUserInfo,
  Area,
  DpaInfo,
  FT_CCUI_CHANGE_PAYMENT_METHOD,
  FT_PI_BOOKING_STATUS_IN_BIC_HEADER,
  FT_PI_PIB_BIC_DOWNLOAD_INVOICE,
} from '@whitbread-eos/api';
import {
  AgentMemoState,
  formatDataTestId,
  useAgentMemo,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useCallback, useState } from 'react';

import { ChangeLog } from '../../../../common';
import BookingActions from './BookingActions.component';
import getActionsByCriteria, { BookingActionsCriteria } from './bookingActionsFactory';

interface Props {
  bookingReference: string;
  basketReference: string | null;
  operaConfNumber?: string;
  area: Area;
  bookingStatus: string;
  bookingType?: string;
  dpaInfo?: DpaInfo;
  setIsAgentOverrideModalVisible?: Dispatch<SetStateAction<boolean>>;
  overridenUserInfo?: OverridenUserInfo;
  hideBookingStatus?: boolean;
  role?: string;
  handleResendInvoiceAction?: () => void;
  handleDownloadInvoiceAction?: () => void;
  isDownloadingInvoice?: boolean;
  handleResendConfirmationAction?: () => void;
  shouldRenderStatusAndActions?: boolean;
  handleRepeatBookingAction?: (func?: () => void) => void;
  handleChangePayment?: () => void;
  paymentOption: string;
  isChangedPaymentApplied?: boolean;
}
export default function BookingActionsContainer(props: Readonly<Props>) {
  const [showChangeLogModal, setShowChangeLogModal] = useState(false);
  const { openAgentMemo, setAgentMemoReservationId } = useAgentMemo();

  const {
    [FT_CCUI_CHANGE_PAYMENT_METHOD]: isChangePaymentFeatureEnabled,
    [FT_PI_BOOKING_STATUS_IN_BIC_HEADER]: isBICHeaderBookingStatusEnabled,
    [FT_PI_PIB_BIC_DOWNLOAD_INVOICE]: isBICDownloadInvoiceEnabled,
  } = useFeatureToggle();

  const handleAgentMemo = ({ variant, reservationId }: AgentMemoState) => {
    setAgentMemoReservationId(reservationId);
    openAgentMemo(variant);
  };

  const handleChangeLogClicked = useCallback(() => {
    setShowChangeLogModal(true);
  }, []);

  const criteria: BookingActionsCriteria = {
    area: props.area,
    role: props?.role ?? 'manager',
    bookingType: props.bookingType,
    bookingStatus: props.bookingStatus,
    isChangePaymentFeatureEnabled,
    isBICDownloadInvoiceEnabled,
  };

  const baseDataTestId = 'BookingActions';

  const linksToBeShown = getActionsByCriteria(
    criteria,
    props?.basketReference,
    props.paymentOption,
    props?.setIsAgentOverrideModalVisible,
    handleAgentMemo,
    props?.handleResendInvoiceAction,
    props?.handleDownloadInvoiceAction,
    props?.isDownloadingInvoice,
    props?.handleResendConfirmationAction,
    props?.handleRepeatBookingAction,
    handleChangeLogClicked,
    props?.handleChangePayment
  );

  return (
    <>
      <Box data-testid={formatDataTestId(baseDataTestId, 'Container')}>
        <BookingActions
          {...props}
          config={linksToBeShown}
          baseDataTestId={baseDataTestId}
          isBICHeaderBookingStatusEnabled={isBICHeaderBookingStatusEnabled}
        />
      </Box>
      {showChangeLogModal && (
        <ChangeLog
          showChangeLogModal={showChangeLogModal}
          setShowChangeLogModal={setShowChangeLogModal}
          bookingReference={props.bookingReference}
        />
      )}
    </>
  );
}

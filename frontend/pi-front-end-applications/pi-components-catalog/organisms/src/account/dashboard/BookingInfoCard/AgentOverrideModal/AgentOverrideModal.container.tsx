import { Box } from '@chakra-ui/react';
import {
  DASHBOARD_UPDATE_OVERRIDE_RESERVATION,
  OverridenUserInfo,
  OverrideReason,
  UpdateReservationOverrideReasonsCriteria,
} from '@whitbread-eos/api';
import { FormProps } from '@whitbread-eos/atoms';
import { formatDataTestId, useMutationRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { SetStateAction, useCallback, useEffect, useState } from 'react';

import AgentOverrideModal from './AgentOverrideModal.component';

export interface Props {
  basketReference: string;
  hotelId: string;
  reasons: OverrideReason[];
  isVisible: boolean;
  onClose: () => void;
  getBookingInfo?: () => void | undefined;
  overridenUserInfo: OverridenUserInfo;
  error?: string;
}

export default function AgentOverrideModalContainer({
  basketReference,
  hotelId,
  reasons,
  error: reasonsError,
  isVisible,
  onClose,
  getBookingInfo,
  overridenUserInfo,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const reservationOverrideReasons = overridenUserInfo.reservationOverrideReasons;

  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    selectReason: '',
    managerName: '',
    callerName: '',
  });

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );

  const {
    mutation,
    error: mutationError,
    isSuccess,
  } = useMutationRequest(DASHBOARD_UPDATE_OVERRIDE_RESERVATION);

  const onSubmit = (data: FormProps['defaultValues']) => {
    const updateReservationOverrideReasonsCriteria: UpdateReservationOverrideReasonsCriteria = {
      basketReference,
      hotelId,
      reasonCode: data.selectReason as string,
      reasonName: reasons.find((reason) => reason.code === data.selectReason)?.name as string,
      callerName: data.callerName as string,
      managerName: (data.managerName as string) || '',
    };
    mutation.mutate(updateReservationOverrideReasonsCriteria);
  };

  useEffect(() => {
    if (isSuccess && getBookingInfo) {
      getBookingInfo();
      onClose();
    }
  }, [isSuccess]);

  useEffect(() => {
    // if the reservation has been overriden, the default values are being set after the overridenUserInfo
    if (overridenUserInfo.reservationOverridden) {
      setDefaultValues({
        selectReason: reservationOverrideReasons.reasonCode,
        managerName: reservationOverrideReasons.managerName as string,
        callerName: reservationOverrideReasons.callerName,
      });
    }
  }, [overridenUserInfo.reservationOverridden]);

  return (
    <Box data-testid={formatDataTestId('AgentOverride', 'Container')}>
      <AgentOverrideModal
        getFormState={getFormState}
        defaultValues={defaultValues}
        reasons={reasons}
        error={(mutationError || reasonsError) as string | undefined}
        isVisible={isVisible}
        onClose={onClose}
        onSubmit={onSubmit}
        t={t}
      />
    </Box>
  );
}

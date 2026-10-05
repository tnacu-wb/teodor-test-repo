import { QueryClient } from '@tanstack/react-query';
import { Customer, type BIReservationListItem } from '@whitbread-eos/api';
import { Form } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

import { guestDetailsBBFormConfig } from './guestDetailsBBFormConfig';

export interface GuestDetailsBBContainerProps {
  numberOfRooms: number;
  reservationByIdList: BIReservationListItem[];
  validationLabels: object;
  onSubmit: (data: object) => void;
  labels: object;
  guestList: any;
  setGuestUser: any;
  getFormState: any;
  queryClient: QueryClient;
  isDynamicSearchVisible: boolean;
  isAccompanyingGuestDetailsEnabled: boolean;
  accessLevel: string;
  selfBookerDetails: string;
  userDetails?: Customer;
}

export default function GuestDetailsBBContainer({
  numberOfRooms,
  reservationByIdList,
  validationLabels,
  onSubmit,
  labels,
  guestList,
  setGuestUser,
  getFormState,
  queryClient,
  isDynamicSearchVisible,
  isAccompanyingGuestDetailsEnabled,
  accessLevel,
  selfBookerDetails,
  userDetails,
}: Readonly<GuestDetailsBBContainerProps>) {
  const { t } = useTranslation();
  const baseDataTestIdGuestDetails = 'GuestDetailsBBContainer';
  const baseDataTestIdAccompayningGuestDetails = 'AccompanyingGuestDetailsBBContainer';

  return (
    <Form
      {...guestDetailsBBFormConfig({
        getFormState,
        defaultValues: guestList,
        onSubmit,
        baseDataTestIdGuestDetails: baseDataTestIdGuestDetails,
        baseDataTestIdAccompayningGuestDetails: baseDataTestIdAccompayningGuestDetails,
        t,
        labels,
        validationLabels,
        reservationByIdList,
        numberOfRooms,
        queryClient,
        guestList,
        setGuestUser,
        isDynamicSearchVisible,
        autoComplete: 'off',
        isAccompanyingGuestDetailsEnabled,
        accessLevel,
        selfBookerDetails,
        userDetails,
      })}
    />
  );
}

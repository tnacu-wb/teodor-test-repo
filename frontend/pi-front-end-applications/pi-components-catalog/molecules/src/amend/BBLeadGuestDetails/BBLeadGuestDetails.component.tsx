import { QueryClient } from '@tanstack/react-query';
import {
  AmendLeadGuestLabels,
  AmendLeadGuestValidationLabels,
  BBLeadGuestDetailsType,
  GuestDetails,
  KeyValuePair,
  Suggestion,
  Customer,
} from '@whitbread-eos/api';
import { Form, FormProps } from '@whitbread-eos/atoms';
import { useTranslation } from 'next-i18next';

import { BBLeadGuestDetailsFormConfig } from './BBLeadGuestDetailsFormConfig';

export interface GuestDetailsBBContainerProps {
  numberOfRooms: number;
  validationLabels: AmendLeadGuestValidationLabels;
  onSubmit: (guestDetails: BBLeadGuestDetailsType) => void;
  labels: AmendLeadGuestLabels;
  guestList: FormProps['defaultValues'];
  setGuestUser: (user: Suggestion, index: number) => void;
  getFormState: FormProps['getFormState'];
  queryClient: QueryClient;
  isDynamicSearchVisible: boolean;
  isEdit: boolean;
  bbEmployeeList?: Suggestion[];
  defaultGuest?: GuestDetails;
  onEditBbInput?: (data: KeyValuePair | GuestDetails) => void;
  userDetails?: Customer;
}

export default function BBLeadGuestDetails({
  numberOfRooms,
  validationLabels,
  onSubmit,
  labels,
  guestList,
  setGuestUser,
  getFormState,
  queryClient,
  isDynamicSearchVisible,
  isEdit,
  bbEmployeeList,
  defaultGuest,
  onEditBbInput,
  userDetails,
}: Readonly<GuestDetailsBBContainerProps>) {
  const { t } = useTranslation();
  const baseDataTestId = 'GuestDetailsBBContainer';
  return (
    <Form
      {...BBLeadGuestDetailsFormConfig({
        getFormState,
        defaultValues: guestList,
        onSubmit,
        baseTestId: baseDataTestId,
        t,
        labels,
        validationLabels,
        numberOfRooms,
        queryClient,
        guestList,
        setGuestUser,
        isDynamicSearchVisible,
        isEdit,
        bbEmployeeList,
        defaultGuest,
        onEditBbInput,
        userDetails,
      })}
    />
  );
}

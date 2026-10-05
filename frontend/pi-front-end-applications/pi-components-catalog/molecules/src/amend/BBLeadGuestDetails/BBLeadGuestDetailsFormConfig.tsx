import { QueryClient } from '@tanstack/react-query';
import {
  AmendLeadGuestLabels,
  AmendLeadGuestValidationLabels,
  BBLeadGuestDetailsType,
  Customer,
  GuestDetails,
  KeyValuePair,
  Suggestion,
} from '@whitbread-eos/api';
import { FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  formatGuestTitleOptions,
  getTitleDropdownValues,
} from '@whitbread-eos/utils';

import BBGuestDetailsForm from '../../guest-details/BBGuestDetailsForm';
import validateForm from './formValidation';

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

interface BBGuestDetailsFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (guestDetails: BBLeadGuestDetailsType) => void;
  baseTestId: string;
  t: (id: string) => string;
  labels: AmendLeadGuestLabels;
  validationLabels: AmendLeadGuestValidationLabels;
  numberOfRooms: number;
  queryClient: QueryClient;
  guestList: FormProps['defaultValues'];
  setGuestUser: (user: Suggestion, index: number) => void;
  isDynamicSearchVisible: boolean;
  isEdit: boolean;
  bbEmployeeList?: Suggestion[];
  defaultGuest?: GuestDetails;
  onEditBbInput?: (data: KeyValuePair | GuestDetails) => void;
  userDetails?: Customer;
}

export const BBLeadGuestDetailsFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseTestId,
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
}: BBGuestDetailsFormConfigArgsType) => {
  const { formValidationSchema } = validateForm(validationLabels);
  const config = {
    id: 'leadGuestDetailsForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          dropdownOptions: getTitleDropdownValues(
            formatGuestTitleOptions(t('booking.guest.nameTitles')),
            defaultGuest?.title ? defaultGuest.title : ''
          ),
          name: 'guestDetailsBBForm',
          label: '',
          testid: formatDataTestId(baseTestId, 'Form'),
          Component: BBGuestDetailsForm,
          props: {
            numberOfRooms,
            labels,
            defaultValues,
            queryClient,
            guestList,
            setGuestUser,
            isDynamicSearchVisible,
            isEdit,
            isAmendPage: true,
            bbEmployeeList,
            defaultGuest,
            onEditBbInput,
            userDetails,
          },
        },
      ],
      onSubmitAction: onSubmit,
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
  } as FormProps;

  return config;
};

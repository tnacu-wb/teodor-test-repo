import { QueryClient } from '@tanstack/react-query';
import { Customer, type BIReservationListItem } from '@whitbread-eos/api';
import { FORM_BUTTON_TYPES, FORM_FIELD_TYPES, FormProps } from '@whitbread-eos/atoms';
import { BBAllGuestsDetailsForm } from '@whitbread-eos/molecules';
import { formatDataTestId, formatGuestTitleOptions } from '@whitbread-eos/utils';

import validateForm from './formValidation';

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

interface BBGuestDetailsFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: object) => void;
  baseDataTestIdGuestDetails: string;
  baseDataTestIdAccompayningGuestDetails: string;
  t: (id: string) => string;
  labels: object;
  validationLabels: object;
  numberOfRooms: number;
  reservationByIdList: BIReservationListItem[];
  queryClient: QueryClient;
  guestList: any;
  setGuestUser: any;
  isDynamicSearchVisible: boolean;
  autoComplete: string;
  isAccompanyingGuestDetailsEnabled: boolean;
  accessLevel: string;
  selfBookerDetails: string;
  userDetails?: Customer;
}

export const guestDetailsBBFormConfig = ({
  autoComplete,
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestIdGuestDetails,
  baseDataTestIdAccompayningGuestDetails,
  t,
  labels,
  validationLabels,
  numberOfRooms,
  reservationByIdList,
  queryClient,
  guestList,
  setGuestUser,
  isDynamicSearchVisible,
  isAccompanyingGuestDetailsEnabled,
  accessLevel,
  selfBookerDetails,
  userDetails,
}: BBGuestDetailsFormConfigArgsType) => {
  const { formValidationSchema } = validateForm(validationLabels);

  const config = {
    autoComplete: autoComplete,
    id: 'guestDetailsBBForm',
    elements: {
      fieldsContainerStyles: {
        marginBottom: 0,
      },
      buttonsContainerStyles: {
        marginBottom: 0,
      },
      fields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          dropdownOptions: formatGuestTitleOptions(t('booking.guest.nameTitles')),
          name: 'bbGuestDetails',
          label: '',
          testid: formatDataTestId(baseDataTestIdGuestDetails, 'Form'),
          Component: BBAllGuestsDetailsForm,
          props: {
            numberOfRooms,
            labels,
            reservationByIdList,
            defaultValues,
            queryClient,
            guestList,
            setGuestUser,
            isDynamicSearchVisible,
            isAccompanyingGuestDetailsEnabled,
            accessLevel,
            selfBookerDetails,
            baseDataTestIdAccompayningGuestDetails,
            userDetails,
          },
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          action: onSubmit,
          label: 'Submit',
          styles: {
            display: 'none',
          },
          props: {
            variant: 'tertiary',
            size: 'full',
          },
        },
      ],
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
  } as FormProps;

  return config;
};

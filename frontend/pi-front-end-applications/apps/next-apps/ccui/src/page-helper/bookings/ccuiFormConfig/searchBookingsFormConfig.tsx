import { Box, Link, StyleProps } from '@chakra-ui/react';
import type { ClearHotelFieldsState, SBForm } from '@whitbread-eos/api';
import type { SingleDatePickerLabels } from '@whitbread-eos/atoms';
import {
  ChevronDown24,
  ChevronUp24,
  FORM_FIELD_TYPES,
  FormDynamicFieldCompProps,
  FormProps,
} from '@whitbread-eos/atoms';
import {
  ArrivalDate,
  BookingsSubmitButton,
  Cancellation as CancellationDate,
  HotelDropdown,
  LocationDropdown,
  PhoneSelector,
  ResetSearchCriteriaButton,
} from '@whitbread-eos/molecules';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { Dispatch, SetStateAction, useState } from 'react';

import validateBookingsForm from './validateBookingsForm';

interface SearchBookingsFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: SBForm) => void;
  onReset: () => void;
  baseDataTestId: string;
  t: (id: string) => string;
  singleDatePickerLabels: SingleDatePickerLabels;
  clearHotelFields?: ClearHotelFieldsState;
  resetForm?: number;
  language?: string;
  clearPhoneField?: boolean;
  setClearPhoneField?: Dispatch<SetStateAction<boolean>>;
  disableGuestSurname?: boolean;
  disableBookerSurname?: boolean;
  enhancedSearch?: boolean;
}

const ExtendedSearchCriteria = ({ field, handleSetValue }: FormDynamicFieldCompProps) => {
  const { t } = useTranslation();
  const { name } = field;
  const [isExtendedSearchSelected, setIsExtendedSearchSelected] = useState(false);

  const extendedSearchCriteriaValue = !isExtendedSearchSelected
    ? 'extendedSearchCriteria'
    : 'collapsedSearchCriteria';

  const toggleSearchCriteria = () => {
    if (!handleSetValue) {
      return;
    }

    setIsExtendedSearchSelected(!isExtendedSearchSelected);

    handleSetValue(name, extendedSearchCriteriaValue);
  };

  return (
    <Link {...linkStyles} onClick={() => toggleSearchCriteria()}>
      {isExtendedSearchSelected
        ? t('ccui.manageBooking.showLess')
        : t('ccui.manageBooking.showMore')}
      <Box ml="1">
        {!isExtendedSearchSelected ? (
          <ChevronDown24 color="var(--chakra-colors-btnSecondaryEnabled)" />
        ) : (
          <ChevronUp24 color="var(--chakra-colors-btnSecondaryEnabled)" />
        )}
      </Box>
    </Link>
  );
};

export const searchBookingsFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  onReset,
  baseDataTestId,
  t,
  singleDatePickerLabels,
  clearHotelFields,
  resetForm,
  language,
  clearPhoneField,
  setClearPhoneField,
  disableGuestSurname = false,
  disableBookerSurname = false,
  enhancedSearch = false,
}: SearchBookingsFormConfigArgsType) => {
  const { formValidationSchema } = validateBookingsForm({ t, enhancedSearch });

  const extendedSearchCriteriaFields = {
    extendedSearchCriteria: [
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        id: 'guestLastName',
        name: 'guestLastName',
        label: t('ccui.manageBooking.guestSurname'),
        testid: formatDataTestId(baseDataTestId, 'GuestSurname'),
        styles: { ...inputStyle },
        props: {
          useTooltip: true,
          isDisabled: disableGuestSurname,
        },
      },
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        id: 'bookerPostcode',
        name: 'bookerPostcode',
        label: t('ccui.manageBooking.bookerPostcode'),
        testid: formatDataTestId(baseDataTestId, 'BookerPostcode'),
        styles: { ...inputStyle },
        props: {
          useTooltip: true,
        },
      },
      {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        id: 'hotelDetails',
        name: 'hotelDetails',
        label: t('ccui.manageBooking.hotelName'),
        testid: formatDataTestId(baseDataTestId, 'HotelName'),
        Component: HotelDropdown,
        styles: { ...inputStyle },
        props: {
          clearHotelFields,
        },
      },
      {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        id: 'hotelLocation',
        name: 'hotelLocation',
        label: t('ccui.manageBooking.hotelLocation'),
        testid: formatDataTestId(baseDataTestId, 'HotelLocation'),
        Component: LocationDropdown,
        styles: { ...inputStyle },
        props: {
          clearHotelFields,
        },
      },
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        id: 'bookerEmail',
        name: 'bookerEmail',
        label: t('ccui.manageBooking.emailAddress'),
        testid: formatDataTestId(baseDataTestId, 'EmailAddress'),
        styles: { ...inputStyle },
        props: {
          useTooltip: true,
        },
      },
      {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        id: 'bookerPhone',
        name: 'bookerPhone',
        label: t('ccui.manageBooking.telephoneNumber'),
        testid: formatDataTestId(baseDataTestId, 'TelephoneNumber'),
        Component: PhoneSelector,
        styles: { ...inputStyle },
        props: {
          useTooltip: true,
          clearField: clearPhoneField,
          showIcon: false,
          currentLang: language,
        },
      },
      {
        type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
        id: 'cancellationDate',
        name: 'cancellationDate',
        label: t('ccui.manageBooking.cancellationDate'),
        testid: formatDataTestId(baseDataTestId, 'CancellationDate'),
        Component: CancellationDate,
        styles: { ...inputStyle },
        props: {
          singleDatePickerLabels,
          useTooltip: true,
        },
      },
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        id: 'companyName',
        name: 'companyName',
        label: t('ccui.manageBooking.companyName'),
        testid: formatDataTestId(baseDataTestId, 'CompanyName'),
        styles: { ...inputStyle },
        props: {
          useTooltip: true,
        },
      },
      {
        type: FORM_FIELD_TYPES.INPUT_TEXT,
        id: 'thirdPartyBookingReferenceNumber',
        name: 'thirdPartyBookingReferenceNumber',
        label: t('ccui.manageBooking.3rdPartyBookingReference'),
        testid: formatDataTestId(baseDataTestId, '3rdPartyBookingReference'),
        styles: { ...inputStyle },
        props: {
          useTooltip: true,
          isDisabled: true,
        },
      },
    ],
  };
  const config = {
    id: 'searchBookingsForm',
    elements: {
      fieldsContainerStyles,
      buttonsContainerStyles,
      fields: [
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'bookingReference',
          name: 'bookingReference',
          label: enhancedSearch
            ? t('ccui.manageBooking.reference')
            : t('ccui.manageBooking.bookingReference'),
          testid: formatDataTestId(baseDataTestId, 'BookingReference'),
          styles: { ...inputStyle },
          props: {
            useTooltip: true,
          },
        },
        {
          type: FORM_FIELD_TYPES.INPUT_TEXT,
          id: 'bookerLastName',
          name: 'bookerLastName',
          label: t('ccui.manageBooking.bookingSurname'),
          testid: formatDataTestId(baseDataTestId, 'BookingSurname'),
          styles: { ...inputStyle },
          props: {
            useTooltip: true,
            isDisabled: disableBookerSurname,
          },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'arrivalDate',
          name: 'arrivalDate',
          label: t('ccui.manageBooking.arrivalDate'),
          testid: formatDataTestId(baseDataTestId, 'ArrivalDate'),
          Component: ArrivalDate,
          styles: { ...inputStyle },
          props: {
            singleDatePickerLabels,
            useTooltip: true,
          },
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          id: 'extendedSearchCriteria',
          name: 'extendedSearchCriteria',
          label: '',
          Component: ExtendedSearchCriteria,
          relatedFields: extendedSearchCriteriaFields,
          testid: formatDataTestId(baseDataTestId, 'ExtendedSearchCriteria'),
          styles: {
            gridColumn: '3/3',
            placeSelf: 'end',
            marginRight: 'lg',
          },
          hidden: !enhancedSearch,
        },
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          label: t('ccui.manageBooking.searchBooking'),
          name: 'bookingsSubmitButton',
          action: onSubmit,
          testid: formatDataTestId(baseDataTestId, 'Submit'),
          Component: BookingsSubmitButton,
          props: {
            type: 'submit',
            enhancedSearch,
          },
        },
      ],
      bottomFields: [
        {
          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
          label: t('ccui.manageBooking.clearSearch'),
          name: 'resetSearchCriteriaButton',
          testid: formatDataTestId(baseDataTestId, 'Reset'),
          Component: ResetSearchCriteriaButton,
          props: {
            action: onReset,
            clearHotelFields,
            setClearPhoneField,
          },
        },
      ],
    },
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    resetForm,
  } as FormProps;

  return config;
};

const fieldsContainerStyles = {
  display: 'grid',
  gridTemplateColumns: '1fr 1fr 1fr',
  gridTemplateRows: 'auto',
  justifyItems: 'stretch',
  columnGap: 'lg',
  rowGap: 'sm',
  height: 'auto',
  mb: 'md',
} as StyleProps;
const buttonsContainerStyles = {} as StyleProps;

const inputStyle = {
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  p: 0,
} as StyleProps;

const linkStyles = {
  display: 'flex',
  alignItems: 'center',
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
} as StyleProps;

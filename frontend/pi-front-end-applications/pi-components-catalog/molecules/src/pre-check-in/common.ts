import { BoxProps, FlexProps, GridProps, StyleProps, TextProps } from '@chakra-ui/react';
import { BookingConfirmation } from '@whitbread-eos/api';
import { DropdownStyles } from '@whitbread-eos/atoms';
import { analytics, getBillingAddress } from '@whitbread-eos/utils';
import { isDate, format } from 'date-fns';
import { NextRouter } from 'next/router';
import { ControllerRenderProps } from 'react-hook-form';
import { v4 as uuidv4 } from 'uuid';

import { DATE_FORMAT } from '../utils/constants';
import { BookingData, Dependent } from './types';

export interface Nationality {
  value: string;
  label: string;
  image: string;
}

const GERMAN = 'de';

export const fieldProps = (field: any) => {
  return {
    name: field.name,
    onBlur: field.onBlur,
    value: field.value,
    onChange: (value: any) => {
      field.onChange(value);
    },
  };
};

export const boxStyle = {
  px: 'sm',
  py: 'sm',
  pos: 'relative',
  mt: 'xl',
  mb: 'md',
} as BoxProps;

export const labelStyle = {
  h: '1.25rem',
  w: 'fit-content',
  fontSize: 'xl',
  align: 'center',
  px: 'xs',
  fontWeight: 'normal',
  ml: '0.750rem',
  top: '-0.625rem',
  color: 'darkGrey1',
  backgroundColor: 'baseWhite',
  zIndex: '1',
  lineHeight: '1',
  pos: 'absolute',
} as TextProps;

export const gridStyles = {
  w: { base: '100%', md: '50%' },
  flex: '0 0 auto',
  alignItems: 'center',
} as GridProps;

export const inputStyle = {
  height: 'var(--chakra-space-4xl)',
  _placeholder: { opacity: 0.7, color: 'var(--chakra-darkGrey1)' },
  borderColor: 'lightGrey1',
  borderRadius: 'var(--chakra-radii-md)',
} as StyleProps;

export const countryDropdownStyle = {
  menuButtonStyles: { borderColor: 'lightGrey1' },
} as DropdownStyles;

export const inputIconStyles = {
  top: 'var(--chakra-space-sm)',
};

export const renderDropdownStyles = (hasError: boolean) => {
  return {
    border: '1px solid',
    borderColor: hasError ? 'var(--chakra-colors-error)' : 'lightGrey1',
    borderRadius: 'var(--chakra-radii-md)',
  };
};

export const dropdownStyles = {
  menuButtonStyles: {
    borderColor: 'var(--chakra-colors-lightGrey1)',
  },
  menuListStyles: {
    py: 0,
    maxHeight: '12.5rem',
  },
};

export const dataErrorText = {
  alignItems: 'center',
  color: 'var(--chakra-colors-error)',
  fontSize: 'var(--chakra-fontSizes-xs)',
  marginLeft: '15px',
};

export const inputStyles = {
  variant: 'unstyled',
  textOverflow: 'ellipsis',
  border: 'none',
  cursor: 'pointer',
  minH: '40px',
  _focus: {
    border: 'none',
  },
  _placeholder: {
    color: 'var(--chakra-colors-darkGrey1)',
  },
};

export const wrapperStyles = {
  minH: '56px',
  px: '4',
  border: '1px solid',
  borderColor: 'lightGrey1',
  borderRadius: 'md',
};

export const todayLabel = 'Today';
export const tomorrowLabel = 'Tomorrow';

export const contentPadding = { base: '0', md: 'sm' };

export interface FieldsType {
  label: string;
  name: string;
  type: string;
  testId: string;
  disabled?: boolean;
}

export const personalDetailsFields: FieldsType[] = [
  {
    type: 'input',
    name: 'firstName',
    label: 'firstname',
    testId: 'FirstName',
  },
  {
    type: 'input',
    name: 'lastName',
    label: 'lastname',
    testId: 'Surname',
    disabled: true,
  },
  {
    type: 'input',
    name: 'address',
    label: 'address',
    testId: 'Address',
  },
  {
    type: 'input',
    name: 'city',
    label: 'city',
    testId: 'City',
  },
  {
    type: 'input',
    name: 'postalCode',
    label: 'postalcode',
    testId: 'PostalCode',
  },
  {
    type: 'countryDropdown',
    name: 'country',
    label: 'country',
    testId: 'Country',
  },
  {
    type: 'datePicker',
    name: 'dateOfBirth',
    label: 'dateofbirth',
    testId: 'DateOfBirth',
  },
  {
    type: 'autoComplete',
    name: 'nationality',
    label: 'nationality',
    testId: 'Nationality',
  },
  {
    type: 'input',
    name: 'passport',
    label: 'mandatoryPassport',
    testId: 'Passport',
  },
];

export const showPassportField = (values: any, field: string) =>
  values[field]?.value && values[field]?.value !== 'DE';

export const showDependencyPassportField = (values: any, index: number) =>
  values.dependents?.[index] && isExist(values.dependents[index]?.nationality);

const isExist = (fields: { value: string }) => {
  const { value } = fields || {};
  if (value === undefined) return true;
  return value && value?.toLowerCase() === GERMAN;
};

export const modalContentStyles = {
  direction: 'column',
  alignItems: 'center',
  textAlign: 'center',
  mt: 'lg',
  mb: '2xl',
  px: {
    base: '1.5rem',
    xs: '1rem',
    sm: '1.06rem',
    lg: '1.5rem',
    xl: '1.53rem',
  },
} as FlexProps;

export const headingStyles = {
  fontFamily: 'header',
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
  mb: 'lg',
  color: 'darkGrey1',
};

export const subHeadingStyles = {
  fontSize: 'lg',
  fontWeight: 'regular',
  lineHeight: '3',
  mb: 'lg',
  color: 'darkGrey2',
};

export const modalButtonsContainerStyles = {
  w: '100%',
  flexDirection: { base: 'column', sm: 'row' },
  justifyContent: 'center',
  alignItems: 'center',
} as FlexProps;

export const modalButtonsStyles = {
  w: {
    base: 'full',
    xs: '21.43rem',
    sm: '16.18rem',
    md: '15.93rem',
    lg: '16.5rem',
    xl: '17.87rem',
  },
  h: '3.5rem',
  borderRadius: '.25rem',
  p: 'md',
};

export const bookingDetailsFields = [
  {
    type: 'input',
    name: 'bookingNumber',
    label: 'bookingnumber',
    testId: 'bookingNumber',
    styles: { pr: contentPadding, mb: 'md' },
  },
  {
    type: 'input',
    name: 'surname',
    label: 'surname',
    testId: 'surname',
    styles: { pl: contentPadding, mb: 'md' },
  },
  {
    type: 'datePicker',
    name: 'arrivalDate',
    label: 'arrivaldate',
    testId: 'ArrivalDate',
    styles: { pl: contentPadding, mb: 'md' },
  },
];

export const bookDetailGridStyles = {
  w: { base: '100%', md: '31.1875em' },
  flex: '0 0 auto',
  alignItems: 'left',
  pr: {
    base: '0',
    md: '0',
  },
  m: '0',
  p: '0',
  mt: 'xl',
  _first: { mt: '3xl' },
  _last: { mb: '0', pb: '0' },
  borderRadius: 0,
} as GridProps;

export const searchBookingGridStyles = {
  ...bookDetailGridStyles,
  mt: '0',
  _first: { mt: '0' },
  mb: 'xl',
} as GridProps;

export const bookDetailboxStyle = {
  pos: 'relative',
  w: { base: '100%', md: '70.625em' },
} as BoxProps;

interface AccordionContainerStyle {
  w: { base: string; md: string };
  px: string;
  py: string;
  borderWidth: string;
  borderColor: string;
  borderRadius: string;
  mb?: string;
  index: number[];
  allowToggle: boolean;
  _first?: { mb?: string; mt?: string };
  _last?: { mb?: string; mt?: string };
}

interface AccordionPanelStyle {
  padding: number;
  border: number;
}

interface AccordionButtonStyle {
  borderWidth: number;
  borderBottom: number;
  borderTop: number;
  fontSize: string;
  fontWeight: string;
  color: string;
  marginBottom: string;
  padding: string;
  margin: string;
}

interface AccordionItemStyle {
  border: number;
}

export interface AccordionOverwriteStylesType {
  container: AccordionContainerStyle;
  panel: AccordionPanelStyle;
  button: AccordionButtonStyle;
  item: AccordionItemStyle;
  text: any;
}

export interface CountryType {
  nationality?: string;
  countryCode: string;
  flagSrc: string;
}

export const handleAccordionToggle = (
  accordionIndex: number,
  setAccordionIndex: (index: number) => void
) => {
  setAccordionIndex(Number(!accordionIndex));
};

export const accordionOverwriteStyles: AccordionOverwriteStylesType = {
  container: {
    w: { base: '100%', md: '100%' },
    px: 'xl',
    py: 'xl',
    borderWidth: '1px',
    borderColor: 'lightGrey1',
    borderRadius: '4px',
    mb: 'xl',
    _first: { mb: '3xl', mt: '0' },
    index: [0],
    allowToggle: true,
  },
  panel: { padding: 0, border: 0 },
  button: {
    borderWidth: 0,
    borderBottom: 0,
    borderTop: 0,
    fontSize: 'xl',
    fontWeight: 'semibold',
    color: 'baseBlack',
    marginBottom: '0',
    padding: '0',
    margin: '0',
  },
  item: { border: 0 },
  text: { m: 0, p: 0 },
};

export const dependencyFields = [
  {
    type: 'input',
    name: 'firstname',
    label: 'firstname',
    testId: 'FirstName',
  },
  {
    type: 'input',
    name: 'lastname',
    label: 'lastname',
    testId: 'Surname',
  },
  {
    type: 'datePicker',
    name: 'dateofbirth',
    label: 'dateofbirth',
    testId: 'dateofbirth',
  },
  {
    type: 'autoComplete',
    name: 'nationality',
    label: 'nationality',
    testId: 'Nationality',
  },
  {
    type: 'input',
    name: 'passport',
    label: 'mandatoryPassport',
    testId: 'Passport',
  },
];

export const wraperBoxStyle = { px: 'sm', py: 'sm' } as BoxProps;

export const getNationality = (field: ControllerRenderProps, nationalities: any) => {
  return nationalities.find((nationality: Nationality) => nationality?.value === field?.value);
};

export const scrollIntoViewOnError = () => {
  setTimeout(() => {
    const allErrorElements = document?.querySelectorAll('[class*="error"], [style*="error"]');
    const errorDiv = allErrorElements[0];
    errorDiv?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }, 300);
};

export const addScrollEvent = () => {
  const reviewBtn = document.getElementById('reg-form-submit-btn');
  const saveBtn = document.getElementById('reg-form-save-btn');
  if (reviewBtn)
    reviewBtn?.addEventListener('click', () => {
      scrollIntoViewOnError();
    });
  if (saveBtn) saveBtn?.addEventListener('click', () => scrollIntoViewOnError());
};

interface FieldProp {
  value: string;
}

interface Field {
  onChange: (value: any) => void;
}

type HandleSetValueField = (fieldToReset: string, value: string) => void;

export const handleNationalityChange = (
  fieldProp: FieldProp,
  field: Field,
  fieldToReset: string,
  handleSetValue: HandleSetValueField
): void => {
  field.onChange(fieldProp);
  handleSetValue?.(fieldToReset, '');
};

export interface GetPaymentParamsProps {
  billingAddress?: any;
  bookingConfirmation?: BookingConfirmation;
  language: string;
  country: string;
  copyBasketReference: string;
}

export const getPaymentParams = ({
  billingAddress,
  bookingConfirmation,
  language,
  country,
  copyBasketReference,
}: GetPaymentParamsProps) => {
  if (bookingConfirmation) {
    const { hotelId } = bookingConfirmation;
    const billingObj = {
      ...bookingConfirmation.reservationByIdList[0].billing,
      landline: undefined, //for now createPaymentMutation does not accept landline in billing
      address: getBillingAddress({
        countryRouter: country,
        isBillingAddressDisplayed: false,
        billingAddress,
        billing: bookingConfirmation.reservationByIdList[0].billing,
      }),
      differentBillingAddress: false,
      bookerIsNotGuest: false,
    };

    let card;

    const rooms = bookingConfirmation.reservationByIdList.map((room) => ({
      adultsNumber: room?.roomStay?.adultsNumber,
      rate: 'FLEXRATE',
      type: 'DOUBLE',
    }));

    const bookingRequest = {
      businessSite: {
        identifier: hotelId,
        name: bookingConfirmation.hotelName,
        type: 'HOTEL',
        location: hotelId,
      },
      channel: 'PI',
      journey: 'BOOKING',
      language,
      rooms,
      arrivalDate: bookingConfirmation.reservationByIdList[0].roomStay.arrivalDate,
      departureDate: bookingConfirmation.reservationByIdList[0].roomStay.departureDate,
      type: 'PAY_ON_ARRIVAL',
    };

    const businessItemsRequest = {};

    const paymentRequest = {
      billing: billingObj,
      card,
      environment: window.location.origin,
      subType: 'ECOMM',
      type: 'CARD',
      pibaCardPresent: true,
      ...businessItemsRequest,
    };

    const createPaymentCriteria = {
      booking: bookingRequest,
      payment: paymentRequest,
      hotelId: hotelId,
      requestId: uuidv4(),
      tmpBasketRef: `${copyBasketReference}_preCheckIn`,
    };
    return { ...createPaymentCriteria };
  }
};

export const getGuestDetailsObject = (key: GuestDetailKeys, value: string | any, t: any) => {
  const detailsMap = {
    [GuestDetailKeys.firstName]: value,
    [GuestDetailKeys.lastName]: value,
    [GuestDetailKeys.passport]: value,
    [GuestDetailKeys.dateOfBirth]: getFormattedDate(value),
    [GuestDetailKeys.nationality]: value?.label,
  };
  let label = '';
  switch (key) {
    case 'dateofbirth':
      label = t(`precheckin.additionalfields.${key}`);
      break;
    case 'passport':
      label = t(`precheckin.details.${key}`);
      break;
    case 'nationality':
      label = t('precheckin.additionalfields.nationalities');
      break;
    default:
      label = t(`precheckin.regcard.${key}`);
  }
  return { key: label, value: detailsMap[key] || '' };
};

export enum GuestDetailKeys {
  firstName = 'firstname',
  lastName = 'lastname',
  passport = 'passport',
  dateOfBirth = 'dateofbirth',
  nationality = 'nationality',
}

export const getFormattedDate = (date?: Date) => {
  return date && isDate(date) ? format(date, DATE_FORMAT) : '';
};

export const generateDependentsData = (
  nationalities: Nationality[],
  t: any,
  dependents?: Dependent[]
) => {
  const reorderedData = dependents?.map((dependent) => {
    if (dependent?.nationality?.value && dependent.nationality.label === '') {
      const nationality = nationalities.find(({ value }) => value === dependent.nationality.value);
      if (nationality) {
        dependent.nationality.label = nationality.label;
      }
    }

    const { firstname, lastname, dateofbirth, nationality, passport } = dependent;
    return { firstname, lastname, dateofbirth, nationality, passport };
  });
  const dependentGuests = reorderedData || [];
  return dependentGuests
    .map((guest, index) => {
      const guestDetails = Object.entries(guest)
        .filter(([, value]) => value)
        .map(([key, value]) => getGuestDetailsObject(key as GuestDetailKeys, value, t));

      return guestDetails.length
        ? { title: `${t('precheckin.guest')} ${index + 1}`, rows: guestDetails }
        : null;
    })
    .filter(Boolean) as BookingData[];
};

export const onIframeLoad = (time: string) => {
  analytics.update({
    paymentLoadTime: time,
  });
};

export function isCanvasEmpty(canvas: HTMLCanvasElement) {
  const context = canvas.getContext('2d');

  if (context) {
    const imageData = context.getImageData(0, 0, canvas.width, canvas.height);
    const data = imageData.data;

    for (let i = 0; i < data.length; i += 4) {
      if (data[i + 3] !== 0) return false; // Canvas is not empty
    }
    return true; // Canvas is empty
  }
}

export const handleIframeHeight = (isSuccess: boolean) => {
  if (isSuccess) {
    const iframe = document?.getElementById('paymentFrame') as HTMLIFrameElement;
    if (iframe) {
      iframe.addEventListener('load', () => {
        const scrollHeight = iframe.scrollHeight;
        iframe.style.height = `${scrollHeight + scrollHeight / 2 - 60}px`;
      });
    }
  }
};

export const handleRedirect = (path: string, push: NextRouter['push']) => push(path);

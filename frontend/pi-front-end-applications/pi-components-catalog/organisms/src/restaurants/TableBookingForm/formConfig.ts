import {
  FormProps,
  TableBookingDetails,
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
} from '@whitbread-eos/atoms/restaurants';
import { PhoneSelector } from '@whitbread-eos/molecules';
import { formatDataTestId } from '@whitbread-eos/utils';
import { dropdrownManipulatorFn } from '@whitbread-eos/utils/restaurants';

import validateForm from './formValidation';

interface TableBookingFormConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (data: TableBookingDetails) => void;
  baseDataTestId: string;
  formContentData: any;
  isMenuOptionAvailable?: boolean;
  currentLang?: string;
  isAltStyle?: boolean;
}

declare module 'zod' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

export const tabletBookingDetailsFormConfig = ({
  getFormState,
  defaultValues,
  onSubmit,
  baseDataTestId,
  formContentData,
  isMenuOptionAvailable,
  currentLang,
  isAltStyle,
}: TableBookingFormConfigArgsType) => {
  const adultsDropDownOptions = dropdrownManipulatorFn(
    formContentData?.['reservationform.adult.dropdown.values'] ?? ''
  );
  const childrenDropDownOptions = dropdrownManipulatorFn(
    formContentData?.['reservationform.children.dropdown.values'] ?? ''
  );
  const highchairsDropDownOptions = dropdrownManipulatorFn(
    formContentData?.['reservationform.highchair.dropdown.values'] ?? ''
  );

  const { formStepOneValidationSchema, completeFormValidationSchema, enquiryFormValidationSchema } =
    validateForm(formContentData, isMenuOptionAvailable);
  const errorsOrder = [
    'adults',
    'adultsByEnquiry',
    'childrenByEnquiry',
    'selectedSession',
    'firstname',
    'lastname',
    'email',
    'telephoneNumber',
  ];

  const enquiryFormFields = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'adultsByEnquiry',
      label: formContentData?.['reservationform.enquiry.adult.label'],
      props: {
        charLimit: 3,
        type: 'text',
      },
      testid: formatDataTestId(baseDataTestId, 'Adults'),
      styles: { maxW: '100%', mb: '2xl' },
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'childrenByEnquiry',
      label: formContentData?.['reservationform.enquiry.children.label'],
      optionalText: formContentData?.['reservationform.optional.label'],
      optional: true,
      props: {
        charLimit: 3,
        type: 'text',
      },
      testid: formatDataTestId(baseDataTestId, 'Children'),
      styles: { maxW: '100%' },
    },
  ];
  const specialRequest = [
    {
      type: FORM_FIELD_TYPES.DROPDOWN,
      name: 'highchair',
      label: formContentData?.['reservationform.highchair.label'],
      optionalText: formContentData?.['reservationform.optional.label'],
      optional: true,
      dropdownOptions: highchairsDropDownOptions,
      testid: formatDataTestId(baseDataTestId, 'Highchair'),
      styles: { maxW: '100%', mb: '2xl' },
      props: {
        placeholder: formContentData?.['reservationform.highchair.placeholder'],
        showStatusIcon: false,
      },
    },
    {
      type: FORM_FIELD_TYPES.CHECKBOX,
      name: 'wheelchair',
      label: formContentData?.['reservationform.wheelchair.label'],
      optional: true,
      optionalText: formContentData?.['reservationform.optional.label'],
      testid: formatDataTestId(baseDataTestId, 'Wheelchair'),
      styles: { maxW: '100%', mb: '2xl', fontWeight: 'bold' },
    },
    {
      type: FORM_FIELD_TYPES.TEXT_AREA,
      name: 'specialRequest',
      label: formContentData?.['reservationform.other.requirement.label'],
      optionalText: formContentData?.['reservationform.optional.label'],
      charLimit: 200,
      testid: formatDataTestId(baseDataTestId, 'specialRequest'),
      styles: { maxW: '100%', mb: '2xl' },
    },
  ];
  const userDetails = [
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'firstname',
      label: formContentData?.['reservationform.yourdetails.firstname.label'],
      charLimit: 100,
      styles: inputStyle,
      props: {
        charLimit: 100,
        type: 'text',
        isAutoFocused: true,
      },
      className: 'sessioncamhidetext',
      testid: formatDataTestId(baseDataTestId, 'FirstName'),
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'lastname',
      label: formContentData?.['reservationform.yourdetails.lastname.label'],
      styles: inputStyle,
      props: {
        charLimit: 100,
        type: 'text',
      },
      className: 'sessioncamhidetext',
      testid: formatDataTestId(baseDataTestId, 'LastName'),
    },
    {
      type: FORM_FIELD_TYPES.INPUT_TEXT,
      name: 'emailAddress',
      label: formContentData?.['reservationform.yourdetails.email.label'],
      styles: inputStyle,
      props: {
        charLimit: 100,
        type: 'text',
      },
      className: 'sessioncamhidetext',
      testid: formatDataTestId(baseDataTestId, 'Email'),
    },
    {
      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
      name: 'telephoneNumber',
      label: formContentData?.['reservationform.yourdetails.contact.label'],
      Component: PhoneSelector,
      props: {
        showIcon: false,
        currentLang,
        className: 'sessioncamhidetext',
        isAltStyle,
      },
      testid: formatDataTestId(baseDataTestId, 'Mobile'),
    },
    // Commented out as per phase 1 restaurants requirements
    // {
    //   type: FORM_FIELD_TYPES.CHECKBOX,
    //   name: 'consent',
    //   label: replaceInputPlaceholder(
    //     formContentData[`reservationform.marketing.checkbox.label`],
    //     restaurantBrandNameForMarketing
    //   ),
    //   testid: formatDataTestId(baseDataTestId, 'checkBox1'),
    //   styles: inputStyle,
    // },
    {
      type: FORM_FIELD_TYPES.CHECKBOX,
      name: 'privacyStatement',
      label: formContentData?.['reservationform.policystatement.checkbox.label'],
      privactStatementLinkText:
        formContentData?.['reservationform.policystatement.checkbox.label.linkname'],
      testid: formatDataTestId(baseDataTestId, 'checkBox2'),
      styles: inputStyle,
      isPrivacStatement: true,
    },
  ];

  const config = {
    id: 'TableBookingForm',
    elements: {
      fields: [
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'adults',
          label: formContentData?.['reservationform.adult.label'],
          dropdownOptions: adultsDropDownOptions,
          testid: formatDataTestId(baseDataTestId, 'AdultForBooking'),
          styles: { maxW: '100%' },
          props: {
            placeholder: formContentData?.['reservationform.adult.placeholder'],
            showStatusIcon: false,
          },
        },
        {
          type: FORM_FIELD_TYPES.DROPDOWN,
          name: 'children',
          label: formContentData?.['reservationform.children.label'],
          optionalText: formContentData?.['reservationform.optional.label'],
          optional: true,
          dropdownOptions: childrenDropDownOptions,
          testid: formatDataTestId(baseDataTestId, 'Child'),
          styles: { maxW: '100%', my: '2xl' },
          props: {
            placeholder: formContentData?.['reservationform.children.placeholder'],
            showStatusIcon: false,
          },
        },
        {
          name: 'enquryFormFields',
          type: FORM_FIELD_TYPES.ENQUIRY_FORM_FIELDS,
          label: '',
          styles: { maxW: '100%', my: '2xl' },
          testid: formatDataTestId(baseDataTestId, 'enquiryDetials'),
          relatedFields: enquiryFormFields,
        },
        {
          type: FORM_FIELD_TYPES.SINGLE_DATE_PICKER,
          name: 'date',
          label: formContentData?.['reservationform.date.label'],
          testid: formatDataTestId(baseDataTestId, 'SelectedDate'),
          props: {
            isRightIcon: true,
            isDisabled: false,
          },
          styles: { w: '100%', mb: '2xl' },
        },
        {
          type: FORM_FIELD_TYPES.SESSION_TABS,
          name: 'time',
          tabLabel: formContentData?.['reservationform.sessionstarttime.label'],
          tapLabel: formContentData?.['reservationform.sessionTime.label'],
          testid: formatDataTestId(baseDataTestId, 'SessionTabs'),
          styles: { maxW: '100%', mb: '2xl' },
        },
        {
          type: FORM_FIELD_TYPES.MENUDROPDOWN,
          name: 'menuId',
          label: formContentData?.['reservationform.menu.label'],
          testid: formatDataTestId(baseDataTestId, 'Menu'),
          styles: { maxW: '100%', mb: '2xl' },
          props: {
            placeholder: formContentData?.['reservationform.menu.placeholder'],
            showStatusIcon: false,
          },
        },
        {
          type: FORM_FIELD_TYPES.SWITCH,
          name: 'additionalRequirement',
          label: formContentData?.['reservationform.additional.requirement.label'],
          testid: formatDataTestId(baseDataTestId, 'AdditionalRequirement'),
          styles: { maxW: '100%', mb: '2xl' },
          relatedFields: specialRequest,
        },
        {
          name: 'userDetails',
          type: FORM_FIELD_TYPES.USER_DETAILS,
          label: formContentData?.['reservationform.yourdetails.heading'],
          relatedFields: userDetails,
        },
      ],
      buttons: [
        {
          type: FORM_BUTTON_TYPES.SUBMIT,
          label: [
            formContentData?.['reservationform.continue.button.label'],
            formContentData?.['reservationform.enquiry.button.label'],
            formContentData?.['reservationform.book.button.label'],
          ],
          action: onSubmit,
          testid: formatDataTestId(baseDataTestId, 'Submit'),
          props: {
            variant: 'primary',
            size: 'md',
          },
        },
      ],
    },
    errorsOrder,
    defaultValues,
    validationSchema: [
      formStepOneValidationSchema,
      completeFormValidationSchema,
      enquiryFormValidationSchema,
    ],
    getFormState,
  } as FormProps;

  return config;
};

const inputStyle = {
  w: '100%',
  // W: {
  //   mobile: '100%',
  //   sm: '100%',
  //   md: '100%',
  //   lg: '100%',
  //   xl: '100%',
  // },
};

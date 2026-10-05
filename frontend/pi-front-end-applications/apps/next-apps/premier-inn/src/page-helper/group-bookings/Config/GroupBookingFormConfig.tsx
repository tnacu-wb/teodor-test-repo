import { Heading, type TextProps, type BoxProps, Text, Box, StyleProps } from '@chakra-ui/react';
import type { DatepickerRangeSelectionDate } from '@whitbread-eos/api';
import { HotelBrand } from '@whitbread-eos/api';
import {
  FORM_BUTTON_TYPES,
  FORM_FIELD_TYPES,
  FormProps,
  FormWithAccordianProps,
  Notification,
  Alert,
  AutocompleteFormField,
  DatePickerFormField,
} from '@whitbread-eos/atoms';
import { PhoneSelector } from '@whitbread-eos/molecules';
import {
  formatDataTestId,
  formatGuestTitleOptions,
  formatTextWithSpace,
  renderSanitizedHtml,
} from '@whitbread-eos/utils';
import { useState } from 'react';
import type { UseFormTrigger, UseFormSetValue } from 'react-hook-form';

import RoomCounter from '../RoomCounter';
import RoomTotalCounter from '../RoomTotalCounter';
import validateForm from './formValidation';

declare module 'yup' {
  interface ArraySchema<T> {
    unique(a: string, message: string): ArraySchema<T>;
  }
}

export const BREAKFAST = 'BREAKFAST';
export const MEALDEAL = 'MEALDEAL';

type ContactType = {
  firstName: string;
  lastName: string;
  title: string;
  phoneNumber: string;
  emailAddress: string;
};

type BookingType = {
  BookerType: string;
  companyName: string;
  purposeOfStay: string;
  reasonForVisit: string;
  hotels: string;
  datepicker: DatepickerRangeSelectionDate;
  reasonForVisitOther?: string;
};

type AutocompleteHotelType = {
  value: string;
  component: string;
};

interface GroupBookingContactDetailsConfigArgsType {
  getFormState: FormProps['getFormState'];
  defaultValues: FormProps['defaultValues'];
  onSubmit: (value: any) => void;
  baseTestId: string;
  t: (id: string) => string;
  currentLang: string | undefined;
  handleAccordionToggle: (value: any) => void;
  openNextAccordian: () => void;
  accordionIndex: number;
  hotels: AutocompleteHotelType[];
  islessThanMinCount: boolean;
  createGroupBookingLoading: boolean;
  createGroupBookingError: boolean;
}

export const GroupBookingFormConfig = ({
  getFormState,
  defaultValues,
  openNextAccordian,
  onSubmit,
  baseTestId,
  t,
  currentLang,
  accordionIndex,
  handleAccordionToggle,
  hotels,
  islessThanMinCount,
  createGroupBookingLoading,
  createGroupBookingError,
}: GroupBookingContactDetailsConfigArgsType) => {
  const [selectedHotelBrandId, setSelectedHotelBrandId] = useState<string>('');
  const formatOptionsWithDelimiter = (options = '', delimiter = ',') => {
    const words = String(options).replace(/'/g, '').split(`${delimiter}`);

    return words.map((option: string) => {
      return {
        value: formatTextWithSpace(option.trim()),
        label: option,
        id: option,
        testid: formatDataTestId(baseTestId, formatTextWithSpace(option.trim())),
      };
    });
  };
  const [hideCompanyName, setHideCompanyNameFlag] = useState(true);
  const [hideReasonForVisit, setHideReasonForVisitFlag] = useState(true);
  const [reasonForVisitOther, setReasonForVisitOther] = useState(false);
  const [isSchoolYouthEnabled, setIsSchoolYouthEnabled] = useState(false);
  const [isTravelWithKidsEnabled, setIsTravelWithKidsEnabled] = useState(false);
  const [isCommentsTextNearMax, setIsCommentsTextNearMax] = useState(false);

  const titleOptions = formatGuestTitleOptions(
    t('groupBooking.contactDetails.yourContact.nameTitles.options')
  );

  const hotelsPlaceholder = t('groupBooking.bookingDetails.bookingDetails.hotelSelection');

  const breakfastOption = {
    value: BREAKFAST,
    label: t('groupBooking.bookingDetails.packageType.breakfast'),
    testid: formatDataTestId(baseTestId, 'packageType-Breakfast'),
  };
  const mealDealOption = {
    value: MEALDEAL,
    label: t('groupBooking.bookingDetails.packageType.mealdeal'),
    testid: formatDataTestId(baseTestId, 'packageType-Mealdeal'),
  };

  const handleCommentsChange = (event: any) => {
    if (event.target.value.length >= 995) {
      setIsCommentsTextNearMax(true);
    } else {
      setIsCommentsTextNearMax(false);
    }
  };
  // use brand id (e.g. PI, PID to hide / show meals options per de/gb hotels
  const handleSelectOption = (item: { originalValue: { brand: string } }) => {
    setSelectedHotelBrandId(item?.originalValue?.brand);
  };
  const isGermanHotel = () => selectedHotelBrandId !== HotelBrand.PID;

  const { formValidationSchema } = validateForm({
    t,
    currentLang,
    isSchoolYouthEnabled,
    hideCompanyName,
    hideReasonForVisit,
    reasonForVisitOther,
  });

  const config = {
    id: 'GroupBookingFormConfig',
    accordians: [
      {
        title: t('groupBooking.contactDetails.title'),
        onToggleSection: () => handleAccordionToggle(0),
        elements: {
          fields: [
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'yourContactDetails',
              content: (
                <Heading as="h3" {...sectionTitleStyle}>
                  {t('groupBooking.contactDetails.yourContact.title')}
                </Heading>
              ),
            },
            {
              type: FORM_FIELD_TYPES.DROPDOWN,
              name: 'title',
              dropdownOptions: titleOptions,
              testid: formatDataTestId(baseTestId, 'Title'),
              styles: { maxW: '8.5rem', mt: '2xl' },
              label: t('groupBooking.contactDetails.yourContact.nameTitles.placeholder'),
            },
            {
              type: FORM_FIELD_TYPES.INPUT_TEXT,
              name: 'firstName',
              label: t('groupBooking.contactDetails.yourContact.firstName'),
              testid: formatDataTestId(baseTestId, 'firstName'),
              styles: { ...inputStyle, marginBottom: 'lg' },
              props: {
                className: 'sessioncamhidetext',
              },
            },
            {
              type: FORM_FIELD_TYPES.INPUT_TEXT,
              name: 'lastName',
              label: t('groupBooking.contactDetails.yourContact.lastName'),
              testid: formatDataTestId(baseTestId, 'lastName'),
              styles: { ...inputStyle, marginBottom: 'lg' },
              props: {
                className: 'sessioncamhidetext',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'phoneNumber',
              label: t('groupBooking.contactDetails.yourContact.phoneNumber'),
              Component: PhoneSelector,
              styles: {
                ...inputStyle,
              },
              props: {
                showIcon: false,
                currentLang,
                className: 'sessioncamhidetext',
              },
              testid: formatDataTestId(baseTestId, 'Mobile'),
            },
            {
              type: FORM_FIELD_TYPES.INPUT_EMAIL,
              name: 'emailAddress',
              label: t('groupBooking.contactDetails.yourContact.emailAddress'),
              testid: formatDataTestId(baseTestId, 'emailAddress'),
              props: {
                showIcon: false,
                className: 'sessioncamhidetext',
              },
              styles: inputStyle,
            },
            {
              type: FORM_BUTTON_TYPES.BUTTON,
              label: t('groupBooking.contactDetails.buttonLabel'),
              triggerFields: [],
              action: async (trigger: UseFormTrigger<ContactType>) => {
                const isValid = await trigger([
                  'firstName',
                  'lastName',
                  'title',
                  'phoneNumber',
                  'emailAddress',
                ]);
                if (isValid) {
                  openNextAccordian();
                }
              },
              props: {
                variant: 'primary',
                size: 'full',
              },
              styles: { ...continueTextStyle, ...continueButtonSectionStyle },
              testid: formatDataTestId(baseTestId, 'ContactDetailsContinue'),
            },
          ],
          buttons: [],
        },
      },
      {
        title: t('groupBooking.bookingDetails.title'),
        onToggleSection: () => handleAccordionToggle(1),
        elements: {
          fields: [
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'bookerTypeTitle',
              content: (
                <Text {...sectionTitleStyle}>
                  {t('groupBooking.bookingDetails.bookingType.title')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.RADIO_GROUP,
              name: 'BookerType',
              options: formatOptionsWithDelimiter(
                t('groupBooking.bookingDetails.bookingType.options'),
                ','
              ),
              testid: formatDataTestId(baseTestId, 'BookerType'),
              styles: { mt: 'md', pt: 'xs', cursor: 'default' },
              onChangeAction: (
                value: string,
                handleSetValue: UseFormSetValue<{ [key: string]: string | number | object }>
              ) => {
                handleSetValue('companyName', '');
              },
              onChange: (value: string) => {
                if (value === 'Personal' || value === 'Privat') setHideCompanyNameFlag(true);
                else setHideCompanyNameFlag(false);
              },
              errorStyles,
            },
            {
              type: FORM_FIELD_TYPES.INPUT_TEXT,
              name: 'companyName',
              label: t('groupBooking.bookingDetails.companyName'),
              testid: formatDataTestId(baseTestId, 'companyName'),
              styles: { mt: 'md', pt: 'xs' },
              hidden: hideCompanyName,
              props: {
                className: 'sessioncamhidetext',
              },
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'purposeTitle',
              content: (
                <Text {...sectionTitleStyle}>
                  {t('groupBooking.bookingDetails.purposeOfStay.title')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.RADIO_GROUP,
              name: 'purposeOfStay',
              options: formatOptionsWithDelimiter(
                t('groupBooking.bookingDetails.purposeOfStay.options'),
                ','
              ),
              testid: formatDataTestId(baseTestId, 'Purpose'),
              styles: { mt: 'md', pt: 'xs', cursor: 'default' },
              errorStyles,
            },
            {
              type: FORM_FIELD_TYPES.CHECKBOX,
              name: 'isSchoolOrYouth',
              styles: {
                ...inputStyle,
              },
              label: t('groupBooking.bookingDetails.purposeOfStay.isSchoolOrYouthGroup'),
              testid: formatDataTestId(baseTestId, 'isSchoolOrYouth'),
              onChange: (value: boolean) => {
                setIsSchoolYouthEnabled(value);
              },
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'reasonForVisitTitle',
              content: (
                <Text {...sectionTitleStyle}>
                  {t('groupBooking.bookingDetails.reasonForVisit.title')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.DROPDOWN,
              name: 'reasonForVisit',
              dropdownOptions: formatOptionsWithDelimiter(
                t('groupBooking.bookingDetails.reasonForVisit.options'),
                ','
              ),
              testid: formatDataTestId(baseTestId, 'reasonForVisit'),
              styles: { ...reasonForVisitStyles },
              props: {
                placeholder: t('groupBooking.bookingDetails.reasonForVisit.placeholder'),
              },
              onChangeAction: (
                value: string,
                handleSetValue: UseFormSetValue<{ [key: string]: string | number | object }>
              ) => {
                handleSetValue('reasonForVisitOther', '');
              },
              onChange: (value: string) => {
                if (value === t('groupBooking.bookingDetails.reasonForVisit.other.label'))
                  setHideReasonForVisitFlag(false);
                else setHideReasonForVisitFlag(true);
              },
              errorStyles,
            },
            {
              type: FORM_FIELD_TYPES.INPUT_TEXT,
              name: 'reasonForVisitOther',
              label: t('groupBooking.bookingDetails.reasonForVisit.other.placeholder'),
              testid: formatDataTestId(baseTestId, 'reasonForVisitOther'),
              styles: { mt: 'md', pt: 'xs' },
              hidden: hideReasonForVisit,
              onChange: (value: string) => {
                if (value === '') setReasonForVisitOther(false);
                else setReasonForVisitOther(true);
              },
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'bookingDetailsTitle',
              content: (
                <Text {...sectionTitleStyle}>
                  {t('groupBooking.bookingDetails.bookingDetails.title')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'bookingDetailsDescription',
              content: (
                <Text fontSize="md">
                  {t('groupBooking.bookingDetails.bookingDetails.description')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'hotels',
              Component: AutocompleteFormField,
              testid: formatDataTestId(baseTestId, 'hotels'),
              styles: {
                mt: 'md',
                pt: 'xs',
                sx: {
                  'div:first-child > span + div': {
                    mb: '2xl',
                  },
                },
              },
              props: {
                placeholder: hotelsPlaceholder,
                hotels,
                handleSelectOption,
              },
              errorStyles,
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'datepicker',
              Component: DatePickerFormField,
              styles: {
                ...inputStyle,
              },
              props: {
                labels: {
                  resetButtonLabel: t('groupBooking.bookingDetails.bookingDetails.resetButton'),
                  doneButtonLabel: t('groupBooking.bookingDetails.bookingDetails.doneButton'),
                  todayLabel: t('groupBooking.bookingDetails.bookingDetails.today'),
                  tomorrowLabel: t('groupBooking.bookingDetails.bookingDetails.tomorrow'),
                  checkoutLabel: t('groupBooking.bookingDetails.bookingDetails.checkOut'),
                  checkInLabel: t('groupBooking.bookingDetails.bookingDetails.checkIn'),
                },
                locale: currentLang,
              },
              testid: formatDataTestId(baseTestId, 'Datepicker'),
              errorStyles,
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'packageTypeTitle',
              content: (
                <Text {...sectionTitleStyle}>
                  {t('groupBooking.bookingDetails.packageType.title')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'packageTypeDescription',
              content: (
                <Text fontSize="md">
                  {t('groupBooking.bookingDetails.packageType.description')}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.RADIO_GROUP,
              name: 'packageType',
              options: [breakfastOption, mealDealOption],
              testid: formatDataTestId(baseTestId, 'packageType'),
              styles: { mt: 'md', pt: 'xs', cursor: 'default' },
              hidden: !isGermanHotel(),
            },
            {
              type: FORM_FIELD_TYPES.CHECKBOX,
              name: 'packageTypeCheckbox',
              styles: {
                ...inputStyle,
                mt: 'md',
              },
              label: t('groupBooking.bookingDetails.packageType.breakfast'),
              testid: formatDataTestId(baseTestId, 'packageType-Breakfast-checkbox'),
              hidden: isGermanHotel(),
              props: {
                defaultChecked: true,
              },
            },
            {
              type: FORM_BUTTON_TYPES.BUTTON,
              label: t('groupBooking.bookingDetails.buttonLabel'),
              action: async (trigger: UseFormTrigger<BookingType>) => {
                const isValid = await trigger([
                  'BookerType',
                  'companyName',
                  'purposeOfStay',
                  'reasonForVisit',
                  'hotels',
                  'datepicker',
                  'reasonForVisitOther',
                ]);
                if (isValid) {
                  openNextAccordian();
                }
              },
              props: {
                variant: 'primary',
                size: 'full',
              },
              styles: { ...continueTextStyle, ...continueButtonSectionStyle },
              testid: formatDataTestId(baseTestId, 'BookingDetailsContinue'),
            },
          ],
          buttons: [],
        },
      },
      {
        title: t('groupBooking.roomRequirements.title'),
        onToggleSection: () => handleAccordionToggle(2),
        elements: {
          fields: [
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'roomRequirementsHeading',
              content: (
                <Heading as="h3" {...sectionTitleStyle}>
                  {t('groupBooking.roomRequirements.rooms.title')}
                </Heading>
              ),
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'roomRequirementsDescription',
              content: <Text>{t('groupBooking.roomRequirements.type.description')}</Text>,
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'roomRequirementsRoomTypes',
              content: (
                <Text
                  className="roomTypesLink"
                  mt="sm"
                  sx={{
                    a: { color: 'darkGrey3', textDecoration: 'underline' },
                  }}
                >
                  {renderSanitizedHtml(t('groupBooking.roomRequirements.type.button.label'))}
                </Text>
              ),
            },
            {
              type: FORM_FIELD_TYPES.CHECKBOX,
              name: 'isTravellingWithChild',
              label: t('groupBooking.roomRequirements.rooms.travellingWithChild'),
              styles: {
                ...inputStyle,
                mt: 'md',
                mb: '0',
              },
              testid: formatDataTestId(baseTestId, 'isTravellingWithChild'),
              hidden: isSchoolYouthEnabled,
              onChange: (value: boolean) => {
                setIsTravelWithKidsEnabled(value);
              },
            },
            {
              type: FORM_FIELD_TYPES.CHECKBOX,
              name: 'isAccessibleRoom',
              label: t('groupBooking.roomRequirements.rooms.accessibleRoomRequired'),
              styles: {
                ...inputStyle,
                mt: 'sm',
                mb: 'md',
              },
              testid: formatDataTestId(baseTestId, 'isAccessibleRoom'),
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'atLeastOneAdultNotification',
              content: (
                <Box
                  mt="xs"
                  mb="lg"
                  data-testid={formatDataTestId(baseTestId, 'atLeastOneAdultNotification')}
                >
                  <Notification
                    description={t(
                      'groupBooking.bookingDetails.purposeOfStay.isSchoolOrYouthGroup.required'
                    )}
                    maxWidth="full"
                    status="warning"
                    variant="alert"
                    svg={<Alert />}
                    style={{ maxWidth: 'none' }}
                    isInnerHTML
                    sx={{ a: { color: 'darkGrey3', textDecoration: 'underline' } }}
                  />
                </Box>
              ),
              hidden: !isTravelWithKidsEnabled && !isSchoolYouthEnabled,
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'minimumRoomsNotification',
              content: (
                <Box
                  mt="xs"
                  mb="lg"
                  data-testid={formatDataTestId(baseTestId, 'minTenRoomsNotification')}
                >
                  <Notification
                    description={t('groupBooking.roomRequirements.rooms.notification')}
                    maxWidth="full"
                    status="warning"
                    variant="error"
                    svg={<Alert />}
                    style={{ maxWidth: 'none' }}
                    isInnerHTML
                    sx={{
                      a: { color: 'darkGrey3', textDecoration: 'underline' },
                    }}
                  />
                </Box>
              ),
              hidden: !islessThanMinCount,
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'singleOccupancy',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'singleOccupancy'),
              props: {
                roomType: 'singleOccupancy',
                roomLabel: t('groupBooking.roomRequirements.type.single.title'),
                roomOccupancy: t('groupBooking.roomRequirements.type.single.subtitle'),
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'doubleOccupancy',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'doubleOccupancy'),
              props: {
                roomType: 'doubleOccupancy',
                roomLabel: t('groupBooking.roomRequirements.type.double.title'),
                roomOccupancy: t('groupBooking.roomRequirements.type.double.subtitle'),
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'twinRooms',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'twinRooms'),
              props: {
                roomType: 'twinRooms',
                roomLabel: t('groupBooking.roomRequirements.type.twin.title'),
                roomOccupancy: t('groupBooking.roomRequirements.type.twin.subtitle'),
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'familyOf21A1C',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'familyOf21A1C'),
              props: {
                roomType: 'familyOf21A1C',
                roomLabel: t('groupBooking.roomRequirements.rooms.familyOfTwo.title'),
                roomOccupancy: t('groupBooking.roomRequirements.rooms.familyOfTwo.subtitle'),
                hidden: 'isTravellingWithChild',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'familyOf32A1C',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'familyOf32A1C'),
              props: {
                roomType: 'familyOf32A1C',
                roomLabel: t('groupBooking.roomRequirements.rooms.familyOfTwoPlusOne.title'),
                roomOccupancy: t('groupBooking.roomRequirements.rooms.familyOfTwoPlusOne.subtitle'),
                hidden: 'isTravellingWithChild',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'familyOf31A2C',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'familyOf31A2C'),
              props: {
                roomType: 'familyOf31A2C',
                roomLabel: t('groupBooking.roomRequirements.rooms.familyOfOnePlusTwo.title'),
                roomOccupancy: t('groupBooking.roomRequirements.rooms.familyOfOnePlusTwo.subtitle'),
                hidden: 'isTravellingWithChild',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'familyOf42A2C',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'familyOf42A2C'),
              props: {
                roomType: 'familyOf42A2C',
                roomLabel: t('groupBooking.roomRequirements.rooms.familyOfFour.title'),
                roomOccupancy: t('groupBooking.roomRequirements.rooms.familyOfFour.subtitle'),
                hidden: 'isTravellingWithChild',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'accessibleSingle',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'accessibleSingle'),
              props: {
                roomType: 'accessibleSingle',
                roomLabel: t('groupBooking.roomRequirements.type.accessibleSingle.title'),
                roomOccupancy: t('groupBooking.roomRequirements.type.accessibleSingle.subtitle'),
                hidden: 'isAccessibleRoom',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'accessibleDouble',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'accessibleDouble'),
              props: {
                roomType: 'accessibleDouble',
                roomLabel: t('groupBooking.roomRequirements.type.accessibleDouble.title'),
                roomOccupancy: t('groupBooking.roomRequirements.type.accessibleDouble.subtitle'),
                hidden: 'isAccessibleRoom',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'accessibleTwin',
              Component: RoomCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'accessibleTwin'),
              props: {
                roomType: 'accessibleTwin',
                roomLabel: t('groupBooking.roomRequirements.type.accessibleTwin.title'),
                roomOccupancy: t('groupBooking.roomRequirements.type.accessibleTwin.subtitle'),
                hidden: 'isAccessibleRoom',
              },
            },
            {
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              name: 'RoomTotalCount',
              Component: RoomTotalCounter,
              styles: { marginBottom: '0' },
              testid: formatDataTestId(baseTestId, 'accessibleTwin'),
              props: {
                title: t('groupBooking.roomRequirements.total.title'),
                totalCountStyle,
                totalStyles,
                roomLabel: t('groupBooking.roomRequirements.total.rooms'),
              },
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'roomRequirementsAdditionalInfoHeading',
              content: (
                <Heading as="h3" {...sectionTitleStyle}>
                  {isSchoolYouthEnabled
                    ? t('groupBooking.roomRequirements.additionalInfo.title.required')
                    : t('groupBooking.roomRequirements.additionalInfo.title')}
                </Heading>
              ),
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'roomRequirementsAdditionalInfo',
              content: (
                <Box
                  className="formatLinks"
                  sx={{
                    'p:not(:first-child)': { paddingTop: 'md' },
                  }}
                >
                  {renderSanitizedHtml(
                    isSchoolYouthEnabled
                      ? t(
                          'groupBooking.roomRequirements.additionalInfo.description.isSchoolOrYouthGroup'
                        )
                      : t('groupBooking.roomRequirements.additionalInfo.description')
                  )}
                </Box>
              ),
            },
            {
              type: FORM_FIELD_TYPES.TEXTAREA,
              testid: formatDataTestId(baseTestId, 'comments'),
              name: 'comments',
              label: t('groupBooking.roomRequirements.additionalInfo.comments'),
              styles: {
                ...inputStyle,
                mt: 'lg',
                mb: 'md',
              },
              onChange: () => handleCommentsChange(event),
              props: {
                className: 'sessioncamhidetext',
                maxLength: 1000,
              },
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              testid: formatDataTestId(baseTestId, 'commentsWarning'),
              name: 'commentsWarning',
              hidden: !isCommentsTextNearMax,
              content: (
                <Box role="alert" aria-live="polite" {...warningStyles}>
                  <em>{t('groupBooking.roomRequirements.additionalInfo.comments.max')}</em>
                </Box>
              ),
            },
            {
              type: FORM_BUTTON_TYPES.SUBMIT,
              label: t('groupBooking.submit.button.label'),
              action: onSubmit,
              props: {
                variant: 'primary',
                size: 'full',
                isDisabled: createGroupBookingLoading,
              },
              styles: {
                ...continueTextStyle,
                ...continueButtonSectionStyle,
              },
              testid: formatDataTestId(baseTestId, 'RoomRequirementsSubmit'),
            },
            {
              type: FORM_FIELD_TYPES.NON_FIELD_CONTENT,
              name: 'createGroupBookingErrorNotification',
              content: (
                <Box
                  mt="md"
                  data-testid={formatDataTestId(baseTestId, 'createGroupBookingErrorNotification')}
                >
                  <Notification
                    description={t('groupBooking.confirmation.error')}
                    maxWidth="full"
                    status="warning"
                    variant="error"
                    svg={<Alert />}
                    style={{ maxWidth: 'none' }}
                    isInnerHTML
                    sx={{
                      a: { color: 'darkGrey3', textDecoration: 'underline' },
                    }}
                  />
                </Box>
              ),
              hidden: !createGroupBookingError,
            },
          ],
          buttons: [],
        },
      },
    ],
    defaultValues,
    validationSchema: formValidationSchema,
    getFormState,
    accordionIndex,
    handleAccordionToggle,
  } as FormWithAccordianProps;

  return config;
};

const inputStyle = {
  width: '100%',
};

const warningStyles = {
  padding: '.25rem 1rem',
  fontWeight: 500,
};

const sectionTitleStyle = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
  mt: 'lg',
} as TextProps;

const continueButtonSectionStyle = {
  width: { mobile: 'full', lg: '26.25rem' },
  mt: '2xl',
} as BoxProps;

const continueTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
} as TextProps;

const totalStyles = {
  borderTop: '1px solid var(--chakra-colors-lightGrey2)',
  pt: 'md',
  pr: 'xl',
  sx: {
    strong: {
      ':first-child': {
        fontWeight: '500',
        paddingRight: '2rem',
        fontSize: 'xl',
      },

      fontWeight: 'semibold',
      fontSize: '2xl',
    },
  },
};

const totalCountStyle = {
  width: '7.5rem',
  display: 'inline-block',
};

const reasonForVisitStyles = {
  mt: 'md',
  pt: 'xs',
  sx: {
    'div:first-child': {
      'span .chakra-menu__menu-list': {
        maxHeight: '420px',
        overflowX: 'scroll',
      },
    },
  },
} as StyleProps;

const errorStyles = {
  textStyle: { sx: { textWrap: 'wrap' } },
  containerStyle: {
    sx: {
      height: { base: 'var(--chakra-space-2xl)', sm: 'var(--chakra-space-lg)' },
    },
  },
};

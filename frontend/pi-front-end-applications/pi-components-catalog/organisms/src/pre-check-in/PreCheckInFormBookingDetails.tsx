import { Flex, Text, TextProps } from '@chakra-ui/react';
import { BookingDetails } from '@whitbread-eos/molecules';
import { useTranslation } from 'next-i18next';
import { Control, FieldErrors, UseFormGetValues } from 'react-hook-form';

function PreCheckInFormBookingDetails({
  control,
  formField,
  errors,
  getValues,
}: Readonly<PreCheckInFormBookingDetailsProps>) {
  const { t } = useTranslation();
  return (
    <Flex direction="column" gap="xl">
      <Text {...headingStyles}>{t('precheckin.bookingdetails')}</Text>
      <Text {...subHeadingStyles}>{t('precheckin.bookingdetails.description')}</Text>
      <BookingDetails
        formField={formField}
        control={control}
        errors={errors}
        getValues={getValues}
      />
    </Flex>
  );
}

export default PreCheckInFormBookingDetails;

interface FormField {
  name: string;
  label: string;
  type?: string;
}

interface PreCheckInFormBookingDetailsProps {
  control: Control<any>;
  formField: FormField;
  errors: FieldErrors<any>;
  getValues: UseFormGetValues<any>;
  bookingReferenceError?: boolean;
}

const subHeadingStyles = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: 3,
  color: 'darkGrey2',
} as TextProps;

const headingStyles = {
  fontFamily: 'header',
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: 3,
  color: 'darkGrey1',
} as TextProps;

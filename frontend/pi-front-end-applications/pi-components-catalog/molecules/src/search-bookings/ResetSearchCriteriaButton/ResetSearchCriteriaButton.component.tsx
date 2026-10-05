import { BoxProps, Flex, Text, TextProps } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { FormDynamicFieldCompProps } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';

export default function ResetSearchCriteriaButton({
  handleSetValue,
  handleResetField,
  formField,
}: Readonly<FormDynamicFieldCompProps>) {
  const testId = formField.testid ?? 'Reset';

  const queryClient = useQueryClient();

  const handleClick = () => {
    if (!handleResetField || !handleSetValue) {
      return;
    }

    handleResetField(`bookingReference`, { defaultValue: '' });
    handleResetField(`bookerLastName`, { defaultValue: '' });
    handleResetField(`arrivalDate`, { defaultValue: '' });
    handleResetField(`guestLastName`, { defaultValue: '' });
    handleResetField('hotelDetails', { defaultValue: { name: '', code: '' } });
    handleResetField('hotelLocation', { defaultValue: '' });
    handleResetField(`cancellationDate`, { defaultValue: '' });
    handleResetField(`companyName`, { defaultValue: '' });
    handleResetField(`bookerPostcode`, { defaultValue: '' });
    handleResetField(`bookerEmail`, { defaultValue: '' });
    handleResetField(`bookerPhone`, { defaultValue: '' });
    handleResetField(`thirdPartyBookingReferenceNumber`, { defaultValue: '' });

    const { setClearHotelLocation, setClearHotelName } = formField?.props?.clearHotelFields ?? {};
    setClearHotelLocation?.(true);
    setClearHotelName?.(true);

    const { setClearPhoneField } = formField?.props ?? {};
    setClearPhoneField?.(true);

    queryClient.cancelQueries({ queryKey: ['GetSearchBookingsResults'] });
    queryClient.invalidateQueries({ queryKey: ['GetSearchBookingsResults'], refetchType: 'none' });

    formField?.props?.action();
  };

  return (
    <Flex
      onClick={() => handleClick()}
      {...buttonWrapper}
      data-testid={formatDataTestId(testId, 'Button')}
    >
      <Text {...textStyle}>{formField.label}</Text>
    </Flex>
  );
}

const buttonWrapper = {
  alignItems: 'center',
  justifyContent: 'center',
  cursor: 'pointer',
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mt: 'lg',
  _focus: {
    boxShadow: 'none',
    textDecoration: 'underline',
  },
  _hover: {
    textDecoration: 'underline',
  },
} as BoxProps;

const textStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'btnSecondaryEnabled',
} as TextProps;

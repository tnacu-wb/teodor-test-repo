import { Box, BoxProps, Flex, TextProps } from '@chakra-ui/react';
import { Button } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';
import { useWatch } from 'react-hook-form';

export default function BookingsSubmitButton({ formField, getValues, control, errors }: any) {
  const fields = getValues();
  const checkErrors = Object.keys(errors).length;
  const [disabled, setDisabled] = useState(true);
  const bookingReference = useWatch({ name: 'bookingReference', control });
  const bookerLastName = useWatch({ name: 'bookerLastName', control });
  const arrivalDate = useWatch({ name: 'arrivalDate', control });
  const guestLastName = useWatch({ name: 'guestLastName', control });
  const bookerPostcode = useWatch({ name: 'bookerPostcode', control });
  const hotelDetails = useWatch({ name: 'hotelDetails', control });
  const bookerEmail = useWatch({ name: 'bookerEmail', control });
  const bookerPhone = useWatch({ name: 'bookerPhone', control });
  const cancellationDate = useWatch({ name: 'cancellationDate', control });
  const companyName = useWatch({ name: 'companyName', control });
  const thirdPartyBookingReferenceNumber = useWatch({
    name: 'thirdPartyBookingReferenceNumber',
    control,
  });
  const testId = formField.testid || 'Submit';

  function isEmpty(formFields: any) {
    const formFieldsCopy = { ...formFields, hotelId: hotelDetails.code };

    delete formFieldsCopy.extendedSearchCriteria;
    delete formFieldsCopy.bookingsSubmitButton;
    delete formFieldsCopy.resetSearchCriteriaButton;
    delete formFieldsCopy.hotelLocation;
    delete formFieldsCopy.hotelDetails;

    return Object.values(formFieldsCopy).every((x) => !x);
  }

  function isFilled(formFields: any): boolean {
    const formFieldsCopy = { ...formFields, hotelId: hotelDetails.code };

    delete formFieldsCopy.extendedSearchCriteria;
    delete formFieldsCopy.bookingsSubmitButton;
    delete formFieldsCopy.resetSearchCriteriaButton;
    delete formFieldsCopy.hotelLocation;
    delete formFieldsCopy.hotelDetails;

    return Object.values(formFieldsCopy).filter((x) => x).length > 2;
  }

  function validateEnhancedSearch() {
    const minRequiredFieldsFilled = isFilled(fields);
    const hasRefFieldFilled = fields?.bookingReference || fields?.thirdPartyBookingReferenceNumber;
    const isRequiredFieldsFilled = hasRefFieldFilled || minRequiredFieldsFilled;

    if ((!isRequiredFieldsFilled || checkErrors > 0) && !disabled) {
      setDisabled(true);
    } else if (isRequiredFieldsFilled && disabled && checkErrors === 0) {
      setDisabled(false);
    }
  }

  function validateSearch() {
    const checkEmpty = isEmpty(fields);
    if ((checkEmpty === true || checkErrors > 0) && disabled !== true) {
      setDisabled(true);
    } else if (checkEmpty === false && disabled === true && checkErrors === 0) {
      setDisabled(false);
    }
  }

  useEffect(() => {
    formField.props.enhancedSearch ? validateEnhancedSearch() : validateSearch();
  }, [
    bookingReference,
    bookerLastName,
    arrivalDate,
    guestLastName,
    bookerPostcode,
    hotelDetails,
    bookerEmail,
    bookerPhone,
    cancellationDate,
    companyName,
    thirdPartyBookingReferenceNumber,
    checkErrors,
    formField.props.enhancedSearch,
  ]);

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const { action, ...extraPropsFields } = formField.props;
  return (
    <Flex>
      <Box>
        <Button
          type="submit"
          data-testid={formatDataTestId(testId, 'Button')}
          {...extraPropsFields}
          isDisabled={disabled}
          {...searchButtonStyle}
          {...searchBtnTextStyle}
          onClick={formField.props.action}
        >
          {formField.label}
        </Button>
      </Box>
    </Flex>
  );
}

const searchButtonStyle = {
  w: {
    lg: '24.5rem',
    xl: '26.25rem',
  },
  mb: 'sm',
  backgroundColor: 'var(--chakra-colors-btnSecondaryEnabled)',
} as BoxProps;

const searchBtnTextStyle = {
  fontWeight: 'semibold',
  fontSize: 'lg',
  lineHeight: '3',
  color: 'baseWhite',
} as TextProps;

import { Box, Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  FIND_BOOKING,
  FIND_BOOKING_COOKIE_NAME_KEY,
  FIND_BOOKING_SOURCE_PMS,
  FindBookingCriteria,
  HeaderInformationData,
  Query,
} from '@whitbread-eos/api';
import { Error, Form, FormProps, ModalVariants, Notification } from '@whitbread-eos/atoms';
import {
  cleanupFindBookingToken,
  graphQLRequest,
  isStringValid,
  setCookie,
  useCustomLocale,
} from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { SetStateAction, useCallback, useState } from 'react';

import { manageBookingFormConfig } from './manageBookingFormConfig';

interface Props {
  labels: HeaderInformationData;
  isOpen: boolean;
  onClose: () => void;
  baseTestId: string;
}

export default function ManageBookingModal({
  labels,
  onClose,
  isOpen,
  baseTestId,
}: Readonly<Props>) {
  const { language, country } = useCustomLocale();
  const [isSubmitDisabled, setIsSubmitDisabled] = useState(false);
  const [resetForm, setResetForm] = useState(0);
  const [findBookingError, setFindBookingError] = useState('');
  const [defaultValues, setDefaultValues] = useState<FormProps['defaultValues']>({
    bookingReference: '',
    bookingSurname: '',
    arrivalDate: new Date().toString(),
  });

  const onFindBookingSuccess = (response: Query, findBookingCriteria: FindBookingCriteria) => {
    if (!response?.findBooking) {
      return;
    }
    setIsSubmitDisabled(false);

    const {
      ref,
      basketReference,
      cookieName,
      token,
      sourcePms,
      redirectBase,
      minutesTillExpiry,
      operaConfNumber,
    } = response.findBooking;
    if (
      (sourcePms === FIND_BOOKING_SOURCE_PMS.OPERA && ref && basketReference) ||
      (sourcePms === FIND_BOOKING_SOURCE_PMS.BART && ref)
    ) {
      const cookieValue =
        sourcePms === FIND_BOOKING_SOURCE_PMS.OPERA
          ? {
              token,
              basketReference,
              bookingReference: ref,
              operaConfNumber: operaConfNumber ?? '',
            }
          : { arrivalDate: findBookingCriteria.arrivalDate, surname: findBookingCriteria.lastName };
      if (typeof window !== 'undefined') {
        window.localStorage.setItem(FIND_BOOKING_COOKIE_NAME_KEY, cookieName ?? '');
      }
      setCookie(
        cookieName ?? '',
        window.btoa(JSON.stringify(cookieValue)),
        Number(minutesTillExpiry)
      );

      if (isStringValid(redirectBase)) {
        window.location.href = `${redirectBase}?bookingReference=${ref}`;
      }
      onCloseModal();
    } else {
      setFindBookingError(labels?.headerInformation?.form?.bookingInvalid ?? '');
    }
  };

  const onFindBookingError = () => {
    setIsSubmitDisabled(false);
    setFindBookingError(labels?.headerInformation?.form?.searchBookingError ?? '');
  };

  const onSubmit = (data: FormProps['defaultValues']) => {
    cleanupFindBookingToken();
    const queryClient = new QueryClient();
    setIsSubmitDisabled(true);

    const findBookingCriteria: FindBookingCriteria = {
      country,
      language,
      arrivalDate: format(new Date(data.arrivalDate as string), 'yyyy-MM-dd'),
      lastName: data.bookingSurname as string,
      resNo: String(data.bookingReference).toUpperCase(),
    };

    queryClient
      .fetchQuery({
        queryKey: [
          'FindBooking',
          findBookingCriteria.country,
          findBookingCriteria.language,
          findBookingCriteria.arrivalDate,
          findBookingCriteria.lastName,
          findBookingCriteria.resNo,
        ],
        queryFn: () => graphQLRequest(FIND_BOOKING, { findBookingCriteria }),
      })
      .then((response: Query) => onFindBookingSuccess(response, findBookingCriteria))
      .catch(onFindBookingError);
  };

  const getFormState: FormProps['getFormState'] = useCallback(
    (state: any) => {
      setDefaultValues(state as SetStateAction<FormProps['defaultValues']>);
    },
    [setDefaultValues]
  );

  const onCloseModal = () => {
    setResetForm((prev) => prev + 1);
    setFindBookingError('');
    onClose();
  };

  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={onCloseModal}
      dataTestId={baseTestId}
      variant="default"
      variantProps={{
        title: '',
        delimiter: true,
        overflowVisible: true,
        isCentered: false,
      }}
    >
      <Flex {...wrapperStyles}>
        <Text as="h4" {...textStyles.title} data-testid={`${baseTestId}_Title`}>
          {labels?.headerInformation?.form?.findBookingTitle}
        </Text>
        <Text as="h6" {...textStyles.description} data-testid={`${baseTestId}_Description`} mb="xl">
          {labels?.headerInformation?.form?.findBookingDescription}
        </Text>
        {isStringValid(findBookingError) && (
          <Box mb="xl">
            <Notification
              svg={<Error />}
              status="error"
              description={findBookingError}
              variant="error"
              data-testid={`${baseTestId}-Error-Message`}
              maxW="full"
            />
          </Box>
        )}
        <Form
          {...manageBookingFormConfig({
            getFormState,
            defaultValues,
            onSubmit,
            baseTestId,
            resetForm,
            labels,
            isSubmitDisabled,
          })}
        />
      </Flex>
    </ModalVariants>
  );
}

const textStyles = {
  title: {
    textAlign: 'center',
    fontWeight: 'semibold',
    fontSize: 'xl',
    lineHeight: '3',
  } as TextProps,
  description: {
    textAlign: 'center',
    pt: 'md',
    fontWeight: 'normal',
    fontSize: 'md',
    lineHeight: '3',
  } as TextProps,
};

const wrapperStyles = {
  alignItems: 'center',
  justifyContent: 'center',
  direction: 'column',
  width: { sm: '22.063rem', md: '21.75rem', lg: '24.5rem', xl: '26.25rem' },
  mt: 'lg',
  mb: 'md',
  mx: { mobile: 'md', sm: 'lg', md: '8xl' },
} as FlexProps;

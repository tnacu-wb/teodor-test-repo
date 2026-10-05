import { Box, BoxProps, Divider, Flex, Heading, HeadingProps } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { format, parse } from 'date-fns';

import { IdvCheckbox } from './components';

type IdvCheckboxItemType = {
  value: string;
  partOfSearch: boolean;
};

export type BookingInformationType = {
  reservationNumber: IdvCheckboxItemType;
  hotelName: string;
  arrivalDate: string;
  departureDate: string;
  emailAddress: string;
};

type IDVCardPropsType = {
  bookingInformation: BookingInformationType;
  t: (id: string) => string;
  inputValues: any;
  baseDataTestId: string;
};

const BookingInformation = ({
  bookingInformation,
  t,
  inputValues,
  baseDataTestId,
}: IDVCardPropsType) => (
  <Box {...cardStyle}>
    <Flex flexDir="column">
      <Heading {...headingStyle} data-testid={formatDataTestId(baseDataTestId, 'Heading')}>
        {t('ccui.idv.bookingInformation.heading')}
      </Heading>
      <Divider mt="1rem" mb="2rem"></Divider>
      <IdvCheckbox
        value={bookingInformation.reservationNumber.value}
        partOfSearch={inputValues.bookingReference ? inputValues.bookingReference !== '' : false}
        text={t('ccui.idv.bookingInformation.reservationNumber')}
        dataTestId={formatDataTestId(baseDataTestId, 'ReservationNumber')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={bookingInformation.hotelName}
        partOfSearch={inputValues.hotelName ? inputValues.hotelName !== '' : false}
        text={t('ccui.idv.bookingInformation.hotelName')}
        dataTestId={formatDataTestId(baseDataTestId, 'HotelName')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={format(
          parse(bookingInformation.arrivalDate, 'yyyy-MM-dd', new Date()),
          'dd/MM/yyyy'
        )}
        partOfSearch={inputValues.arrivalDate ? inputValues.arrivalDate !== '' : false}
        text={t('ccui.idv.bookingInformation.arrivalDate')}
        dataTestId={formatDataTestId(baseDataTestId, 'ArrivalDate')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={format(
          parse(bookingInformation.departureDate, 'yyyy-MM-dd', new Date()),
          'dd/MM/yyyy'
        )}
        partOfSearch={inputValues.departureDate ? inputValues.departureDate !== '' : false}
        text={t('ccui.idv.bookingInformation.departureDate')}
        dataTestId={formatDataTestId(baseDataTestId, 'DepartureDate')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={bookingInformation.emailAddress}
        partOfSearch={inputValues.bookerEmail ? inputValues.bookerEmail !== '' : false}
        text={t('ccui.idv.bookingInformation.emailAddress')}
        dataTestId={formatDataTestId(baseDataTestId, 'EmailAddress')}
      ></IdvCheckbox>
    </Flex>
  </Box>
);

const cardStyle = {
  w: { mobile: 'full', xs: 'full', sm: 'full', md: 'full', lg: '311px', xl: '333px' },
  border: '1px solid var(--chakra-colors-lightGrey4)',
  p: '1.5rem',
  mr: { md: '18px', lg: '20px' },
} as BoxProps;

const headingStyle = {
  color: 'darkGrey2',
  fontSize: '1.25rem',
  lineHeight: '1.5rem',
  fontWeight: '700',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as HeadingProps;

export default BookingInformation;

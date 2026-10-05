import { Box, BoxProps, Divider, Flex, Heading, HeadingProps } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';

import { IdvCheckbox } from './components';

export type PersonalInformationType = {
  bookerName: string;
  guestName: string;
  address: string;
  postcode: string;
  telephoneNumber: string;
  cardUsedToMakeBooking: string;
};

type IDVCardPropsType = {
  personalInformation: PersonalInformationType;
  t: (id: string) => string;
  inputValues: any;
  baseDataTestId: string;
};

const PersonalInformation = ({
  personalInformation,
  t,
  inputValues,
  baseDataTestId,
}: IDVCardPropsType) => (
  <Box {...cardStyle}>
    <Flex flexDir="column">
      <Heading {...headingStyle} data-testid={formatDataTestId(baseDataTestId, 'Heading')}>
        {t('ccui.idv.personalInformation.heading')}
      </Heading>
      <Divider mt="1rem" mb="2rem"></Divider>
      <IdvCheckbox
        value={personalInformation.bookerName}
        partOfSearch={inputValues.bookerLastName ? inputValues.bookerLastName !== '' : false}
        text={t('ccui.idv.personalInformation.bookerName')}
        dataTestId={formatDataTestId(baseDataTestId, 'BookerName')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={personalInformation.guestName}
        partOfSearch={inputValues.guestLastName ? inputValues.guestLastName !== '' : false}
        text={t('ccui.idv.personalInformation.guestName')}
        dataTestId={formatDataTestId(baseDataTestId, 'GuestName')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={personalInformation.address}
        partOfSearch={inputValues.bookerAddress ? inputValues.bookerAddress !== '' : false}
        text={t('ccui.idv.personalInformation.address')}
        dataTestId={formatDataTestId(baseDataTestId, 'Address')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={personalInformation.postcode}
        partOfSearch={inputValues.bookerPostcode ? inputValues.bookerPostcode !== '' : false}
        text={t('ccui.idv.personalInformation.postcode')}
        dataTestId={formatDataTestId(baseDataTestId, 'Postcode')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={personalInformation.telephoneNumber}
        partOfSearch={inputValues.bookerPhone ? inputValues.bookerPhone !== '' : false}
        text={t('ccui.idv.personalInformation.telephoneNumber')}
        dataTestId={formatDataTestId(baseDataTestId, 'TelephoneNumber')}
      ></IdvCheckbox>
      <Divider my="1rem"></Divider>
      <IdvCheckbox
        value={personalInformation.cardUsedToMakeBooking}
        partOfSearch={inputValues.bookerCard ? inputValues.bookerLastName !== '' : false}
        text={t('ccui.idv.personalInformation.cardUsedToMakeBooking')}
        dataTestId={formatDataTestId(baseDataTestId, 'CardUsedToMakeBooking')}
      ></IdvCheckbox>
    </Flex>
  </Box>
);

const cardStyle = {
  w: { mobile: 'full', xs: 'full', sm: 'full', md: 'full', lg: '311px', xl: '333px' },
  border: '1px solid var(--chakra-colors-lightGrey4)',
  p: '1.5rem',
  mr: { md: '17px', lg: '19px' },
} as BoxProps;

const headingStyle = {
  color: 'darkGrey2',
  fontSize: '1.25rem',
  lineHeight: '1.5rem',
  fontWeight: '700',
  fontFamily: 'Proxima Nova Sans, helvetica, arial, sans-serif',
} as HeadingProps;

export default PersonalInformation;

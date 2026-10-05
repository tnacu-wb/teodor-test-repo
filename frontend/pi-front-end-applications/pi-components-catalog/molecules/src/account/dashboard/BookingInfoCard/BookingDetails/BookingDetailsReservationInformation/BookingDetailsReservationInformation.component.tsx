import type { TextProps } from '@chakra-ui/react';
import { Flex, Text } from '@chakra-ui/react';
import { PurposeOfStay, GuaranteeCodes } from '@whitbread-eos/api';
import { Badge } from '@whitbread-eos/atoms';
import { formatDataTestId } from '@whitbread-eos/utils';
import upperFirst from 'lodash/upperFirst';

export interface Props {
  sourcePms: string;
  rateType: string;
  isBart: boolean;
  baseDataTestId: string;
  t: (id: string) => string;
  gdsReferenceNumber?: any;
  distBookingChannel?: any;
  reasonForStay?: string;
  isReadOnly?: boolean;
  paymentOption: string;
  paymentMethod?: string;
}

export default function BookingDetailsReservationInformationComponent({
  baseDataTestId,
  sourcePms,
  rateType,
  isBart,
  t,
  gdsReferenceNumber,
  distBookingChannel,
  reasonForStay,
  isReadOnly,
  paymentOption,
  paymentMethod,
}: Readonly<Props>) {
  const badgeColorByPMS = isBart ? 'zipSecondary' : 'primary';

  // Get the payment type text based on the payment option - which here has been based on the guarantee code
  const paymentTypeText = getPaymentTypeText(paymentOption, paymentMethod, t);

  return (
    <Flex
      direction="column"
      mb="lg"
      data-testid={formatDataTestId(baseDataTestId, 'BartBookingdDetailsReservationInformation')}
    >
      <Flex>
        <Badge variant="primary" badgecolor={badgeColorByPMS} {...getBadgeStyles(isBart)}>
          {upperFirst(sourcePms.toLowerCase())}
        </Badge>
      </Flex>
      <Flex justifyContent="space-between" mt="lg">
        <Text {...getLabelTextStyle(isReadOnly)}>
          {t('ccui.manageBooking.typeOfBooking')}
          <Text as="span" fontWeight="normal" color="darkGrey2">
            {`: ${
              reasonForStay === PurposeOfStay.BUSINESS
                ? t('booking.reason.business')
                : t('booking.reason.leisure')
            }`}
          </Text>
        </Text>
      </Flex>
      {distBookingChannel && (
        <Flex justifyContent="space-between">
          <Text {...labelStyle}>
            {t('ccui.manageBooking.bookingChannel')}
            <Text as="span" fontWeight="normal">
              {`: ${upperFirst(distBookingChannel.toLowerCase())}`}
            </Text>
          </Text>
        </Flex>
      )}
      <Flex justifyContent="space-between">
        <Text {...getLabelTextStyle(isReadOnly)}>
          {t('ccui.manageBooking.rateType')}
          <Text as="span" fontWeight="normal" color="darkGrey2">
            {`: ${upperFirst(rateType.toLowerCase())}`}
          </Text>
        </Text>
      </Flex>
      {gdsReferenceNumber && (
        <Flex justifyContent="space-between">
          <Text {...labelStyle}>
            {t('ccui.manageBooking.3rdPartyBookingReference')}
            <Text as="span" fontWeight="normal">
              {`: ${upperFirst(gdsReferenceNumber.toLowerCase())}`}
            </Text>
          </Text>
        </Flex>
      )}
      <Flex justifyContent="space-between">
        <Text {...getLabelTextStyle(isReadOnly)}>
          {t('ccui.manageBooking.paymentType.label')}
          <Text as="span" fontWeight="normal" color="darkGrey2">
            {`: ${paymentTypeText}`}
          </Text>
        </Text>
      </Flex>
    </Flex>
  );
}

export const getPaymentTypeText = (
  paymentType: string,
  paymentMethod: string | undefined,
  t: (id: string) => string
) => {
  const creditDebitCodes = ['VA', 'CVA', 'MC', 'DMC', 'AX', 'DVA'];

  if (paymentType === GuaranteeCodes.ACCOUNT_TO_COMPANY) {
    return t('ccui.manageBooking.paymentType.accountToCompany');
  }
  if (paymentType === GuaranteeCodes.RESERVE_WITHOUT_CARD) {
    return t('ccui.manageBooking.paymentType.nonGuaranteed');
  }
  if (paymentMethod && ['BU', 'BD'].includes(paymentMethod)) {
    return t('ccui.manageBooking.paymentType.PIBA');
  }
  if (paymentMethod && ['PP', 'DPP'].includes(paymentMethod)) {
    return t('ccui.manageBooking.paymentType.Paypal');
  }
  if (paymentMethod && creditDebitCodes.includes(paymentMethod)) {
    return t('ccui.manageBooking.paymentType.creditDebit');
  }
  return t('ccui.manageBooking.paymentType.other');
};

const getBadgeStyles = (isBartReservation: boolean) => {
  return {
    px: isBartReservation ? '1rem' : '0.5rem',
  };
};
const getLabelTextStyle = (isReadOnly?: boolean) =>
  isReadOnly
    ? {
        color: 'darkGrey1',
        fontSize: 'lg',
        lineHeight: '3',
        fontWeight: 'semibold',
        w: { mobile: '7.25rem', sm: 'full' },
      }
    : labelStyle;

const labelStyle = {
  fontWeight: 'semibold',
  fontSize: 'md',
  lineHeight: '3',
  color: 'darkGrey1',
  as: 'span',
} as TextProps;

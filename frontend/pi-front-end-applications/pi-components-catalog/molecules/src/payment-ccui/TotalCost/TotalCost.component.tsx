import type { BoxProps, FlexProps, HeadingProps, TextProps } from '@chakra-ui/react';
import { Box, Button, Flex, Heading, RadioGroup, Text } from '@chakra-ui/react';
import { EmailConfirmation, AMEND_A2C_PAYMENT_PATH } from '@whitbread-eos/api';
import { FORM_VALIDATIONS, Input, RadioButton } from '@whitbread-eos/atoms';
import { formatCurrency, formatPrice, getAmendSectionTranslations } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import React, { Dispatch, SetStateAction, useEffect, useState } from 'react';
import * as yup from 'yup';

import RoomRatePolicies from '../RoomRatePolicies';

interface Props {
  discount?: number;
  totalCost: number;
  hotelName: string;
  hotelId: string;
  hotelBrand: string;
  currencyCode?: string;
  language?: string;
  disabledOption?: string;
  country: string;
  isSectionVisible?: boolean;
  hasAllChecks?: boolean;
  setSendEmail: Dispatch<SetStateAction<boolean>>;
  isCompWithoutEckoh?: boolean;
  onConfirmClick?: () => void;
  continueWithPayment?: () => void;
  isDiscountApplied?: boolean;
  previousTotalCost: number;
  sendEmail?: boolean;
  emailSection: {
    emailAddress: string | undefined;
    setEmailAddress: Dispatch<SetStateAction<string | undefined>>;
    emailError: string | undefined;
    setEmailError: Dispatch<SetStateAction<string>>;
  };
  isFromChangePaymentBIC?: boolean;
}

export default function TotalCost({
  discount = 0,
  totalCost,
  hotelName,
  hotelId,
  hotelBrand,
  currencyCode = 'GBP',
  language = 'en',
  disabledOption = '',
  country,
  isSectionVisible = true,
  hasAllChecks,
  setSendEmail,
  isCompWithoutEckoh,
  onConfirmClick,
  isDiscountApplied,
  previousTotalCost,
  emailSection,
  sendEmail,
  continueWithPayment,
  isFromChangePaymentBIC = false,
}: Readonly<Props>) {
  const [isRoomRatePoliciesChecked, setIsRoomRatePoliciesChecked] = useState<boolean>(false);
  const hasEmailAddress = emailSection?.emailAddress ? !emailSection?.emailError : true;
  const isButtonEnabled = !sendEmail
    ? isRoomRatePoliciesChecked && hasAllChecks && hasEmailAddress
    : isRoomRatePoliciesChecked &&
      hasAllChecks &&
      sendEmail &&
      emailSection?.emailAddress &&
      !emailSection?.emailError;
  const [typeOfConfirmation, setTypeOfConfirmation] = useState<string>(
    EmailConfirmation.SEND_EMAIL
  );
  const router = useRouter();
  useEffect(() => {
    if (disabledOption === EmailConfirmation.SEND_EMAIL) {
      setTypeOfConfirmation(EmailConfirmation.NO_SEND_EMAIL);
      setSendEmail(false);
    }

    if (disabledOption === EmailConfirmation.NO_SEND_EMAIL) {
      setTypeOfConfirmation(EmailConfirmation.SEND_EMAIL);
      setSendEmail(true);
    }
  }, [disabledOption]);

  useEffect(() => {
    if (!sendEmail && !emailSection?.emailAddress) {
      emailSection?.setEmailError('');
    }
  }, [sendEmail]);

  useEffect(() => {
    if (
      isCompWithoutEckoh &&
      router.pathname === AMEND_A2C_PAYMENT_PATH &&
      !isFromChangePaymentBIC
    ) {
      setIsRoomRatePoliciesChecked(true);
    }
  }, [isCompWithoutEckoh]);

  const { t } = useTranslation(['common']);

  const yupSchema = getYupSchema(
    sendEmail,
    t('config.errorMessages.spf.email.invalid'),
    t('booking.login.required.text')
  );

  const { bookingSummaryLabels } = getAmendSectionTranslations(t);

  if (!isSectionVisible) return <></>;

  return (
    <Flex {...wrapperStyles} data-testid="totalCostSection">
      {isDiscountApplied && renderDiscountSection(discount)}
      <Flex
        {...totalCostSectionStyles.wrapper}
        data-testid={`totalCostSection_${isDiscountApplied ? 'new-total-cost' : 'total-cost'}`}
      >
        <Flex
          {...textWrapperStyles}
          mb={1}
          data-testid={`totalCostSection_${
            isDiscountApplied ? 'new-total-cost-title' : 'total-cost-title'
          }`}
        >
          <Heading
            as="h6"
            {...totalCostSectionStyles.title}
            data-testid={`totalCostSection_${
              isDiscountApplied ? 'new-total-cost-title-heading' : 'total-cost-title-heading'
            }`}
          >
            {isDiscountApplied
              ? t('ccui.payment.confirmBooking.newTotalCost')
              : t('booking.summary.totalPrice')}
          </Heading>
        </Flex>
        <Flex {...textWrapperStyles} mb={2} h={8} data-testid="totalCostSection_total-cost-price">
          <Heading
            as="h2"
            {...totalCostSectionStyles.amount}
            data-testid="totalCostSection_total-cost-heading"
          >
            {renderPrice(totalCost, currencyCode)}
          </Heading>
        </Flex>
        <Flex {...textWrapperStyles} data-testid="totalCostSection_total-cost-hotel-name">
          <Heading
            as="h5"
            {...totalCostSectionStyles.hotel}
            data-testid="totalCostSection_total-cost-hotel-name-heading"
          >
            {hotelName}
          </Heading>
        </Flex>
      </Flex>
      <Box {...policiesWrapperStyles} data-testid="totalCostSection_room-rate-policies-wrapper">
        <RoomRatePolicies
          isRoomRatePoliciesChecked={isRoomRatePoliciesChecked}
          onChange={() => setIsRoomRatePoliciesChecked(!isRoomRatePoliciesChecked)}
          language={language}
          country={country}
          hotelId={hotelId}
          hotelBrand={hotelBrand}
        />
      </Box>
      <Box mb={8} data-testid="totalCostSection_options">
        <RadioGroup
          value={typeOfConfirmation}
          onChange={(option) => {
            setTypeOfConfirmation(option);
            if (option === EmailConfirmation.SEND_EMAIL) {
              setSendEmail(true);
            } else {
              setSendEmail(false);
            }
          }}
          data-testid="totalCostSection_radio-group"
        >
          <RadioButton
            value={EmailConfirmation.SEND_EMAIL}
            variant="borderless"
            mb={6}
            data-testid="totalCostSection_send-mail"
            isDisabled={EmailConfirmation.SEND_EMAIL === disabledOption}
            type={EmailConfirmation.SEND_EMAIL || ''}
          >
            <Text
              data-testid="totalCostSection_text-send-mail"
              fontWeight={EmailConfirmation.SEND_EMAIL === typeOfConfirmation ? '600' : '400'}
            >
              {t('ccui.payment.confirmBooking.textSendMail')}
            </Text>
          </RadioButton>
          <RadioButton
            value={EmailConfirmation.NO_SEND_EMAIL}
            variant="borderless"
            data-testid="totalCostSection_no-send-mail"
            isDisabled={EmailConfirmation.NO_SEND_EMAIL === disabledOption}
            type={EmailConfirmation.NO_SEND_EMAIL || ''}
          >
            <Text
              data-testid="totalCostSection_text-no-send-mail"
              fontWeight={EmailConfirmation.NO_SEND_EMAIL === typeOfConfirmation ? '600' : '400'}
            >
              {t('ccui.payment.confirmBooking.textNoMail')}
            </Text>
          </RadioButton>
        </RadioGroup>
      </Box>
      <Box
        mb={8}
        w={{
          mobile: 'full',
          xs: 'full',
          sm: '25.063rem',
          md: '26.25rem',
          lg: '24.5rem',
          xl: '26.25rem',
        }}
      >
        <Input
          name="emailAddress"
          value={emailSection?.emailAddress}
          onChange={(email) => {
            emailSection?.setEmailAddress(email);
            emailSection?.setEmailError('');
            yupSchema.validate({ email: email }).catch((err) => {
              emailSection?.setEmailError(err?.errors?.[0]);
            });
          }}
          isInputAriaRequired={sendEmail}
          type="email"
          placeholderText={
            sendEmail
              ? t('ccui.payment.emailAddress.placeholder.required')
              : t('ccui.payment.emailAddress.placeholder')
          }
          label={
            sendEmail
              ? t('ccui.payment.emailAddress.label')
              : t('ccui.idv.bookingInformation.emailAddress')
          }
          error={emailSection?.emailError}
        />
      </Box>
      {isCompWithoutEckoh ? (
        <Button
          w={{
            mobile: 'full',
            xs: 'full',
            sm: '25.063rem',
            md: '18rem',
            lg: '18rem',
            xl: '19.313rem',
          }}
          variant="primary"
          onClick={onConfirmClick}
          isDisabled={!isButtonEnabled}
          data-testid="totalCostSection_confirm-booking-total-cost"
        >
          {isFromChangePaymentBIC
            ? bookingSummaryLabels.confirmChangesLabel
            : t('ccui.payment.confirmBooking.button')}
        </Button>
      ) : (
        <Button
          form="billingAddressForm"
          w={{
            mobile: 'full',
            xs: 'full',
            sm: '25.063rem',
            md: '18.5rem',
            lg: '18rem',
            xl: '19.313rem',
          }}
          type="submit"
          onClick={continueWithPayment}
          variant="primary"
          isDisabled={!isButtonEnabled}
          data-testid="totalCostSection_confirm-booking-total-cost"
        >
          {isFromChangePaymentBIC
            ? bookingSummaryLabels.confirmChangesLabel
            : t('ccui.payment.confirmBooking.button')}
        </Button>
      )}
    </Flex>
  );

  function getYupSchema(
    isRequired: boolean | undefined,
    invalidEmailError: string,
    requiredError: string
  ) {
    return isRequired
      ? yup.object().shape({
          email: yup
            .string()
            .email(invalidEmailError)
            .max(FORM_VALIDATIONS.EMAIL.MAX, invalidEmailError)
            .required(requiredError),
        })
      : yup.object().shape({
          email: yup
            .string()
            .email(invalidEmailError)
            .max(FORM_VALIDATIONS.EMAIL.MAX, invalidEmailError),
        });
  }

  function renderDiscountSection(discount: number) {
    return (
      <Box data-testid="totalCostSection_discount" w={64} mb={8}>
        <Text as="p" {...discountSectionStyles.title}>
          {`${t('ccui.payment.confirmBooking.discount.title')} `}
          <Text as="span" fontWeight={700}>
            {renderPrice(discount, currencyCode)}
          </Text>
        </Text>
        <Text as="p" {...discountSectionStyles.previousTotalCost}>
          {t('ccui.payment.confirmBooking.discount.previousTotalCostMessage')}
        </Text>
        <Text as="p" {...discountSectionStyles.previousTotalCostValue}>
          {renderPrice(previousTotalCost, currencyCode)}
        </Text>
      </Box>
    );
  }

  function renderPrice(amount: number, currencyCode: string) {
    const formattedAmount = amount.toFixed(2) as unknown as number;
    return formatPrice(formatCurrency(currencyCode), formattedAmount, language);
  }
}

const policiesWrapperStyles = {
  overflow: 'hidden',
  w: 'full',
  maxW: 'full',
  mb: 6,
} as BoxProps;

const textWrapperStyles = {
  w: 'full',
  h: 6,
  alignItems: 'center',
} as FlexProps;

const wrapperStyles = {
  boxShadow: '0px 0px 12px var(--chakra-colors-lightGrey4)',
  mt: 16,
  alignItems: 'flex-start',
  flexDirection: 'column',
  p: 8,
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '54.5rem',
    lg: '54rem',
    xl: '50.5rem',
  },
} as FlexProps;

const discountSectionStyles = {
  title: {
    fontSize: 'xs',
    lineHeight: 2,
    fontWeight: 'normal',
    color: 'darkGrey1',
    h: 5,
  } as TextProps,
  previousTotalCost: {
    fontSize: 'md',
    lineHeight: 3,
    fontWeight: 'normal',
    color: 'lightGrey1',
    textDecorationLine: 'line-through',
    w: '13rem',
    h: '1.188rem',
  } as TextProps,
  previousTotalCostValue: {
    fontSize: 'md',
    lineHeight: 3,
    fontWeight: 'normal',
    color: 'lightGrey1',
    textDecorationLine: 'line-through',
    w: 20,
    h: '1.063rem',
  } as TextProps,
};

const totalCostSectionStyles = {
  wrapper: {
    flexDir: 'column',
    w: 'full',
    mb: 6,
  } as FlexProps,
  title: {
    fontSize: 'md',
    lineHeight: 3,
    fontWeight: 'semibold',
    color: 'darkGrey1',
    w: 'full',
  } as HeadingProps,
  amount: {
    fontSize: '3xl',
    lineHeight: 4,
    fontWeight: 'bold',
    color: 'darkGrey1',
    w: 'full',
  } as HeadingProps,
  hotel: {
    fontSize: 'lg',
    lineHeight: 3,
    fontWeight: 'semibold',
    color: 'darkGrey1',
    w: 'full',
  } as HeadingProps,
};

import {
  Box,
  Button,
  StyleProps,
  Text,
  Link,
  Flex,
  FlexProps,
  ButtonProps,
  LinkProps,
} from '@chakra-ui/react';
import {
  Area,
  CONFIRM_AMEND_STATUS,
  INITIAL_CONFIRM_AMEND_ERROR_KEY,
  INITIAL_CONFIRM_AMEND_ERROR_VALUE,
  AMEND_DEPOSIT_FOLIOS_EXCEPTION_VALUE,
  AMEND_CONFIRM_EXCEPTION_VALUE,
  AMEND_REFUND_EXCEPTION_VALUE,
  AMEND_REVERT_EXCEPTION_VALUE,
  FT_PI_AUTH0_LOGIN,
} from '@whitbread-eos/api';
import type { AmendConfirmationErrorLS } from '@whitbread-eos/api';
import { Notification, Success, Error, FailConfirmation } from '@whitbread-eos/atoms';
import {
  analytics,
  useUserData,
  useCustomLocale,
  getSecureTwoURL,
  useSessionStorage,
  useFeatureToggle,
  useAuth0Navigation,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useState, type MouseEvent } from 'react';

import { AuthContentManagerPIVariant } from '../../../authentication';
import { BookingInfoCard } from '../BookingInfoCard';
import BookingInfoCardHeader from '../BookingInfoCardHeader';

export interface BookingInfoCardProps {
  bookingReference: string;
  basketReference: string | null;
  tempBookingReference?: string;
  operaConfNumber?: string;
  area: Area;
  inputValues: any;
  isAmendPage?: boolean;
  amendBookingStatus?: CONFIRM_AMEND_STATUS;
  email?: string;
}

interface Mode {
  mode: string;
}

declare global {
  interface Window {
    piConfig: {
      [key: string]: Mode;
      paymentsRedesign: Mode;
      billingAddressCapture: Mode;
      digRegCard: Mode;
      ancillaries: Mode;
      roomPickerRedesign: Mode;
    };
  }
}

export default function ManageBookingCardWrapper({
  bookingReference,
  tempBookingReference,
  basketReference,
  operaConfNumber,
  area,
  inputValues,
  isAmendPage = false,
  amendBookingStatus,
  email,
}: Readonly<BookingInfoCardProps>) {
  const { t } = useTranslation();
  const { isLoggedIn } = useUserData();
  const [isLoginModalOpen, setIsLoginModalOpen] = useState(false);
  const { country, language } = useCustomLocale();
  const [confirmAmendErrorValue] = useSessionStorage<AmendConfirmationErrorLS>(
    INITIAL_CONFIRM_AMEND_ERROR_KEY,
    INITIAL_CONFIRM_AMEND_ERROR_VALUE
  );
  const { [FT_PI_AUTH0_LOGIN]: isAuth0Enabled } = useFeatureToggle();
  const { navigateToLogin, navigateToSignup } = useAuth0Navigation();
  const toggleLoginModal = () => {
    setIsLoginModalOpen(!isLoginModalOpen);
  };

  const handleLoginClick = (event: MouseEvent) => {
    event.preventDefault();
    if (isAuth0Enabled) {
      analytics.track('auth_sign_in_click', { authStep: 'sign_in_click' });
      navigateToLogin();
    } else {
      toggleLoginModal();
    }
  };

  const handleSignUpClick = () => {
    if (isAuth0Enabled) {
      analytics.track('auth_sign_up_click', { authStep: 'sign_up_click' });
      navigateToSignup();
    } else {
      window.location.href = `${getSecureTwoURL()}/${country}/${language}/account/register.html`;
    }
  };

  const failConfirmationData = {
    t,
    data: {
      emailAddress: email ?? '',
      bookingReference: bookingReference,
      notificationText: t('booking.confirmation.paymentMessage'),
      paymentOption: 'PAY-NOW',
    },
  };

  if (isAmendPage && amendBookingStatus === CONFIRM_AMEND_STATUS.paymentError) {
    return (
      <Box {...wrapperStyles}>
        <FailConfirmation {...failConfirmationData} />
      </Box>
    );
  }

  const { errorTitle, errorDescription } = getAmendErrorMessage();

  return (
    <Box {...wrapperStyles}>
      <BookingInfoCardHeader
        basketReference={basketReference}
        bookingReference={bookingReference}
        isAmendPage={isAmendPage}
        area={area}
      />
      {isAmendPage && (
        <Box mb="lg">
          {amendBookingStatus === CONFIRM_AMEND_STATUS.success && (
            <Notification
              prefixDataTestId="amend-booking-confirmation-success-notification"
              description={t('amend.bookingHasUpdated')}
              variant="success"
              status="success"
              maxW="full"
              svg={<Success />}
            />
          )}
          {amendBookingStatus === CONFIRM_AMEND_STATUS.error && (
            <Notification
              prefixDataTestId="amend-booking-confirmation-error-notification"
              title={errorTitle}
              description={errorDescription}
              status="error"
              variant="error"
              maxW="full"
              svg={<Error />}
            />
          )}
        </Box>
      )}
      <BookingInfoCard
        area={area}
        basketReference={basketReference}
        tempBookingReference={tempBookingReference}
        operaConfNumber={operaConfNumber}
        bookingReference={bookingReference}
        inputValues={inputValues}
        isAmendPage={isAmendPage}
        isAmendSuccessful={amendBookingStatus === CONFIRM_AMEND_STATUS.success}
      />
      {area === Area.PI && !isLoggedIn && (
        <Flex {...signUpLogInWrapperStyles}>
          <Button
            data-testid="amend-booking-confirmation-SignUpButton"
            onClick={handleSignUpClick}
            {...signUpButtonStyle}
          >
            {t('dashboard.bookings.signUpButtonLabel')}
          </Button>
          <Flex {...loginLinkWrapperStyles}>
            <Text color="var(--chakra-colors-darkGrey1)">
              {t('dashboard.bookings.alreadyHaveAccount')}
            </Text>
            <Link
              href="#"
              data-testid="amend-booking-confirmation-LogInLink"
              {...loginLinkStyles}
              onClick={handleLoginClick}
            >
              {t('dashboard.bookings.login')}
            </Link>
          </Flex>

          {!isAuth0Enabled && (
            <AuthContentManagerPIVariant
              isLoginModalOpen={isLoginModalOpen}
              toggleLoginModal={toggleLoginModal}
            />
          )}
        </Flex>
      )}
    </Box>
  );

  function getAmendErrorMessage() {
    switch (confirmAmendErrorValue) {
      case AMEND_DEPOSIT_FOLIOS_EXCEPTION_VALUE:
      case AMEND_CONFIRM_EXCEPTION_VALUE:
        return {
          errorTitle: t('amend.errors.somethingwrong.noChanges.title'),
          errorDescription: t('amend.errors.somethingwrong.noChanges.description'),
        };
      case AMEND_REFUND_EXCEPTION_VALUE:
        return {
          errorTitle: t('amend.errors.somethingwrong.refundError.title'),
          errorDescription: t('amend.errors.somethingwrong.refundError.description'),
        };

      case AMEND_REVERT_EXCEPTION_VALUE:
        return {
          errorTitle: t('amend.errors.somethingwrong.paymentReverted.title'),
          errorDescription: t('amend.errors.somethingwrong.paymentReverted.description'),
        };
      default:
        return {
          errorTitle: t('amend.errors.somethingwrong.title'),
          errorDescription: t('amend.errors.somethingwrong.description'),
        };
    }
  }
}

//<editor-fold desc="Styles" defaultstate="collapsed">
const wrapperStyles = {
  backgroundColor: 'lightGrey5',
  p: { mobile: 'sm', sm: 'md', lg: 'lg' },
  mt: { mobile: 'xl', sm: '3xl ', lg: '5xl' },
  mb: { mobile: 'xmd', sm: 'md', md: 'lg', lg: 'xl', xl: '5xl' },
} as StyleProps;

const signUpLogInWrapperStyles = {
  flexDirection: {
    mobile: 'column',
    sm: 'row',
  },
  alignItems: {
    mobile: 'center',
    sm: 'left',
  },
} as FlexProps;

const signUpButtonStyle = {
  variant: 'tertiary',
  mt: '2xl',
  width: { sm: '14.25rem', xs: '17.3125rem', mobile: '14rem' },
  height: '2.5rem',
} as ButtonProps;

const loginLinkWrapperStyles = {
  mt: { mobile: 'md', sm: '2xl' },
  ml: { mobile: '0px', sm: 'lg' },
  alignItems: 'center',
  flexDirection: {
    mobile: 'column',
    xs: 'row',
  },
} as FlexProps;

const loginLinkStyles = {
  display: 'block',
  textDecoration: 'underline',
  pl: 'xs',
  color: 'var(--chakra-colors-tertiary)',
} as LinkProps;

//</editor-fold>

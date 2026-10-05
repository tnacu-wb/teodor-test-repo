import { FlexProps, TextProps, ButtonProps, Flex, Text, Box, Button } from '@chakra-ui/react';
import {
  Area,
  OverridenUserInfo,
  DpaInfo,
  IDV_INITIAL_DATA,
  IDV_STATUS_KEY,
  IdvStatus,
  BC_RESERVATION_STATUS,
} from '@whitbread-eos/api';
import { Success, Icon } from '@whitbread-eos/atoms';
import { formatDataTestId, getIDVPassedStatus, useLocalStorage } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import { BookingActionLinkProps } from './bookingActionsFactory';

export interface Props {
  bookingReference?: string;
  operaConfNumber?: string;
  config: BookingActionLinkProps[];
  area: Area;
  bookingStatus: string;
  bookingType?: string;
  dpaInfo?: DpaInfo;
  baseDataTestId: string;
  overridenUserInfo?: OverridenUserInfo;
  hideBookingStatus?: boolean;
  shouldRenderStatusAndActions?: boolean;
  isBICHeaderBookingStatusEnabled?: boolean;
}

export default function BookingActions({
  bookingReference,
  operaConfNumber,
  config,
  area,
  bookingStatus,
  dpaInfo,
  baseDataTestId,
  overridenUserInfo,
  hideBookingStatus,
  shouldRenderStatusAndActions,
  isBICHeaderBookingStatusEnabled,
}: Readonly<Props>) {
  const { t } = useTranslation(['common']);
  const [idvStatus] = useLocalStorage<IdvStatus>(IDV_STATUS_KEY, IDV_INITIAL_DATA);
  const idvPassed =
    (dpaInfo && getIDVPassedStatus(dpaInfo)) ||
    (idvStatus.passed && idvStatus.bookingReference === bookingReference);
  const overriddenStatus = overridenUserInfo?.reservationOverridden;
  const isOperaConfNumberAvailable = operaConfNumber && operaConfNumber.length > 0;
  const operaConfOrBookingReferenceStyle = isOperaConfNumberAvailable
    ? operaConfNumberStyle
    : bookingReferenceStyle;

  return (
    <Flex direction={{ base: 'column', sm: 'row', lg: 'column' }}>
      <Box
        data-testid={formatDataTestId(
          baseDataTestId,
          isOperaConfNumberAvailable ? 'OperaConfNumber' : 'BookingReference'
        )}
      >
        <Text {...bookingReferenceTitleStyle} width={'max-content'}>
          {isOperaConfNumberAvailable
            ? displayOperaConfirmationLabel()
            : t('dashboard.bookings.bookingReference')}
        </Text>
        <Text {...operaConfOrBookingReferenceStyle}>
          {isOperaConfNumberAvailable ? operaConfNumber : bookingReference}
        </Text>
      </Box>
      {!shouldRenderStatusAndActions && (
        <Flex {...bookingActionsStyle} data-testid={formatDataTestId(baseDataTestId, 'Links')}>
          {renderBasketStatusAndActions(bookingStatus, area)}
        </Flex>
      )}
    </Flex>
  );

  function displayOperaConfirmationLabel() {
    return `${
      area === Area.CCUI
        ? t('ccui.managebooking.operaConfirmation')
        : t('account.dashboard.booking.confirmationNumber')
    }:`;
  }

  function displayOverridenStyleLink(
    idvPassed?: boolean,
    overriden?: boolean,
    key?: string,
    type?: string,
    isLinkEnabled?: boolean
  ) {
    const isOverridePoliciesSuccess = key === 'overridePolicies' && overriden && idvPassed;
    const nonHeaderStyles = isOverridePoliciesSuccess
      ? overridenPoliciesSuccess
      : textLinkStyles(idvPassed && isLinkEnabled);
    return type === 'header' ? textHeaderLinkStyles : nonHeaderStyles;
  }

  function renderActionElement(
    link: BookingActionLinkProps,
    styles: TextProps | ButtonProps,
    action?: { onClick?: () => void }
  ) {
    const IconComponent = link.icon;
    const content = (
      <>
        {IconComponent && <Icon svg={<IconComponent />} />}
        {t(link.title)}
      </>
    );
    const ariaLabel = link.ariaLabel && t(link.ariaLabel);

    if (link.isButton) {
      return (
        <Button
          {...(styles as ButtonProps)}
          {...action}
          data-testid={formatDataTestId(baseDataTestId, link.key)}
          {...(ariaLabel && { 'aria-label': ariaLabel })}
          isDisabled={link.isLinkEnabled === false}
        >
          {content}
        </Button>
      );
    }

    return (
      <Text {...(styles as TextProps)} {...action}>
        {content}
      </Text>
    );
  }

  function renderBasketStatusAndActions(bookingStatus: string, area: string) {
    return (
      <>
        {area !== 'ccui' && (
          <>
            {(!isBICHeaderBookingStatusEnabled && hideBookingStatus) ?? (
              <Text {...bookingStatusStyle}>{t(returnBookingStatusLabel(bookingStatus))} </Text>
            )}
            {config.map((link: any) => {
              const action = link.action ? { onClick: link.action } : {};
              const styles = link.isButton
                ? piButtonStyle
                : textLinkStyles(link.isLinkEnabled !== false);
              return (
                <Flex key={link.title} {...linkContainerStyles(overriddenStatus && link.key)}>
                  {renderActionElement(link, styles, action)}
                </Flex>
              );
            })}
          </>
        )}
        {area === 'ccui' && (
          <>
            {config.map((link: any) => {
              const action = link.action ? { onClick: link.action } : {};
              return (
                <Flex {...linkContainerStyles(overriddenStatus && link.key)}>
                  <Text
                    key={link.title}
                    {...displayOverridenStyleLink(
                      idvPassed,
                      overriddenStatus,
                      link.key,
                      link.type,
                      link.isLinkEnabled
                    )}
                    {...action}
                  >
                    {t(link.title)}
                  </Text>
                  {idvPassed &&
                    overridenUserInfo?.reservationOverridden &&
                    link.key === 'overridePolicies' && (
                      <Box data-testid={formatDataTestId(baseDataTestId, 'OverridenSuccess')}>
                        <Success />
                      </Box>
                    )}
                </Flex>
              );
            })}
          </>
        )}
      </>
    );
  }
}

export function returnBookingStatusLabel(status: string) {
  if (status === BC_RESERVATION_STATUS.CANCELLED) {
    return 'dashboard.bookings.cancelled';
  }
  return 'dashboard.bookings.upcoming';
}

const bookingActionsStyle = {
  gap: 'sm',
  flexDirection: { base: 'column', mobile: 'column', xs: 'column', sm: 'row', lg: 'column' },
  mt: 'md',
  ml: { base: 0, sm: 'auto', lg: 0 },
  alignSelf: { base: 'auto', sm: 'flex-end', lg: 'auto' },
} as FlexProps;

const bookingStatusStyle = {
  fontSize: 'md',
  fontWeight: 'semibold',
  color: 'darkGrey1',
  lineHeight: '3',
} as TextProps;

const bookingReferenceTitleStyle = {
  color: 'primary',
  lineHeight: '3',
  fontSize: 'md',
  fontWeight: 'normal',
} as TextProps;

const bookingReferenceStyle = {
  color: 'darkGrey2',
  fontSize: '3xl',
  fontWeight: 'normal',
  lineHeight: '4',
} as TextProps;

const operaConfNumberStyle = {
  color: 'darkGrey2',
  fontSize: 'lg',
  fontWeight: 'normal',
  lineHeight: '3',
} as TextProps;

const linkContainerStyles = (overridenStatus: boolean) => {
  return {
    direction: overridenStatus ? 'row' : 'column',
    alignItems: overridenStatus && 'center',
  } as FlexProps;
};
const textLinkStyles = (isEnabled: boolean | undefined) => {
  return {
    color: isEnabled ? 'btnSecondaryEnabled' : 'darkGrey1',
    lineHeight: '3',
    fontWeight: 'medium',
    fontSize: 'md',
    cursor: isEnabled ? 'pointer' : 'not-allowed',
    pointerEvents: isEnabled ? 'auto' : 'none',
    opacity: isEnabled ? '1' : '0.5',
  } as TextProps;
};
const textHeaderLinkStyles = {
  color: 'darkGrey2',
  fontSize: 'md',
  fontWeight: 'bold',
  lineHeight: '4',
  marginTop: 'xs',
} as TextProps;

const overridenPoliciesSuccess = {
  color: 'success',
  lineHeight: '3',
  fontWeight: 'bold',
  fontSize: 'md',
  cursor: 'pointer',
  pointerEvents: 'auto',
  marginRight: 'sm',
} as TextProps;

const piButtonStyle = {
  width: 'auto',
  height: '32px',
  color: 'btnSecondaryEnabled',
  lineHeight: '3',
  fontWeight: 'medium',
  fontSize: { base: 'sm', lg: 'md' },
  variant: 'tertiary',
  minWidth: 'auto',
  gap: 'xs',
  opacity: 1,
  borderRadius: 'base',
  borderWidth: '1px',
} as ButtonProps;

import {
  BOOKING_TYPE,
  BASKET_STATUS,
  Area,
  MemoModalVariants,
  paymentOptions,
} from '@whitbread-eos/api';
import { ResendInvoice, Download } from '@whitbread-eos/atoms';
import { AgentMemoState } from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, ComponentType } from 'react';

export type BookingActionsCriteria = {
  area: Area;
  role: string;
  bookingType?: string;
  bookingStatus: string;
  isChangePaymentFeatureEnabled: boolean;
  isBICDownloadInvoiceEnabled: boolean;
};

export type BookingActionLinkProps = {
  title: string;
  key: string;
  action?: () => void;
  type: string;
  isLinkEnabled?: boolean;
  isButton?: boolean;
  icon?: ComponentType;
  ariaLabel?: string;
};

type BookingActionsLinks = {
  [key: string]: string[];
};

const createLink = (
  title: string,
  key: string,
  isButton?: boolean,
  icon?: ComponentType,
  ariaLabel?: string
) => ({
  action: handleGenericAction,
  type: 'link',
  isLinkEnabled: true,
  title,
  key,
  ...(isButton && { isButton }),
  ...(icon && { icon }),
  ...(ariaLabel && { ariaLabel }),
});

const allLinks: BookingActionLinkProps[] = [
  createLink('ccui.manageBooking.options.resendConfirmation', 'resendConfirmation'),
  createLink(
    'dashboard.bookings.downloadInvoice',
    'downloadInvoice',
    true,
    Download,
    'dashboard.bookings.downloadInvoiceAria'
  ),
  createLink('dashboard.bookings.sendInvoice', 'resendInvoice', true, ResendInvoice),
  {
    title: 'ccui.manageBooking.options.otherOptions',
    key: 'otherOptions',
    type: 'header',
  },
  createLink('ccui.manageBooking.options.changeLog', 'changeLog'),
  createLink('ccui.manageBooking.options.agentNotes', 'agentNotes'),
  createLink('ccui.manageBooking.options.splitBooking', 'splitBooking'),
  createLink('ccui.manageBooking.options.repeatBooking', 'repeatBooking'),
  createLink('ccui.manageBooking.options.cancelRebook', 'cancelRebook'),
  createLink('ccui.manageBooking.options.overridePolicies', 'overridePolicies'),
  createLink('ccui.manageBooking.options.changePIPBACNPStatus', 'changePiba'),
  createLink('ccui.manageBooking.options.updateDiscountAmount', 'updateDiscountAmount'),
  createLink('ccui.manageBooking.options.updateBookerDetails', 'bookerDetails'),
  createLink('ccui.manageBooking.option.changePaymentMethod', 'changePaymentMethod'),
];

const getActionsByCriteria = (
  criteria: BookingActionsCriteria,
  basketReference: string | null,
  paymentOption: string,
  setIsAgentOverrideModalVisible?: Dispatch<SetStateAction<boolean>>,
  handleAgentMemo?: (state: AgentMemoState) => void,
  handleResendInvoiceAction?: () => void,
  handleDownloadInvoiceAction?: () => void,
  isDownloadingInvoice?: boolean,
  handleResendConfirmationAction?: () => void,
  handleRepeatBooking?: (func?: () => void) => void,
  handleChangeLogClicked?: () => void,
  handleChangePayment?: () => void
) => {
  return allLinks
    .filter((link) => isAvailable(link.key, criteria))
    .map((link) => {
      switch (link.key) {
        case 'overridePolicies':
          link.action = () =>
            setIsAgentOverrideModalVisible && setIsAgentOverrideModalVisible(true);
          break;
        case 'agentNotes':
          link.action = () =>
            handleAgentMemo?.({ reservationId: basketReference, variant: MemoModalVariants.CARD });
          break;
        case 'resendInvoice':
          link.action = () => handleResendInvoiceAction?.();
          break;
        case 'downloadInvoice':
          link.action = () => handleDownloadInvoiceAction?.();
          link.isLinkEnabled = !isDownloadingInvoice;
          break;
        case 'resendConfirmation':
          link.action = () => handleResendConfirmationAction?.();
          break;
        case 'repeatBooking':
          link.action = () => handleRepeatBooking?.();
          break;
        case 'changeLog':
          link.action = () => handleChangeLogClicked?.();
          break;
        case 'changePaymentMethod':
          link.action = () => handleChangePayment?.();
          link.isLinkEnabled = paymentOption === paymentOptions.RESERVE_WITHOUT_CARD;
          break;
        default:
          link.action = handleGenericAction;
      }
      return link;
    });
};

const linksForCCUI = [
  // Functionality removed based on DNRQ-45422
  // 'resendConfirmation',
  // 'resendInvoice',
  'otherOptions',
  'changeLog',
  'agentNotes',
  'splitBooking',
  'repeatBooking',
  'cancelRebook',
  'overridePolicies',
  'changePiba',
  'updateDiscountAmount',
  'bookerDetails',
  'changePaymentMethod',
];

const linksForManager = ['resendConfirmation', 'resendInvoice', 'downloadInvoice', ...linksForCCUI];

const linksForNormalUser = linksForManager;

const linksForUpcomingBookings = ['resendConfirmation', ...linksForCCUI];

const linksForCancelledBookings = ['otherOptions', 'changeLog', 'agentNotes', 'repeatBooking'];

const linksForPastBookings = ['resendInvoice', 'downloadInvoice', ...linksForCancelledBookings];

const linksForPIByStatus: BookingActionsLinks = {
  [BOOKING_TYPE.UPCOMING]: [],
  [BOOKING_TYPE.PAST]: [],
};

linksForPIByStatus[BOOKING_TYPE.UPCOMING].push('resendConfirmation');
linksForPIByStatus[BOOKING_TYPE.PAST].push('resendInvoice');
linksForPIByStatus[BOOKING_TYPE.PAST].push('downloadInvoice');

function isAvailable(key: string, criteria: BookingActionsCriteria) {
  return (
    isAvailableByRole(key, criteria.role) &&
    isAvailableByArea(key, criteria.area) &&
    isAvailableByFeatureFlag(key, criteria) &&
    (criteria.area === Area.CCUI ||
    ([Area.PI, Area.BB].includes(criteria.area) && criteria.bookingType)
      ? isAvailableByBookingType(key, criteria.bookingType)
      : isAvailableByStatus(key, criteria.bookingStatus))
  );
}

function isAvailableByFeatureFlag(key: string, criteria: BookingActionsCriteria) {
  switch (key) {
    case 'changePaymentMethod':
      return criteria.isChangePaymentFeatureEnabled && linksForCCUI.includes(key);
    case 'downloadInvoice':
      return criteria.isBICDownloadInvoiceEnabled && criteria.area !== Area.CCUI;
    default:
      return true;
  }
}

function isAvailableByRole(key: string, role: string) {
  switch (role) {
    case 'manager':
      return linksForManager.includes(key);
    case 'all':
      return linksForNormalUser.includes(key);
    default:
      return true;
  }
}

function isAvailableByArea(key: string, area: Area) {
  const mergedPILinksByStatus = [
    ...linksForPIByStatus[BOOKING_TYPE.UPCOMING],
    ...linksForPIByStatus[BOOKING_TYPE.PAST],
  ];

  switch (area) {
    case Area.CCUI:
      return linksForCCUI.includes(key);
    case Area.PI:
      return mergedPILinksByStatus.includes(key);
    case Area.BB:
      return mergedPILinksByStatus.includes(key);
    default:
      return true;
  }
}

function isAvailableByStatus(key: string, bookingStatus: string) {
  switch (bookingStatus) {
    case BASKET_STATUS.CANCELLED:
      return linksForCancelledBookings.includes(key);
    case BASKET_STATUS.COMPLETED:
      return linksForPastBookings.includes(key);
    default:
      return true;
  }
}

function isAvailableByBookingType(key: string, bookingType?: string) {
  switch (bookingType) {
    case BOOKING_TYPE.UPCOMING:
    case BOOKING_TYPE.CHECKED_IN:
      return (
        linksForUpcomingBookings.includes(key) ||
        linksForPIByStatus[BOOKING_TYPE.UPCOMING].includes(key)
      );
    case BOOKING_TYPE.PAST:
      return (
        linksForPastBookings.includes(key) || linksForPIByStatus[BOOKING_TYPE.PAST].includes(key)
      );
    case BOOKING_TYPE.CANCELLED:
      return linksForCancelledBookings.includes(key);
    default:
      return false;
  }
}

function handleGenericAction() {
  console.log(`Handle action`);
}

export default getActionsByCriteria;

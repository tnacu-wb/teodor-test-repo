import { BookingSummaryLabels } from '@whitbread-eos/api';
import type { TitleDropdownOption } from '@whitbread-eos/api';

export function getAmendSectionTranslations(t: (key: string) => string) {
  const bookingSummaryLabels: BookingSummaryLabels = {
    title: t('booking.header.summary'),
    confirmChangesLabel: t('amend.confirmChanges'),
    mealsLabel: t('booking.hotel.summary.meals'),
    extrasLabel: t('amend.extras.extras'),
    previousTotalLabel: t('dashboard.bookings.previousTotal'),
    totalCostLabel: t('booking.confirmation.totalCost'),
    continueToPaymentLabel: t('ccui.amend.continue.to.payment'),
    expandDetail: t('amend.expandDetail'),
  };
  const leadGuestLabels = {
    guestTitle: t('amend.nameTitles'),
    title: t('booking.contactDetails.title'),
    firstName: t('booking.leadGuest.FirstName'),
    lastName: t('booking.leadGuest.LastName'),
    email: t('dashboard.bookings.emailPlaceholder'),
    emailAddress: t('dashboard.bookings.emailPlaceholder'),
    addressLine1: t('booking.guest.addressAddress1'),
    addressLine2: t('booking.guest.addressAddress2'),
    addressLine3: t('booking.guest.addressAddress3'),
    postalCode: t('booking.guest.addressPostCode'),
    city: t('booking.guest.addressCity'),
    country: 'Choose a country',
  };
  const leadGuestValidationLabels = {
    titleError: t('config.errorMessages.yourDetails.title.required'),
    firstNameRequiredError: t('config.errorMessages.yourDetails.firstName.required'),
    firstNameMinError: t('config.errorMessages.yourDetails.firstName.min'),
    lastNameRequiredError: t('config.errorMessages.yourDetails.lastName.required'),
    firstNameInvalidError: t('config.errorMessages.yourDetails.firstName.invalid'),
    lastNameInvalidError: t('config.errorMessages.yourDetails.lastName.invalid'),
    emailInvalidError: t('config.errorMessages.yourDetails.email.max'),
  };
  const notificationLabels = {
    title: t('amend.notification.reset.header'),
    description: t('amend.notification.reset.message'),
  };

  const summaryOfPaymentsLabels = {
    balancePaid: t('amend.prepaid'),
    payOnArrival: t('booking.payOnArrival'),
    totalCost: t('booking.confirmation.totalCost'),
    refund: t('amend.refund'),
    refundTerms: t('amend.refundTerms'),
    nonRefundable: 'Non-refundable',
    balanceAuthorised: 'Balance authorised',
    donation: t('amend.goshDonation'),
  };

  const removeRoomModalLabels = {
    title: t('amend.removeRoom'),
    confirmLabel: t('amend.removeRoom.confirmation'),
    notificationLabel: t('amend.roomMayNotBeAvailable'),
    removeModalRoom: t('amend.removeRoom'),
    cancelModalRoom: t('ccui.manageBooking.cancelButton'),
    roomSuccessfullRemoved: t('amend.roomSuccessfullRemoved'),
  };

  const stayDatesLabels = {
    arrivalDate: t('amend.arrivalDate'),
    nightsLabel: t('search.nights'),
    nightOption: t('amend.night'),
    nightsOption: t('amend.nights'),
    checkOut: t('search.checkOut'),
    hotel: t('numberList.hotel'),
    yourStayDatesTitle: t('amend.yourStayDates'),
    numberOfNightsErrorMessage: t('ccui.search.nrOfnights.errormessage'),
  };

  const roomAvailabilityLabels = {
    addRoom: t('amend.addRoom'),
    roomAvailable: t('amend.roomAvailable'),
    roomsUnavailable: t('amend.roomsUnavailable'),
    roomsUnavailableDescription: t('amend.roomsUnavailableDescription'),
    checkRoomAvailability: t('amend.checkRoomAvailability'),
    leadGuest: t('amend.leadGuest'),
    cancelBtn: t('ccui.manageBooking.cancelButton'),
    guests: t('amend.guests'),
    update: t('amend.update'),
    roomSuccessfullyAdded: t('amend.roomSuccessfullyAdded'),
    roomSuccessfullyUpdated: t('amend.roomSuccessfullyUpdated'),
    cityTaxNotIncluded: t('amend.cityTax.notIncluded'),
  };

  return {
    bookingSummaryLabels,
    leadGuestLabels,
    leadGuestValidationLabels,
    notificationLabels,
    summaryOfPaymentsLabels,
    removeRoomModalLabels,
    stayDatesLabels,
    roomAvailabilityLabels,
  };
}

export const getTitleDropdownValues = (
  titles: TitleDropdownOption[],
  currentTitle: string | number | boolean | object
) => {
  const optionExists = titles.find((title) => title.id === currentTitle);
  if (!optionExists && currentTitle !== '') {
    return [{ id: currentTitle, label: currentTitle }, ...titles];
  }
  return titles;
};

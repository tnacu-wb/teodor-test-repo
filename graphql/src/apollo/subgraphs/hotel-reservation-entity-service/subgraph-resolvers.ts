import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  getFindBooking,
  getFindBookingForKiosk,
  updateUdfc20
} from './services/manage-booking-service';
import { getManageBookingInformation } from './services/manage-booking-service';
import { getSearchBookings } from './services/manage-booking-service';
import {
  amendDistribution,
  cancelOnHoldReservation,
  cancelReservation,
  copyBooking,
  createReservation,
  createMemo,
  getCancellationPolicies,
  updateCnp,
  updateRateCode,
  updateReasonForStay,
  updateReservationOverrideReasons,
  updateRoomType,
  attachFileToReservation,
  updateReservationPackageScheduled,
  updateReservationPreferences,
  addNewRoom,
  updateEmail,
  saveReservationAncillaries,
  createReservationGuest,
  updateReservationPackagesByReservation,
  removeRoom,
  amendEditRoom,
  changeBookingDates
} from './services/hotel-reservation-service';
import { getBartBookingInformation } from './services/bart-booking-information-service';
import { getPmsBookingInformation } from './services/hotel-reservation-service';
import { getBookingAllowances } from './services/hotel-reservation-service';
import { getMemos } from './services/hotel-reservation-service';
import { getSearchBookingsCcui } from './services/hotel-reservation-service';
import {
  confirmAmend,
  confirmAmendLogic,
  getAmendConfirmationPrices,
  getAmendPaymentOptions,
  getAmendSummary
} from './services/amend-service';
import { retrieveChangesLog } from './services/change-log-service';
import { savePreCheckInStatus } from './services/pre-check-in-status-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    amendConfirmationPrices: (_: any, args: any, context: any): Promise<any> => {
      return getAmendConfirmationPrices(args, context);
    },
    amendSummary: (_: any, args: any, context: any): Promise<any> => {
      return getAmendSummary(args, context);
    },
    bartBookingInformation: (_: any, args: any, context: any): Promise<any> => {
      return getBartBookingInformation(args, context);
    },
    bookingAllowances: (_: any, args: any, context: any): Promise<any> => {
      return getBookingAllowances(args, context);
    },
    cancellationPolicies: (_: any, args: any, context: any): Promise<any> => {
      return getCancellationPolicies(args, context);
    },
    findBooking: (_: any, args: any, context: any): Promise<any> => {
      return getFindBooking(args, context);
    },
    findBookingForKiosk: (_: any, args: any, context: any): Promise<any> => {
      return getFindBookingForKiosk(args, context);
    },
    getMemos: (_: any, args: any, context: any): Promise<any> => {
      return getMemos(args, context);
    },
    manageBooking: (_: any, args: any, context: any): Promise<any> => {
      return getManageBookingInformation(args, context);
    },
    paymentOptions: (_: any, args: any, context: any): Promise<any> => {
      return getAmendPaymentOptions(args, context);
    },
    searchBookings: (_: any, args: any, context: any): Promise<any> => {
      return getSearchBookings(args, context);
    },
    searchBookingsCcui: (_: any, args: any, context: any): Promise<any> => {
      return getSearchBookingsCcui(args, context);
    },
    pmsBookingInformation: (_: any, args: any, context: any): Promise<any> => {
      return getPmsBookingInformation(args, context);
    },
    retrieveChangesLog: (_: any, args: any, context: any): Promise<any> => {
      return retrieveChangesLog(args, context);
    }
  },
  Mutation: {
    addNewRoom: (_: any, args: any, context: any): Promise<any> => {
      return addNewRoom(args, context);
    },
    amendDistribution: (_: any, args: any, context: any): Promise<any> => {
      return amendDistribution(args, context);
    },
    amendEditRoom: (_: any, args: any, context: any): Promise<any> => {
      return amendEditRoom(args, context);
    },
    attachFileToReservation: (_: any, args: any, context: any): Promise<any> => {
      return attachFileToReservation(args, context);
    },
    cancelOnHoldReservation: (_: any, args: any, context: any): Promise<any> => {
      return cancelOnHoldReservation(args, context);
    },
    cancelReservation: (_: any, args: any, context: any): Promise<any> => {
      return cancelReservation(args, context);
    },
    changeBookingDates: (_: any, args: any, context: any): Promise<any> => {
      return changeBookingDates(args, context);
    },
    createReservationGuest: (_: any, args: any, context: any): Promise<any> => {
      return createReservationGuest(args, context);
    },
    confirmAmendLogic: (_: any, args: any, context: any): Promise<any> => {
      return confirmAmendLogic(args, context);
    },
    copyBooking: (_: any, args: any, context: any): Promise<any> => {
      return copyBooking(args, context);
    },
    createMemo: (_: any, args: any, context: any): Promise<any> => {
      return createMemo(args, context);
    },
    createReservation: (_: any, args: any, context: any): Promise<any> => {
      return createReservation(args, context);
    },
    preCheckInStatus: (_: any, args: any, context: any): Promise<any> => {
      return savePreCheckInStatus(args, context);
    },
    removeRoom: (_: any, args: any, context: any): Promise<any> => {
      return removeRoom(args, context);
    },
    updateReasonForStay: (_: any, args: any, context: any): Promise<any> => {
      return updateReasonForStay(args, context);
    },
    updateRateCode: (_: any, args: any, context: any): Promise<any> => {
      return updateRateCode(args, context);
    },
    updateRoomType: (_: any, args: any, context: any): Promise<any> => {
      return updateRoomType(args, context);
    },
    updateReservationOverrideReasons: (_: any, args: any, context: any): Promise<any> => {
      return updateReservationOverrideReasons(args, context);
    },
    updateCnp: (_: any, args: any, context: any): Promise<any> => {
      return updateCnp(args, context);
    },
    updateReservationPackagesByReservation: (_: any, args: any, context: any): Promise<any> => {
      return updateReservationPackagesByReservation(args, context);
    },
    updateReservationPackageScheduled: (_: any, args: any, context: any): Promise<any> => {
      return updateReservationPackageScheduled(args, context);
    },
    updateReservationPreferences: (_: any, args: any, context: any): Promise<any> => {
      return updateReservationPreferences(args, context);
    },
    updateEmail: (_: any, args: any, context: any): Promise<any> => {
      return updateEmail(args, context);
    },
    saveReservation: (_: any, args: any, context: any): Promise<any> => {
      return saveReservationAncillaries(args, context);
    },
    confirmAmend: (_: any, args: any, context: any): Promise<any> => {
      return confirmAmend(args, context);
    },
    updateUdfc20: (_: any, args: any, context: any): Promise<any> => {
      return updateUdfc20(args, context);
    }
  },
  FindBooking: {
    isThirdPartyBooking(parent: any) {
      return parent?.idContext === '3rd Party';
    }
  }
};

export const hotelReservationEntitySubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

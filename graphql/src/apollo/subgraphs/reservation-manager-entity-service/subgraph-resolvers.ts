import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { retrieveBookingHistory } from './services/booking-history-service';
import { retrieveBookingInfoCardDetails } from './services/booking-info-card-details-service';
import { resendInvoiceEmail } from './services/resend-invoice-email-service';
import { resendConfirmationEmail } from './services/resend-confirmation-email-service';
import { cancelBooking } from './services/booking-service';
import { getUpcomingBookings } from './services/get-upcoming-bookings-service';
import { downloadBookingInvoice } from './services/download-booking-invoice-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    bookingHistory: (_: any, args: any, context: any): Promise<any> => {
      return retrieveBookingHistory(args, context);
    },
    bookingInfoCardDetails: (_: any, args: any, context: any): Promise<any> => {
      return retrieveBookingInfoCardDetails(args, context);
    },
    getUpcomingBookings: (_: any, args: any, context: any): Promise<any> => {
      return getUpcomingBookings(args, context);
    }
  },
  Mutation: {
    cancelBooking: (_: any, args: any, context: any): Promise<any> => {
      return cancelBooking(args, context);
    },
    resendInvoiceEmail: (_: any, args: any, context: any): Promise<any> => {
      return resendInvoiceEmail(args, context);
    },
    resendConfirmationEmail: (_: any, args: any, context: any): Promise<any> => {
      return resendConfirmationEmail(args, context);
    },
    downloadBookingInvoice: (_: any, args: any, context: any): Promise<any> => {
      return downloadBookingInvoice(args, context);
    }
  }
};

export const reservationManagerEntitySubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import {
  getBookingConfirmation,
  getBookingConfirmationAuthenticated,
  getBookingConfirmationAuthenticatedWithToken
} from './services/booking-confirmation-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    bookingConfirmation: (_: any, args: any, context: any): Promise<any> => {
      return getBookingConfirmation(args, context);
    },
    bookingConfirmationAuthenticated: (_: any, args: any, context: any): Promise<any> => {
      return getBookingConfirmationAuthenticated(args, context);
    },
    bookingConfirmationAuthenticatedWithToken: (_: any, args: any, context: any): Promise<any> => {
      return getBookingConfirmationAuthenticatedWithToken(args, context);
    }
  },
  Mutation: {},
  BookingConfirmationDetails: {
    isThirdPartyBooking(parent: any) {
      return parent?.idContext === '3rd Party';
    }
  }
};

export const bookingConfirmationSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

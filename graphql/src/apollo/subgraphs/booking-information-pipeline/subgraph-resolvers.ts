import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { getBookingInformationService } from './services/booking-information-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    bookingInformation: (_: any, args: any, context: any): Promise<any> => {
      return getBookingInformationService(args, context);
    }
  },
  Mutation: {}
};

export const bookingInformationSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

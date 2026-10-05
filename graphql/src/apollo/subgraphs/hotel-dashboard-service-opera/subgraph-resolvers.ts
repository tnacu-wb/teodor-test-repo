import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { getHotelDashboard } from './services/get-hotel-dashboard';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getHotelDashboard: (_: any, args: any, context: any): Promise<any> => {
      return getHotelDashboard(args, context);
    }
  },
  Mutation: {}
};

export const hotelDashboardServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

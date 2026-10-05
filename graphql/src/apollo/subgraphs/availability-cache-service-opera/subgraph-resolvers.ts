import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  getLowestPricesByLocationForCalendar,
  getLowestRatesByHotel,
  getLowestRatesByLocationId
} from './services/price-finder-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getLowestRatesByLocationId: (_: any, args: any, context: any): Promise<any> => {
      return getLowestRatesByLocationId(args, context);
    },
    getLowestRatesByHotel: (_: any, args: any, context: any): Promise<any> => {
      return getLowestRatesByHotel(args, context);
    },
    getLowestPricesByLocationForCalendar: (_: any, args: any, context: any): Promise<any> => {
      return getLowestPricesByLocationForCalendar(args, context);
    }
  },
  Mutation: {}
};

export const availabilityCacheSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

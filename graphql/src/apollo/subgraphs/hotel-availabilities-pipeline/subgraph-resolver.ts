import { buildSubgraphSchema } from '@apollo/subgraph';
import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { getHotelAvailabilities } from './services/hotel-availabilities-service';
import { getHotelAvailabilitiesV2 } from './services/hotel-availabilities-v2-service';
import { getHotelAvailabilityInfo } from './services/hotel-availability-info-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    hotelAvailabilities: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilities(args, context);
    },
    hotelAvailabilitiesV2: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilitiesV2(args, context);
    },
    hotelAvailabilityInfo: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilityInfo(args, context);
    }
  }
};

export const hotelAvailabilitiesSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

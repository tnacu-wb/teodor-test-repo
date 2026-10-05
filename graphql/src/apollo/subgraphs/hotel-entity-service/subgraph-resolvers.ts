import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { getHotelAvailabilitiesByIds } from './services/hotel-availability-by-ids-service';
import { getHotelAvailabilities } from './services/hotel-availability-service';
import { getHotelDistanceFromSearch } from './services/hotel-distance-from-search-service';
import { getHotelsLocations } from './services/hotels-locations-service';
import { getHotelInventory } from './services/hotel-inventory-service';
import { getCancellationReasons } from './services/cancelation-resons-service';
import { getHotelPreferences } from './services/get-hotel-preferences-service';
import { getHotelAvailabilitiesByIdsV2 } from './services/hotel-availability-by-ids-v2-service';
import { getCreateGroupBooking } from './services/create-group-boocking-service';
import { getHotelAvailabilitiesByIdsV3 } from './services/hotel-availability-by-ids-v3-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    cancellationReasons: (_: any, args: any, context: any): Promise<any> => {
      return getCancellationReasons(args, context);
    },
    hotelAvailabilityByIds: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilitiesByIds(args, context);
    },
    hotelAvailability: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilities(args, context);
    },
    hotelDistanceFromSearch: (_: any, args: any, context: any): Promise<any> => {
      return getHotelDistanceFromSearch(args, context);
    },
    hotelsLocations: (_: any, args: any, context: any): Promise<any> => {
      return getHotelsLocations(args, context);
    },
    hotelInventory: (_: any, args: any, context: any): Promise<any> => {
      return getHotelInventory(args, context);
    },
    getHotelPreferences: (_: any, args: any, context: any): Promise<any> => {
      return getHotelPreferences(args, context);
    }
  },
  Mutation: {
    hotelAvailabilityByIdsV2: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilitiesByIdsV2(args, context);
    },
    hotelAvailabilityByIdsV3: (_: any, args: any, context: any): Promise<any> => {
      return getHotelAvailabilitiesByIdsV3(args, context);
    },
    createGroupBooking: (_: any, args: any, context: any): Promise<any> => {
      return getCreateGroupBooking(args, context);
    }
  }
};

export const hotelEntitySubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

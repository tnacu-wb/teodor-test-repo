import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { getCountries } from './services/countries-service';
import { getCategoryLabels, getLabels } from './services/labels-service';
import { getCookieConsent } from './services/cookie-consent-service';
import { getSeoInformation } from './services/seo-information-service';
import {
  getHotelInformation,
  getHotelInformationBySlug,
  retrieveHotelsInformation,
  getAllHotelsShortInformation
} from './services/hotel-information-service';
import { getFooter } from './services/footer-service';
import { getRoomClassConfig } from './services/room-class-config-service';
import { getPriceFinderGlobalConfig } from './services/price-finder-global-config-service';
import { getCardManagementLabels } from './services/get-card-management-labels-service';
import { getRatesInformation, getRatesInformationV2 } from './services/rates-information-service';
import { getRoomTypeInformation } from './services/room-type-information-service';
import { dlpInformation } from './services/dlp-information-service';
import { getBookingFlowInformation } from './services/booking-flow-information-service';
import { getMaxRoomsLimitation } from './services/search-rules-service';
import { getSearchInformation } from './services/search-information-service';
import { getPageData } from './services/get-page-data-service';
import { globalConfig, promotionsInformation } from './services/global-config-service';
import { homepageAppsContent } from './services/homepage-apps-content-service';
import { getHeaderInformation } from './services/index-header-data-service';
import {
  getMaxNightsLimitation,
  getRoomOccupancyLimitation
} from './services/max-limitations-content-service';
import { getMaxArrivalDateLimitation } from './services/max-limitations-content-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    countries: (_: any, args: any, context: any): Promise<any> => {
      return getCountries(args, context);
    },
    footer: (_: any, args: any, context: any): Promise<any> => {
      return getFooter(args, context);
    },
    labels: (_: any, args: any, context: any, info: any): Promise<any> => {
      return getLabels(args, context, info);
    },
    globalConfig: (_: any, args: any, context: any): Promise<any> => {
      return globalConfig(args, context);
    },
    categoryLabels: (_: any, args: any, context: any): Promise<any> => {
      return getCategoryLabels(args, context);
    },
    cookieConsent: (_: any, args: any, context: any): Promise<any> => {
      return getCookieConsent(args, context);
    },
    seoInformation: (_: any, args: any, context: any): Promise<any> => {
      return getSeoInformation(args, context);
    },
    getHotelsInformation: async (_: any, args: any, context: any): Promise<any> => {
      return retrieveHotelsInformation(args, context);
    },
    roomClassConfig: async (_: any, args: any, context: any): Promise<any> => {
      return getRoomClassConfig(args, context);
    },
    priceFinderConfig: async (_: any, args: any, context: any): Promise<any> => {
      return getPriceFinderGlobalConfig(args, context);
    },
    getCardManagementLabels: async (_: any, args: any, context: any): Promise<any> => {
      return getCardManagementLabels(args, context);
    },
    dlpInformation: async (_: any, args: any, context: any): Promise<any> => {
      return dlpInformation(args, context);
    },
    getPageData: async (_: any, args: any, context: any, info: any): Promise<any> => {
      return getPageData(args, context, info);
    },
    ratesInformation: async (_: any, args: any, context: any): Promise<any> => {
      return getRatesInformation(args, context);
    },
    ratesInformationV2: async (_: any, args: any, context: any): Promise<any> => {
      return getRatesInformationV2(args, context);
    },
    roomTypeInformation: async (_: any, args: any, context: any): Promise<any> => {
      return getRoomTypeInformation(args, context);
    },
    bookingFlowInformation: async (_: any, args: any, context: any): Promise<any> => {
      return getBookingFlowInformation(args, context);
    },
    maxRoomsLimitation: async (_: any, args: any, context: any): Promise<any> => {
      return getMaxRoomsLimitation(args, context);
    },
    searchInformation: async (_: any, args: any, context: any): Promise<any> => {
      return getSearchInformation(args, context);
    },
    hotelInformation: async (_: any, args: any, context: any): Promise<any> => {
      return getHotelInformation(args, context);
    },
    hotelInformationBySlug: async (_: any, args: any, context: any): Promise<any> => {
      return getHotelInformationBySlug(args, context);
    },
    allHotelsShortInformation: async (_: any, args: any, context: any): Promise<any> => {
      return getAllHotelsShortInformation(args, context);
    },
    homepageAppsContent: async (_: any, args: any, context: any): Promise<any> => {
      return homepageAppsContent(args, context);
    },
    headerInformation: async (_: any, args: any, context: any, info: any): Promise<any> => {
      return getHeaderInformation(args, context, info);
    },
    maxNightsLimitation: (_: any, args: any, context: any): Promise<any> => {
      return getMaxNightsLimitation(args, context);
    },
    maxArrivalDateLimitation: (_: any, args: any, context: any): Promise<any> => {
      return getMaxArrivalDateLimitation(args, context);
    },
    roomOccupancyLimitations: (_: any, args: any, context: any): Promise<any> => {
      return getRoomOccupancyLimitation(args, context);
    },
    promotionsInformation: (_: any, args: any, context: any): Promise<any> => {
      return promotionsInformation(args, context);
    }
  },
  Mutation: {}
};
export const contentEntitySubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

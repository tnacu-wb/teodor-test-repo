import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { getRoomSubstitutionLimitation } from './services/room-substitution-rule-service';
import { getOccupancySupplement } from './services/occupancy-supplement-service';
import { getVatRule } from './services/vat-rule-service';
import { getPaypalRule } from './services/paypal-rule-service';
import { readSchema } from '../../utils/base-utils';
import { retrieveResourceId } from './services/search-resource-id-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    roomSubstitutionLimitations: (_: any, args: any, context: any): Promise<any> => {
      return getRoomSubstitutionLimitation(args, context);
    },
    occupancySupplement: (_: any, args: any, context: any): Promise<any> => {
      return getOccupancySupplement(args, context);
    },
    vatRules: (_: any, args: any, context: any): Promise<any> => {
      return getVatRule(args, context);
    },
    searchResourceId: (_: any, args: any, context: any): Promise<any> => {
      return retrieveResourceId(args, context);
    },
    paypalStatus: (_: any, args: any, context: any): Promise<any> => {
      return getPaypalRule(args, context);
    }
  }
};

export const ruleAgentEntitySubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

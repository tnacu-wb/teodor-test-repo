import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { getPartialAddress, getFormattedAddress } from './services/address-lookup-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    partialAddress: (_: any, args: any, context: any): Promise<any> => {
      return getPartialAddress(args, context);
    },
    formattedAddress: (_: any, args: any, context: any): Promise<any> => {
      return getFormattedAddress(args, context);
    }
  }
};

export const addressLookupEntitySubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

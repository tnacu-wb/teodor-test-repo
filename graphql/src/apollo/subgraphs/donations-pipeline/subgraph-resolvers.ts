import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { getDonations } from './services/donations-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    donations: (_: any, args: any, context: any): Promise<any> => {
      return getDonations(args, context);
    }
  },
  Mutation: {}
};

export const donationsSubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

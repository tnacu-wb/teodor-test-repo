import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { createAccount } from './services/account-registration-service';
import { updateMarketingPreferences } from './services/marketing-preferences-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {},
  Mutation: {
    createAccount: (_: any, args: any, context: any): Promise<any> => {
      return createAccount(args, context);
    },
    updateMarketingPreferences: (_: any, args: any, context: any): Promise<any> => {
      return updateMarketingPreferences(args, context);
    }
  }
};

export const accountEntitySubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

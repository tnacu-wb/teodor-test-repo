import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { termsAndConditions } from './service/terms-and-conditions-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    termsAndConditions: (_: any, args: any, context: any): Promise<any> => {
      return termsAndConditions(args, context);
    }
  }
};

export const termsAndConditionsSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

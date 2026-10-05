import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { businessTetherLogin } from './services/business-tether-service';
import { businessTether } from './services/business-tether';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {},
  Mutation: {
    businessTetherLogin: (_: any, args: any, context: any): Promise<any> => {
      return businessTetherLogin(args, context);
    },
    businessTether: (_: any, args: any, context: any): Promise<any> => {
      return businessTether(args, context);
    }
  }
};

export const businessTetherServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

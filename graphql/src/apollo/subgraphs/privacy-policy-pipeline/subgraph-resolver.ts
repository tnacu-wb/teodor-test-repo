import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { privacyPolicy } from './service/privacy-policy-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    privacyPolicy: (_: any, args: any, context: any): Promise<any> => {
      return privacyPolicy(args, context);
    }
  }
};

export const privacyPolicySubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { packages } from './service/packages-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    packages: (_: any, args: any, context: any): Promise<any> => {
      return packages(args, context);
    }
  },
  Mutation: {}
};

export const packagesSubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { addFeedback } from './services/feedback-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {},
  Mutation: {
    addFeedback: (_: any, args: any, context: any): Promise<any> => {
      return addFeedback(args, context);
    }
  }
};

export const feedbackServiceOperaSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

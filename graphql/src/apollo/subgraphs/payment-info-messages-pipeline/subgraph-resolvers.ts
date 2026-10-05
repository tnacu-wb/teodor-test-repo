import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { getPaymentInfoMessagesService } from './services/payment-info-messages-service';
import { readSchema } from '../../utils/base-utils';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    paymentInfoMessages: (_: any, args: any, context: any): Promise<any> => {
      return getPaymentInfoMessagesService(args, context);
    }
  },
  Mutation: {}
};

export const paymentInfoSubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

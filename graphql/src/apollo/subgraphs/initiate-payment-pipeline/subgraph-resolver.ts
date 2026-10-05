import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { getInitiatePayment, getInitiatePaypalPayment } from './service/initiate-payment-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {},
  Mutation: {
    initiatePayment: (_: any, args: any, context: any): Promise<any> => {
      return getInitiatePayment(args, context);
    },
    initiatePaypalPayment: (_: any, args: any, context: any): Promise<any> => {
      return getInitiatePaypalPayment(args, context);
    }
  }
};

export const initiatePaymentSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

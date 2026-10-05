import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  getPaymentMethods,
  getCcuiPaymentMethods,
  getCheckInOnlinePaymentActions
} from './services/payment-methods-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    paymentMethods: (_: any, args: any, context: any): Promise<any> => {
      return getPaymentMethods(args, context);
    },
    paymentCcuiMethods: (_: any, args: any, context: any): Promise<any> => {
      return getCcuiPaymentMethods(args, context);
    },
    checkInOnlinePaymentActions: (_: any, args: any, context: any): Promise<any> => {
      return getCheckInOnlinePaymentActions(args, context);
    }
  },
  Mutation: {}
};

export const paymentMethodsEntitySubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

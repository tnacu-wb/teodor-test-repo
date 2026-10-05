import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import {
  getAccountSpending,
  getAccountUpcomingSpending,
  getEmployeeSpend,
  getPaymentInfo,
  retrieveCompanySpending
} from './services/company-spending-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getCompanySpending: async (_: any, args: any, context: any) => {
      return retrieveCompanySpending(args, context);
    },
    getAccountUpcomingSpending: async (_: any, args: any, context: any) => {
      return getAccountUpcomingSpending(args, context);
    },
    getEmployeeSpend: async (_: any, args: any, context: any) => {
      return getEmployeeSpend(args, context);
    },
    getAccountSpending: async (_: any, args: any, context: any) => {
      return getAccountSpending(args, context);
    },
    getPaymentInfo: async (_: any, args: any, context: any) => {
      return getPaymentInfo(args, context);
    }
  },
  Mutation: {}
};

export const spendingEntityServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

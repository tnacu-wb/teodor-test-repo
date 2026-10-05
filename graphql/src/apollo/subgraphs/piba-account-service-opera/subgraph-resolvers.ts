import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { getAccountList } from './services/get-account-list-service';
import { resetMemorableWord } from './services/reset-memorable-word-service';
import {
  getAccountBalanceSummary,
  getAccountBalanceSummaryV2
} from './services/get-account-balance-summary-service';
import { viewCustomerInvoicesV2 } from './services/view-invoices-v2-service';
import { viewAccountTransactions } from './services/view-account-transactions-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getAccountList: (_: any, args: any, context: any): Promise<any> => {
      return getAccountList(args, context);
    },
    getAccountBalanceSummary: (_: any, args: any, context: any): Promise<any> => {
      return getAccountBalanceSummary(args, context);
    },
    getAccountBalanceSummaryV2: (_: any, args: any, context: any): Promise<any> => {
      return getAccountBalanceSummaryV2(args, context);
    }
  },
  Mutation: {
    resetMemorableWord: (_: any, args: any, context: any): Promise<any> => {
      return resetMemorableWord(args, context);
    },
    viewCustomerInvoicesV2: (_: any, args: any, context: any): Promise<any> => {
      return viewCustomerInvoicesV2(args, context);
    },
    viewAccountTransactions: (_: any, args: any, context: any): Promise<any> => {
      return viewAccountTransactions(args, context);
    }
  }
};

export const pibaAccountServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

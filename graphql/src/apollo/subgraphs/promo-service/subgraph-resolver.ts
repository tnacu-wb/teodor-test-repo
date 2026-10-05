import { gql } from 'graphql-tag';
import { readSchema } from '../../utils/base-utils';
import { buildSubgraphSchema } from '@apollo/subgraph';
import {
  createPromoBatch,
  getPromoBatchById,
  getPromoBatchSummary,
  promoKind,
  markPromoBatchAsDownloadedApi
} from '../promo-service/services/promo-batch-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getBatchById: (_: any, { batchId }: { batchId: string }, context: any): Promise<any> => {
      return getPromoBatchById({ batchId }, context);
    },
    getBatchSummary: (_: any, args: any, context: any): Promise<any> => {
      const { page, size, sort } = args;
      return getPromoBatchSummary({ page, size, sort }, context);
    },
    markPromoBatchAsDownloaded: async (_: any, { batchId }: { batchId: string }, context: any) => {
      return await markPromoBatchAsDownloadedApi({ batchId }, context);
    },
    promoKind: (_: any, args: any, context: any): Promise<any> => {
      const { promoCode, country, channel, subChannel } = args;

      return promoKind(
        {
          promoCode,
          country,
          channel,
          subChannel
        },
        context
      );
    }
  },
  Mutation: {
    createPromoBatch: (_: any, args: any, context: any): Promise<any> => {
      return createPromoBatch(args, context);
    }
  }
};

export const promoServiceSubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

import { readSchema } from '../../utils/base-utils';
import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { getPromotionPanel } from './services/promotion-panel-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    promotionPanel: (_: any, args: any, context: any): Promise<any> => {
      return getPromotionPanel(args, context);
    }
  },
  Mutation: {}
};

export const promotionPanelSubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

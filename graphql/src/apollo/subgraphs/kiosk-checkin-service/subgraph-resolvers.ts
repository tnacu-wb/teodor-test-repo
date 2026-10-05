import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { checkIn } from './services/checkin-service';
import { roomAllocation } from './services/room-allocation-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {},
  Mutation: {
    checkIn: (_: any, args: any, context: any): Promise<any> => {
      return checkIn(args, context);
    },
    roomAllocation: (_: any, args: any, context: any): Promise<any> => {
      return roomAllocation(args, context);
    }
  }
};

export const kioskCheckinServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

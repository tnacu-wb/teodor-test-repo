import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  digitalKeyGenerateOtp,
  digitalKeyProvision,
  digitalKeyCheckIn,
  registerMobileDevice,
  googleWalletProvisioning
} from './services/digital-key-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Mutation: {
    digitalKeyGenerateOtp: (_: any, args: any, context: any): Promise<any> => {
      return digitalKeyGenerateOtp(args, context);
    },
    digitalKeyProvision: (_: any, args: any, context: any): Promise<any> => {
      return digitalKeyProvision(args, context);
    },
    digitalKeyCheckIn: (_: any, args: any, context: any): Promise<any> => {
      return digitalKeyCheckIn(args, context);
    },
    registerMobileDevice: (_: any, args: any, context: any): Promise<any> => {
      return registerMobileDevice(args, context);
    },
    googleWalletProvisioning: (_: any, args: any, context: any): Promise<any> => {
      return googleWalletProvisioning(args, context);
    }
  }
};

export const digitalKeyServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

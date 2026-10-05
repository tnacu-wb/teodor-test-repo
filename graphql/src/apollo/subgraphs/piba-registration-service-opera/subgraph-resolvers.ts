import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  getRegistrationInfo,
  GetRegistrationInfoArgs
} from './services/get-registration-info-service';
import {
  authenticateRegistration,
  AuthenticateRegistrationRequest
} from './services/authenticate-registration-service';
import { submitRegistration } from './services/submit-registration-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getRegistrationInfo: (_: any, args: GetRegistrationInfoArgs, context: any): Promise<any> => {
      return getRegistrationInfo(args, context);
    }
  },
  Mutation: {
    authenticateRegistration: (_: any, args: any, context: any): Promise<any> => {
      return authenticateRegistration(args, context);
    },
    submitRegistration: (_: any, args: any, context: any): Promise<any> => {
      return submitRegistration(args, context);
    }
  }
};

export const pibaRegistrationServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { innBRegistrationStepOne, innBRegistrationStepTwo } from './services/innb-register-service';
import { appsRegistration } from './services/apps-register-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {},
  Mutation: {
    innBRegistrationStepOne: (_: any, args: any, context: any): Promise<any> => {
      return innBRegistrationStepOne(args, context);
    },
    innBRegistrationStepTwo: (_: any, args: any, context: any): Promise<any> => {
      return innBRegistrationStepTwo(args, context);
    },
    appsRegistration: (_: any, args: any, context: any): Promise<any> => {
      return appsRegistration(args, context);
    }
  }
};

export const hotelRegisterServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

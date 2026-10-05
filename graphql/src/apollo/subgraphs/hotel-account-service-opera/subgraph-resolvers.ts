import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { getProfileDetails } from './services/get-profile-details-service';
import { updateProfileDetails } from './services/update-profile-details-service';
import {
  getAccountInfo,
  getNotifications,
  getNotificationsV2
} from './services/inn-business-service';
import { resetPassword } from './services/reset-password-service';
import { forgotPassword } from './services/forgot-password-service';
import { validateResetKey } from './services/validate-reset-key-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getProfileDetails: (_: any, args: any, context: any): Promise<any> => {
      return getProfileDetails(args, context);
    },
    getProfileDetailsV2: (_: any, args: any, context: any): Promise<any> => {
      return getProfileDetails(args, context);
    },
    getProfileDetailsV3: (_: any, args: any, context: any): Promise<any> => {
      return getProfileDetails(args, context);
    },
    getNotifications: (_: any, args: any, context: any): Promise<any> => {
      return getNotifications(args, context);
    },
    getNotificationsV2: (_: any, args: any, context: any): Promise<any> => {
      return getNotificationsV2(args, context);
    },
    getAccountInfo: (_: any, args: any, context: any): Promise<any> => {
      return getAccountInfo(args, context);
    }
  },
  Mutation: {
    updateProfileDetails: (_: any, args: any, context: any): Promise<any> => {
      return updateProfileDetails(args, context);
    },
    resetPassword: (_: any, args: any, context: any): Promise<any> => {
      return resetPassword(args, context);
    },
    forgotPassword: (_: any, args: any, context: any): Promise<any> => {
      return forgotPassword(args, context);
    },
    validateResetKey: (_: any, args: any, context: any): Promise<any> => {
      return validateResetKey(args, context);
    }
  }
};

export const hotelAccountServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { retrieveAnonymousNewsletterPreferences } from './services/anonymous-newsletter-preferences-service';
import { retrieveContactPreferences } from './services/get-contact-preferences-service';
import { updateContactPreferences } from './services/update-contact-preferences-service';
import { updateEmailPreferences } from './services/update-email-preferences-service';
import { updateEmailPreferencesWithChannelId } from './services/update-email-preferences-with-channel-id';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    anonymousNewsletterPreferences: (_: any, args: any, context: any): Promise<any> => {
      return retrieveAnonymousNewsletterPreferences(args, context);
    },
    getContactPreferences: (_: any, args: any, context: any): Promise<any> => {
      return retrieveContactPreferences(args, context);
    }
  },
  Mutation: {
    updateContactPreferences: (_: any, args: any, context: any): Promise<any> => {
      return updateContactPreferences(args, context);
    },
    updateEmailPreferences: (_: any, args: any, context: any): Promise<any> => {
      return updateEmailPreferences(args, context);
    },
    updateEmailPreferencesWithChannelId(_: any, args: any, context: any): Promise<any> {
      return updateEmailPreferencesWithChannelId(args, context);
    }
  }
};

export const marketingServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

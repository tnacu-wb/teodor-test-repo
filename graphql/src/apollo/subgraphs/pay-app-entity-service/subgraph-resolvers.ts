import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  deleteApplication,
  deleteApplicationCard,
  getApplicationDetails,
  getAppLookupData,
  getCompanyDetailsLookup,
  getPayApplications,
  initializeApplication,
  shareApplication,
  getApplicationCards,
  removeParticipant,
  submitApplication,
  appPreCheck,
  directDebit,
  updateResumeUrl,
  getDdSepaFormStatus
} from './services/pay-app-service';
import { updateAppContactDetails } from './services/update-app-contact-details-service';
import { getWorldlineUserPreferences } from './services/get-wl-user-preferences-service';
import { updateAppCompanyDetails } from './services/update-app-company-details-service';
import { addApplicationCard } from './services/application-card-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getPayApplications: (_: any, args: any, context: any): Promise<any> => {
      return getPayApplications(args, context);
    },
    getWorldlineUserPreferences: (_: any, args: any, context: any): Promise<any> => {
      return getWorldlineUserPreferences(args, context);
    },
    getAppLookupData: (_: any, args: any, context: any, info: any): Promise<any> => {
      return getAppLookupData(args, context, info);
    },
    companyDetailsLookup: (_: any, args: any, context: any): Promise<any> => {
      return getCompanyDetailsLookup(args, context);
    },
    getApplicationDetails: (_: any, args: any, context: any): Promise<any> => {
      return getApplicationDetails(args, context);
    },
    getApplicationCards: (_: any, args: any, context: any): Promise<any> => {
      return getApplicationCards(args, context);
    },
    appPreCheck: (_: any, args: any, context: any): Promise<any> => {
      return appPreCheck(args, context);
    },
    getDdSepaFormStatus: (_: any, args: any, context: any): Promise<any> => {
      return getDdSepaFormStatus(args, context);
    }
  },
  Mutation: {
    initializeApplication: (_: any, args: any, context: any): Promise<any> => {
      return initializeApplication(args, context);
    },
    updateAppContactDetails: (_: any, args: any, context: any): Promise<any> => {
      return updateAppContactDetails(args, context);
    },
    deleteApplication: (_: any, args: any, context: any): Promise<any> => {
      return deleteApplication(args, context);
    },
    updateAppCompanyDetails: (_: any, args: any, context: any): Promise<any> => {
      return updateAppCompanyDetails(args, context);
    },
    shareApplication: (_: any, args: any, context: any): Promise<any> => {
      return shareApplication(args, context);
    },
    deleteApplicationCard: (_: any, args: any, context: any): Promise<any> => {
      return deleteApplicationCard(args, context);
    },
    addApplicationCard: (_: any, args: any, context: any): Promise<any> => {
      return addApplicationCard(args, context);
    },
    removeParticipant: (_: any, args: any, context: any): Promise<any> => {
      return removeParticipant(args, context);
    },
    submitApplication: (_: any, args: any, context: any): Promise<any> => {
      return submitApplication(args, context);
    },
    submitApplicationV1: (_: any, args: any, context: any): Promise<any> => {
      return submitApplication(args, context);
    },
    directDebit: (_: any, args: any, context: any): Promise<any> => {
      return directDebit(args, context);
    },
    updateResumeUrl: (_: any, args: any, context: any): Promise<any> => {
      return updateResumeUrl(args, context);
    }
  }
};

export const payAppEntitySubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

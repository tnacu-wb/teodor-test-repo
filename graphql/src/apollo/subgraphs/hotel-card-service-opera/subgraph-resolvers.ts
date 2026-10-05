import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { retrievePaymentCards } from './services/get-payment-cards-service';
import { retrieveAllPIBACards } from './services/get-all-piba-cards-service';
import { retrievePIBACardDetails } from './services/get-piba-card-details-service';
import { retrieveAccountRegisteredUsers } from './services/get-account-registered-users-service';
import { updatePaymentCard } from './services/update-payment-card-service';
import { activateInnBPIBACard } from './services/activate-innb-piba-card-service';
import { updateInnBPIBACard } from './services/update-innb-piba-card-service';
import { initiateSaveCard } from './services/save-card-service';
import { addInnBPIBACard } from './services/add-innb-piba-card-service';
import { initiateAuthorizeCard } from './services/authorize-card-service';
import { deleteCompanyCard } from './services/delete-company-card-service';
import { cancelAndReplaceInnBPIBACard } from './services/cancel-and-replace-innb-piba-card-service';
import { inviteCardHolder } from './services/invite-cardholder-service';
import { getCostCentreDetails } from './services/get-cost-centre-details-service';
import { replaceCard } from './services/replace-card-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getPaymentCards: (_: any, args: any, context: any): Promise<any> => {
      return retrievePaymentCards(args, context);
    },
    getAllPIBACards: (_: any, args: any, context: any): Promise<any> => {
      return retrieveAllPIBACards(args, context);
    },
    getPIBACardDetails: (_: any, args: any, context: any): Promise<any> => {
      return retrievePIBACardDetails(args, context);
    },
    getAccountRegisteredUsers: (_: any, args: any, context: any): Promise<any> => {
      return retrieveAccountRegisteredUsers(args, context);
    },
    getCostCentreDetails: (_: any, args: any, context: any): Promise<any> => {
      return getCostCentreDetails(args, context);
    }
  },
  Mutation: {
    updatePaymentCard: (_: any, args: any, context: any): Promise<any> => {
      return updatePaymentCard(args, context);
    },
    activateInnBPIBACard: (_: any, args: any, context: any): Promise<any> => {
      return activateInnBPIBACard(args, context);
    },
    updateInnBPIBACard: (_: any, args: any, context: any): Promise<any> => {
      return updateInnBPIBACard(args, context);
    },
    saveCard: (_: any, args: any, context: any): Promise<any> => {
      return initiateSaveCard(args, context);
    },
    addInnBPIBACard: (_: any, args: any, context: any): Promise<any> => {
      return addInnBPIBACard(args, context);
    },
    authorizeCard: (_: any, args: any, context: any): Promise<any> => {
      return initiateAuthorizeCard(args, context);
    },
    deleteCompanyCard: (_: any, args: any, context: any): Promise<any> => {
      return deleteCompanyCard(args, context);
    },
    cancelAndReplaceInnBPIBACard: (_: any, args: any, context: any): Promise<any> => {
      return cancelAndReplaceInnBPIBACard(args, context);
    },
    inviteCardHolder: (_: any, args: any, context: any): Promise<any> => {
      return inviteCardHolder(args, context);
    },
    replaceCard: (_: any, args: any, context: any): Promise<any> => {
      return replaceCard(args, context);
    }
  }
};

export const hotelCardServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { deleteCustomQuestion } from './services/delete-custom-question-service';
import { createCompanyUserQuestion } from './services/create-company-user-question-service';
import { getCompanyDetails, updateCompanyDetails } from './services/company-details-service';
import { retrieveCompanyRegistrationQuestionsAndAnswers } from './services/get-company-registration-questions-and-answers-service';
import { retrieveEmployeeRegistrationQuestionsAndAnswers } from './services/get-employee-registration-questions-and-answers-service';
import { updateBusinessQuestions } from './services/update-business-questions-service';
import { updateBookingAllowances } from './services/update-booking-allowances-service';
import { updateCompanyUserQuestion } from './services/update-company-user-question-service';
import { updateBookingAlerts } from './services/update-booking-alerts-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    companyDetails: (_: any, args: any, context: any): Promise<any> => {
      return getCompanyDetails(args, context);
    },
    companyDetailsV2: (_: any, args: any, context: any): Promise<any> => {
      return getCompanyDetails(args, context);
    },
    companyDetailsV3: (_: any, args: any, context: any): Promise<any> => {
      return getCompanyDetails(args, context);
    },
    getCompanyRegistrationQuestionsAndAnswers: (_: any, args: any, context: any): Promise<any> => {
      return retrieveCompanyRegistrationQuestionsAndAnswers(args, context);
    },
    getEmployeeRegistrationQuestionsAndAnswers: (_: any, args: any, context: any): Promise<any> => {
      return retrieveEmployeeRegistrationQuestionsAndAnswers(args, context);
    }
  },
  Mutation: {
    deleteCustomQuestion: (_: any, args: any, context: any): Promise<any> => {
      return deleteCustomQuestion(args, context);
    },
    createCompanyUserQuestion: (_: any, args: any, context: any): Promise<any> => {
      return createCompanyUserQuestion(args, context);
    },
    updateBusinessQuestions: (_: any, args: any, context: any): Promise<any> => {
      return updateBusinessQuestions(args, context);
    },
    updateBookingAllowances: (_: any, args: any, context: any): Promise<any> => {
      return updateBookingAllowances(args, context);
    },
    updateCompanyDetails: (_: any, args: any, context: any): Promise<any> => {
      return updateCompanyDetails(args, context);
    },
    updateCompanyUserQuestion: (_: any, args: any, context: any): Promise<any> => {
      return updateCompanyUserQuestion(args, context);
    },
    updateBookingAlerts: (_: any, args: any, context: any): Promise<any> => {
      return updateBookingAlerts(args, context);
    }
  }
};

export const companyServiceOperaSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

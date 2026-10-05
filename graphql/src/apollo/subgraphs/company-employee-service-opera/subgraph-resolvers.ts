import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import {
  addEmployee,
  getActivationDetails,
  getEmployeeDetails,
  getEmployees,
  getEmployeesWithFilteringOptions,
  sendActivationEmail,
  updateEmployee
} from './services/employee-service';
import { readSchema } from '../../utils/base-utils';
import {
  approveRejectEmployee,
  getInnBusinessActivationDetails,
  getInnBusinessActivationDetailsV2,
  resendActivationEmail
} from './services/innbusiness-service';

// Function to read and parse a schema file
const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    getEmployees: (_: any, args: any, context: any): Promise<any> => {
      return getEmployees(args, context);
    },
    getEmployeesV2: (_: any, args: any, context: any): Promise<any> => {
      return getEmployees(args, context);
    },
    getEmployeeDetails: (_: any, args: any, context: any): Promise<any> => {
      return getEmployeeDetails(args, context);
    },
    getEmployeeDetailsV2: (_: any, args: any, context: any): Promise<any> => {
      return getEmployeeDetails(args, context);
    },
    getEmployeeDetailsV3: (_: any, args: any, context: any): Promise<any> => {
      return getEmployeeDetails(args, context);
    },
    getActivationDetails: (_: any, args: any, context: any): Promise<any> => {
      return getActivationDetails(args, context);
    },
    getInnBusinessActivationDetails: (_: any, args: any, context: any): Promise<any> => {
      return getInnBusinessActivationDetails(args, context);
    },
    getInnBusinessActivationDetailsV2: (_: any, args: any, context: any): Promise<any> => {
      return getInnBusinessActivationDetailsV2(args, context);
    },
    getEmployeesWithFilteringOptions: (_: any, args: any, context: any): Promise<any> => {
      return getEmployeesWithFilteringOptions(args, context);
    },
    getEmployeesWithFilteringOptionsV2: (_: any, args: any, context: any): Promise<any> => {
      return getEmployeesWithFilteringOptions(args, context);
    }
  },
  Mutation: {
    sendActivationEmail: (_: any, args: any, context: any): Promise<any> => {
      return sendActivationEmail(args, context);
    },
    addEmployee: (_: any, args: any, context: any): Promise<any> => {
      return addEmployee(args, context);
    },
    updateEmployee: (_: any, args: any, context: any): Promise<any> => {
      return updateEmployee(args, context);
    },
    resendActivationEmail: (_: any, args: any, context: any): Promise<any> => {
      return resendActivationEmail(args, context);
    },
    approveRejectEmployee: (_: any, args: any, context: any): Promise<any> => {
      return approveRejectEmployee(args, context);
    }
  }
};

export const companyEmployeeServiceSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

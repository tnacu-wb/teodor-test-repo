import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { managementInformationReport } from './services/management_information_report_service';
import { emergencyReport } from './services/emergency_report_service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    managementInformationReport: (_: any, args: any, context: any): Promise<any> => {
      return managementInformationReport(args, context);
    },
    emergencyReport: (_: any, args: any, context: any): Promise<any> => {
      return emergencyReport(args, context);
    }
  },
  Mutation: {}
};

export const companyReportingSubgraphResolvers = () =>
  buildSubgraphSchema([{ typeDefs, resolvers }]);

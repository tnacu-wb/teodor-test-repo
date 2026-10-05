import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import { searchCompanies } from './services/search-companies-service';
import { companyProfile } from './services/company-profile-service';
import { companyProfileById } from './services/company-profile-by-id-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    searchCompanies: (_: any, args: any, context: any): Promise<any> => {
      return searchCompanies(args, context);
    },
    companyProfile: (_: any, args: any, context: any): Promise<any> => {
      return companyProfile(args, context);
    },
    companyProfileById: (_: any, args: any, context: any): Promise<any> => {
      return companyProfileById(args, context);
    }
  },
  Mutation: {}
};

export const companyEntitySubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

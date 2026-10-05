import { gql } from 'graphql-tag';
import { buildSubgraphSchema } from '@apollo/subgraph';
import { readSchema } from '../../utils/base-utils';
import {
  backgroundCharge,
  confirmPreCheckIn,
  confirmPreCheckOut,
  getBasket,
  getBasketStatus,
  sendEmailOption,
  updateReservation
} from './services/basket-service';
import { getEckohRecordingStatus, initiateEckohPayment } from './services/ccui-eckoh-service';
import { initiateCcuiPayment, updateDiscount } from './services/ccui-payment-service';

const typeDefs = gql(readSchema(__dirname, './schema/schema.graphql'));

export const resolvers = {
  Query: {
    basket: (_: any, args: any, context: any): Promise<any> => {
      return getBasket(args, context);
    },
    basketStatus: (_: any, args: any, context: any): Promise<any> => {
      return getBasketStatus(args, context);
    },
    eckohRecordingStatus: (_: any, args: any, context: any): Promise<any> => {
      return getEckohRecordingStatus(args, context);
    }
  },
  Mutation: {
    initiateEckohPayment: (_: any, args: any, context: any): Promise<any> => {
      return initiateEckohPayment(args, context);
    },
    initiateCcuiPayment: (_: any, args: any, context: any): Promise<any> => {
      return initiateCcuiPayment(args, context);
    },
    confirmPreCheckIn: (_: any, args: any, context: any): Promise<any> => {
      return confirmPreCheckIn(args, context);
    },
    sendEmailOption: (_: any, args: any, context: any): Promise<any> => {
      return sendEmailOption(args, context);
    },
    updateDiscount: (_: any, args: any, context: any): Promise<any> => {
      return updateDiscount(args, context);
    },
    updateReservation: (_: any, args: any, context: any): Promise<any> => {
      return updateReservation(args, context);
    },
    confirmPreCheckOut: (_: any, args: any, context: any): Promise<any> => {
      return confirmPreCheckOut(args, context);
    },
    backgroundCharge: (_: any, args: any, context: any): Promise<any> => {
      return backgroundCharge(args, context);
    }
  }
};

export const basketSubgraphResolvers = () => buildSubgraphSchema([{ typeDefs, resolvers }]);

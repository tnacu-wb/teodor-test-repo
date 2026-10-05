import { post } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { endpoints } from './base-service';

export const checkIn = async ({ checkInCriteria }: { checkInCriteria: any }, context: any) => {
  try {
    return await post(endpoints.CHECKIN, checkIn, checkInCriteria, context);
  } catch (error: Error | any) {
    handleError(error, checkInCriteria);
  }
};

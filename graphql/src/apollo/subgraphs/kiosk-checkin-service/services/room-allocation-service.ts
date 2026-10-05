import { post } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';

export const roomAllocation = async (
  { roomAllocationCriteria }: { roomAllocationCriteria: any },
  context: any
) => {
  try {
    return await post(endpoints.ROOM_ALLOCATION, roomAllocation, roomAllocationCriteria, context);
  } catch (error: Error | any) {
    handleError(error, roomAllocationCriteria);
  }
};

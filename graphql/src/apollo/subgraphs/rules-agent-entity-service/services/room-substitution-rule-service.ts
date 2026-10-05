import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';

/**
 * This method is used to get the room substitution limitations
 * @param adult The number of adults
 * @param children The number of children
 * @param roomType The room type
 * @param pms The pms type
 * @param channel The channel id
 * @param context The context object
 * @returns The response containing room substitution limitations
 */
export const getRoomSubstitutionLimitation = async (args: any, context: any): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'adults', value: args.roomSubstitutionCriteria.adults, required: true },
      { key: 'children', value: args.roomSubstitutionCriteria.children, required: true },
      { key: 'roomType', value: args.roomSubstitutionCriteria.roomType, required: true },
      { key: 'pms', value: args.roomSubstitutionCriteria.pms, required: true },
      { key: 'channel', value: args.roomSubstitutionCriteria.channel, required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);
    const response = await get(
      endpoints.ROOM_SUBSTITUTION_LIMITATIONS,
      getRoomSubstitutionLimitation,
      finalMap,
      context
    );
    const roomSubstitutionLimitations = {
      substitutionList: response['substitution-list']
    };
    return roomSubstitutionLimitations;
  } catch (error) {
    handleError(error, args);
  }
};

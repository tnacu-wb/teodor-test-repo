import { addFieldsToMap } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';

export const getMealsAem = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldsToAdd = [
      { key: 'hotelId', value: args.hotelId, required: false },
      { key: 'language', value: args.language, required: false },
      { key: 'country', value: args.country, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(endpoints.GET_MEALS_AEM, getMealsAem, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

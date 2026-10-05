import { addFieldsToMap, replaceServiceEndpoint } from '../../../utils/base-utils';
import { get } from '../../../client/rest-client';
import { endpoints } from './base-service';
import { handleError } from '../../../exception/error-handler';
import { PipelineContext } from '../../../pipeline/context/PipelineContext';

export const getLogoAem = async (
  args: any,
  context: any,
  pipelineContext: PipelineContext
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};

  try {
    const serviceEndpoint = replaceServiceEndpoint(
      endpoints.GET_LOGO_AEM,
      '{hotelId}',
      args.hotelId
    );

    const fieldsToAdd = [
      { key: 'language', value: args.language, required: false },
      { key: 'country', value: args.country, required: false }
    ];
    addFieldsToMap(fieldsToAdd, finalMap);
    return await get(serviceEndpoint, getLogoAem, finalMap, context);
  } catch (error) {
    handleError(error, finalMap);
  }
};

import { handleError } from '../../../exception/error-handler';
import { addFieldsToMap } from '../../../utils/base-utils';
import { endpoints } from './base-service';
import { get } from '../../../client/rest-client';
import { ActionContextKeys } from '../../booking-information-pipeline/actions/ActionContextKeys';

export const getRoomTypeInformation = async (
  {
    brand,
    language,
    country,
    hotelId
  }: { brand: string; language: string; country: string; hotelId?: string },
  context: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    const fieldToAdd = [
      { key: 'brand', value: brand, required: true },
      { key: 'language', value: language, required: true },
      { key: 'country', value: country, required: true },
      { key: 'hotelId', value: hotelId, required: false }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.ROOM_TYPE_INFORMATION, getRoomTypeInformation, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

export const fetchRoomType = async (
  args: any,
  context: any,
  pipelineContext: any
): Promise<any> => {
  let finalMap: { [key: string]: any } = {};
  try {
    let hotelInfo = pipelineContext.get(ActionContextKeys.HOTEL_INFORMATION_FOR_BOOKING);
    const fieldToAdd = [
      { key: 'language', value: args.language, required: true },
      { key: 'country', value: args.country, required: true },
      { key: 'brand', value: hotelInfo.brand.toLowerCase(), required: true }
    ];
    addFieldsToMap(fieldToAdd, finalMap);

    return await get(endpoints.ROOM_TYPE, fetchRoomType, finalMap, context);
  } catch (error: Error | any) {
    handleError(error, finalMap);
  }
};

import { ActionContextKeys } from '../actions/ActionContextKeys';
import { PipelineManager } from '../../../pipeline/manager/PipelineManager';
import { logPipelineError } from '../../../utils/base-utils';

import { PipelineAvailabilityInfoCriteria } from '../models/availability-info-criteria';
import { HotelAvailabilityPipelineAction } from '../actions/HotelAvailabilityPipelineAction';
import { RateInformationPipelineAction } from '../actions/RateInformationPipelineAction';
import { RoomTypesInformationPipelineAction } from '../actions/RoomTypesInformationPipelineAction';
import { RoomClassConfigurationPipelineAction } from '../actions/RoomClassConfigurationPipelineAction';

export const getHotelAvailabilityInfo = async (
  criteria: PipelineAvailabilityInfoCriteria,
  context: any
): Promise<any> => {
  const pipelineManager = new PipelineManager();
  const actions = [
    new HotelAvailabilityPipelineAction(),
    new RateInformationPipelineAction(),
    new RoomTypesInformationPipelineAction(),
    new RoomClassConfigurationPipelineAction()
  ];

  const pipelineContext = await pipelineManager.manage(actions, criteria, context);
  try {
    const availability = pipelineContext.get(ActionContextKeys.HOTEL_AVAILABILITY);
    const classifications = pipelineContext.get(ActionContextKeys.RATE_INFORMATION);
    const roomTypes = pipelineContext.get(ActionContextKeys.ROOM_TYPE_INFORMATION);
    const roomClassConfig = pipelineContext.get(ActionContextKeys.ROOM_CLASS_CONFIG);

    let availabilityInfo = availability;

    const rates: any[] = [];

    availability.roomRates.forEach((rate: any) => {
      let ratePlanCode = rate.ratePlanCode;
      let hotelAvailabilityRoomRateInfo = rate;
      if (ratePlanCode) {
        let rateClassificationInfo = classifications.rateClassifications.find(
          (rateInfo: any) => rateInfo.ratePlanCode === ratePlanCode
        );
        if (rateClassificationInfo) {
          hotelAvailabilityRoomRateInfo.rateClassification = rateClassificationInfo;
        }

        hotelAvailabilityRoomRateInfo.roomTypes = getRoomDetails(rate, roomTypes, roomClassConfig);
      }

      rates.push(hotelAvailabilityRoomRateInfo);
    });

    availabilityInfo.roomRates = rates;
    return availabilityInfo;
  } catch (error: Error | any) {
    logPipelineError(error, criteria, getHotelAvailabilityInfo);
    throw error;
  }
};

function getRoomDetails(rate: any, roomTypes: any, roomClassConfig: any) {
  const typesList: any[] = [];
  rate.roomTypes.forEach((roomType: any) => {
    let roomTypeDetails = roomType;
    const roomTypesList: any[] = [];

    roomType.rooms.forEach((room: any) => {
      let roomTypeInfo = room;
      if (roomTypeInfo.pmsRoomType) {
        let roomTypeDetails = roomTypes.roomTypes.find((room: any) =>
          room.roomTypeCode.includes(roomTypeInfo.pmsRoomType)
        );
        if (roomTypeDetails) {
          roomTypeInfo.roomTypeDetails = roomTypeDetails;
        }
      }
      if (roomTypeInfo.roomClass) {
        let roomClassDetails = roomClassConfig.roomClassConfig.find(
          (conf: any) => conf.code === roomTypeInfo.roomClass
        );
        if (roomClassDetails) {
          roomTypeInfo.roomClassOrder = roomClassDetails;
        }
      }
      roomTypesList.push(roomTypeInfo);
    });

    roomTypeDetails.roomTypeDetails = roomTypesList;
    typesList.push(roomTypeDetails);
  });

  return typesList;
}

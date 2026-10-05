import axios from 'axios';
import { getAccessToken, getConfigForEnv } from './auth';
import { Environment } from './environments';
import { validateHotelId, validateRatePlanCode } from './validation';

const ROOM_TYPE_BATCH_SIZE = 10;

export interface RatePlanPayload {
  dailyRateScheduleRange: {
    hotelId: string;
    ratePlanCode: string;
    roomTypes: string[];
    roomClasses: string[];
    dateRange: {
      timeSpan: { startDate: string; endDate: string };
      sunday: boolean;
      monday: boolean;
      tuesday: boolean;
      wednesday: boolean;
      thursday: boolean;
      friday: boolean;
      saturday: boolean;
    };
    incrementFlag: boolean;
    rateAmounts: {
      onePersonRate: string;
      twoPersonRate?: string | null;
      extraPersonRate?: string | null;
      overrideFloorAmount: boolean;
    };
  };
}

export async function updateDailySchedules(
  hotelId: string,
  ratePlanCode: string,
  payload: RatePlanPayload,
  env: Environment
) {
  const roomTypes = payload.dailyRateScheduleRange?.roomTypes || [];
  if (roomTypes.length <= ROOM_TYPE_BATCH_SIZE) {
    return await sendRequest(hotelId, ratePlanCode, payload, env);
  }

  const batches: string[][] = [];
  for (let i = 0; i < roomTypes.length; i += ROOM_TYPE_BATCH_SIZE) {
    batches.push(roomTypes.slice(i, i + ROOM_TYPE_BATCH_SIZE));
  }

  const results = [];
  for (let i = 0; i < batches.length; i++) {
    const batchPayload: RatePlanPayload = {
      dailyRateScheduleRange: { ...payload.dailyRateScheduleRange, roomTypes: batches[i] },
    };
    const result = await sendRequest(hotelId, ratePlanCode, batchPayload, env);
    results.push({ batch: i + 1, roomTypes: batches[i], result });
  }
  return {
    message: `Successfully updated ${batches.length} batches covering ${roomTypes.length} room types`,
    batches: results,
  };
}

async function sendRequest(hotelId: string, ratePlanCode: string, payload: RatePlanPayload, env: Environment) {
  const safeHotelId = validateHotelId(hotelId);
  const safeRatePlanCode = validateRatePlanCode(ratePlanCode);
  const token = await getAccessToken(env);
  const config = getConfigForEnv(env);
  const url = `${config.baseUrl}/rtp/v0/hotels/${safeHotelId}/ratePlans/${safeRatePlanCode}/dailySchedules`;

  const response = await axios.put(url, payload, {
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      'x-app-key': config.appKey,
      'x-hotelid': safeHotelId,
      'x-enterprise-id': config.enterpriseId,
    },
    timeout: 120000,
  });
  return response.data;
}

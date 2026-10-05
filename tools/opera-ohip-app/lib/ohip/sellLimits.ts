import axios from 'axios';
import { getAccessToken, getConfigForEnv } from './auth';
import { Environment } from './environments';
import { validateHotelId } from './validation';

export interface SellLimitsPayload {
  sellLimitsByDateRange: Array<{
    sellLimitDateRanges: Array<{
      actionType: string;
      startDate: string;
      endDate: string;
      sunday: boolean;
      monday: boolean;
      tuesday: boolean;
      wednesday: boolean;
      thursday: boolean;
      friday: boolean;
      saturday: boolean;
      amount: string;
      flatOrPercentage: string;
    }>;
    hotelId: string;
    codeCategory: string;
    codeValue: string;
  }>;
}

export async function updateSellLimitsByDateRange(hotelId: string, payload: SellLimitsPayload, env: Environment) {
  const safeHotelId = validateHotelId(hotelId);
  const token = await getAccessToken(env);
  const config = getConfigForEnv(env);
  const url = `${config.baseUrl}/inv/v0/hotels/${safeHotelId}/sellLimitsByDateRange`;

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

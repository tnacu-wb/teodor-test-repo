import axios from 'axios';
import { getAccessToken, getConfigForEnv } from './auth';
import { Environment } from './environments';
import { validateHotelId } from './validation';

export interface RestrictionsPayload {
  hotelId: string;
  date: string;
}

export async function updateRestrictions(hotelId: string, payload: RestrictionsPayload, env: Environment) {
  const safeHotelId = validateHotelId(hotelId);
  const token = await getAccessToken(env);
  const config = getConfigForEnv(env);
  const url = `${config.baseUrl}/par/v0/hotels/${safeHotelId}/restrictions`;

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

import { BASE_EXPERIENCE_API } from '@WB-playwright/constants'
import { HotelAvailabilityInput } from '@WB-playwright/types';
import * as request from 'superagent';

import { createHotelAvailability, createHotelInventory } from './../response';
import * as fs from 'fs/promises';

let authToken: string | null = null;

export const GraphQlUtils = {
  async setAuthToken(cookies: any) {
    const tokenCookie = (await cookies).find((cookie: any) => cookie.name === 'id_token_cookie');
    if (tokenCookie) {
      return (authToken = `Bearer ${tokenCookie.value}`);
    } else {
      throw new Error('Token cookie is undefined');
    }
  },

  async makeGraphqlCall(queryFile: string, variables = {}, failIfError = true) {
    const path = `./playwright/fixtures/${queryFile}`;
    const query = (await fs.readFile(path)).toString();
    const MAX_RETRIES = 5;
    let response;

    // retry making the call if the response returns 500 errors
    for (let retry = 0; retry < MAX_RETRIES; retry++) {
      response = await request
        .post(BASE_EXPERIENCE_API)
        .set('Authorization', authToken || '')
        .set('X-WHIT-API-KEY', process.env.graphQlXWhitApiKey || '')
        .send({ query, variables })
        .catch((err) => {
          console.log(JSON.stringify(err));
          if (retry === MAX_RETRIES - 1) {
            throw err;
          }
        });

      if (
        !failIfError ||
        (response &&
          response.statusCode == 200 &&
          !(response.body.errors && response.body.errors[0].errorType >= '500'))
      ) {
        break;
      }
      console.log(`Retrying call: [ ${retry + 1}/${MAX_RETRIES} retry ]`);
      // add some delay between retries
      await new Promise((r) => setTimeout(r, 2000));
    }
    if (!response || response.statusCode !== 200 || response.body.errors) {
      console.log(`Call request: ${JSON.stringify(response?.request)}`);
      console.log(`Error response: ${JSON.stringify(response?.body)}`);
    }
    if (failIfError) {
      throw new Error(`Error response: ${JSON.stringify(response?.body)}`);
    }

    return response;
  },

  async graphqlGetHotelInventory(hotelId: string, dateRangeStart: string, dateRangeEnd: string) {
    const queryFile = 'getHotelInventory.graphql';
    const variables = {
      hotelId: hotelId,
      dateRangeStart: dateRangeStart,
      dateRangeEnd: dateRangeEnd,
    };

    const response = await this.makeGraphqlCall(queryFile, variables);
    if (!response || !response.body) {
      throw new Error('Failed to fetch hotel inventory');
    }
    const hotelInventory = response.body.data.hotelInventory;
    return createHotelInventory(hotelInventory);
  },

  async graphqlGetSingleHotelAvailability(
    hotelAvailabilityInput: HotelAvailabilityInput,
    failIfError = true,
    companyId = undefined
  ) {
    const queryFile = 'hotelAvailabilityBB.graphql';
    const arrival = hotelAvailabilityInput.arrival;
    const departure = hotelAvailabilityInput.departure;
    const requestRooms = [];
    for (const room of hotelAvailabilityInput.rooms) {
      if (typeof room.roomType !== 'string' && room.roomType.id != undefined) {
        const newRoom = { ...room };
        newRoom.roomType = room.roomType.id;
        requestRooms.push(newRoom);
      }
    }
    const variables: {
      arrival: string;
      departure: string;
      rooms: any[];
      bookingChannel: any;
      companyId: string | undefined;
      hotelId?: string;
    } = {
      hotelId: hotelAvailabilityInput.hotelCode,
      arrival: arrival,
      departure: departure,
      rooms: requestRooms,
      bookingChannel: hotelAvailabilityInput.bookingChannel,
      companyId: companyId,
    };

    const response = await this.makeGraphqlCall(queryFile, variables, failIfError);
    if (!response) {
      throw new Error('Failed to get a response from makeGraphqlCall');
    }
    const responseBody = response.body;
    if (responseBody.errors) {
      return responseBody;
    }
    return createHotelAvailability(responseBody.data.hotelAvailability);
  },
};

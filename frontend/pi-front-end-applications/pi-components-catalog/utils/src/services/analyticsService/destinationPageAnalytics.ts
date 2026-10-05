import {
  DlpAnalytics,
  HotelInformationOptional,
  DlpAnalyticsRequest,
  DlpAnalyticsSectionObject,
} from '@whitbread-eos/api';
import { DlpItem } from '@whitbread-eos/api/dist/types/graphql';

import analytics from './analytics';

const DESTINATION_PAGE_NAME = 'premier inn: seo: hotels in';

const updateDestinationPageAnalytics = ({
  locationName,
  hotelDisplayedCount,
  destinations,
  hotels,
  filterType,
  thingsToDo,
  mapReference,
  premierPlusLabel,
  promos,
}: DlpAnalyticsRequest) => {
  const hotelsAnalytics = hotels.map((hotel: HotelInformationOptional, i: number) => {
    const hotelLabels = [];
    if (hotel.hotelFacilities?.find((facility) => facility.code === 'PRR')) {
      hotelLabels.push(premierPlusLabel);
    }
    if (hotel.messagingFlag?.text && hotel.messagingFlag.text != 'hub') {
      hotelLabels.push(hotel.messagingFlag.text);
    }
    return {
      hotelName: hotel.name,
      hotelCode: hotel.hotelId,
      orderPosition: i + 1,
      hotelLabels,
      tripAdvisorRating: hotel.tripAdvisorReviews?.rating,
      tripAdvisorReviewsCount: hotel.tripAdvisorReviews?.numberOfReviews,
    };
  });

  const destinationsListAnalytics = destinations?.map((destination: DlpItem, i: number) => ({
    destinationLocationName: destination.title,
    destinationPosition: i + 1,
  }));

  const analyticsThingsToDo = (thingsToDo?.items?.map((toDo) => ({
    tileName: toDo.title,
    tilePosition: toDo.order,
  })) ?? []) as DlpAnalyticsSectionObject[];

  const analyticsTravelGuides = (promos?.map((promo) => ({
    tileName: promo.title,
    tilePosition: promo.order,
  })) ?? []) as DlpAnalyticsSectionObject[];

  const dlpAnalytics = {
    locationName,
    hotelDisplayedCount,
    destinations: destinationsListAnalytics,
    hotels: hotelsAnalytics,
    filterType,
    thingsToDo: analyticsThingsToDo,
    travelGuides: analyticsTravelGuides,
    mapReference,
    filterSectionOpened: false,
    filtersCleared: false,
    searchFilter: '',
  } as DlpAnalytics;

  return analytics.update({
    pageName: `${DESTINATION_PAGE_NAME} ${locationName}`,
    dlp: dlpAnalytics,
  });
};

export default updateDestinationPageAnalytics;

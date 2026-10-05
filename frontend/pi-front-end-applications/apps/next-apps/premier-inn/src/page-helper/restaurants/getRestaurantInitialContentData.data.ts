import { QueryClient, dehydrate } from '@tanstack/react-query';
import {
  GET_LOCATIONS_DETAILS,
  GET_OCCASIONS_DETAILS,
  GET_OUTLETS,
  GET_PAGE_CONTENT,
} from '@whitbread-eos/api';
import {
  QueriesLogger,
  getDefaultSessionTracing,
  logger,
  graphQLRequestRestaurants,
} from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import getConfig from 'next/config';

interface GetRestaurantInitialContantDataParams extends GetServerSidePropsContext {
  queryClient: QueryClient;
  restaurantBrandNameForAemApi: string;
  location: string;
  subLocation: string;
  restaurantBrandName: string;
}

interface Outlet {
  aztecSiteReference: string;
  defaultOccasionId: string | null;
  features: {
    bookableAreas: boolean;
  };
  id: string;
  timezone: string;
  name: string;
}

export async function getRestaurantInitialContentDataFn({
  queryClient,
  restaurantBrandNameForAemApi,
  location,
  subLocation,
  restaurantBrandName,
  query,
  req,
  res,
}: GetRestaurantInitialContantDataParams) {
  const cookies = new Cookies(req, res);
  const sessionTracing = getDefaultSessionTracing(cookies);
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const { fetchQuery, prefetchQuery } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | Restaurants | Restaurants Landing Page'
  );
  let siteId = '';
  let occasionId = '';
  let restaurantId = '';
  logger.info({
    label: 'AEM Data for TB:START_DATA_FETCHING',
    msg: {
      query,
      ...sessionTracing,
    },
  });

  try {
    await prefetchQuery(['getPageContent'], () => graphQLRequestRestaurants(GET_PAGE_CONTENT));
    logger.info({
      label: 'TB:PagesLabelContent success:Pages Label from AEM Content success api call',
      msg: {
        parameter: restaurantBrandNameForAemApi,
      },
    });
  } catch (error) {
    // Log errors
    logger.info({
      label: 'TB:PagesLabelContent error:Pages Label from AEM Content error',
      msg: {
        error,
      },
    });
  }
  try {
    await prefetchQuery(['getLocationsDetails', restaurantBrandNameForAemApi], () =>
      graphQLRequestRestaurants(GET_LOCATIONS_DETAILS, {
        restaurant: restaurantBrandNameForAemApi,
        location: '',
        subLocation: '',
      })
    );
    logger.info({
      label: 'TB:LocationsDetails success:locations details data for specific restaurant',
      msg: {
        parameters: {
          restaurantBrandNameForAemApi,
        },
      },
    });
  } catch (error) {
    logger.info({
      label: 'TB:LocationsDetails error:locations details Error for specific restaurant',
      msg: {
        error,
        parameters: {
          restaurantBrandNameForAemApi,
        },
      },
    });
  }
  try {
    const { locations } = await fetchQuery(
      ['getLocationDetails', restaurantBrandNameForAemApi, location || '', subLocation || ''],
      () =>
        graphQLRequestRestaurants(GET_LOCATIONS_DETAILS, {
          restaurant: restaurantBrandNameForAemApi,
          location: '',
          subLocation: subLocation || '',
        })
    );
    restaurantId = locations[0]?.id;
    logger.info({
      label:
        'TB:LocationDetails success:location details data for specific restaurant and location',
      msg: {
        parameters: {
          location,
          subLocation,
          restaurantBrandNameForAemApi,
        },
      },
    });
  } catch (error: any) {
    // Check if error is a 404
    const is404Error =
      error?.response?.errors?.[0]?.errorType === '404' ||
      error?.response?.errors?.[0]?.message?.includes('"statusCode":404');

    if (is404Error) {
      return { resourceNotFound: true };
    }

    logger.info({
      label: 'TB:LocationDetails error:location details Error for specific restaurant and location',
      msg: {
        error,
        parameters: {
          location,
          subLocation,
          restaurantBrandNameForAemApi,
        },
      },
    });
  }
  try {
    const { outlets } = await fetchQuery(['getOutletsDetails', '', '', restaurantId], () =>
      graphQLRequestRestaurants(GET_OUTLETS, {
        id: restaurantId,
        location: '',
      })
    );

    siteId = outlets.companies[0].sites[0].id;

    logger.info({
      label: 'TB:OutletsDetails success:Outlet details data for specific location',
      msg: {
        parameters: {
          location,
          subLocation,
          restaurantId,
          restaurantBrandName,
        },
      },
    });
  } catch (error) {
    logger.info({
      label: 'TB:OutletsDetails error:Outlets details error for specific location',
      msg: {
        error,
        parameters: {
          location,
          subLocation,
          restaurantId,
          restaurantBrandName,
        },
      },
    });
  }
  try {
    const { occasions } = await fetchQuery(['getOccasionsDetails', siteId], () =>
      graphQLRequestRestaurants(GET_OCCASIONS_DETAILS, { siteId: siteId })
    );
    const occasion = occasions.occasions.find((item: Outlet) => {
      return item.name == publicRuntimeConfig.NEXT_PUBLIC_OCCASION_NAME;
    });
    if (occasion.id) {
      occasionId = occasion.id;
    }
    logger.info({
      label:
        'TB:OccasionsDetails success:Occasions details data for specific restaurant and location',
      msg: {
        parameters: {
          siteId,
          occasionId,
        },
      },
    });
  } catch (error) {
    logger.info({
      label:
        'TB:OccasionsDetails error:Occasions details error for specific restaurant and location',
      msg: {
        error,
        parameters: {
          siteId,
          occasionId,
        },
      },
    });
  }
  logger.info({
    label: 'AEM Data for TB:END_DATA_FETCHING',
    msg: {
      query,
      ...sessionTracing,
    },
  });
  return {
    dehydratedState: dehydrate(queryClient),
    occasionId: occasionId,
    siteId: siteId,
  };
}

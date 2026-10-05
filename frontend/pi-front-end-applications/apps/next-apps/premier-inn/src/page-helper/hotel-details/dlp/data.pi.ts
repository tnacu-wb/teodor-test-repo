import { dehydrate, QueryClient } from '@tanstack/react-query';
import {
  GET_HOTELS_INFORMATION,
  GET_STATIC_CONTENT,
  SITE_LEISURE,
  GET_DESTINATION_PAGE_INFORMATION,
  OrderedHotelCode,
  getInnBusinessCommonIcons,
  SEARCH_INFORMATION_RESULTS,
} from '@whitbread-eos/api';
import { getGQLClient, graphQLRequest, QueriesLogger, WB_SESSION_ID } from '@whitbread-eos/utils';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';

interface Props extends GetServerSidePropsContext {
  queryClient: QueryClient;
  language: string;
  country: string;
  featureToggles: { [key: string]: boolean };
}

export const INITIAL_NUMBER_OF_RENDERED_CARDS = 12;
export const GET_HOTELS_INFORMATION_QUERY_KEY = 'getHotelsInformation';

const createDLPPiDataLoaderFn = async ({
  queryClient,
  language,
  country,
  req,
  res,
  query,
}: Props) => {
  const cookies = new Cookies(req, res);
  const sessionId = cookies.get(WB_SESSION_ID);
  const client = getGQLClient(sessionId);
  const { fetchQuery, logQueries } = new QueriesLogger(
    queryClient,
    req,
    res,
    query,
    'PI | DLP | Destination Landing Page'
  );

  const slug = query?.slug as string[];
  const dlpPath = slug?.join('/')?.replace('.html', '');

  const dlpQueryKey = ['dlpInformation', language, country, dlpPath];
  const getDlpInformation = fetchQuery(dlpQueryKey, () =>
    graphQLRequest(
      GET_DESTINATION_PAGE_INFORMATION,
      {
        country,
        language,
        dlpPath,
      },
      undefined,
      undefined,
      client
    )
  );

  const data = await getDlpInformation;

  const hotelIds = data.dlpInformation.hotels
    .sort((a: OrderedHotelCode, b: OrderedHotelCode) => a.order - b.order)
    .map((hotel: OrderedHotelCode) => hotel.code);
  const { latitude: latitudeRef, longitude: longitudeRef } = data.dlpInformation.coordinates || {
    latitude: 0,
    longitude: 0,
  };

  const queryParams = {
    country,
    language,
    hotelIds,
    latitudeRef,
    longitudeRef,
    tripAdvisorDataRequired: true,
  };

  let promises = [];

  const hotelsInformationQueryKey = [
    GET_HOTELS_INFORMATION_QUERY_KEY,
    queryParams.country,
    queryParams.language,
    queryParams.hotelIds,
    queryParams.longitudeRef,
    queryParams.latitudeRef,
    queryParams.tripAdvisorDataRequired,
  ];

  if (hotelIds.length > 0) {
    const getHotelsInformation = fetchQuery(hotelsInformationQueryKey, () =>
      graphQLRequest(
        GET_HOTELS_INFORMATION,
        {
          ...queryParams,
        },
        undefined,
        undefined,
        client
      )
    );
    promises.push(getHotelsInformation);
  }

  const getStaticContentQuery = fetchQuery(['GetStaticContent', language, country], () =>
    graphQLRequest(
      GET_STATIC_CONTENT,
      {
        language,
        country,
        site: SITE_LEISURE,
        businessBooker: false,
      },
      undefined,
      undefined,
      client
    )
  );

  const getCommonItemsQuery = fetchQuery(['GetCommonItems', language, country], () =>
    graphQLRequest(
      getInnBusinessCommonIcons(),
      {
        country,
        language,
      },
      undefined,
      undefined,
      client
    )
  );

  const searchInformationQueryKey = ['searchInformation', language, country];
  const getSearchInformationQuery = fetchQuery(searchInformationQueryKey, () =>
    graphQLRequest(
      SEARCH_INFORMATION_RESULTS,
      {
        country,
        language,
      },
      undefined,
      undefined,
      client
    )
  );

  promises = [
    ...promises,
    getStaticContentQuery,
    getDlpInformation,
    getCommonItemsQuery,
    getSearchInformationQuery,
  ];

  await Promise.all(promises);

  logQueries(performance.now());

  return {
    dehydratedState: dehydrate(queryClient),
    dlpQueryKey,
    hotelsInformationQueryKey,
    searchInformationQueryKey,
  };
};

export default createDLPPiDataLoaderFn;

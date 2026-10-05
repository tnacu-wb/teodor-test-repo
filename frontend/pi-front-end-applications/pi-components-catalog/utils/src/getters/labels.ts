import { QueryClient } from '@tanstack/react-query';
import { getStaticLabels, CountryCode } from '@whitbread-eos/api';
import type { ReservationRoomType, StaticContent } from '@whitbread-eos/api';
import getConfig from 'next/config';

import { transformLabels } from '../formatters';
import { graphQLRequest } from '../hooks';
import { logger } from '../logger/logger';

interface Options {
  language: string;
  queryClient: QueryClient;
  fetchPreCheckInLabel?: boolean;
  forceRefetch?: boolean;
}

export function getServerSideCustomLocale(locale: string) {
  const language = locale === CountryCode.GB ? 'en' : 'de';
  const country = locale === CountryCode.GB ? 'gb' : 'de';

  return { language, country };
}

declare global {
  /* eslint-disable */
  // noinspection ES6ConvertVarToLetConst
  var WB: {
    cache: {
      enCommon: {
        value: any;
        expiringTime: any;
      };
      deCommon: {
        value: any;
        expiringTime: any;
      };
      enLabels: {
        value: any;
        expiringTime: any;
      };
      deLabels: {
        value: any;
        expiringTime: any;
      };
      unleashDefinitions?: {
        value: any;
        expiringTime: any;
      };
    };
  };
}
type LabelsKey = 'enLabels' | 'deLabels';

async function getStaticContentLabels(
  queryClient: QueryClient,
  fetchPreCheckInLabel: boolean = false
) {
  const query = getStaticLabels(fetchPreCheckInLabel);

  const enContent = queryClient.fetchQuery({
    queryKey: ['GetStaticContentLabels', 'en', 'gb'],
    queryFn: () =>
      graphQLRequest(query, {
        language: 'en',
        country: 'gb',
      }) as Promise<any>,
  });
  const deContent = queryClient.fetchQuery({
    queryKey: ['GetStaticContentLabels', 'de', 'de'],
    queryFn: () =>
      graphQLRequest(query, {
        language: 'de',
        country: 'de',
      }) as Promise<any>,
  });
  const [enLabels, deLabels] = await Promise.all([enContent, deContent]);

  return { enLabels, deLabels };
}

function setLabelsInCache(enLabels: StaticContent, deLabels: StaticContent, startTime: number) {
  const cache = global.WB.cache;
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const expiringTime =
    startTime + Number(publicRuntimeConfig.NEXT_APP_STATIC_CONTENT_CACHE_TTL) * 1000;
  cache[`enLabels`] = {
    value: {
      ...transformLabels(enLabels),
    },
    expiringTime,
  };
  cache[`deLabels`] = {
    value: {
      ...transformLabels(deLabels),
    },
    expiringTime,
  };
}

export async function getI18nLabels({
  language,
  queryClient,
  fetchPreCheckInLabel = false,
  forceRefetch = false,
}: Options) {
  if (language !== 'en' && language !== 'de') {
    throw new Error('Unsupported labels language; expected en or de');
  }

  const now = new Date().getTime();

  logger.trace({
    label: 'FETCH_LABELS_START',
  });

  const cache = global.WB.cache;

  const labelsKey: LabelsKey = language === 'en' ? 'enLabels' : 'deLabels';

  const shouldFetchAgain =
    forceRefetch || !cache[labelsKey] || now >= cache[labelsKey].expiringTime;

  if (shouldFetchAgain) {
    const { enLabels, deLabels } = await getStaticContentLabels(queryClient, fetchPreCheckInLabel);

    logger.info({
      label: 'END_FETCH_GET_STATIC_CONTENT',
      message: {
        timeSpentFetchingLabels: `${new Date().getTime() - now}ms`,
      },
    });
    setLabelsInCache(enLabels, deLabels, now);
  }

  logger.trace({
    label: 'FETCH_LABELS_END',
  });

  return {
    _nextI18Next: {
      initialI18nStore: {
        [language]: {
          common: cache[`${labelsKey}`].value,
        },
      },
      initialLocale: language,
      userConfig: null,
    },
  };
}

export function displayStorageSubstitutionLabels(
  currentReservationRoomData: ReservationRoomType | undefined,
  operaRoomName: string,
  isSilentFeatureFlagEnabled?: boolean
): string | null {
  return `${
    (isSilentFeatureFlagEnabled &&
      currentReservationRoomData?.silentSubstitution &&
      currentReservationRoomData?.roomLabelCode) ||
    operaRoomName
  }`;
}

export { transformLabels };

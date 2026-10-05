/* eslint-disable @typescript-eslint/no-explicit-any */
import { QueryClient } from '@tanstack/react-query';

import {
  displayStorageSubstitutionLabels,
  getI18nLabels,
  getServerSideCustomLocale,
  transformLabels,
} from './labels';

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: '1800',
  },
}));

global.WB = {
  cache: {
    enLabels: {
      value: {},
      expiringTime: new Date().getTime(),
    },
    deLabels: {
      value: {},
      expiringTime: new Date().getTime(),
    },
  },
};

const fetchStaticContent = {
  labels: {
    main: '{"header": "Header"}',
    piBookings: '{"piBooking": "PIBooking"}',
    booking: '{"booking": "Booking"}',
  },
};

jest.mock('../hooks/use-request', () => ({
  ...jest.requireActual('../hooks/use-request'),
  graphQLRequest: () => fetchStaticContent,
}));

describe('i18n labels methods', () => {
  let mockStaticContent;

  describe('transformLabels Method', () => {
    beforeEach(() => {
      mockStaticContent = {
        labels: {
          main: '{"header": "Header"}',
          piBookings: '{"piBooking": "PIBooking"}',
          booking: '{"booking": "Booking"}',
        },
      };
    });

    it('should return an empty object if there is no static content', () => {
      const labels = transformLabels({} as any);
      expect(labels).toEqual({});
    });

    it('should return an object with all static labels', () => {
      const labels = transformLabels(mockStaticContent);
      const expectedOutput = {
        header: 'Header',
        piBooking: 'PIBooking',
        booking: 'Booking',
      };
      expect(labels).toEqual(expectedOutput);
    });

    it('should return an object without main labels if they are missing', () => {
      mockStaticContent.labels.main = null as any;
      const labels = transformLabels(mockStaticContent);
      const expectedOutput = {
        piBooking: 'PIBooking',
        booking: 'Booking',
      };
      expect(labels).toEqual(expectedOutput);
    });

    it('should return an object without piBookings labels if they are missing', () => {
      mockStaticContent.labels.piBookings = null as any;
      const labels = transformLabels(mockStaticContent);
      const expectedOutput = {
        header: 'Header',
        booking: 'Booking',
      };
      expect(labels).toEqual(expectedOutput);
    });

    it('should return an object without booking labels if they are missing', () => {
      mockStaticContent.labels.booking = null as any;
      const labels = transformLabels(mockStaticContent);
      const expectedOutput = {
        header: 'Header',
        piBooking: 'PIBooking',
      };
      expect(labels).toEqual(expectedOutput);
    });
  });

  describe('getI18nLabels Method', () => {
    const mockQueryClient = new QueryClient();

    it.each(['__proto__', 'constructor', 'prototype', 'toString', 'fr', '', 'EN'])(
      'rejects unsupported language %s before accessing the cache or fetching',
      async (language) => {
        const queryClient = { fetchQuery: jest.fn() } as unknown as QueryClient;
        const cacheBefore = { ...global.WB.cache };

        await expect(getI18nLabels({ language, queryClient })).rejects.toThrow(
          'Unsupported labels language; expected en or de'
        );

        expect(queryClient.fetchQuery).not.toHaveBeenCalled();
        expect(global.WB.cache).toEqual(cacheBefore);
      }
    );

    const refetchedStaticContent = {
      labels: {
        main: '{"restaurant": "Restaurant"}',
        piBookings: '{"roomType": "Room Type"}',
        booking: '{"bookNow": "Book Now"}',
      },
    };

    jest.mock(
      '@tanstack/react-query',
      () =>
        ({
          // eslint-disable-next-line @typescript-eslint/ban-ts-comment
          // @ts-ignore
          fetchQuery: async (queryKey: any, queryFn: any) => {
            const key = queryKey[0];
            queryFn();
            if (key === 'GetStaticContent') {
              return Promise.resolve(refetchedStaticContent);
            }
          },
        }) as any
    );

    it('should return the english i18n labels config for next', async () => {
      global.WB.cache = {
        ...global.WB.cache,
        enLabels: {
          value: {},
          expiringTime: new Date().getTime() - 10,
        },
      };
      const i18nLabels = await getI18nLabels({
        language: 'en',
        queryClient: mockQueryClient,
      });

      const expectedOutput = {
        _nextI18Next: {
          initialI18nStore: {
            en: {
              common: {
                booking: 'Booking',
                header: 'Header',
                piBooking: 'PIBooking',
              },
            },
          },
          initialLocale: 'en',
          userConfig: null,
        },
      };
      expect(i18nLabels).toEqual(expectedOutput);
    });

    it('should return the german i18n labels config for next', async () => {
      global.WB.cache = {
        ...global.WB.cache,
        deLabels: {
          value: {},
          expiringTime: new Date().getTime() - 10,
        },
      };
      const i18nLabels = await getI18nLabels({
        language: 'de',
        queryClient: mockQueryClient,
      });

      const expectedOutput = {
        _nextI18Next: {
          initialI18nStore: {
            de: {
              common: {
                booking: 'Booking',
                header: 'Header',
                piBooking: 'PIBooking',
              },
            },
          },
          initialLocale: 'de',
          userConfig: null,
        },
      };
      expect(i18nLabels).toEqual(expectedOutput);
    });

    it('should return the cached content', async () => {
      global.WB.cache = {
        ...global.WB.cache,
        enLabels: {
          value: {
            booking: 'Booking from cache',
            footer: 'Footer from cache',
            header: 'Header from cache',
            piBooking: 'PIBooking from cache',
          },
          expiringTime: new Date().getTime() + 10,
        },
      };
      const i18nLabels = await getI18nLabels({
        language: 'en',
        queryClient: mockQueryClient,
      });

      const expectedOutput = {
        _nextI18Next: {
          initialI18nStore: {
            en: {
              common: {
                booking: 'Booking from cache',
                footer: 'Footer from cache',
                header: 'Header from cache',
                piBooking: 'PIBooking from cache',
              },
            },
          },
          initialLocale: 'en',
          userConfig: null,
        },
      };
      expect(i18nLabels).toEqual(expectedOutput);
    });

    it('should use distinct query keys when fetching static content labels', async () => {
      const fetchQuery = jest.fn(({ queryFn }) => queryFn());
      const queryClient = { fetchQuery } as unknown as QueryClient;
      global.WB.cache = {
        ...global.WB.cache,
        enLabels: {
          value: {},
          expiringTime: new Date().getTime() - 10,
        },
      };

      await getI18nLabels({
        language: 'en',
        queryClient,
      });

      expect(fetchQuery).toHaveBeenNthCalledWith(
        1,
        expect.objectContaining({ queryKey: ['GetStaticContentLabels', 'en', 'gb'] })
      );
      expect(fetchQuery).toHaveBeenNthCalledWith(
        2,
        expect.objectContaining({ queryKey: ['GetStaticContentLabels', 'de', 'de'] })
      );
    });
  });

  describe('getServerSideCustomLocale Method', () => {
    it('should return the correct value', function () {
      expect(getServerSideCustomLocale('gb')).toEqual({ language: 'en', country: 'gb' });
      expect(getServerSideCustomLocale('de')).toEqual({ language: 'de', country: 'de' });
    });
  });
});

describe('displayStorageSubstitutionLabels Method', () => {
  const firstRoomInitialValue = 'Bigger room';
  const secondRoomInitialValue = 'Accessible room';
  const storageRoomsMock = {
    value: [
      {
        roomLabelCode: 'Double room',
        silentSubstitution: true,
      },
      {
        roomLabelCode: 'Single room',
        silentSubstitution: true,
      },
    ],
    expire: 999,
  };
  it('should return FIRST ROOM label with FEATURE FLAG TRUE and SILENT SUBSTITUTION TRUE', () => {
    expect(
      displayStorageSubstitutionLabels(storageRoomsMock?.value[0], firstRoomInitialValue, true)
    ).toEqual('Double room');
  });

  it('should return SECOND ROOM label with FEATURE FLAG TRUE and SILENT SUBSTITUTION TRUE', () => {
    expect(
      displayStorageSubstitutionLabels(storageRoomsMock?.value[1], secondRoomInitialValue, true)
    ).toEqual('Single room');
  });

  it('should return FIRST ROOM label with FEATURE FLAG FALSE and SILENT SUBSTITUTION TRUE', () => {
    expect(
      displayStorageSubstitutionLabels(storageRoomsMock?.value[0], firstRoomInitialValue, false)
    ).toEqual('Bigger room');
  });

  it('should return SECOND ROOM label with FEATURE FLAG FALSE and SILENT SUBSTITUTION TRUE', () => {
    expect(
      displayStorageSubstitutionLabels(storageRoomsMock?.value[1], secondRoomInitialValue, false)
    ).toEqual('Accessible room');
  });

  it('should return FIRST ROOM label with FEATURE FLAG TRUE and SILENT SUBSTITUTION FALSE', () => {
    storageRoomsMock.value[0].silentSubstitution = false;
    expect(
      displayStorageSubstitutionLabels(storageRoomsMock?.value[0], firstRoomInitialValue, true)
    ).toEqual('Bigger room');
  });

  it('should return SECOND ROOM label with FEATURE FLAG TRUE and SILENT SUBSTITUTION FALSE', () => {
    storageRoomsMock.value[1].silentSubstitution = false;
    expect(
      displayStorageSubstitutionLabels(storageRoomsMock?.value[1], secondRoomInitialValue, true)
    ).toEqual('Accessible room');
  });
});

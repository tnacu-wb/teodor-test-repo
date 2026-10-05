import * as ReactQuery from '@tanstack/react-query';

import { getDefaultSessionTracing, getLogger, instrumentQueryClient } from './logger';

const queryClient = new ReactQuery.QueryClient();

jest.spyOn(global.console, 'warn').mockImplementation(() => ({}));

const params = {
  queryClient: queryClient,
};

describe('getLogger', () => {
  it('getLogger', async () => {
    const result = getLogger();
    expect(result.levels.labels[10]).toBe('trace');
  });

  it('getDefaultSessionTracing', async () => {
    const result = getDefaultSessionTracing({ get: jest.fn(), set: jest.fn() });
    expect(result['WB-SESSION-ID']).toBe(undefined);
  });

  it('getDefaultSessionTracing with cookie name', async () => {
    const result = getDefaultSessionTracing({
      get: () => {
        return true;
      },
      set: jest.fn(),
    });
    expect(result['WB-SESSION-ID']).toBe(true);
  });

  it('instrumentQueryClient fetchQuery', async () => {
    const result = instrumentQueryClient(params.queryClient);
    expect(result).toHaveProperty('fetchQuery');
  });

  it('instrumentQueryClient prefetchQuery', async () => {
    const result = instrumentQueryClient(params.queryClient).fetchQuery(
      ['Intentional Logger Message fetch'],
      jest.fn().mockResolvedValue({})
    );
    await expect(result).resolves.toEqual({});
  });

  it('instrumentQueryClient', async () => {
    const result = instrumentQueryClient(params.queryClient).prefetchQuery(
      ['Intentional Logger Message prefetch'],
      jest.fn().mockResolvedValue({})
    );
    await expect(result).resolves.toEqual(undefined); // prefetchQuery returns undefined
  });
});

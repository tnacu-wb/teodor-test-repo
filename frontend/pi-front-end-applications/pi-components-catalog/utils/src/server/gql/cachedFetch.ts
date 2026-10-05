import { cache } from 'react';

export const cachedFetch = cache(
  async (endpoint: string, headers: string, body: string, cacheOptions: string) => {
    const headersObj = JSON.parse(headers);
    const cacheOptionsObj = JSON.parse(cacheOptions);

    const res = await fetch(endpoint, {
      method: 'POST',
      headers: headersObj,
      body,
      ...cacheOptionsObj,
    });

    if (!res.ok) {
      return null;
    }

    return await res.json();
  }
);

import getConfig from 'next/config';

const { publicRuntimeConfig = {} } = getConfig() ?? {};

export async function getSuggestions(value: string) {
  const response = await fetch(
    `${publicRuntimeConfig.NEXT_PUBLIC_SNOWDROP_BASE_URL}v1/autocomplete?input=/${value}&gplaces[components]=country:uk|country:de`
  );
  return response.json();
}

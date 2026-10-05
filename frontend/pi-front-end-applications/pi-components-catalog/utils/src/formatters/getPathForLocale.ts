import { LOCALES } from '@whitbread-eos/api';

export const getPathForLocale = (locale: LOCALES | string | undefined, path: string): string =>
  `/${locale}/${path}`;

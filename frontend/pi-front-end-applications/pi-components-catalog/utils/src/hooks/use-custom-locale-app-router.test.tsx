import { usePathname } from 'next/navigation';

import { getLocaleByPathname, getCountryLanguageByLocale } from '../server';
import useCustomLocaleAppRouter, {
  computeCountryLanguageFromPathname,
} from './use-custom-locale-app-router';

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(),
}));

jest.mock('../server', () => ({
  getLocaleByPathname: jest.fn(),
  getCountryLanguageByLocale: jest.fn(),
}));

describe('computeCountryLanguageFromPathname', () => {
  it('should return the correct country and language based on the pathname', () => {
    (getLocaleByPathname as jest.Mock).mockReturnValue('en-gb');
    (getCountryLanguageByLocale as jest.Mock).mockReturnValue({ country: 'gb', language: 'en' });

    const result = computeCountryLanguageFromPathname('/en-gb/some-path');

    expect(getLocaleByPathname).toHaveBeenCalledWith('/en-gb/some-path');
    expect(getCountryLanguageByLocale).toHaveBeenCalledWith('en-gb');
    expect(result).toEqual({ country: 'gb', language: 'en' });
  });

  it('should return default country and language when pathname is null', () => {
    const result = computeCountryLanguageFromPathname(null);

    expect(result).toEqual({ country: 'gb', language: 'en' });
  });
});

describe('useCustomLocaleAppRouter', () => {
  it('should return the correct country and language based on the pathname', () => {
    (usePathname as jest.Mock).mockReturnValue('/en-gb');
    (getLocaleByPathname as jest.Mock).mockReturnValue('en-gb');
    (getCountryLanguageByLocale as jest.Mock).mockReturnValue({ country: 'gb', language: 'en' });

    const result = useCustomLocaleAppRouter();

    expect(usePathname).toHaveBeenCalled();
    expect(getLocaleByPathname).toHaveBeenCalledWith('/en-gb');
    expect(getCountryLanguageByLocale).toHaveBeenCalledWith('en-gb');
    expect(result).toEqual({ country: 'gb', language: 'en' });
  });
});

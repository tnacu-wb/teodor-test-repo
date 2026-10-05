import { getProxyOptions, setProxyOptionsCookies } from './proxyOptions';

describe('proxyOptions', () => {
  it('call with default values', () => {
    const rez = getProxyOptions({}, {});
    expect(rez).toStrictEqual({
      useProxyAPI: true,
      cookie: undefined,
      host: 'https://',
      accessToken: undefined,
    });
  });

  it('call width valid values', () => {
    const rez = getProxyOptions(
      { headers: { host: 'localhosts', cookie: 'cookie' } },
      { tokenSet: { accessToken: '' } }
    );
    expect(rez).toStrictEqual({
      useProxyAPI: true,
      cookie: undefined,
      host: 'https://',
      accessToken: '',
    });
  });

  it('call width valid values host', () => {
    const rez = getProxyOptions(
      { headers: { host: 'localhost', cookie: 'cookie' } },
      { tokenSet: { accessToken: '' } }
    );
    expect(rez).toStrictEqual({
      useProxyAPI: true,
      cookie: undefined,
      host: 'https://',
      accessToken: '',
    });
  });

  it('call width  undefined host', () => {
    const rez = getProxyOptions(
      { headers: { host: undefined, cookie: undefined } },
      { tokenSet: { accessToken: '' } }
    );
    expect(rez).toStrictEqual({
      useProxyAPI: true,
      cookie: undefined,
      host: 'https://',
      accessToken: '',
    });
  });

  it('call width  undefined headers', () => {
    const rez = getProxyOptions({ headers: undefined }, { tokenSet: { accessToken: '' } });
    expect(rez).toStrictEqual({
      useProxyAPI: true,
      cookie: undefined,
      host: 'https://',
      accessToken: '',
    });
  });
});

describe('setProxyOptionsCookies', () => {
  it('call with default values', () => {
    const rez = setProxyOptionsCookies({}, {});
    expect(rez).toStrictEqual(undefined);
  });

  it('call with props undefined', () => {
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    const rez = setProxyOptionsCookies({});
    expect(rez).toStrictEqual(undefined);
  });
});

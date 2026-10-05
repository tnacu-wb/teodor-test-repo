import withPiTokenRedirect from './withPiTokenRedirect';

const mockGetServerSideCustomLocale = jest.fn();
const mockGetChannelByToken = jest.fn();
const mockGssp = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  getServerSideCustomLocale: (...args: any[]) => mockGetServerSideCustomLocale(...args),
  ID_TOKEN_COOKIE: 'id_token_cookie',
}));
jest.mock('@whitbread-eos/utils/server', () => ({
  getChannelByToken: (...args: any[]) => mockGetChannelByToken(...args),
}));
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: jest.fn(),
  }));
});

describe('withPiTokenRedirect', () => {
  let context: any;
  let Cookies: any;

  beforeEach(() => {
    jest.clearAllMocks();
    context = {
      req: {},
      res: {},
      locale: 'gb',
    };
    // eslint-disable-next-line @typescript-eslint/no-require-imports
    Cookies = require('cookies');
  });

  it('should redirect to /country/language/home.html if PI token is present', async () => {
    process.env.NEXT_PUBLIC_PI_BASE_URL = 'https://pi.example.com';
    const cookiesInstance = { get: jest.fn().mockReturnValue('token123') };
    (Cookies as jest.Mock).mockImplementation(() => cookiesInstance);
    mockGetServerSideCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockGetChannelByToken.mockReturnValue('PI');

    const wrapped = withPiTokenRedirect(mockGssp);
    const result = await wrapped(context);

    expect(result).toEqual({
      redirect: {
        destination: 'https://pi.example.com/gb/en/home.html',
        permanent: false,
      },
    });
    expect(mockGssp).not.toHaveBeenCalled();
  });

  it('should call gssp if no id token cookie is present', async () => {
    const cookiesInstance = { get: jest.fn().mockReturnValue(undefined) };
    (Cookies as jest.Mock).mockImplementation(() => cookiesInstance);
    mockGetServerSideCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockGssp.mockResolvedValue({ props: { foo: 'bar' } });

    const wrapped = withPiTokenRedirect(mockGssp);
    const result = await wrapped(context);

    expect(mockGssp).toHaveBeenCalledWith(context);
    expect(result).toEqual({ props: { foo: 'bar' } });
  });

  it('should call gssp if channel is not PI', async () => {
    const cookiesInstance = { get: jest.fn().mockReturnValue('token123') };
    (Cookies as jest.Mock).mockImplementation(() => cookiesInstance);
    mockGetServerSideCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockGetChannelByToken.mockReturnValue('OTHER');
    mockGssp.mockResolvedValue({ props: { foo: 'baz' } });

    const wrapped = withPiTokenRedirect(mockGssp);
    const result = await wrapped(context);

    expect(mockGssp).toHaveBeenCalledWith(context);
    expect(result).toEqual({ props: { foo: 'baz' } });
  });

  it('should use default locale if context.locale is undefined', async () => {
    context.locale = undefined;
    const cookiesInstance = { get: jest.fn().mockReturnValue('token123') };
    (Cookies as jest.Mock).mockImplementation(() => cookiesInstance);
    mockGetServerSideCustomLocale.mockReturnValue({ country: 'gb', language: 'en' });
    mockGetChannelByToken.mockReturnValue('PI');

    const wrapped = withPiTokenRedirect(mockGssp);
    await wrapped(context);

    expect(mockGetServerSideCustomLocale).toHaveBeenCalledWith('gb');
  });
});

import { IncomingMessage } from 'http';

// Global reference to access the mock in tests
let mockGetSession: jest.Mock;

// Mock auth0 from lib/auth0
jest.mock('./auth0', () => {
  const getSession = jest.fn();
  mockGetSession = getSession;
  return {
    auth0: {
      getSession,
    },
  };
});

// Import functions after mock is set up
const modulePromise = import('./getAuth0Token');

describe('getAuth0AccessToken', () => {
  let getAuth0AccessToken: (req: IncomingMessage) => Promise<string | null>;
  let mockReq: Partial<IncomingMessage>;

  beforeAll(async () => {
    const importedModule = await modulePromise;
    getAuth0AccessToken = importedModule.getAuth0AccessToken;
  });

  beforeEach(() => {
    jest.clearAllMocks();
    mockReq = {} as IncomingMessage;
  });

  it('should return access token when session exists', async () => {
    const mockAccessToken = 'samplevalue';
    const mockSession = {
      tokenSet: {
        accessToken: mockAccessToken,
      },
    };

    mockGetSession.mockResolvedValue(mockSession);

    const result = await getAuth0AccessToken(mockReq as IncomingMessage);

    expect(mockGetSession).toHaveBeenCalledWith(mockReq);
    expect(result).toBe(mockAccessToken);
  });

  it('should return null when session does not exist', async () => {
    mockGetSession.mockResolvedValue(null);

    const result = await getAuth0AccessToken(mockReq as IncomingMessage);

    expect(mockGetSession).toHaveBeenCalledWith(mockReq);
    expect(result).toBeNull();
  });

  it('should return null when tokenSet is missing', async () => {
    const mockSession = {};

    mockGetSession.mockResolvedValue(mockSession);

    const result = await getAuth0AccessToken(mockReq as IncomingMessage);

    expect(result).toBeNull();
  });

  it('should return null when tokenSet.accessToken is undefined', async () => {
    const mockSession = {
      tokenSet: {},
    };

    mockGetSession.mockResolvedValue(mockSession);

    const result = await getAuth0AccessToken(mockReq as IncomingMessage);

    expect(result).toBeNull();
  });

  it('should return null on error', async () => {
    mockGetSession.mockRejectedValue(new Error('Database error'));

    const result = await getAuth0AccessToken(mockReq as IncomingMessage);

    expect(result).toBeNull();
  });
});

describe('getAuth0UserEmail', () => {
  let getAuth0UserEmail: (req: IncomingMessage) => Promise<string | null>;
  let mockReq: Partial<IncomingMessage>;

  beforeAll(async () => {
    const importedModule = await modulePromise;
    getAuth0UserEmail = importedModule.getAuth0UserEmail;
  });

  beforeEach(() => {
    jest.clearAllMocks();
    mockReq = {} as IncomingMessage;
  });

  it('should return user email when session exists', async () => {
    const mockEmail = 'test@example.com';
    const mockSession = {
      user: {
        email: mockEmail,
      },
    };

    mockGetSession.mockResolvedValue(mockSession);

    const result = await getAuth0UserEmail(mockReq as IncomingMessage);

    expect(mockGetSession).toHaveBeenCalledWith(mockReq);
    expect(result).toBe(mockEmail);
  });

  it('should return null when session does not exist', async () => {
    mockGetSession.mockResolvedValue(null);

    const result = await getAuth0UserEmail(mockReq as IncomingMessage);

    expect(mockGetSession).toHaveBeenCalledWith(mockReq);
    expect(result).toBeNull();
  });

  it('should return null when user is missing', async () => {
    const mockSession = {};

    mockGetSession.mockResolvedValue(mockSession);

    const result = await getAuth0UserEmail(mockReq as IncomingMessage);

    expect(result).toBeNull();
  });

  it('should return null when user.email is undefined', async () => {
    const mockSession = {
      user: {},
    };

    mockGetSession.mockResolvedValue(mockSession);

    const result = await getAuth0UserEmail(mockReq as IncomingMessage);

    expect(result).toBeNull();
  });

  it('should return null on error', async () => {
    mockGetSession.mockRejectedValue(new Error('Network error'));

    const result = await getAuth0UserEmail(mockReq as IncomingMessage);

    expect(result).toBeNull();
  });
});

describe('getAuth0TokenAndEmail', () => {
  let getAuth0TokenAndEmail: (
    req: IncomingMessage
  ) => Promise<{ accessToken: string | null; email: string | null }>;
  let mockReq: Partial<IncomingMessage>;

  beforeAll(async () => {
    const importedModule = await modulePromise;
    getAuth0TokenAndEmail = importedModule.getAuth0TokenAndEmail;
  });

  beforeEach(() => {
    jest.clearAllMocks();
    mockReq = {} as IncomingMessage;
  });

  it('should return both accessToken and email when session exists', async () => {
    mockGetSession.mockResolvedValue({
      tokenSet: { accessToken: 'mock-token' },
      user: { email: 'test@example.com' },
    });

    const result = await getAuth0TokenAndEmail(mockReq as IncomingMessage);

    expect(mockGetSession).toHaveBeenCalledWith(mockReq);
    expect(result).toEqual({ accessToken: 'mock-token', email: 'test@example.com' });
  });

  it('should return nulls when session is null', async () => {
    mockGetSession.mockResolvedValue(null);

    const result = await getAuth0TokenAndEmail(mockReq as IncomingMessage);

    expect(result).toEqual({ accessToken: null, email: null });
  });

  it('should return null accessToken when tokenSet is missing', async () => {
    mockGetSession.mockResolvedValue({ user: { email: 'test@example.com' } });

    const result = await getAuth0TokenAndEmail(mockReq as IncomingMessage);

    expect(result).toEqual({ accessToken: null, email: 'test@example.com' });
  });

  it('should return null email when user is missing', async () => {
    mockGetSession.mockResolvedValue({ tokenSet: { accessToken: 'mock-token' } });

    const result = await getAuth0TokenAndEmail(mockReq as IncomingMessage);

    expect(result).toEqual({ accessToken: 'mock-token', email: null });
  });

  it('should return nulls on error', async () => {
    mockGetSession.mockRejectedValue(new Error('Session error'));

    const result = await getAuth0TokenAndEmail(mockReq as IncomingMessage);

    expect(result).toEqual({ accessToken: null, email: null });
  });
});

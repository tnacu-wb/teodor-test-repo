import { Auth0Client } from '@auth0/nextjs-auth0/server';
import { CCUI_ROLES } from '@whitbread-eos/api';

interface SessionWithToken {
  user: Record<string, unknown>;
  idToken?: Record<string, unknown> | null;
}

const mockAuth0Client = jest.fn();

jest.mock('@auth0/nextjs-auth0/server', () => ({
  Auth0Client: jest.fn(),
}));

describe('auth0', () => {
  const originalEnv = process.env;
  let mockBeforeSessionSaved: (session: SessionWithToken) => Promise<SessionWithToken>;

  beforeEach(() => {
    jest.clearAllMocks();
    process.env = {
      ...originalEnv,
      AUTH0_DOMAIN: 'test.auth0.com',
      AUTH0_CLIENT_ID: 'test-client-id',
      AUTH0_CLIENT_SECRET: 'test-client-secret',
      AUTH0_SECRET: 'test-secret',
      AUTH0_BASE_URL: 'https://test.example.com',
      AUTH0_SESSION_ROLLING: 'true',
    };

    (Auth0Client as jest.Mock).mockImplementation((config) => {
      mockBeforeSessionSaved = config.beforeSessionSaved;
      return mockAuth0Client;
    });
  });

  afterEach(() => {
    process.env = originalEnv;
  });

  describe('Auth0Client initialization', () => {
    it('should initialize Auth0Client with correct configuration', () => {
      jest.isolateModules(() => {
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        require('./auth0');
      });

      expect(Auth0Client).toHaveBeenCalledWith({
        domain: 'test.auth0.com',
        clientId: 'test-client-id',
        clientSecret: 'test-client-secret',
        secret: 'test-secret',
        appBaseUrl: 'https://test.example.com',
        session: {
          rolling: true,
        },
        beforeSessionSaved: expect.any(Function),
      });
    });

    it('should set session rolling to true when AUTH0_SESSION_ROLLING is true', () => {
      process.env.AUTH0_SESSION_ROLLING = 'true';

      jest.isolateModules(() => {
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        require('./auth0');
      });

      expect(Auth0Client).toHaveBeenCalledWith(
        expect.objectContaining({
          session: {
            rolling: true,
          },
        })
      );
    });

    it('should set session rolling to false when AUTH0_SESSION_ROLLING is false', () => {
      process.env.AUTH0_SESSION_ROLLING = 'false';

      jest.isolateModules(() => {
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        require('./auth0');
      });

      expect(Auth0Client).toHaveBeenCalledWith(
        expect.objectContaining({
          session: {
            rolling: false,
          },
        })
      );
    });

    it('should set session rolling to false when AUTH0_SESSION_ROLLING is undefined', () => {
      delete process.env.AUTH0_SESSION_ROLLING;

      jest.isolateModules(() => {
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        require('./auth0');
      });

      expect(Auth0Client).toHaveBeenCalledWith(
        expect.objectContaining({
          session: {
            rolling: false,
          },
        })
      );
    });
  });

  describe('beforeSessionSaved', () => {
    beforeEach(() => {
      jest.isolateModules(() => {
        // eslint-disable-next-line @typescript-eslint/no-require-imports
        require('./auth0');
      });
    });

    it('should preserve CCUI_ROLES from idToken to session user', async () => {
      const mockRoles = ['CC_Role03', 'CC_Role05'];
      const session = {
        user: {},
        idToken: {
          [CCUI_ROLES]: mockRoles,
        },
      };

      const result = await mockBeforeSessionSaved(session);

      expect(result.user[CCUI_ROLES]).toEqual(mockRoles);
    });

    it('should return session when CCUI_ROLES is valid non-empty array', async () => {
      const mockRoles = ['CC_Role03'];
      const session = {
        user: {},
        idToken: {
          [CCUI_ROLES]: mockRoles,
        },
      };

      const result = await mockBeforeSessionSaved(session);

      expect(result).toEqual({
        user: {
          [CCUI_ROLES]: mockRoles,
        },
        idToken: {
          [CCUI_ROLES]: mockRoles,
        },
      });
    });

    it('should throw error when CCUI_ROLES is missing from user', async () => {
      const session = {
        user: {},
        idToken: {},
      };

      await expect(mockBeforeSessionSaved(session)).rejects.toThrow(
        'User lacks required CCUI_ROLES'
      );
    });

    it('should throw error when CCUI_ROLES is empty array', async () => {
      const session = {
        user: {},
        idToken: {
          [CCUI_ROLES]: [],
        },
      };

      await expect(mockBeforeSessionSaved(session)).rejects.toThrow(
        'User lacks required CCUI_ROLES'
      );
    });

    it('should throw error when CCUI_ROLES is null', async () => {
      const session = {
        user: {
          [CCUI_ROLES]: null,
        },
        idToken: {},
      };

      await expect(mockBeforeSessionSaved(session)).rejects.toThrow(
        'User lacks required CCUI_ROLES'
      );
    });

    it('should throw error when CCUI_ROLES is undefined', async () => {
      const session = {
        user: {
          [CCUI_ROLES]: undefined,
        },
        idToken: {},
      };

      await expect(mockBeforeSessionSaved(session)).rejects.toThrow(
        'User lacks required CCUI_ROLES'
      );
    });

    it('should handle missing idToken without error', async () => {
      const mockRoles = ['CC_Role03'];
      const session = {
        user: {
          [CCUI_ROLES]: mockRoles,
        },
      };

      const result = await mockBeforeSessionSaved(session);

      expect(result.user[CCUI_ROLES]).toEqual(mockRoles);
    });

    it('should handle null idToken without error', async () => {
      const mockRoles = ['CC_Role03'];
      const session = {
        user: {
          [CCUI_ROLES]: mockRoles,
        },
        idToken: null,
      };

      const result = await mockBeforeSessionSaved(session);

      expect(result.user[CCUI_ROLES]).toEqual(mockRoles);
    });

    it('should preserve multiple roles from idToken', async () => {
      const mockRoles = ['CC_Role03', 'CC_Role05', 'CC_Role12'];
      const session = {
        user: {},
        idToken: {
          [CCUI_ROLES]: mockRoles,
        },
      };

      const result = await mockBeforeSessionSaved(session);

      expect(result.user[CCUI_ROLES]).toEqual(mockRoles);
      expect(result.user[CCUI_ROLES]).toHaveLength(3);
    });
  });
});

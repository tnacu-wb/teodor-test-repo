import handler from './fpid';

function createMockRes() {
  const headers = {};
  return {
    statusCode: null,
    json: jest.fn(),
    setHeader: (key, value) => {
      headers[key] = value;
    },
    getHeader: (key) => headers[key],
    status: function (code) {
      this.statusCode = code;
      return this;
    },
  };
}

describe('fpid API handler', () => {
  afterEach(() => {
    delete process.env.NEXT_ENV;
  });

  it('should set allowedOrigin to https://www.premierinn.com for hulk/premier-inn', () => {
    process.env.NEXT_ENV = 'hulk';
    const req = { method: 'GET', cookies: {}, query: { app: 'premier-inn' }, headers: {} };
    const res = createMockRes();
    handler(req, res);
    expect(res.getHeader('Access-Control-Allow-Origin')).toBe('https://www.premierinn.com');
  });

  it('should set allowedOrigin to https://business.premierinn.com for hulk/business-booker', () => {
    process.env.NEXT_ENV = 'hulk';
    const req = { method: 'GET', cookies: {}, query: { app: 'business-booker' }, headers: {} };
    const res = createMockRes();
    handler(req, res);
    expect(res.getHeader('Access-Control-Allow-Origin')).toBe('https://business.premierinn.com');
  });

  it('should set allowedOrigin to https://www.testenv.premierinn.digital for testenv/premier-inn', () => {
    process.env.NEXT_ENV = 'testenv';
    const req = { method: 'GET', cookies: {}, query: {}, headers: {} };
    const res = createMockRes();
    handler(req, res);
    expect(res.getHeader('Access-Control-Allow-Origin')).toBe(
      'https://www.testenv.premierinn.digital'
    );
  });

  it('should set allowedOrigin to https://business.testenv.premierinn.digital for testenv/business-booker', () => {
    process.env.NEXT_ENV = 'testenv';
    const req = { method: 'GET', cookies: {}, query: { app: 'business-booker' }, headers: {} };
    const res = createMockRes();
    handler(req, res);
    expect(res.getHeader('Access-Control-Allow-Origin')).toBe(
      'https://business.testenv.premierinn.digital'
    );
  });

  it('should return a valid UUIDv4 and set cookie for GET', () => {
    process.env.NEXT_ENV = 'hulk';
    const req = { method: 'GET', cookies: {}, query: { app: 'premier-inn' }, headers: {} };
    const res = createMockRes();
    handler(req, res);
    const jsonCall = res.json.mock.calls[0][0];
    expect(res.statusCode).toBe(200);
    expect(jsonCall.fpid).toMatch(
      /[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}/i
    );
    expect(res.getHeader('Set-Cookie')).toMatch(
      /fpid=.*; Path=\/; Domain=\.(premierinn\.digital|premierinn\.com); Secure; SameSite=Lax; Expires=/
    );
  });

  it('should reuse fpid from cookies if present', () => {
    process.env.NEXT_ENV = 'hulk';
    const req = {
      method: 'GET',
      cookies: { fpid: 'test-uuid' },
      query: { app: 'premier-inn' },
      headers: {},
    };
    const res = createMockRes();
    handler(req, res);
    const jsonCall = res.json.mock.calls[0][0];
    expect(jsonCall.fpid).toBe('test-uuid');
  });

  it('should return 405 for non-GET requests', () => {
    process.env.NEXT_ENV = 'hulk';
    const req = { method: 'POST', cookies: {}, query: { app: 'premier-inn' }, headers: {} };
    const res = createMockRes();
    handler(req, res);
    expect(res.statusCode).toBe(405);
    expect(res.json).toHaveBeenCalledWith({ error: 'Method not allowed' });
  });
});

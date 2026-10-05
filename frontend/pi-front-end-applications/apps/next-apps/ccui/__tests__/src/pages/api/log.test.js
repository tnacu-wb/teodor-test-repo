import log from '~pages/api/log';

const mockResponse = () => {
  const res = {};
  res.status = jest.fn().mockReturnValue({ send: jest.fn() });
  return res;
};

describe('log function', () => {
  it('should return undefined if method is GET', () => {
    const res = mockResponse();
    const rez = log({ method: 'GET' }, res);
    expect(rez).toEqual(undefined);
  });
});

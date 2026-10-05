import liveness from '~pages/api/liveness';

const mockResponse = () => {
  const res = {};
  res.status = jest.fn().mockReturnValue(res);
  res.json = jest.fn().mockReturnValue(res);
  return res;
};

describe('liveness endpoint', () => {
  it('should match the snapshot', () => {
    const req = mockResponse();
    const res = mockResponse();
    liveness(req, res);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ status: 'ok' });
  });
});
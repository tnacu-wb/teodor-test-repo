import readiness from '~pages/api/readiness';

const mockResponse = () => {
  const res = {};
  res.status = jest.fn().mockReturnValue(res);
  res.json = jest.fn().mockReturnValue(res);
  return res;
};

describe('readiness endpoint', () => {
  it('should match the snapshot', () => {
    const req = mockResponse();
    const res = mockResponse();
    readiness(req, res);
    expect(res.status).toHaveBeenCalledWith(200);
    expect(res.json).toHaveBeenCalledWith({ status: 'ok' });
  });
});
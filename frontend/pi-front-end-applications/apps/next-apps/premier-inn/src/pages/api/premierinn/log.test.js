import { logger, decodeFromBase64, getCookie } from '@whitbread-eos/utils';
import { constants } from 'http2';

import log from './log';

jest.mock('@whitbread-eos/utils', () => ({
  logger: {
    info: jest.fn(),
  },
  decodeFromBase64: jest.fn(),
  getCookie: jest.fn(),
}));

describe('log function', () => {
  let req;
  let res;

  beforeEach(() => {
    req = {
      method: 'POST',
      body: '',
    };
    res = {
      status: jest.fn().mockReturnThis(),
      send: jest.fn(),
    };

    getCookie.mockReturnValue('mock-session-id');
    decodeFromBase64.mockReturnValue('{"log": "test log"}');
  });

  it('should respond with 405 if the method is not POST', () => {
    req.method = 'GET';

    log(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_METHOD_NOT_ALLOWED);
    expect(res.send).toHaveBeenCalled();
  });

  it('should respond with 401 if session ID cookie is missing', () => {
    getCookie.mockReturnValue(null);

    log(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_UNAUTHORIZED);
    expect(res.send).toHaveBeenCalled();
  });

  it('should respond with 400 if body is malformed', () => {
    decodeFromBase64.mockImplementation(() => {
      throw new Error('Malformed JSON');
    });

    log(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_BAD_REQUEST);
    expect(res.send).toHaveBeenCalled();
  });

  it('should respond with 400 if log property is missing in body', () => {
    decodeFromBase64.mockReturnValue('{}');

    log(req, res);

    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_BAD_REQUEST);
    expect(res.send).toHaveBeenCalled();
  });

  it('should log and respond with 201 if request is valid', () => {
    log(req, res);

    expect(logger.info).toHaveBeenCalledWith('test log');
    expect(res.status).toHaveBeenCalledWith(constants.HTTP_STATUS_CREATED);
    expect(res.send).toHaveBeenCalled();
  });
});

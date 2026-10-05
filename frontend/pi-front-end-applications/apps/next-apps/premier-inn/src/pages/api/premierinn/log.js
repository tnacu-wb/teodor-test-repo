import { logger, decodeFromBase64, getCookie } from '@whitbread-eos/utils';
import { constants } from 'http2';

export default function log(req, res) {
  if (req.method !== 'POST') {
    res.status(constants.HTTP_STATUS_METHOD_NOT_ALLOWED).send();
    return;
  }

  const sessionId = getCookie('WB-SESSION-ID', { req, res });
  if (!sessionId) {
    res.status(constants.HTTP_STATUS_UNAUTHORIZED).send();
    return;
  }

  let body;
  try {
    body = JSON.parse(decodeFromBase64(req.body));
  } catch {
    res.status(constants.HTTP_STATUS_BAD_REQUEST).send();
    return;
  }

  if (!body?.log) {
    res.status(constants.HTTP_STATUS_BAD_REQUEST).send();
    return;
  }

  logger.info(body.log);
  res.status(constants.HTTP_STATUS_CREATED).send();
}

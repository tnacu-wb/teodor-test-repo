import getConfig from 'next/config';
import { ErrorInfo } from 'react';

import { encodeToBase64 } from '../helpers/base64';

type ClientLog = {
  label: string;
  err: string;
  errorInfo: ErrorInfo;
  sessionId: string;
};

const error = (log: ClientLog) => {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const sendLogsToOpenSearch = publicRuntimeConfig?.NEXT_OPENSEARCH_LOGS === 'true';

  try {
    logOnAppDynamics(log);
    sendLogsToOpenSearch && logOnOpenSearch(log);
  } catch (err) {
    console.log(err);
  }
};

/**
 * AppDynamics reads browser console, so it's enough to log the error there
 */
const logOnAppDynamics = (log: ClientLog) => console.error(log);

/**
 * Once Next Upgrade it's completed we can use Server Action instead of API endpoint
 */
const logOnOpenSearch = (log: ClientLog) => {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const endpoint = publicRuntimeConfig?.NEXT_PUBLIC_LOG_ENDPOINT ?? '';

  if (endpoint) {
    navigator?.sendBeacon(endpoint, encodeToBase64(JSON.stringify({ log })));
  }
};

export default { error, logOnAppDynamics, logOnOpenSearch };

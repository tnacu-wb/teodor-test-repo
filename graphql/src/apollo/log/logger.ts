import pino from 'pino';
import { redactionKeys } from './redaction-keys';
import { AsyncLocalStorage } from 'async_hooks';

const asyncLocalStorage = new AsyncLocalStorage<{ traceId: string }>();

const _logger = pino({
  level: 'info',
  redact: {
    paths: redactionKeys,
    censor: '**********'
  },
  transport: {
    target: 'pino-pretty',
    options: {
      colorize: true, // Enable colorized output
      translateTime: 'yyyy-mm-dd HH:MM:ss.l', // Format the timestamp
      ignore: 'pid,hostname,caller', // Ignore the automatic inclusion of "caller"
      // Custom format: Properly display the caller
      messageFormat: '[{caller}] {msg}'
    }
  },
  base: {
    pid: false // Disable process ID logging
  }
});

// Create a logger instance
const createLogger = (caller: string) => {
  // Helper function to log messages with a static caller
  const logMessage = (
    level: 'debug' | 'info' | 'warn' | 'error',
    message: string,
    meta: object = {}
  ) => {
    const store = asyncLocalStorage.getStore();
    const traceId = store ? ' ' + store.traceId : '';

    _logger[level]({
      msg: '[appName: opera-apollo-subgraphs]' + `${traceId} ` + `${message}`, // Main log message
      caller, // Configure the caller statically
      ...meta // Pass any additional metadata
    });
  };

  // Return a logger object with caller pre-configured
  return {
    debug: (message: string, meta: object = {}) => logMessage('debug', message, meta),
    info: (message: string, meta: object = {}) => logMessage('info', message, meta),
    warn: (message: string, meta: object = {}) => logMessage('warn', message, meta),
    error: (message: string, meta: object = {}) => logMessage('error', message, meta)
  };
};

export default createLogger;

export const getAsyncLocalStorage = () => asyncLocalStorage;

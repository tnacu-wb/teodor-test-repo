import dotenv from 'dotenv';
import { basename, resolve } from 'path';
import createLogger from '../log/logger';

const log = createLogger(basename(__filename));

// Define the environment variables LOCAL true, HELM will set the environment specific values.
if (process.env.LOCAL === 'true') {
  const localEnvPath = resolve(__dirname, '../../..', '.env.local');
  dotenv.config({ path: localEnvPath });
  log.info(`Environment file loaded for the local development: ${localEnvPath}`);
} else {
  log.info(`Environment variables should be loaded by the Helm chart`);
}

export const config = {
  READ_CONNECTION_TIMEOUT: Number(process.env.READ_CONNECTION_TIMEOUT) || 2000,
  PORT: process.env.PORT || 4000,
  INTROSPECTION: process.env.INTROSPECTION || 'false',
  // Option are console, dynatrace, none
  ENABLE_OPEN_TELEMETRY: process.env.ENABLE_OPEN_TELEMETRY || 'none',
  // Delay in ms before exporting spans
  SCHEDULED_DELAY_MILLIS: process.env.SCHEDULED_DELAY_MILLIS || 5000,
  // Max spans to keep in the buffer
  MAX_QUEUE_SIZE: process.env.MAX_QUEUE_SIZE || 2048,
  // Max spans to export in a single batch
  MAX_EXPORT_BATCH_SIZE: process.env.MAX_EXPORT_BATCH_SIZE || 512,
  // Timeout for sending a batch in ms
  EXPORT_TIMEOUT_MILLIS: process.env.EXPORT_TIMEOUT_MILLIS || 30000,
  DYNATRACE_URL: process.env.DYNATRACE_URL,
  DYNATRACE_TOKEN: process.env.DYNATRACE_TOKEN,
  CORS_ALLOWED_ORIGINS: ''
};

import axios from 'axios';
import { config } from '../config/configuration';
import createLogger from '../log/logger';
import { basename } from 'path';

const log = createLogger(basename(__filename));

export const createAxios = (baseURL: string) => {
  log.info(
    `Creating axios with Read connection timeout: ${config.READ_CONNECTION_TIMEOUT}, for baseURL ${baseURL}`
  );
  const axiosInstance = axios.create({
    baseURL,
    timeout: config.READ_CONNECTION_TIMEOUT // Timeout in milliseconds, e.g., 2000ms = 2 seconds
  });

  axiosInstance.interceptors.request.use((config) => {
    // Log the outgoing request details
    log.debug(
      `[Axios] Sending headers to Microservice for ${config.method?.toUpperCase()} ${config.url}:`,
      config.headers
    );
    return config;
  });

  return axiosInstance;
};

/**
 * Config barrel export.
 *
 * All configuration modules are re-exported here for convenient single-path imports:
 *   import { getEnvironmentConfig, getViewport, getLambdaTestProjects } from './config';
 */

export type { Environment, App, EnvironmentConfig, GetEnvironmentConfigOptions } from './environments';
export { getEnvironmentConfig } from './environments';

export type { ViewportName } from './browsers';
export { viewports, getViewport, localProjects, getLocalProjects } from './browsers';

export { getLambdaTestConfig, getLambdaTestProjects } from './lambdatest';

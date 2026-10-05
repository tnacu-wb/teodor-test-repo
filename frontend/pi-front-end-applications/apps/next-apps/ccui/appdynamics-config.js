global.appd = require('appdynamics');

appd.profile({
  controllerHostName: process.env.APPDYNAMICS_CONTROLLER_HOST_NAME,
  controllerPort: process.env.APPDYNAMICS_CONTROLLER_PORT,
  controllerSslEnabled: process.env.APPDYNAMICS_CONTROLLER_SSL_ENABLED,
  accountName: process.env.APPDYNAMICS_AGENT_ACCOUNT_NAME,
  accountAccessKey: process.env.APPDYNAMICS_AGENT_ACCOUNT_ACCESS_KEY, //required
  applicationName: process.env.APPDYNAMICS_AGENT_APPLICATION_NAME,
  tierName: 'ccui',
  nodeName: process.env.APPDYNAMICS_JAVA_AGENT_REUSE_NODE_NAME,
  reuseNode: true,
  enableGraphQL: true,
  reuseNodePrefix: process.env.APPDYNAMICS_JAVA_AGENT_REUSE_NODE_NAME_PREFIX,
  maxProcessSnapshotsPerPeriod: 0,
});

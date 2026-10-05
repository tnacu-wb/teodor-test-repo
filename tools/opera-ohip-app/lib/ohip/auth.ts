import axios from 'axios';
import { EnvConfig, Environment, getEnvConfig } from './environments';
import { validateEnvironment } from './validation';

const tokenCache = new Map<Environment, { token: string; expiry: number }>();

export async function getAccessToken(rawEnv: Environment): Promise<string> {
  const env = validateEnvironment(rawEnv);
  const cached = tokenCache.get(env);
  if (cached && Date.now() < cached.expiry - 60000) {
    return cached.token;
  }

  const config = getEnvConfig(env);
  const tokenUrl = `${config.baseUrl}/oauth/v1/tokens`;
  const params = new URLSearchParams();
  params.append('grant_type', 'client_credentials');
  if (config.scope) {
    params.append('scope', config.scope);
  }

  const headers: Record<string, string> = {
    'Content-Type': 'application/x-www-form-urlencoded',
    'x-app-key': config.appKey,
    Authorization: 'Basic ' + Buffer.from(`${config.clientId}:${config.clientSecret}`).toString('base64'),
  };
  if (config.enterpriseId) {
    headers['enterpriseId'] = config.enterpriseId;
  }

  const response = await axios.post(tokenUrl, params.toString(), { headers, timeout: 15000 });
  const token = response.data.access_token;
  const expiresIn = response.data.expires_in || 3600;
  tokenCache.set(env, { token, expiry: Date.now() + expiresIn * 1000 });
  return token;
}

export function getConfigForEnv(env: Environment): EnvConfig {
  return getEnvConfig(validateEnvironment(env));
}

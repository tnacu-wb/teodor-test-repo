import { randomBytes } from 'crypto';

// Dynamic import helper for next/headers to avoid bundling in client components
const getNextHeaders = async () => {
  if (typeof window !== 'undefined') return null;
  try {
    const { headers } = await import('next/headers');
    return headers;
  } catch {
    return null;
  }
};

export const getLocalhostHeadersAsync = async (
  isServerSide: boolean
): Promise<Record<string, string>> => {
  let host;
  try {
    if (isServerSide) {
      const headersFn = await getNextHeaders();
      if (headersFn) {
        host = (await headersFn()).get('host') ?? '';
      }
    } else if (typeof window !== 'undefined') {
      host = window?.location?.host;
    }
  } catch {
    return {};
  }
  const isLocalhost = host?.includes('localhost');
  if (isLocalhost) {
    return {
      ...(isServerSide && { Referer: `http://${host}/` }),
      'X-Dev-Nonce': randomBytes(16).toString('base64'),
    };
  }

  return {};
};

import { initializeUnleash } from '@whitbread-eos/utils/instrumentation';

export async function initNode(): Promise<void> {
  const appName = process.env.NEXT_PUBLIC_UNLEASH_APP_NAME || 'business-booker';
  await initializeUnleash(appName);
}

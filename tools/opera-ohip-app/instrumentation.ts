export async function register() {
  if (process.env.NEXT_RUNTIME === 'nodejs') {
    const { validateEnvironmentConfig } = await import('./lib/env-check');
    const result = validateEnvironmentConfig();

    if (result.errors.length > 0) {
      for (const error of result.errors) {
        console.warn(`[env-check] ${error}`);
      }
    }

    if (!result.valid) {
      console.error(
        '[env-check] No OHIP environments configured. The app will fail on API requests. See .env.example for required variables.'
      );
    } else {
      console.info(`[env-check] OHIP environments ready: ${result.configured.join(', ')}`);
    }
  }
}

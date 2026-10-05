export async function register() {
  if (process.env.NEXT_RUNTIME === 'nodejs') {
    const { initNode } = await import('./instrumentation-node');
    await initNode();
  }
}

import { NextResponse } from 'next/server';
import axios from 'axios';
import { getEnvConfig } from '@/lib/ohip/environments';

export async function GET() {
  try {
    const config = getEnvConfig('UAT');
    await axios.get(`${config.baseUrl}/oauth/v1/tokens`, { timeout: 5000, validateStatus: () => true });
    return NextResponse.json({
      status: 'healthy',
      ohip: 'reachable',
      gateway: config.baseUrl,
      timestamp: new Date().toISOString(),
    });
  } catch (error: unknown) {
    const message = error instanceof Error ? error.message : 'Unknown error';
    return NextResponse.json(
      { status: 'unhealthy', ohip: 'unreachable', error: message, timestamp: new Date().toISOString() },
      { status: 503 }
    );
  }
}

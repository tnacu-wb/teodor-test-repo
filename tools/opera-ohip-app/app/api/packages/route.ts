import { NextRequest } from 'next/server';
import { updatePackage } from '@/lib/ohip/packages';
import {
  successResponse,
  errorResponse,
  validateRequiredFields,
  parseRequestBody,
  isErrorResponse,
  resolveEnvironment,
} from '@/lib/api-utils';

export async function POST(request: NextRequest) {
  const startTime = Date.now();
  try {
    const bodyOrError = await parseRequestBody(request);
    if (isErrorResponse(bodyOrError)) return bodyOrError;

    const validationError = validateRequiredFields(bodyOrError, ['hotelId', 'packageCode', 'payload']);
    if (validationError) return validationError;

    const { hotelId, packageCode, payload, environment } = bodyOrError;
    const env = resolveEnvironment(environment);
    if (isErrorResponse(env)) return env;
    const result = await updatePackage(
      hotelId as string,
      packageCode as string,
      payload as Parameters<typeof updatePackage>[2],
      env
    );
    return successResponse(result, env, startTime);
  } catch (error: unknown) {
    return errorResponse(error, startTime);
  }
}

import { NextRequest } from 'next/server';
import { updateRestrictions } from '@/lib/ohip/restrictions';
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

    const validationError = validateRequiredFields(bodyOrError, ['hotelId', 'payload']);
    if (validationError) return validationError;

    const { hotelId, payload, environment } = bodyOrError;
    const env = resolveEnvironment(environment);
    if (isErrorResponse(env)) return env;
    const result = await updateRestrictions(
      hotelId as string,
      payload as Parameters<typeof updateRestrictions>[1],
      env
    );
    return successResponse(result, env, startTime);
  } catch (error: unknown) {
    return errorResponse(error, startTime);
  }
}

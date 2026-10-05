import { NextRequest } from 'next/server';
import { updateDailySchedules } from '@/lib/ohip/ratePlan';
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

    const validationError = validateRequiredFields(bodyOrError, ['hotelId', 'ratePlanCode', 'payload']);
    if (validationError) return validationError;

    const { hotelId, ratePlanCode, payload, environment } = bodyOrError;
    const env = resolveEnvironment(environment);
    if (isErrorResponse(env)) return env;
    const result = await updateDailySchedules(
      hotelId as string,
      ratePlanCode as string,
      payload as Parameters<typeof updateDailySchedules>[2],
      env
    );
    return successResponse(result, env, startTime);
  } catch (error: unknown) {
    return errorResponse(error, startTime);
  }
}

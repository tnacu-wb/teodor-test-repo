import { Environment } from './ohip/environments';

// ─── API Response Types ───────────────────────────────────────────────────────

export interface ApiSuccessResponse<T = unknown> {
  success: true;
  data: T;
  environment: Environment;
  duration: string;
  timestamp: string;
}

export interface ApiErrorResponse {
  success: false;
  error: string | Record<string, unknown>;
  status: number;
  duration: string;
  timestamp: string;
}

export type ApiResponse<T = unknown> = ApiSuccessResponse<T> | ApiErrorResponse;

// ─── API Request Types ────────────────────────────────────────────────────────

export interface RatePlanRequest {
  hotelId: string;
  ratePlanCode: string;
  payload: {
    dailyRateScheduleRange: {
      hotelId: string;
      ratePlanCode: string;
      roomTypes: string[];
      roomClasses: string[];
      dateRange: {
        timeSpan: { startDate: string; endDate: string };
        sunday: boolean;
        monday: boolean;
        tuesday: boolean;
        wednesday: boolean;
        thursday: boolean;
        friday: boolean;
        saturday: boolean;
      };
      incrementFlag: boolean;
      rateAmounts: {
        onePersonRate: string;
        twoPersonRate?: string | null;
        extraPersonRate?: string | null;
        overrideFloorAmount: boolean;
      };
    };
  };
  environment?: Environment;
}

export interface RestrictionsRequest {
  hotelId: string;
  payload: {
    hotelId: string;
    date: string;
  };
  environment?: Environment;
}

export interface SellLimitsRequest {
  hotelId: string;
  payload: {
    sellLimitsByDateRange: Array<{
      sellLimitDateRanges: Array<{
        actionType: string;
        startDate: string;
        endDate: string;
        sunday: boolean;
        monday: boolean;
        tuesday: boolean;
        wednesday: boolean;
        thursday: boolean;
        friday: boolean;
        saturday: boolean;
        amount: string;
        flatOrPercentage: string;
      }>;
      hotelId: string;
      codeCategory: string;
      codeValue: string;
    }>;
  };
  environment?: Environment;
}

// ─── Client-side UI Types ─────────────────────────────────────────────────────

export interface ResponseState {
  status: 'idle' | 'loading' | 'success' | 'error';
  operation?: string;
  message?: string;
  data?: Record<string, unknown>;
  timestamp?: string;
}

// ─── OHIP Error Types ─────────────────────────────────────────────────────────

export interface OhipErrorDetail {
  type?: string;
  title?: string;
  detail?: string;
  status?: number;
  'o:errorCode'?: string;
}

export function isAxiosError(error: unknown): error is {
  isAxiosError: true;
  response?: { status: number; data: OhipErrorDetail | string };
  message: string;
} {
  if (typeof error !== 'object' || error === null) {
    return false;
  }
  const candidate = error as Record<string, unknown>;
  // Axios tags its errors with `isAxiosError: true`; rely on that marker rather
  // than the mere presence of a `message`/`response` field so unrelated objects
  // aren't misclassified.
  return candidate.isAxiosError === true && typeof candidate.message === 'string';
}

import { UseMutationResult } from '@tanstack/react-query';
import { Dispatch, SetStateAction } from 'react';
import {
  Control,
  ControllerRenderProps,
  FieldErrors,
  UseFormClearErrors,
  UseFormGetValues,
  UseFormSetValue,
} from 'react-hook-form';

export interface BatchEligibility {
  region: string;
  channel: string;
  platforms: string[];
}

export type CreatePromoBatchVariables = {
  operaPromoCode: string;
  batchCount: number;
  codeLength: number;
  expiryDate: string;
  notes: string;
  prefix: string;
  hotelId: string;
  requestedBy: string;
  campaignName: string;
};

export type CreatePromoBatchResponse = {
  createPromoBatch: {
    batchId: string;
    status: string;
    createdAt: string;
    operaPromoCode: string;
    prefix: string;
    batchCount: number;
    codeLength: number;
    s3Key?: string;
    notes: string;
    requestedBy: string;
    expiryDate: string;
    updatedAt?: string;
    completedAt?: string;
    campaignName: string;
    isMultiple?: boolean;
    batchEligibilities: BatchEligibility[];
  };
};

export type CreatePromoCodeFormContextType = {
  createPromoMutation: UseMutationResult<
    CreatePromoBatchResponse,
    unknown,
    CreatePromoBatchVariables
  >;
  createPromoData?: CreatePromoBatchResponse;
  createPromoIsLoading: boolean;
  createPromoIsError: boolean;
  createPromoError: unknown;
  createPromoIsSuccess: boolean;
  loadingTransition: boolean;
  setLoadingTransition: Dispatch<SetStateAction<boolean>>;
};

export interface HandlePromoApiErrorParams {
  createPromoIsError: boolean;
  createPromoError: any;
  duplicatePrefixError: string;
  expiryDateInvalidError: string;
  promotionsInvalidError: string;
  handleSetError: (name: string, error: { type: string; message: string }) => void;
}

export interface FieldController {
  field: ControllerRenderProps;
  label: string;
  name: string;
  type: string;
  testId: string;
  isDisabled?: boolean;
}
export interface FormField {
  name: string;
  label: string;
  type?: string;
  testid?: string;
}

export interface CreatePromtionCodeFormDetailsProps {
  control: Control<any>;
  formField: FormField;
  errors: FieldErrors<any>;
  getValues: UseFormGetValues<any>;
  handleSetError: (name: string, error: { type: string; message: string }) => void;
  clearErrors: UseFormClearErrors<any>;
  setValue: UseFormSetValue<any>;
  handleSetValue?: UseFormSetValue<any>;
  handleClearErrors?: UseFormClearErrors<any>;
}

export type PlatformKey = 'PI' | 'CCUI' | 'PREMIER_INN_BUSINESS';
export type CountryKey = 'GB' | 'DE';

export interface PlatformItem {
  enabled: boolean;
  selected: { web: boolean; app: boolean };
}

export interface CountryPlatformConfig {
  enabled: boolean;
  platforms: Record<PlatformKey, PlatformItem>;
}

export type PlatformConfig = Record<CountryKey, CountryPlatformConfig>;

export interface PlatformConfigFieldProps {
  value: PlatformConfig;
  onChange: (value: PlatformConfig) => void;
  onBlur?: () => void;
  label: string;
  description: string;
  testId?: string;
}

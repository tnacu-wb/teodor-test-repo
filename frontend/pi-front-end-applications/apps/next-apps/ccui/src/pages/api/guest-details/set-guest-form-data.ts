import { encodeToBase64, logger } from '@whitbread-eos/utils';
import { RedisKeyPrefix, RedisStorageServer } from '@whitbread-eos/utils/server';
import type { NextApiRequest, NextApiResponse } from 'next';
import * as yup from 'yup';

interface GuestFormDataResponse {
  success?: boolean;
  error?: string;
}

const BASKET_REFERENCE_REGEX =
  /^[A-Z]{3}-[a-z0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$/;

const ALLOWED_FORM_DATA_KEYS = [
  'reasonForStay',
  'title',
  'firstName',
  'lastName',
  'email',
  'phone',
  'landline',
  'companyName',
  'addressLine1',
  'addressLine2',
  'addressLine3',
  'addressLine4',
  'postalCode',
  'manualAddressToggle',
  'cityName',
  'postcodeAddress',
  'addressSelection',
  'countryCode',
  'acceptFutureMailing',
  'bookingForSomeoneElse',
  'basketReferenceId',
  'leadGuest',
  'billing_countryCode',
  'billing_companyName',
  'billing_addressLine1',
  'billing_addressLine2',
  'billing_addressLine3',
  'billing_addressLine4',
  'billing_cityName',
  'billing_postalCode',
  'billing_addressSelection',
  'billingAddressCheckbox',
  'billing_postcodeAddress',
  'billing_manualAddressToggle',
  'whoBookerIsTabs',
  'password',
  'confirmPassword',
];

const ALLOWED_LEAD_GUEST_KEYS = [
  'stayInThisRoom',
  'title',
  'firstName',
  'lastName',
  'email',
  'accompanyingfirstName',
  'accompanyinglastName',
  'addressLine1',
  'addressLine2',
  'addressLine3',
  'addressLine4',
  'postcodeAddress',
  'companyName',
  'country',
  'countryCode',
];

const leadGuestSchema = yup
  .object()
  .shape({
    stayInThisRoom: yup.boolean().notRequired().nullable(),
    title: yup.string().notRequired().nullable(),
    firstName: yup.string().notRequired().nullable(),
    lastName: yup.string().notRequired().nullable(),
    email: yup.string().notRequired().nullable(),
    accompanyingfirstName: yup.string().notRequired().nullable(),
    accompanyinglastName: yup.string().notRequired().nullable(),
    addressLine1: yup.string().notRequired().nullable(),
    addressLine2: yup.string().notRequired().nullable(),
    addressLine3: yup.string().notRequired().nullable(),
    addressLine4: yup.string().notRequired().nullable(),
    postcodeAddress: yup.string().notRequired().nullable(),
    companyName: yup.string().notRequired().nullable(),
    country: yup.string().notRequired().nullable(),
    countryCode: yup.string().notRequired().nullable(),
  })
  .test('only-allowed-lead-guest-keys', 'Invalid formData payload', (value) => {
    if (typeof value !== 'object' || value === null) {
      return false;
    }
    return Object.keys(value).every((key) => ALLOWED_LEAD_GUEST_KEYS.includes(key));
  });

const guestFormDataSchema = yup
  .object()
  .shape({
    reasonForStay: yup.string().notRequired().nullable(),
    title: yup.string().notRequired().nullable(),
    firstName: yup.string().notRequired().nullable(),
    lastName: yup.string().notRequired().nullable(),
    email: yup.string().notRequired().nullable(),
    phone: yup.string().notRequired().nullable(),
    landline: yup.string().notRequired().nullable(),
    companyName: yup.string().notRequired().nullable(),
    addressLine1: yup.string().notRequired().nullable(),
    addressLine2: yup.string().notRequired().nullable(),
    addressLine3: yup.string().notRequired().nullable(),
    addressLine4: yup.string().notRequired().nullable(),
    postalCode: yup.string().notRequired().nullable(),
    manualAddressToggle: yup.string().notRequired().nullable(),
    cityName: yup.string().notRequired().nullable(),
    postcodeAddress: yup.string().notRequired().nullable(),
    addressSelection: yup.string().notRequired().nullable(),
    countryCode: yup.string().notRequired().nullable(),
    acceptFutureMailing: yup.boolean().notRequired().nullable(),
    bookingForSomeoneElse: yup.boolean().notRequired().nullable(),
    basketReferenceId: yup.string().notRequired().nullable(),
    leadGuest: yup.array().of(leadGuestSchema).notRequired().nullable(),
    billing_countryCode: yup.string().notRequired().nullable(),
    billing_companyName: yup.string().notRequired().nullable(),
    billing_addressLine1: yup.string().notRequired().nullable(),
    billing_addressLine2: yup.string().notRequired().nullable(),
    billing_addressLine3: yup.string().notRequired().nullable(),
    billing_addressLine4: yup.string().notRequired().nullable(),
    billing_cityName: yup.string().notRequired().nullable(),
    billing_postcodeAddress: yup.string().notRequired().nullable(),
    billing_postalCode: yup.string().notRequired().nullable(),
    billing_addressSelection: yup.string().notRequired().nullable(),
    billing_manualAddressToggle: yup.string().notRequired().nullable(),
    billingAddressCheckbox: yup.boolean().notRequired().nullable(),
    whoBookerIsTabs: yup.string().notRequired().nullable(),
    password: yup.string().notRequired().nullable(),
    confirmPassword: yup.string().notRequired().nullable(),
  })
  .test('only-allowed-keys', 'Invalid formData payload', (value) => {
    if (typeof value !== 'object' || value === null) {
      return false;
    }
    return Object.keys(value).every((key) => ALLOWED_FORM_DATA_KEYS.includes(key));
  });

export default async function handler(
  req: NextApiRequest,
  res: NextApiResponse<GuestFormDataResponse>
): Promise<void> {
  res.setHeader('Cache-Control', 'no-store');

  if (req.method === 'POST') {
    const { basketReferenceId, formData } = req.body ?? {};

    if (!basketReferenceId || !formData) {
      res.status(400).json({ error: 'basketReferenceId and formData are required' });
      return;
    }
    if (!BASKET_REFERENCE_REGEX.test(String(basketReferenceId))) {
      res.status(400).json({ error: 'Invalid basketReferenceId format' });
      return;
    }

    try {
      await guestFormDataSchema.validate(formData, { strict: true });
    } catch (validationError) {
      res.status(400).json({ error: 'Invalid formData payload' });
      return;
    }

    try {
      const redisKey = `${RedisKeyPrefix.GUEST_DETAILS_FORM_DATA_CCUI}::${basketReferenceId}`;
      const redisStorage = RedisStorageServer.getInstance();
      const encodedFormData = encodeToBase64(JSON.stringify(formData));

      if (!encodedFormData) {
        throw new Error('Failed to encode formData');
      }

      await redisStorage.setItem(redisKey, encodedFormData);
      res.status(200).json({ success: true });
    } catch (error) {
      logger.error({ error }, 'CCUI_SAVE_GUEST_DETAILS_FORM_DATA_ERROR');
      res.status(500).json({ error: 'Failed to save guest details form data' });
    }
    return;
  }

  res.status(405).json({ error: 'Method not allowed' });
}

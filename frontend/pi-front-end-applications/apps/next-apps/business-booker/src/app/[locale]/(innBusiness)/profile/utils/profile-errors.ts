export type ProfileFieldErrors = Record<string, string>;
export type ProfileContactDetails = {
  telephone?: string | null;
  mobile?: string | null;
};

const PROFILE_CONTACT_ERROR_MAP = [
  { code: 1, field: 'title.displayValue', key: 'profile.profile.form.title.required' },
  { code: 2, field: 'title.displayValue', key: 'profile.profile.form.title.invalid' },
  { code: 3, field: 'firstName', key: 'profile.profile.form.firstname.required' },
  { code: 4, field: 'firstName', key: 'profile.profile.form.firstname.invalid' },
  { code: 5, field: 'lastName', key: 'profile.profile.form.lastname.required' },
  { code: 6, field: 'lastName', key: 'profile.profile.form.lastname.invalid' },
  { code: 7, field: 'email', key: 'profile.profile.form.email.required' },
  { code: 8, field: 'email', key: 'profile.profile.form.email.invalid' },
];

const PROFILE_NUMERIC_ONLY_CODE = 11;
const PROFILE_MOBILE_INVALID_CODE = 9;
const PROFILE_TELEPHONE_INVALID_CODE = 10;

export const buildProfileFieldErrors = (
  codes: number[],
  t: (key: string) => string,
  contactDetail?: ProfileContactDetails
): ProfileFieldErrors => {
  if (!codes.length) {
    return {};
  }

  const codeSet = new Set(codes);
  const fieldErrors: ProfileFieldErrors = {};
  const setIfEmpty = (field: string, message: string) => {
    if (!fieldErrors[field]) {
      fieldErrors[field] = message;
    }
  };

  PROFILE_CONTACT_ERROR_MAP.forEach(({ code, field, key }) => {
    if (codeSet.has(code)) {
      setIfEmpty(field, t(key));
    }
  });

  if (codeSet.has(PROFILE_NUMERIC_ONLY_CODE)) {
    if (contactDetail?.telephone) {
      setIfEmpty('phoneNumber.phoneNumber', t('profile.profile.form.phone.numericOnly'));
    }
    if (contactDetail?.mobile) {
      setIfEmpty('alternatePhoneNumber.phoneNumber', t('profile.profile.form.phone.numericOnly'));
    }
  }
  if (codeSet.has(PROFILE_TELEPHONE_INVALID_CODE)) {
    setIfEmpty('phoneNumber.phoneNumber', t('profile.profile.form.telephone.invalid'));
  }
  if (codeSet.has(PROFILE_MOBILE_INVALID_CODE)) {
    setIfEmpty('alternatePhoneNumber.phoneNumber', t('profile.profile.form.mobile.invalid'));
  }

  return fieldErrors;
};

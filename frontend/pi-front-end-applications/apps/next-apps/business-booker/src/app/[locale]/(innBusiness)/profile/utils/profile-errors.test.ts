import { buildProfileFieldErrors } from './profile-errors';

const t = (key: string) => key;

describe('buildProfileFieldErrors', () => {
  it('returns empty object when no codes are provided', () => {
    expect(buildProfileFieldErrors([], t)).toEqual({});
  });

  it('maps field errors based on provided codes', () => {
    const errors = buildProfileFieldErrors([1, 4, 8], t);
    expect(errors).toEqual({
      'title.displayValue': 'profile.profile.form.title.required',
      firstName: 'profile.profile.form.firstname.invalid',
      email: 'profile.profile.form.email.invalid',
    });
  });

  it('adds numeric-only errors only when values are present', () => {
    const errors = buildProfileFieldErrors([11], t, {
      telephone: '+441234',
      mobile: '123',
    });
    expect(errors).toEqual({
      'phoneNumber.phoneNumber': 'profile.profile.form.phone.numericOnly',
      'alternatePhoneNumber.phoneNumber': 'profile.profile.form.phone.numericOnly',
    });
  });
});

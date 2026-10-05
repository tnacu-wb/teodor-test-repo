import jwtDecode from 'jwt-decode';

export default function decodeIdToken(token: string) {
  let unpackedJwt = { email: '', name: '', exp: null, profile: { employeeId: '' } };

  try {
    if (token?.length) {
      unpackedJwt = jwtDecode(token);
    }
  } catch (e) {
    // eslint-disable-next-line no-console
    console.log(e);
  }

  if (unpackedJwt && !unpackedJwt?.email?.length && unpackedJwt?.name?.length) {
    unpackedJwt.email = unpackedJwt.name;
  }

  return unpackedJwt;
}

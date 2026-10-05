export function formatReservationNumberInQuery(queryString: string) {
  if (!queryString) return '';

  const params = new URLSearchParams(queryString);
  const original = params.get('reservationNumber') || '';

  let letters = '';
  let digits = '';

  // Single-pass scan, optimized
  for (let i = 0; i < original.length; i++) {
    const code = original.charCodeAt(i);

    // Letters A-Z or a-z
    if (letters.length < 3 && ((code >= 65 && code <= 90) || (code >= 97 && code <= 122))) {
      letters += original[i];
    }
    // Digits 0-9
    else if (digits.length < 7 && code >= 48 && code <= 57) {
      digits += original[i];
    }

    // Stop early if done
    if (letters.length === 3 && digits.length === 7) break;
  }

  params.set('reservationNumber', letters + digits);
  return params.toString();
}

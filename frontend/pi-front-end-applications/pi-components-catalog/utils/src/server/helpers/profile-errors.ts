type ErrorPayload = {
  code?: string;
  details?: unknown;
};

type GraphQLErrorLike = {
  message?: string;
};

const PROFILE_CONTACT_ERROR_CODE = '7102';

const parseErrorPayload = (message?: string): ErrorPayload | null => {
  if (!message) {
    return null;
  }
  try {
    return JSON.parse(message);
  } catch {
    return null;
  }
};

const extractNumericCodes = (input: string) => {
  const matches = input.match(/\d+/g) ?? [];
  return matches.map((match) => Number(match)).filter((code) => !Number.isNaN(code));
};

export const findErrorCode = (code: string, errors: GraphQLErrorLike[]) => {
  return errors.some((error) => parseErrorPayload(error?.message)?.code === code);
};

export const extractProfileContactErrorCodes = (errors: GraphQLErrorLike[]) => {
  const codes = new Set<number>();

  errors.forEach((error) => {
    const parsed = parseErrorPayload(error?.message);
    if (!parsed || parsed.code !== PROFILE_CONTACT_ERROR_CODE) {
      return;
    }

    const details = Array.isArray(parsed.details) ? parsed.details : [];
    details.forEach((detail) => {
      if (typeof detail !== 'string') {
        return;
      }
      const lastColonIndex = detail.lastIndexOf(':');
      const codesSection = lastColonIndex >= 0 ? detail.slice(lastColonIndex + 1) : detail;
      extractNumericCodes(codesSection).forEach((code) => codes.add(code));
    });
  });

  return Array.from(codes).sort((a, b) => a - b);
};

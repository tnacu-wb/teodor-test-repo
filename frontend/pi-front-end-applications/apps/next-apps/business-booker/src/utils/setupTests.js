import crypto from 'crypto';
import { TextEncoder, TextDecoder, Request } from 'util';

globalThis.IS_REACT_ACT_ENVIRONMENT = true;
global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;
global.Request = Request;

jest.mock('next/cache', () => ({
  unstable_cache: jest.fn((fn) => fn),
  revalidateTag: jest.fn(),
  revalidatePath: jest.fn(),
}));

jest.mock('next/headers', () => ({
  cookies: jest.fn(() => ({
    get: jest.fn(),
    set: jest.fn(),
    has: jest.fn(),
  })),
  headers: jest.fn(() => ({
    get: jest.fn(),
    has: jest.fn(),
  })),
}));

global.matchMedia =
  global.matchMedia ||
  function () {
    return {
      matches: false,
      addListener: function () {
        return true;
      },
      removeListener: function () {
        return true;
      },
    };
  };

Object.defineProperty(global, 'crypto', {
  value: {
    getRandomValues: (arr) => crypto.randomBytes(arr.length),
  },
});

window.scrollTo = jest.fn();

/* eslint-disable @typescript-eslint/no-empty-function */
// eslint-disable-next-line @typescript-eslint/no-require-imports
const crypto = require('crypto');
global.matchMedia =
  global.matchMedia ||
  function () {
    return {
      matches: false,
      addListener: function () {},
      removeListener: function () {},
    };
  };

Object.defineProperty(global, 'crypto', {
  value: {
    getRandomValues: (arr) => crypto.randomBytes(arr.length),
  },
});

process.env.AUTH0_SECRET = 'AUTH0_SECRET';
process.env.AUTH0_BASE_URL = 'AUTH0_BASE_URL';
process.env.AUTH0_ISSUER_BASE_URL = 'http://test.com';
process.env.AUTH0_CLIENT_ID = 'AUTH0_CLIENT_ID';
process.env.AUTH0_CLIENT_SECRET = 'AUTH0_CLIENT_SECRET';
process.env.AUTH0_SESSION_ROLLING = true;
process.env.AUTH0_SESSION_ROLLING_DURATION = 7200;

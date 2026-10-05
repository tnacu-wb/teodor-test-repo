import { TextDecoder, TextEncoder } from 'util';

global.matchMedia =
  global.matchMedia ||
  function () {
    return {
      matches: false,
      addListener: function () {},
      removeListener: function () {},
    };
  };

global.TextEncoder = TextEncoder;
global.TextDecoder = TextDecoder;

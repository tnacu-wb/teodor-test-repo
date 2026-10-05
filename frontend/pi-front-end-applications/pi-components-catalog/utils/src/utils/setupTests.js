/* eslint-disable @typescript-eslint/no-empty-function */
jest.mock('nanoid', () => {
  return {
    nanoid: () => {},
  };
});
global.matchMedia =
  global.matchMedia ||
  function () {
    return {
      matches: false,
      addListener: function () {},
      removeListener: function () {},
    };
  };

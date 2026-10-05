import '@testing-library/jest-dom';

import useCustomLocale from './use-custom-locale';

const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('Use custom locale hook', () => {
  it('should return correct value if locale is gb', () => {
    mockUseRouter.mockReturnValue({
      locale: 'gb',
    });
    expect(useCustomLocale()).toEqual({
      country: 'gb',
      language: 'en',
    });
  });

  it('should return correct value if locale is de', () => {
    mockUseRouter.mockReturnValue({
      locale: 'de',
    });
    expect(useCustomLocale()).toEqual({
      country: 'de',
      language: 'de',
    });
  });
});

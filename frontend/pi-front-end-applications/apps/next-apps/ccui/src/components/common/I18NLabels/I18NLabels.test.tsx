import '@testing-library/jest-dom';

import { render } from '~utils/test-utils';

import I18NLabels from './I18NLabels';

const router: any = { locale: 'en' };

jest.mock('next/router', () => ({
  useRouter() {
    return {
      router: router,
    };
  },
}));

describe('<I18NLabels />', () => {
  it('should print a console.log for default', () => {
    const spyGroupCollaped = jest.spyOn(console, 'groupCollapsed').mockImplementation(() => null);
    const spyLog = jest.spyOn(console, 'log').mockImplementation(() => null);
    const spyGroupEnd = jest.spyOn(console, 'groupEnd').mockImplementation(() => null);

    render(<I18NLabels />);
    expect(spyLog).toHaveBeenCalled();

    spyGroupCollaped.mockReset();
    spyLog.mockReset();
    spyGroupEnd.mockReset();
  });
});

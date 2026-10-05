import { render } from '../../../utils/test-utils';
import LanguageMenu from './LanguageMenu.component';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: jest.fn(),
    refresh: jest.fn(),
  })),
  usePathname: jest.fn(),
}));

const mockProps = {
  languages: [
    {
      code: 'gb',
      language: 'English',
      flagUrl: '/etc/clientlibs/pi-header/resources/images/british-round.svg',
      url: '/gb/en/inn-business/home.html',
    },
    {
      code: 'de',
      language: 'Deutsch',
      flagUrl: '/etc/clientlibs/pi-header/resources/images/germany-round.svg',
      url: '/de/de/inn-business/home.html',
    },
  ],
  language: 'en',
  enIcon: '/',
  deIcon: '/',
};

describe('LanguageMenu component', () => {
  it('should render Language Menu', () => {
    const { getByTestId } = render(<LanguageMenu {...mockProps} />);
    expect(getByTestId('Language-Switcher-Button')).toBeInTheDocument();
  });
  it('should render the Language Menu in EN', () => {
    mockProps.language = 'en';
    const { getByAltText } = render(<LanguageMenu {...mockProps} />);
    expect(getByAltText('English')).toBeInTheDocument();
  });
  it('should render the Language Menu in DE', () => {
    mockProps.language = 'de';
    const { getByAltText } = render(<LanguageMenu {...mockProps} />);
    expect(getByAltText('Deutsch')).toBeInTheDocument();
  });
});

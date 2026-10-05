import { Area } from '@whitbread-eos/api';

import { BritishFlagRounded, GermanFlagRounded, Tick24 } from '../../assets/icons';
import { formatDataTestId } from '../../utils/formatters';
import { render } from '../../utils/test-utils';
import Icon from '../Icon';
import LanguageSelector, { Props } from './LanguageSelector.component';

const mockUseRouter = jest.fn();
const routerMock = {
  locale: 'en',
  asPath: '/gb/en/home.html',
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter.mockReturnValue(routerMock),
}));

const props: Props = {
  currentLanguage: 'en',
  prefixDataTestId: '',
  languagesList: [
    {
      locale: 'en' as 'en' | 'de',
      languageName: 'britishLanguage',
      icon: <Icon svg={<BritishFlagRounded cursor="pointer" data-testid="britishFlag" />} />,
    },
    {
      locale: 'de' as 'en' | 'de',
      languageName: 'germanLanguage',
      icon: <Icon svg={<GermanFlagRounded cursor="pointer" data-testid="germanFlag" />} />,
    },
  ],
  tickIcon: <Icon svg={<Tick24 />} />,
  area: Area.PI,
};

describe('LanguageSelector', () => {
  beforeEach(() => {
    mockUseRouter.mockReturnValue({
      locale: 'en',
      asPath: '/gb/en/home.html',
    });
  });
  it('should render a <LanguageSelector> with children', function () {
    const { queryAllByTestId, getByTestId } = render(<LanguageSelector {...props} />);

    expect(
      getByTestId(formatDataTestId(props.prefixDataTestId, 'languageSelectorContainer'))
    ).toBeInTheDocument();

    const britishFlags = queryAllByTestId('britishFlag');
    const germanyFlags = queryAllByTestId('germanFlag');
    expect(britishFlags.length).toBe(2);
    expect(germanyFlags.length).toBe(1);
  });

  it('should render currentLang EN', function () {
    const { getByText } = render(<LanguageSelector {...props} />);

    const britishLanguage = getByText('britishLanguage');
    expect(britishLanguage).toBeInTheDocument();
  });

  it('should render currentLang DE', function () {
    props.currentLanguage = 'de';
    mockUseRouter.mockReturnValue({
      locale: 'de',
      asPath: '/de/home.html',
    });

    const { getByText } = render(<LanguageSelector {...props} />);

    const germanLanguage = getByText('germanLanguage');
    expect(germanLanguage).toBeInTheDocument();
  });
});

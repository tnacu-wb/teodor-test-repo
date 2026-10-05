import { render } from '@testing-library/react';
import {
  BritishFlagRegular,
  GermanFlagRegular,
  Icon,
  LanguageOptions,
  Tick24,
} from '@whitbread-eos/atoms';

import { mockedBBLabels } from './../mockResponse';
import BBNavigationMenu, { type Props } from './BBNavigationMenu.component';

const languageMenuLabels = [
  { locale: 'en', languageName: 'English', icon: <Icon svg={<BritishFlagRegular />} /> },
  { locale: 'en', languageName: 'German', icon: <Icon svg={<GermanFlagRegular />} /> },
] as LanguageOptions[];

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  Popover: () => <div />,
  LanguageSelectorSwitcher: () => <div data-testid="languageSelector" />,
}));

const mockIsLogoutButton = jest.fn();

const mockProps: Props = {
  navigationLabels: [
    {
      id: 'Business',
      navTitle: mockedBBLabels.content.menu.business,
      subNav: mockedBBLabels.content.subNav,
    },
    {
      id: 'Account',
      navTitle: 'Business Account',
      subNav: [],
    },
    {
      id: 'Company',
      navTitle: 'Company',
      subNav: [],
    },
    {
      id: 'CompanyName',
      navTitle: 'Company Name',
      subNav: [],
    },
  ],
  tickIcon: <Icon svg={<Tick24 />} />,
  language: 'en',
  languageMenuLabels,
  dataTestId: 'BusinessNavMenu',
  isLogoutButton: mockIsLogoutButton,
};

describe('BusinessNavMenu', () => {
  it('should render a <BBNavigationMenu> with default props ', function () {
    const { getByTestId } = render(<BBNavigationMenu {...mockProps} />);
    expect(getByTestId('BusinessNavMenu-NavigationLinks')).toBeTruthy();
  });

  it('should have DE as default language ', function () {
    const { getByTestId } = render(<BBNavigationMenu {...mockProps} language="de" />);
    expect(getByTestId('languageSelector')).toBeInTheDocument();
  });
});

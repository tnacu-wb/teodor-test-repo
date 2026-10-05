import { Language } from '@whitbread-eos/api';
import {
  BritishFlagRegular,
  GermanFlagRegular,
  Icon,
  LanguageOptions,
  Tick24,
} from '@whitbread-eos/atoms';

import { render, userEvent } from '../../../../utils/test-utils';
import { mobileHeaderLabels } from './../mockResponse';
import BBNavigationMenuMobile from './BBNavigationMenuMobile.component';

const languageMenuLabels = [
  { locale: 'en', languageName: 'English', icon: <Icon svg={<BritishFlagRegular />} /> },
  { locale: 'en', languageName: 'German', icon: <Icon svg={<GermanFlagRegular />} /> },
] as LanguageOptions[];

const mockIsLogoutButton = jest.fn();

const mockProps = {
  navigationLabels: mobileHeaderLabels,
  tickIcon: <Icon svg={<Tick24 />} />,
  language: 'en' as Language,
  languageMenuLabels,
  isLogoutButton: mockIsLogoutButton,
  genericLabels: {
    language: 'English',
    mobileMenuButton: 'Menu',
  },
};

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

describe('BBNavigationMenuMobile', () => {
  it('should render a <BBNavigationMenuMobile> with default props ', function () {
    const { getByTestId, queryByTestId } = render(<BBNavigationMenuMobile {...mockProps} />);
    expect(getByTestId('BusinessNavMobile')).toBeTruthy();
    expect(queryByTestId('modalSideBar')).not.toBeInTheDocument();
  });

  it('should show the mobile menu nav when clicking on the burger button ', async function () {
    const { getByTestId } = render(<BBNavigationMenuMobile {...mockProps} />);
    const toggleButton = getByTestId('BusinessNavMobile-toggleButton');

    await userEvent.click(toggleButton);

    expect(getByTestId('modalSideBar')).toBeInTheDocument();
  });

  it('should show the Language sidebar when clicking the Language option', async function () {
    const { getByTestId } = render(<BBNavigationMenuMobile {...mockProps} />);

    await userEvent.click(getByTestId('burgerMenu'));
    expect(getByTestId('defaultSideNav')).toBeInTheDocument();

    const triggerBtn = getByTestId('BusinessNavMobile-TriggerLanguageSideNav');
    await userEvent.click(triggerBtn);
    expect(getByTestId('languageSelectorSideNav')).toBeInTheDocument();
  });

  it('should show the Company sidebar when clicking the Company management option', async function () {
    const { getByTestId } = render(<BBNavigationMenuMobile {...mockProps} />);

    await userEvent.click(getByTestId('BusinessNavMobile-toggleButton'));
    expect(getByTestId('defaultSideNav')).toBeInTheDocument();
    const triggerBtn = getByTestId('BusinessNavMobile-Company-management-TriggerSideNav');
    await userEvent.click(triggerBtn);
    expect(getByTestId('Company-management-Container')).toBeInTheDocument();
  });

  it('should show the About sidebar when clicking the About option', async function () {
    const { getByTestId } = render(<BBNavigationMenuMobile {...mockProps} />);

    await userEvent.click(getByTestId('burgerMenu'));
    expect(getByTestId('defaultSideNav')).toBeInTheDocument();
    const triggerBtn = getByTestId('BusinessNavMobile-About-Premier-Inn-TriggerSideNav');
    await userEvent.click(triggerBtn);
    expect(getByTestId('About-Premier-Inn-Container')).toBeInTheDocument();
  });

  it('should show the Business Account sidebar when clicking the Business Account option', async function () {
    const { getByTestId } = render(<BBNavigationMenuMobile {...mockProps} />);

    await userEvent.click(getByTestId('burgerMenu'));
    expect(getByTestId('defaultSideNav')).toBeInTheDocument();
    const triggerBtn = getByTestId('BusinessNavMobile-Business-Account-TriggerSideNav');
    await userEvent.click(triggerBtn);
    expect(getByTestId('Business-Account-Container')).toBeInTheDocument();
  });
});

import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import { CookiePoliciesLabels } from '../types';
import ManageCookies from './ManageCookies.component';

const defaultCookiePermissions = [
  {
    id: 0,
    name: 'permissionEssential',
    value: true,
  },
  {
    id: 1,
    name: 'permissionPerformance',
    value: false,
  },
  {
    id: 2,
    name: 'permissionExperience',
    value: false,
  },
  {
    id: 3,
    name: 'permissionMarketing',
    value: false,
  },
];

const props = {
  labels: {
    cookieConsent: {
      cookiePolicies: {
        brand: 'pi',
        introView: {
          acceptAllButtonText: 'Accept all cookies',
          description:
            'Collecting cookies helps us improve our website, keep everything secure and personalise your experience by tailoring content just for you. If you’re happy with this, you can ‘accept all cookies’, or to find out more, click ‘manage cookies’.',
          manageButtonText: 'Manage cookies',
          title: 'Cookies and how we use them',
        },
        manageView: {
          alwaysActiveText: 'Always Active',
          description:
            '<p>Choose the cookies that work for you. If you need more information, please see our cookies notice.</p>\n',
          saveSettingsButtonText: 'Confirm settings',
          title: 'Manage cookies',
          cookieGroup: [
            {
              cookieName: 'permissionEssential',
              description:
                '<p>Some cookies are essential – our website wouldn’t work without them! We collect them to keep our website secure and ensure that from browsing to booking, your online experience runs smoothly.</p>\n',
              isAlwaysActive: true,
              title: 'Essential',
              toggleLabel: 'Essentials are always active.',
            },
            {
              cookieName: 'permissionPerformance',
              description:
                '<p>These cookies help us understand user experiences. We’ll never use them to identify you personally – just to monitor how well our website is working and if there’s any room for improvement.</p>\n',
              isAlwaysActive: false,
              title: 'Performance (Recommended) ',
              toggleLabel:
                'Button to choose whether to allow or disallow us to collect performance cookies.',
            },
            {
              cookieName: 'permissionExperience',
              description:
                '<p>We use these cookies to personalise your experience – tailoring content throughout your visit. We also use these cookies to test new website features and improve functionality across our website.</p>\n',
              isAlwaysActive: false,
              title: 'Experience',
              toggleLabel:
                'Button to choose whether to allow or disallow us to collect experience cookies.',
            },
            {
              cookieName: 'permissionMarketing',
              description:
                '<p>These cookies allow us to tailor the advertising you receive from Premier Inn. Without these cookies you would still receive adverts – they would just be less relevant to you.</p>\n',
              isAlwaysActive: false,
              title: 'Marketing',
              toggleLabel:
                'choose the cookies that work for you. If you need more information, please see our cookies notice. ',
            },
          ],
        },
      },
    },
  } as CookiePoliciesLabels,
  cookiePermissions: defaultCookiePermissions,
  setCookiePolicies: jest.fn(),
  setCookiePermissions: jest.fn(),
  manageCookies: jest.fn(),
};

describe('<ManageCookie />', () => {
  it('should render the ManageCookieModal correctly', () => {
    const { getByTestId } = render(<ManageCookies {...props} />);
    expect(getByTestId('ManageCookieModal-Container')).toBeInTheDocument();
  });

  it('should render the ManageCookieModal description correctly', () => {
    const { getByTestId } = render(<ManageCookies {...props} />);
    expect(getByTestId('ManageCookieModal-Description')).toBeInTheDocument();
  });

  it('should cancel ManageCookieModal when pressing Confirm settings btn', async () => {
    const { getByTestId } = render(<ManageCookies {...props} />);
    const confirmCookiesBtn = getByTestId('ManageCookieModal-Confirm settings');
    fireEvent.click(confirmCookiesBtn);
    expect(props.setCookiePolicies).toBeCalledTimes(1);
  });
  it('should make the permission switch visible and it should be enabled if the users clicked on it', () => {
    const { getAllByRole } = render(<ManageCookies {...props} />);

    const switcher = getAllByRole('checkbox')[0];
    expect(switcher).not.toBeChecked();

    fireEvent.click(switcher);

    expect(switcher).toBeChecked();
  });
});

import '@testing-library/jest-dom';
import type { PrivacyPolicy } from '@whitbread-eos/api';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import DataSecuritySection from './DataSecuritySection.component';

const privacyPolicyMockData: PrivacyPolicy = {
  name: 'title',
  description: '<p>description</p>',
  linkLabel: 'link label',
  linkSrc: '/path/example',
  moreInfoLabel: 'Find out more',
  moreInfo: [
    {
      image: 'imagePath',
      description: 'description',
    },
  ],
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: (html: string) => html,
}));

describe('DataSecuritySection', () => {
  it('should render the component without the extended section', () => {
    const { queryByTestId } = render(<DataSecuritySection privacyPolicy={privacyPolicyMockData} />);
    expect(queryByTestId('PrivacyPolicy-Expanded-Wrapper')).toBeFalsy();
  });

  it('should render a <DataSecuritySection/> and expanded container on button click', async () => {
    const { getByTestId, queryByTestId } = render(
      <DataSecuritySection privacyPolicy={privacyPolicyMockData} />
    );
    fireEvent.click(getByTestId('PrivacyPolicy-ExpandButton'));
    await waitFor(() => {
      expect(queryByTestId('PrivacyPolicy-Expanded-Wrapper')).toBeTruthy();
    });
  });

  it('should render the component content correctly', async () => {
    const { getByTestId } = render(<DataSecuritySection privacyPolicy={privacyPolicyMockData} />);
    fireEvent.click(getByTestId('PrivacyPolicy-ExpandButton'));
    await waitFor(() => {
      expect(getByTestId('PrivacyPolicy-Expanded-Item-Wrapper').textContent).toBe('description');
      expect(getByTestId('PrivacyPolicy-Main-Description').textContent).toBe(
        privacyPolicyMockData.description
      );
    });
  });

  it('should render empty string if the description did not come', async () => {
    const { getByTestId } = render(
      <DataSecuritySection
        privacyPolicy={{
          name: 'title',
          linkLabel: 'link label',
          linkSrc: '/path/example',
          moreInfoLabel: 'Find out more',
          moreInfo: [
            {
              image: 'imagePath',
              description: 'description',
            },
          ],
        }}
      />
    );
    fireEvent.click(getByTestId('PrivacyPolicy-ExpandButton'));
    await waitFor(() => {
      expect(getByTestId('PrivacyPolicy-Main-Description').textContent).toBe('');
    });
  });
});

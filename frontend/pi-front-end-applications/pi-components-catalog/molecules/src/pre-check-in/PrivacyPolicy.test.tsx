import React from 'react';

import { render } from '../utils/test-utils';
import PrivacyPolicy from './PrivacyPolicy';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  withTranslation: () => (Component: any) => Component,
  useTranslation: () => ({ t: (key: string) => key }),
}));

describe('Should render PrivacyPolicy component', () => {
  test('Renders the Privacy Policy component', async () => {
    const { getByTestId } = render(<PrivacyPolicy />);
    expect(getByTestId('privacy-policy-label')).toBeInTheDocument();
  });
});

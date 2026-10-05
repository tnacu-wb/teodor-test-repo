import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { DataPolicySection } from './data-policy-section';

jest.mock('@whitbread-eos/utils/server', () => ({
  formatIBAssetsUrl: jest.fn((url) => url),
  useTranslation: jest.fn(() => ({
    t: (key: string) => key,
  })),
  cn: jest.fn(),
}));

describe('DataPolicySection', () => {
  const mockIcons = {
    'icon.notification.info': '/mock-icon-info.png',
  };

  it('renders DataPolicySection', () => {
    render(<DataPolicySection icons={mockIcons} />);

    expect(screen.getByTestId('Data-Policy-Container')).toBeInTheDocument();
  });
});

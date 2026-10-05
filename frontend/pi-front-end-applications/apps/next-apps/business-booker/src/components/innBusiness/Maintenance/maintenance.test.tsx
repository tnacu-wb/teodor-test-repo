import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';
import React from 'react';

import { Maintenance } from './maintenance';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getPayApplicationLabels: () => ({}),
    getInnBusinessHeaderLabels: () => ({}),
    getCommonIcons: () => ({}),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
  };
});

jest.mock('./maintenance-content', () => {
  return {
    MaintenanceContent: ({ children }: { children: React.ReactNode }) => (
      <div data-testid="Maintenance-Content">{children}</div>
    ),
  };
});

describe('Maintenance Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders', async () => {
    render(<Maintenance />);

    await waitFor(() => {
      expect(screen.getByTestId('Maintenance-Content')).toBeInTheDocument();
    });
  });
});

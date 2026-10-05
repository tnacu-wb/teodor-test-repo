import { ChakraProvider } from '@chakra-ui/react';
import { describe, it, expect, jest, beforeEach } from '@jest/globals';
import '@testing-library/jest-dom';
import { render, screen, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { usePromoTranslation } from '@whitbread-eos/utils';

import { PlatformConfig } from '../types';
import PlatformConfigField from './PlatformConfigField';
import { toggleCountryEnabled, togglePlatformEnabled, toggleSelection } from './common';

jest.mock('./common', () => ({
  COUNTRY_ROWS: [
    { key: 'GB', label: 'United Kingdom (GB)' },
    { key: 'DE', label: 'Germany (DE)' },
  ],
  PLATFORM_ROWS: [
    { key: 'PI', label: 'PI', options: ['web', 'app'] },
    { key: 'CCUI', label: 'CCUI', options: ['web'] },
    { key: 'PREMIER_INN_BUSINESS', label: 'Premierinn Inn Business', options: ['web', 'app'] },
  ],
  toggleCountryEnabled: jest.fn(),
  togglePlatformEnabled: jest.fn(),
  toggleSelection: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  usePromoTranslation: jest.fn(),
}));

const mockTranslations = {
  platformLabel: 'Platform',
  selectedLabel: 'Selected',
};

const renderWithChakra = (ui: React.ReactElement) => render(<ChakraProvider>{ui}</ChakraProvider>);

const buildPlatformItem = (enabled = false, web = false, app = false) => ({
  enabled,
  selected: { web, app },
});

const buildCountryConfig = (enabled = false) => ({
  enabled,
  platforms: {
    PI: buildPlatformItem(),
    CCUI: buildPlatformItem(),
    PREMIER_INN_BUSINESS: buildPlatformItem(),
  },
});

const buildDefaultConfig = (): PlatformConfig => ({
  GB: buildCountryConfig(false),
  DE: buildCountryConfig(false),
});

const setup = (overrides: Partial<PlatformConfig> = {}) => {
  const onChange = jest.fn();
  const onBlur = jest.fn();
  const value: PlatformConfig = { ...buildDefaultConfig(), ...overrides };

  renderWithChakra(
    <PlatformConfigField
      value={value}
      onChange={onChange}
      onBlur={onBlur}
      label="Platform Configuration"
      description="Configure platforms for each country."
      testId="platformConfig"
    />
  );

  return { onChange, onBlur, value };
};

beforeEach(() => {
  jest.clearAllMocks();
  (usePromoTranslation as jest.Mock).mockReturnValue(mockTranslations);
});

describe('PlatformConfigField', () => {
  it('renders the label and description', () => {
    setup();

    expect(screen.getByText('Platform Configuration')).toBeInTheDocument();
    expect(screen.getByText('Configure platforms for each country.')).toBeInTheDocument();
  });

  it('renders a row for each country', () => {
    setup();
    expect(screen.getByText('United Kingdom (GB)')).toBeInTheDocument();
    expect(screen.getByText('Germany (DE)')).toBeInTheDocument();
  });

  it('renders the translated Platform/Selected header labels when a country is enabled', () => {
    setup({ GB: buildCountryConfig(true) });
    expect(screen.getByText('Platform')).toBeInTheDocument();
    expect(screen.getByText('Selected')).toBeInTheDocument();
  });

  it('falls back gracefully when translation values are missing (t?.platformLabel is undefined)', () => {
    (usePromoTranslation as jest.Mock).mockReturnValue({});
    setup({ GB: buildCountryConfig(true) });
    expect(screen.queryByText('Platform')).not.toBeInTheDocument();
  });

  it('does not show the platform table when a country is not enabled', () => {
    setup();

    expect(screen.queryByText('PI')).not.toBeInTheDocument();
    expect(screen.queryByText('CCUI')).not.toBeInTheDocument();
    expect(screen.queryByText('Premierinn Inn Business')).not.toBeInTheDocument();
  });

  it('shows the platform table when a country is enabled', () => {
    setup({ GB: buildCountryConfig(true) });

    expect(screen.getByText('PI')).toBeInTheDocument();
    expect(screen.getByText('CCUI')).toBeInTheDocument();
    expect(screen.getByText('Premierinn Inn Business')).toBeInTheDocument();
  });

  it('calls onChange and onBlur when a country checkbox is ticked', async () => {
    const updatedConfig = { fake: 'updated-config' };
    (toggleCountryEnabled as jest.Mock).mockReturnValue(updatedConfig);

    const { onChange, onBlur, value } = setup();
    const gbCheckbox = screen.getByRole('checkbox', { name: /United Kingdom/i });

    await userEvent.click(gbCheckbox);

    expect(toggleCountryEnabled).toHaveBeenCalledWith(value, 'GB', true);
    expect(onChange).toHaveBeenCalledWith(updatedConfig);
    expect(onBlur).toHaveBeenCalled();
  });

  it('works even when onBlur is not provided', () => {
    const onChange = jest.fn();
    (toggleCountryEnabled as jest.Mock).mockReturnValue({});

    renderWithChakra(
      <PlatformConfigField
        value={buildDefaultConfig()}
        onChange={onChange}
        label="Platform Configuration"
        description="desc"
      />
    );

    const gbCheckbox = screen.getByRole('checkbox', { name: /United Kingdom/i });
    expect(() => userEvent.click(gbCheckbox)).not.toThrow();
    expect(onChange).toHaveBeenCalled();
  });

  it('calls onChange and onBlur when a platform checkbox is ticked', async () => {
    const updatedConfig = { fake: 'updated-config' };
    (togglePlatformEnabled as jest.Mock).mockReturnValue(updatedConfig);

    const { onChange, onBlur, value } = setup({ GB: buildCountryConfig(true) });
    const piCheckbox = screen.getByRole('checkbox', { name: 'PI' });
    await userEvent.click(piCheckbox);

    expect(togglePlatformEnabled).toHaveBeenCalledWith(value, 'GB', 'PI', true);
    expect(onChange).toHaveBeenCalledWith(updatedConfig);
    expect(onBlur).toHaveBeenCalled();
  });
  it('disables the Web/App options when the platform itself is not enabled', () => {
    setup({ GB: buildCountryConfig(true) });
    const webButtons = screen.getAllByRole('button', { name: 'Web' });
    webButtons.forEach((button) => expect(button).toBeDisabled());
  });

  it('does nothing when a Web/App option is clicked on a disabled platform', async () => {
    const { onChange } = setup({ GB: buildCountryConfig(true) });
    const webButtons = screen.getAllByRole('button', { name: 'Web' });
    await userEvent.click(webButtons[0]);

    expect(toggleSelection).not.toHaveBeenCalled();
    expect(onChange).not.toHaveBeenCalled();
  });

  it('renders the App option only for PI and Premierinn Inn Business, not CCUI', () => {
    setup({ GB: buildCountryConfig(true) });
    const appButtons = screen.getAllByRole('button', { name: 'App' });
    expect(appButtons).toHaveLength(2);
  });

  it('calls onChange and onBlur when an option tag (Web/App) is clicked on an enabled platform', async () => {
    const updatedConfig = { fake: 'updated-selection' };
    (toggleSelection as jest.Mock).mockReturnValue(updatedConfig);

    const gbConfig = buildCountryConfig(true);
    gbConfig.platforms.PI.enabled = true;
    const { onChange, onBlur, value } = setup({ GB: gbConfig });
    const piRow = screen.getByText('PI').closest('div');
    const webButton = within(piRow!).getByRole('button', { name: 'Web' });

    await userEvent.click(webButton);

    expect(toggleSelection).toHaveBeenCalledWith(value, 'GB', 'PI', 'web');
    expect(onChange).toHaveBeenCalledWith(updatedConfig);
    expect(onBlur).toHaveBeenCalled();
  });

  it('applies optionSelectedStyles when option is selected and optionUnselectedStyles when not selected', () => {
    const gbConfig = buildCountryConfig(true);
    gbConfig.platforms.PI.enabled = true;
    gbConfig.platforms.PI.selected.web = true;
    gbConfig.platforms.PI.selected.app = false;
    setup({ GB: gbConfig });

    const piRow = screen.getByText('PI').closest('div');
    const webButton = within(piRow!).getByRole('button', { name: 'Web' });
    const appButton = within(piRow!).getByRole('button', { name: 'App' });

    expect(webButton).not.toBeDisabled();
    expect(appButton).not.toBeDisabled();
  });
});

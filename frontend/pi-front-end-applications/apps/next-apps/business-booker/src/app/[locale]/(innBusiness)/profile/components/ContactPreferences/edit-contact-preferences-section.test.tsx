import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import * as serverUtils from '@whitbread-eos/utils/server';

import { EditContactPreferencesSection } from './edit-contact-preferences-section';

jest.mock('@whitbread-eos/utils/server');

describe('EditContactPreferencesSection', () => {
  const mockTranslations = {
    t: jest.fn((key) => {
      if (key === 'marketingpreferences.description') {
        return 'This is the description content';
      }
      return key;
    }),
    translations: {},
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(serverUtils.useTranslation).mockReturnValue(mockTranslations);
  });

  it('renders the section with title, description and edit button', () => {
    // eslint-disable-next-line no-empty-pattern
    const {} = render(<EditContactPreferencesSection locale="en-gb" />);

    expect(screen.getByTestId('EditContactPreferencesSection')).toBeInTheDocument();

    const title = screen.getByTestId('EditContactPreferencesSection-Title');
    expect(title).toBeInTheDocument();
    expect(title).toHaveTextContent('marketingpreferences.title');

    const editLink = screen.getByTestId('EditContactPreferencesSection-EditLink');
    expect(editLink).toBeInTheDocument();
    expect(editLink).toHaveAttribute('href', '/en-gb/profile/contact-preferences');

    const editButton = screen.getByTestId('EditContactPreferencesSection-EditButton');
    expect(editButton).toBeInTheDocument();
    expect(editButton).toHaveTextContent('marketingpreferences.button.edit');
  });

  it('renders with different locale', () => {
    render(<EditContactPreferencesSection locale="de-de" />);
    const editLink = screen.getByTestId('EditContactPreferencesSection-EditLink');
    expect(editLink).toHaveAttribute('href', '/de-de/profile/contact-preferences');
  });
});

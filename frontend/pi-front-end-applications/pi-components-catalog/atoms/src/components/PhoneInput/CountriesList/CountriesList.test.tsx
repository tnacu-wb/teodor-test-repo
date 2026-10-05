import React, { ComponentProps } from 'react';

import { render, screen, userEvent, waitFor } from '../../../utils/test-utils';
import CountriesList from './CountriesList.component';
import mockedCountries from './mocks/countries.json';

const randomStringValue = () => Math.random().toString(36).slice(2);

const defaultProps: ComponentProps<typeof CountriesList> = {
  formatAssetsUrl: (url) => url,
  options: mockedCountries,
  placeholder: 'search-placeholder',
  onChange: jest.fn(),
};

describe('PhoneInput/CountriesList', () => {
  it('should match snapshot', async () => {
    const { container } = render(<CountriesList {...defaultProps} />);
    expect(container).toMatchSnapshot();
  });

  it('should merge custom classNames to the targeted element', async () => {
    const className = randomStringValue();
    const { container } = render(<CountriesList {...defaultProps} className={className} />);
    expect(container.firstChild).toHaveClass(className);
  });

  it('should spread properties to the targeted element', async () => {
    const testProp = 'data-t-props-spread';
    const value = randomStringValue();
    const { container } = render(<CountriesList {...defaultProps} {...{ [testProp]: value }} />);
    expect(container.firstChild).toHaveAttribute(testProp, value);
  });

  it('should set the search input placeholder', () => {
    render(<CountriesList {...defaultProps} />);
    expect(screen.getByPlaceholderText(defaultProps.placeholder as string)).toBeInTheDocument();
  });

  it('should render all the options', () => {
    render(<CountriesList {...defaultProps} />);
    expect(screen.getAllByRole('option')).toHaveLength(defaultProps.options.length);
  });

  it('should filter the options list based on search input value', () => {
    render(<CountriesList {...defaultProps} />);
    expect(mockedCountries.length).toBeGreaterThan(1);
    userEvent.type(screen.getByRole('textbox'), mockedCountries[0].countryName);
    expect(screen.getAllByRole('option')).toHaveLength(1);
    expect(screen.getByText(mockedCountries[0].countryName)).toBeVisible();
  });

  it('should call onChange with the item on item click', () => {
    render(<CountriesList {...defaultProps} />);
    const country = mockedCountries[2];
    userEvent.click(screen.getByText(country.countryName));
    expect(defaultProps.onChange).toHaveBeenCalledWith(country);
  });

  describe('Keyboard Navigation', () => {
    it('should auto-focus first item when list receives focus', async () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');

      listbox.focus();

      await waitFor(() => {
        const firstOption = screen.getByRole('option', {
          name: new RegExp(mockedCountries[0].countryName),
        });
        expect(firstOption).toHaveAttribute('aria-selected', 'true');
        expect(listbox).toHaveAttribute(
          'aria-activedescendant',
          `country-option-${mockedCountries[0].countryCode}`
        );
      });
    });

    it('should navigate to next item on ArrowDown', () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');

      listbox.focus();
      userEvent.keyboard('{ArrowDown}');

      const secondOption = screen.getByRole('option', {
        name: new RegExp(mockedCountries[1].countryName),
      });
      expect(secondOption).toHaveAttribute('aria-selected', 'true');
      expect(listbox).toHaveAttribute(
        'aria-activedescendant',
        `country-option-${mockedCountries[1].countryCode}`
      );
    });

    it('should navigate to previous item on ArrowUp', () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');

      listbox.focus();
      userEvent.keyboard('{ArrowDown}{ArrowDown}');
      userEvent.keyboard('{ArrowUp}');

      const secondOption = screen.getByRole('option', {
        name: new RegExp(mockedCountries[1].countryName),
      });
      expect(secondOption).toHaveAttribute('aria-selected', 'true');
    });

    it('should wrap to first item when ArrowDown on last item', () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');
      const lastIndex = mockedCountries.length - 1;

      listbox.focus();
      // Navigate to last item
      for (let i = 0; i < lastIndex; i++) {
        userEvent.keyboard('{ArrowDown}');
      }
      // ArrowDown again should wrap to first
      userEvent.keyboard('{ArrowDown}');

      const firstOption = screen.getByRole('option', {
        name: new RegExp(mockedCountries[0].countryName),
      });
      expect(firstOption).toHaveAttribute('aria-selected', 'true');
    });

    it('should wrap to last item when ArrowUp on first item', () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');
      const lastIndex = mockedCountries.length - 1;

      listbox.focus();
      userEvent.keyboard('{ArrowUp}');

      const lastOption = screen.getByRole('option', {
        name: new RegExp(mockedCountries[lastIndex].countryName),
      });
      expect(lastOption).toHaveAttribute('aria-selected', 'true');
    });

    it('should select focused item on Enter key', () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');

      listbox.focus();
      userEvent.keyboard('{ArrowDown}');
      userEvent.keyboard('{Enter}');

      expect(defaultProps.onChange).toHaveBeenCalledWith(mockedCountries[1]);
    });

    it('should call onClose on Escape key', () => {
      const onClose = jest.fn();
      render(<CountriesList {...defaultProps} onClose={onClose} />);
      const listbox = screen.getByRole('listbox');

      listbox.focus();
      userEvent.keyboard('{Escape}');

      expect(onClose).toHaveBeenCalledTimes(1);
    });

    it('should reset focus to first item when filtering', async () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');
      const searchInput = screen.getByRole('textbox');

      listbox.focus();
      userEvent.keyboard('{ArrowDown}{ArrowDown}');

      // Filtering should reset focus
      userEvent.type(searchInput, mockedCountries[3].countryName);

      // Focus list again after filtering
      listbox.focus();

      await waitFor(() => {
        const firstFilteredOption = screen.getByRole('option', {
          name: new RegExp(mockedCountries[3].countryName),
        });
        expect(firstFilteredOption).toHaveAttribute('aria-selected', 'true');
      });
    });

    it('should update focused item on mouse enter', async () => {
      render(<CountriesList {...defaultProps} />);
      const listbox = screen.getByRole('listbox');

      listbox.focus();

      const thirdCountry = mockedCountries[2];
      const thirdOption = screen.getByRole('option', {
        name: new RegExp(thirdCountry.countryName),
      });
      userEvent.hover(thirdOption);

      await waitFor(() => {
        expect(thirdOption).toHaveAttribute('aria-selected', 'true');
        expect(listbox).toHaveAttribute(
          'aria-activedescendant',
          `country-option-${thirdCountry.countryCode}`
        );
      });
    });
  });

  describe('Search Input Auto-Focus', () => {
    it('should auto-focus search input when component mounts', () => {
      render(<CountriesList {...defaultProps} />);
      const searchInput = screen.getByRole('textbox');

      expect(searchInput).toHaveFocus();
    });

    it('should support ArrowDown navigation from search input', async () => {
      render(<CountriesList {...defaultProps} />);
      const searchInput = screen.getByRole('textbox');
      const listbox = screen.getByRole('listbox');

      expect(searchInput).toHaveFocus();

      // First item should be highlighted by default
      await waitFor(() => {
        const firstOption = screen.getByRole('option', {
          name: new RegExp(mockedCountries[0].countryName),
        });
        expect(firstOption).toHaveAttribute('aria-selected', 'true');
      });

      // ArrowDown should move to second item
      userEvent.keyboard('{ArrowDown}');

      await waitFor(() => {
        const secondOption = screen.getByRole('option', {
          name: new RegExp(mockedCountries[1].countryName),
        });
        expect(secondOption).toHaveAttribute('aria-selected', 'true');
        expect(listbox).toHaveAttribute(
          'aria-activedescendant',
          `country-option-${mockedCountries[1].countryCode}`
        );
      });
    });

    it('should support ArrowUp navigation from search input', async () => {
      render(<CountriesList {...defaultProps} />);
      const searchInput = screen.getByRole('textbox');
      const listbox = screen.getByRole('listbox');
      const lastIndex = mockedCountries.length - 1;

      expect(searchInput).toHaveFocus();

      // ArrowUp from first should wrap to last
      userEvent.keyboard('{ArrowUp}');

      await waitFor(() => {
        const lastOption = screen.getByRole('option', {
          name: new RegExp(mockedCountries[lastIndex].countryName),
        });
        expect(lastOption).toHaveAttribute('aria-selected', 'true');
        expect(listbox).toHaveAttribute(
          'aria-activedescendant',
          `country-option-${mockedCountries[lastIndex].countryCode}`
        );
      });
    });

    it('should support Enter key selection from search input', async () => {
      render(<CountriesList {...defaultProps} />);
      const searchInput = screen.getByRole('textbox');

      expect(searchInput).toHaveFocus();

      // Navigate to second item
      userEvent.keyboard('{ArrowDown}');

      await waitFor(() => {
        const secondOption = screen.getByRole('option', {
          name: new RegExp(mockedCountries[1].countryName),
        });
        expect(secondOption).toHaveAttribute('aria-selected', 'true');
      });

      // Press Enter
      userEvent.keyboard('{Enter}');

      expect(defaultProps.onChange).toHaveBeenCalledWith(mockedCountries[1]);
    });

    it('should support Escape key from search input', () => {
      const onClose = jest.fn();
      render(<CountriesList {...defaultProps} onClose={onClose} />);
      const searchInput = screen.getByRole('textbox');

      expect(searchInput).toHaveFocus();

      userEvent.keyboard('{Escape}');

      expect(onClose).toHaveBeenCalledTimes(1);
    });

    it('should allow typing in search input without triggering navigation', async () => {
      render(<CountriesList {...defaultProps} />);
      const searchInput = screen.getByRole('textbox');

      expect(searchInput).toHaveFocus();

      // Type country name (not arrow keys)
      userEvent.type(searchInput, mockedCountries[2].countryName);

      // Should filter to that country
      await waitFor(() => {
        expect(screen.getAllByRole('option')).toHaveLength(1);
        expect(screen.getByText(mockedCountries[2].countryName)).toBeVisible();
      });
    });
  });
});

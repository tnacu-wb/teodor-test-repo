import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import React from 'react';

import SecondLevelNav from './SecondLevelNav.component';

const mockIcons = { item1: 'icon1', item2: 'icon2', item3: 'icon3' };
const mockLinks = [
  {
    key: 'one',
    label: 'One',
    isActive: false,
    href: '/one',
    condition: true,
  },
  {
    key: 'two',
    label: 'Two',
    isActive: false,
    href: '/two',
    condition: true,
  },
  {
    key: 'three',
    label: 'Three',
    isActive: false,
    href: '/three',
    condition: true,
  },
];
const mockOnLinkClick = jest.fn();
const mockSecondLevelLinks = [
  { type: 'link', key: 'item1', label: 'Item 1', isActive: false, href: '/item1', condition: true },
  { type: 'link', key: 'item2', label: 'Item 2', isActive: true, href: '/item2', condition: false },
  { type: 'link', key: 'item3', label: 'Item 3', isActive: false, href: '/item3', condition: true },
];

jest.mock('next/navigation', () => ({
  usePathname: jest.fn(() => '/item1'),
}));

const mockProps = {
  secondLevelLinks: mockSecondLevelLinks,
  icons: mockIcons,
  onLinkClick: mockOnLinkClick,
};

describe('SecondLevelNav Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.resetModules();
  });
  it('renders the navigation items that have condition true', () => {
    render(<SecondLevelNav {...mockProps} />);
    expect(screen.getByText(mockSecondLevelLinks[0].label)).toBeInTheDocument();
    expect(
      screen.queryByText(
        (content, element) =>
          content === mockSecondLevelLinks[1].label &&
          element?.textContent === mockSecondLevelLinks[1].label
      )
    ).not.toBeInTheDocument();
    expect(screen.getByText(mockSecondLevelLinks[2].label)).toBeInTheDocument();
  });

  it('calls the onClick handler when an item is clicked', () => {
    render(<SecondLevelNav {...mockProps} />);
    const firstItem = screen.getByText(mockSecondLevelLinks[0].label);
    fireEvent.click(firstItem);
    expect(mockOnLinkClick).toHaveBeenCalled();
  });

  it('does not apply active class to non-active items', () => {
    render(<SecondLevelNav {...mockProps} />);
    // item3 as item2 has condition false and should not be rendered, so item3 is the next non-active item
    const nonActiveItem = screen.getByText(mockSecondLevelLinks[2].label);
    expect(nonActiveItem).not.toHaveClass('text-primaryColor');
  });

  it('renders empty state when no items are provided', () => {
    mockProps.secondLevelLinks = [];
    render(<SecondLevelNav {...mockProps} />);
    expect(screen.getByTestId('SecondLevelNav-container')).toBeEmptyDOMElement();
  });

  it('marks the link active when its href has a query string matching the current pathname', () => {
    const linksWithQuery = [
      {
        type: 'link',
        key: 'item1',
        label: 'Item 1',
        isActive: false,
        href: '/item1?account=abc-123',
        condition: true,
      },
      {
        type: 'link',
        key: 'item3',
        label: 'Item 3',
        isActive: false,
        href: '/item3',
        condition: true,
      },
    ];
    render(<SecondLevelNav {...mockProps} secondLevelLinks={linksWithQuery} />);
    expect(screen.getByText('Item 1')).toHaveClass('text-primaryColor');
    expect(screen.getByText('Item 3')).not.toHaveClass('text-primaryColor');
  });

  it('should blur focused nav item on popstate (back/forward navigation)', () => {
    const { getByText } = render(
      <SecondLevelNav secondLevelLinks={mockLinks} icons={mockIcons} autoFocusFirst />
    );
    const firstLink = getByText('One').closest('a');
    const blurSpy = jest.spyOn(firstLink as HTMLElement, 'blur');
    (firstLink as HTMLElement).focus();
    expect(document.activeElement).toBe(firstLink);
    window.dispatchEvent(new PopStateEvent('popstate'));
    expect(blurSpy).toHaveBeenCalled();
    blurSpy.mockRestore();
  });
});

describe('SecondLevelNav handleKeyDown', () => {
  it('should handle ArrowDown, ArrowUp, Escape, and ArrowLeft keys', () => {
    const onCloseSecondLevelNav = jest.fn();
    const onLeftArrowBack = jest.fn();

    const { getByTestId, getByText } = render(
      <SecondLevelNav
        secondLevelLinks={mockLinks}
        icons={mockIcons}
        onCloseSecondLevelNav={onCloseSecondLevelNav}
        onLeftArrowBack={onLeftArrowBack}
        autoFocusFirst
      />
    );
    const container = getByTestId('SecondLevelNav-container');
    const firstLink = getByText('One').closest('a');
    const secondLink = getByText('Two').closest('a');
    const thirdLink = getByText('Three').closest('a');

    // Focus the first link
    (firstLink as HTMLElement | null)?.focus();

    // ArrowDown should move focus to second link
    fireEvent.keyDown(container, { key: 'ArrowDown' });
    expect(document.activeElement).toBe(secondLink);

    // ArrowDown again should move focus to third link
    fireEvent.keyDown(container, { key: 'ArrowDown' });
    expect(document.activeElement).toBe(thirdLink);

    // ArrowDown again should wrap to first link
    fireEvent.keyDown(container, { key: 'ArrowDown' });
    expect(document.activeElement).toBe(firstLink);

    // ArrowUp should wrap to third link
    fireEvent.keyDown(container, { key: 'ArrowUp' });
    expect(document.activeElement).toBe(thirdLink);

    // ArrowLeft should call onLeftArrowBack
    fireEvent.keyDown(container, { key: 'ArrowLeft' });
    expect(onLeftArrowBack).toHaveBeenCalled();

    // Escape should call onCloseSecondLevelNav
    fireEvent.keyDown(container, { key: 'Escape' });
    expect(onCloseSecondLevelNav).toHaveBeenCalled();
  });

  it('should activate the focused link when Space is pressed', () => {
    const onLinkClick = jest.fn();
    const { getByTestId, getByText } = render(
      <SecondLevelNav
        secondLevelLinks={mockLinks}
        icons={mockIcons}
        onLinkClick={onLinkClick}
        autoFocusFirst
      />
    );
    const container = getByTestId('SecondLevelNav-container');
    const firstLink = getByText('One').closest('a');
    (firstLink as HTMLElement | null)?.focus();

    // Press Space key
    fireEvent.keyDown(container, { key: ' ' });
    expect(onLinkClick).toHaveBeenCalled();
  });
});

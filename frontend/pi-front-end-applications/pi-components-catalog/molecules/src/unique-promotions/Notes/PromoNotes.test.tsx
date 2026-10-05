import { render, screen } from '@testing-library/react';
import React from 'react';

import PromoNotes from './PromoNotes';

jest.mock('@whitbread-eos/utils', () => ({
  usePromoTranslation: () => ({
    notesTitle: 'Important Notes',
  }),
}));

describe('PromoNotes Component', () => {
  const mockNotes = ['Note line 1', 'Note line 2', 'Note line 3'];

  it('renders the title and all notes correctly with default props', () => {
    render(<PromoNotes notes={mockNotes} />);
    expect(screen.getByText('Important Notes')).toBeInTheDocument();
    mockNotes.forEach((note) => {
      expect(screen.getByText(note)).toBeInTheDocument();
    });

    const listItems = screen.getAllByRole('listitem');
    expect(listItems).toHaveLength(mockNotes.length);
  });

  it('applies background: "none" when isBackground is explicitly set to false', () => {
    const { container } = render(<PromoNotes isBackground={false} notes={mockNotes} />);
    const wrapperBox = container.firstChild as HTMLElement;
    expect(wrapperBox).toHaveStyle('background: none');
  });

  it('applies the default background when isBackground is set to true', () => {
    const { container } = render(<PromoNotes isBackground={true} notes={mockNotes} />);
    const wrapperBox = container.firstChild as HTMLElement;
    expect(wrapperBox).toHaveStyle('background: #EAF2F3');
  });

  it('handles empty notes array without throwing an error', () => {
    render(<PromoNotes notes={[]} />);
    expect(screen.getByText('Important Notes')).toBeInTheDocument();
    expect(screen.queryByRole('listitem')).not.toBeInTheDocument();
  });

  it('handles null or undefined notes gracefully (testing notes optional chaining branch)', () => {
    // @ts-expect-error Testing runtime boundary check for undefined notes
    const { rerender } = render(<PromoNotes notes={undefined} />);
    expect(screen.queryByRole('listitem')).not.toBeInTheDocument();
    // @ts-expect-error Testing runtime boundary check for null notes
    rerender(<PromoNotes notes={null} />);
    expect(screen.queryByRole('listitem')).not.toBeInTheDocument();
  });
});

import { render, screen, fireEvent } from '@testing-library/react';

import { RadioCard } from './RadioCard.component';

describe('RadioCard', () => {
  const defaultProps = {
    header: <span>Header</span>,
    isChecked: false,
    value: 'option1',
    onClick: jest.fn(),
    children: <div>Content</div>,
  };

  it('renders the wrapper and header', () => {
    render(<RadioCard {...defaultProps} />);
    expect(screen.getByTestId('RadioCard-Wrapper')).toBeInTheDocument();
    expect(screen.getByText('Header')).toBeInTheDocument();
  });

  it('renders children content', () => {
    render(<RadioCard {...defaultProps} />);
    expect(screen.getByText('Content')).toBeInTheDocument();
  });

  it('renders Radio unchecked by default', () => {
    render(<RadioCard {...defaultProps} />);
    const radio = screen.getByRole('radio');
    expect(radio).not.toBeChecked();
  });

  it('renders Radio checked when isChecked is true', () => {
    render(<RadioCard {...defaultProps} isChecked={true} />);
    const radio = screen.getByRole('radio');
    expect(radio).toBeChecked();
  });

  it('calls onClick when radio is clicked', () => {
    render(<RadioCard {...defaultProps} />);
    const radio = screen.getByRole('radio');
    fireEvent.click(radio);
    expect(defaultProps.onClick).toHaveBeenCalled();
  });

  it('applies correct styles when checked', () => {
    render(<RadioCard {...defaultProps} isChecked={true} />);
    const wrapper = screen.getByTestId('RadioCard-Wrapper');
    expect(wrapper).toHaveStyle('border-color: darkPurple');
  });

  it('applies correct styles when not checked', () => {
    render(<RadioCard {...defaultProps} isChecked={false} ref={null} />);
    const wrapper = screen.getByTestId('RadioCard-Wrapper');
    expect(wrapper).toHaveStyle('border-color: lightGrey3');
  });
});

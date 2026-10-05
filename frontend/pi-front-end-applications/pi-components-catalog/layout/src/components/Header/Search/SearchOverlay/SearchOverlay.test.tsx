import { render, fireEvent } from '@testing-library/react';

import SearchOverlay from './SearchOverlay.component';

describe('SearchOverlay component', () => {
  it('should not render when isVisible is false', () => {
    const onClose = jest.fn();
    const { queryByTestId } = render(<SearchOverlay isVisible={false} onClose={onClose} />);

    expect(queryByTestId('IB-Search-Overlay')).not.toBeInTheDocument();
  });

  it('should render when isVisible is true', () => {
    const onClose = jest.fn();
    const { getByTestId } = render(<SearchOverlay isVisible={true} onClose={onClose} />);

    expect(getByTestId('IB-Search-Overlay')).toBeInTheDocument();
  });

  it('should call onClose when overlay is clicked', () => {
    const onClose = jest.fn();
    const { getByTestId } = render(<SearchOverlay isVisible={true} onClose={onClose} />);

    fireEvent.click(getByTestId('IB-Search-Overlay'));

    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('should have aria-hidden attribute set to true', () => {
    const onClose = jest.fn();
    const { getByTestId } = render(<SearchOverlay isVisible={true} onClose={onClose} />);

    expect(getByTestId('IB-Search-Overlay')).toHaveAttribute('aria-hidden', 'true');
  });

  it('should call onClose when Escape key is pressed', () => {
    const onClose = jest.fn();
    render(<SearchOverlay isVisible={true} onClose={onClose} />);

    fireEvent.keyDown(document, { key: 'Escape' });

    expect(onClose).toHaveBeenCalledTimes(1);
  });

  it('should not call onClose when other keys are pressed', () => {
    const onClose = jest.fn();
    render(<SearchOverlay isVisible={true} onClose={onClose} />);

    fireEvent.keyDown(document, { key: 'Enter' });
    fireEvent.keyDown(document, { key: 'Tab' });

    expect(onClose).not.toHaveBeenCalled();
  });
});

import '@testing-library/jest-dom';

import { render, userEvent } from '../../../utils/test-utils';
import SidebarToggle from './SidebarToggle.component';

describe('SidebarToggle Component', () => {
  const getDefaultProps = () => ({
    collapseIcon: '/',
    expandIcon: '/',
    onToggle: jest.fn(),
    isCollapsed: false,
    className: '',
  });

  it('should render SidebarToggle expanded', () => {
    const { getByRole, getByTestId } = render(<SidebarToggle {...getDefaultProps()} />);

    const button = getByRole('button', { name: /collapse sidebar/i });
    expect(button).toBeInTheDocument();
    expect(button).toHaveAttribute('aria-expanded', 'true');
    expect(getByTestId('SidebarToggle-button')).toBeInTheDocument();
  });

  it('should render SidebarToggle collapsed', () => {
    const { getByRole } = render(<SidebarToggle {...getDefaultProps()} isCollapsed />);

    const button = getByRole('button', { name: /expand sidebar/i });
    expect(button).toBeInTheDocument();
    expect(button).toHaveAttribute('aria-expanded', 'false');
  });

  it('should call onToggle when button is clicked', async () => {
    const onToggle = jest.fn();
    const { getByRole } = render(<SidebarToggle {...getDefaultProps()} onToggle={onToggle} />);

    const button = getByRole('button', { name: /collapse sidebar/i });
    await userEvent.click(button);

    expect(onToggle).toHaveBeenCalledTimes(1);
  });

  it('should update aria-label based on collapsed state', () => {
    const { rerender, getByRole } = render(
      <SidebarToggle {...getDefaultProps()} isCollapsed={false} />
    );

    expect(getByRole('button')).toHaveAttribute('aria-label', 'Collapse sidebar');

    rerender(<SidebarToggle {...getDefaultProps()} isCollapsed={true} />);

    expect(getByRole('button')).toHaveAttribute('aria-label', 'Expand sidebar');
  });

  it('should apply custom className', () => {
    const customClass = 'custom-toggle-class';
    const { getByTestId } = render(
      <SidebarToggle {...getDefaultProps()} className={customClass} />
    );

    expect(getByTestId('SidebarToggle-button')).toHaveClass(customClass);
  });
});

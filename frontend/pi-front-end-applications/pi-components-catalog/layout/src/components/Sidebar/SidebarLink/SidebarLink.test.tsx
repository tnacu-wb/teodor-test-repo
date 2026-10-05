import { render } from '../../../utils/test-utils';
import SidebarLink from './SidebarLink.component';

const mockProps = {
  label: 'Home',
  icon: '/',
  isActive: false,
  isCollapsed: false,
  mainStyle: true,
  href: '/homepage',
  testId: 'sidebar-link-id',
};

jest.mock('next/navigation', () => ({
  usePathname: () => {
    return '/';
  },
}));

describe('SidebarLinks Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Home link', () => {
    const { getByText } = render(<SidebarLink {...mockProps} />);
    expect(getByText('Home')).toBeInTheDocument();
  });

  it('should render active component', () => {
    mockProps.isActive = true;
    const { getByText } = render(<SidebarLink {...mockProps} />);
    expect(getByText('Home')).toBeInTheDocument();
  });

  it('should render second style component component', () => {
    mockProps.mainStyle = false;
    const { getByText } = render(<SidebarLink {...mockProps} />);
    expect(getByText('Home')).toBeInTheDocument();
  });

  it('should call onKeyDown when key is pressed', () => {
    const onKeyDown = jest.fn();
    const { getByTestId } = render(<SidebarLink {...mockProps} onKeyDown={onKeyDown} />);
    const link = getByTestId('sidebar-link-id-Sidebar-Link');
    link.focus();
    link.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter', bubbles: true }));
    expect(onKeyDown).toHaveBeenCalled();
  });
});

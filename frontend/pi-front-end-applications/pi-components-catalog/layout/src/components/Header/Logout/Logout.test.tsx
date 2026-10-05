import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { PIB_MANUAL_LOGOUT_FLAG } from '@whitbread-eos/utils';

import Logout from './Logout.component';

// Add a mock for Menu to wrap DropdownMenuItem
jest.mock('@whitbread-eos/atoms/ui', () => ({
  Menu: (props: any) => <div data-testid="Menu">{props.children}</div>,
  DropdownMenuItem: (props: any) => (
    <div data-testid="DropdownMenuItem" {...props}>
      {props.children}
    </div>
  ),
}));

const mockRouterReplace = jest.fn();
const mockRouterRefresh = jest.fn();
jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    replace: mockRouterReplace,
    refresh: mockRouterRefresh,
  }),
}));

describe('Logout Component', () => {
  const mockOnLogoutClick = jest.fn();
  const mockSecureUrl = 'https://secure.example.com';

  const setup = (isAuthActive: boolean) => {
    render(
      <div>
        {/* Simulate Menu context */}
        <div data-testid="Menu">
          <Logout
            isAuthActive={isAuthActive}
            secureUrl={mockSecureUrl}
            asMenuItem={true}
            onLogoutClick={mockOnLogoutClick}
          />
        </div>
      </div>
    );
  };

  beforeEach(() => {
    jest.clearAllMocks();
    sessionStorage.clear();
  });

  it('should call onLogoutClick when the logout link is clicked', () => {
    setup(false);

    const logoutLink = screen.getByTestId('Account-Logout-Link');
    fireEvent.click(logoutLink);

    expect(mockOnLogoutClick).toHaveBeenCalledTimes(1);
    expect(sessionStorage.getItem(PIB_MANUAL_LOGOUT_FLAG)).toBeNull();
  });

  it('should not post a message to the iframe if isAuthActive is false', () => {
    setup(false);

    const logoutLink = screen.getByTestId('Account-Logout-Link');
    const postMessageSpy = jest.spyOn(window, 'postMessage');

    fireEvent.click(logoutLink);

    expect(postMessageSpy).not.toHaveBeenCalled();
  });

  it('should post a logout message to the iframe if isAuthActive is true', () => {
    setup(true);

    const iframe = document.createElement('iframe');
    iframe.id = 'authIframe';
    document.body.appendChild(iframe);

    const contentWindow = iframe.contentWindow as Window;
    const postMessageSpy = jest.spyOn(contentWindow, 'postMessage');

    const logoutLink = screen.getByTestId('Account-Logout-Link');
    fireEvent.click(logoutLink);

    expect(postMessageSpy).toHaveBeenCalledWith(
      JSON.stringify({ action: 'logout' }),
      mockSecureUrl
    );
    expect(sessionStorage.getItem(PIB_MANUAL_LOGOUT_FLAG)).toBe('true');

    document.body.removeChild(iframe);
  });

  it('renders the logout link inside DropdownMenuItem', () => {
    setup(false);
    const dropdown = screen.getByTestId('DropdownMenuItem');
    const logoutLink = screen.getByTestId('Account-Logout-Link');
    expect(dropdown).toContainElement(logoutLink);
  });
});

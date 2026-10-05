import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';

import { AuthIframe } from './AuthIframe.component';

const mockGetBrowserFtOverrides = jest.fn<Record<string, boolean>, []>();

jest.mock('@whitbread-eos/utils', () => ({
  getBrowserFtOverrides: () => mockGetBrowserFtOverrides(),
  getSecureTwoURL: () => 'https://secure2.dev.premierinn.digital',
}));

describe('AuthIframe', () => {
  beforeEach(() => {
    mockGetBrowserFtOverrides.mockReturnValue({});
  });

  it('renders the Secure2 login iframe when Auth0 is disabled and there is no override', () => {
    render(<AuthIframe country="gb" language="en" isAuth0Enabled={false} />);

    const iframe = screen.getByTitle('authIframe');
    expect(iframe).toBeInTheDocument();
    expect(iframe).toHaveAttribute(
      'src',
      'https://secure2.dev.premierinn.digital/gb/en/common/login.html'
    );
  });

  it('does not render the iframe when Auth0 is enabled and there is no override', () => {
    render(<AuthIframe country="gb" language="en" isAuth0Enabled={true} />);

    expect(screen.queryByTitle('authIframe')).not.toBeInTheDocument();
  });

  it('hides the iframe when the ftOverride cookie forces Auth0 on, even though the page prop says off', async () => {
    mockGetBrowserFtOverrides.mockReturnValue({ release_pi_auth0_login: true });

    render(<AuthIframe country="gb" language="en" isAuth0Enabled={false} />);

    await waitFor(() => {
      expect(screen.queryByTitle('authIframe')).not.toBeInTheDocument();
    });
  });

  it('shows the iframe when the ftOverride cookie forces Auth0 off, even though the page prop says on', async () => {
    mockGetBrowserFtOverrides.mockReturnValue({ release_pi_auth0_login: false });

    render(<AuthIframe country="gb" language="en" isAuth0Enabled={true} />);

    await waitFor(() => {
      expect(screen.getByTitle('authIframe')).toBeInTheDocument();
    });
  });

  it('re-resolves from the new isAuth0Enabled prop (not a stale context value) on route transitions', () => {
    const { rerender } = render(<AuthIframe country="gb" language="en" isAuth0Enabled={false} />);
    expect(screen.getByTitle('authIframe')).toBeInTheDocument();

    rerender(<AuthIframe country="gb" language="en" isAuth0Enabled={true} />);
    expect(screen.queryByTitle('authIframe')).not.toBeInTheDocument();
  });
});

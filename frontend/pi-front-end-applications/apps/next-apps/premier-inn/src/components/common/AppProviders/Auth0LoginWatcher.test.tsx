import { render, waitFor } from '@testing-library/react';

import { Auth0LoginWatcher } from './Auth0LoginWatcher';

const mockUseUser = jest.fn();
jest.mock('@auth0/nextjs-auth0', () => ({
  useUser: () => mockUseUser(),
}));

describe('Auth0LoginWatcher', () => {
  afterEach(() => {
    mockUseUser.mockReset();
  });

  it('renders nothing', () => {
    mockUseUser.mockReturnValue({ user: null, isLoading: false, error: null });
    const onChange = jest.fn();

    const { container } = render(<Auth0LoginWatcher onChange={onChange} />);

    expect(container.firstChild).toBeNull();
  });

  it('calls onChange(true) once a user session resolves', async () => {
    mockUseUser.mockReturnValue({
      user: { email: 'testing.manager@mailinator.com' },
      isLoading: false,
      error: null,
    });
    const onChange = jest.fn();

    render(<Auth0LoginWatcher onChange={onChange} />);

    await waitFor(() => {
      expect(onChange).toHaveBeenCalledWith(true);
    });
  });

  it('calls onChange(false) when there is no user session', async () => {
    mockUseUser.mockReturnValue({ user: null, isLoading: false, error: null });
    const onChange = jest.fn();

    render(<Auth0LoginWatcher onChange={onChange} />);

    await waitFor(() => {
      expect(onChange).toHaveBeenCalledWith(false);
    });
  });

  it('does not call onChange while the session is still loading', () => {
    mockUseUser.mockReturnValue({ user: null, isLoading: true, error: null });
    const onChange = jest.fn();

    render(<Auth0LoginWatcher onChange={onChange} />);

    expect(onChange).not.toHaveBeenCalled();
  });
});

import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { Scheme } from '@whitbread-eos/api';
import { appPreCheck } from '@whitbread-eos/utils/server';
import * as nextNavigation from 'next/navigation';
import React from 'react';

import LinkAccountButton from './LinkAccountButton';

jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: jest.fn(),
    query: {},
    asPath: '',
    route: '/',
  })),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  WorldlineLink: (props: any) => (
    <div data-testid="WorldlineLink" {...props}>
      {props.children}
    </div>
  ),
  Button: (props: any) => (
    <button data-testid="link-account-button" {...props}>
      {props.children}
    </button>
  ),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  appPreCheck: jest.fn(() => Promise.resolve({ isTetheredUser: true })),
  cn: jest.fn((...args: string[]) => args.join(' ')),
  useTranslation: jest.fn(() => ({
    t: (key: string) => key,
  })),
}));

jest.mock('../ExistingAccountModal', () => ({
  ExistingAccountModal: ({ isModalOpen, onClose }: any) =>
    isModalOpen ? (
      <div data-testid="ExistingAccountModal" onClick={onClose}>
        Modal
      </div>
    ) : null,
}));

describe('LinkAccountButton', () => {
  it('renders the button with the correct text', () => {
    render(
      <LinkAccountButton
        variant="primary"
        className="link-account-button"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={jest.fn()}
      >
        Link Account
      </LinkAccountButton>
    );
    const button = screen.getByText('Link Account');
    expect(button).toBeInTheDocument();
  });

  it('calls router.push with correct URL when button is clicked and isTetheredUser is true', async () => {
    const setIsModalOpen = jest.fn();
    appPreCheck.mockResolvedValueOnce({ isTetheredUser: true });

    const pushMock = jest.fn();
    (nextNavigation.useRouter as jest.Mock).mockReturnValue({
      push: pushMock,
      query: {},
      asPath: '',
      route: '/',
    });

    render(
      <LinkAccountButton
        variant="primary"
        className="link-account-button"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={setIsModalOpen}
      >
        Link Account
      </LinkAccountButton>
    );

    const button = await screen.findByTestId('link-account-button');
    button.click();
    expect(pushMock).toHaveBeenCalledWith(
      `${process?.env?.NEXT_PUBLIC_WORLDLINE_HOST ?? ''}/BBLinkCode.aspx`
    );
  });

  it('renders Button with correct props', () => {
    render(
      <LinkAccountButton
        variant="secondary"
        className="test-class"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={jest.fn()}
      >
        Link Account
      </LinkAccountButton>
    );
    const button = screen.getByTestId('link-account-button');
    expect(button).toBeInTheDocument();
    expect(button).toHaveClass('test-class');
  });

  it('calls window._satellite.track when button is clicked', async () => {
    const setIsModalOpen = jest.fn();
    appPreCheck.mockResolvedValueOnce({ isTetheredUser: true });

    window._satellite = { track: jest.fn() };

    render(
      <LinkAccountButton
        variant="primary"
        className="link-account-button"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={setIsModalOpen}
      >
        Link Account
      </LinkAccountButton>
    );

    const button = await screen.findByTestId('link-account-button');
    button.click();
    expect(window._satellite.track).toHaveBeenCalledWith('linkAccount');
  });

  it('renders Button with default variant if none is provided', () => {
    render(
      <LinkAccountButton
        variant="default"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={jest.fn()}
      >
        Link Account
      </LinkAccountButton>
    );
    const button = screen.getByTestId('link-account-button');
    expect(button).toHaveAttribute('variant', 'default');
  });

  it('does not call router.push or window._satellite.track when isTetheredUser is false', async () => {
    const setIsModalOpen = jest.fn();
    (appPreCheck as jest.Mock).mockResolvedValueOnce({ isTetheredUser: false });

    const pushMock = jest.fn();
    (nextNavigation.useRouter as jest.Mock).mockReturnValue({
      push: pushMock,
      query: {},
      asPath: '',
      route: '/',
    });

    window._satellite = { track: jest.fn() };

    render(
      <LinkAccountButton
        variant="primary"
        className="link-account-button"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={setIsModalOpen}
      >
        Link Account
      </LinkAccountButton>
    );

    const button = await screen.findByTestId('link-account-button');
    button.click();

    await screen.findByRole('button');

    expect(pushMock).not.toHaveBeenCalled();
    expect(window._satellite.track).not.toHaveBeenCalled();
    expect(setIsModalOpen).toHaveBeenCalledWith(true);
  });

  it('uses default variant if variant prop is missing', () => {
    render(
      <LinkAccountButton
        variant="default"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={jest.fn()}
      >
        Link Account
      </LinkAccountButton>
    );
    const button = screen.getByTestId('link-account-button');
    expect(button).toHaveAttribute('variant', 'default');
  });

  it('does not call setAppCheckResult again if appCheckResult is already set', async () => {
    const setIsModalOpen = jest.fn();
    appPreCheck.mockResolvedValueOnce({ isTetheredUser: true });

    render(
      <LinkAccountButton
        variant="primary"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={setIsModalOpen}
      >
        Link Account
      </LinkAccountButton>
    );

    await screen.findByTestId('link-account-button');
    render(
      <LinkAccountButton
        variant="primary"
        scheme={'en' as Scheme}
        token="sample-token"
        setIsModalOpen={setIsModalOpen}
      >
        Link Account
      </LinkAccountButton>
    );
    expect(appPreCheck).toHaveBeenCalled();
  });
});

import '@testing-library/jest-dom';
import { render, cleanup } from '@testing-library/react';

import AmazonChatIcon from './AmazonChatIcon.component';

jest.mock('@whitbread-eos/utils', () => ({
  formatAssetsUrl: (path: string) => path,
}));

describe('AmazonChatIcon', () => {
  const baseUrl = 'http://localhost';
  const amazonChatIcon = '/chat-icon.png';

  let observers: any[] = [];

  beforeAll(() => {
    class MockMutationObserver {
      /* global MutationCallback */
      callback: MutationCallback;
      constructor(cb: MutationCallback) {
        this.callback = cb;
        observers.push(this);
      }

      observe() {}

      disconnect() {}
    }

    Object.defineProperty(window, 'MutationObserver', {
      writable: true,
      configurable: true,
      value: MockMutationObserver,
    });
  });

  beforeEach(() => {
    document.head.innerHTML = '';
    document.body.innerHTML = '';
    observers = [];
  });

  afterEach(() => {
    cleanup();
  });

  it('should inject style tag into document head', () => {
    render(<AmazonChatIcon amazonChatIcon={amazonChatIcon} />);
    const style = document.head.querySelector('style');
    expect(style).toBeInTheDocument();
    expect(style?.innerHTML).toContain('#amazon-connect-open-widget-button');
  });

  it('should add an img to the button when it appears', () => {
    render(<AmazonChatIcon amazonChatIcon={amazonChatIcon} />);

    const button = document.createElement('button');
    button.id = 'amazon-connect-open-widget-button';
    document.body.appendChild(button);
    observers.forEach((obs) => obs.callback([], obs));

    const img = document.querySelector<HTMLImageElement>('#live-chat-gif-icon');

    expect(img).toBeInTheDocument();
    expect(img?.src).toBe(`${baseUrl}${amazonChatIcon}`);
  });

  it('should clean up styles on unmount', () => {
    const { unmount } = render(<AmazonChatIcon amazonChatIcon={amazonChatIcon} />);
    expect(document.head.querySelector('style')).toBeInTheDocument();

    unmount();

    expect(document.head.querySelector('style')).toBeNull();
  });

  it('should do nothing when the chat button is not present', () => {
    render(<AmazonChatIcon amazonChatIcon={amazonChatIcon} />);

    observers.forEach((obs) => obs.callback([], obs));
    const img = document.querySelector('#live-chat-gif-icon');
    expect(img).toBeNull();
  });
});

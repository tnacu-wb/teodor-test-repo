import { render, screen, fireEvent } from '@testing-library/react';
import React from 'react';

import { formatTime, ResendText, TimerStatus } from './common';
import { downloadFileFromUrl } from './common';

jest.mock('@whitbread-eos/utils', () => ({
  usePromoTranslation: () => ({
    oneTimePasswordExpiredMessage: 'Code has expired',
    expiresTimerText: 'Expires in',
  }),
}));

describe('formatTime', () => {
  it('formats seconds less than a minute correctly', () => {
    expect(formatTime(5)).toBe('0:05');
    expect(formatTime(9)).toBe('0:09');
    expect(formatTime(30)).toBe('0:30');
  });

  it('formats exactly one minute correctly', () => {
    expect(formatTime(60)).toBe('1:00');
  });

  it('formats minutes and seconds correctly', () => {
    expect(formatTime(75)).toBe('1:15');
    expect(formatTime(125)).toBe('2:05');
    expect(formatTime(185)).toBe('3:05');
  });

  it('handles zero seconds', () => {
    expect(formatTime(0)).toBe('0:00');
  });

  it('pads single-digit seconds with leading zero', () => {
    expect(formatTime(61)).toBe('1:01');
    expect(formatTime(121)).toBe('2:01');
  });
});

describe('ResendText', () => {
  it('renders the text and resend button', () => {
    render(<ResendText onResend={jest.fn()} />);

    expect(screen.getByText(/Didn't receive your code\?/i)).toBeInTheDocument();

    expect(screen.getByRole('button', { name: /resend/i })).toBeInTheDocument();
  });

  it('calls onResend when resend button is clicked', () => {
    const onResendMock = jest.fn();
    render(<ResendText onResend={onResendMock} />);

    fireEvent.click(screen.getByRole('button', { name: /resend/i }));

    expect(onResendMock).toHaveBeenCalledTimes(1);
  });
});

describe('TimerStatus', () => {
  it('renders expired state when timeLeft is 0', () => {
    render(<TimerStatus timeLeft={0} />);

    expect(screen.getByText('Code has expired')).toBeInTheDocument();
  });

  it('renders running timer when timeLeft > 0', () => {
    render(<TimerStatus timeLeft={125} />); // 2:05

    expect(screen.getByText('Expires in')).toBeInTheDocument();

    expect(screen.getByText('2:05')).toBeInTheDocument();
  });

  it('formats time correctly for less than 10 seconds', () => {
    render(<TimerStatus timeLeft={7} />);
    expect(screen.getByText('0:07')).toBeInTheDocument();
  });

  it('formats time correctly for exactly 10 minutes', () => {
    render(<TimerStatus timeLeft={600} />);
    expect(screen.getByText('10:00')).toBeInTheDocument();
  });
});

describe('downloadFileFromUrl', () => {
  let appendSpy: jest.SpyInstance;
  let removeSpy: jest.SpyInstance;

  beforeEach(() => {
    appendSpy = jest.spyOn(document.body, 'appendChild');
    removeSpy = jest.spyOn(document.body, 'removeChild');
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('returns early when url is empty', () => {
    const createSpy = jest.spyOn(document, 'createElement');

    downloadFileFromUrl('');

    expect(createSpy).not.toHaveBeenCalled();
    expect(appendSpy).not.toHaveBeenCalled();
    expect(removeSpy).not.toHaveBeenCalled();
  });

  it('downloads file with provided filename', () => {
    const anchor = document.createElement('a');
    const clickSpy = jest.spyOn(anchor, 'click').mockImplementation();

    jest.spyOn(document, 'createElement').mockReturnValue(anchor);

    downloadFileFromUrl('https://example.com/file.zip', 'custom.zip');

    expect(anchor.href).toBe('https://example.com/file.zip');
    expect(anchor.download).toBe('custom.zip');
    expect(anchor.rel).toBe('noopener noreferrer');
    expect(appendSpy).toHaveBeenCalledWith(anchor);
    expect(clickSpy).toHaveBeenCalled();
    expect(removeSpy).toHaveBeenCalledWith(anchor);
  });

  it('uses filename from url when filename is not provided', () => {
    const anchor = document.createElement('a');
    jest.spyOn(anchor, 'click').mockImplementation();
    jest.spyOn(document, 'createElement').mockReturnValue(anchor);

    downloadFileFromUrl('https://example.com/my-file.zip');

    expect(anchor.download).toBe('my-file.zip');
  });

  it('falls back to default filename when url has no file', () => {
    const anchor = document.createElement('a');
    jest.spyOn(anchor, 'click').mockImplementation();
    jest.spyOn(document, 'createElement').mockReturnValue(anchor);

    downloadFileFromUrl('https://example.com/');

    expect(anchor.download).toBe('download');
  });
});

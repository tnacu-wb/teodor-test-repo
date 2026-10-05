import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import React from 'react';
import { useFormContext } from 'react-hook-form';

import SocialMediaHandleFields from './social-media-handle-fields';

jest.mock('react-hook-form', () => ({
  useFormContext: jest.fn(),
  Controller: jest.fn(({ render }) => render({ field: {} })),
}));

describe('SocialMediaHandleFields', () => {
  const mockTrigger = jest.fn();
  const mockClearErrors = jest.fn();
  const mockHandleSocialMediaChange = jest.fn();

  const defaultProps = {
    headingStyle: 'heading-class',
    subHeadingStyle: 'subheading-class',
    icons: {
      'icon.chevron.down': '/path/to/chevron-icon',
      'icon.notification.error': '/path/to/error-icon',
    },
    socialMediaValuePlaceholder: 'Enter social media handle',
    socialMediaOptions: [
      { value: 'facebook', displayValue: 'Facebook' },
      { value: 'twitter', displayValue: 'Twitter' },
    ],
    handleSocialMediaChange: mockHandleSocialMediaChange,
  };

  beforeEach(() => {
    (useFormContext as jest.Mock).mockReturnValue({
      control: {},
      formState: { errors: {} },
      trigger: mockTrigger,
      clearErrors: mockClearErrors,
    });
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders the component with heading and subheading', () => {
    render(<SocialMediaHandleFields {...defaultProps} />);

    expect(screen.getByText('Your company’s social media handle')).toBeInTheDocument();
    expect(screen.getByText('(optional)')).toBeInTheDocument();
    expect(
      screen.getByText(
        'This can reduce the amount of time it takes to confirm your company details.'
      )
    ).toBeInTheDocument();
  });

  it('renders the social media type select field', () => {
    render(<SocialMediaHandleFields {...defaultProps} />);

    const selectField = screen.getByText('Social Media Handle');
    expect(selectField).toBeInTheDocument();
  });

  it('renders the social media value input field', () => {
    render(<SocialMediaHandleFields {...defaultProps} />);

    const inputField = screen.getByText('Enter social media handle');
    expect(inputField).toBeInTheDocument();
  });

  it('calls handleSocialMediaChange when social media type changes', async () => {
    const spyOnAddressChange = jest.spyOn(
      { mockHandleSocialMediaChange },
      'mockHandleSocialMediaChange'
    );
    render(<SocialMediaHandleFields {...defaultProps} />);

    const selectField = screen.getByTestId('socialMediaType-IB-Form-Select-Button');

    await waitFor(async () => {
      fireEvent.click(selectField);
    });

    await waitFor(async () => {
      fireEvent.click(screen.getByTestId('socialMediaType-twitter-Option'));
    });

    expect(spyOnAddressChange).toHaveBeenCalledWith({ value: 'twitter', displayValue: 'Twitter' });
  });
});

import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { Card } from './Card';

describe('Card', () => {
  it('should render the component', () => {
    render(
      <Card
        title="Phone"
        testId="phone-card"
        description="Some description"
        info="Extra info"
        imageProps={{ src: '/icons/phone.svg', alt: 'Phone icon', testId: 'phone-image' }}
      />
    );

    expect(screen.getByText('Phone')).toBeInTheDocument();
    expect(screen.getByText('Some description')).toBeInTheDocument();
    expect(screen.getByText('Extra info')).toBeInTheDocument();
    expect(screen.getByTestId('phone-image')).toBeInTheDocument();
    expect(screen.getByTestId('phone-image')).toHaveAttribute('src', '/icons/phone.svg');
    expect(screen.getByTestId('phone-image')).toHaveAttribute('alt', 'Phone icon');
  });

  it('should render the component with an email link', () => {
    render(
      <Card
        title="Email"
        testId="email-card"
        description="Some description"
        info="info@example.com"
        isEmail={true}
        imageProps={{ src: '/icons/email.svg', alt: 'Email icon', testId: 'email-image' }}
      />
    );

    expect(screen.getByText('Email')).toBeInTheDocument();
    expect(screen.getByText('Some description')).toBeInTheDocument();
    expect(screen.getByText('info@example.com')).toBeInTheDocument();
    expect(screen.getByTestId('email-image')).toBeInTheDocument();
    expect(screen.getByTestId('email-image')).toHaveAttribute('src', '/icons/email.svg');
    expect(screen.getByTestId('email-image')).toHaveAttribute('alt', 'Email icon');
    expect(screen.getByTestId('email-card-link')).toHaveAttribute(
      'href',
      'mailto:info@example.com'
    );
  });

  it('should render the component with a regular link', () => {
    render(
      <Card
        title="Website"
        testId="website-card"
        description="Visit our website"
        info="Click here"
        link="https://example.com"
        imageProps={{ src: '/icons/website.svg', alt: 'Website icon', testId: 'website-image' }}
      />
    );

    expect(screen.getByText('Website')).toBeInTheDocument();
    expect(screen.getByText('Visit our website')).toBeInTheDocument();
    expect(screen.getByText('Click here')).toBeInTheDocument();
    expect(screen.getByTestId('website-image')).toBeInTheDocument();
    expect(screen.getByTestId('website-image')).toHaveAttribute('src', '/icons/website.svg');
    expect(screen.getByTestId('website-image')).toHaveAttribute('alt', 'Website icon');
    expect(screen.getByTestId('website-card-link')).toHaveAttribute('href', 'https://example.com');
  });

  it('should prevent default behavior for "#" links', async () => {
    render(
      <Card
        title="Disabled Link"
        testId="disabled-card"
        description="This link is disabled"
        info="Not clickable"
        link="#"
      />
    );

    expect(screen.getByText('Disabled Link')).toBeInTheDocument();
    expect(screen.getByText('This link is disabled')).toBeInTheDocument();
    expect(screen.getByText('Not clickable')).toBeInTheDocument();
    expect(screen.getByTestId('disabled-card-link')).toHaveAttribute('href', '#');
  });

  it('should render the component without a link when neither isEmail nor link is true', () => {
    render(
      <Card
        title="No Link"
        testId="no-link-card"
        description="This card has no link"
        info="No link available"
      />
    );

    expect(screen.getByText('No Link')).toBeInTheDocument();
    expect(screen.getByText('This card has no link')).toBeInTheDocument();
    expect(screen.getByText('No link available')).toBeInTheDocument();
    expect(screen.queryByTestId('no-link-card-link')).not.toBeInTheDocument();
  });

  it('should render the component with an onClick handler and invoke it when clicked', async () => {
    const user = userEvent.setup();
    const mockOnClick = jest.fn();
    render(
      <Card
        title="Test Card"
        description="This is a test card."
        info="Click me"
        testId="test-card"
        link="#"
        onClick={mockOnClick}
      />
    );

    expect(screen.getByText('Test Card')).toBeInTheDocument();
    expect(screen.getByText('This is a test card.')).toBeInTheDocument();
    expect(screen.getByText('Click me')).toBeInTheDocument();

    const linkElement = screen.getByTestId('test-card-link');
    await user.click(linkElement);
    expect(mockOnClick).toHaveBeenCalledTimes(1);
  });

  it('should not invoke onClick if it is not provided', async () => {
    const user = userEvent.setup();
    const mockOnClick = jest.fn();

    render(
      <Card
        title="Test Card"
        description="This is a test card."
        info="Click me"
        testId="test-card"
        link="#"
      />
    );
    const linkElement = screen.getByTestId('test-card-link');
    await user.click(linkElement);
    expect(mockOnClick).not.toHaveBeenCalled();
  });

  it('should render the component as a button when isButton is true', () => {
    render(
      <Card
        title="Button Card"
        testId="button-card"
        description="This is a button card"
        info="Click me"
        isButton={true}
        imageProps={{ src: '/icons/button.svg', alt: 'Button icon', testId: 'button-image' }}
      />
    );

    expect(screen.getByText('Button Card')).toBeInTheDocument();
    expect(screen.getByText('This is a button card')).toBeInTheDocument();
    expect(screen.getByText('Click me')).toBeInTheDocument();
    expect(screen.getByTestId('button-card-button')).toBeInTheDocument();
    expect(screen.queryByTestId('button-card-link')).not.toBeInTheDocument();
  });

  it('should invoke onClick when button is clicked', async () => {
    const user = userEvent.setup();
    const mockOnClick = jest.fn();
    render(
      <Card
        title="Button Card"
        testId="button-card"
        description="This is a button card"
        info="Click me"
        isButton={true}
        onClick={mockOnClick}
      />
    );

    const buttonElement = screen.getByTestId('button-card-button');
    await user.click(buttonElement);
    expect(mockOnClick).toHaveBeenCalledTimes(1);
  });
});

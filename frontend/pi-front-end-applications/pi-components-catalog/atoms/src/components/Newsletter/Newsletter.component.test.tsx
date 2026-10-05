import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import Newsletter from './Newsletter.component';

const mockNewsletterData = {
  data: {
    newsletterSignup: {
      introViewTitle: 'Newsletter',
      introViewText:
        'Simply fill in your details below to be the first to hear about all our latest news and getaway innspiration!',
      signUpButtonText: 'Sign up',
    },
  },
  routerPush: jest.fn(),
};

describe('Promotion component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render promotion component correctly', () => {
    const { getByTestId } = render(<Newsletter {...mockNewsletterData} />);

    expect(getByTestId('newsletter-title')).toBeInTheDocument();
    expect(getByTestId('newsletter-description')).toBeInTheDocument();
    expect(getByTestId('newsletter-link')).toBeInTheDocument();
  });

  it('calls routerPush with the correct URL when the button is clicked', () => {
    const { getByTestId } = render(<Newsletter {...mockNewsletterData} />);

    fireEvent.click(getByTestId('newsletter-link'));

    expect(mockNewsletterData.routerPush).toHaveBeenCalledWith(
      'https://www.premierinn.com/de/de/presse.html'
    );
  });

  it("doesn't render anything if there is no data", () => {
    // @ts-expect-error for testing purpose
    const { container } = render(<Newsletter {...{ ...mockNewsletterData, data: {} }} />);

    expect(container.firstChild).toBeNull();
  });
});

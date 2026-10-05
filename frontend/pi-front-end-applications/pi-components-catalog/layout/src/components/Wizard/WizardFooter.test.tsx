import React from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import { Props, WizardFooter } from './WizardFooter.component';

const mockProps: Props = {
  buttonLabel: 'test',
};

describe('WizardFooter component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render WizardFooter component', () => {
    const { getByTestId } = render(<WizardFooter {...mockProps} />);

    expect(getByTestId('footer-button')).toBeInTheDocument();
  });

  it('should render WizardFooter component with a link', () => {
    mockProps.linkLabel = 'test';
    const { getByTestId } = render(<WizardFooter {...mockProps} />);

    expect(getByTestId('footer-link')).toBeInTheDocument();
    expect(getByTestId('footer-button')).toBeInTheDocument();
  });

  it('should use default onButtonClick when button is clicked', () => {
    const { getByTestId } = render(<WizardFooter {...mockProps} />);

    fireEvent.click(getByTestId('footer-button'));
  });

  it('should use default onLinkClick when link is clicked', () => {
    mockProps.linkLabel = 'test';
    const { getByTestId } = render(<WizardFooter {...mockProps} />);

    fireEvent.click(getByTestId('footer-link'));
  });
});

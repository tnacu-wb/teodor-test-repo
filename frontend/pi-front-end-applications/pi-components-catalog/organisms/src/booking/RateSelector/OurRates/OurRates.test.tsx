import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import type { HIRateClassification } from '@whitbread-eos/api';
import React from 'react';

import OurRates from './OurRates.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  renderSanitizedHtml: (html: string) => html,
  getCorporateDiscountRatePlanCode: jest.fn().mockImplementation(() => 'FCDNLR30'),
}));

describe('OurRates component', () => {
  const rateClassifications: (HIRateClassification | undefined)[] = [
    {
      ratePlanCode: 'SEMIFLEX',
      rateName: 'Semi-Flex',
      rateDescription: 'Pay now, fully refundable',
      isCorporateDiscountAvailable: false,
    },
    {
      ratePlanCode: 'FCDNLR30',
      rateName: 'Travel Industry Rate',
      rateDescription: 'Pay now, fully refundable with free cancellation',
      isCorporateDiscountAvailable: true,
    },
  ] as any;

  const rateClassificationsWithoutCorporateDiscount: (HIRateClassification | undefined)[] = [
    {
      ratePlanCode: 'SEMIFLEX',
      rateName: 'Semi-Flex',
      rateDescription: 'Pay now, fully refundable',
      isCorporateDiscountAvailable: false,
    },
    {
      ratePlanCode: 'STANDARD',
      rateName: 'Standard',
      rateDescription: 'Pay now, non-refundable',
      isCorporateDiscountAvailable: false,
    },
    {
      ratePlanCode: 'FCDNLR30',
      rateName: 'Travel Industry Rate ',
      rateDescription:
        'Pay now or on arrival, fully refundable with free cancellation up to 6pm on the day of arrival',
      isCorporateDiscountAvailable: true,
    },
  ] as any;

  it('renders the component correctly', () => {
    render(<OurRates brand="PI" rateClassifications={rateClassifications} />);

    expect(screen.getByText('pihotelinfo.ratesExplained')).toBeInTheDocument();
  });

  it('opens and closes the modal when the link is clicked', () => {
    render(<OurRates brand="PI" rateClassifications={rateClassifications} />);

    const modalLink = screen.getByTestId('hdp_ratesExplainedLinkText');
    fireEvent.click(modalLink);

    expect(screen.getByText('pihotelinfo.ourRates')).toBeInTheDocument();

    fireEvent.click(screen.getByText('pihotelinfo.ourRates'));
    expect(screen.queryByText('pihotelinfo.ourRates')).toBeInTheDocument();
  });

  it('renders travel industry information if corporate discount is available', () => {
    render(<OurRates brand="PI" rateClassifications={rateClassifications} />);

    fireEvent.click(screen.getByTestId('hdp_ratesExplainedLinkText'));

    expect(screen.getByText('pihotelinfo.ourRates')).toBeInTheDocument();
  });

  it('should render travel industry information if corporate discount is available', () => {
    render(
      <OurRates brand="PI" rateClassifications={rateClassificationsWithoutCorporateDiscount} />
    );

    fireEvent.click(screen.getByTestId('hdp_ratesExplainedLinkText'));

    expect(screen.queryByText('pihotelinfo.ourRates')).toBeInTheDocument();
  });

  it('sorts rates when corporate discount is available', () => {
    render(<OurRates brand="PremierInn" rateClassifications={rateClassifications} />);

    fireEvent.click(screen.getByTestId('hdp_ratesExplainedLinkText'));

    const rateNames = screen
      .getAllByText(/Semi-Flex|Travel Industry Rate/)
      .map((node) => node.textContent);
    expect(rateNames[1]).toBe('Travel Industry Rate');
  });

  it('does not sort rates when corporate discount is not available', () => {
    render(
      <OurRates brand="PID" rateClassifications={rateClassificationsWithoutCorporateDiscount} />
    );

    fireEvent.click(screen.getByTestId('hdp_ratesExplainedLinkText'));

    const rateNames = screen.getAllByText(/Semi-Flex|Standard/).map((node) => node.textContent);
    expect(rateNames[0]).toBe('Semi-Flex');
    expect(rateNames[1]).toBe('Standard');
  });

  it('renders the component correctly with brand hub', () => {
    render(<OurRates brand="hub" rateClassifications={rateClassifications} />);

    expect(screen.getByText('pihotelinfo.ratesExplained')).toBeInTheDocument();
  });
});

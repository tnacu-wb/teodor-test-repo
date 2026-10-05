import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { InnBusinessPaySectionSkeleton } from './inn-business-pay-section-skeleton';

describe('InnBusinessPaySectionSkeleton', () => {
  it('renders the skeleton structure with correct number of elements', () => {
    render(<InnBusinessPaySectionSkeleton />);

    const container = screen.getByTestId('inn-business-pay-skeleton');
    expect(container).toBeInTheDocument();

    const notificationSkeleton = screen.getByTestId('notification-skeleton');
    expect(notificationSkeleton).toBeInTheDocument();

    const cardSkeletons = screen.getAllByTestId('card-skeleton');
    expect(cardSkeletons).toHaveLength(2);

    const preferenceSkeletons = screen.getAllByTestId('preference-skeleton');
    expect(preferenceSkeletons).toHaveLength(6);
  });

  it('applies correct styling classes', () => {
    render(<InnBusinessPaySectionSkeleton />);

    const container = screen.getByTestId('inn-business-pay-skeleton');
    expect(container).toHaveClass(
      'px-12',
      'pt-12',
      'mobile:min-w-full',
      'mobile:px-4',
      'mt-10',
      'mb-8'
    );

    const notificationSkeleton = screen.getByTestId('notification-skeleton');
    expect(notificationSkeleton).toHaveClass(
      'mb-6',
      'w-[620px]',
      'mobile:w-auto',
      'bg-lightGrey4',
      'h-16',
      'rounded',
      'animate-pulse'
    );

    const [firstCardSkeleton] = screen.getAllByTestId('card-skeleton');
    expect(firstCardSkeleton).toHaveClass('mb-6');

    const [firstPreferenceSkeleton] = screen.getAllByTestId('preference-skeleton');
    expect(firstPreferenceSkeleton).toHaveClass('flex', 'flex-col', 'gap-1');
  });
});

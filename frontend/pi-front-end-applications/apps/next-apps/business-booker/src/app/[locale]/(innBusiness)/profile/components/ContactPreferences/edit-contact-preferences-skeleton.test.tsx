import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { EditContactPreferencesSkeleton } from './edit-contact-preferences-skeleton';

describe('EditContactPreferencesSkeleton', () => {
  it('renders the skeleton with all placeholder elements', () => {
    const { container } = render(<EditContactPreferencesSkeleton />);

    expect(
      container.querySelector('[data-testid="EditContactPreferencesSkeleton"]')
    ).toBeInTheDocument();

    const skeletons = container.getElementsByClassName('animate-pulse');
    expect(skeletons).toHaveLength(4);

    const titleSkeleton = container.querySelector('.w-\\[18rem\\]');
    expect(titleSkeleton).toBeInTheDocument();

    const descriptionSkeletons = container.querySelectorAll('.w-full.h-\\[1\\.5rem\\]');
    expect(descriptionSkeletons).toHaveLength(2);

    const buttonSkeleton = container.querySelector('.h-\\[56px\\]');
    expect(buttonSkeleton).toBeInTheDocument();
  });
});

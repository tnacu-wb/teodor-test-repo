import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import ChangePasswordSkeleton from './ChangePasswordSkeleton';

describe('ChangePasswordSkeleton', () => {
  it('renders the skeleton container with correct test id', () => {
    render(<ChangePasswordSkeleton />);
    expect(screen.getByTestId('ChangePasswordSkeleton')).toBeInTheDocument();
  });

  it('renders three Skeleton components', () => {
    render(<ChangePasswordSkeleton />);
    expect(screen.getByTestId('ChangePasswordSkeleton').querySelectorAll('div').length).toBe(3);
  });

  it('applies correct class names to Skeleton components', () => {
    render(<ChangePasswordSkeleton />);
    const container = screen.getByTestId('ChangePasswordSkeleton');
    const skeletonDivs = container.querySelectorAll('div');
    expect(
      Array.from(skeletonDivs).some((div) =>
        div.className.includes('h-[2rem] w-[16rem] mt-[1.5rem] mb-[1.5rem]')
      )
    ).toBe(true);
    expect(
      Array.from(skeletonDivs).some((div) =>
        div.className.includes('h-[1.5rem] w-[14rem] mt-[1.5rem] mb-[1.5rem]')
      )
    ).toBe(true);
    expect(
      Array.from(skeletonDivs).some((div) =>
        div.className.includes(
          'mobile:w-[21.438rem] w-[19.313rem] h-[3.5rem] mt-[1.5rem] mb-[4rem]'
        )
      )
    ).toBe(true);
  });
});

import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { ProgressBar } from './progress-bar';

describe('ProgressBar', () => {
  it('renders with correct data-testid', () => {
    const { getByTestId } = render(
      <ProgressBar
        solidFillPercentage={30}
        patternFillPercentage={20}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    expect(getByTestId('progress-bar')).toBeInTheDocument();
  });

  it('applies opacity-50 class when hasTransparency is true', () => {
    const { getByTestId } = render(
      <ProgressBar
        solidFillPercentage={40}
        patternFillPercentage={10}
        dataTestId="progress-bar"
        hasTransparency={true}
      />
    );
    expect(getByTestId('progress-bar').className).toContain('opacity-50');
  });

  it('does not apply opacity-50 class when hasTransparency is false', () => {
    const { getByTestId } = render(
      <ProgressBar
        solidFillPercentage={40}
        patternFillPercentage={10}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    expect(getByTestId('progress-bar').className).not.toContain('opacity-50');
  });

  it('renders solid fill with correct width', () => {
    const { container } = render(
      <ProgressBar
        solidFillPercentage={55}
        patternFillPercentage={0}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    const solidDiv = container.querySelector(
      '.absolute.top-0.left-0.h-full.bg-primaryColor'
    ) as HTMLDivElement;
    expect(solidDiv).toBeInTheDocument();
    expect(solidDiv.style.width).toBe('55%');
  });

  it('renders pattern fill with correct width and left', () => {
    const { container } = render(
      <ProgressBar
        solidFillPercentage={25}
        patternFillPercentage={35}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    const patternDiv = container.querySelector(
      '.absolute.top-0.h-full.border-r-2'
    ) as HTMLDivElement;
    expect(patternDiv).toBeInTheDocument();
    expect(patternDiv.style.left).toBe('');
    expect(patternDiv.style.width).toBe('25%');
  });

  it('renders both solid and pattern fill when both percentages are > 0', () => {
    const { container } = render(
      <ProgressBar
        solidFillPercentage={10}
        patternFillPercentage={15}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    const solidDiv = container.querySelector('.absolute.top-0.left-0.h-full.bg-primaryColor');
    const patternDiv = container.querySelector('.absolute.top-0.h-full.border-r-2');
    expect(solidDiv).toBeInTheDocument();
    expect(patternDiv).toBeInTheDocument();
  });

  it('renders only solid fill when patternFillPercentage is 0', () => {
    const { container } = render(
      <ProgressBar
        solidFillPercentage={80}
        patternFillPercentage={0}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    const solidDiv = container.querySelector('.absolute.top-0.left-0.h-full.bg-primaryColor');
    const patternDiv = container.querySelector('.absolute.top-0.h-full.border-r-2');
    expect(solidDiv).toBeInTheDocument();
    expect(patternDiv).toBeInTheDocument();
    if (patternDiv) {
      expect((patternDiv as HTMLDivElement).style.width).toBe('80%');
    }
  });

  it('renders only pattern fill when solidFillPercentage is 0', () => {
    const { container } = render(
      <ProgressBar
        solidFillPercentage={0}
        patternFillPercentage={60}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    const solidDiv = container.querySelector('.absolute.top-0.left-0.h-full.bg-primaryColor');
    const patternDiv = container.querySelector('.absolute.top-0.h-full.border-r-2');
    expect(solidDiv).toBeInTheDocument();
    if (solidDiv) {
      expect((solidDiv as HTMLDivElement).style.width).toBe('0%');
    }
    expect(patternDiv).toBeInTheDocument();
    if (patternDiv) {
      expect((patternDiv as HTMLDivElement).style.left).toBe('');
      expect((patternDiv as HTMLDivElement).style.width).toBe('0%');
    }
  });

  it('renders with 0% widths when both percentages are 0', () => {
    const { container } = render(
      <ProgressBar
        solidFillPercentage={0}
        patternFillPercentage={0}
        dataTestId="progress-bar"
        hasTransparency={false}
      />
    );
    const solidDiv = container.querySelector(
      '.absolute.top-0.left-0.h-full.bg-primaryColor'
    ) as HTMLDivElement | null;
    const patternDiv = container.querySelector(
      '.absolute.top-0.h-full.border-r-2'
    ) as HTMLDivElement | null;
    expect(solidDiv).toBeInTheDocument();
    if (solidDiv) {
      expect(solidDiv.style.width).toBe('0%');
    }
    expect(patternDiv).toBeInTheDocument();
    expect(patternDiv!.style.width).toBe('0%');
  });
});

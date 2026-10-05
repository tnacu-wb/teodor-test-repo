import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { ContactPreferencesSkeleton } from './contact-preferences-skeleton';

describe('ContactPreferencesSkeleton', () => {
  beforeEach(() => {
    render(<ContactPreferencesSkeleton />);
  });

  it('renders with the correct test id', () => {
    expect(screen.getByTestId('ContactPreferencesSkeleton')).toBeInTheDocument();
  });

  describe('Structure', () => {
    it('renders all major sections', () => {
      expect(screen.getByTestId('ContactPreferencesSkeleton')).toHaveClass('px-12 pt-12');

      const headerContainer = screen.getByTestId('ContactPreferencesSkeleton').children[1];
      expect(headerContainer).toHaveClass('w-full md:w-[620px]');

      const infoContainer = screen.getByTestId('ContactPreferencesSkeleton').children[2];
      expect(infoContainer).toHaveClass('w-full md:w-[620px]');

      const emailContainer = screen.getByTestId('ContactPreferencesSkeleton').children[3];
      expect(emailContainer).toHaveClass('w-full md:w-[620px]');
    });

    it('renders correct number of brand items', () => {
      const brandItems = screen.getAllByTestId('brand-item');
      expect(brandItems).toHaveLength(6);
    });
  });

  describe('Skeleton Components', () => {
    it('renders all skeleton elements in the back button section', () => {
      const backButtonContainer = screen.getByTestId('ContactPreferencesSkeleton').children[0];
      expect(backButtonContainer.children).toHaveLength(2);
      expect(backButtonContainer).toHaveClass('flex items-center');
    });

    it('renders all skeleton elements in the button container', () => {
      const buttonContainer = screen.getByTestId('ContactPreferencesSkeleton').children[0];
      expect(buttonContainer.children).toHaveLength(2);
    });
  });

  describe('Responsive Design', () => {
    it('applies correct mobile classes', () => {
      expect(screen.getByTestId('ContactPreferencesSkeleton')).toHaveClass(
        'mobile:min-w-full mobile:px-4'
      );
    });

    it('applies correct desktop classes for brand list', () => {
      const brandLists = screen.getAllByTestId('brand-item')[0].parentElement;
      expect(brandLists).toHaveClass('w-full md:w-[310px]');
    });
  });

  describe('Section Styles', () => {
    it('renders sections with correct border styles', () => {
      const mainSection = screen.getByTestId('ContactPreferencesSkeleton').children[4];
      expect(mainSection).toHaveClass('border border-lightGrey4');

      const middleSection = screen.getByTestId('ContactPreferencesSkeleton').children[5];
      expect(middleSection).toHaveClass('border-b-0 border-t-0');

      const bottomSection = screen.getByTestId('ContactPreferencesSkeleton').children[6];
      expect(bottomSection).toHaveClass('rounded-t-none');
    });
  });

  describe('Checkbox Sections', () => {
    it('renders checkbox sections with correct structure', () => {
      const checkboxSections = document.querySelectorAll('[class*="checkboxSectionStyle"]');
      checkboxSections.forEach((section) => {
        expect(section).toHaveClass('flex-1 flex flex-col gap-6');
        expect(section.children[0]).toHaveClass('flex flex-col gap-2');
        expect(section.children[1]).toHaveClass('flex items-center gap-2');
      });
    });
  });
});

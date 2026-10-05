import { render, RenderOptions } from '@testing-library/react';
import { FC, ReactElement, ReactNode } from 'react';

const AllTheProviders: FC<{ children: ReactNode }> = ({ children }) => {
  return <>{children}</>;
};

const customRender = (ui: ReactElement, options?: Omit<RenderOptions, 'wrapper'>) =>
  render(ui, { wrapper: AllTheProviders, ...options });

const mockUseTranslation = () => {
  return {
    t: (str: string) => str,
  };
};

/* eslint-disable import-x/export */
export * from '@testing-library/react';
export * from 'jest-axe';
export { default as userEvent } from '@testing-library/user-event';
export { customRender as render };
export { mockUseTranslation };
/* eslint-enable */

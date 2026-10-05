import { render, RenderOptions } from '@testing-library/react';
import React, { FC, ReactElement } from 'react';

const AllTheProviders: FC = ({ children }) => {
  return <>{children}</>;
};

const customRender = (ui: ReactElement, options?: Omit<RenderOptions, 'wrapper'>) =>
  render(ui, { wrapper: AllTheProviders, ...options });

/* eslint-disable import-x/export */
export * from '@testing-library/react';
export * from 'jest-axe';
export { default as userEvent } from '@testing-library/user-event';
export { customRender as render };
/* eslint-enable */

import '@testing-library/jest-dom';
import '@testing-library/jest-dom/extend-expect';
import 'jest-axe/extend-expect';

/**
 * This mock ensures that dynamically imported components are available
 * immediately in tests without additional async handling. The loader promise
 * is resolved at module initialization time via microtasks, before any test
 * function executes. This avoids the need for waitFor() or other async utilities
 * when testing components wrapped with dynamic().
 */
jest.mock('next/dynamic', () => ({
  __esModule: true,
  default: function mockDynamic(loader: () => Promise<any>) {
    // eslint-disable-next-line @typescript-eslint/no-var-requires
    const React = require('react');
    let ResolvedComponent: any = null;

    loader().then((mod: any) => {
      ResolvedComponent = mod.default || mod;
    });

    const MockDynamic = React.forwardRef(function MockDynamic(props: any, ref: any) {
      if (!ResolvedComponent) return null;
      return React.createElement(ResolvedComponent, { ...props, ref });
    });

    return MockDynamic;
  },
}));

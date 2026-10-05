import { render, act } from '@testing-library/react';
import React from 'react';

import {
  FeatureToggleContextProvider,
  useFeatureToggleData,
  FeatureToggleContext,
} from './FeatureToggleContext';

describe('FeatureToggleContextProvider', () => {
  afterEach(() => {
    // Clear any cookies set during a test
    document.cookie
      .split(';')
      .map((c) => c.trim())
      .filter(Boolean)
      .forEach((c) => {
        document.cookie = c.replace(/=.*/, `=;expires=${new Date(0).toUTCString()};path=/`);
      });
  });

  it('applies ftOverride cookie on top of defaultFeatureToggles on mount', () => {
    document.cookie = 'ftOverride=featureA=true,featureB=false';

    const { getByTestId } = render(
      <FeatureToggleContextProvider
        defaultFeatureToggles={{ featureA: false, featureB: true, featureC: true }}
      >
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => (
            <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(JSON.parse(getByTestId('featureToggleData').textContent ?? '{}')).toEqual({
      featureA: true,
      featureB: false,
      featureC: true,
    });
  });

  it('ignores ftOverride flags that this page never computed, matching server-side scoping', () => {
    document.cookie = 'ftOverride=featureA=true,unrelatedFlag=true';

    const { getByTestId } = render(
      <FeatureToggleContextProvider defaultFeatureToggles={{ featureA: false }}>
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => (
            <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(JSON.parse(getByTestId('featureToggleData').textContent ?? '{}')).toEqual({
      featureA: true,
    });
  });

  it('ignores dangerous keys like __proto__ in the ftOverride cookie', () => {
    document.cookie = 'ftOverride=featureA=true,__proto__=true,constructor=true';

    const { getByTestId } = render(
      <FeatureToggleContextProvider defaultFeatureToggles={{ featureA: false }}>
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => (
            <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(JSON.parse(getByTestId('featureToggleData').textContent ?? '{}')).toEqual({
      featureA: true,
    });
    expect(Object.prototype).not.toHaveProperty('featureA');
  });

  it('trims whitespace around keys/values in the ftOverride cookie', () => {
    document.cookie = 'ftOverride=featureA=true, featureB=false';

    const { getByTestId } = render(
      <FeatureToggleContextProvider defaultFeatureToggles={{ featureA: false, featureB: true }}>
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => (
            <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(JSON.parse(getByTestId('featureToggleData').textContent ?? '{}')).toEqual({
      featureA: true,
      featureB: false,
    });
  });

  it('does not throw when the ftOverride cookie has a malformed percent-encoded value', () => {
    document.cookie = 'ftOverride=%E0%A4%A';

    const { getByTestId } = render(
      <FeatureToggleContextProvider defaultFeatureToggles={{ featureA: false }}>
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => (
            <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(getByTestId('featureToggleData').textContent).toBe('{"featureA":false}');
  });

  it('leaves feature toggles untouched when no ftOverride cookie is present', () => {
    const { getByTestId } = render(
      <FeatureToggleContextProvider defaultFeatureToggles={{ featureA: false }}>
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => (
            <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(getByTestId('featureToggleData').textContent).toBe('{"featureA":false}');
  });

  it('sets initial feature toggles correctly', () => {
    const { getByText } = render(
      <FeatureToggleContextProvider>
        <FeatureToggleContext.Consumer>
          {({ featureToggles }) => <div>{JSON.stringify(featureToggles)}</div>}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    expect(getByText('{}')).toBeInTheDocument(); // Empty object as initial value
  });

  it('updates feature toggles correctly', () => {
    const { getByText } = render(
      <FeatureToggleContextProvider>
        <FeatureToggleContext.Consumer>
          {({ featureToggles, setFeatureToggles }) => (
            <div>
              <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
              <button onClick={() => setFeatureToggles({ newToggle: true })}>
                Update Feature Toggles
              </button>
            </div>
          )}
        </FeatureToggleContext.Consumer>
      </FeatureToggleContextProvider>
    );

    act(() => {
      getByText('Update Feature Toggles').click();
    });

    expect(getByText('{"newToggle":true}')).toBeInTheDocument();
  });
});

describe('useFeatureToggleData', () => {
  it('returns correct context values', () => {
    const TestComponent = () => {
      const { featureToggles, setFeatureToggles } = useFeatureToggleData();
      return (
        <div>
          <span data-testid="featureToggleData">{JSON.stringify(featureToggles)}</span>
          <button onClick={() => setFeatureToggles({ newToggle: true })}>Update</button>
        </div>
      );
    };

    const { getByTestId, getByText } = render(
      <FeatureToggleContextProvider>
        <TestComponent />
      </FeatureToggleContextProvider>
    );

    expect(getByTestId('featureToggleData').textContent).toBe('{}');

    act(() => {
      getByText('Update').click();
    });

    expect(getByTestId('featureToggleData').textContent).toBe('{"newToggle":true}');
  });
});

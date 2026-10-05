import React from 'react';
import { render } from '@testing-library/react';
import CookiesPage from '../../../../src/pages/business-booker/cookies';

describe('CookiesPage', () => {
  const consentCookies = {
    consentGiven: true,
    permissionExperience: true,
    permissionMarketing: false,
    permissionPerformance: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders without crashing', () => {
    const { container } = render(<CookiesPage consentCookies={consentCookies} />);
    expect(container).toBeTruthy();
  });

  it('posts message to parent window if in iframe', () => {
    const postMessageMock = jest.fn();
    // @ts-ignore
    window.parent = { postMessage: postMessageMock };
    // @ts-ignore
    window.self = {};
    // Simulate iframe
    Object.defineProperty(window, 'parent', {
      value: { postMessage: postMessageMock },
      writable: true,
    });
    Object.defineProperty(window, 'self', {
      value: {},
      writable: true,
    });

    render(<CookiesPage consentCookies={consentCookies} />);
    expect(postMessageMock).toHaveBeenCalledWith(
      {
        type: 'COOKIES_IFRAME_LOADED',
        consentCookies,
      },
      'http://localhost'
    );
  });

  it('does not post message if not in iframe', () => {
    const postMessageMock = jest.fn();
    Object.defineProperty(window, 'parent', {
      value: window,
      writable: true,
    });
    render(<CookiesPage consentCookies={consentCookies} />);
    expect(postMessageMock).not.toHaveBeenCalled();
  });

  it('getLayout returns an empty fragment', () => {
    const Layout = CookiesPage.getLayout();
    expect(Layout).toEqual(<></>);
  });

  describe('getServerSideProps', () => {
    const mockCookies = {
      get: jest.fn(),
    };
    const mockReq = { headers: { host: 'testhost' } };
    const mockRes = {};

    beforeEach(() => {
      jest.resetModules();
      jest.clearAllMocks();
    });

    it('returns redirect if not InnBusiness', async () => {
      jest.doMock('@whitbread-eos/utils', () => ({
        getServerSideCustomLocale: () => ({ language: 'en', country: 'gb' }),
      }));
      jest.doMock('@whitbread-eos/utils/server', () => ({
        isInnBusinessApp: () => false,
      }));
      const { getServerSideProps } = await import('../../../../src/pages/business-booker/cookies');
      const result = await getServerSideProps({
        req: mockReq,
        res: mockRes,
        locale: 'gb',
      } as any);
      expect(result).toHaveProperty('redirect');
      expect(result?.redirect?.destination).toContain('/gb/en/business-booker/home.html');
    });

    it('returns consentCookies props if InnBusiness', async () => {
      jest.doMock('@whitbread-eos/utils', () => ({
        getServerSideCustomLocale: () => ({ language: 'en', country: 'gb' }),
      }));
      jest.doMock('@whitbread-eos/utils/server', () => ({
        isInnBusinessApp: () => true,
      }));
      jest.doMock('cookies', () => {
        return jest.fn().mockImplementation(() => ({
          get: (key: string) => {
            if (key === 'consent_cookie') return 'true';
            if (key === 'permissionExperience') return 'true';
            if (key === 'permissionMarketing') return 'false';
            if (key === 'permissionPerformance') return 'true';
            return undefined;
          },
        }));
      });
      const { getServerSideProps } = await import('../../../../src/pages/business-booker/cookies');
      const result = await getServerSideProps({
        req: mockReq,
        res: mockRes,
        locale: 'gb',
      } as any);
      expect(result).toHaveProperty('props');
      expect(result?.props?.consentCookies).toEqual({
        consentGiven: true,
        permissionExperience: true,
        permissionMarketing: false,
        permissionPerformance: true,
      });
    });
  });
});
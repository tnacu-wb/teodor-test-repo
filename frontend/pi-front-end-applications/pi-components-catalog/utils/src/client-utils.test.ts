// Import AFTER mocks
import { downloadFromS3PreSignedUrl } from './client-utils';

/**
 * @jest-environment node
 */

// Mock dependencies BEFORE importing client-utils
jest.mock('./utils/tailwind', () => ({
  cn: jest.fn(),
}));

jest.mock('./utils/i18nClient', () => ({
  TranslationProvider: jest.fn(),
  useTranslation: jest.fn(),
}));

jest.mock('./hoc', () => ({
  RolesRequired: jest.fn(),
}));

jest.mock('./formatters/getPathForLocale', () => ({
  getPathForLocale: jest.fn(),
}));

jest.mock('./server/formatters', () => ({
  formatIBAssetsUrl: jest.fn(),
  sanitize: jest.fn(),
  findError: jest.fn(),
  getSavedCardType: jest.fn(),
  ParseDateToYMD: jest.fn(),
  parseRegistrationQuestions: jest.fn(),
  formatHDPUrl: jest.fn(),
  capitalizeFirstLetter: jest.fn(),
  getLabelType: jest.fn(),
  getStyling: jest.fn(),
  resolveAndDownloadBlob: jest.fn(),
  formatAccountNumber: jest.fn(),
  mapSwitchState: jest.fn(),
  getDefaultSwitchState: jest.fn(),
  getVariant: jest.fn(),
  normalizeAddress: jest.fn(),
  parseAnswersObj: jest.fn(),
}));

jest.mock('./server/validators', () => ({
  isPIBACardType: jest.fn(),
}));

jest.mock('./server/getters/labels', () => ({
  getInitials: jest.fn(),
}));

jest.mock('./server/getters', () => ({
  getLocaleByPathname: jest.fn(),
  getCountryLanguageByLocale: jest.fn(),
}));

describe('downloadFromS3PreSignedUrl', () => {
  let mockAnchor: Partial<HTMLAnchorElement>;
  let createElementSpy: jest.Mock;
  let appendChildSpy: jest.Mock;
  let removeChildSpy: jest.Mock;
  let clickSpy: jest.Mock;

  beforeEach(() => {
    // Create a mock anchor element
    clickSpy = jest.fn();
    mockAnchor = {
      href: '',
      download: '',
      click: clickSpy,
    };

    // Create mock document object
    createElementSpy = jest.fn().mockReturnValue(mockAnchor);
    appendChildSpy = jest.fn();
    removeChildSpy = jest.fn();

    // Mock global document object - define it properly so it's accessible in the module scope
    const documentMock = {
      createElement: createElementSpy,
      body: {
        appendChild: appendChildSpy,
        removeChild: removeChildSpy,
      },
    };

    Object.defineProperty(global, 'document', {
      value: documentMock,
      writable: true,
      configurable: true,
    });

    jest.spyOn(console, 'warn').mockImplementation();
  });

  afterEach(() => {
    jest.restoreAllMocks();
    delete (global as any).document;
  });

  it('should create an anchor element with correct attributes and trigger download', () => {
    const testUrl = 'https://s3.amazonaws.com/test-bucket/file.pdf?signature=abc123';
    const testFileName = 'invoice.pdf';

    downloadFromS3PreSignedUrl(testUrl, testFileName);

    // Verify anchor element was created
    expect(createElementSpy).toHaveBeenCalledWith('a');

    // Verify anchor attributes were set correctly
    expect(mockAnchor.href).toBe(testUrl);
    expect(mockAnchor.download).toBe(testFileName);

    // Verify anchor was added to DOM, clicked, and removed
    expect(appendChildSpy).toHaveBeenCalledWith(mockAnchor);
    expect(clickSpy).toHaveBeenCalledTimes(1);
    expect(removeChildSpy).toHaveBeenCalledWith(mockAnchor);
  });

  it('should handle different file types and URLs', () => {
    const testUrl = 'https://example.com/path/to/document.xlsx?token=xyz';
    const testFileName = 'report.xlsx';

    downloadFromS3PreSignedUrl(testUrl, testFileName);

    expect(mockAnchor.href).toBe(testUrl);
    expect(mockAnchor.download).toBe(testFileName);
    expect(clickSpy).toHaveBeenCalled();
  });

  it('should warn and return early when document is undefined (SSR environment)', () => {
    // Remove document to simulate SSR
    delete (global as any).document;

    const consoleWarnSpy = jest.spyOn(console, 'warn');

    downloadFromS3PreSignedUrl('https://example.com/file.pdf', 'file.pdf');

    // Verify warning was logged
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      'downloadFromS3PreSignedUrl can only be called in a browser environment'
    );

    // Verify no DOM manipulation occurred
    expect(createElementSpy).not.toHaveBeenCalled();
    expect(appendChildSpy).not.toHaveBeenCalled();
    expect(clickSpy).not.toHaveBeenCalled();
  });
});

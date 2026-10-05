import { Locales } from './locales';

export interface StringBaseData {
  default?: string;
  de?: string;
  en?: string;
  uat?: string;
  uat_de?: string;
  demo?: string;
  demo_de?: string;
  node?: string | Array<string | number>;
  aemType?: string;
  [key: string]: unknown;
}

/**
 * String base class containing UI text strings mapped for each language.
 */
export class StringBase {
  private static LABELS_DICTIONARY: Record<string, unknown> | null = null; // cached AEM labels dictionary
  private static AEM_TYPE: string | null = null; // cached aem type

  constructor(readonly data: StringBaseData = {}) {}

  /** Get the string value for the current locale language. */
  get name(): Promise<string> {
    return (async () => {
      const { ApiDictionary } = await import('../api/aem/apiDictionary');

      // Get current locale from browser options or environment, default to 'gb-en'
      const localeString = String(
        (global.browser?.options as Record<string, unknown> | undefined)?.locale ??
        'gb-en'
      );
      
      // Parse locale to get language code (e.g., 'gb-en' -> 'en', 'de-de' -> 'de')
      const tokens = localeString.toLowerCase().split('-');
      const language = tokens[1] ?? 'en';
      
      // Get environment if available
      const environment = String(
        (global.browser?.options as Record<string, unknown> | undefined)?.env ??
        ''
      );
    
    // Handle AEM node-based lookups
    if (this.data.node) {
      const regex = /<([^>]+)>|\n|&nbsp;/g;
      let resultLabel: string | null = null;
      
      try {
        switch (this.data.aemType) {
          case 'generic':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchGenericDictionary(language);
            }
            resultLabel = this.navigateNode(StringBase.LABELS_DICTIONARY);
            break;
            
          case 'searchResults':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchSearchResultsDictionary(language);
            }
            resultLabel = this.navigateNode(StringBase.LABELS_DICTIONARY);
            break;
            
          case 'payment':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchBookingDictionary(language);
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'bookings':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchDashboardLabels();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ancillaries':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchAncillariesDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibLayout':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessLayoutDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibCardManagement':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessCardManagementDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibCompanyManagement':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessCompanyManagementDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibUserManagement':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessUserManagementDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibHeader':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessHeaderDictionary();
            }
            resultLabel = this.navigateNode(StringBase.LABELS_DICTIONARY);
            break;
            
          case 'ibProfileManagement':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessProfileManagementDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibHome':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessHomeDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibSpendingAndReporting':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessSpendingAndReportingDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibAuth':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessAuthDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibNotifications':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessNotificationsDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibIcons':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessIconsDictionary();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibPayApplication':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessPayApplication();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          case 'ibContactUs':
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchInnBusinessContactUs();
            }
            resultLabel = String(this.navigateNode(StringBase.LABELS_DICTIONARY) ?? '').replace(regex, '');
            break;
            
          default:
            if (StringBase.AEM_TYPE !== this.data.aemType || StringBase.LABELS_DICTIONARY === null) {
              StringBase.LABELS_DICTIONARY = await ApiDictionary.fetchLabelsDictionary(language);
            }
            resultLabel = this.navigateNode(StringBase.LABELS_DICTIONARY);
            if (!resultLabel) {
              resultLabel = this.getDefaultValue(language, environment);
            }
        }
        
        StringBase.AEM_TYPE = this.data.aemType ?? null;
        if (resultLabel) {
          return resultLabel;
        }
      } catch (error) {
        throw new Error(`Failed to fetch AEM dictionary for ${this.data.aemType ?? 'labels'}: ${error}`);
      }
    }
    
    // Fall back to static string values
    return this.getDefaultValue(language, environment);
    })();
  }

  /**
   * Navigate through the node path to get the value from the dictionary.
   * Handles both dot-notation strings and array paths.
   */
  private navigateNode(dictionary: Record<string, unknown> | null): string | null {
    if (!dictionary || !this.data.node) {
      return null;
    }

    if (typeof this.data.node === 'string' && dictionary[this.data.node] !== undefined) {
      return String(dictionary[this.data.node]);
    }

    let current: unknown = dictionary;
    const nodePath = Array.isArray(this.data.node) 
      ? this.data.node 
      : String(this.data.node).split('.');

    for (const key of nodePath) {
      if (current && typeof current === 'object') {
        current = (current as Record<string, unknown>)[String(key)];
      } else {
        return null;
      }
    }

    return current ? String(current) : null;
  }

  /**
   * Get the default value based on language and environment.
   */
  private getDefaultValue(language: string, environment: string): string {
    // Try to get environment-language specific value first
    if (environment) {
      const envLanguageKey = `${environment}_${language}`;
      if (this.data[envLanguageKey] !== undefined) {
        return String(this.data[envLanguageKey]);
      }
      // Try environment-only value (for 'en' locale)
      if (language === 'en' && this.data[environment] !== undefined) {
        return String(this.data[environment]);
      }
    }
    
    // Try language-specific value
    if (this.data[language] !== undefined) {
      return String(this.data[language]);
    }
    
    // Fall back to default
    return String(this.data.default ?? '');
  }

  static fromData(data: StringBaseData): StringBase {
    return new StringBase(data);
  }
}

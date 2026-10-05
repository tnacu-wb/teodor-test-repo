/* eslint-disable prettier/prettier */
import { config } from '@WB-playwright/config';
import {
  fetchLabelsDictionary,
  fetchGenericDictionary,
  fetchInnBusinessLayoutDictionary,
  getCountry,
  fetchInnBusinessUserManagementDictionary,
  fetchInnBusinessCardManagementDictionary,
} from '@WB-playwright/utils';

export type Strings = {
  default: string;
  de?: string;
  node?: string[];
  aemType?: string;
};

const Strings: Record<string, Strings> = {
  ACCESSIBLE_GENERIC: { default: 'Accessible', de: 'Barrierefrei', node: ['content', 'global', 'accessible'], aemType: 'generic' },
  ACTIVE: { default: 'Active', node: ['cardMgmt.cardStatus.options.active'], aemType: 'ibCardManagement' },
  ACTIVATE: { default: 'Activate', node: ['cardMgmt.cardStatus.options.activate'], aemType: 'ibCardManagement' },
  ADD_NEW_CARD: { default: 'Add a new card', de: 'Neue Karte hinzufügen' },
  ADULT_LOWER_CASE: { default: 'adult', de: 'Erwachsener',  node: ['content', 'global', 'adult'], aemType: 'generic' },
  ADULTS_LOWER_CASE: { default: 'adults', de: 'Erwachsene', node: ['content', 'global', 'adults'], aemType: 'generic' },
  CANCELLED: { default: 'Cancelled', de: 'Storniert', node: ['cardMgmt.cardStatus.options.cancelled'], aemType: 'ibCardManagement' },
  CANCELLED_CARDS: { default: 'Cancelled cards', de: 'Stornierte Karten' },
  CARD_HOLDER: { default: 'Card holder', de: 'Karteninhaber' },
  CARD_HOLDER_REGISTRATION: { default: 'Card holder reg.', de: 'Karteninhaber-Reg.' },
  CARD_NO: { default: 'Card no.', de: 'Kartennummer' },
  CARD_MANAGEMENT: { default: 'Card management', de: 'Kartenverwaltung', node: ['innbusinessLayout.menu.manage.options.cards'], aemType: 'ibLayout' },
  CARD_STATUS: { default: 'Card status', de: 'Kartenstatus'},
  CENTRALLY_STORED: { default: 'Centrally Stored', de: 'Zentral gespeichert', node: ['cardMgmt.tabs.centrallyStored'], aemType: 'ibCardManagement' },
  CHILD_LOWER_CASE: { default: 'child', de: 'Kind', node: ['content', 'global', 'child'], aemType: 'generic' },
  DISPATCHING: { default: 'Dispatching', de: 'In Zustellung', node: ['cardMgmt.cardStatus.options.dispatching'], aemType: 'ibCardManagement'  },
  DOUBLE_GENERIC: { default: 'Double', de: 'Doppel', node: ['content', 'global', 'double'], aemType: 'generic' },
  DOWNLOAD: { default: 'Download (.csv)', de: 'Download (.csv)'},
  FAMILY_GENERIC: { default: 'Family', de: 'Familie', node: ['content', 'global', 'family'], aemType: 'generic' },
  INCLUDE_COT: { default: 'Include a cot?', de: 'Babybett hinzufügen?', node: ['content', 'form', 'includeCot'], aemType: 'generic' },
  ONLY_MY_CARDS: { default: 'Only my cards', de: 'Nur meine Karten' },
  ROLE_SUPER: { default: 'Travel manager', de: 'Reisemanager', node: ['userMgmt.manageEmployees.accountRole.TravelManager'], aemType: 'ibUserManagement' },
  ROOM_LOWER_CASE: { default: 'room', de: 'Zimmer', node: ['content', 'global', 'room'], aemType: 'generic' },
  SINGLE_GENERIC: { default: 'Single', de: 'Einzel', node: ['content', 'global', 'single'], aemType: 'generic' },
  SHOW: { default: 'Show:', de: 'Anzeigen:' },
  TOOLTIP_MESSAGE: {
    default:
      'Choose the account role that best reflects your employee:Travel Manager: Full access. Can manage employee accounts, make and view bookings for all employees, download reports, use and manage stored payment cards, update company info, and apply for an InnBusiness Pay account.Booker: Can book for any employee, view and edit bookings made by them, download reports, and use stored payment cards.Self-Booker: Can book for themselves, view and edit their own bookings, download reports, and use stored payment cards.Guest: View-only access to their own booking details.',
    de: 'Wählen Sie die Kontorolle, die Ihren Mitarbeiter am besten widerspiegelt:Reisemanager: Vollzugriff. Kann Mitarbeiterkonten verwalten, Buchungen für alle Mitarbeiter vornehmen und anzeigen, Berichte herunterladen, gespeicherte Zahlungskarten verwenden und verwalten, Unternehmensinformationen aktualisieren und ein InnBusiness Pay-Konto beantragen.Bucher: Kann für jeden Mitarbeiter buchen, von ihm vorgenommene Buchungen anzeigen und bearbeiten, Berichte herunterladen und gespeicherte Zahlungskarten verwenden.Selbstbucher: Kann für sich selbst buchen, eigene Buchungen einsehen und bearbeiten, Berichte herunterladen und gespeicherte Zahlungskarten verwenden.Gast: Nur Anzeigezugriff auf seine eigenen Buchungsdetails.',
    node: ['userMgmt.manageEmployees.employeeManagement.tooltipMsg'],
    aemType: 'ibUserManagement',
  },
  TWIN_GENERIC: { default: 'Twin', de: 'Zweibettzimmer', node: ['content', 'global', 'twin'], aemType: 'generic' },
  WHERE_TO: {default: 'Where to?',de: 'Wohin geht es?',node: ['content', 'form', 'where'],aemType: '' },
  YOUR_CARD: { default: 'Your card', de: 'Ihre Karte' },
} as const;

type stringKeys = keyof typeof Strings;
let LABELS_DICTIONARY: Record<string, any> | null = null; // the cached AEM labels dictionary
let AEM_TYPE: string | null = null; // the aem type of the dictionary

// Initialize the AEM dictionaries based on aemType
async function initializeLabelsDictionary(aemType: string): Promise<void> {
  if (!LABELS_DICTIONARY || AEM_TYPE !== aemType) {
    switch (aemType) {
      case 'generic':
        LABELS_DICTIONARY = await fetchGenericDictionary(
          config.LANGUAGE,
          getCountry(config.LANGUAGE)
        );
        break;
      case 'ibLayout':
        LABELS_DICTIONARY = await fetchInnBusinessLayoutDictionary(config.LANGUAGE);
        break;
      case 'ibUserManagement':
        LABELS_DICTIONARY = await fetchInnBusinessUserManagementDictionary(config.LANGUAGE);
        break;
      case 'ibCardManagement':
        LABELS_DICTIONARY = await fetchInnBusinessCardManagementDictionary(config.LANGUAGE);
        break;
      default:
        LABELS_DICTIONARY = await fetchLabelsDictionary(config.LANGUAGE);
    }
    AEM_TYPE = aemType;
  }
}

export async function getLabel(labelKey: stringKeys): Promise<string> {
  const labelData = Strings[labelKey];

  // Initialize the dictionary based on AEM type
  if (labelData.aemType) await initializeLabelsDictionary(labelData.aemType);

  // Get the default or localized label value
  const defaultString = config.LANGUAGE === 'de' ? labelData.de : labelData.default;

  // If node is defined, get the dictionary value
  if (labelData.node && LABELS_DICTIONARY) {
    const regex = /<([^>]+)>|\n|&nbsp;/g;
    const nodeKey = labelData.node ? labelData.node.join('.') : '';
    const dictionaryValue = LABELS_DICTIONARY[nodeKey];
    return dictionaryValue ? dictionaryValue.replace(regex, '') : defaultString;
  }
  return defaultString || '';
}

export const Labels = new Proxy(Strings, {
  get: (_target, labelKey: string) => {
    return getLabel(labelKey as stringKeys);
  },
});

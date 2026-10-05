import { type StringBase } from '../stringBase';
import { IbStrings } from './ibStrings';

/** Card shown on the Inn Business confirmation page. */
export interface ConfirmationPageCard {
  /** Localized string for the card title. */
  title: StringBase;
  /** Localized string for the card description. */
  description: StringBase;
  /** Localized string for the card icon. */
  icon: StringBase;
  /** Localized string for the card action button. */
  button: StringBase;
}

/**
 * The cards form IB conformation page as an enum
 */
export class ConfirmationPageCards {
  private constructor() {}

  static readonly APPLY_FOR_IB_PAY: ConfirmationPageCard = {
    title: IbStrings.AUTH_CONFIRMATION_OPTION_1_TITLE,
    description: IbStrings.AUTH_CONFIRMATION_OPTION_1_DESCRIPTION,
    icon: IbStrings.AUTH_CONFIRMATION_OPTION_1_ICON,
    button: IbStrings.AUTH_CONFIRMATION_OPTION_1_BUTTON,
  };
  static readonly TAKE_HOME_TOUR: ConfirmationPageCard = {
    title: IbStrings.AUTH_CONFIRMATION_OPTION_2_TITLE,
    description: IbStrings.AUTH_CONFIRMATION_OPTION_2_DESCRIPTION,
    icon: IbStrings.AUTH_CONFIRMATION_OPTION_2_ICON,
    button: IbStrings.AUTH_CONFIRMATION_OPTION_2_BUTTON,
  };
  static readonly ADD_CARD: ConfirmationPageCard = {
    title: IbStrings.AUTH_CONFIRMATION_OPTION_3_TITLE,
    description: IbStrings.AUTH_CONFIRMATION_OPTION_3_DESCRIPTION,
    icon: IbStrings.AUTH_CONFIRMATION_OPTION_3_ICON,
    button: IbStrings.AUTH_CONFIRMATION_OPTION_3_BUTTON,
  };
  static readonly ADD_EMPLOYEE: ConfirmationPageCard = {
    title: IbStrings.AUTH_CONFIRMATION_OPTION_4_TITLE,
    description: IbStrings.AUTH_CONFIRMATION_OPTION_4_DESCRIPTION,
    icon: IbStrings.AUTH_CONFIRMATION_OPTION_4_ICON,
    button: IbStrings.AUTH_CONFIRMATION_OPTION_4_BUTTON,
  };

  /**
   * Get ConfirmationPageCard object based on 'title' property
   * @param confirmationPageCardTitle card title
   * @returns ConfirmationPageCard object
   */
  static async getCardByTitle(confirmationPageCardTitle: string): Promise<ConfirmationPageCard> {
    for (const card of Object.values(ConfirmationPageCards)) {
      if (typeof card === 'object' && 'title' in card && await card.title.name === confirmationPageCardTitle) {
        return card;
      }
    }
    throw new Error(`${confirmationPageCardTitle} card is not found in the list!`);
  }

  static async getConfirmationPageCardByTitleKey(titleKey: string): Promise<ConfirmationPageCard> {
    return ConfirmationPageCards.getCardByTitle(titleKey);
  }
}

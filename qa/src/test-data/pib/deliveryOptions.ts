import { IbStrings } from './ibStrings';

/** Delivery option used in Create InnBusiness Pay Card page */
export interface DeliveryOption {
  name: Promise<string>;
  buttonSelected: boolean;
  optionAvailable: boolean;
}

/** Delivery option state combinations used when validating card delivery choices. */
export class DeliveryOptions {
  private constructor() {}

  static readonly COMPANY_REGISTERED_ADDRESS_SELECTED_AND_AVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_REGISTERED_ADDRESS.name, buttonSelected: true, optionAvailable: true };
  static readonly COMPANY_REGISTERED_ADDRESS_NOT_SELECTED_AND_AVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_REGISTERED_ADDRESS.name, buttonSelected: false, optionAvailable: true };
  static readonly COMPANY_REGISTERED_ADDRESS_NOT_SELECTED_AND_UNAVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_REGISTERED_ADDRESS.name, buttonSelected: false, optionAvailable: false };
  static readonly COMPANY_CORRESPONDENCE_ADDRESS_SELECTED_AND_AVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_CORRESPONDENCE_ADDRESS.name, buttonSelected: true, optionAvailable: true };
  static readonly COMPANY_CORRESPONDENCE_ADDRESS_NOT_SELECTED_AND_AVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_CORRESPONDENCE_ADDRESS.name, buttonSelected: false, optionAvailable: true };
  static readonly COMPANY_CORRESPONDENCE_ADDRESS_NOT_SELECTED_AND_UNAVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_CORRESPONDENCE_ADDRESS.name, buttonSelected: false, optionAvailable: false };
  static readonly CARDHOLDER_ADDRESS_SELECTED_AND_AVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_CARDHOLDER_ALTERNATIVE_ADDRESS.name, buttonSelected: true, optionAvailable: true };
  static readonly CARDHOLDER_ADDRESS_NOT_SELECTED_AND_AVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_CARDHOLDER_ALTERNATIVE_ADDRESS.name, buttonSelected: false, optionAvailable: true };
  static readonly CARDHOLDER_ADDRESS_NOT_SELECTED_AND_UNAVAILABLE: DeliveryOption = { name: IbStrings.SEND_CARD_TO_CARDHOLDER_ALTERNATIVE_ADDRESS.name, buttonSelected: false, optionAvailable: false };
}

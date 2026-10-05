import { UpdatePreferencesRequest } from '@whitbread-eos/api';
import { z } from 'zod';

export type CompanyAddressPayload = {
  countryCode: string;
  line1: string;
  line2?: string;
  line3?: string;
  line4?: string;
  line5?: string;
  postCode?: string;
};

export type InnBRegistrationStepOnePayload = {
  companyName: string;
  email: string;
  address: CompanyAddressPayload;
  language: string;
  socialMediaId?: string;
  socialMediaType?: string;
  uniqueTaxpayerReference?: string;
  companyType?: string;
  updatePreferencesRequest?: UpdatePreferencesRequest;
};

export type MarketingPreferencesPayload = {
  brandCodes: string[];
  optIn: boolean;
  doubleOptIn: boolean;
  language?: string;
  title?: string;
  customerId?: string;
  countryOfResidence?: string;
  firstName?: string;
  lastName?: string;
  channel: string;
  journey?: string;
  locale?: string;
};

export const registerSchema = (t: (key: string) => string) => {
  return z.object({
    emailAddress: z
      .string()
      .regex(
        RegExp(
          /^[a-z0-9]+(?:[._'-][a-z0-9]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/i
        ),
        {
          message: t('users.userMgmt.employee.edit.error.emailFormat'),
        }
      ),
    companyName: z
      .string()
      .min(2, {
        message: t('company.coMngt.companyInfo.company.name.required'),
      })
      .max(100, {
        message: t('company.coMngt.companyInfo.company.name.required'),
      })
      .regex(
        RegExp(
          /^[a-zÀÁÂÃÄÅĀẶĄẮÆǼẞÇĆĈĊČĎĐÈÉÊËĒĔĖĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİĴĶĹĻĽĿŁÑŃŅŇȠÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽàáâãäåāặąắæǽßçćĉċčďđèéêëēĕėěĝğġģĥħìíîïĩīǐįĵķĺļľŀłñńņňƞòóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž\u005B\u005D\u005C\u002F\u00AB\u00BB\u2018.,:;_!?"“”’*%=+£$€¥&@#()\-'\d{}>< ]+$/gi
        ),
        {
          message: t('company.coMngt.companyInfo.company.name.required'),
        }
      ),
    uniqueTaxpayerReference: z.string().optional(),
    socialMediaType: z
      .object({
        displayValue: z.string(),
        value: z.string(),
      })
      .optional(),
    socialMediaValue: z.string().optional(),
    selectAddress: z
      .object({
        displayValue: z.string(),
        value: z.string(),
      })
      .optional(),
  });
};

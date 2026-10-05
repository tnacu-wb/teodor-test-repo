import { z } from 'zod';

export const companyNameSchema = (t: any, variant = 'default') => {
  const errorMessage =
    variant === 'payApp'
      ? t('payApplication.payapp.memorable.error')
      : t('company.coMngt.companyInfo.company.name.required');

  return z.object({
    companyName: z
      .string()
      .min(1, {
        message: errorMessage,
      })
      .max(100, { message: errorMessage })
      .regex(
        RegExp(
          /^[a-zÀÁÂÃÄÅĀẶĄẮÆǼẞÇĆĈĊČĎĐÈÉÊËĒĔĖĚĜĞĠĢĤĦÌÍÎÏĨĪǏĮİĴĶĹĻĽĿŁÑŃŅŇȠÒÓÔÕÖØŌǑŐǾŒṘŖŘŚŜṢŠŢŤŦÙÚÛÜŨŪǓŮŰŲŴẂẀẄỲÝŸŶŹŻŽàáâãäåāặąắæǽßçćĉċčďđèéêëēĕėěĝğġģĥħìíîïĩīǐįĵķĺļľŀłñńņňƞòóôõöøōǒőǿœṙŗřśŝṣšţťŧùúûüũūǔůűųŵẃẁẅỳýÿŷźżž\u005B\u005D\u005C\u002F\u00AB\u00BB\u2018.,:;_!?"“”’*%=+£$€¥&@#()\-'\d{}>< ]+$/gi
        ),
        {
          message: errorMessage,
        }
      ),
  });
};

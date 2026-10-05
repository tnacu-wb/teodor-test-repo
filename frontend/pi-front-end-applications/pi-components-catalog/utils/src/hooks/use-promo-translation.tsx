import { useTranslation } from 'next-i18next';

export default function usePromoTranslation() {
  const { t } = useTranslation();

  return {
    // Expiry / OTP modal
    expiresTimerText: t('promotions.expires.timer'),
    oneTimePasswordOpenFileText: t('promotions.oneTimePassword.openFile'),
    oneTimePasswordExpiredMessage: t('promotions.expired.message'),

    // Voucher / Promo code validation
    voucherPrefixValidationError: t('promotions.valid.voucherPrefix'),
    voucherPrefixRequiredError: t('promotions.voucher.required'),
    promoCodeRequiredError: t('promotions.required'),
    promoCodeValidationError: t('promotions.valid'),
    promoCodeLengthValidationError: t('promotions.promoCode.valid'),
    prefixRequiredError: t('promotions.prefix.required'),
    promoCodeValidation: t('promotions.valid'),
    promotionsAlreadyUsedError: t('promotions.alreadyUsed'),
    promotionsInvalidError: t('promotions.invalidCode'),
    genericFilterErros: t('genericpromo.filter.error'),
    genericDatesError: t('genericpromo.dates.error'),

    // Hotel ID
    hotelIdLabel: t('promotions.hotelID.label'),
    hotelIdHint: t('promotions.hotelID.hint'),
    hotelIdPlaceholder: t('promotions.hotelID.placeholder'),
    hotelIdRequiredError: t('promotions.hotelID.required'),

    // Titles / headers
    requestedByTitle: t('promotions.requestedBy'),
    amountTitle: t('promotions.amount'),
    voucherPrefixTitle: t('promotions.prefix.check'),
    promoCodeHeader: t('promotions.promoCode.header'),
    promoCodeTableHeader: t('promotions.table.promoCode.header'),
    campaignTitle: t('promotions.campaign.title'),
    createdAtTitle: t('promotions.created'),
    channelLabel: t('promotions.channel.label'),
    channelStatusLabel: t('promotions.channel.status'),

    // Batch / status
    firstBatchInfoMessage: t('promotions.firstBatch.info'),
    batchProcessingMessage: t('promotions.batch.processing'),
    batchSuccessMessage: t('promotions.batch.success'),
    codeExpiredMessage: t('promotions.code.expired'),
    batchErrorMessage: t('promotions.batch.error'),
    awaitingFirstBatchMessage: t('promotions.awaitBatch.placeholder'),
    batchFailedStatus: t('promotions.batchFailed'),
    statusComplete: t('promotions.status.complete'),
    statusPending: t('promotions.status.pending'),
    statusRunning: t('promotions.status.running'),
    statusExporting: t('promotions.status.export'),
    statusFailed: t('promotions.status.failed'),

    // Empty / misc
    noActiveBatchesMessage: t('promotions.no.activeBatches'),
    showHiddenText: t('promotions.showHidden'),

    // Download
    downloadTitle: t('promotions.table.download'),
    downloadButtonText: t('promotions.download.button'),
    downloadHideText: t('promotions.download.hide'),
    downloadFileText: t('promotions.download.fileText'),
    accessFileInstruction: t('promotions.access.file'),

    // Campaign / form
    campaignNameLabel: t('promotions.campaign.name'),
    campaignNameRequiredError: t('promotions.campaign.required'),
    campaignNameValidationError: t('promotions.valid.campaignName'),
    formTitle: t('promotions.form.title'),
    duplicatePrefixError: t('promotions.duplicate.prefix'),

    // Dates
    startDateLabel: t('promotions.startDate'),
    endDateLabel: t('promotions.endDate'),
    endDateInvalidError: t('promotions.endDate.invalid'),

    // Create / generate
    generateButtonText: t('promotions.generate'),
    generateNewBatchText: t('promotions.generate.newBatch'),
    generatingCodePlaceholder: t('promotions.generateCode.placeholder'),
    createNewBatchTitle: t('promotions.create.batch'),

    // Buttons
    cancelButtonText: t('promotions.cancel.button'),
    cancelAndReturnText: t('promotions.cancel.return'),

    // Channels
    channelWebLabel: t('promotions.channel.checkbox1'),
    channelAppLabel: t('promotions.channel.checkbox2'),
    channelCcuiLabel: t('promotions.channel.checkbox3'),
    channelInnBusinessLabel: t('promotions.channel.checkbox4'),
    channelPi: t('genericpromo.channel.pi'),
    channelPib: t('genericpromo.channel.pib'),

    // Vouchers
    vouchersNeededLabel: t('promotions.vouchers.needed'),
    vouchersCountRequiredError: t('promotions.voucher.count.required.error'),
    vouchersCountInvalidError: t('promotions.voucher.count.invalid.error'),
    vouchersRequired: t('promotions.voucher.required'),
    vouchersValidation: t('promotions.valid.voucherPrefix'),

    // Notes
    notesTitle: t('promotions.notes'),
    notesLineOne: t('promotions.notes.line1'),
    notesLineTwo: t('promotions.notes.line2'),
    notesLineThree: t('promotions.notes.line3'),

    // Tags
    includeRateTagText: t('promotions.include.tag'),

    // Batch list
    promoBatchesTitle: t('promotions.batch.title'),
    promoCodeFormLabel: t('promotions.promoCode.form'),

    // generic promo labels/Text
    countryGermanyLabel: t('genericpromo.country.de'),
    countryUnitedLondonLabel: t('genericpromo.country.uk'),
    platformConfigurationTitle: t('genericpromo.platform.title'),
    selectedLabel: t('genericpromo.selected.label'),
    platformLabel: t('genericpromo.platform.label'),
    configurePlatformText: t('genericpromo.platform.configure'),

    // generic Input labels
    isGenericLabel: t('genericpromo.title'),
    genericPromoCodeLabel: t('genericpromo.placeholder'),
    limitRedemptionsLabel: t('genericpromo.limit.redemption'),
    maximumRedemptionsLabel: t('genericpromo.max.redemption'),
    regionLabel: t('genericpromo.region.label'),
    eligibilityLabel: t('genericpromo.eligibility.label'),

    // generic promo notes
    genericpromoNotesOne: t('genericpromo.notes1'),
    genericpromoNotesTwo: t('genericpromo.notes2'),
    genericpromoNotesThree: t('genericpromo.notes3'),

    // generic promo errors
    genericPromoInvalidError: t('gernericpromo.filter.error'),
  };
}

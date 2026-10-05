export enum UserRoles {
  MANAGER = 'ROLE-G-SC-CCUI-Manager',
  AGENT = 'ROLE-G-SC-CCUI-Agent',
  PROMO_ADMIN = 'ROLE-G-SC-CCUI-UAT-PROMOADMIN',
  PROMO_ADMIN_PROD = 'ROLE-G-SC-CCUI-PROD-PROMOADMIN',
}

export enum UserAccessLevels {
  SUPER = 'SUPER', // travel manager user
  BOOKER = 'BOOKER', // booker user
  SELF = 'SELF', // self-booker user
  STAYER = 'STAYER', // guest user
  BUSINESS_PAY_MANAGER = 'BUSINESS_PAY_MANAGER', // business pay manager user
  BUSINESS_PAY_USER = 'BUSINESS_PAY_USER', // business pay user
}

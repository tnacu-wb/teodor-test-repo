/**
 * {
    "data": {
        "getWorldlineUserPreferences": [
            {
                "tetheredUserGuid": "70ffaa91-c655-4e53-ad34-5381895ac9de",
                "settings": [
                    {
                        "smsTypeId": 1,
                        "isSmsSelected": true,
                        "smsTypeDescription": "Account nearing credit limit",
                        "smsType": "SMS001"
                    },
                    {
                        "smsTypeId": 2,
                        "isSmsSelected": true,
                        "smsTypeDescription": "Account on stop",
                        "smsType": "SMS002"
                    },
                    {
                        "smsTypeId": 3,
                        "isSmsSelected": true,
                        "smsTypeDescription": "Card credit limit reached",
                        "smsType": "SMS003"
                    },
                    {
                        "smsTypeId": 4,
                        "isSmsSelected": true,
                        "smsTypeDescription": "Invoice ready for review",
                        "smsType": "SMS004"
                    },
                    {
                        "smsTypeId": 8,
                        "isSmsSelected": true,
                        "smsTypeDescription": "Account resumed",
                        "smsType": "SMS008"
                    }
                ],
                "details": {
                    "showSmsStopsToCardholder": null,
                    "sendCardsToCardholder": null,
                    "accountName": "I",
                    "roleId": null,
                    "accountNumber": 100391,
                    "roleDescription": "Account Holder"
                }
            }
        ]
    }
}
 */
export class WorldlineUserPreferences {
  [key: string]: unknown;
  details?: Record<string, unknown>;
  settings?: Array<{ smsTypeId?: number; isSmsSelected?: boolean; smsTypeDescription?: string; smsType?: string }>;

  /**
   * Worldline user preferences
   * @param data data
   * @param data.userPreferences Worldline user preferences
   */
  constructor(data: { userPreferences?: Record<string, unknown> } = {}) {
    const userPreferences = data.userPreferences ?? {};
    this.settings = userPreferences.settings as WorldlineUserPreferences['settings'];
    this.details = userPreferences.details as Record<string, unknown> | undefined;
  }

  static fromResponse(data: { userPreferences?: Record<string, unknown> }): WorldlineUserPreferences {
    return new WorldlineUserPreferences(data);
  }
}
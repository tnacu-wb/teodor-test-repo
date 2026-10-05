package contracts.oauth.status_200.PermissionManagementApi

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get newsletter preferences with Azure Authentication")
    request {
        method 'GET'
        url '/marketing/newsletter/email/channel/CHNL39288_28a5d984-7782-4e93-99a5-b2e9f8a746f3?brandCodes=PINN'
        headers {
            header('Content-Type', 'application/json')
        }
    }
        response {
            status 200
            body('''
                {
                    "permissions": [
                        {
                            "brandCode": "PINN",
                            "brand": "Premier Inn",
                            "optIn": true,
                            "2ndOptInReq": false,
                            "2ndOptIn": false,
                            "secondPartyOptIn": true,
                            "thirdPartyVendorsOptIn": true
                        }
                    ],
                    "valid" : true,
                    "loyaltyAccounts": [
                        {
                            "loyaltyBrand": "Brewers Fayre",
                            "loyaltySystemId": "987293"
                        }
                    ],
                    "deleted" : false
                }
        ''')
            headers {
                header('Content-Type', 'application/json')
            }
    }
}
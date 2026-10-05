package contracts.oauth.status_200.CustomerHub

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Get newsletter preferences with Azure Authentication")
    request {
        method 'POST'
        url '/marketing/hotels/newsletter/get'
        headers {
            header('Content-Type', 'application/json')
        }
        body '''
        {
            "requestId": "fsytdfxusdgcilugasld",
            "contactChannel": 
                {
                    "contactChannelType": "Email",
                    "contactChannelValue": "test@gmail.com"
                },
            "brandCodes": [
                "PINN", 
                "WINN"
            ]
        }
        '''
    }
    response {
        status 200
        body('''
                {
                    "contactChannelId": "CHNL12047_bjghsdu_dahbj_dsjhbj",
                    "contactChannelValue": "email@email.com",
                    "brandPermissions": [
                        {
                            "brandCode": "PINN",
                            "brand": "Premier Inn",
                            "optIn": true,
                            "2ndOptInReq": false,
                            "2ndOptIn": false,
                            "contentPermission": {
                                "2ndParty": true,
                                "3rdParty": true
                            },
                            "lastOptInDate": "2019-11-20T18:08:15.323",
                            "lastSourceBusinessKey": "ACE_EditPermissions_business_key",
                            "lastModifiedBy": "name.surname"
                        }
                    ],
                    "shared": false,
                    "valid" : true,
                    "loyaltyAccounts": [
                        {
                            "loyaltyBrand": "Brewers Fayre",
                            "loyaltySystemId": "987293"
                        }
                    ],
                    "modified": "2019-12-03T09:50:54.0633726Z",
                    "deleted" : false
                }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
package contracts.success

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should update newsletter preferences")
    request {
        method 'PUT'
        url '/marketing/hotels/newsletter/customer-id-test'
        headers {
            header('Content-Type', 'application/json')
            header('authenticationKey', 'auth-key-test')
        }
        body '''
        {
          "correlationId": "1984000",
          "subscriptions": [
            {
              "contactType": "Email",
              "contactValue": "test@gmail.com",
              "subscribe": "false",
              "brandCode": "PINN"
            }
          ]
        }
        '''
    }

    response {
        status 204
    }
}
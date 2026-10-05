import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should logout from current session")
    request {
        method 'POST'
        url '/auth/hotels/logout'
        body(
            '''
           {
              "sessionId": "qpQzrHMfVsJcOhSM"
            }
            '''
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }

    response {
        status 200
        body('''
        {
          "logoutSuccessful": true
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
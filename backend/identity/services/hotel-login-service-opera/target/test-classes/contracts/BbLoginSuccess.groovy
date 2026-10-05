import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should login in Business Booker")
    request {
        method 'POST'
        url '/auth/hotels/login?business=true'
        body(
            '''
           {
                "username": "myuser@whitbread.com",
                "password": "securePa$$w0rd"
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
            "sessionId": "123456",
            "loginSuccessful": true
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
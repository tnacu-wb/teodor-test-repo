import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should initSession secured")
    request {
        method 'POST'
        url '/auth/session?business=false'
        body(
                '''
           {
              "guestHistoryNumber": "136e97d9f612e8407fda1c6a378e7e40Fib2j5GRY6Tq9rqvQ9rk9g=="
           }
            '''
        )
        headers {
            header('Content-Type', 'application/json')
            header('X-Forwarded-For', '52.28.45.240')
        }
    }

    response {
        status 200
        body(
                "sessionId": "uI2EHi0nuG9agu3G"
        )
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
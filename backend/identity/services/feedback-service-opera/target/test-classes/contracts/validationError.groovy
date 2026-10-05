import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should fail creating pending reservation")
    request {
        method 'POST'
        url '/feedback'
        body(
                '''
            {}

            '''
        )
        headers {
            header('Content-Type', 'application/json')
            header('Accept', 'application/json')
        }
    }

    response {
        status 400
        body('''
        {  
           "code":"001",
           "details":[  
                "whb_wffeedbacktype must not be null",
                "whb_reasonforcontact must not be null",
                "title must not be blank",
                "whb_wflastname must not be blank",
                "whb_wfemailaddress must not be blank",
                "whb_summary must not be blank",
                "whb_contacttype must not be null",
                "whb_wffirstname must not be blank",
                "whb_wfididntbookthroughpremierinncom must not be null",
                "whb_wfreasonforfeedback must not be blank",
                "whb_wfpostcode must not be blank",
                "whb_wfcontactnumber must not be blank"
           ]
        }
        ''')
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
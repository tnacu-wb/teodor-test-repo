import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should fail creating pending reservation")
    request {
        method 'POST'
        url '/feedback'
        body(
                '''
            {
              "whb_wfsource": "https://www.whitbreadinns.co.uk",
              "title": "Failing Feedback Form",
              "whb_contacttype": 130570009,
              "whb_reasonforcontact": 2,
              "whb_wfbookingrefifapplicable": "AB12345",
              "whb_wfcontactnumber": "+447724547605",
              "whb_wfdateofstayifapplicable": "2018-02-14",
              "whb_wfemailaddress": "dimitris.damilos@whitbread.com",
              "whb_summary": "This was a weird hotel",
              "whb_wffeedbacktype": 130570000,
              "whb_wffirstname": "Walter",
              "whb_wfhotelname": "ABEMAI",
              "whb_wfididntbookthroughpremierinncom": false,
              "whb_wflastname": "White",
              "whb_wfpostcode": "AB1 2CD",
              "whb_wfreasonforfeedback": "Restaurant/Bar experience",
              "whb_wftypeofvisit": "Dinner",
              "whb_loyaltycardnumber": "my-loyalty-card-num",
              "whb_wfchecknumber": "check-number",
              "whb_wfsleep": false,
              "whb_wfreported": true
            }

            '''
        )
        headers {
            header('Content-Type', 'application/json')
            header('Accept', 'application/json')
        }
    }

    response {
        status 500
        headers {
            header('Content-Type', 'application/json')
        }
    }
}
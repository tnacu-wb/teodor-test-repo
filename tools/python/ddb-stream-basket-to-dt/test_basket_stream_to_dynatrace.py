import unittest
from unittest import mock

from basket_stream_to_dynatrace import Shard, extract_relevant_fields, shard_watcher

class TestExtractRelevantFields(unittest.TestCase):

    def test_extract_relevant_fields_insert_completed_no_error(self):
        # Arrange
        record = {'eventID': '0a23f01c-ebf7-4cce-9367-10a5fc77e2ea', 'eventName': 'INSERT', 'eventVersion': '1.1', 
                  'eventSource': 'aws:dynamodb', 'awsRegion': 'ddblocal', 'dynamodb': {
                      'Keys': {'threeLetterHotelId': {'S': 'BFM'}, 'sortKey': {'S': 'BFM-4000eb3a-c021-4ffe-9c4c-fdd5e6b338ce'}},
                       'NewImage': {'basketId': {'S': 'BFM-4000eb3a-c021-4ffe-9c4c-fdd5e6b338ce'}, 
                                    'itemTypes': {'L': [{'M': {'confirmationData': {'L': [{'S': 'sourceId'}, {'S': 'hotelId'}]}, 
                                                               'type': {'S': 'STAY'}}}]}, 'threeLetterHotelId': {'S': 'BFM'}, 
                                    'lastModifiedAt': {'S': '2025-02-12T20:42:19Z'}, 'channel': {'S': 'PI'}, 
                                    'sendMail': {'BOOL': True}, 'hotelId': {'S': 'LONBRO'}, 'paymentChannel': {'S': 'PI'}, 
                                    'reference': {'S': 'BFM3815973'}, 'createdAt': {'S': '2025-02-12T20:42:17Z'}, 
                                    'emailAddress': {'S': 'alex.morgan@example.com'}, 
                                    'sortKey': {'S': 'BFM-4000eb3a-c021-4ffe-9c4c-fdd5e6b338ce'}, 
                                    'paymentID': {'S': '23528216122D'}, 'subChannel': {'S': 'WEB'}, 
                                    'paymentOption': {'S': 'PAY_NOW'}, 'currency': {'S': 'GBP'}, 
                                    'lockingTime': {'S': '2025-02-12T20:42:19Z'}, 'cleanUpTime': {'N': '1802507439'}, 
                                    'items': {'L': [{'M': {'reqAction': {'S': 'COMMIT'}, 'sourceId': {'S': '184947438'}, 
                                                           'hasOccupancySup': {'BOOL': False}, 'ack': {'N': '0'}, 
                                                           'details': {'NULL': True}, 'type': {'S': 'STAY'}}}]}, 
                                    'totalCost': {'S': '21000'}, 'paymentStatus': {'S': 'COMPLETED'}, 
                                    'status': {'S': 'COMPLETED'}}, 
                       'SequenceNumber': '000000000000000001013', 'SizeBytes': 658, 'StreamViewType': 'NEW_IMAGE'}}
        # Act
        result = extract_relevant_fields(record)
        # Assert
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        self.assertEqual(result["event.type"], "WEB.COMPLETED")
        self.assertEqual(result["basketId"], "BFM-4000eb3a-c021-4ffe-9c4c-fdd5e6b338ce")
        self.assertEqual(result["reference"], "BFM3815973")
        self.assertEqual(result["paymentID"], "23528216122D")
        self.assertEqual(result["subChannel"], "WEB")
        self.assertEqual(result["totalCost"], 21000)

    def test_extract_relevant_fields_with_error(self):
        # Arrange
        record = {'eventID': 'b327403c-3bd4-47c9-9250-620d0692d559', 'eventName': 'MODIFY', 'eventVersion': '1.1', 
                  'eventSource': 'aws:dynamodb', 'awsRegion': 'ddblocal', 'dynamodb': {
                      'Keys': {'threeLetterHotelId': {'S': 'BFM'}, 'sortKey': {'S': '00452ab9-dbe1-493b-9006-8d28b61446b0'}}, 
                      'NewImage': {'basketId': {'S': 'BFM-00452ab9-dbe1-493b-9006-8d28b61446b0'}, 
                                   'itemTypes': {'L': [{'M': {'confirmationData': {'L': [{'S': 'hotelId'}, {'S': 'sourceId'}]}, 
                                                              'type': {'S': 'STAY'}}}]}, 
                                   'threeLetterHotelId': {'S': 'BFM'}, 'lastModifiedAt': {'S': '2025-02-13T16:39:14Z'}, 
                                   'basketError': {'M': {'code': {'S': 'A fraud check was triggered and no payment has been made'}, 
                                                         'description': {'S': 'fraud_check_failed'}, 'type': {'S': 'PAYMENT'}}}, 
                                   'retryPayment': {'BOOL': True}, 'channel': {'S': 'PI'}, 'sendMail': {'BOOL': False}, 
                                   'hotelId': {'S': 'LONBRO'}, 'paymentChannel': {'S': 'APPS_IOS'}, 
                                   'reference': {'S': 'BFM1693842'}, 'createdAt': {'S': '2025-02-13T16:39:13Z'}, 
                                   'emailAddress': {'S': 'sam.taylor@example.org'}, 
                                   'sortKey': {'S': '00452ab9-dbe1-493b-9006-8d28b61446b0'}, 'paymentID': {'S': '88715433818D'}, 
                                   'paymentOption': {'S': 'PAY_NOW'}, 'subChannel': {'S': 'MOBILE'}, 'currency': {'S': 'GBP'}, 
                                   'lockingTime': {'S': '2025-02-13T16:39:14Z'}, 'cleanUpTime': {'N': '1771022964'}, 
                                   'items': {'L': [{'M': {'reqAction': {'NULL': True}, 'sourceId': {'S': '185479645'}, 
                                                          'hasOccupancySup': {'BOOL': False}, 'ack': {'NULL': True}, 
                                                          'details': {'NULL': True}, 'type': {'S': 'STAY'}}}]}, 
                                   'totalCost': {'S': '13600'}, 'status': {'S': 'FAILED'}}, 
                      'SequenceNumber': '000000000000000001016', 'SizeBytes': 758, 'StreamViewType': 'NEW_IMAGE'}}
        # Act
        result = extract_relevant_fields(record)
        # Assert
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        self.assertEqual(result["event.type"], "MOBILE.FAILED")
        self.assertEqual(result["basketId"], "BFM-00452ab9-dbe1-493b-9006-8d28b61446b0")
        self.assertEqual(result["errorCode"], "A fraud check was triggered and no payment has been made")
        self.assertEqual(result["errorDescription"], "fraud_check_failed")
        self.assertEqual(result["errorType"], "PAYMENT")
        self.assertEqual(result["paymentOption"], "PAY_NOW")
        self.assertEqual(result["totalCost"], 13600)

    def test_extract_relevant_fields_remove_event(self):
        """Test REMOVE event type"""
        record = {
            'eventID': 'test-remove-id',
            'eventName': 'REMOVE',
            'eventVersion': '1.1',
            'eventSource': 'aws:dynamodb',
            'awsRegion': 'us-east-1',
            'dynamodb': {
                'Keys': {'threeLetterHotelId': {'S': 'BFM'}, 'sortKey': {'S': 'test-basket-id'}},
                'SequenceNumber': '000000000000000001',
                'SizeBytes': 100,
                'StreamViewType': 'NEW_IMAGE'
            }
        }
        result = extract_relevant_fields(record)
        # Should still return base fields even without NewImage
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        # No event.type since there's no status field
        self.assertNotIn("event.type", result)

    def test_extract_relevant_fields_missing_new_image(self):
        """Test record with no NewImage"""
        record = {
            'eventID': 'test-id',
            'eventName': 'INSERT',
            'dynamodb': {}
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        # No event.type since there's no status field
        self.assertNotIn("event.type", result)
        # Should have the two base fields: event.provider and build.timestamp
        self.assertEqual(len(result), 2)
        self.assertIn("build.timestamp", result)

    def test_extract_relevant_fields_empty_new_image(self):
        """Test record with empty NewImage"""
        record = {
            'eventID': 'test-id',
            'eventName': 'MODIFY',
            'dynamodb': {
                'NewImage': {}
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        # No event.type since there's no status field
        self.assertNotIn("event.type", result)
        # Should have the two base fields: event.provider and build.timestamp
        self.assertEqual(len(result), 2)
        self.assertIn("build.timestamp", result)

    def test_extract_relevant_fields_partial_data(self):
        """Test record with only some fields populated"""
        record = {
            'eventID': 'test-partial',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-partial-test'},
                    'status': {'S': 'OPEN'},
                    'reference': {'S': 'BFM1234567'}
                    # Missing many other fields
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        self.assertEqual(result["event.type"], "null.OPEN")
        self.assertEqual(result["basketId"], "BFM-partial-test")
        self.assertEqual(result["reference"], "BFM1234567")
        # Should not have fields that weren't in the record
        self.assertNotIn("paymentID", result)
        self.assertNotIn("totalCost", result)
        self.assertNotIn("errorCode", result)

    def test_extract_relevant_fields_decimal_total_cost(self):
        """Test totalCost with decimal format like '14999.00'"""
        record = {
            'eventID': 'test-decimal',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-decimal-test'},
                    'totalCost': {'S': '14999.00'}
                }
            }
        }
        result = extract_relevant_fields(record)
        # Should attempt int conversion but keep as string if it fails
        self.assertEqual(result["totalCost"], "14999.00")

    def test_extract_relevant_fields_numeric_string_conversion(self):
        """Test numeric strings are converted to int when possible"""
        record = {
            'eventID': 'test-numeric',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-numeric-test'},
                    'totalCost': {'S': '25000'}
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["totalCost"], 25000)
        self.assertIsInstance(result["totalCost"], int)

    def test_extract_relevant_fields_status_open(self):
        """Test basket with OPEN status (no payment completed)"""
        record = {
            'eventID': 'test-open',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-open-basket'},
                    'status': {'S': 'OPEN'},
                    'reference': {'S': 'BFM9999999'},
                    'createdAt': {'S': '2025-12-03T10:00:00Z'}
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["event.type"], "null.OPEN")
        self.assertEqual(result["basketId"], "BFM-open-basket")
        self.assertNotIn("paymentID", result)
        self.assertNotIn("paymentStatus", result)

    def test_extract_relevant_fields_status_cancelled(self):
        """Test basket with CANCELLED status"""
        record = {
            'eventID': 'test-cancelled',
            'eventName': 'MODIFY',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-cancelled-basket'},
                    'status': {'S': 'CANCELLED'},
                    'reference': {'S': 'BFM8888888'},
                    'paymentID': {'S': '12345678901D'},
                    'totalCost': {'S': '18600'}
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["event.type"], "null.CANCELLED")
        self.assertEqual(result["basketId"], "BFM-cancelled-basket")
        self.assertEqual(result["paymentID"], "12345678901D")
        self.assertEqual(result["totalCost"], 18600)

    def test_extract_relevant_fields_status_pay_pending(self):
        """Test basket with PAY_PENDING status"""
        record = {
            'eventID': 'test-pending',
            'eventName': 'MODIFY',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-pending-basket'},
                    'status': {'S': 'PAY_PENDING'},
                    'reference': {'S': 'BFM7777777'},
                    'paymentID': {'S': '99999999999D'},
                    'totalCost': {'S': '7900'}
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["event.type"], "null.PAY_PENDING")
        self.assertEqual(result["totalCost"], 7900)

    def test_extract_relevant_fields_no_pii_in_output(self):
        """Verify that PII (email addresses) are NOT extracted"""
        record = {
            'eventID': 'test-pii',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-pii-test'},
                    'reference': {'S': 'BFM1111111'},
                    'emailAddress': {'S': 'customer@example.com'},
                    'totalCost': {'S': '15000'}
                }
            }
        }
        result = extract_relevant_fields(record)
        # emailAddress should NOT be in the result (not in fields list)
        self.assertNotIn("emailAddress", result)
        # But other fields should be present
        self.assertEqual(result["basketId"], "BFM-pii-test")
        self.assertEqual(result["reference"], "BFM1111111")
        self.assertEqual(result["totalCost"], 15000)

    def test_extract_relevant_fields_multiple_error_types(self):
        """Test different error types beyond PAYMENT"""
        record = {
            'eventID': 'test-system-error',
            'eventName': 'MODIFY',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-system-error'},
                    'status': {'S': 'FAILED'},
                    'basketError': {
                        'M': {
                            'code': {'S': 'SYSTEM_ERROR'},
                            'description': {'S': 'Internal system failure'},
                            'type': {'S': 'SYSTEM'}
                        }
                    }
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["errorCode"], "SYSTEM_ERROR")
        self.assertEqual(result["errorDescription"], "Internal system failure")
        self.assertEqual(result["errorType"], "SYSTEM")

    def test_extract_relevant_fields_missing_error_fields(self):
        """Test basketError with missing nested fields"""
        record = {
            'eventID': 'test-incomplete-error',
            'eventName': 'MODIFY',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-incomplete-error'},
                    'status': {'S': 'FAILED'},
                    'basketError': {
                        'M': {
                            'code': {'S': 'UNKNOWN_ERROR'}
                            # Missing description and type
                        }
                    }
                }
            }
        }
        result = extract_relevant_fields(record)
        self.assertEqual(result["errorCode"], "UNKNOWN_ERROR")
        # Should handle missing nested fields gracefully

    def test_extract_relevant_fields_null_error_subfields(self):
        """basketError sub-fields that are NULL-typed should be skipped, not emitted as None."""
        record = {
            'eventID': 'test-null-error',
            'eventName': 'MODIFY',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-null-error'},
                    'status': {'S': 'FAILED'},
                    'basketError': {
                        'M': {
                            'code': {'NULL': True},          # non-string attribute
                            'description': {'S': 'something failed'},
                            'type': {'NULL': True},          # non-string attribute
                        }
                    }
                }
            }
        }
        result = extract_relevant_fields(record)
        # NULL-typed sub-fields must not appear at all (no None values)
        self.assertNotIn("errorCode", result)
        self.assertNotIn("errorType", result)
        # The valid string sub-field is still extracted
        self.assertEqual(result["errorDescription"], "something failed")

    def test_extract_relevant_fields_null_values(self):
        """Test fields with NULL type in DynamoDB"""
        record = {
            'eventID': 'test-nulls',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-null-test'},
                    'status': {'S': 'OPEN'},
                    'paymentID': {'NULL': True},  # NULL type field
                    'totalCost': {'S': '10000'}
                }
            }
        }
        result = extract_relevant_fields(record)
        # NULL fields should be skipped (no 'S' key)
        self.assertNotIn("paymentID", result)
        self.assertEqual(result["basketId"], "BFM-null-test")
        self.assertEqual(result["totalCost"], 10000)

    def test_extract_relevant_fields_all_channels(self):
        """Test different channel values"""
        channels = [
            ('PI', 'WEB'),
            ('PI', 'MOBILE'),
            ('DISTR', 'AGENCY'),
            ('BB', None)
        ]
        for channel, sub_channel in channels:
            with self.subTest(channel=channel, sub_channel=sub_channel):
                new_image = {
                    'basketId': {'S': f'BFM-{channel}-test'},
                    'channel': {'S': channel}
                }
                if sub_channel:
                    new_image['subChannel'] = {'S': sub_channel}
                
                record = {
                    'eventID': f'test-{channel}',
                    'eventName': 'INSERT',
                    'dynamodb': {'NewImage': new_image}
                }
                result = extract_relevant_fields(record)
                self.assertEqual(result["channel"], channel)
                if sub_channel:
                    self.assertEqual(result["subChannel"], sub_channel)

    def test_extract_relevant_fields_payment_options(self):
        """Test different paymentOption values"""
        payment_options = ['PAY_NOW', 'PAY_ON_ARRIVAL', 'PAY_LATER']
        for option in payment_options:
            with self.subTest(paymentOption=option):
                record = {
                    'eventID': f'test-{option}',
                    'eventName': 'INSERT',
                    'dynamodb': {
                        'NewImage': {
                            'basketId': {'S': f'BFM-{option}-test'},
                            'paymentOption': {'S': option},
                            'totalCost': {'S': '20000'}
                        }
                    }
                }
                result = extract_relevant_fields(record)
                self.assertEqual(result["paymentOption"], option)

    def test_extract_relevant_fields_all_extracted_fields_present(self):
        """Test record with all extractable fields populated"""
        record = {
            'eventID': 'test-complete',
            'eventName': 'INSERT',
            'dynamodb': {
                'NewImage': {
                    'basketId': {'S': 'BFM-complete-basket'},
                    'type': {'S': 'BOOKING'},
                    'threeLetterHotelId': {'S': 'BFM'},
                    'lastModifiedAt': {'S': '2025-12-03T12:00:00Z'},
                    'channel': {'S': 'PI'},
                    'hotelId': {'S': 'LONBRO'},
                    'subChannel': {'S': 'WEB'},
                    'reference': {'S': 'BFM5555555'},
                    'createdAt': {'S': '2025-12-03T11:00:00Z'},
                    'paymentID': {'S': '11111111111D'},
                    'paymentOption': {'S': 'PAY_NOW'},
                    'currency': {'S': 'GBP'},
                    'totalCost': {'S': '50000'},
                    'paymentStatus': {'S': 'COMPLETED'},
                    'status': {'S': 'COMPLETED'}
                }
            }
        }
        result = extract_relevant_fields(record)
        
        # Verify all fields are present
        self.assertEqual(result["event.provider"], "whit.pi.basket")
        self.assertEqual(result["event.type"], "WEB.COMPLETED")
        self.assertEqual(result["basketId"], "BFM-complete-basket")
        self.assertEqual(result["type"], "BOOKING")
        self.assertEqual(result["threeLetterHotelId"], "BFM")
        self.assertEqual(result["lastModifiedAt"], "2025-12-03T12:00:00Z")
        self.assertEqual(result["channel"], "PI")
        self.assertEqual(result["hotelId"], "LONBRO")
        self.assertEqual(result["subChannel"], "WEB")
        self.assertEqual(result["reference"], "BFM5555555")
        self.assertEqual(result["createdAt"], "2025-12-03T11:00:00Z")
        self.assertEqual(result["paymentID"], "11111111111D")
        self.assertEqual(result["paymentOption"], "PAY_NOW")
        self.assertEqual(result["currency"], "GBP")
        self.assertEqual(result["totalCost"], 50000)
        self.assertEqual(result["paymentStatus"], "COMPLETED")
        # status field is now mapped to event.type, not included separately

class TestShardWatcher(unittest.TestCase):
    """Behavioural tests for shard_watcher (SRE-359 findings 1, 2, 3)."""

    def _make_shard(self):
        return Shard(
            stream_arn="arn:aws:dynamodb:eu-west-1:000000000000:table/test/stream/x",
            shard_id="shard-1",
            parent_shard_id=None,
            starting_sequence_number="1",
            ending_sequence_number=None,
        )

    def _record(self, status="OPEN"):
        return {
            "eventID": "e1",
            "eventName": "INSERT",
            "dynamodb": {
                "NewImage": {"basketId": {"S": "BFM-x"}, "status": {"S": status}},
                "SequenceNumber": "1",
            },
        }

    def test_shutdown_after_five_consecutive_failures(self):
        """Finding 2: shutdown fires on the 5th consecutive failure, not the 6th."""
        shard = self._make_shard()
        shutdown_event = mock.Mock()
        shutdown_event.is_set.return_value = False

        with mock.patch("basket_stream_to_dynatrace.boto3"), \
             mock.patch("basket_stream_to_dynatrace.get_shard_iterator", return_value="it-0"), \
             mock.patch("basket_stream_to_dynatrace.get_next_records", return_value=([self._record()], "it-next")), \
             mock.patch("basket_stream_to_dynatrace.post_to_dynatrace", return_value=False) as post, \
             mock.patch("basket_stream_to_dynatrace.time.sleep"):
            shard_watcher(shard, log_level=40, dt_env="whitbread-non-prod", shutdown_event=shutdown_event)

        # 5 attempts, all failing, then break — not 6.
        self.assertEqual(post.call_count, 5)
        shutdown_event.set.assert_called_once()

    def test_error_count_resets_after_success(self):
        """Finding 1: a success resets the consecutive-failure counter."""
        shard = self._make_shard()
        shutdown_event = mock.Mock()
        shutdown_event.is_set.return_value = False

        # fail x4, success (reset), fail x4, then a success that ends the loop
        # (its next iterator is None). A non-resetting counter would have hit 5
        # and shut down; with the reset it never reaches the threshold.
        post_results = [False, False, False, False, True, False, False, False, False, True]
        # Terminate the loop on the final success by returning a None next iterator then.
        next_iters = ["it"] * 9 + [None]

        with mock.patch("basket_stream_to_dynatrace.boto3"), \
             mock.patch("basket_stream_to_dynatrace.get_shard_iterator", return_value="it-0"), \
             mock.patch("basket_stream_to_dynatrace.get_next_records",
                        side_effect=[([self._record()], nxt) for nxt in next_iters]), \
             mock.patch("basket_stream_to_dynatrace.post_to_dynatrace", side_effect=post_results) as post, \
             mock.patch("basket_stream_to_dynatrace.time.sleep"):
            shard_watcher(shard, log_level=40, dt_env="whitbread-non-prod", shutdown_event=shutdown_event)

        # All 10 posts were attempted (loop never tripped the shutdown threshold).
        self.assertEqual(post.call_count, 10)
        shutdown_event.set.assert_not_called()

    def test_empty_new_image_record_is_still_posted(self):
        """Finding 3: a record with an explicitly empty NewImage still yields a summary and is posted (not dropped)."""
        shard = self._make_shard()
        shutdown_event = mock.Mock()
        shutdown_event.is_set.return_value = False
        # Explicit empty NewImage mapping (not merely a missing key) — the case
        # a REMOVE / no-attributes event produces.
        empty_record = {"eventID": "e1", "eventName": "MODIFY", "dynamodb": {"NewImage": {}}}

        with mock.patch("basket_stream_to_dynatrace.boto3"), \
             mock.patch("basket_stream_to_dynatrace.get_shard_iterator", return_value="it-0"), \
             mock.patch("basket_stream_to_dynatrace.get_next_records", return_value=([empty_record], None)), \
             mock.patch("basket_stream_to_dynatrace.post_to_dynatrace", return_value=True) as post, \
             mock.patch("basket_stream_to_dynatrace.time.sleep"):
            shard_watcher(shard, log_level=40, dt_env="whitbread-non-prod", shutdown_event=shutdown_event)

        post.assert_called_once()
        posted_summaries = post.call_args.args[0]
        self.assertEqual(len(posted_summaries), 1)
        self.assertEqual(posted_summaries[0]["event.provider"], "whit.pi.basket")


if __name__ == "__main__":
    unittest.main()

#! /usr/bin/env python
import argparse
import collections
import multiprocessing as mp
import time
import typing
import logging
import os
import sys
import signal
from datetime import datetime, timezone

import requests
import boto3
from botocore.config import Config
import re

# Build timestamp from container build (via file) or fallback to module load time
def _get_build_timestamp() -> str:
    """Read build timestamp from file (set during Docker build) or generate at runtime."""
    try:
        with open("/build-timestamp.txt") as f:
            return f.read().strip()
    except (FileNotFoundError, IOError):
        return datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M:%S %Z")

BUILD_TIMESTAMP = _get_build_timestamp()

Shard = collections.namedtuple(
    typename="Shard",
    field_names=[
        "stream_arn",
        "shard_id",
        "parent_shard_id",
        "starting_sequence_number",
        "ending_sequence_number"
    ]
)

def shard_watcher(shard: Shard, log_level: int, dt_env: str, start_at_oldest = False, shutdown_event=None):
    """
    Reads events from each shard, summarising the output and POSTing to Dynatrace.
    """

    # Set up logging in the child process
    logging.basicConfig(
        level=log_level,
        format='%(asctime)s %(levelname)s %(processName)s %(message)s',
    )
    logger = logging.getLogger("ddb_stream.shard_watcher")
    # Suppress noisy boto3/botocore logs
    logging.getLogger("boto3").setLevel(logging.WARNING)
    logging.getLogger("botocore").setLevel(logging.WARNING)

    # Create reusable boto3 client with retry configuration
    retry_config = Config(
        retries={
            'max_attempts': 5,
            'mode': 'adaptive'
        },
        max_pool_connections=50
    )
    streams_client = boto3.client("dynamodbstreams", config=retry_config)

    logger.info("Watcher started for shard: %s", shard.shard_id)
    shard_iterator_type = "TRIM_HORIZON" if start_at_oldest else "LATEST"
    shard_iterator = get_shard_iterator(shard, shard_iterator_type, streams_client)
    
    error_count = 0

    while shard_iterator is not None:
        if shutdown_event is not None and shutdown_event.is_set():
            logger.info("Shutdown event set; stopping watcher for shard %s", shard.shard_id)
            break
        # Only advance the iterator if posting to Dynatrace succeeds.
        records, next_shard_iterator = get_next_records(shard_iterator, streams_client)

        logger.debug("Shard %s got %d records", shard.shard_id, len(records))
        # extract_relevant_fields always returns at least the base envelope
        # (event.provider/build.timestamp), so every record yields a summary.
        summaries = [extract_relevant_fields(record) for record in records]
        for summary in summaries:
            logger.debug("Incoming event summarised to: %s", summary)

        if summaries:
            logger.info("Posting %d summaries to Dynatrace", len(summaries))
            if post_to_dynatrace(summaries, dt_env):
                # Only advance iterator if post succeeded
                error_count = 0  # reset on success: threshold counts *consecutive* failures
                shard_iterator = next_shard_iterator
                if shutdown_event is not None and shutdown_event.is_set():
                    logger.info("Shutdown event set after posting; stopping watcher for shard %s", shard.shard_id)
                    break
            else:
                logger.error("Failed to post summaries to Dynatrace; will retry same records.")
                error_count += 1
                if error_count >= 5:
                    logger.error("Too many errors posting to Dynatrace (%d consecutive). Signaling parent to shutdown.", error_count)
                    if shutdown_event is not None:
                        shutdown_event.set()
                    break
                # Do not advance iterator; retry same batch next loop
                time.sleep(2 ** error_count)  # Exponential backoff
        else:
            # No records this poll; just advance iterator
            shard_iterator = next_shard_iterator
            time.sleep(0.5)
    
def validate_dt_env(dt_env: str) -> str:
    """
    Validate the Dynatrace environment name to avoid using arbitrary hostnames.

    Returns the validated environment string, or raises ValueError if invalid.
    """
    if not isinstance(dt_env, str):
        raise ValueError("Dynatrace environment name must be a string.")
    dt_env = dt_env.strip()
    if not dt_env:
        raise ValueError("Dynatrace environment name must not be empty.")
    # Allow only alphanumeric characters and dashes to prevent host header / URL manipulation
    if not re.fullmatch(r"[A-Za-z0-9\-]+", dt_env):
        raise ValueError(
            f"Invalid Dynatrace environment name '{dt_env}'. "
            "Only letters, numbers, and dashes are allowed."
        )
    return dt_env

def sanitize_log_message(message: typing.Any, max_length: int = 1000) -> str:
    """
    Sanitize potentially untrusted data before logging to reduce risk of log injection.

    - Coerce to string.
    - Remove carriage returns and newlines so the log entry stays on a single line.
    - Truncate very long messages to avoid log flooding.
    """
    text = str(message)
    text = text.replace("\r", "").replace("\n", "")
    if len(text) > max_length:
        text = text[:max_length] + "...[truncated]"
    return text

def post_to_dynatrace(summaries: typing.List[dict], dt_env: str) -> bool:
    """
    POST summaries to Dynatrace Business Events API.
    """
    logger = logging.getLogger("ddb_stream.post_to_dynatrace")
    if dt_env == "fail":
        logger.error("Simulated failure posting to Dynatrace.")
        return False
    api_key = os.environ.get("DT_API_KEY")
    if api_key == "localtest":
        logger.info("I would have posted %d summaries to Dynatrace as follows: %s", len(summaries), summaries)
        return True
    if not api_key:
        logger.error("DT_API_KEY environment variable not set. Cannot POST to Dynatrace.")
        return False

    headers = {
        "Authorization": f"Api-Token {api_key}",
        "Content-Type": "application/json"
    }
    try:
        response = requests.post(f"https://{dt_env}.live.dynatrace.com/api/v2/bizevents/ingest", json=summaries, headers=headers, timeout=10)
        if response.status_code >= 200 and response.status_code < 300:
            logger.info("Successfully posted %d summaries to Dynatrace.", len(summaries))
            return True
        else:
            safe_text = sanitize_log_message(response.text)
            logger.error("Dynatrace POST failed: %s %s", response.status_code, safe_text)
            return False
    except Exception as e:
        logger.error("Exception posting to Dynatrace: %s", e)
        return False

def extract_relevant_fields(record: dict) -> dict:
    """
    Extracts key fields from a DynamoDB Streams record for concise logging or processing.
    Returns a dict with the relevant fields if present in the 'NewImage'.
    """
    fields = ['basketId', 'type', 'threeLetterHotelId', 'lastModifiedAt', 'channel', 'hotelId', 'subChannel',
              'reference', 'createdAt', 'paymentID', 'paymentOption', 'currency', 'totalCost', 'paymentStatus']
    new_image = record.get("dynamodb", {}).get("NewImage")
    result: typing.Dict[str, typing.Union[str, int]] = {
        "event.provider": "whit.pi.basket",
        "build.timestamp": BUILD_TIMESTAMP
    }
    if new_image:
        for field in fields:
            data = new_image.get(field)
            if data:
                # Try to convert to int if possible, otherwise keep as string
                # Handle NULL type fields by checking for 'S' key first
                string_value = data.get('S')
                if string_value is not None:
                    try:
                        result[field] = int(string_value)
                    except (ValueError, TypeError):
                        result[field] = string_value
        # Map status field to event.type, prepended with subChannel (or channel)
        status = new_image.get('status')
        if status:
            status_value = status.get('S')
            if status_value is not None:
                # Determine prefix: use DISTR for DISTR channel, subChannel if present, or "null"
                channel_value = result.get('channel')
                subchannel_value = result.get('subChannel')
                
                if channel_value == 'DISTR':
                    prefix = channel_value
                elif subchannel_value:
                    prefix = subchannel_value
                else:
                    prefix = 'null'
                
                result['event.type'] = f"{prefix}.{status_value}"
        error = new_image.get('basketError')
        # Extract error details if present.  These are added separately (not part of fields) as the names need to be changed.  
        if error:
            error_m = error.get('M')
            if error_m:
                # Safely extract error fields. Only emit a key when the sub-field
                # is a string ('S') attribute; skip NULL/other types so we never
                # add a key with a None value (consistent with the field loop above).
                code = error_m.get('code')
                if code and code.get('S') is not None:
                    result['errorCode'] = code.get('S')
                description = error_m.get('description')
                if description and description.get('S') is not None:
                    result['errorDescription'] = description.get('S')
                error_type = error_m.get('type')
                if error_type and error_type.get('S') is not None:
                    result['errorType'] = error_type.get('S')
    return result

def start_watching(stream_arn: str, log_level: int, dt_env: str) -> None:
    """
    Starts watching the given DynamoDB stream ARN, spawning a separate process for each open shard.
    """
    shutdown_event = mp.Event()
    shard_to_watcher: typing.Dict[str, mp.Process] = {}
    initial_loop = True
    shutdown_requested = {"signal": False}

    def _handle_signal(signum, _frame):
        shutdown_requested["signal"] = True
        shutdown_event.set()
        logging.getLogger("ddb_stream.start_watching").info("Received signal %s; initiating graceful shutdown", signum)

    signal.signal(signal.SIGTERM, _handle_signal)
    signal.signal(signal.SIGINT, _handle_signal)

    while not shutdown_event.is_set():
        open_shards = list_open_shards(stream_arn=stream_arn)
        start_at_oldest = True
        if initial_loop:
            start_at_oldest = False
            initial_loop = False

        for shard in open_shards:
            if shard.shard_id not in shard_to_watcher:
                args = (shard, log_level, dt_env, start_at_oldest, shutdown_event)
                process = mp.Process(target=shard_watcher, args=args)
                shard_to_watcher[shard.shard_id] = process
                process.start()

        time.sleep(10)

    # Shutdown: allow workers to finish current batch, then terminate if needed
    for proc in shard_to_watcher.values():
        if proc.is_alive():
            proc.join(timeout=15)
    for proc in shard_to_watcher.values():
        if proc.is_alive():
            proc.terminate()
            proc.join(timeout=5)

    if shutdown_requested["signal"]:
        print("Shutdown signal received. All child processes stopped.")
        sys.exit(0)

    print("Shutdown event received. All child processes terminated.")
    sys.exit(70)

def list_all_shards(stream_arn: str, client=None, **kwargs: dict) -> typing.List[Shard]:

    def _shard_response_to_shard(response: dict) -> Shard:
        return Shard(
            stream_arn=stream_arn,
            shard_id=response.get("ShardId"),
            parent_shard_id=response.get("ParentShardId"),
            starting_sequence_number=response.get("SequenceNumberRange", {}).get("StartingSequenceNumber"),
            ending_sequence_number=response.get("SequenceNumberRange", {}).get("EndingSequenceNumber")
        )
 
    if client is None:
        retry_config = Config(retries={'max_attempts': 5, 'mode': 'adaptive'})
        client = boto3.client("dynamodbstreams", config=retry_config)
    pagination_args = {}
    exclusive_start_shard_id = kwargs.get("next_page_identifier", None)
    if exclusive_start_shard_id is not None:
        pagination_args["ExclusiveStartShardId"] = exclusive_start_shard_id
    
    response = client.describe_stream(
        StreamArn=stream_arn,
        **pagination_args
    )

    list_of_shards = [_shard_response_to_shard(item) for item in response["StreamDescription"]["Shards"]]

    next_page_identifier = response["StreamDescription"].get("LastEvaluatedShardId")
    if next_page_identifier is not None:
        list_of_shards += list_all_shards(
            stream_arn=stream_arn,
            client=client,
            next_page_identifier=next_page_identifier
        )
    
    return list_of_shards

def is_open_shard(shard: Shard) -> bool:
    return shard.ending_sequence_number is None

def list_open_shards(stream_arn: str) -> typing.List[Shard]:
    all_shards = list_all_shards(
        stream_arn=stream_arn
    )

    open_shards = [shard for shard in all_shards if is_open_shard(shard)]

    return open_shards

def get_shard_iterator(shard: Shard, iterator_type: str = "LATEST", client=None) -> str:
    if client is None:
        retry_config = Config(retries={'max_attempts': 5, 'mode': 'adaptive'})
        client = boto3.client("dynamodbstreams", config=retry_config)

    response = client.get_shard_iterator(
        StreamArn=shard.stream_arn,
        ShardId=shard.shard_id,
        ShardIteratorType=iterator_type
    )
    
    return response["ShardIterator"]

def get_next_records(shard_iterator: str, client=None) -> typing.Tuple[typing.List[dict], str]:
    if client is None:
        retry_config = Config(retries={'max_attempts': 5, 'mode': 'adaptive'})
        client = boto3.client("dynamodbstreams", config=retry_config)

    response = client.get_records(
        ShardIterator=shard_iterator
    )

    return response["Records"], response.get("NextShardIterator")

def get_latest_stream_arn(table_name: str, client=None) -> str:
    """
    Returns the LatestStreamArn for a DynamoDB table by name.
    """
    if client is None:
        retry_config = Config(retries={'max_attempts': 5, 'mode': 'adaptive'})
        client = boto3.client("dynamodb", config=retry_config)
    response = client.describe_table(TableName=table_name)
    stream_arn = response["Table"].get("LatestStreamArn")
    if not stream_arn:
        raise ValueError(f"No stream enabled for table {table_name}. Enable DynamoDB Streams on the table.")
    return stream_arn

def main():
    parser = argparse.ArgumentParser(description="Summarise the DynamoDB Stream from the basket service into Dynatrace business events.  " 
            "The Dynatrace API key must be set in the environment variable DT_API_KEY.")
    parser.add_argument("table_name", type=str, help="The name of the DynamoDB table to follow.")
    parser.add_argument("--dt-env", type=str, default="whitbread-non-prod", help="The Dynatrace environment name (default: whitbread-non-prod).")
    parser.add_argument("--debug", "-d", action="store_true", help="Enable seriously noisy debug logging.")
    parser.add_argument("--log-level", type=str, default=None, 
                        help="Set log level: DEBUG, INFO, WARNING, ERROR, CRITICAL (overrides --debug). Can also be set via LOG_LEVEL env var.")
    parsed = parser.parse_args()

    # Set up logging at the requested level
    # Priority: --log-level flag > LOG_LEVEL env var > --debug flag > INFO (default)
    if parsed.log_level:
        log_level = getattr(logging, parsed.log_level.upper(), logging.INFO)
    elif os.environ.get("LOG_LEVEL"):
        log_level = getattr(logging, os.environ.get("LOG_LEVEL", "").upper(), logging.INFO)
    elif parsed.debug:
        log_level = logging.DEBUG
    else:
        log_level = logging.INFO

    try:
        stream_arn = get_latest_stream_arn(parsed.table_name)
    except Exception as e:
        print(f"Error getting stream ARN for table {parsed.table_name}: {e}")
        sys.exit(1)

    try:
        validated_dt_env = validate_dt_env(parsed.dt_env)
    except ValueError as e:
        print(f"Invalid Dynatrace environment value for --dt-env: {e}")
        sys.exit(2)

    # Try a post to DT with the stream ARN to make sure it works and exit if not.
    if validated_dt_env != "fail" and not post_to_dynatrace(
            [{"event.provider":"whit.pi.basket", "event.type":"watcher-started", "stream_arn": stream_arn, "build.timestamp": BUILD_TIMESTAMP}],
            validated_dt_env):
        print("Error posting to Dynatrace. Check DT_API_KEY environment variable and DT_ENV parameter.")
        sys.exit(70)

    start_watching(stream_arn, log_level, validated_dt_env)

if __name__ == "__main__":
    main()

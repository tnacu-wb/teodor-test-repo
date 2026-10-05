package uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils;

import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;

public class MemoUtils {

  public static String getMemoType(String memoTitle) {
    switch (memoTitle) {
      case OhipConstants.AGENT_NOTES_COMMENT_TITLE:
        return "AGENT";
      case OhipConstants.BUSINESS_NOTES_COMMENT_TITLE:
      case OhipConstants.SPECIAL_NOTES_COMMENT_TITLE:
        return "SYSTEM";
      default:
        return "OPERA";
    }
  }
}

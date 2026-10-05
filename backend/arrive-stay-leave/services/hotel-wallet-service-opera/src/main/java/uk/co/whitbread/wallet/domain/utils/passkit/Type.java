package uk.co.whitbread.wallet.domain.utils.passkit;

/**
 * Type is used to keep the UI details that are dynamically changed when the template is filled in.
 */
public interface Type {

  String getName();

  String getBackgroundColor();

  String getForegroundColor();

  String getLabelColor();
}
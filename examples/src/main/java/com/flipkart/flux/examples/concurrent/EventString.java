package com.flipkart.flux.examples.concurrent;

import com.flipkart.flux.client.model.Event;

/**
 * Created by kumar.vivek on 12/10/16.
 */
public class EventString implements Event {
  private String rowNo;

  public EventString(String rowNo) {
    this.rowNo = rowNo;
  }

  public String getRowNo() {
    return rowNo;
  }
}

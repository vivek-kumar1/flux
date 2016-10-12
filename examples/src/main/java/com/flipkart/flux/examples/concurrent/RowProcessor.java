package com.flipkart.flux.examples.concurrent;

import com.flipkart.flux.client.model.Task;

/**
 * Created by kumar.vivek on 12/10/16.
 */
public class RowProcessor {
  @Task(version = 0, timeout = 10000)
  public void processRow(EventString str) {
    System.out.println(Thread.currentThread().getId() + " " + str.getRowNo());
  }
}

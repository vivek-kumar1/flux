package com.flipkart.flux.examples.concurrent;

import com.flipkart.flux.client.model.Workflow;

import javax.inject.Inject;

/**
 * Created by kumar.vivek on 12/10/16.
 */
public class SampleWorkflow {
  private final RowProcessor rowProcessor;

  @Inject
  public SampleWorkflow(RowProcessor rowProcessor) {
    this.rowProcessor = rowProcessor;
  }

  @Workflow(version = 0)
  public void executeWorkflow(EventString[] row) {
    for(EventString str : row) {
      rowProcessor.processRow(str);
    }
  }
}

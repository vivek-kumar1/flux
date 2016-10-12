package com.flipkart.flux.examples.concurrent;

import com.google.inject.Guice;
import com.google.inject.Injector;

import com.flipkart.flux.client.FluxClientComponentModule;
import com.flipkart.flux.client.FluxClientInterceptorModule;

import java.util.ArrayList;

/**
 * Created by kumar.vivek on 12/10/16.
 */
public class RunWorkFlow {
  public static void main(String[] args) {
    final Injector injector = Guice.createInjector(new FluxClientComponentModule(), new FluxClientInterceptorModule());
    final SampleWorkflow sampleWorkflow = injector.getInstance(SampleWorkflow.class);
    ArrayList<EventString> rows = new ArrayList<>();
    for (int i=0; i<10; i++) {
      rows.add(new EventString("row_" + i));
    }
    sampleWorkflow.executeWorkflow(rows.toArray(new EventString[0]));
  }
}

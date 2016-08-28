/*
 * Copyright 2012-2016, the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flipkart.flux.runner;

import com.flipkart.flux.constant.RuntimeConstants;
import com.flipkart.flux.guice.module.ConfigModule;
import com.flipkart.polyguice.core.support.Polyguice;
import com.google.inject.AbstractModule;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * <code>GuiceJunit4Runner</code> provides guice injection capabilities to a test class instance
 * The present implementation works in conjunction with {@link Modules} annotation which is used to specify the
 * guice modules to load before preparing for injecting instances in the test instance
 *
 * The current implementation maintains an in memory cache that maps the set of modules chosen with the polyguice instance
 * prepared. This saves time in not having to prepare a new polyguice instance for every test, despite it using the same modules
 *
 * However, this will lead to issues where 2 tests using different "set" of modules, but ones that ultimately depend on
 * instantiation of the same class & that class impl instantiation is not idempotent (i.e we cannot have 2 impls of that class within
 * the same JVM - something that binds to a port for example)
 * This can be easily circumvented by using the union of the 2 sets in both classes.
 * //todo - add first class support so we don't have to go around this situation
 * @author shyam.akirala
 */
public class GuiceJunit4Runner extends BlockJUnit4ClassRunner {
//        polyguice.modules(configModule, new DeploymentUnitModule(), new HibernateModule(), new ContainerModule(), new TaskModule(), new FluxClientInterceptorModule(), new RuntimeTestModule(), new AkkaModule());
    static final Map<Set<Class<? extends AbstractModule>>,Polyguice> polyguiceMap = new HashMap<>();
    static final ConfigModule configModule = new ConfigModule(GuiceJunit4Runner.class.getClassLoader().getResource(RuntimeConstants.CONFIGURATION_YML));
    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws org.junit.runners.model.InitializationError if the test class is malformed.
     */
    public GuiceJunit4Runner(Class<?> klass) throws InitializationError, IOException {
        super(klass);
    }

    @Override
    protected Object createTest() throws Exception {
        final Object testInstance = super.createTest();
        final Polyguice polyInstance = getPolyInstance(testInstance);
        polyInstance.getComponentContext().inject(testInstance);
        return testInstance;
    }

    private Polyguice getPolyInstance(Object testInstance) {
        final Modules moduleAnnotation = testInstance.getClass().getAnnotation(Modules.class);
        if (moduleAnnotation == null) {
            throw new RuntimeException("You now have to supply a @Modules annotation to use GuiceJunit4Runner");
        }
        final Class<? extends AbstractModule>[] configuredModules = moduleAnnotation.value();
        final HashSet<Class<? extends AbstractModule>> moduleClassSet = new HashSet<>();
        Collections.addAll(moduleClassSet, configuredModules);
        if (!polyguiceMap.containsKey(moduleClassSet)) {
            // todo - try to reuse existing modules rather than creating new ones. May need to become a feature in PolyGuice
            final AbstractModule[] createdModules = moduleClassSet.stream()
                .filter(aClass -> aClass != ConfigModule.class)
                .map(new Function<Class<? extends AbstractModule>, AbstractModule>() {
                    @Override
                    public AbstractModule apply(Class<? extends AbstractModule> aClass) {
                        try {
                            return aClass.newInstance();
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).toArray(value -> new AbstractModule[value+1]);
            createdModules[createdModules.length-1] = configModule;
            polyguiceMap.put(moduleClassSet,
                new Polyguice().modules(createdModules).registerConfigurationProvider(configModule.getConfigProvider()).prepare());
        }
        return polyguiceMap.get(moduleClassSet);
    }
}

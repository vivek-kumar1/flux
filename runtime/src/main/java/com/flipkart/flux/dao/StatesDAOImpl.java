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

package com.flipkart.flux.dao;

import com.flipkart.flux.dao.iface.StatesDAO;
import com.flipkart.flux.domain.State;
import com.flipkart.flux.domain.Status;
import org.hibernate.Query;
import org.hibernate.SessionFactory;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * <code>StatesDAOImpl</code> is an implementation of {@link StatesDAO} which uses Hibernate to perform operations.
 * @author shyam.akirala
 */
public class StatesDAOImpl extends AbstractDAO<State> implements StatesDAO {

    @Inject
    public StatesDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    @Transactional
    public State create(State state) {
        return super.save(state);
    }

    @Override
    @Transactional
    public void updateState(State state) {
        super.update(state);
    }

    @Override
    @Transactional
    public void updateStatus(Long stateId, Long stateMachineId, Status status) {
        Query query = currentSession().createQuery("update State set status = :status where id = :stateId and stateMachineId = :stateMachineId");
        query.setString("status", status != null ? status.toString() : null);
        query.setLong("stateId", stateId);
        query.setLong("stateMachineId", stateMachineId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void updateRollbackStatus(Long stateId, Long stateMachineId, Status rollbackStatus) {
        Query query = currentSession().createQuery("update State set rollbackStatus = :rollbackStatus where id = :stateId and stateMachineId = :stateMachineId");
        query.setString("rollbackStatus", rollbackStatus != null ? rollbackStatus.toString() : null);
        query.setLong("stateId", stateId);
        query.setLong("stateMachineId", stateMachineId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void incrementRetryCount(Long stateId, Long stateMachineId) {
        Query query = currentSession().createQuery("update State set attemptedNoOfRetries = attemptedNoOfRetries + 1 where id = :stateId and stateMachineId = :stateMachineId");
        query.setLong("stateId", stateId);
        query.setLong("stateMachineId", stateMachineId);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public State findById(Long id) {
        return super.findById(State.class, id);
    }
}

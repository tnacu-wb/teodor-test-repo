package uk.co.whitbread.rules.manager.infrastructure.config;

import liquibase.exception.DatabaseException;
import liquibase.exception.LockException;
import liquibase.lockservice.StandardLockService;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is enforcing to release the lock from the database. A viable solution til the replicaset for rules-manager
 * service is set to 1, otherwise the Liquibase portion has to be moved into k8s container <a
 * href="https://kubernetes.io/docs/concepts/workloads/pods/init-containers/">init</a> phase.
 */
@Slf4j
public class ForceReleaseLockService extends StandardLockService {

  @Override
  public int getPriority() {
    return super.getPriority() + 1;
  }

  @Override
  public void waitForLock() throws LockException {
    try {
      log.info("Obtaining the lock forcefully!");
      super.forceReleaseLock();
    } catch (DatabaseException e) {
      log.error("Could not enforce getting the lock.", e);
      throw new LockException("Could not enforce getting the lock.", e);
    }
    super.waitForLock();
  }
}

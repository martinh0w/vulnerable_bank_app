package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;

import ninja.jira.skeletonkey.app.entity.BillingOrganization;

/**
 * This class sends MYSQL queries to billing organization db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface BillingOrganizationRepository extends CrudRepository<BillingOrganization, String> {
}
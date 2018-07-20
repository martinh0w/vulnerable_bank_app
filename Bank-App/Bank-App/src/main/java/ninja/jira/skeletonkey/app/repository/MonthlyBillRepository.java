package ninja.jira.skeletonkey.app.repository;

import org.springframework.data.repository.CrudRepository;
import java.util.List;

import ninja.jira.skeletonkey.app.entity.MonthlyBill;

/**
 * This class sends MYSQL queries to monthly bill db by interpreting from method names.
 * Provides default CRUD methods.
 */
public interface MonthlyBillRepository extends CrudRepository<MonthlyBill, Integer> {
    List<MonthlyBill> findMonthlyBillsByCardNumber (String cardNumber);
}
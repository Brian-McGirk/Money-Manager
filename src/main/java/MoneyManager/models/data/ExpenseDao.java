package MoneyManager.models.data;

import MoneyManager.models.Expense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
@Transactional
public interface ExpenseDao extends CrudRepository<Expense, Integer> {
}

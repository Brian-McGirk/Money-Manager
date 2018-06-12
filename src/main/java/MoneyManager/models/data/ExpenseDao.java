package MoneyManager.models.data;

import MoneyManager.models.Category;
import MoneyManager.models.Expense;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;


@Repository
@Transactional
public interface ExpenseDao extends CrudRepository<Expense, Integer> {
    public Optional<Expense> findByCategoryId(int id);
}

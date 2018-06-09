package MoneyManager.models.data;

import MoneyManager.models.Income;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;



@Repository
@Transactional
public interface IncomeDao extends CrudRepository<Income, Integer> {
}

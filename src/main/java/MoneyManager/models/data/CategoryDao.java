package MoneyManager.models.data;


import MoneyManager.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;


@Repository
@Transactional
public interface CategoryDao extends CrudRepository<Category, Integer> {
    public Category findByName(String name);
    public boolean existsByName(String name);
}



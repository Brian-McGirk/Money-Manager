package MoneyManager.models.data;

import MoneyManager.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface UserDao extends CrudRepository<User, Integer> {
    public User findByUserName(String userName);
    public boolean existsByUserName(String userName);
}

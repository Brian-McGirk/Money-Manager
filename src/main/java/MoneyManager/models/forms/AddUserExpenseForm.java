package MoneyManager.models.forms;


import MoneyManager.models.Expense;
import MoneyManager.models.User;

import javax.validation.constraints.NotNull;

public class AddUserExpenseForm {

    @NotNull
    private int userId;

    @NotNull
    private int expenseId;

    private Iterable<Expense> expenses;

    private User user;

    public AddUserExpenseForm() { }

    public AddUserExpenseForm(Iterable<Expense> expenses, User user) {
        this.expenses = expenses;
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public Iterable<Expense> getExpenses() {
        return expenses;
    }

    public User getUser() {
        return user;
    }
}

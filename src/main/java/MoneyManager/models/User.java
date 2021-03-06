package MoneyManager.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue
    private int id;

    @NotNull
    @Size(min=3, max=20, message = "Username must be 3-20 characters")
    private String userName;

    private String pw_hash;

//    private String requestedBy;

    @Transient
    @Size(min=3, max=20, message = "Password must be 5-15 characters")
    private String password;

    @Transient
    private String verifyPassword;

    @ManyToMany
    private List<Expense> expenses;

    @ManyToMany
    private List<Category> categories;

    @ManyToMany
    private List<Income> incomes;

    @ElementCollection
    private List<String> requestedBy;

    @ManyToMany
    @JoinTable(name="user_partners",
            joinColumns=@JoinColumn(name="userId"),
            inverseJoinColumns=@JoinColumn(name="partnerId")
    )
    private List<User> partners;

    @ManyToMany
    @JoinTable(name="user_partners",
            joinColumns=@JoinColumn(name="partnerId"),
            inverseJoinColumns=@JoinColumn(name="userId")
    )
    private List<User> partnersOf;


    public User() { }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPw_hash() {
        return pw_hash;
    }

    public void setPw_hash(String pw_hash) {
        this.pw_hash = pw_hash;
    }

    public List<String> getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(List<String> requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void addRequest(String name){
        requestedBy.add(name);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }


    public void setVerifyPassword(String verifyPassword) {
        this.verifyPassword = verifyPassword;

    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void addItem(Expense item){
        expenses.add(item);
    }

    public List<Category> getCategories() { return categories; }

    public List<User> getPartners() {
        return partners;
    }

    public void addPartner(User user){
        partners.add(user);
    }

    public List<User> getPartnersOf() {

        return partnersOf;
    }

    public void addPartnerOf(User user){
        partnersOf.add(user);
    }

    public void addCategory(Category category){
        categories.add(category);
    }

    public List<Income> getIncomes() { return incomes; }

    public void addIncome(Income income){incomes.add(income);}

}

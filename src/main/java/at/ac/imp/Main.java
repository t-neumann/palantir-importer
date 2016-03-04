package at.ac.imp;

import java.util.List; 
import java.util.Date;
import java.util.Iterator; 
 
import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.SessionFactory;

import javax.persistence.EntityManager;

public class Main {
  public static void main(String[] args) {
	Employee employee = new Employee();
	employee.setFirstName("Dhaka");
    employee.setLastName("Bangladesh");
    employee.setSalary(1000);

    EntityManager em = PersistenceProvider.INSTANCE.getEntityManager();
    
    em.getTransaction().begin();
    em.persist(employee);
    em.getTransaction().commit();
    em.close();
    PersistenceProvider.INSTANCE.close();
  }
}
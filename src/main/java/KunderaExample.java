import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by tulh on 14/07/2016.
 */
public class KunderaExample
{
    public static void main(String args[])
    {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("cassandra_pu");
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, "tsmith");
        System.out.println(user.toString());
        em.close();
        emf.close();
    }
}

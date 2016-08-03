import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by tulh on 14/07/2016.
 */
@Entity
@Table(name = "users", schema = "videodb@cassandra_pu")
public class User
{
    @Id
    @Column
    private String username;

    @Embedded
    private Address address;

    @Column(name = "created_date")
    private Date createdDate;

    @ElementCollection
    @Column
    private List<String> email;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column
    private String password;

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public Address getAddress()
    {
        return address;
    }

    public void setAddress(Address address)
    {
        this.address = address;
    }

    public Date getCreatedDate()
    {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate)
    {
        this.createdDate = createdDate;
    }

    public List<String> getEmail()
    {
        return email;
    }

    public void setEmail(List<String> email)
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Embeddable
    @Entity(name = "address")
    class Address
    {
        @Column
        private String street;

        @Column
        private String city;

        @Column
        private String zip;

        @ElementCollection
        private Set<Phone> phones;

        @Column
        private String location;

        public Address()
        {
        }

        public String getStreet()
        {
            return street;
        }

        public void setStreet(String street)
        {
            this.street = street;
        }

        public String getCity()
        {
            return city;
        }

        public void setCity(String city)
        {
            this.city = city;
        }

        public String getZip()
        {
            return zip;
        }

        public void setZip(String zip)
        {
            this.zip = zip;
        }

        public Set<Phone> getPhones()
        {
            return phones;
        }

        public void setPhones(Set<Phone> phones)
        {
            this.phones = phones;
        }

        public String getLocation()
        {
            return location;
        }

        public void setLocation(String location)
        {
            this.location = location;
        }
    }

    @Embeddable
    @Entity(name = "phone")
    class Phone
    {
        @Column
        private String number;

        @ElementCollection
        @Column
        private Set<String> tags;

        public String getNumber()
        {
            return number;
        }

        public void setNumber(String number)
        {
            this.number = number;
        }

        public Set<String> getTags()
        {
            return tags;
        }

        public void setTags(Set<String> tags)
        {
            this.tags = tags;
        }
    }

    @Override
    public String toString()
    {
        return "User{" +
                "username='" + username + '\'' +
                ", address=" + address +
                ", createdDate=" + createdDate +
                ", email=" + email +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

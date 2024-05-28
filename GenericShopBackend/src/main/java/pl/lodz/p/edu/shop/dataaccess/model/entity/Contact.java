package pl.lodz.p.edu.shop.dataaccess.model.entity;

import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.lodz.p.edu.shop.dataaccess.model.superclass.AbstractEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class Contact extends AbstractEntity {

    private static final Logger log = LoggerFactory.getLogger(Contact.class);
    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(unique = true)
    private Address address;


    private Contact(String firstName, String lastName, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    protected Contact() {
    }

    protected Contact(ContactBuilder<?, ?> b) {
        super(b);
        Long id = super.getId();
        Long version = super.getVersion();

        this.firstName = b.firstName;
        this.lastName = b.lastName;
        this.address = b.address;
        Long id1 = super.getId();
        Long version1 = super.getVersion();
    }

    public static ContactBuilder<?, ?> builder() {
        return new ContactBuilderImpl();
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Contact)) return false;
        final Contact other = (Contact) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        final Object this$firstName = this.getFirstName();
        final Object other$firstName = other.getFirstName();
        if (this$firstName == null ? other$firstName != null : !this$firstName.equals(other$firstName)) return false;
        final Object this$lastName = this.getLastName();
        final Object other$lastName = other.getLastName();
        if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) return false;
        final Object this$address = this.getAddress();
        final Object other$address = other.getAddress();
        if (this$address == null ? other$address != null : !this$address.equals(other$address)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Contact;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $firstName = this.getFirstName();
        result = result * PRIME + ($firstName == null ? 43 : $firstName.hashCode());
        final Object $lastName = this.getLastName();
        result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
        final Object $address = this.getAddress();
        result = result * PRIME + ($address == null ? 43 : $address.hashCode());
        return result;
    }

    public String toString() {
        return "Contact(super=" + super.toString() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", address=" + this.getAddress() + ")";
    }

    public String getPostalCode() {
        return this.address.getPostalCode();
    }

    public String getCountry() {
        return this.address.getCountry();
    }

    public String getCity() {
        return this.address.getCity();
    }

    public String getStreet() {
        return this.address.getStreet();
    }

    public Integer getHouseNumber() {
        return this.address.getHouseNumber();
    }

    public void setPostalCode(String postalCode) {
        this.address.setPostalCode(postalCode);
    }

    public void setCountry(String country) {
        this.address.setCountry(country);
    }

    public void setCity(String city) {
        this.address.setCity(city);
    }

    public void setStreet(String street) {
        this.address.setStreet(street);
    }

    public void setHouseNumber(Integer houseNumber) {
        this.address.setHouseNumber(houseNumber);
    }

    public Long getId() {
        return this.address.getId();
    }

    public Long getVersion() {
        return this.address.getVersion();
    }

    public String getCreatedBy() {
        return this.address.getCreatedBy();
    }

    public String getModifiedBy() {
        return this.address.getModifiedBy();
    }

    public LocalDateTime getCreatedAt() {
        return this.address.getCreatedAt();
    }

    public LocalDateTime getModifiedAt() {
        return this.address.getModifiedAt();
    }

    public static abstract class ContactBuilder<C extends Contact, B extends ContactBuilder<C, B>> extends AbstractEntityBuilder<C, B> {
        private String firstName;
        private String lastName;
        private Address address;

        public B firstName(String firstName) {
            this.firstName = firstName;
            return self();
        }

        public B lastName(String lastName) {
            this.lastName = lastName;
            return self();
        }

        public B address(Address address) {
            this.address = address;
            return self();
        }

        protected abstract B self();

        public abstract C build();

        public String toString() {
            return "Contact.ContactBuilder(super=" + super.toString() + ", firstName=" + this.firstName + ", lastName=" + this.lastName + ", address=" + this.address + ")";
        }
    }

    private static final class ContactBuilderImpl extends ContactBuilder<Contact, ContactBuilderImpl> {
        private ContactBuilderImpl() {
        }

        protected ContactBuilderImpl self() {
            return this;
        }

        public Contact build() {
            Contact contact = new Contact(this);
            return contact;
        }
    }
}

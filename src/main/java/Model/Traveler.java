package Model;

import java.time.LocalDate;

public class Traveler {
    private Long id;
    private Long member_id;
    private String first_name;
    private String surname;
    private int age;
    private String gender;
    private LocalDate created;

    public Traveler(){}

    public Traveler(Long memid,String first_name, String surname, int age, String gender) {
        this.member_id = memid;
        this.first_name = first_name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
    }

    public Traveler(Long id,Long memid, String first_name, String surname, int age, String gender) {
        this.id = id;
        this.member_id = memid;
        this.first_name = first_name;
        this.surname = surname;
        this.age = age;
        this.gender = gender;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public Long getMember_id() {
        return member_id;
    }

    public void setMember_id(Long member_id) {
        this.member_id = member_id;
    }


    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "User{" +
                "id::" + id + "\n" +
                "first_name='" + first_name + "\n" +
                "surname='" + surname + "\n" +
                "age=" + age +"\n" +
                "gender=" + gender +"\n" +
                "created=" + created+"\n" +
                "}";
    }
}

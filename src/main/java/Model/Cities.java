package Model;

public class Cities {
    private Long id;
    private String name;
    private String country;

    public Cities(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public Cities(Long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Cities{" +
                "id::" + id +
                " name=" + name +
                " Country=" + country +
                "}\n";
    }
}

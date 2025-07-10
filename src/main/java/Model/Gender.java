package Model;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private String gen;
    Gender(String gen) {
        this.gen= gen;
    }

    public String getGender() {
        return gen;
    }

    public void setGender(String gen) {
        this.gen = gen;
    }
}

package A_interview_experiences.flipkart.flipkartminutes.model;

import lombok.Getter;

@Getter
public abstract class User {
    protected final String id;
    protected final String name;

    protected User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s{id=%s, name=%s}", getClass().getSimpleName(), id, name);
    }
}
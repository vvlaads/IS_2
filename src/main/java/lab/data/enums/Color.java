package lab.data.enums;

public enum Color {
    RED(1),
    BLACK(2),
    BROWN(3);
    private final Integer code;

    Color(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return this.code;
    }
}
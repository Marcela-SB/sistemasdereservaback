package br.com.deart.sistemadereservasdeart.enums;

public enum WeekDays {
    DOMINGO(1, "Domingo", 0),
    SEGUNDA(2, "Segunda", 1),
    TERCA(3, "Terça", 2),
    QUARTA(4, "Quarta", 3),
    QUINTA(5, "Quinta", 4),
    SEXTA(6, "Sexta", 5),
    SABADO(7, "Sábado", 6);

    private final int number;
    private final String name;
    private final int index;

    WeekDays(int number, String name, int index) {
        this.number = number;
        this.name = name;
        this.index = index;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static WeekDays fromIndex(int index) {
        for (WeekDays day : values()) {
            if (day.index == index) {
                return day;
            }
        }
        return null;
    }
}
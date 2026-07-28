package br.com.deart.sistemadereservasdeart.enums;

public enum ClassTime {
    M1, M2, M3, M4, M5, M6,
    T1, T2, T3, T4, T5, T6,
    N1, N2, N3, N4;

    public String getDescription() {
        return this.name(); // Retorna exatamente "M1", "T2", "N4", etc.
    }

    public static ClassTime fromIndex(int index) {
        ClassTime[] valores = values();
        if (index >= 0 && index < valores.length) {
            return valores[index];
        }
        return null;
    }
}

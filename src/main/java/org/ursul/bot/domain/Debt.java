package org.ursul.bot.domain;

public class Debt {

    private CumulatedSpend donator;
    private CumulatedSpend debtor;
    private Double sum;

    public Debt(CumulatedSpend donator, CumulatedSpend debtor, Double sum) {
        this.donator = donator;
        this.debtor = debtor;
        this.sum = sum;
    }

    public CumulatedSpend getDonator() {
        return donator;
    }

    public CumulatedSpend getDebtor() {
        return debtor;
    }

    public Double getSum() {
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Debt debt = (Debt) o;

        if (donator != null ? !donator.equals(debt.donator) : debt.donator != null) return false;
        if (debtor != null ? !debtor.equals(debt.debtor) : debt.debtor != null) return false;
        return sum != null ? sum.equals(debt.sum) : debt.sum == null;

    }

    @Override
    public int hashCode() {
        int result = donator != null ? donator.hashCode() : 0;
        result = 31 * result + (debtor != null ? debtor.hashCode() : 0);
        result = 31 * result + (sum != null ? sum.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Debt{");
        sb.append("donator=").append(donator);
        sb.append(", debtor=").append(debtor);
        sb.append(", sum=").append(sum);
        sb.append('}');
        return sb.toString();
    }
}
